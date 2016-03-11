package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.RepairTask;
import ZKGPBTAI.economy.tasks.WorkerTask;

/**
 * Created by Hallvard on 10.03.2016.
 */
public class RepairUnit extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
         EconomyManager em = getBlackboard().economyManager;
         RepairTask rt =  em.createRepairTask(em.getWorker(tree));
        if(rt == null)
            result = true;
        return rt;
    }
}
