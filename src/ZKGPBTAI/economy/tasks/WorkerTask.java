package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import bt.Task;
import com.springrts.ai.oo.AIFloat3;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public abstract class WorkerTask extends Observable {
    public List<Worker> assignedWorkers;
    public AIFloat3 position;

    /**
     * Start the task
     * @param worker    worker to start the task
     * @return          Task successfully started.
     */
    public abstract boolean start(@NotNull Worker worker);


    // Three states: null(Running), true(Succeed) and false(failed)
    private Boolean result = (null);

    public WorkerTask(){
        this.assignedWorkers = new ArrayList<>();
        this.position = new AIFloat3();
    }

    public AIFloat3 getPos(){
        return this.position;
    }

    public void addWorker(Worker w){
        this.assignedWorkers.add(w);
    }

    public void removeWorker(Worker w){
        this.assignedWorkers.remove(w);
    }

    public List<Worker> stopWorkers(int frame){
        for (Worker w: assignedWorkers){
            w.clearTask(frame);
        }
        return assignedWorkers;
    }

    public void complete(){ setResult(Boolean.TRUE);}
    public void fail() { setResult(Boolean.FALSE);}

    /**
     * Use complete and fail instead!
     * @param result    result to be set
     */
//    @Deprecated
//    public void setResult(boolean result){
//        setResult(new Boolean(result));
//    }

    private void setResult(Boolean result) {
        this.result = result;
        setChanged();
        notifyObservers();
    }

    public Boolean getResult() {
        return result;
    }

    /**
     * Starts the task for all assigned workers
     * @return  false if a single worker failed
     */
    public boolean startAll() {
        List<Worker> starts = assignedWorkers.stream().filter(w -> !start(w)).collect(Collectors.toList());
        return starts.isEmpty();
    }
}
