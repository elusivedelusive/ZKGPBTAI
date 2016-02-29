package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.WorkerTask;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;

import java.util.Random;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveToRandom extends WorkerAction {
    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard();
        final Map map = bb.callback.getMap();
        final Random r = new Random(System.nanoTime());
        final AIFloat3 pos = new AIFloat3(r.nextFloat()*map.getWidth()*8, 0, r.nextFloat()*map.getHeight()*8);

        return bb.createMoveTask(bb.getWorker(tree), pos);
    }
}
