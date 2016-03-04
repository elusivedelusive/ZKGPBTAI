package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import com.springrts.ai.Enumerations;
import com.sun.istack.internal.NotNull;

/**
 * Created by Hallvard on 01.03.2016.
 */
public class ReclaimTask extends WorkerTask {

    public final float RECLAIM_RADIUS = 400.0f;

    public ReclaimTask() {
        super();
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        worker.getUnit().reclaimInArea(worker.getPos(), RECLAIM_RADIUS,
                (short)Enumerations.UnitCommandOptions.UNIT_COMMAND_OPTION_RIGHT_MOUSE_KEY.getValue(), Integer.MAX_VALUE);
        return false;
    }
}
