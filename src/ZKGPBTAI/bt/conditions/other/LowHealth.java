package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Hallvard on 23.02.2016.
 */
public class LowHealth extends Condition<EconomyManager> {
    
    final float THRESHOLD = 50;//Percent
    
    @Override
    protected boolean condition() {
        Unit unit = getBlackboard().getWorker(tree).getUnit();
        
        return unit.getMaxHealth() * (THRESHOLD/100) >= unit.getHealth();
    }
}d
