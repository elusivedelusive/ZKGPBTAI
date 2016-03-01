package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.utils.MapHandler;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveToMapCentre extends WorkerAction {

    public final float DISTANCE_MULTIPLIER = 0.5f;

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard();
        Worker w = bb.getWorker(tree);
        Map map = bb.callback.getMap();

        final AIFloat3 centre = new AIFloat3(map.getWidth()*4, 0, map.getHeight()*4); // 4 = 8(Granuality) / 2(map centre)
        final float dist = Utility.distance(w.getPos(), centre)*DISTANCE_MULTIPLIER;

        AIFloat3 destination = MapHandler.getPoint(w.getPos(), MapHandler.angleDegrees(w.getPos(), centre), dist);
        /*
        getBlackboard().write("MoveToMapCentre: Old  position! x="+w.getPos().x+"  z="+w.getPos().z);
        getBlackboard().write("MoveToMapCentre: New  position! x="+destination.x+"  z="+destination.z);
        getBlackboard().write("MoveToMapCentre: Full position! x="+centre.x+"  z="+centre.z);
        */
        return bb.createMoveTask(w, destination);
    }
}
