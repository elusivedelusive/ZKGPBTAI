package ZKGPBTAI.utils;

import com.springrts.ai.oo.clb.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hallvard on 24.02.2016.
 */
public class MapHandler {

    /**
     *  WIDTH/HEIGHT    : 1
     *  HEIGHTMAP       : 1
     *
     *  RADAR           : 8
     *  LOS             : 16
     *
     * @param map
     * @param m1
     * @param scale
     * @return
     */
    public static List<Integer> scale(Map map, List<Integer> m1 , int scale) {
        int width = map.getWidth();
        int height = map.getHeight();

        double actualWidth = Math.sqrt((m1.size()/(height/width)));
        assert (actualWidth % 1.0d == 0);

        double actualHeight = m1.size()/actualWidth;
        assert (actualHeight % 1.0d == 0);

        double currentScale = width/actualWidth;
        assert currentScale == (height/actualHeight);

        if(scale == currentScale)
            return m1;

        if(scale < currentScale)
            return increase(m1, (int)currentScale-(scale==1 ? 0:scale), (int)actualWidth, (int)actualHeight);
        return decrease(m1, (int)(scale-currentScale), (int)actualWidth, (int)actualHeight);
    }

    private static List<Integer> increase(List<Integer> list, int pow, int w, int h) {

        Integer[] scaled = new Integer[list.size()*pow*pow];

        for(int y=0; y<h; y++) {
            for(int x=0; x<w; x++) {
                for(int rootY = y*pow; rootY<(y*pow)+pow; rootY++) {
                    for(int rootX = x*pow; rootX<(x*pow)+pow; rootX++) {
                        scaled[rootY*h*pow+rootX] = list.get(y*h+x);
                    }
                }
            }
        }
        return Arrays.asList(scaled);
    }


    private static List<Integer> decrease(List<Integer> list, int pow, int w, int h) {
        List<Integer> scaled = new ArrayList<>();
        for(int i=0; i<list.size(); i++) {
            int gap = (int)Math.floor(i/(pow*h));
            if(gap%pow != 0)
                continue;
            if(i%pow != 0)
                continue;
            scaled.add(list.get(i));
        }
        return scaled;
    }

    /**
     * Retruns the logarithm og the number with base 2
     * @param i     number
     * @return      log base 2 of number
     */
    public static double log2n(double i) {
        return Math.log(i)/Math.log(2);
    }

}
