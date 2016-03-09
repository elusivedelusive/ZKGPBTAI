package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.utils.MapHandler;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Map;

import java.util.List;

/**
 * Created by Jonatan on 24-Feb-16.
 */
public class InRadarRange extends Condition<Main> {

    @Override
    protected boolean condition() {
        final int GRANUALITY = 8;
        final int SCALE = GRANUALITY*8; //8 = difference between scale 1 and coordinates

        Map map = getBlackboard().economyManager.callback.getMap();
        Worker worker = getBlackboard().economyManager.getWorker(tree);

        List<Integer> radar = MapHandler.scale(map, map.getRadarMap(), GRANUALITY);
        final int x = (int)worker.getPos().x/SCALE;
        final int z = (int)worker.getPos().z/SCALE;
        return radar.get(z*(map.getHeight()/GRANUALITY) + x) > 0; //getHeight is on scale 1 in MapHandler
    }
}
