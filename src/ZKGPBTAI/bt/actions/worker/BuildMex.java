package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.WorkerTask;
import bt.leaf.Action;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class BuildMex  extends WorkerAction {
    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard();
        return bb.economyManager.createMetalExtractorTask(bb.getWorker(tree));
    }
}
