package com.cryptic.model.content.packet_actions.interactions.items;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.minigames.MinigameManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import com.cryptic.utility.Color;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;
import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.ItemType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.*;
import static com.cryptic.utility.ItemIdentifiers.ABYSSAL_TENTACLE;

public class ItemActionFive {
    private static final Logger playerDropLogs = LogManager.getLogger("PlayerDropLogs");
    private static final Level PLAYER_DROPS;

    static {
        PLAYER_DROPS = Level.getLevel("PLAYER_DROPS");
    }

    public static void click(Player player, Item item) {
        final int id = item.getId();
        final int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, -1);
        if (slot == -1) {
            return;
        }
        
        ItemType definition = CacheManager.INSTANCE.getItem(id);
        if (definition.getInterfaceOptions().get(4) == null) {
            return;
        }

        if (PacketInteractionManager.checkItemInteraction(player, item, 5)) {
            return;
        }

        List<Integer> charged_item_ids = Arrays.asList(TOXIC_BLOWPIPE, SERPENTINE_HELM, TRIDENT_OF_THE_SWAMP, TOXIC_STAFF_OF_THE_DEAD, TOME_OF_FIRE, SCYTHE_OF_VITUR, SANGUINESTI_STAFF, CRAWS_BOW, VIGGORAS_CHAINMACE, THAMMARONS_SCEPTRE, TRIDENT_OF_THE_SEAS, MAGMA_HELM, TANZANITE_HELM, ABYSSAL_TENTACLE);

        boolean ignore_charged_actions = charged_item_ids.stream().anyMatch(charged_item_id -> charged_item_id == item.getId());

        if (PacketInteractionManager.checkItemInteraction(player, item, 4) && !ignore_charged_actions) {
            return;
        }

        if (player.getPetEntity().dropPet(player, item)) {
            return;
        }

        ItemDefinition def = item.definition(World.getWorld());
        //System.out.println(Arrays.toString(def.ioptions));
        //System.out.println(Arrays.toString(def.options));
        if (def != null && def.ioptions != null && def.ioptions[4] != null && def.ioptions[4].equalsIgnoreCase("destroy")) {
            destroyOption(player, slot, true);
        } else {
            //Check to see if the player is special teleblocked
            if (player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                player.teleblockMessage();
                return;
            }

            if (item.getId() == CustomItemIdentifiers.ESCAPE_KEY && WildernessArea.isInWilderness(player)) {
                player.message("You can't drop this item.");
                return;
            }

            int totalValueStack = item.getValue() * item.getAmount();
            boolean stackableValuedItem = item.stackable() && totalValueStack > 10_000;
            if ((stackableValuedItem || item.getValue() > 10_000) && WildernessArea.isInWilderness(player)) {
                if(!player.inventory().contains(item)) {
                    return;
                }
                player.confirmDialogue(new String[]{"Are you sure you wish to drop your "+item.unnote().name()+"?", "Everyone can pick up your "+item.unnote().name()+" it will be lost forever.", "Dropping the item is at your own risk."}, "", "Proceed.", "Nevermind.", () -> {
                    if(!player.inventory().contains(item)) {
                        return;
                    }
                    player.inventory().remove(item,true);
                    dropItem(player, item);
                });
                return;
            }

            player.inventory().remove(item, true);

            if (item.getValue() == 0 && WildernessArea.isInWilderness(player)) {
                player.message(Color.RED.wrap("You dropped a spawnable item in the wilderness, it disappears as it touches the ground."));
                return;
            }

            //Remove from inventory
            dropItem(player, item);
        }
    }

    private static void dropItem(Player player, Item item) {
        player.getRisk().update();

        playerDropLogs.log(PLAYER_DROPS, "Player " + player.getUsername() + " dropped item " + item.getAmount() + "x " + item.unnote().name() + " (id " + item.getId() + ") at X: " + player.getX() + ", Y: " + player.getY());
        Utils.sendDiscordInfoLog("Player " + player.getUsername() + " dropped item " + item.getAmount() + "x " + item.unnote().name() + " (id " + item.getId() + ") at X: " + player.getX() + ", Y: " + player.getY(), "playerdrops");

        //We probably don't need to duplicate item attributes/properties here,
        //since dropping loses charges in OSRS.
        GroundItem groundItem = new GroundItem(item, player.tile(), player);

        //When dropping items in the wilderness everyone can instantly pick them up
        if (WildernessArea.isInWilderness(player)) {
            groundItem.setState(GroundItem.State.SEEN_BY_EVERYONE);
        }

        //Drop the item on the floor
        GroundItemHandler.createGroundItem(groundItem);
    }

    private static void destroyOption(Player player, int invSlot, boolean destroyIt) {
        player.stopActions(false);
        player.animate(-1); // Reset animation

        Item item = player.inventory().get(invSlot);
        if (item == null) return;

        String msg = destroyIt ? "Destroying this item permanently destroys it. You cannot get it back." : "It will be dropped to the ground, you can pick it up.";

        player.setDestroyItem(item.getId());
        String[][] info = {//The info the dialogue gives
            {"Are you sure you want to destroy this item?", "14174"},
            {"Yes.", "14175"}, {"No.", "14176"}, {"", "14177"},
            {msg, "14182"}, {"You cannot get it back if discarded.", "14183"},
            {item.name(), "14184"}};
        player.getPacketSender().sendItemOnInterfaceSlot(14171, item.getId(), item.getAmount(), 0);
        for (String[] strings : info) player.getPacketSender().sendString(Integer.parseInt(strings[1]), strings[0]);
        player.getPacketSender().sendChatboxInterface(14170);
    }
}