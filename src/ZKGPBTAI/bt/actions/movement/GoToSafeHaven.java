package ZKGPBTAI.bt.actions.movement;

import ZKGPBTAI.bt.actions.worker.WorkerAction;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.tasks.WorkerTask;
import ZKGPBTAI.influence_map.InfluenceMap;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 29-Feb-16.
 */
public class GoToSafeHaven extends WorkerAction{

    @Override
    protected WorkerTask getWorkerTask() {
        EconomyManager em = getBlackboard();
        Unit u = getBlackboard().getWorker(tree).getUnit();

        AIFloat3 safety = Utility.getNearestSafeHaven(em.influenceManager.im, u.getPos());
        return null;//u.moveTo(safety, (short) 0, 6000);
    }

}
