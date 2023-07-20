package com.aelous.model.content.minigames.impl.tempoross.skilling.interaction;

import com.aelous.model.content.minigames.impl.tempoross.TemporossHandler;
import com.aelous.model.content.minigames.impl.tempoross.process.Tempoross;
import com.aelous.model.content.minigames.impl.tempoross.skilling.FishingSpots;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SpiritPoolsInteraciton extends PacketInteraction {
    @Getter @Setter public static boolean interacting = false;
    public static final List<Integer> damage = new ArrayList<>();
    public static int randomDelay = Utils.random(2, 4);
    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        for (var b : TemporossHandler.bossList) {
            if (npc.id() == b.getId()) {
                if (player.getInventory().contains(ItemIdentifiers.HARPOON)) {
                    startSpiritPoolInteraction(player);
                    return true;
                } else {
                    player.message("You need a harpoon to perform this action.");
                    return false;
                }
            }
        }

        for (var s : FishingSpots.fishingSpots) {
            if (npc.id() == s.getId()) {
                if (player.getInventory().contains(ItemIdentifiers.HARPOON)) {
                    fish(player);
                    return true;
                } else {
                    player.message("You need a harpoon to perform this action.");
                    return false;
                }
            }
        }
        return false;
    }

    public static void fish(Player player) {
        player.repeatingTask(randomDelay, t -> {
            Entity target = ((WeakReference<Entity>) player.getAttribOr(AttributeKey.TARGET, new WeakReference<>(null))).get();
            if (target == null || !target.isNpc() || target.dead() || target.finished() || !Tempoross.isActivatePools()) {
                player.animate(-1);
                System.out.println("random stop?");
                t.stop();
                return;
            }
            player.getInventory().add(new Item(25565, 1));
            player.animate(618);
        });
    }

    public static void startSpiritPoolInteraction(Player player) {
        setInteracting(true);
        player.repeatingTask(randomDelay, t -> {
            int randomDamage = Utils.random(1, 12);
            Entity target = ((WeakReference<Entity>) player.getAttribOr(AttributeKey.TARGET, new WeakReference<>(null))).get();
            if (target == null || !target.isNpc() || target.dead() || target.finished() || !Tempoross.isActivatePools()) {
                player.animate(-1);
                setInteracting(false);
                t.stop();
                return;
            }
            damage.add(randomDamage);
            player.animate(618);
        });
    }
}
