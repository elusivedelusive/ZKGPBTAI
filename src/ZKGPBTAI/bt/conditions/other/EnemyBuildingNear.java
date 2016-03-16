package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.military.Enemy;
import ZKGPBTAI.utils.Utility;
import bt.leaf.Condition;
import com.springrts.ai.oo.AIFloat3;

/**
 * Created by Hallvard on 23.02.2016.
 */
public class EnemyBuildingNear extends Condition<Main> {

    final float RADIUS_THRESHOLD = 1.5f;

    @Override
    protected boolean condition() {
        EconomyManager bb = getBlackboard().economyManager;
        final AIFloat3 uPos = bb.getWorker(tree).getUnit().getPos();
        final float range = bb.getWorker(tree).getUnit().getDef().getBuildDistance();

        for(Enemy e : bb.militaryManager.getVisibleEnemies().values()) {
            if(e.isStatic)
                if(Utility.distance(e.getPos(), uPos) < (range*RADIUS_THRESHOLD))
                    return true;
        }
        return false;
    }
}
