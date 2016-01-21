package ZKGPBTAI;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.influence_map.InfluenceManager;
import ZKGPBTAI.military.MilitaryManager;
import com.springrts.ai.oo.AbstractOOAI;
import com.springrts.ai.oo.clb.Resource;


/**
 * Created by Jonatan on 30-Nov-15.
 */
public abstract class Manager extends AbstractOOAI {
    public EconomyManager economyManager;
    public MilitaryManager militaryManager;
    public InfluenceManager influenceManager;
    public Resource m, e;


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
}
