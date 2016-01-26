package ZKGPBTAI.military;

import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.OOAI;
import com.springrts.ai.oo.clb.OOAICallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonatan on 16-Dec-15.
 */
public class Squad {
    List<Fighter> fighters;
    public float metalValue;
    public AIFloat3 target;
    MilitaryManager mm;
    SquadHandler sh;

    public enum STATUS {
        FORMING,
        RALLYING,
        ATTACKING
    }

    STATUS status;

    public Squad(SquadHandler squadHandler) {
        this.fighters = new ArrayList<>();
        this.metalValue = 0;
        status = status.FORMING;
        sh = squadHandler;
    }

    public void addUnit(Fighter f, int frame) {
        if (f.unit.getHealth() <= 0) {
            sh.removeFighter(f);
            return;
        }
        fighters.add(f);
        f.squad = this;
        metalValue += f.metalValue;
        f.getUnit().setMoveState(1, (short) 0, frame + 30);
        commandUnits(f, target);
    }

    public void removeUnit(Fighter f) {
        fighters.remove(f);
        metalValue -= f.metalValue;

    }

    public void setTarget(AIFloat3 pos, MilitaryManager mm) {
        // set a target for the squad to attack.
        target = pos;
        this.mm = mm;
        for (Fighter f : fighters) {
            commandUnits(f, pos);

        }
    }

    public void commandUnits(Fighter f, AIFloat3 pos) {
        try {
            f.fightTo(pos, mm);
        } catch (Exception e) {
            //Catch dead units that have managed to be reassigned
            if (f.unit.getHealth() <= 0) {
                sh.removeFighter(f);
            } else {
                //this probably wont happen
                mm.write((f.unit.getHealth() > 0 ? "HANDLED" : "FAIL IN CLASS SQUAD->commandUnits"));
                reassignUnit(f);
            }

        }
    }

    public void reassignUnit(Fighter f) {
        fighters.remove(f);
        f.squad = null;
        sh.addFighter(f);
    }

    public AIFloat3 getPos() {
        if (fighters.size() > 0) {
            int count = fighters.size();
            float x = 0;
            float z = 0;
            for (Fighter f : fighters) {
                x += (f.getPos().x) / count;
                z += (f.getPos().z) / count;
            }
            AIFloat3 pos = new AIFloat3();
            pos.x = x;
            pos.z = z;
            return pos;
        }
        return target;
    }

    public boolean isRallied() {

        AIFloat3 pos = getPos();
        boolean rallied = true;
        for (Fighter f : fighters) {
            commandUnits(f, pos);
            if (Utility.distance(pos, f.getPos()) > 350) {
                rallied = false;
            }
        }
        return rallied;
    }

    public boolean isDead() {
        if (fighters.size() == 0) {
            return true;
        }
        return false;
    }

    public List<Fighter> cutoff() {
        List<Fighter> tooFar = new ArrayList<Fighter>();
        AIFloat3 pos = getPos();
        for (Fighter f : fighters) {
            if (Utility.distance(pos, f.getPos()) > 1000 && Utility.distance(target, f.getPos()) > 1000) {
                tooFar.add(f);
                f.squad = null;
            }
        }
        fighters.removeAll(tooFar);
        if (fighters.size() < 4 && metalValue < 1000) {
            tooFar.addAll(fighters);
            fighters.clear();
        }
        return tooFar;
    }


}
