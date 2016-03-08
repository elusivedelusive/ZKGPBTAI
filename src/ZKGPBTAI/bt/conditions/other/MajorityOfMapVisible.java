package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.utils.MapHandler;
import bt.leaf.Condition;
import com.springrts.ai.oo.clb.Map;

import java.util.List;
import java.util.Optional;

/**
 * Created by Hallvard on 24.02.2016.
 */
public class MajorityOfMapVisible extends Condition<Main> {
    @Override
    protected boolean condition() {
        EconomyManager em = getBlackboard().economyManager;
        em.write("MajorityOfMapVisible called()");
        Map map = em.callback.getMap();
        List<Integer> los = MapHandler.scale(map, map.getLosMap(), 8);
        List<Integer> radar = MapHandler.scale(map, map.getRadarMap(), 8);

        assert(los.size() == radar.size());
        em.write("MajorityOfMapVisible: " + los.size() + "_" + radar.size());

        int observable = 0;
        int fogOfWar = 0;

        for(int i=0; i<los.size(); i++) {
            if(los.get(i)==0 && radar.get(i)==0) {
                fogOfWar++;
            } else
                observable++;
        }
        em.write("MajorityOfMapVisible result: "+observable+"_vs_"+fogOfWar);
        return observable > fogOfWar;
    }
}
