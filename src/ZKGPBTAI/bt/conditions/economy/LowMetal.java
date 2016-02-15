package ZKGPBTAI.bt.conditions.economy;

import ZKGPBTAI.Main;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class LowMetal extends Condition<Main> {
    @Override
    protected boolean condition() {

        Main m = getBlackboard();

        return ((m.economyManager.metal/m.economyManager.metalStorage) > 0.1 && (m.economyManager.effectiveIncomeMetal - m.economyManager.expendMetal) > -10);
    }
}
