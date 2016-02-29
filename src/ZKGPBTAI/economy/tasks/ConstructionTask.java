package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import bt.Task;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import com.sun.istack.internal.Nullable;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class ConstructionTask extends WorkerTask {
    public UnitDef buildType;
    public int facing;
    public Unit target;

    public ConstructionTask (UnitDef def, AIFloat3 pos, int h){
        super();
        this.position = pos;
        this.buildType = def;
        this.facing = h;
        this.target = null;
    }

    @Override
    public boolean start(Worker w) {
        w.getUnit().build(buildType, getPos(), facing, (short) 0, Integer.MAX_VALUE);
        return true; //TODO
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConstructionTask) {
            ConstructionTask wt = (ConstructionTask) other;
            return (buildType.getUnitDefId() == wt.buildType.getUnitDefId() && position.x == wt.position.x && position.z == wt.position.z && facing == wt.facing);
        }
        return false;
    }

    @Override
    public String toString() {
        return " to build "+this.buildType.getName() + " at " + "x:" + position.x + " z:" + position.z;
    }
}
