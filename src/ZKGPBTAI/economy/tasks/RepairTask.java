package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.Enumerations;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.sun.istack.internal.NotNull;

/**
 * Created by Jonatan on 13-Jan-16.
 */
public class RepairTask extends WorkerTask {

    public static final double REPAIR_THRESHOLD = 0.9d;

    public Unit target;

    public RepairTask(Unit target){
        this.target = target;
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        if(Utility.distance(worker.getPos(), target.getPos()) > worker.getUnit().getDef().getBuildDistance())
            worker.getUnit().moveTo(target.getPos(), (short)0, Integer.MAX_VALUE);
        worker.getUnit().repair(target, (short) Enumerations.UnitCommandOptions.UNIT_COMMAND_OPTION_SHIFT_KEY.getValue(), Integer.MAX_VALUE);
        return true;
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
