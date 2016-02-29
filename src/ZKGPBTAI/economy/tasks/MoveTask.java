package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import com.springrts.ai.Enumerations;
import com.springrts.ai.oo.AIFloat3;
import com.sun.istack.internal.NotNull;

/**
 * Created by Hallvard on 29.02.2016.
 */
public class MoveTask extends WorkerTask {

    public MoveTask(AIFloat3 pos) {
        super();
        this.position = pos;
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        worker.getUnit().moveTo(position, (short)Enumerations.UnitCommandOptions.UNIT_COMMAND_OPTION_RIGHT_MOUSE_KEY.getValue(), Integer.MAX_VALUE);
        return true;
    }
}