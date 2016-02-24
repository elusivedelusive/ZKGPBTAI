package ZKGPBTAI.military;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;

/**
 * Created by Jonatan on 15-Dec-15.
 */
public class Enemy {
    public Unit unit;
    public UnitDef def;
    int unitId;
    AIFloat3 pos;
    public boolean visible = false;
    public boolean isStatic = false;
    public boolean isRadarOnly = true;
    public boolean isRadarVisible = false;
    public boolean identified = false;
    public boolean isDefensive = false;
    float power;
    float speed;
    float threatRadius;

    public Enemy(Unit unit) {
        this.unit = unit;
        this.unitId = unit.getUnitId();
        this.pos = unit.getPos();

        UnitDef d = unit.getDef();

        if (d != null) {
            updateFromUnitDef(d);
            this.def = d;
        } else {
            this.pos = unit.getPos();
            this.isStatic = false;
            updateFromRadarDef(MilitaryManager.radarDef);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Enemy) {
            return unitId == ((Enemy) other).unitId;
        }
        return false;
    }

    void setIdentified() {
        this.identified = true;
    }

    public boolean isIdentified() {
        return this.identified;
    }

    public void updateFromUnitDef(UnitDef def) {
        setIdentified();
        this.isStatic = (unit.getMaxSpeed() == 0);
        this.def = def;
        this.power = def.getPower();
        if(def.getWeaponMounts().size() > 0)
            this.threatRadius = def.getMaxWeaponRange();
        this.speed = def.getSpeed();
        this.isDefensive = isDefensive();
    }

    public void updateFromRadarDef(RadarDef rd){
        this.isStatic = (rd.getSpeed() ==0);
        this.speed = rd.getSpeed();
        this.threatRadius = rd.getRange();
        this.isDefensive = isDefensive();
        this.power = rd.getPower();
    }

    public boolean isDefensive(){
        if(isStatic && threatRadius > 0)
            return true;
        return false;
    }

    public AIFloat3 getPos(){
        return pos;
    }
    public float getPower(){ return power;}
    public float getSpeed(){ return speed;}
    public float getThreatRadius(){ return threatRadius;}
}
