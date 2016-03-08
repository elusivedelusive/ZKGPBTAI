package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.utils.MapHandler;
import ZKGPBTAI.utils.Utility;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Unit;

import java.util.List;

/**
 * Created by Jonatan on 24-Feb-16.
 */
public class InRadarRange extends Condition<Main> {

    @Override
    protected boolean condition() {
        Unit u = getBlackboard().economyManager.getWorker(tree).getUnit();

        return Utility.inRadarRange(getBlackboard().getCallback(), u);
    }
}
