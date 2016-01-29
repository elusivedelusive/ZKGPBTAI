package ZKGPBTAI.bt.actions;

import ZKGPBTAI.GameState;
import ZKGPBTAI.Main;
import bt.leaf.Action;
import bt.utils.BooleanData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

/**
 * Created by Jonatan on 26-Jan-16.
 */
public class Defensive extends Action<Main> {

    @Override
    public TaskState execute() {
        Main.state = GameState.DEFENSIVE;
        //Main.INSTANCE.getCallback().getGame().sendTextMessage("Defensive", 0);
        return TaskState.SUCCEEDED;
    }

    @Override
    public void eval(EvolutionState evolutionState, int i, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        BooleanData data = (BooleanData) gpData;
        data.result = true;
    }
}