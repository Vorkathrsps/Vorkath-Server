package com.aelous.model.entity.combat.method.impl.npcs.bosses.kalphite;

import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.map.position.Area;

/**
 * Created by Jason MacKeigan (http://www.github.com/JasonMacKeigan)
 */
public class KalphiteQueen {

    /**
     * The area that is the kalphite lair
     */
    private static final Area area = new Area(3460, 9475, 3520, 9520);

    public static Area getArea() {
        return area;
    }

    /**
     * A mapping of all kalphite queen forms with their respective animations
     */
    public static int animation(int npc, CombatType combatType) {
        if(npc == 6500) {
            return switch (combatType) {
                case MELEE -> 6241;
                case MAGIC, RANGED -> 6240;
            };
        } else if(npc == 6501) {
            return switch (combatType) {
                case MELEE -> 6235;
                case MAGIC, RANGED -> 6234;
            };
        }

        //return melee animations
        return npc == 6500 ? 6241 : 6235;
    }
}
