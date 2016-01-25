package ZKGPBTAI.influence_map;

import ZKGPBTAI.military.Enemy;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Jonatan on 20-Nov-15.
 */
public class InfluenceMap {
    float[][] myInfluence;
    float[][] opponentInfluence;
    float[][] influenceMap;
    float[][] tensionMap;
    float[][] vulnerabilityMap;
    float[][] modifiedVulnerabilityMap;
    public int width, height;
    public static final int GRANULARITY = 10;
    public static final int POS_CONVERSION_RATIO = 8;
    public static final int CONVERT_TO_MAP_POS_VALUE = GRANULARITY * POS_CONVERSION_RATIO;
    InfluenceManager influenceManager;

    public InfluenceMap(InfluenceManager im) {
        this.influenceManager = im;
        width = im.callback.getMap().getWidth() / GRANULARITY;
        height = im.callback.getMap().getHeight() / GRANULARITY;
        myInfluence = new float[this.width][this.height];
        opponentInfluence = new float[this.width][this.height];
        vulnerabilityMap = new float[this.width][this.height];
        tensionMap = new float[this.width][this.height];
        modifiedVulnerabilityMap = new float[this.width][this.height];
        influenceMap = new float[this.width][this.height];
    }

    public void calculateInfluenceMap() {
        float[][] influenceMap = new float[this.width][this.height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                influenceMap[i][j] = myInfluence[i][j] - opponentInfluence[i][j];
            }
        }
        this.influenceMap = influenceMap;
    }

    public float[][] getMyInfluence() {
        return myInfluence;
    }

    public float[][] getInfluenceMap() {
        return influenceMap;
    }

    public float[][] getOpponentInfluence() {
        return opponentInfluence;
    }

    public float[][] getTensionMap() {
        return tensionMap;
    }

    public float[][] getVulnerabilityMap() {
        return vulnerabilityMap;
    }

    public float[][] getModifiedVulnerabilityMap() {
        return modifiedVulnerabilityMap;
    }

    public void calculateTensionMap() {
        float[][] tensionMap = new float[this.width][this.height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tensionMap[i][j] = myInfluence[i][j] + opponentInfluence[i][j];
            }
        }
        this.tensionMap = tensionMap;
    }

    //Calculated as Tension map -Abs(Influence map)
    public void calculateVulnerabilityMap() {
        float[][] im = getInfluenceMap();
        float[][] vulnerabilityMap = getTensionMap();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                vulnerabilityMap[i][j] -= Math.abs(im[i][j]);
            }
        }
        this.vulnerabilityMap = vulnerabilityMap;
    }

    //Tension map +Influence map
    //Gives high values in areas of conflict where we are strong and low where we are weak
    public void calculateModifiedVulnerabilityMap() {
        float[][] im = getInfluenceMap();
        float[][] modifiedVulnerabilityMap = getTensionMap();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                modifiedVulnerabilityMap[i][j] += im[i][j];
            }
        }
        this.modifiedVulnerabilityMap = modifiedVulnerabilityMap;
    }

    public void propagate(float grid[][], int center_x, int center_z, float falloff, float influence, float fullInfluenceArea) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                int dx = Math.abs(x - center_x);
                int dz = Math.abs(z - center_z);

                if (dx > falloff)
                    continue;
                if (dz > falloff)
                    continue;

                if (x == center_x && z == center_z)
                    continue;
                if (dx + dz <= fullInfluenceArea) {
                    grid[x][z] += influence;
                } else if (dx + dz <= falloff) {
                    grid[x][z] += (float) (influence * Math.pow(0.75f, dx + dz - fullInfluenceArea));

                } else if (dx * dx + dz * dz <= falloff * falloff) {

                    grid[x][z] += (float) (influence * Math.pow(0.75f, dx + dz - fullInfluenceArea));
                } else
                    continue;

                if (grid[x][z] > 10000)
                    grid[x][z] = 10000;
            }
        }

    }

    public void updateInfluence() {
        String progress = "none";
        try {
            for (Unit u : influenceManager.callback.getFriendlyUnits()) {
                int x = ((int) Math.floor(u.getPos().x / POS_CONVERSION_RATIO)) / GRANULARITY;
                int z = ((int) Math.floor(u.getPos().z / POS_CONVERSION_RATIO)) / GRANULARITY;
                myInfluence[x][z] += u.getPower();
                propagate(myInfluence, x, z, getMovementDissipationArea(u), u.getPower() * 2, getFullInfluenceArea(u));
            }
            progress = "friendly";

            //TODO occasionly errors here
            for (Enemy e : influenceManager.militaryManager.getEnemies().values()) {
                int x = ((int) Math.floor(e.getPos().x / POS_CONVERSION_RATIO)) / GRANULARITY;
                int z = ((int) Math.floor(e.getPos().z / POS_CONVERSION_RATIO)) / GRANULARITY;
                if (e.isIdentified()) {
                    opponentInfluence[x][z] += e.unit.getPower();
                    propagate(opponentInfluence, x, z, getMovementDissipationArea(e.unit), e.unit.getPower(), getFullInfluenceArea(e.unit));
                } else {
                    opponentInfluence[x][z] += e.getPower();
                    propagate(opponentInfluence, x, z, (e.getSpeed() == 0) ? 5 : e.getSpeed() * 5, e.getPower(), e.getThreatRadius() / CONVERT_TO_MAP_POS_VALUE);
                }
            }
            progress = "enemy";

            calculateInfluenceMap();
            calculateTensionMap();
            calculateVulnerabilityMap();
            calculateModifiedVulnerabilityMap();
        } catch(Exception e){
            influenceManager.write("IM " + progress);
        }
    }

    public void fadeInfluence() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                myInfluence[i][j] -= myInfluence[i][j] * 0.01;
                opponentInfluence[i][j] -= opponentInfluence[i][j] * 0.01;
            }
        }
    }

    public float getFullInfluenceArea(Unit u) {
        try {
            return u.getMaxRange() / CONVERT_TO_MAP_POS_VALUE;
        } catch (Exception e) {
            return 1f;
        }
    }

    public float getMovementDissipationArea(Unit u) {
        return (u.getMaxSpeed() == 0) ? 5 : u.getMaxSpeed() * 5;
    }

    //http://gameschoolgems.blogspot.no/2009/12/influence-maps-i.html

    public ArrayList<AIFloat3> getNTopLocations(int n, float[][] grid) {
        ArrayList<AIFloat3> locations = new ArrayList<>(n);
        float[][] gridCopy = grid.clone();
        for (int i = 0; i < n; i++) {
            int[] top = getTopLocation(gridCopy);
            locations.add(new AIFloat3(top[0] * CONVERT_TO_MAP_POS_VALUE, 0, top[1] * CONVERT_TO_MAP_POS_VALUE));
            gridCopy[top[0]][top[1]] = 0;
        }
        return locations;
    }

    int[] getTopLocation(float[][] grid) {
        float max = Float.MIN_VALUE;
        int iIndex = 0;
        int jIndex = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (grid[i][j] > max) {
                    max = grid[i][j];
                    iIndex = i;
                    jIndex = j;
                }
            }
        }
        int[] answer = new int[]{iIndex, jIndex};
        return answer;
    }

    public ArrayList<AIFloat3> getNBottomLocations(int n, float[][] grid) {
        ArrayList<AIFloat3> locations = new ArrayList<>(n);
        float[][] gridCopy = grid.clone();
        for (int i = 0; i < n; i++) {
            int[] top = getBottomLocation(gridCopy);
            locations.add(new AIFloat3(top[0] * CONVERT_TO_MAP_POS_VALUE, 0, top[1] * CONVERT_TO_MAP_POS_VALUE));
            gridCopy[top[0]][top[1]] = Float.MAX_VALUE;
        }
        return locations;
    }

    int[] getBottomLocation(float[][] grid) {
        float max = Float.MAX_VALUE;
        int iIndex = 0;
        int jIndex = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (grid[i][j] < max) {
                    max = grid[i][j];
                    iIndex = i;
                    jIndex = j;
                }
            }
        }
        int[] answer = new int[]{iIndex, jIndex};
        return answer;
    }
}
