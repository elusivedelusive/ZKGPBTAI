package ZKGPBTAI.bt.conditions.economy;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class LowMetal extends Condition<EconomyManager> {
    @Override
    protected boolean condition() {

        EconomyManager m = getBlackboard();

        return ((m.economyManager.metal/m.economyManager.metalStorage) < 0.1 && (m.economyManager.effectiveIncomeMetal - m.economyManager.expendMetal) < 0);
    }
}
