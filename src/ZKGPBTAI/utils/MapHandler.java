package ZKGPBTAI.utils;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hallvard on 24.02.2016.
 */
public class MapHandler {

    /**
     * Returns the angle between the two coordinates in degrees
     * @param from  From position
     * @param to    To position
     * @return      angle in degrees.
     */
    public static double angleDegrees(AIFloat3 from, AIFloat3 to) {
        double deltaX = to.x - from.x;
        double deltaZ = to.z - from.z;

        return Math.atan2(deltaZ, deltaX)*180/Math.PI;
    }

    /**
     * Get a new point in a certain distance and angle from the current point.
     * @param start     starting position
     * @param degrees   angle of movement
     * @param distance  distance of movement
     * @return          New AIFloat3 coordinate mathing the description (Y will be 0)
     */
    public static AIFloat3 getPoint(AIFloat3 start, double degrees, double distance) {
        final double radians = Math.toRadians(degrees);
        final double co = Math.cos(radians)*distance;
        final double si = Math.sin(radians)*distance;

        // By adding the cosine to X and sine to Z (Y) of the current coordinates
        // you'll get the new position.
        return new AIFloat3((float)(start.x+co), 0.0f, (float)(start.z+si));
    }

    /**
     *  WIDTH/HEIGHT    : 1
     *  HEIGHTMAP       : 1
     *
     *  RADAR           : 8
     *  LOS             : 16
     *
//     * @param map
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

        int currentScale = (int)(width/actualWidth);
        assert currentScale == (int)(height/actualHeight);

        if(scale == currentScale)
            return m1;

        if(scale < currentScale)
            return increase(m1, (currentScale/scale), (int)actualWidth, (int)actualHeight);
        return decrease(m1, (scale/currentScale), (int)actualWidth, (int)actualHeight);
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
            int gap = (int)Math.floor(i/(h));
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
