package ZKGPBTAI.bt.actions.worker;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.WorkerTask;
import bt.leaf.Action;
import bt.utils.BooleanData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

import java.util.Observable;
import java.util.Optional;

/**
 * Created by Hallvard on 15.02.2016.
 */
public abstract class WorkerAction extends Action<EconomyManager> {

    // Three states: null(Running), true(Succeed) and false(failed)
    protected Boolean result = (null);

    protected abstract WorkerTask getWorkerTask();

    @Override
    public void start() {
        getBlackboard().write("BT - Starting task " + this.getStandardName());
        Optional<WorkerTask> task = Optional.ofNullable(getWorkerTask());

        if(task.isPresent()) {
            task.get().addObserver((Observable obs, Object val) -> {
                if (obs == task.get())
                    result = task.get().getResult();
                getBlackboard().write("Observer " + result);
                task.get().deleteObservers();
            });
        } else if(null == result)
            result = false;
    }


    @Override
    public TaskState execute() {
        if (null == result)
            return TaskState.RUNNING;
        return result ? TaskState.SUCCEEDED : TaskState.FAILED;
    }

    @Override
    public void reset() {
        super.reset();
        this.result = null;
    }

    @Override
    public void eval(EvolutionState evolutionState, int i, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        BooleanData dat = (BooleanData) gpData;
        dat.result = Math.random() > 0.3; //TODO
    }
}
