package ZKGPBTAI.bt.conditions.economy;

import ZKGPBTAI.Main;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class HighEnergy extends Condition<Main> {
    @Override
    protected boolean condition() {
        Main m = getBlackboard();
        return ((m.economyManager.energy/m.economyManager.energyStorage) > 0.9 && (m.economyManager.effectiveIncomeEnergy - m.economyManager.expendEnergy) > 0);
    }
}
