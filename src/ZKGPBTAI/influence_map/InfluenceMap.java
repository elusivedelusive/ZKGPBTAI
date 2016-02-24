package ZKGPBTAI.influence_map;

import ZKGPBTAI.military.Enemy;
import ZKGPBTAI.utils.Utility;
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
    public static final int MAX_INFLUENCE = 10000;
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

                if (grid[x][z] > MAX_INFLUENCE)
                    grid[x][z] = MAX_INFLUENCE;
            }
        }

    }

    //Will occasionlly throw a friendly or enemy math exception at the end of the game
    public void updateInfluence() {
        String progress = "none";
        try {
            for (Unit u : influenceManager.friendlyUnits) {

                progress = "friendly";
                int x = convertToIMCoordinate(u.getPos().x);
                int z = convertToIMCoordinate(u.getPos().z);
                progress += " math";
                try {
                    myInfluence[x][z] += u.getPower();
                    progress += " power";
                    propagate(myInfluence, x, z, getMovementDissipationArea(u), u.getPower(), getFullInfluenceArea(u));
                    progress += " propagate";
                } catch (NullPointerException noPower) {
                    influenceManager.write("FM handled");
                    myInfluence[x][z] += u.getHealth();
                    progress += " power";
                    propagate(myInfluence, x, z, getMovementDissipationArea(u), u.getHealth(), getFullInfluenceArea(u));
                    progress += " propagate";
                } catch (Exception deadUnit) {
                    influenceManager.write("friendly math continued: " + deadUnit.getLocalizedMessage());
                    continue;
                }

            }

            for (Enemy e : influenceManager.militaryManager.getEnemies().values()) {
                progress = "enemy";
                int x = convertToIMCoordinate(e.getPos().x);
                int z = convertToIMCoordinate(e.getPos().z);
                progress += " math";
                try {
                    if (e.isIdentified()) {
                        opponentInfluence[x][z] += e.unit.getPower();
                        progress += " power1";
                        propagate(opponentInfluence, x, z, getMovementDissipationArea(e.unit), e.unit.getPower(), getFullInfluenceArea(e.unit));
                        progress += " propagate1";
                    } else {
                        opponentInfluence[x][z] += e.getPower() / 15;
                        progress += " power2";
                        propagate(opponentInfluence, x, z, (e.getSpeed() == 0) ? 5 : e.getSpeed() * 5, e.getPower() / 15, e.getThreatRadius() / CONVERT_TO_MAP_POS_VALUE);
                        progress += " propagate2";
                    }
                } catch (Exception enemyMath) {
                    influenceManager.write("enemy math continued: " + enemyMath.getLocalizedMessage());
                    continue;
                }

            }


            calculateInfluenceMap();
            calculateTensionMap();
            //calculateVulnerabilityMap();
            //calculateModifiedVulnerabilityMap();
        } catch (Exception e) {
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

    public float getAvgScoreFromTopNLocations(int n, float[][] grid) {
        ArrayList<Float> scores = new ArrayList<>(n);
        float[][] gridCopy = grid.clone();
        for (int i = 0; i < n; i++) {
            int[] top = getTopLocation(gridCopy);
            scores.add(grid[top[0]][top[1]]);
            gridCopy[top[0]][top[1]] = 0;
        }

        int sum = 0;
        for (float i : scores)
            sum += i;
        return sum / scores.size();
    }

    //used to convert from unit coordinates to the coordinates used in the influence map
    //needs to be done for pos x and pos z of an AIFloat3
    private int convertToIMCoordinate(float pos) {
        return ((int) Math.floor(pos / POS_CONVERSION_RATIO)) / GRANULARITY;
    }

    //returns a list of the n highest locations in a given 2d array
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

    //used by getNTopLocations
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

    //returns a list of the n lowest locations in a given 2d array
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

    //used by getNBottompLocations
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

    //Get position on friendly or enemy side of tension
    public AIFloat3 getBorderLinePos(boolean friendlySide){
        if(friendlySide)
            return getArrayDirection(getNTopLocations(1, tensionMap).get(0), 10, true, influenceMap);
        else
            return getArrayDirection(getNTopLocations(1, tensionMap).get(0), 10, false, influenceMap);
    }

    //getNearestSafeHaven
    public AIFloat3 getNearestSafeHaven(AIFloat3 pos){
        float smallestDist = Float.MAX_VALUE;
        AIFloat3 nearest = new AIFloat3();
        for(AIFloat3 p2:getNTopLocations(3,influenceMap)){
            float dist = Utility.distance(pos, p2);
            if(dist < smallestDist) {
                smallestDist = dist;
                nearest = p2;
            }
        }
        return nearest;
    }

    //returns true if tension is higher than 50%
    public boolean HighTension(AIFloat3 pos){
        if(tensionMap[convertToIMCoordinate(pos.x)][convertToIMCoordinate(pos.z)] > (MAX_INFLUENCE/2))
            return true;
        return false;
    }

    //
    public AIFloat3 getArrayDirection(AIFloat3 pos, int radius, boolean ascending, float[][] grid) {
        //all 8 directions in a 2d array
        int[] xd = {-1, -1, 0, +1, +1, +1, 0, -1};
        int[] yd = {0, -1, -1, -1, 0, +1, +1, +1};

        //best so far
        float bestDiff;
        if (ascending)
            bestDiff = Float.MIN_VALUE;
        else
            bestDiff = Float.MAX_VALUE;
        AIFloat3 bestDir = null;

        //x and y converted
        int originX = convertToIMCoordinate(pos.x);
        int originY = convertToIMCoordinate(pos.z);

        //for all directions
        for (int d = 0; d < 8; d++) {
            //check that we are within bounds

            //check that we are within bounds
            if (((originX + (xd[d] * radius)) > width) || ((originX + (xd[d] * radius)) < 0)
                    || ((originY + (yd[d] * radius)) > height) || ((originY + (yd[d] * radius)) < 0)) {
                continue;
            }

            //avg difference in one direction
            float diff = 0;
            float lastDir = grid[originX][originY];
            //check in a direction for the length of the radius
            for (int r = 1; r <= radius; r++) {

                float dir = grid[originX + (xd[d] * r)][originY + (yd[d] * r)];
                diff += lastDir - dir;
                lastDir = dir;
            }


            if (ascending) {
                if (diff > bestDiff) {

                    bestDiff = diff;
                    bestDir = new AIFloat3((originX + (xd[d] * radius)) * CONVERT_TO_MAP_POS_VALUE, 0, (originY + (yd[d] * radius)) * CONVERT_TO_MAP_POS_VALUE);
                }
            } else {
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestDir = new AIFloat3((originX + (xd[d] * radius)) * CONVERT_TO_MAP_POS_VALUE, 0, (originY + (yd[d] * radius)) * CONVERT_TO_MAP_POS_VALUE);
                }
            }
        }
        influenceManager.write("BESTDIR - " + bestDir);
        return bestDir;
    }
}
