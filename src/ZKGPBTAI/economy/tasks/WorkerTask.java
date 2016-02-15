package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import bt.Task;
import com.springrts.ai.oo.AIFloat3;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class WorkerTask extends Observable {
    public List<Worker> assignedWorkers;
    public AIFloat3 position;

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
        if(null == result)
            setResult(false);
        return assignedWorkers;
    }

    public void setResult(boolean result) {
        this.result = result;
        setChanged();
        notifyObservers();
    }

    public Boolean getResult() {
        return result;
    }
}
