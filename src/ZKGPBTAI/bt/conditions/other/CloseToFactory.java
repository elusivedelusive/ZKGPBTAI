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
    final float RADIUS_THRESHOLD = 1.5f;

    @Override
    protected boolean condition() {
        EconomyManager bb = getBlackboard().economyManager;
        final AIFloat3 uPos = bb.getWorker(tree).getUnit().getPos();

        for (Worker w : bb.factories) {
            if (Utility.distance(w.getPos(), uPos) > (w.getUnit().getDef().getLosRadius() * RADIUS_THRESHOLD))
                return true;
        }
        return false;
    }
}
