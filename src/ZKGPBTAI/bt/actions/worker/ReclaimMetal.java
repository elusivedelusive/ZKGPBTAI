package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.economy.tasks.ReclaimTask;
import ZKGPBTAI.economy.tasks.WorkerTask;

/**
 * Created by Hallvard on 02.03.2016.
 */
public class ReclaimMetal extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        ReclaimTask rt = getBlackboard().createReclaimTask(getBlackboard().getWorker(tree));
        return null;
    }
}
