package ZKGPBTAI.military;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 16-Dec-15.
 */
public class Fighter {
    public float metalValue;
    public int id;
    public Squad squad;
    protected Unit unit;

    public Fighter(Unit u, float metal){
        this.unit = u;
        this.id = u.getUnitId();
        this.metalValue = metal;
    }

    public Unit getUnit(){
        return unit;
    }

    public AIFloat3 getPos(){
        return unit.getPos();
    }

    public void fightTo(AIFloat3 pos, OOAICallback cb){
        AIFloat3 target = getRadialPoint(pos, 200f);
        unit.fight(target, (short) 0, 6000);
    }

    public void moveTo(AIFloat3 pos)
    {
        AIFloat3 target = getRadialPoint(pos, 200f);
        unit.fight(target, (short) 0, 6000);
    }

    public static AIFloat3 getRadialPoint(AIFloat3 position, Float radius){
        // returns a random point lying on a circle around the given position.
        AIFloat3 pos = new AIFloat3();
        double angle = Math.random()*2*Math.PI;
        double vx = Math.cos(angle);
        double vz = Math.sin(angle);
        pos.x = (float) (position.x + radius*vx);
        pos.z = (float) (position.z + radius*vz);
        return pos;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Fighter){
            Fighter f = (Fighter) o;
            return (f.id == id);
        }
        return false;
    }
}
