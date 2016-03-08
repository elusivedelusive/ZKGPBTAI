package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.WorkerTask;

/**
 * Created by Hallvard on 01.03.2016.
 */
public class BuildCaretaker extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager em = getBlackboard().economyManager;
        return em.createCaretakerTask(em.getWorker(tree));
    }
}
