package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;

/**
 * Created by Hallvard on 24.02.2016.
 */
public class IsCloaked extends Condition<Main> {

    @Override
    protected boolean condition() {
        return getBlackboard().economyManager.getWorker(tree).getUnit().isCloaked();
    }
}
