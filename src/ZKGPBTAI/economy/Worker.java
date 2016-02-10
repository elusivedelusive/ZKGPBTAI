package ZKGPBTAI.economy;

import ZKGPBTAI.economy.tasks.ConstructionTask;
import ZKGPBTAI.economy.tasks.WorkerTask;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class Worker {

    private Unit unit;
    public int id;
    AIFloat3 lastPos;
    private WorkerTask task;
    int lastTaskFrame = 0;

    Worker(Unit unit) {
        this.unit = unit;
        this.task = null;
        this.id = unit.getUnitId();
        this.lastPos = unit.getPos();
    }

    public void setTask(WorkerTask task, int frame) {
        this.task = task;
        this.lastPos = unit.getPos();
        lastTaskFrame = frame;
    }

    public WorkerTask getTask() {
        return task;
    }

    public Unit getUnit() {
        return unit;
    }

    public AIFloat3 getPos() {
        return unit.getPos();
    }

    public void clearTask(int frame) {
        this.task = null;
        lastTaskFrame = frame;
        if (unit.getHealth() > 0) {
            unit.stop((short) 0, frame + 300);
        }
        lastPos = getPos();
    }


    public boolean unstick(int frame) {
        //if has task and 150 frames have passed
        if (task != null && frame - lastTaskFrame > 150) {
            //distance moved
            float movedist = distance(unit.getPos(), lastPos);
            //distance to task
            float jobdist = distance(unit.getPos(), task.getPos());

            if (movedist < 50 && jobdist > unit.getDef().getBuildDistance() + 5) {
                AIFloat3 unstickPoint = getRadialPoint(unit.getPos(), 75f);
                unit.moveTo(unstickPoint, (short) 0, frame + 6000);
                lastTaskFrame = frame;
                lastPos = unit.getPos();
                return true;
            }
            lastTaskFrame = frame;
            lastPos = unit.getPos();
        }
        return false;
    }

    protected float distance(AIFloat3 pos1, AIFloat3 pos2) {
        float x1 = pos1.x;
        float z1 = pos1.z;
        float x2 = pos2.x;
        float z2 = pos2.z;
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
    }

    protected AIFloat3 getRadialPoint(AIFloat3 position, Float radius) {
        // returns a random point lying on a circle around the given position.
        AIFloat3 p = new AIFloat3();
        double angle = Math.random() * 2 * Math.PI;
        double vx = Math.cos(angle);
        double vz = Math.sin(angle);
        p.x = (float) (position.x + radius * vx);
        p.z = (float) (position.z + radius * vz);
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Worker) {
            Worker w = (Worker) o;
            return (w.id == this.id);
        }
        return false;
    }

}
