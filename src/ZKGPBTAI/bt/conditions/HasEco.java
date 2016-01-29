package ZKGPBTAI.bt.conditions;

import ZKGPBTAI.Main;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 26-Jan-16.
 */
public class HasEco extends Condition<Main> {
    @Override
    protected boolean condition() {
        //Main.INSTANCE.getCallback().getGame().sendTextMessage("HasEco", 0);
        return (Main.INSTANCE.economyManager.effectiveIncome > 20);
    }
}