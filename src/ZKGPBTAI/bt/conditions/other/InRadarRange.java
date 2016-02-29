package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.utils.MapHandler;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Unit;

import java.util.List;

/**
 * Created by Jonatan on 24-Feb-16.
 */
public class InRadarRange extends Condition<EconomyManager> {

    @Override
    protected boolean condition() {
        Unit u = getBlackboard().getWorker(tree).getUnit();

        return getBlackboard().inRadarRange(u);
    }
}
