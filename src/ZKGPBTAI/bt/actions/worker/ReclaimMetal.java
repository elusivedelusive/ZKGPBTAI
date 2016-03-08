package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.ReclaimTask;
import ZKGPBTAI.economy.tasks.WorkerTask;

/**
 * Created by Hallvard on 02.03.2016.
 */
public class ReclaimMetal extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        ReclaimTask rt = getBlackboard().economyManager.createReclaimTask(getBlackboard().economyManager.getWorker(tree));
        if(null == rt) {
            result = true;
        }
        return rt;
    }
}
