package ZKGPBTAI.bt.conditions.recruitment;

import ZKGPBTAI.Main;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class MoreAssaultThanRiot extends Condition<Main> {
    @Override
    protected boolean condition() {
        Main m = getBlackboard();
        return m.militaryManager.assaultCount > m.militaryManager.riotCount;
    }
}
