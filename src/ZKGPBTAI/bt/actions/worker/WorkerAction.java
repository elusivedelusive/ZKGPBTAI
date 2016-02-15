package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.tasks.WorkerTask;
import bt.leaf.Action;
import bt.utils.BooleanData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

import java.util.Observable;

/**
 * Created by Hallvard on 15.02.2016.
 */
public abstract class WorkerAction extends Action<Main> {

    // Three states: null(Running), true(Succeed) and false(failed)
    private Boolean result = (null);

    protected abstract WorkerTask getConstructionTask();

    @Override
    public void start() {
        WorkerTask task = getConstructionTask();
        task.addObserver((Observable obs, Object val) -> {
            if(obs == task)
                result = task.getResult();
        });
    }

    @Override
    public TaskState execute() {
        if(null == result)
            return TaskState.RUNNING;
        return result ? TaskState.SUCCEEDED : TaskState.FAILED;
    }

    @Override
    public void reset(){
        super.reset();
        this.result = null;
    }

    @Override
    public void eval(EvolutionState evolutionState, int i, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        BooleanData dat = (BooleanData)gpData;
        dat.result = Math.random() > 0.3; //TODO
    }
}
