package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.utils.MapHandler;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Map;

import java.util.List;

/**
 * Created by Hallvard on 24.02.2016.
 */
public class MajorityOfMapVisible extends Condition<EconomyManager> {
    @Override
    protected boolean condition() {
        getBlackboard().write("MajorityOfMapVisible called()");
        Map map = getBlackboard().callback.getMap();
        List<Integer> los = MapHandler.scale(map, map.getLosMap(), 8);
        List<Integer> radar = MapHandler.scale(map, map.getRadarMap(), 8);

        assert(los.size() == radar.size());

        int observable = 0;
        int fogOfWar = 0;

        for(int i=0; i<los.size(); i++) {
            if(los.get(i) != 0 ||  radar.get(i) != 0) {
                observable++;
                break;
            }
            fogOfWar++;
        }
        getBlackboard().write("MajorityOfMapVisible result: "+observable+"_vs_"+fogOfWar);
        return observable > fogOfWar;
    }
}
