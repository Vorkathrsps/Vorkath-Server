package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 19, 2020 16:52
 */
public class VetionMinion extends NPC {

    public static List<NPC> houndCount = new ArrayList<>();
    public VetionMinion(NPC vetion, Entity target) {
        super(6613, vetion.tile());
        this.putAttrib(AttributeKey.BOSS_OWNER, vetion);
        this.setTile(vetion.tile().transform(Utils.random(3), -1));
        this.walkRadius(8);
        this.respawns(false);
        this.getCombat().attack(target);
        houndCount.add(vetion);
    }

    public static void death(NPC npc) {
        NPC vetion = npc.getAttribOr(AttributeKey.BOSS_OWNER, null);
        if (vetion != null) {
            //Check for any minions.
            List<NPC> minList = vetion.getAttribOr(AttributeKey.MINION_LIST, null);
            if (minList != null) {
                minList.remove(npc);
                houndCount.remove(npc);
                // All minions dead? Enable damage on vetion again
                if (minList.size() == 0) {
                    vetion.putAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED, false);
                }
            }
        }
    }
}
