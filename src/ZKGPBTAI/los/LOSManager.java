package ZKGPBTAI.los;

import ZKGPBTAI.Manager;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonatan on 27-Jan-16.
 */
public class LOSManager extends Manager {

    List<Integer> losMap;
    List<Integer> recentLosMap;
    List<Integer> radarMap;
    List<Integer> recentRadarMap;
    private int mapWidth;
    private int mapHeight;
    private int losResolution;
    private int gridWidth;
    private int gridHeight;
    private int losGridSize;
    private Map map;

    @Override
    public String getModuleName() {
        return "LOSManager";
    }

    public LOSManager() {
        this.map = callback.getMap();
        this.losMap = map.getLosMap();
        this.radarMap = map.getRadarMap();
        recentLosMap = losMap;
        recentRadarMap = radarMap;
        this.mapHeight = map.getHeight();
        this.mapWidth = map.getWidth();
        this.losResolution = callback.getMod().getLosMipLevel();
        this.losGridSize = (int) Math.pow((double) 2, (double) losResolution);
        this.losMap = map.getLosMap();
        this.gridWidth = mapWidth / losGridSize;
        this.gridHeight = mapHeight / losGridSize;
        setLosManager(this);
    }

    @Override
    public int update(int frame) {
        this.frame = frame;

        if (frame % 10 == 0) {
            this.losMap = callback.getMap().getLosMap();
            this.radarMap = callback.getMap().getRadarMap();
            updateRecent();
        }

        if (frame % 10000 == 0) {
            recentLosMap = losMap;
            recentRadarMap = radarMap;
        }

        return 0;
    }

    public void updateRecent() {
        for (int i = 0; i < recentLosMap.size(); i++) {
            recentLosMap.set(i, recentLosMap.get(i) + losMap.get(i));
        }
        for (int i = 0; i < recentRadarMap.size(); i++) {
            recentRadarMap.set(i, recentRadarMap.get(i) + radarMap.get(i));
        }
    }

    public boolean isInRadar(AIFloat3 pos, boolean recent) {
        //the value for the full resolution position (x, z) is at index ((z * width + x) / res) -
        //the last value, bottom right, is at index (width/res * height/res - 1)

        // convert from world coordinates to heightmap coordinates
        double x = (int) Math.floor(pos.x / 8);
        double z = (int) Math.floor(pos.z / 8);

        int gridX = (int) Math.floor((x / mapWidth) * gridWidth);
        int gridZ = (int) Math.floor((z / mapHeight) * gridHeight);

        int index = Math.min(gridX + gridZ * gridWidth, radarMap.size() - 1);

        if (index >= losMap.size()) {
            return false;
        }
        if (recent)
            return (recentRadarMap.get(index) > 0);
        else
            return (radarMap.get(index) > 0);
    }

    public boolean isInLOS(AIFloat3 pos, boolean recent) {
        //the value for the full resolution position (x, z) is at index ((z * width + x) / res) -
        //the last value, bottom right, is at index (width/res * height/res - 1)

        // convert from world coordinates to heightmap coordinates
        double x = (int) Math.floor(pos.x / 8);
        double z = (int) Math.floor(pos.z / 8);

        int gridX = (int) Math.floor((x / mapWidth) * gridWidth);
        int gridZ = (int) Math.floor((z / mapHeight) * gridHeight);

        int index = Math.min(gridX + gridZ * gridWidth, losMap.size() - 1);

        if (index >= losMap.size()) {
            return false;
        }
        if (recent)
            return (recentLosMap.get(index) > 0);
        else
            return (losMap.get(index) > 0);
    }

    public boolean isObserved(AIFloat3 pos, boolean recent) {
        if (isInRadar(pos, recent))
            return true;
        return isInLOS(pos, recent);
    }

    //returns metal spots that have not been observed using radar or LOS in the last 10000 frames
    public ArrayList<AIFloat3> getUnobservedMetalPositions() {
        ArrayList<AIFloat3> unobserved = new ArrayList<>();
        for (AIFloat3 pos : economyManager.availablemetalspots) {
            if (!isObserved(pos, true))
                unobserved.add(pos);
        }
        return unobserved;
    }
}
