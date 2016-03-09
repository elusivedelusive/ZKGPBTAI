package ZKGPBTAI;

import ZKGPBTAI.bt.actions.*;
import ZKGPBTAI.bt.actions.movement.MoveToMapCentre;
import ZKGPBTAI.bt.actions.movement.MoveToRandom;
import ZKGPBTAI.bt.actions.movement.MoveToSafe;
import ZKGPBTAI.bt.actions.movement.MoveToTension;
import ZKGPBTAI.bt.actions.worker.*;
import ZKGPBTAI.bt.conditions.*;
import ZKGPBTAI.bt.conditions.economy.HighEnergy;
import ZKGPBTAI.bt.conditions.economy.HighMetal;
import ZKGPBTAI.bt.conditions.economy.LowEnergy;
import ZKGPBTAI.bt.conditions.economy.LowMetal;
import ZKGPBTAI.bt.conditions.other.*;
import ZKGPBTAI.economy.EconomyManager;
import ZKGPBTAI.economy.RecruitmentManager;
import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.gui.DebugView;
import ZKGPBTAI.influence_map.InfluenceManager;
import ZKGPBTAI.los.LOSManager;
import ZKGPBTAI.military.MilitaryManager;
import bt.*;
import bt.utils.TreeInterpreter;
import bt.utils.graphics.LiveBT;
import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    public LOSManager losManager;
    public static Main INSTANCE = new Main();
    public static GameState state = GameState.OFFENSIVE;
    public int teamId;
    Long startTime;
    String jonatanTree = "selector[selector[sequence[lowMetal, buildMex], sequence[lowEnergy, buildSolar, buildLotus]], sequence[highEnergy, highMetal, buildStorage, buildFactory],sequence[inverter(inRadarRange),buildRadar, buildGauss]]";
    String bestInd = "inverter(sequence[succeeder(inverter(buildMex)),succeeder(untilFail(buildSolar)),failer(untilSucceed(highEnergy)),buildSolar])";
    String tensionTester = "inverter(sequence[moveToTension, buildLotus,buildSolar])";
    String inRadarRangeTester = "inverter(sequence[inverter(inRadarRange), buildRadar, moveToRandom])";
    String topOfHillTest = "sequence(moveToRandom, topOfHill, buildRadar)";
    //determines if the bot will look for a bt tree or not
    //BT
    public boolean runningBT = true;
    private final HashMap<BehaviourTree<Main>, Worker> trees = new HashMap<>();
    ExecutorService executorService;
    Runnable btRunner;
    Optional<BehaviourTree<Main>> opt;
    String inputTree = "";

    @SuppressWarnings("unchecked")
    public static Class<? extends Task>[] classes = new Class[]{BuildFactory.class, BuildGauss.class, BuildLotus.class, BuildMex.class, BuildRadar.class, BuildSolar.class,
            BuildStorage.class, HighEnergy.class, LowEnergy.class, HighMetal.class, LowMetal.class, MajorityOfMapVisible.class, MoveToMapCentre.class, MoveToRandom.class,
            MoveToSafe.class, MoveToTension.class, EnemyBuildingNear.class, InRadarRange.class, IsAreaControlled.class, TopOfHill.class, LowHealth.class, BuildCaretaker.class, ReclaimMetal.class};

    @Override
    public int init(int teamId, OOAICallback callback) {
        this.callback = callback;
        this.teamId = teamId;
        INSTANCE = this;

        if (runningBT) {
            inputTree = topOfHillTest;
            opt = new TreeInterpreter<>(this).create(classes, inputTree);
            executorService = Executors.newWorkStealingPool();

            btRunner = () -> {
                trees.keySet().forEach(BehaviourTree::step);
                LiveBT.draw();
            };

            callback.getGame().sendTextMessage("inputtree = " + this.inputTree, 0);
        }

        managers = new ArrayList<>();
        //Eco must be called before other managers
        economyManager = new EconomyManager(callback, runningBT, inputTree, opt, trees);
        managers.add(economyManager);
        influenceManager = new InfluenceManager();
        managers.add(influenceManager);
        militaryManager = new MilitaryManager();
        managers.add(militaryManager);
        recruitmentManager = new RecruitmentManager();
        managers.add(recruitmentManager);
        //losManager = new LOSManager();
        //managers.add(losManager);


        startTime = System.nanoTime();
        return 0;
    }

    public String readTree() {
        File f = new File("C:\\Users\\Jonatan\\workspace\\EvolutionRunner\\out\\tree.txt");

        Scanner in;
        try {
            in = new Scanner(f);
            String treeString = in.nextLine();
            while (in.hasNext()) {
                treeString = in.nextLine();
            }
            callback.getGame().sendTextMessage("TREE - " + treeString, 0);
            return treeString;
        } catch (IOException e) {
            callback.getGame().sendTextMessage("Cant read tree", 0);
            return null;
        }

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
                callback.getGame().sendTextMessage(m.getModuleName() + " " + e.getMessage(), 0);
                printException(e);
            }
        }

        if (frame % 30 == 0) {
            if (!debugActivated)
                activateDebug();
            else
                debugView.repaint();

        }

        if (frame % 60 == 0 && runningBT) {
            try {
                executorService.submit(btRunner);
            } catch (Exception e) {
                callback.getGame().sendTextMessage("bt problem", 0);
            }
        }


/*        if (runningBT && frame % 100 == 0) {
            executorService.submit(btTask);
        }*/

        return 0;
    }

    //when the ai is released AKA when game has ended
    @Override
    public int release(int reason) {
        int time = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        double avgMex = economyManager.getAvgMexVSSpots();
        double highestIncome = economyManager.getHighestIncome() / 50d;
        double killVsExpenditureMetal = ((double) militaryManager.getEnemiesKilledMetalValue()) / (double) economyManager.getTotalExpenditure();
        callback.getGame().sendTextMessage("KillvsExpenditure " + killVsExpenditureMetal, 0);
        callback.getGame().sendTextMessage("enemiesKilledMValue " + militaryManager.getEnemiesKilledMetalValue() + " totalExpend " + economyManager.getTotalExpenditure(), 0);
        callback.getGame().sendTextMessage("avgMex " + avgMex, 0);
        killVsExpenditureMetal /= 2;
        if (killVsExpenditureMetal > 1d)
            killVsExpenditureMetal = 1d;
        else if (killVsExpenditureMetal < 0d)
            killVsExpenditureMetal = 0d;

        callback.getGame().sendTextMessage("END " + "teamId: " + this.teamId + " time: " + time / 1000
                + " Soldiers: " + militaryManager.soldiers.size()
                + " avgEco: " + economyManager.getAvgEco()
                + " avgMex: " + avgMex
                + " peakIncome: " + highestIncome
                + " killVsExpenditureMetal: " + killVsExpenditureMetal, 0);
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
