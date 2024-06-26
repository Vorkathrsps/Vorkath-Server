package com.cryptic.model.content.items.interactions;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.content.skill.impl.slayer.slayer_partner.SlayerPartner;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.utility.ItemIdentifiers.ENCHANTED_GEM;

/**
 * @author Origin | December, 24, 2020, 13:05
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class EnchantedGem extends PacketInteraction {

    @Override
    public boolean handleEquipment(Player player, Item item) {
        if (item.getId() == ENCHANTED_GEM) {
            checkSlayerStatus(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == ENCHANTED_GEM) {
                SlayerTask slayerTask = World.getWorld().getSlayerTasks();
                var assignment = slayerTask.getCurrentAssignment(player);
                if (assignment == null) {
                    player.message(Color.BLUE.wrap("You currently do not have an active Slayer Task."));
                    return true;
                }
                if (player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.SLAYERS_NODE)) {
                    if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                        var location = assignment.getLocation(assignment.isWildernessTask(player));
                        if (location != null) Teleports.basicTeleport(player, location);
                        return true;
                    }
                }
                var amount = assignment.getRemainingTaskAmount(player);
                player.message(Color.BLUE.wrap("<img=13><shad=0>Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + amount + "</shad></img>"));
                return true;
            }
        }

        if (option == 3) {
            if (item.getId() == ENCHANTED_GEM) {
                SlayerPartner.partnerOption(player);
                return true;
            }
        }
        return false;
    }

    private static void checkSlayerStatus(Player player) {
        SlayerCreature task = SlayerCreature.lookup(player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
        int num = player.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);

        if (task != null) {
            if (num > 0) {
                player.message("You're assigned to kill " + Slayer.taskName(task.uid) + "; only " + num + " more to go.");
            } else {
                player.message("You need something new to hunt.");
            }
        } else {
            player.message("You need something new to hunt.");
        }
    }
}
