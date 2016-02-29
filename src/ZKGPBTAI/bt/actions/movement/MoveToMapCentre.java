package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.economy.tasks.WorkerTask;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveToMapCentre extends WorkerAction {

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager bb = getBlackboard();
        final Map map = bb.callback.getMap();
        final AIFloat3 pos = new AIFloat3(map.getWidth()*4, 0, map.getHeight()*4); // 4 = 8(Granuality) / 2(map centre)

        return bb.createMoveTask(bb.getWorker(tree), pos);
    }
}
