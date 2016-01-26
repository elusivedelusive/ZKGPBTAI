package ZKGPBTAI;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.RecruitmentManager;
import ZKGPBTAI.influence_map.InfluenceManager;
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
    public static EconomyManager economyManager;
    public static MilitaryManager militaryManager;
    public static InfluenceManager influenceManager;
    public static RecruitmentManager recruitmentManager;
    public static Resource m, e;
    protected Game game;
    protected Economy economy;
    public static OOAICallback callback;
    protected int frame = 0;

    public abstract String getModuleName();

    public void setInfluenceManager(InfluenceManager im) {
        this.influenceManager = im;
    }

    public void setMilitaryManager(MilitaryManager mm) {
        this.militaryManager = mm;
    }

    public void setEcoManager(EconomyManager em) {
        this.economyManager = em;
    }

    public void setRecruitmentManager(RecruitmentManager rm) {
        this.recruitmentManager = rm;
    }

    public void write(String msg){
        callback.getGame().sendTextMessage(msg, 0);
    }
}
