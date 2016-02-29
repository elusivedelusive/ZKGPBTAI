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
public class MoveToSafe extends WorkerAction{

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager em = getBlackboard();
        final Worker worker = em.getWorker(tree);

        return em.createMoveTask(worker, Utility.getNearestSafeHaven(em.influenceManager.im, worker.getUnit().getPos()));
    }

}
