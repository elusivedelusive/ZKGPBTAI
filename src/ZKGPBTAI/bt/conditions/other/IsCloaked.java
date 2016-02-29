package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;

/**
 * Created by Hallvard on 24.02.2016.
 */
public class IsCloaked extends Condition<EconomyManager> {

    @Override
    protected boolean condition() {
        return getBlackboard().getWorker(tree).getUnit().isCloaked();
    }
}
