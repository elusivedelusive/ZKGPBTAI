package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.WorkerTask;
import com.springrts.ai.oo.AIFloat3;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveToTension extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard();
        final AIFloat3 pos = (null); // TODO Jonatan u fix? :)

        return bb.createMoveTask(bb.getWorker(tree), pos);
    }
}
