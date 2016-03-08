package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import com.springrts.ai.Enumerations;
import com.springrts.ai.oo.clb.Feature;
import com.sun.istack.internal.NotNull;

import java.util.Optional;
import java.util.Stack;

/**
 * Created by Hallvard on 01.03.2016.
 */
public class ReclaimTask extends WorkerTask {

    private Stack<Feature> features;

    private Optional<Feature> current;

    public static final float RECLAIM_RADIUS = 300.0f;
    public static final float FEATURE_RADIUS = 75f;

    public ReclaimTask(@NotNull Stack<Feature> featureStack) {
        super();
        this.features = featureStack;
    }

    @Override
    protected void setResult(Boolean result, int frame) {
        current = Optional.empty();
        if(features.isEmpty())
            super.setResult(result, frame);
        else
            start(assignedWorkers.get(0));
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        if(features.isEmpty() && !current.isPresent()) {
            super.setResult(true, worker.getUnit().getLastUserOrderFrame()+1);
            return false;
        }
        current.orElse(features.pop());
        current.ifPresent( feature -> { //should always happen
            worker.getUnit().moveTo(feature.getPosition(), (short)0, Integer.MAX_VALUE);
            worker.getUnit().reclaimInArea(feature.getPosition(), FEATURE_RADIUS, (short)Enumerations.UnitCommandOptions.UNIT_COMMAND_OPTION_SHIFT_KEY.getValue(), Integer.MAX_VALUE);
        });
        return true;
    }
}
