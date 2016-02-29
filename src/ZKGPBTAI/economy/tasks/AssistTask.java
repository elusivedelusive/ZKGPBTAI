package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public class AssistTask extends WorkerTask{
    public Unit target;
    public int frameStart;
    public int jobLength = 1000;

    public AssistTask(Worker w, int frameStart, Unit target){
        this.assignedWorkers = new ArrayList<>();
        this.position = new AIFloat3();
        this.frameStart = frameStart;
        w.getUnit().guard(target,(short)0, 1000);
    }

    public boolean isDone(int frame){
        if(frame - frameStart > jobLength)
            return true;
        return false;
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        return false;
    }
}
