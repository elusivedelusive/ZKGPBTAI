package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.military.Enemy;
import ZKGPBTAI.utils.Utility;
import bt.leaf.Condition;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 11-Mar-16.
 */
public class CloseToFactory extends Condition<Main> {

    @Override
    protected boolean condition() {
        EconomyManager bb = getBlackboard().economyManager;
        final Unit u = bb.getWorker(tree).getUnit();

        for (Worker w : bb.factories) {

            if (Utility.distance(w.getPos(), u.getPos()) < (u.getDef().getBuildDistance()*3.5))
                return true;
        }
        return false;
    }
}
