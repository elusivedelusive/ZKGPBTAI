package ZKGPBTAI;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.RecruitmentManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.influence_map.InfluenceManager;
import ZKGPBTAI.los.LOSManager;
import ZKGPBTAI.military.MilitaryManager;
import bt.BehaviourTree;
import com.springrts.ai.oo.AbstractOOAI;
import com.springrts.ai.oo.clb.Economy;
import com.springrts.ai.oo.clb.Game;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Resource;

import java.util.HashMap;
import java.util.Optional;


/**
 * Created by Jonatan on 30-Nov-15.
 */
public abstract class Manager extends AbstractOOAI {
    public static EconomyManager economyManager;
    public static MilitaryManager militaryManager;
    public static InfluenceManager influenceManager;
    public static RecruitmentManager recruitmentManager;
    public static LOSManager losManager;
    public static Resource m, e;
    protected static Game game;
    protected static Economy economy;
    public static OOAICallback callback;
    public static boolean runningBt = false;
    protected int frame = 0;



    public abstract String getModuleName();

    public void setInfluenceManager(InfluenceManager im) {
        this.influenceManager = im;
    }

    public void setMilitaryManager(MilitaryManager mm) {
        militaryManager = mm;
    }

    public void setEcoManager(EconomyManager em) {
        economyManager = em;
    }

    public void setRecruitmentManager(RecruitmentManager rm) {
        recruitmentManager = rm;
    }

    public void setLosManager(LOSManager lm) {
        losManager = lm;
    }

    public void write(String msg){
        callback.getGame().sendTextMessage(msg, 0);
    }
}
