package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.utils.Utility;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 29-Feb-16.
 */
public class IsAreaControlled extends Condition<Main>{
    @Override
    protected boolean condition() {
        EconomyManager em = getBlackboard().economyManager;
        Unit u = em.getWorker(tree).getUnit();

        return Utility.isAreaControlled(u.getPos(), em.defences, em.militaryManager.getEnemies());
    }
}
