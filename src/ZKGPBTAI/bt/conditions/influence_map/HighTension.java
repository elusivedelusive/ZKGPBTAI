package ZKGPBTAI.bt.conditions.influence_map;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 24-Feb-16.
 */
public class HighTension extends Condition<Main>{

    @Override
    protected boolean condition() {
        EconomyManager em = getBlackboard().economyManager;
        return em.influenceManager.im.isHighTension(em.getWorker(tree).getUnit().getPos());
    }
}
