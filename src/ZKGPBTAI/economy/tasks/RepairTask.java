package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.sun.istack.internal.NotNull;

/**
 * Created by Jonatan on 13-Jan-16.
 */
public class RepairTask extends WorkerTask {
    public Unit target;

    public RepairTask(Unit target){
        this.target = target;
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        return false;
    }

    @Override
    public AIFloat3 getPos(){
        return target.getPos();
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof RepairTask){
            RepairTask wt = (RepairTask)other;
            return (target.getUnitId() == wt.target.getUnitId());
        }
        return false;
    }

    @Override
    public String toString() {
        return " to repair " + target.getDef().getName();
    }
}
