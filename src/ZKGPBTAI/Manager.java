package ZKGPBTAI;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.RecruitmentManager;
import ZKGPBTAI.influence_map.InfluenceManager;
import ZKGPBTAI.los.LOSManager;
import ZKGPBTAI.military.MilitaryManager;
import com.springrts.ai.oo.AbstractOOAI;
import com.springrts.ai.oo.clb.Economy;
import com.springrts.ai.oo.clb.Game;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Resource;


/**
 * Created by Jonatan on 30-Nov-15.
 */
public abstract class Manager extends AbstractOOAI {
    public EconomyManager economyManager;
    public MilitaryManager militaryManager;
    public InfluenceManager influenceManager;
    public RecruitmentManager recruitmentManager;
    public LOSManager losManager;
    public Resource m, e;
    protected Game game;
    protected Economy economy;
    public OOAICallback callback;
    public boolean runningBt = false;
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
