package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 24-Feb-16.
 */
public class InRadarRange extends Condition<EconomyManager> {

    @Override
    protected boolean condition() {
        Unit u = getBlackboard().getWorker(tree).getUnit();

        EconomyManager em = getBlackboard();
        if (em.callback.getMap().getRadarMap().get((int) ((u.getPos().z * em.callback.getMap().getWidth() * u.getPos().x) / 8)) == 1)
            return true;
        else
            return false;
    }
}
