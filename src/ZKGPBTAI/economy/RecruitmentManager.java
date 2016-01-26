package ZKGPBTAI.economy;

import ZKGPBTAI.Manager;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jonatan on 25-Jan-16.
 */
public class RecruitmentManager extends Manager {

    @Override
    public String getModuleName() {
        return "RecruitmentManager";
    }

    ArrayList<String> factoryNames = new ArrayList<String>(Arrays.asList(
            "factorycloak",
            "factoryhover",
            "factoryamph",
            "factorygunship",
            "factoryjump",
            "factoryplane",
            "factoryspider",
            "factorytank",
            "factoryshield",
            "factoryship",
            "factoryveh"));
    public ArrayList<Unit> factories;

    public RecruitmentManager() {

        factories = new ArrayList<>();
        setRecruitmentManager(this);
    }

    @Override
    public int update(int frame) {
        this.frame = frame;


        return 0;
    }

    @Override
    public int unitFinished(Unit u) {
        if (isFactory(u) && !factories.contains(u)) {
            factories.add(u);
            assignRecruitmentTask(u);
        }

        return 0;
    }

    @Override
    public int unitCreated(Unit u, Unit builder) {
        //first factory
        if (isFactory(u) && !u.isBeingBuilt() && builder != null && !factories.contains(u)) {
            factories.add(u);
            assignRecruitmentTask(u);
        }
        return 0;
    }

    @Override
    public int unitIdle(Unit u) {
        if(factories.contains(u)){
            assignRecruitmentTask(u);
        }
        return 0;
    }

    @Override
    public int unitDestroyed(Unit u, Unit killer) {
        if (factories.contains(u))
            factories.remove(u);
        return 0;
    }

    public boolean isFactory(Unit u) {
        return (factoryNames.contains(u.getDef().getName())) ? true : false;
    }

    public UnitDef chooseNewFactory() {
        return callback.getUnitDefByName("factorycloak");
    }

    private Boolean needWorkers() {
        if (((float) economyManager.workers.size() - 1 < Math.floor(economyManager.effectiveIncome / 5))) {
            return true;
        }
        return false;
    }

    public void assignRecruitmentTask(Unit u){
        UnitDef toRecruit;
        switch (u.getDef().getName()){
            case "factorycloak":
                toRecruit = callback.getUnitDefByName(getUnitFromCloakFac());
                u.build(toRecruit, u.getPos(), (short) 0, (short) 0, frame + 3000);
                break;
            case "factoryhover":
                toRecruit = callback.getUnitDefByName(getUnitFromHoverFac());
                u.build(toRecruit, u.getPos(), (short) 0, (short) 0, frame + 3000);
                break;
        }
    }

    public String getUnitFromCloakFac() {
        if (needWorkers()) return "armrectr";
        double rand = Math.random();
        if (economyManager.effectiveIncome < 70) {
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

    private String getUnitFromHoverFac() {
        //TODO corches never build buildings for some reason
        //if (needWorkers()) return "corch";
        double rand = Math.random();
        if (economyManager.effectiveIncome < 30) {
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
