package ZKGPBTAI.bt.conditions;

import ZKGPBTAI.Main;
import bt.leaf.Condition;

/**
 * Created by Jonatan on 26-Jan-16.
 */
public class HasArmy extends Condition<Main>{
    @Override
    protected boolean condition() {
        Main.INSTANCE.getCallback().getGame().sendTextMessage("HasArmy", 0);
        return (Main.INSTANCE.militaryManager.soldiers.size() > 20);
    }
}
