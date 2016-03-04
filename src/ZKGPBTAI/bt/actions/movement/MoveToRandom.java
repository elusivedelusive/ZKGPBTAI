package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.utils.MapHandler;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;

import java.util.Random;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveToRandom extends WorkerAction {

    public final float DISTANCE_MULTIPLIER = 0.5f;

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard().economyManager;
        Worker w = bb.getWorker(tree);
        Map map = bb.callback.getMap();

        final Random r = new Random(System.nanoTime());
        final AIFloat3 pos = new AIFloat3(r.nextFloat()*map.getWidth()*8, 0, r.nextFloat()*map.getHeight()*8);
        final double distance = Utility.distance(w.getPos(), pos)*DISTANCE_MULTIPLIER;

        return bb.createMoveTask(w, MapHandler.getPoint(w.getPos(), MapHandler.angleDegrees(w.getPos(), pos), distance));
    }
}
