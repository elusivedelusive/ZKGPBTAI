package ZKGPBTAI.bt.conditions.influence_map;

import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 24-Feb-16.
 */
public class HighTension extends Condition<EconomyManager>{
    EconomyManager em = getBlackboard();
    @Override
    protected boolean condition() {
        return em.influenceManager.im.isHighTension(em.getWorker(tree).getUnit().getPos());
    }
}
