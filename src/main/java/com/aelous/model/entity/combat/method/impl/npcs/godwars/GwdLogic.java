package com.aelous.model.entity.combat.method.impl.npcs.godwars;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl.KreeArra;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.bandos.Graardor;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.saradomin.Zilyana;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.zamorak.Kril;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCDeath;
import com.aelous.model.map.position.Area;

import java.util.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 15, 2020 13:54
 */
public class GwdLogic {

    private static final List<Integer> gwdBoss = Arrays.asList(2215, 3162, 2205, 3129);

    // Any type of gwd boss - just in the graaardor file srry xxx
    public static boolean isBoss(int npc) {
        return gwdBoss.contains(npc);
    }

    public static void onRespawn(NPC boss) {
        List<NPC> minionList = boss.getAttribOr(AttributeKey.MINION_LIST, null);
        if (minionList == null) return;

        minionList.forEach(NPCDeath::respawn);
    }

    public static void onServerStart() {
        Optional<NPC> boss = Optional.empty();
        Area[] areas =  {KreeArra.getENCAMPMENT(), Zilyana.getENCAMPMENT(), Kril.getENCAMPMENT(), Graardor.getBandosArea()};

        for (Area a : areas) {
            // Identify the boss
            for(NPC n : World.getWorld().getNpcs()) {
                if (n == null)
                    continue;
                if(n.tile().inArea(a)) {
                    if (GwdLogic.isBoss(n.id())) {// Located boss.
                        boss = Optional.of(n);
                    }
                }
            }
            if (boss.isPresent()) {
                // Now identify minions.
                NPC boss1 = boss.get();
                ArrayList<NPC> minionList = new ArrayList<>();
                for(NPC n : World.getWorld().getNpcs()) {
                    if (n == null)
                        continue;
                    if (n.tile().inArea(a) && !GwdLogic.isBoss(n.id())) {
                        minionList.add(n);
                    }
                }

                if (!minionList.isEmpty())
                    boss1.putAttrib(AttributeKey.MINION_LIST, minionList);
            } else {
                System.out.println("boss missing from gwd... wat");
            }
            boss = Optional.empty();
        }
    }
}
