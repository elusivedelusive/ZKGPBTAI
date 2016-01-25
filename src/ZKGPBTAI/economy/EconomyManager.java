package ZKGPBTAI.economy;

import ZKGPBTAI.Main;
import ZKGPBTAI.Manager;
import ZKGPBTAI.economy.tasks.ConstructionTask;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.military.Enemy;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.*;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class EconomyManager extends Manager {
    public static final int MAX_BUILD_DIST = 1000;
    public static final int BUILDING_DIST = 7;

    boolean metalmap = false;
    List<AIFloat3> availablemetalspots;
    float effectiveIncomeMetal = 0;
    float effectiveIncomeEnergy = 0;

    public float effectiveIncome = 0;
    float effectiveExpenditure = 0;

    float metal = 0;
    float energy = 0;

    //used to calculate average economy
    int entries = 0;
    int totalEco = 0;

    Deque<Worker> idlers;
    public ArrayList<Worker> workers, factories, commanders;
    public ArrayList<Unit> metalExtractors, solarPlants, radars, defences, aas, storages, caretakers;
    ArrayList<ConstructionTask> solarTasks, constructionTasks, defenceTasks, metExtractTasks, factoryTasks, radarTasks, storageTasks, caretakerTasks;

    //must be called before other managers
    public EconomyManager(OOAICallback cb) {
        //set variables in Manager
        this.callback = cb;
        this.economy = cb.getEconomy();
        this.game = cb.getGame();
        this.m = callback.getResourceByName("Metal");
        this.e = callback.getResourceByName("Energy");

        availablemetalspots = new ArrayList<>();
        metalExtractors = new ArrayList<>();
        solarPlants = new ArrayList<>();
        solarTasks = new ArrayList<>();
        metExtractTasks = new ArrayList<>();
        constructionTasks = new ArrayList<>();
        storageTasks = new ArrayList<>();
        factoryTasks = new ArrayList<>();
        radarTasks = new ArrayList<>();
        defenceTasks = new ArrayList<>();
        workers = new ArrayList<>();
        radars = new ArrayList<>();
        idlers = new ArrayDeque<>();
        factories = new ArrayList<>();
        commanders = new ArrayList<>();
        defences = new ArrayList<>();
        aas = new ArrayList<>();
        storages = new ArrayList<>();
        caretakerTasks = new ArrayList<>();
        caretakers = new ArrayList<>();

        setEcoManager(this);
    }

    @Override
    public String getModuleName() {
        return "EconomyManager";
    }

    //TODO fix problem with workers just standing still after a while
    @Override
    public int update(int frame) {

        try {
            this.frame = frame;

            if (frame % 5 == 0) {
                //update economy
                effectiveIncomeMetal = economy.getIncome(m);
                effectiveIncomeEnergy = economy.getIncome(e);
                metal = economy.getCurrent(m);
                energy = economy.getCurrent(e);
                float expendMetal = economy.getUsage(m);
                float expendEnergy = economy.getUsage(e);
                effectiveIncome = Math.min(effectiveIncomeMetal, effectiveIncomeEnergy);
                effectiveExpenditure = Math.min(expendMetal, expendEnergy);
                entries++;
                totalEco += effectiveIncome;
            }

            if (frame % 60 == 0) {
                try {
                    cleanWorkers();
                } catch (Exception e) {
                    write("ERROR cleanWorkers EM");
                }
/*                try {
                    cleanTasks();
                } catch (Exception e) {
                    write("ERROR cleanTasks EM", 0);
                }*/

/*                if (workers.size() < constructionTasks.size()) {
                    write("TASK OVERLOAD", 0);
                    for (ConstructionTask ct : constructionTasks) {
                        write("    " + ct.toString() + ct.assignedWorkers, 0);
                    }
                }*/
                for (Worker w : idlers) {
                    if ((w.getTask() == null || !constructionTasks.contains(w.getTask())) && workers.size() > constructionTasks.size()) {
                        try {
                            createWorkerTask(w);
                        } catch (Exception e) {
                            write("createWorkerTask " + e.getMessage() + " HEALTH " + w.getUnit().getHealth());
                        }
                        ConstructionTask ct = (ConstructionTask) w.getTask();
                        try {
                            w.getUnit().build(ct.buildType, ct.getPos(), ct.facing, (short) 0, frame + 5000);
                        } catch (Exception e) {
                            write("build command FAILED " + (w.getUnit().getHealth() > 0 ? "Handled" : "ERROR"));
                            idlers.addAll(w.getTask().stopWorkers(frame));
                            removeTaskFromAllLists(ct);
                            w.setTask(null, frame);
                        }
                    } /*else if (w.getUnit().getCurrentCommands().size() == 0 && w.getTask() != null && w.getUnit().getHealth() > 0) {
                        write("reminded worker", 0);
                        ConstructionTask ct = (ConstructionTask) w.getTask();
                        //write("reminded worker to do " + ct, 0);
                        w.getUnit().build(ct.buildType, ct.getPos(), ct.facing, (short) 0, frame + 5000);
                    }*/
                }
            }
        } catch (Exception e) {
            write(getModuleName() + " " + e.getMessage());
        }

        if (frame % 300 == 0)
            assignCaretakers();

        return 0;
    }

    @Override
    public int unitFinished(Unit unit) {
        checkIfWorker(unit);

        ConstructionTask finished = null;
        for (ConstructionTask ct : constructionTasks) {
            if (ct.target != null) {
                if (ct.target.getUnitId() == unit.getUnitId()) {
                    idlers.addAll(ct.stopWorkers(frame));
                    finished = ct;
                }
            }
        }

        removeTaskFromAllLists(finished);

        switch (unit.getDef().getHumanName()) {
            case "Metal Extractor":
                metalExtractors.add(unit);
                break;
            case "Lotus":
                defences.add(unit);
                break;
            case "Light Gauss Cannon":
                defences.add(unit);
                break;
            case "Solar Collector":
                solarPlants.add(unit);
                break;
            case "Radar Tower":
                radars.add(unit);
                break;
            case "Storage":
                storages.add(unit);
                break;
            case "Caretaker":
                caretakers.add(unit);
                //TODO this has caused an error before
                assignCaretakers();
                break;
        }
        return 0;
    }

    public void assignCaretakers() {
        for (Unit u : caretakers) {
            //if there is a factory find it and guard it. Guard makes caretaker help with construction
            if (factories.size() != 0) {
                u.guard(getNearestFac(u.getPos()).getUnit(), (short) 0, frame + 3000);
                u.setRepeat(true, (short) 0, frame + 3000);
            }
        }
    }

    public Worker getNearestFac(AIFloat3 pos) {
        Worker nearest = null;
        float dist = Float.MAX_VALUE;
        for (Worker f : factories) {
            float d = Utility.distance(pos, f.getPos());
            if (d < dist) {
                dist = d;
                nearest = f;
            }
        }
        return nearest;
    }

    public int getAvgEco() {
        return totalEco / entries;
    }

    public boolean isStationary(Unit u) {
        return u.getMaxSpeed() == 0;
    }

    @Override
    public int unitDestroyed(Unit unit, Unit attacker) {
        //if unit is a worker and has a task then remove task
        if (isStationary(unit)) {
            for (ConstructionTask ct : constructionTasks) {
                if (ct.target != null) {
                    if (ct.target.getUnitId() == unit.getUnitId()) {
                        idlers.addAll(ct.stopWorkers(frame));
                        removeTaskFromAllLists(ct);
                        break;
                    }
                }
            }
        }

        //buildings
        if (metalExtractors.remove(unit)) availablemetalspots.add(unit.getPos());
        solarPlants.remove(unit);
        storages.remove(unit);
        radars.remove(unit);
        defences.remove(unit);
        aas.remove(unit);

        //workers
        factories.remove(unit);
        workers.remove(unit);
        idlers.remove(unit);
        commanders.remove(unit);
        return 0;
    }

    @Override
    public int unitIdle(Unit u) {
        for (Worker worker : workers) {
            if (worker.id == u.getUnitId()) {
                idlers.add(worker);
            }
        }

        for (Worker f : factories) {
            if (f.id == u.getUnitId()) {
                assignFactoryTask(f);
            }
        }
        return 0;
    }

    @Override
    public int unitCreated(Unit unit, Unit builder) {
        if (builder != null && unit.isBeingBuilt()) {
            //if constructing building
            if (unit.getMaxSpeed() == 0) {
                //find the unit constructing the building
                for (Worker w : workers) {
                    if (w.id == builder.getUnitId()) {
                        //if that worker has a task that is a constructionTask
                        if (w.getTask() != null && w.getTask() instanceof ConstructionTask) {
                            //set the target of the constructiontask to be the input unit
                            ConstructionTask ct = (ConstructionTask) w.getTask();
                            //target is used to continue building the same building if interrupted
                            ct.target = unit;
                        } else {
                            for (ConstructionTask ct : constructionTasks) {
                                //find the constructiontask closest to the input unit and set it to be the target of the ct
                                float dist = Utility.distance(ct.getPos(), unit.getPos());
                                if (dist < 25 && ct.buildType.getName().equals(unit.getDef().getName())) {
                                    ct.target = unit;
                                }
                            }
                        }
                    }
                }
            }
            //Needed to remove commanders build task for the first instant factory
        } else if (builder != null && !unit.isBeingBuilt()) {
            for (Worker w : workers) {
                if (w.id == builder.getUnitId()) {
                    constructionTasks.remove(w.getTask());
                    factoryTasks.remove(w.getTask());
                    w.clearTask(frame);
                }
            }
        }
        return 0;
    }

    void removeTaskFromAllLists(WorkerTask wt) {
        constructionTasks.remove(wt);
        metExtractTasks.remove(wt);
        storageTasks.remove(wt);
        solarTasks.remove(wt);
        defenceTasks.remove(wt);
        radarTasks.remove(wt);
        factoryTasks.remove(wt);
        caretakerTasks.remove(wt);
    }

    //This is needed because update is called before unitDestroyed
    void cleanWorkers() {
        List<Worker> invalidWorkers = new ArrayList<>();
        for (Worker w : workers) {
            if (w.getUnit().getHealth() <= 0)
                invalidWorkers.add(w);
        }
        for (Worker w : idlers) {
            if (w.getUnit().getHealth() <= 0)
                invalidWorkers.add(w);
        }

        for (Worker w : invalidWorkers) {
            if (w.getTask() != null) {
                w.getTask().removeWorker(w);
            }
        }

        workers.removeAll(invalidWorkers);
        idlers.removeAll(invalidWorkers);


        for (Worker w : workers) {

            //if worker has lost his orders give him the build command again
            if (w.getUnit().getCurrentCommands().size() == 0) {
                if (w.getTask() != null) {
                    if (constructionTasks.contains(w.getTask())) {
                        ConstructionTask ct = (ConstructionTask) w.getTask();
                        try {
                            w.getUnit().build(ct.buildType, ct.getPos(), ct.facing, (short) 0, frame + 5000);
                            idlers.remove(w);
                        } catch (Exception e) {
                            write("Cant build there " + e.getMessage());
                            removeTaskFromAllLists(ct);
                        }
                    }
                    //handle lazy workers
                } else {
                    idlers.add(w);
                }
            }
            if (w.unstick(frame)) {
                idlers.add(w);
                removeTaskFromAllLists(w.getTask());
                w.clearTask(frame);
            }
            // detect and unstick workers that get stuck on pathing obstacles.
        }
    }

    void checkIfWorker(Unit u) {
        UnitDef def = u.getDef();
        if (def.isBuilder()) {
            if (def.getName().contains("factory") || def.getName().contains("hub")) {
                Worker fac = new Worker(u);
                factories.add(fac);
                assignFactoryTask(fac);
                //assign task
            } else if (u.getMaxSpeed() > 0) {
                Worker w = new Worker(u);
                workers.add(w);
                idlers.add(w);
                if (def.getBuildSpeed() > 8) {
                    commanders.add(w);
                }
            }
        }
    }

    void assignFactoryTask(Worker fac) {
        UnitDef unit;
        if (fac.getUnit().getDef().getName().equals("factorycloak")) {
            unit = callback.getUnitDefByName(getUnitFromCloakFac());
            fac.getUnit().build(unit, fac.getPos(), (short) 0, (short) 0, frame + 3000);
        } else if (fac.getUnit().getDef().getName().equals("factoryhover")) {
            unit = callback.getUnitDefByName(getUnitFromHoverkFac());
            fac.getUnit().build(unit, fac.getPos(), (short) 0, (short) 0, frame + 3000);
        }
    }

    private Boolean needWorkers() {
        if (((float) workers.size() - 1 < Math.floor(effectiveIncome / 5))) {
            return true;
        }
        return false;
    }

    private boolean needRadar(AIFloat3 pos) {
        float closestRadarDistance = Float.MAX_VALUE;
        for (Unit r : radars) {
            float distance = Utility.distance(r.getPos(), pos);
            if (distance < closestRadarDistance) {
                closestRadarDistance = distance;
            }
        }

        for (ConstructionTask r : radarTasks) {
            float distance = Utility.distance(r.getPos(), pos);
            if (distance < closestRadarDistance) {
                closestRadarDistance = distance;
            }
        }
        if (closestRadarDistance > 1000) {
            return true;
        }
        return false;
    }

    boolean needDefender(AIFloat3 pos) {
        for (Unit turret : defences) {
            if (Utility.distance(turret.getPos(), pos) < 200)
                return false;
        }

        return true;
    }

    boolean needCaretaker() {
        for (Worker f : factories) {
            //find amount of caretakers close to a factory
            int cCount = 0;
            for (Unit c : caretakers) {
                if (Utility.distance(f.getPos(), c.getPos()) < 350) cCount++;
                for (WorkerTask wt : caretakerTasks) {
                    if (Utility.distance(f.getPos(), wt.getPos()) < 350) cCount--;
                }

            }
            if ((cCount < Math.floor(effectiveIncome / 15) && caretakers.size() < factories.size())) return true;
        }
        return false;
    }

    void createWorkerTask(Worker worker) {
        AIFloat3 position = worker.getPos();


        //do we need factory
        if ((factories.size() == 0 && factoryTasks.size() == 0)
                || (effectiveIncome > (20 + ((factories.size() - 1) * 10)) && factoryTasks.size() == 0)) {
            createFactoryTask(worker);
        }

        //do we have enough energy
        else if ((effectiveIncome < 15 && metalExtractors.size() > solarPlants.size() + solarTasks.size())
                || (effectiveIncome > 15 && energy < 400 && solarTasks.size() < workers.size())
                || (effectiveIncome > 20 && (metalExtractors.size() * ((metalExtractors.size() / 10) + 1)) > solarPlants.size() + solarTasks.size() && solarTasks.size() < workers.size())) {
            //write("creating energy task", 0);
            createEnergyTask(worker);
        } else if (needRadar(position) && effectiveIncome > 10) {
            //write("creating radar task", 0);
            createRadarTask(worker);
        } else if (needDefender(position) && effectiveIncome > 10 && metal > 50) {
            if (effectiveIncome > 30 && metal > 100) {
                //write("creating gauss task", 0);
                createGaussTask(worker);
            } else if (effectiveIncome > 10) {
                //write("creating lotus task", 0);
                createLotusTask(worker);
            }
/*        } else if (needCaretaker() && effectiveIncome > 15 && metal > 50) {
            createCaretakerTask(worker);*/
        } else if (effectiveIncome > 15 && metal > (economy.getStorage(m) - 100) || energy > (economy.getStorage(e) - 100)) {
            // write("creating storage task", 0);
            createStorageTask(worker);
        } else {
            //write("creating metal task", 0);
            createMetalExtractorTask(worker);
        }

        if (worker.getTask() == null) {
            write("No suitable task");
        }
    }

    //TODO maybe fix redundancy between this and needCaretaker()
    void createCaretakerTask(Worker worker) {

/*        UnitDef caretaker = callback.getUnitDefByName("armnanotc");
        for (Worker f : factories) {
            //find amount of caretakers close to a factory
            int cCount = 0;
            for (Unit c : caretakers) {
                if (Utility.distance(f.getPos(), c.getPos()) < 350) cCount++;
                for (WorkerTask wt : caretakerTasks) {
                    if (Utility.distance(f.getPos(), wt.getPos()) < 350) cCount--;
                }

            }
            if ((cCount < Math.floor(effectiveIncome / 15) && caretakers.size() < factories.size())) {
                AIFloat3 position = f.getPos();
                position = callback.getMap().findClosestBuildSite(caretaker, position, MAX_BUILD_DIST, BUILDING_DIST, 0);
                ConstructionTask ct = new ConstructionTask(caretaker, position, 0);
                if (!caretakerTasks.contains(ct)) {
                    constructionTasks.add(ct);
                    caretakerTasks.add(ct);
                }
                worker.setTask(ct, frame);
                return;
            }

        }
    }*/
        UnitDef caretaker = callback.getUnitDefByName("armnanotc");
        for (Worker f : factories) {
            //find amount of caretakers close to a factory
            for (Unit c : caretakers) {
                //if there is  a caretake close to the factory
                if (Utility.distance(f.getPos(), c.getPos()) < 350) {
                    //if there is a caretaketask close to the factory
                    for (WorkerTask wt : caretakerTasks) {
                        if (Utility.distance(f.getPos(), wt.getPos()) < 350) continue;
                        else if (caretakers.size() < factories.size()) {
                            AIFloat3 position = f.getPos();
                            position = callback.getMap().findClosestBuildSite(caretaker, position, MAX_BUILD_DIST, BUILDING_DIST, 0);
                            ConstructionTask ct = new ConstructionTask(caretaker, position, 0);
                            if (!caretakerTasks.contains(ct)) {
                                constructionTasks.add(ct);
                                caretakerTasks.add(ct);
                            }
                            worker.setTask(ct, frame);
                            return;
                        }
                    }
                }
            }
        }
    }

    void createLotusTask(Worker worker) {
        UnitDef lotus = callback.getUnitDefByName("corllt");
        AIFloat3 position = worker.getUnit().getPos();

        position = callback.getMap().findClosestBuildSite(lotus, position, MAX_BUILD_DIST, BUILDING_DIST, 0);
        ConstructionTask ct = new ConstructionTask(lotus, position, 0);
        if (!defenceTasks.contains(ct)) {
            constructionTasks.add(ct);
            defenceTasks.add(ct);
        }
        worker.setTask(ct, frame);
    }

    void createGaussTask(Worker worker) {
        UnitDef gauss = callback.getUnitDefByName("armpb");
        AIFloat3 position = worker.getUnit().getPos();

        position = callback.getMap().findClosestBuildSite(gauss, position, MAX_BUILD_DIST, BUILDING_DIST, 0);
        ConstructionTask ct = new ConstructionTask(gauss, position, 0);
        if (!defenceTasks.contains(ct)) {
            constructionTasks.add(ct);
            defenceTasks.add(ct);
        }
        worker.setTask(ct, frame);
    }

    void createStorageTask(Worker worker) {
        UnitDef storage = callback.getUnitDefByName("armmstor");
        AIFloat3 position = worker.getUnit().getPos();

        position = callback.getMap().findClosestBuildSite(storage, position, MAX_BUILD_DIST, BUILDING_DIST, Integer.MAX_VALUE);
        ConstructionTask ct = new ConstructionTask(storage, position, 0);
        if (!storageTasks.contains(ct)) {
            constructionTasks.add(ct);
            storageTasks.add(ct);
        }
        worker.setTask(ct, frame);
    }

    void createRadarTask(Worker worker) {
        UnitDef radar = callback.getUnitDefByName("corrad");
        AIFloat3 position = worker.getUnit().getPos();

        position = callback.getMap().findClosestBuildSite(radar, position, MAX_BUILD_DIST, BUILDING_DIST, Integer.MAX_VALUE);
        ConstructionTask ct = new ConstructionTask(radar, position, 0);
        if (!radarTasks.contains(ct)) {
            constructionTasks.add(ct);
            radarTasks.add(ct);
        }
        worker.setTask(ct, frame);
    }

    void createMetalExtractorTask(Worker worker) {
        checkForMetal();
        UnitDef metalExt = callback.getUnitDefByName("cormex");

        ConstructionTask ct = null;
        boolean found = false;
        while (!found) {
            AIFloat3 metalPos = closestMetalSpot(worker.getPos());
            if (callback.getMap().isPossibleToBuildAt(metalExt, metalPos, Integer.MAX_VALUE)) {
                ct = new ConstructionTask(metalExt, metalPos, 0);
                if (!constructionTasks.contains(ct))
                    found = true;
            } else {
                AIFloat3 closePos = callback.getMap().findClosestBuildSite(metalExt, metalPos, MAX_BUILD_DIST, 5, Integer.MAX_VALUE);
                ct = new ConstructionTask(metalExt, closePos, 0);
                if (!constructionTasks.contains(ct))
                    found = true;
            }

            if (!found) {
                availablemetalspots.remove(metalPos);
            } else {
                constructionTasks.add(ct);
                metExtractTasks.add(ct);
                worker.setTask(ct, frame);
            }
        }
    }

    void createEnergyTask(Worker worker) {
        UnitDef solar = callback.getUnitDefByName("armsolar");
        ConstructionTask ct = new ConstructionTask(solar, callback.getMap().findClosestBuildSite(solar, worker.getPos(), MAX_BUILD_DIST, (short) BUILDING_DIST, Integer.MAX_VALUE), 0);
        solarTasks.add(ct);
        constructionTasks.add(ct);
        worker.setTask(ct, frame);
    }

    void createFactoryTask(Worker worker) {
        UnitDef factory;
        switch (factories.size()) {
            case 0:
                factory = callback.getUnitDefByName("factorycloak");
                break;
            case 1:
                factory = callback.getUnitDefByName("factoryhover");
                break;
            case 2:
                factory = callback.getUnitDefByName("factorycloak");
                break;
            case 3:
                factory = callback.getUnitDefByName("factorycloak");
                break;
            default:
                factory = callback.getUnitDefByName("factorycloak");
                break;
        }

        AIFloat3 position = worker.getUnit().getPos();

        ConstructionTask ct = new ConstructionTask(factory, callback.getMap().findClosestBuildSite(factory, position, 600f, 5, 0), 0);
        if (!factoryTasks.contains(ct)) {
            constructionTasks.add(ct);
            factoryTasks.add(ct);
        }
        worker.setTask(ct, frame);
    }

    public AIFloat3 closestMetalSpot(AIFloat3 unitposition) {
        AIFloat3 closestspot = null;
        for (AIFloat3 metalspot : availablemetalspots) {
            if (closestspot == null) {
                closestspot = metalspot;
            } else if (Utility.distance(metalspot, unitposition) < Utility.distance(closestspot, unitposition) && metalspot.hashCode() != unitposition.hashCode() && callback.getMap().isPossibleToBuildAt(callback.getUnitDefByName("cormex"), metalspot, 0)) {
                closestspot = metalspot;
            }
        }
        availablemetalspots.remove(closestspot);
        return closestspot;
    }

    public void checkForMetal() {

        availablemetalspots = callback.getMap().getResourceMapSpotsPositions(m);
        if (availablemetalspots.isEmpty()) {
            metalmap = false;
        } else {
            metalmap = true;
            for (Unit u : metalExtractors) {
                for (int m = 0; m < availablemetalspots.size(); m++) {
                    if (Utility.distance(u.getPos(), availablemetalspots.get(m)) < 5)
                        availablemetalspots.remove(m);
                }
            }

            //check for enemy metalExtractors
            for (Enemy e : militaryManager.getVisibleEnemies().values()) {
                if (e.def.getName().equals("cormex")) {
                    for (int m = 0; m < availablemetalspots.size(); m++) {
                        if (Utility.distance(e.getPos(), availablemetalspots.get(m)) < 5) {
                            availablemetalspots.remove(m);
                        }
                    }
                }
            }
        }

        if (availablemetalspots.isEmpty()) {
            write("Out of metal spots");
        }
    }

    private String getUnitFromCloakFac() {
        if (needWorkers()) return "armrectr";
        double rand = Math.random();
        if (effectiveIncome < 70) {
            if (rand > 0.5)
                return "armrock";
            else if (rand > 0.3)
                return "armzeus";
            else if (rand > 0.1)
                return "armwar";
            else
                return "armzeus";
        } else {
            if (rand > 0.6)
                return "armrock";
            else if (rand > 0.4)
                return "armzeus";
            else
                return "armwar";
        }
    }

    private String getUnitFromHoverkFac() {
        //TODO corches never build buildings for some reason
        //if (needWorkers()) return "corch";
        double rand = Math.random();
        if (effectiveIncome < 30) {
            if (rand > 0.1)
                return "nsaclash";
            else
                return "hoverriot";
        } else {
            if (rand > 0.35)
                return "nsaclash";
            else if (rand > 0.1)
                return "hoverriot";
            else
                return "armanni";
        }
    }
}
