package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import com.springrts.ai.oo.AIFloat3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class WorkerTask {
    public List<Worker> assignedWorkers;
    public AIFloat3 position;

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
}
