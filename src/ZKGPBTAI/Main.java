package ZKGPBTAI;

import ZKGPBTAI.bt.actions.*;
import ZKGPBTAI.bt.conditions.*;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.RecruitmentManager;
import ZKGPBTAI.gui.DebugView;
import ZKGPBTAI.influence_map.InfluenceManager;
import ZKGPBTAI.military.MilitaryManager;
import bt.BehaviourTree;
import bt.Task;
import bt.utils.TreeInterpreter;
import bt.utils.graphics.LiveBT;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jonatan on 16-Nov-15.
 */
public class Main extends com.springrts.ai.oo.AbstractOOAI {
    OOAICallback callback;
    public List<Manager> managers;
    public DebugView debugView;
    boolean debugActivated = false;
    public EconomyManager economyManager;
    public MilitaryManager militaryManager;
    public InfluenceManager influenceManager;
    public RecruitmentManager recruitmentManager;
    public static Main INSTANCE = new Main();
    public static GameState state = GameState.OFFENSIVE;
    public int teamId;
    Long startTime;
    public BehaviourTree<Main> bt;

    @Override
    public int init(int teamId, OOAICallback callback) {
        this.callback = callback;
        this.teamId = teamId;
        managers = new ArrayList<>();
        //Eco must be called before other managers
        economyManager = new EconomyManager(callback);
        managers.add(economyManager);
        influenceManager = new InfluenceManager();
        managers.add(influenceManager);
        militaryManager = new MilitaryManager();
        managers.add(militaryManager);
        recruitmentManager = new RecruitmentManager();
        managers.add(recruitmentManager);

        startTime = System.nanoTime();

        @SuppressWarnings("unchecked")
        Class<? extends Task>[] c = new Class[]{
                Defensive.class, Offensive.class,
                HasEco.class, HasArmy.class
        };
        Class<? extends Task>[] classes = new Class[]{Defensive.class, Offensive.class, HasEco.class, HasArmy.class};
        Optional<BehaviourTree<Main>> opt = new TreeInterpreter<Main>(this).create(classes, "selector[hasArmy, hasEco, offensive]");
        bt = opt.get();
        LiveBT.startTransmission(bt);
        return 0;
    }

    public OOAICallback getCallback() {
        return callback;
    }

    @Override
    public int update(int frame) {
        for (Manager m : managers) {
            try {
                m.update(frame);

            } catch (Exception e) {
                callback.getGame().sendTextMessage("AAAsAAAAAARGH " + e.getMessage(), 0);
                printException(e);
            }
        }

        if (frame % 10 == 0) {
            if (!debugActivated)
                activateDebug();
            else
                debugView.repaint();

        }

        if (frame % 100 == 0) {
            bt.step();
            LiveBT.draw();
        }
        return 0;
    }

    //when the ai is released AKA when game has ended
    @Override
    public int release(int reason) {
        int time = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        callback.getGame().sendTextMessage("END " + "teamId: " + this.teamId + " time: " + time / 1000 + " Soldiers: " + militaryManager.soldiers.size() + " avgEco: " + economyManager.getAvgEco(), 0);
        return 0;
    }

    private void activateDebug() {
        if (!debugActivated) {
            try {
                debugView = new DebugView(this);
                debugView.setThreatImage(influenceManager.getThreatMap());
                debugView.repaint();
                this.debugActivated = true;
            } catch (Exception e) {
                debug(e);
            }
        }
    }

    private void debug(Exception e) {
        debug(e.getMessage());
        for (StackTraceElement ste : e.getStackTrace()) {
            debug(ste.toString());
        }
    }

    public void debug(String s) {
        callback.getGame().sendTextMessage(s, 0);
    }

    @Override
    public int unitCreated(Unit unit, Unit builder) {
        for (Manager m : managers) {
            try {
                m.unitCreated(unit, builder);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int unitFinished(Unit unit) {
        for (Manager m : managers) {
            try {
                m.unitFinished(unit);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int unitIdle(Unit unit) {
        for (Manager m : managers) {
            try {
                m.unitIdle(unit);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int unitDamaged(Unit unit, Unit attacker, float damage, AIFloat3 dir, WeaponDef weaponDef, boolean paralyzed) {
        for (Manager m : managers) {
            try {
                m.unitDamaged(unit, attacker, damage, dir, weaponDef, paralyzed);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int unitDestroyed(Unit unit, Unit attacker) {
        for (Manager m : managers) {
            try {
                m.unitDestroyed(unit, attacker);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int enemyEnterLOS(Unit enemy) {
        for (Manager m : managers) {
            try {
                m.enemyEnterLOS(enemy);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int enemyLeaveLOS(Unit enemy) {
        for (Manager m : managers) {
            try {
                m.enemyLeaveLOS(enemy);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int enemyEnterRadar(Unit enemy) {
        for (Manager m : managers) {
            try {
                m.enemyEnterRadar(enemy);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int enemyLeaveRadar(Unit enemy) {
        for (Manager m : managers) {
            try {
                m.enemyLeaveRadar(enemy);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int enemyDestroyed(Unit enemy, Unit attacker) {
        for (Manager m : managers) {
            try {
                m.enemyDestroyed(enemy, attacker);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int enemyDamaged(Unit enemy, Unit attacker, float damage, AIFloat3 dir, WeaponDef weaponDef, boolean paralyzed) {
        for (Manager m : managers) {
            try {
                m.enemyDamaged(enemy, attacker, damage, dir, weaponDef, paralyzed);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0;
    }

    @Override
    public int commandFinished(Unit unit, int commandId, int commandTopicId) {
        for (Manager m : managers) {
            try {
                m.commandFinished(unit, commandId, commandTopicId);
            } catch (Exception e) {
                printException(e);
            }
        }
        return 0; // signaling: OK
    }

    public void printException(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
    }

}
