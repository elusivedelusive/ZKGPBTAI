package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.utils.MapHandler;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveToTension extends WorkerAction {


    final float DISTANCE_MULTIPLIER = 0.5f;

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard();
        Worker w = bb.getWorker(tree);

        final AIFloat3 pos = bb.influenceManager.im.getNTopLocations(1, bb.influenceManager.im.getTensionMap()).get(0);
        final double distance = Utility.distance(w.getPos(), pos) * DISTANCE_MULTIPLIER;

        return bb.createMoveTask(w, MapHandler.getPoint(w.getPos(), MapHandler.angleDegrees(w.getPos(), pos), distance));
    }
}
