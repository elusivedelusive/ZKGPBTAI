package ZKGPBTAI.economy.tasks;

import ZKGPBTAI.economy.Worker;
import ZKGPBTAI.utils.Utility;
import com.springrts.ai.Enumerations;
import com.springrts.ai.Util;
import com.springrts.ai.oo.clb.Feature;
import com.sun.istack.internal.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by Hallvard on 01.03.2016.
 */
public class ReclaimTask extends WorkerTask {

    private Stack<Feature> features;

    private boolean sorted = false;

    public static final float RECLAIM_RADIUS = 450f;
    public static final float FEATURE_RADIUS = 75f;

    public ReclaimTask(@NotNull Stack<Feature> featureStack) {
        super();
        this.features = featureStack;
    }

    @Override
    protected void setResult(Boolean result, int frame) {
        features.pop();
        sorted = false;
        if(features.empty())
            super.setResult(result, frame);
        else
            start(assignedWorkers.get(0));
    }

    @Override
    public boolean start(@NotNull Worker worker) {
        //Sort so the closest feature gets reclaimed first
        if(!sorted) {
            Comparator<Feature> dist = (f1, f2) -> Float.compare(Utility.distance(f2.getPosition(), worker.getPos()), Utility.distance(f1.getPosition(), worker.getPos()));
            Collections.sort(features, dist);
            sorted = true;
        }

        Feature current = features.peek();
        if(current.getReclaimLeft() <= 0) {
            setResult(true, worker.getUnit().getLastUserOrderFrame() + 1);
            return false;
        }
        if(Utility.distance(worker.getPos(), current.getPosition()) > FEATURE_RADIUS)
            worker.getUnit().moveTo(current.getPosition(), (short)0, Integer.MAX_VALUE);
        worker.getUnit().reclaimInArea(current.getPosition(), FEATURE_RADIUS, (short) Enumerations.UnitCommandOptions.UNIT_COMMAND_OPTION_SHIFT_KEY.getValue(), Integer.MAX_VALUE);

        return true;
    }

    public Stack<Feature> getStack() {
        return features;
    }

    @Override
    public String toString(){
        return "reclaiming ";
    }
}
