package ZKGPBTAI.bt.conditions.recruitment;

import ZKGPBTAI.Main;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class MoreRaidersThanSkirmishers extends Condition<Main> {
    @Override
    protected boolean condition() {
        Main m = getBlackboard();
        return m.militaryManager.raiderCount > m.militaryManager.skirmisherCount;
    }
}
