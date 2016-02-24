package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.military.Enemy;
import ZKGPBTAI.utils.Utility;
import bt.leaf.Condition;
import com.springrts.ai.oo.AIFloat3;

/**
 * Created by Hallvard on 23.02.2016.
 */
public class EnemyBuildingNear extends Condition<EconomyManager> {

    final float RADIUS_THRESHOLD = 1.5f;

    @Override
    protected boolean condition() {
        EconomyManager bb = getBlackboard();
        final AIFloat3 uPos = bb.getWorker(tree).getUnit().getPos();

        for(Enemy e : bb.militaryManager.getVisibleEnemies().values()) {
            if(e.isStatic)
                if(Utility.distance(e.getPos(), uPos) > (e.unit.getDef().getLosRadius()*RADIUS_THRESHOLD))
                    return true;
        }
        return false;
    }
}
