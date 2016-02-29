package ZKGPBTAI.bt.conditions.other;

import ZKGPBTAI.Main;
import ZKGPBTAI.economy.EconomyManager;
import bt.leaf.Condition;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;
import com.springrts.ai.oo.clb.Unit;

import java.util.List;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class TopOfHill extends Condition<EconomyManager> {

    /**
     * TODO Not the best way, find a better solution..
     * @return  Wheather the unit is situated on top of a local hill
     */
    @Override
    protected boolean condition() {
        EconomyManager bb = getBlackboard();
        final AIFloat3 pos = bb.getWorker(tree).getUnit().getPos();
        final Map map = bb.callback.getMap();

        final int x = (int)Math.floor(pos.x/8);
        final int y = (int)Math.floor(pos.z/8);

        //Give the unit-pos a slight advantage.
        final float highest = (float)Math.ceil(map.getElevationAt(x, y));

        final int r = 5; //radius
        for(int ys = y-r; ys<y+r; ys++) {
            for(int xs=x-r; xs<x+r; xs++) {
                if(ys<0 || xs<0)
                    continue;
                float f = map.getElevationAt(xs, ys);
                if(f > highest)
                    return false;
            }
        }
        return true;
    }
}
