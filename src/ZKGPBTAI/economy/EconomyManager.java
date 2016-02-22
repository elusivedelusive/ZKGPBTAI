package ZKGPBTAI.economy;

import ZKGPBTAI.Main;
import ZKGPBTAI.Manager;
import ZKGPBTAI.bt.actions.Defensive;
import ZKGPBTAI.bt.actions.Offensive;
import ZKGPBTAI.bt.actions.worker.*;
import ZKGPBTAI.bt.conditions.HasArmy;
import ZKGPBTAI.bt.conditions.HasEco;
import ZKGPBTAI.bt.conditions.economy.HighEnergy;
import ZKGPBTAI.bt.conditions.economy.HighMetal;
import ZKGPBTAI.bt.conditions.economy.LowEnergy;
import ZKGPBTAI.bt.conditions.economy.LowMetal;
import ZKGPBTAI.economy.tasks.AssistTask;
import ZKGPBTAI.economy.tasks.ConstructionTask;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.military.Enemy;
import ZKGPBTAI.utils.Utility;
import bt.BehaviourTree;
import bt.Task;
import bt.utils.TreeInterpreter;
import bt.utils.graphics.LiveBT;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.*;


import java.lang.reflect.Array;
import java.util.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class EconomyManager extends Manager {
    public static final int MAX_BUILD_DIST = 5000;
    public static final int BUILDING_DIST = 7;
    public static float map_width;
    public static float map_height;
    boolean metalmap = false;
    public List<AIFloat3> availablemetalspots;
    public float effectiveIncomeMetal = 0;
    public float effectiveIncomeEnergy = 0;

    public float effectiveIncome = 0;
    public float effectiveExpenditure = 0;

    public float metal = 0;
    public float energy = 0;

    public float metalStorage = 0;
    public float energyStorage = 0;

    public float expendMetal = 0;
    public float expendEnergy = 0;

    //used to calculate average economy
    int entries = 0;
    int totalEco = 0;

    Deque<Worker> idlers;
    public ArrayList<Worker> workers, factories, commanders;
    public ArrayList<Unit> metalExtractors, solarPlants, radars, defences, aas, storages, caretakers;
    ArrayList<ConstructionTask> solarTasks, constructionTasks, defenceTasks, metExtractTasks, factoryTasks, radarTasks, storageTasks, caretakerTasks;
    ArrayList<AssistTask> assistTasks;
    ArrayList<Worker> idlersGivenWork;

    //BT
    private final HashMap<BehaviourTree<EconomyManager>, Worker> trees = new HashMap<>();
    ScheduledExecutorService executorService;
    Runnable btTask;
    Optional<BehaviourTree<EconomyManager>> opt;
    String inputTree = "";

    //must be called before other managers
    public EconomyManager(OOAICallback cb, boolean runningBT, String inputTree) {
        //set variables in Manager
        this.callback = cb;
        this.economy = cb.getEconomy();
        this.game = cb.getGame();
        this.m = callback.getResourceByName("Metal");
        this.e = callback.getResourceByName("Energy");
        this.runningBt = runningBT;
        this.inputTree = inputTree;

        map_height = callback.getMap().getHeight() * 8f;
        map_width = callback.getMap().getWidth() * 8f;
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
        idlersGivenWork = new ArrayList<>();
        assistTasks = new ArrayList<>();

        setEcoManager(this);

        if (runningBT) {

            executorService = Executors.newScheduledThreadPool(1);

            @SuppressWarnings("unchecked")
            Class<? extends Task>[] classes = new Class[]{BuildFactory.class, BuildGauss.class, BuildLotus.class, BuildMex.class, BuildRadar.class, BuildSolar.class,
                    BuildStorage.class, HighEnergy.class, LowEnergy.class, HighMetal.class, HighEnergy.class, LowMetal.class,};
            write("inputtree = " + inputTree);
            opt = new TreeInterpreter<>(this).create(classes, inputTree);
        }
    }

    @Override
    public String getModuleName() {
        return "EconomyManager";
    }

    //TODO fix problem with workers just standing still after a while
    @Override
    public int update(int frame) {
        this.frame = frame;

        if (frame % 5 == 0) {
            //update economy
            energyStorage = economy.getStorage(e);
            metalStorage = economy.getStorage(m);

            effectiveIncomeMetal = economy.getIncome(m);
            effectiveIncomeEnergy = economy.getIncome(e);
            metal = economy.getCurrent(m);
            energy = economy.getCurrent(e);
            expendMetal = economy.getUsage(m);
            expendEnergy = economy.getUsage(e);
            effectiveIncome = Math.min(effectiveIncomeMetal, effectiveIncomeEnergy);
            effectiveExpenditure = Math.min(expendMetal, expendEnergy);

            //stats
            entries++;
            totalEco += effectiveIncome;
        }

        if (frame % 60 == 0) {

            try {
                if (runningBt)
                    executorService.submit(btTask);
            } catch (Exception e) {
                write("bt problem");
            }

            write("===================================================================");

            write("Workers: " + workers.size() + " Idlers: " + idlers.size() + " Tasks: " + constructionTasks.size());
            for (ConstructionTask task : constructionTasks) {
                write("Tasks " + task.buildType.getHumanName() + " pos " + task.getPos());
            }

            try {
                cleanWorkers();
            } catch (Exception e) {
                write("ERROR cleanWorkers EM");
            }


/*            //check is assistask is done
            for (AssistTask at : assistTasks) {
                if (at.isDone(frame))
                    ((WorkerTask) at).stopWorkers(frame);
            }*/

            try {
                cleanTasks();
            } catch (Exception e) {
                write("cleanTasks has crashed");
            }


            for (Worker w : workers) {
                if (w.getTask() != null) {
                    try {
                        ConstructionTask ct = (ConstructionTask) w.getTask();
                        w.getUnit().build(ct.buildType, ct.getPos(), ct.facing, (short) 0, frame + 5000);
                    } catch (Exception e) {
                        write("EcoUpdate exception " + e.getMessage());
                        w.getTask().stopWorkers(frame);
                        idlers.add(w);
                    }
                }
            }

            //==============DEBUGGING=================
            for (Worker w : workers) {
                String target = "";
                String task = "";
                String order = "";
                if (w.getTask() != null) {
                    task = ((ConstructionTask) w.getTask()).buildType.getHumanName();
                    target = (((ConstructionTask) w.getTask()).target != null) ? ((ConstructionTask) w.getTask()).target.getDef().getHumanName() : "";
                }
                if (w.getUnit().getCurrentCommands().size() > 0)
                    order = w.getUnit().getCurrentCommands().get(0).toString();

                write(w.id + " " + w.getUnit().getDef().getHumanName() + " - " + task + " - " + target + " - " + order);
            }
            //==============DEBUGGING=================

            if (!runningBt) {
                for (Worker w : idlers) {
                    if ((w.getTask() == null || w.getUnit().getCurrentCommands().size() == 0 || !constructionTasks.contains(w.getTask())) && workers.size() > constructionTasks.size()) {
                        try {
                            createWorkerTask(w);
                        } catch (Exception e) {
                            write("createWorkerTask " + e.getMessage() + " HEALTH " + w.getUnit().getHealth());
                        }
                        ConstructionTask ct = (ConstructionTask) w.getTask();
                        try {
                            w.getUnit().build(ct.buildType, ct.getPos(), ct.facing, (short) 0, frame + 5000);
                            //==============DEBUGGING=================
                            //write(w.id + " " + w.getUnit().getDef().getHumanName() + " - new " + ct.buildType.getHumanName());
                            //==============DEBUGGING=================
                            idlersGivenWork.add(w);
                        } catch (Exception e) {
                            write("build command FAILED " + (w.getUnit().getHealth() > 0 ? "Handled" : "ERROR"));
                            ct.stopWorkers(frame);
                            removeTaskFromAllLists(ct);
                            w.setTask(null, frame);
                        }
                    }
                }
                idlers.removeAll(idlersGivenWork);
                idlersGivenWork.clear();
                write("===================================================================");
            }
        }
        return 0;
    }

    @Override
    public int unitFinished(Unit unit) {
        ConstructionTask finished = null;
        for (ConstructionTask ct : constructionTasks) {
            if (ct.target != null) {
                if (ct.target.getUnitId() == unit.getUnitId()) {
                    //================= BT ================
                    ct.setResult(true);
                    //================= BT ================
                    ct.stopWorkers(frame);
                    finished = ct;
                    write("Task finished: " + ct.target.getDef().getHumanName());
                }
            }
        }

        if (finished == null) {
            for (ConstructionTask ct : constructionTasks) {
                if (ct.buildType == unit.getDef() && ct.position == unit.getPos()) {
                    //================= BT ================
                    ct.setResult(true);
                    //================= BT ================
                    ct.stopWorkers(frame);
                    finished = ct;
                    write("Task finished2: " + ct.target.getDef().getHumanName());
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
                        ct.stopWorkers(frame);
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


        for (Worker w : workers) {
            if (w.id == unit.getUnitId()) {
                write(unit.getUnitId() + " " + unit.getDef().getHumanName() + " IS DEAD");
                if (w.getTask() != null) {
                    removeTaskFromAllLists(w.getTask());
                }

                //================= BT ================
                if (runningBt) {
                    if (trees.containsValue(w)) {
                        BehaviourTree<EconomyManager> btToRemove = null;
                        for (HashMap.Entry<BehaviourTree<EconomyManager>, Worker> entry : trees.entrySet()) {
                            if (entry.getValue() == w) {
                                btToRemove = entry.getKey();
                                break;
                            }
                        }
                        if (btToRemove != null)
                            trees.remove(btToRemove);
                    }
                }
                //================= BT ================
            }
        }
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
                if (!idlers.contains(worker))
                    idlers.add(worker);
            }
        }
        return 0;
    }

    @Override
    public int unitCreated(Unit unit, Unit builder) {
        checkIfWorker(unit);
        if (builder == null)
            return 0;

        if (unit.isBeingBuilt()) {
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
                            write("target found");
                            ct.target = unit;
                        } else {
                            for (ConstructionTask ct : constructionTasks) {
                                write("using Dist to find target");
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
        } else if (!unit.isBeingBuilt()) {
            for (Worker w : workers) {
                if (w.id == builder.getUnitId()) {
                    constructionTasks.remove(w.getTask());
                    factoryTasks.remove(w.getTask());
                    w.clearTask(frame);

                    if (runningBt)
                        createBTForWorker(w);
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

    void cleanTasks() {
        ArrayList<ConstructionTask> uselessTasks = new ArrayList<>();
        //if the task has no assigned workers

        for (ConstructionTask ct : constructionTasks) {
            write("cleanTasks1");
            if (ct.assignedWorkers.size() == 0) {
                uselessTasks.add(ct);
                write("Task was removed because it had no workers");
            }

            //if no worker has that task
/*            write("cleanTasks2");
            boolean workerHasTask = false;
            for (Worker w : workers) {
                if (w.getTask() != null) {
                    write(((ConstructionTask) w.getTask()).buildType.getHumanName());
                    if (w.getTask().equals(ct))
                        workerHasTask = true;
                }
            }
            if (!workerHasTask)
                uselessTasks.add(ct);*/

            write("cleanTasks3");
            //if it is not possible to build at the location
            if (ct.target == null && !callback.getMap().isPossibleToBuildAt(ct.buildType, ct.getPos(), 0)) {
                write("is not possible to build at");
                uselessTasks.add(ct);
            }
        }
        for (ConstructionTask ct : uselessTasks) {
            ct.stopWorkers(frame);
            removeTaskFromAllLists(ct);
        }
    }

    //This is needed because update is called before unitDestroyed
    void cleanWorkers() {
        List<Worker> invalidWorkers = new ArrayList<>();
        for (Worker w : workers) {
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
            if (w.getTask() != null)
                idlers.remove(w);
        }

        //do not remove
/*        for (Worker w : workers) {
            //if worker has lost his orders give him the build command again
            if (w.getUnit().getCurrentCommands().size() == 0) {
                if (w.getTask() != null) {
                    if (constructionTasks.contains(w.getTask())) {
                        ConstructionTask ct = (ConstructionTask) w.getTask();
                        try {
                            if (ct.target == null && frame - w.lastTaskFrame > 500) {
                                ct.stopWorkers(frame);
                                removeTaskFromAllLists(ct);
                            } else {
                                w.getUnit().build(ct.buildType, ct.getPos(), ct.facing, (short) 0, frame + 5000);
                                idlers.remove(w);
                            }
                        } catch (Exception e) {
                            write("Cant build there " + e.getMessage());
                            removeTaskFromAllLists(ct);
                            w.clearTask(frame);
                        }
                    }
                    //handle lazy workers
                } else {
                    w.clearTask(frame);
                }
            }

            // detect and unstick workers that get stuck on pathing obstacles.
*//*            if (w.unstick(frame))
                write("unsticked");*//*
        }*/
    }

    void checkIfWorker(Unit u) {
        UnitDef def = u.getDef();
        if (def.isBuilder()) {
            if (def.getName().contains("factory") || def.getName().contains("hub")) {
                Worker fac = new Worker(u);
                factories.add(fac);
                //assignFactoryTask(fac);
                //assign task
            } else if (u.getMaxSpeed() > 0) {
                Worker w = new Worker(u);
                write(w.id + " " + w.getUnit().getDef().getHumanName() + " CREATED");
                workers.add(w);
                if (def.getBuildSpeed() > 8) {
                    commanders.add(w);
                    createFactoryTask(w);
                } else {
                    idlers.add(w);
                    if (runningBt)
                        createBTForWorker(w);
                }
            }
        }
    }

    private void createBTForWorker(Worker w) {
        final BehaviourTree<EconomyManager> bt = opt.get();

        btTask = () -> {
            bt.step();
            LiveBT.draw();
        };
        LiveBT.startTransmission(bt);

        trees.put(bt, w);
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

    void createCaretakerTask(Worker worker) {

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

    public AssistTask createAssistTask(Worker w, int frame, Unit target) {
        Unit fac = getNearestFac(w.getPos()).getUnit();
        AssistTask at = new AssistTask(w, frame, target);
        assistTasks.add(at);
        return at;
    }

    public ConstructionTask getBuildSite(Worker w, UnitDef def, ArrayList<ConstructionTask> taskList, boolean isFactory) {
        boolean taskCreated = false;
        ConstructionTask ct = null;
        AIFloat3 position = w.getPos();
        if (w.getTask() == null) {
            while (taskCreated != true) {
                position = w.getRadialPoint(position, 200f);

                position = callback.getMap().findClosestBuildSite(def, position, MAX_BUILD_DIST, BUILDING_DIST, 0);
                //then check if the closest build site is valid

                if (isFactory && !isOffsetEdgeOfMap(position))
                    continue;
                if (doesNotCoverMetalSPot(position)) {
                    ct = new ConstructionTask(def, position, 0);

                    //if there are no other construction tasks they will not overlap so no need to iterate through the list
                    if (constructionTasks.size() == 0) {
                        taskList.add(ct);
                        w.setTask(ct, frame);
                        ct.addWorker(w);
                        return ct;
                    }

                    for (ConstructionTask c : constructionTasks) {
                        if (Utility.distance(position, c.getPos()) > 7) {
                            if (!taskList.contains(ct)) {
                                taskList.add(ct);
                                w.setTask(ct, frame);
                                ct.addWorker(w);
                                return ct;
                            }
                        }
                    }
                }
            }
        }
        return ct;
    }

    //Checks if pos is too close to edge of map
    //used to ensure units in factories can move out
    public boolean isOffsetEdgeOfMap(AIFloat3 pos) {
        float dist = 75f;
        if ((pos.x - dist) < 0f || (pos.x + dist) > map_width) {
            write("posX: " + pos.x + " " + (pos.x - dist) + "  width " + map_width);
            return false;
        }

        if ((pos.z - dist) < 0f || (pos.z + dist) > map_height) {
            write("posZ: " + pos.z + " " + (pos.z - dist) + " height " + map_height);
            return false;
        }
        return true;
    }

    public boolean doesNotCoverMetalSPot(AIFloat3 pos) {
        checkForMetal();
        for (AIFloat3 metalspot : availablemetalspots) {
            if (Utility.distance(metalspot, pos) < 100f) {
                write("DONT BUILD ON ZE METAL");
                return false;
            }
        }
        return true;
    }

    public ConstructionTask createLotusTask(Worker worker) {
        UnitDef lotus = callback.getUnitDefByName("corllt");
        ConstructionTask ct = getBuildSite(worker, lotus, defenceTasks, false);
        constructionTasks.add(ct);
        return ct;
    }

    public ConstructionTask createGaussTask(Worker worker) {
        UnitDef gauss = callback.getUnitDefByName("armpb");
        ConstructionTask ct = getBuildSite(worker, gauss, defenceTasks, false);
        constructionTasks.add(ct);
        return ct;
    }

    public ConstructionTask createStorageTask(Worker worker) {
        UnitDef storage = callback.getUnitDefByName("armmstor");
        ConstructionTask ct = getBuildSite(worker, storage, storageTasks, false);
        constructionTasks.add(ct);
        return ct;
    }

    public ConstructionTask createRadarTask(Worker worker) {
        UnitDef radar = callback.getUnitDefByName("corrad");
        ConstructionTask ct = getBuildSite(worker, radar, radarTasks, false);
        constructionTasks.add(ct);
        return ct;
    }

    public ConstructionTask createEnergyTask(Worker worker) {
        UnitDef solar = callback.getUnitDefByName("armsolar");
        ConstructionTask ct = getBuildSite(worker, solar, solarTasks, false);
        constructionTasks.add(ct);
        return ct;
    }

    public ConstructionTask createFactoryTask(Worker worker) {
        UnitDef factory = recruitmentManager.chooseNewFactory();
        ConstructionTask ct = getBuildSite(worker, factory, factoryTasks, true);
        constructionTasks.add(ct);
        return ct;
    }

    public ConstructionTask createMetalExtractorTask(Worker worker) {
        checkForMetal();
        UnitDef metalExt = callback.getUnitDefByName("cormex");
        ConstructionTask ct = null;
        boolean found = false;
        while (!found) {
            AIFloat3 metalPos = closestMetalSpot(worker.getPos());
            if (callback.getMap().isPossibleToBuildAt(metalExt, metalPos, Integer.MAX_VALUE)) {
                ct = new ConstructionTask(metalExt, metalPos, 0);
                if (!constructionTasks.contains(ct)) {
                    found = true;
                    break;
                }
            } else {
                AIFloat3 closePos = callback.getMap().findClosestBuildSite(metalExt, metalPos, MAX_BUILD_DIST, 5, Integer.MAX_VALUE);
                ct = new ConstructionTask(metalExt, closePos, 0);
                if (!constructionTasks.contains(ct)) {
                    found = true;
                    break;
                }
            }


            availablemetalspots.remove(metalPos);
        }

        if (found) {
            write("Creating metTask at " + ct.getPos());
            constructionTasks.add(ct);
            metExtractTasks.add(ct);
            worker.setTask(ct, frame);
            ct.addWorker(worker);
            return ct;
        }
        return ct;
    }

    public AIFloat3 closestMetalSpot(AIFloat3 unitposition) {
        AIFloat3 closestspot = null;
        for (AIFloat3 metalspot : availablemetalspots) {
            if (closestspot == null) {
                closestspot = metalspot;
            } else if (Utility.distance(metalspot, unitposition) < Utility.distance(closestspot, unitposition)
                    && metalspot.hashCode() != unitposition.hashCode()
                    && callback.getMap().isPossibleToBuildAt(callback.getUnitDefByName("cormex"), metalspot, 0)) {
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
                    if (Utility.distance(u.getPos(), availablemetalspots.get(m)) < 7)
                        availablemetalspots.remove(m);
                }
            }

            //check for enemy metalExtractors
            for (Enemy e : militaryManager.getVisibleEnemies().values()) {
                if (e.unit.getMaxSpeed() == 0) {
                    for (int m = 0; m < availablemetalspots.size(); m++) {
                        if (Utility.distance(e.getPos(), availablemetalspots.get(m)) < 7) {
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

    public Worker getWorker(BehaviourTree bt) {
        return trees.get(bt);
    }
}
