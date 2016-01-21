package ZKGPBTAI.military;

import com.springrts.ai.oo.clb.UnitDef;
import com.springrts.ai.oo.clb.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonatan on 15-Dec-15.
 */
public class RadarDef {
    float averageSpeed = 0;
    float averagePower = 0;
    float averageRange = 0;
    float averageDanger = 0;
    ArrayList<UnitDef> unitDefs;

    public RadarDef(List<UnitDef> defs){
        this.unitDefs = (ArrayList<UnitDef>) defs;

        float totalSpeed = 0;
        float totalRange = 0;
        float totalDanger = 0;
        float totalPower = 0;

        for(UnitDef ud:unitDefs){
            totalSpeed += ud.getSpeed();
            totalDanger += Math.sqrt(ud.getPower() * Math.min(1, ud.getWeaponMounts().size()));
            totalRange += ud.getMaxWeaponRange();
            totalPower += ud.getPower();
        }

        int udSize = unitDefs.size();
        averageSpeed = totalSpeed / udSize;
        averageRange = totalRange / udSize;
        averageRange = totalDanger / udSize;
        averagePower = (totalPower / udSize)/2;
    }

    public float getDanger(){
        return this.averageDanger;
    }

    public float getSpeed(){
        return this.averageSpeed;
    }

    public float getRange(){
        return this.averageRange;
    }

    public float getPower(){ return this.averagePower;}
}
