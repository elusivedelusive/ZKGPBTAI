package ZKGPBTAI.military;

import ZKGPBTAI.Main;
import ZKGPBTAI.Manager;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jonatan on 30-Nov-15.
 */
public class MilitaryManager extends Manager {

    public List<Unit> soldiers;
    HashMap<Integer, Enemy> enemies;
    HashMap<Integer, Fighter> fighters;
    ArrayList<Unit> caretakers;
    public ArrayList<Squad> squads;
    public Squad nextSquad;
    public static RadarDef radarDef;

    public SquadHandler squadHandler;

    public MilitaryManager() {
        soldiers = new ArrayList<>();
        enemies = new HashMap<>();
        fighters = new HashMap<>();
        caretakers = new ArrayList<>();
        radarDef = new RadarDef(callback.getUnitDefs());
        nextSquad = null;
        squads = new ArrayList<>();

        squadHandler = new SquadHandler(this);
        setMilitaryManager(this);
    }

    //TODO add scouts that are Glaive/light raider bots that scout metal spots
    //TODO Ensure balanced squads
    //TODO FIX occasional "MilitaryManager null" call
    @Override
    public String getModuleName() {
        return "MilitaryManager";
    }

    @Override
    public int update(int frame) {
        this.frame = frame;
        try {
            squadHandler.update(frame, callback);
        } catch (Exception e) {
            write(getModuleName() + " " + e.getMessage());
        }

        try {
            if (frame % 200 == 0) {
                this.caretakers = economyManager.caretakers;
            }
        } catch (Exception e) {
            write("Caretaker call " + e.getMessage());
        }

        if(frame%1000 == 0){
            write(getVisibleEnemies().size() + " Identified enemies");
        }
        return 0;
    }

    @Override
    public int unitFinished(Unit u) {
        try {
            String name = u.getDef().getHumanName();
            if (name.equals("Glaive")
                    || name.equals("Scythe")
                    || name.equals("Rocko")
                    || name.equals("Warrior")
                    || name.equals("Zeus")
                    || name.equals("Hammer")
                    || name.equals("Spectre")
                    || name.equals("Gremlin")
                    || name.equals("Tick")
                    //hovercraft
                    || name.equals("Quill")
                    || name.equals("Dagger")
                    || name.equals("Scalpel")
                    || name.equals("Halberd")
                    || name.equals("Claymore")
                    || name.equals("Mace")
                    || name.equals("Penetratot")
                    || name.equals("Flail")) {
                soldiers.add(u);
                Fighter f = new Fighter(u, u.getDef().getCost(m));
                fighters.put(f.id, f);
                squadHandler.addFighter(f);
            }
        } catch (Exception e) {
            write("MM unitFinished" + e.getMessage());
        }
        return 0;
    }

    @Override
    public int unitDestroyed(Unit unit, Unit attacker) {
        try {
            for (Unit u : soldiers) {
                if (u.getUnitId() == unit.getUnitId()) {
                    soldiers.remove(unit);
                    squadHandler.removeFighter(fighters.get(unit.getUnitId()));
                    break;
                }
            }
        } catch (Exception e) {
            write("MM unitDestroyed" + e.getMessage());
        }
        return 0;
    }

    @Override
    public int enemyEnterLOS(Unit u) {
        try {
            if (enemies.containsKey(u.getUnitId())) {
                enemies.get(u.getUnitId()).updateFromUnitDef(u.getDef());
                enemies.get(u.getUnitId()).visible = true;
            } else {
                Enemy e = new Enemy(u);
                e.visible = true;
                enemies.put(u.getUnitId(), e);
            }
        } catch (Exception e) {
            write("MM enemyEnterLOS" + e.getMessage());
        }
        return 0;
    }

    @Override
    public int enemyLeaveLOS(Unit u) {
        try {
            if (enemies.containsKey(u.getUnitId())) {
                enemies.get(u.getUnitId()).visible = false;
            }
        } catch (Exception e) {
            write("MM EneemyLeaveLOS" + e.getMessage());
        }
        return 0;
    }

    @Override
    public int enemyEnterRadar(Unit u) {
        try {
            if (enemies.containsKey(u.getUnitId())) {
                enemies.get(u.getUnitId()).isRadarVisible = true;
            } else {
                if (u.getDef() != null) {
                    Enemy e = new Enemy(u);
                    e.visible = true;
                    e.isRadarVisible = true;
                    enemies.put(u.getUnitId(), e);
                } else {
                    Enemy e = new Enemy(u);
                    enemies.put(u.getUnitId(), e);
                    e.isRadarVisible = true;
                }
            }
        } catch (Exception e) {
            write("MM enemyEnterRadar" + e.getMessage());
        }

        return 0;
    }

    @Override
    public int enemyLeaveRadar(Unit u) {
        try {
            if (enemies.containsKey(u.getUnitId())) {
                if (enemies.get(u.getUnitId()).isDefensive || enemies.get(u.getUnitId()).isStatic) {
                    enemies.get(u.getUnitId()).visible = false;
                } else {
                    enemies.remove(u.getUnitId());
                }
            }
        } catch (Exception e) {
            write("MM enemeyLeaveRadar" + e.getMessage());
        }
        return 0;
    }

    @Override
    public int enemyDestroyed(Unit u, Unit attacker) {
        try {
            if (enemies.containsKey(u.getUnitId())) {
                enemies.remove(u.getUnitId());
            }
        } catch (Exception e) {
            write("MM enemyDestoyed" + e.getMessage());
        }
        return 0;
    }

    public void removeDeadIdentifiedEnemies(){
        ArrayList<Integer> deadEnemies = new ArrayList<Integer>();
        for (Enemy e : enemies.values()) {
            if (e.isIdentified() && e.unit.getHealth() <= 0) {
                deadEnemies.add(e.unitId);
            }
        }
        for (Integer key : deadEnemies) {
            enemies.remove(key);
        }
    }

    public HashMap<Integer, Enemy> getEnemies() {
        removeDeadIdentifiedEnemies();
        return enemies;
    }

    public HashMap<Integer, Enemy> getVisibleEnemies() {
        HashMap<Integer, Enemy> visibleEnemies = new HashMap<>();
        for (Enemy e : militaryManager.getEnemies().values()) {
            if (e.isIdentified()) visibleEnemies.put(e.unitId, e);
        }
        return visibleEnemies;
    }
}
