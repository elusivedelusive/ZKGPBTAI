package ZKGPBTAI.utils;

import ZKGPBTAI.influence_map.InfluenceManager;
import ZKGPBTAI.influence_map.InfluenceMap;
import ZKGPBTAI.military.Enemy;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jonatan on 14-Jan-16.
 */
public final class Utility {

    public static float distance(AIFloat3 pos1, AIFloat3 pos2) {
        float x1 = pos1.x;
        float z1 = pos1.z;
        float x2 = pos2.x;
        float z2 = pos2.z;
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
    }

    @Deprecated
    public static boolean inRadarRange(OOAICallback callback, Unit u) {
        List<Integer> radar = MapHandler.scale(callback.getMap(), callback.getMap().getRadarMap(), 1);
        int pos = (int) (u.getPos().z * (callback.getMap().getWidth() / 8) + u.getPos().x);
        return (radar.get(pos) > 0);
    }

    //getNearestSafeHaven
    public static AIFloat3 getNearestSafeHaven(InfluenceMap im, AIFloat3 pos) {
        float smallestDist = Float.MAX_VALUE;
        AIFloat3 nearest = new AIFloat3();
        for (AIFloat3 p2 : im.getNTopLocations(3, im.getInfluenceMap())) {
            float dist = distance(pos, p2);
            if (dist < smallestDist) {
                smallestDist = dist;
                nearest = p2;
            }
        }
        return nearest;
    }

    public static boolean isAreaControlled(AIFloat3 pos, ArrayList<Unit> turrets, HashMap<Integer, Enemy> enemies) {
        int turretCount = 0;
        for (Unit turret : turrets) {
            if (distance(pos, turret.getPos()) < turret.getMaxRange())
                turretCount++;
        }

        if (turretCount > 1) {
            for (Enemy e : enemies.values()) {
                if (distance(pos, e.getPos()) < 250)
                    return false;
            }
            return true;
        }
        return false;
    }

}
