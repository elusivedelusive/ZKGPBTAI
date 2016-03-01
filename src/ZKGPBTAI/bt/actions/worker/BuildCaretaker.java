package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.economy.tasks.WorkerTask;

/**
 * Created by Hallvard on 01.03.2016.
 */
public class BuildCaretaker extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        return getBlackboard().createCaretakerTask(getBlackboard().getWorker(tree));
    }
}
