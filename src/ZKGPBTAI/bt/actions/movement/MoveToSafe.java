package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.influence_map.InfluenceMap;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 29-Feb-16.
 */
public class MoveToSafe extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager em = getBlackboard().economyManager;
        final Worker worker = em.getWorker(tree);

        if (em.caretakers.size() != 0) {
            float closest = Float.MAX_VALUE;
            AIFloat3 closestPos = em.caretakers.get(0).getPos();
            for (Unit u : em.caretakers) {
                float dist = Utility.distance(u.getPos(), worker.getPos());
                if (dist < closest) {
                    closest = dist;
                    closestPos = u.getPos();
                }
            }
            return em.createMoveTask(worker, closestPos);
        }
        return em.createMoveTask(worker, Utility.getNearestSafeHaven(em.influenceManager.im, worker.getUnit().getPos()));
    }

}
