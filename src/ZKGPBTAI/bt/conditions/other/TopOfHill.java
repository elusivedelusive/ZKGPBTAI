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
public class TopOfHill extends Condition<Main> {

    /**
     * TODO Not the best way, find a better solution..
     * @return  Wheather the unit is situated on top of a local hill
     */
    @Override
    protected boolean condition() {
        EconomyManager bb = getBlackboard().economyManager;
        final AIFloat3 pos = bb.getWorker(tree).getUnit().getPos();
        final Map map = bb.callback.getMap();

        final int x = (int)pos.x;
        final int z = (int)pos.z;


        //JONATAN
        int radius = (int)bb.getWorker(tree).getUnit().getMaxRange();
        float unitElevation = (float)Math.ceil(map.getElevationAt(x, z));
        int width = map.getWidth()*8;
        int height = map.getHeight()*8;
        //bb.write("height at pos: "+ map.getHeightMap().get(z*height + x));
        //bb.write("elevation at pos: " + map.getElevationAt(x*8, z*8));

        int[] xd = {-1, -1, 0, +1, +1, +1, 0, -1};
        int[] yd = {0, -1, -1, -1, 0, +1, +1, +1};
        float totalHeight = 0;
        float heights = 0;
        float minHeight = Float.MAX_VALUE;
        for (int d = 0; d < 8; d++) {

            //check that we are within bounds
            if (((x + (xd[d] * radius)) > width) || ((x + (xd[d] * radius)) < 0)
                    || ((z + (yd[d] * radius)) > height) || ((z + (yd[d] * radius)) < 0)) {
                continue;
            }



            for (int r = 0; r <= radius; r++) {
                heights++;
                float value = map.getElevationAt(x+(xd[d] *r), z+(yd[d] *r));
                totalHeight += value;
                if(value < minHeight)
                    minHeight = value;
            }


        }
        totalHeight = totalHeight - (minHeight *heights);
        unitElevation = unitElevation - minHeight;
        float avgHeight = totalHeight/heights;

        //bb.write("range = " + radius);
        //bb.write("highest = " +  unitElevation);
        //bb.write("avg = " +  avgHeight);
        //bb.write("elevation = " + (unitElevation/avgHeight));
        if((unitElevation/avgHeight) > 1.15 && (unitElevation-avgHeight) > 10)
            return true;
        return false;
    }
}
