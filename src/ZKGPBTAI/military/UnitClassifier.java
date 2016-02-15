package ZKGPBTAI.military;

import com.springrts.ai.oo.clb.Unit;

/**
 * Created by Jonatan on 15-Feb-16.
 */
public final class UnitClassifier {

    public static String classify(Unit u) {
        switch (u.getDef().getName()) {
            //Raiders
            case "armpw":
                return "raider";
            case "corak":
                return "raider";
            case "corgator":
                return "raider";
            case "corpyro":
                return "raider";
            case "panther":
                return "raider";
            case "corsh":
                return "raider";
            case "amphraider3":
                return "raider";

            //Assault
            case "armzeus":
                return "assault";
            case "corthud":
                return "assault";
            case "corraid":
                return "assault";
            case "spiderassault":
                return "assault";
            case "correap":
                return "assault";

            //skirmisher
            case "armrock":
                return "skirmisher";
            case "corstorm":
                return "skirmisher";
            case "armsptk":
                return "skirmisher";
            case "nsaclash":
                return "skirmisher";
            case "amphfloater":
                return "skirmisher";

            //riot
            case "armwar":
                return "riot";
            case "cormak":
                return "riot";
            case "corlevlr":
                return "riot";
            case "hoverriot":
                return "riot";
            case "amphriot":
                return "riot";

            //artillery
            case "armham":
                return "artillery";
            case "corgarp":
                return "artillery";
            case "armmerl":
                return "artillery";
            case "firewalker":
                return "artillery";
            case "cormart":
                return "artillery";
            case "trem":
                return "artillery";

            //scout
            case "armflea":
                return "scout";
            case "corfav":
                return "scout";
            case "puppy":
                return "scout";

            //aa
            case "armjeth":
                return "aa";
            case "corcrash":
                return "aa";
            case "vehaa":
                return "aa";
            case "armaak":
                return "aa";
            case "spideraa":
                return "aa";
            case "corsent":
                return "aa";
            case "hoveraa":
                return "aa";
            case "amphaa":
                return "aa";

            //support
            case "cormist":
                return "support";
            case "armmanni":
                return "support";
            case "shieldfelon":
                return "support";
            case "slowmort":
                return "support";

            //bomb
            case "armtick":
                return "bomb";
            case "corroach":
                return "bomb";

            //shield
            case "core_spectre":
                return "shield";

            //cloak
            case "spherecloaker":
                return "cloak";
        }
        return "";
    }
}
