package ZKGPBTAI.military;

import ZKGPBTAI.GameState;
import ZKGPBTAI.Main;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jonatan on 11-Jan-16.
 */
public class SquadHandler {
    MilitaryManager mm;
    public ArrayList<Squad> squads;
    public HashMap<Integer, Fighter> fighters;
    int frame = 0;
    Squad newSquad;
    public HashMap<Integer, Fighter> retreatedFighters;

    public SquadHandler(MilitaryManager mm) {
        this.mm = mm;
        squads = new ArrayList<>();
        fighters = new HashMap<>();
        retreatedFighters = new HashMap<>();
        newSquad = null;
    }

    public void update(int frame, OOAICallback callback) {
        this.frame = frame;

        if (frame % 90 == 0)
            updateSquads(callback);
    }

    public void addFighter(Fighter f) {
        //if already added then return
        if (fighters.containsKey(f.id) && f.squad != null) return;

        if (newSquad == null) {
            newSquad = new Squad(this);
            newSquad.setTarget(getDefenceRally(0), frame, mm);
        }

        newSquad.addUnit(f, frame);
        fighters.put(f.id, f);
    }

    public void addAssault(Fighter f) {
    }

    public void addSiege(Fighter f) {
    }

    public void addAir(Fighter f) {
    }

    public void addShield(Fighter f) {
    }

    public void updateSquads(OOAICallback callback) {
        try {
            cleanUnits();
        } catch (Exception e) {
            callback.getGame().sendTextMessage("MM SH cleanUnits" + e.getMessage(), 0);
        }

/*        try {
            handleRetreat();
        } catch (Exception e) {
            callback.getGame().sendTextMessage("MM SH handleRetreat" + e.getMessage(), 0);
        }*/

        try {
            removeDeadSquads();
        } catch (Exception e) {
            callback.getGame().sendTextMessage("MM SH removeDead" + e.getMessage(), 0);
        }
        try {
            //set Defence rally for forming squads
            if (newSquad != null) {
                newSquad.setTarget(getDefenceRally(0), frame, mm);
                //See if a squad is done forming
                if (newSquad.status == Squad.STATUS.FORMING && newSquad.metalValue > mm.economyManager.effectiveIncome * 60) {
                    newSquad.status = Squad.STATUS.RALLYING;
                    squads.add(newSquad);
                    newSquad = null;
                }
            }
        } catch (Exception e) {
            callback.getGame().sendTextMessage("MM SH setRallyNewSquads" + e.getMessage(), 0);
        }

        //TODO find and fix error in isRallied or setTarget
        try {
            for (int i = 0; i < squads.size(); i++) {
                Squad s = squads.get(i);

                try {
                    //see if a squad is rallied
                    if (s.status == Squad.STATUS.RALLYING && s.isRallied()) {
                        s.status = Squad.STATUS.ATTACKING;
                        if(Main.state == GameState.OFFENSIVE)
                            s.setTarget(getAttackLocation(i), frame, mm);
                        else
                            s.setTarget(getDefenceRally(i), frame, mm);
                    }
                } catch (Exception e) {
                    callback.getGame().sendTextMessage("MM SH see if squad is rallied" + e.getMessage(), 0);
                }

                try {
                    //get new attack location
                    if (s.isRallied()) {
                        s.setTarget(getAttackLocation(i), frame, mm);
                    }
                } catch (Exception e) {
                    callback.getGame().sendTextMessage("MM SH getNewAttackLocation" + e.getMessage(), 0);
                }

            }
        } catch (Exception e) {
            callback.getGame().sendTextMessage("MM SH moveOtherSquads" + e.getMessage(), 0);
        }

    }

    public void handleRetreat() {
        //check if there is any unit healer
        if (mm.caretakers.size() == 0) return;

        //find dying units
        ArrayList<Fighter> toBeRetreated = new ArrayList<>();
        for (Fighter f : fighters.values()) {
            //if health is less than 15%
            if (f.getUnit().getHealth() < (0.15 * f.getUnit().getMaxHealth())) {
                retreatFighter(f);
            }
        }

        //find healed units
        ArrayList<Fighter> toBeReturnedToDuty = new ArrayList<>();
        for (Fighter f : retreatedFighters.values()) {
            if (f.getUnit().getHealth() == f.getUnit().getMaxHealth() || f.getUnit().getHealth() <= 0) {
                toBeReturnedToDuty.add(f);
            }
        }

        //return healed units to duty
        for (Fighter f : toBeReturnedToDuty) {
            retreatedFighters.remove(f.getUnit().getUnitId());
            addFighter(f);
        }

        //retreat dying units
        for (Fighter f : toBeRetreated)
            retreatFighter(f);


        //TODO gives error when calling moveTo
        //move retreated units to nearest caretaker
        for (Fighter f : retreatedFighters.values()) {
            AIFloat3 nearest = mm.caretakers.get(0).getPos();

            float dist = Float.MAX_VALUE;

            for (Unit u : mm.caretakers) {
                float d = Utility.distance(f.getPos(), u.getPos());
                if (d < dist) {
                    dist = d;
                    nearest = u.getPos();
                }
            }
            mm.write("careTaker Pos = " + nearest);
            f.moveTo(nearest);
        }
    }

    public void retreatFighter(Fighter f) {
        removeFighter(f);
        f.squad = null;
        retreatedFighters.put(f.id, f);
    }

    public void removeDeadSquads() {
        ArrayList<Squad> deadSquads = new ArrayList<>();
        for (Squad s : squads) {
            //remove fighters cutoff from their squads and reassign them
            for (Fighter f : s.cutoff()) {
                addFighter(f);
            }

            if (s.isDead()) {
                deadSquads.add(s);
            }
        }
        squads.removeAll(deadSquads);
    }

    void cleanUnits() {
        ArrayList<Integer> invalidFighters = new ArrayList<Integer>();
        for (Fighter f : fighters.values()) {
            if (f.getUnit().getHealth() <= 0) {
                if (f.squad != null) {
                    f.squad.removeUnit(f);
                }
                invalidFighters.add(f.id);
            }
        }
        for (Integer key : invalidFighters) {
            fighters.remove(key);
        }
    }

    public void removeFighter(Fighter f) {
        fighters.remove(f.id);
        for (Squad s : squads) {
            s.removeUnit(f);
        }
    }

    private AIFloat3 getDefenceRally(int squadNumber) {
        if (squadNumber >= 4)
            squadNumber -= 4;
        return mm.influenceManager.im.getNTopLocations(35, mm.influenceManager.im.getTensionMap()).get(squadNumber);
    }

    private AIFloat3 getAttackLocation(int squadNumber) {
        if (squadNumber >= 4)
            squadNumber -= 4;
        return (mm.influenceManager.im.getNTopLocations(5, mm.influenceManager.im.getOpponentInfluence())).get(squadNumber);
    }
}
