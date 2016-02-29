package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.utils.Utility;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 29-Feb-16.
 */
public class IsAreaControlled extends Condition<EconomyManager>{
    @Override
    protected boolean condition() {
        Unit u = getBlackboard().getWorker(tree).getUnit();
        EconomyManager em = getBlackboard();
        return Utility.isAreaControlled(u.getPos(), em.defences, em.militaryManager.getEnemies());
    }
}
