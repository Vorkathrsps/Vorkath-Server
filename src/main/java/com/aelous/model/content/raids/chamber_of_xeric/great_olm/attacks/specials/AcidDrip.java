package com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.specials;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.aelous.model.content.raids.party.Party;
import com.aelous.core.task.Task;
import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Patrick van Elderen | May, 16, 2021, 18:58
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class AcidDrip {

    private static boolean finished(Entity entity, Player player) {
        if (entity != null) {
            return entity.isNpc() && (entity.dead() || !entity.isRegistered()) || (player != null && (player.dead() || !player.isRegistered() || player.tile().distance(entity.tile()) > 12));
        }
        return false;
    }

    public static void performAttack(Party party) {
        NPC npc = party.getGreatOlmNpc();
        npc.performGreatOlmAttack(party);
        party.setOlmAttackTimer(6);

        Player player = party.randomPartyPlayer();
        List<GameObject> poisons = new ArrayList<>();
        List<Tile> poisonTiles = new ArrayList<>();
        Player[] closePlayers = npc.closePlayers(64);

        Task.repeatingTask(t -> {
            if (finished(npc, player) || t.tick >= 23) {
                t.stop();
            } else {
                if (player != null && (player.getRaids() != null && player.getRaids().raiding(player) && GreatOlm.insideChamber(player))) {
                    Tile acidSpotPosition = player.tile();
                    GameObject pool = new GameObject(30032, acidSpotPosition, 10, World.getWorld().random(3)).setSpawnedfor(Optional.of(player));
                    poisons.add(pool);
                    Chain.bound(null).runFn(6, () -> {
                        poisonTiles.add(acidSpotPosition);
                    });
                }

                for (GameObject object : poisons) {
                    for (Player p : closePlayers) {
                        p.getPacketSender().sendObject(object);
                    }
                }

                //System.out.println("closeplayers: "+closePlayers.length);
                for (Player p : closePlayers) {
                    if (poisonTiles.contains(p.tile())) {
                        int hit = World.getWorld().random(1, 3);
                        p.hit(npc, hit, SplatType.POISON_HITSPLAT);
                    }
                }
            }
        });

        Chain.bound(null).runFn(1 + 23 + 1, () -> {
            poisons.forEach(object -> {
                for (Player p : closePlayers) {
                    p.getPacketSender().sendObjectRemoval(object);
                }
            });
            poisons.clear();
            poisonTiles.clear();
        });
    }
}
