package ZKGPBTAI.utils;

import com.springrts.ai.oo.AIFloat3;

/**
 * Created by Jonatan on 14-Jan-16.
 */
public final class Utility {

    public static final float distance(AIFloat3 pos1, AIFloat3 pos2) {
        float x1 = pos1.x;
        float z1 = pos1.z;
        float x2 = pos2.x;
        float z2 = pos2.z;
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
    }
}
