package com.cryptic.model.content.packet_actions.interactions.items;

import com.cryptic.model.content.consumables.potions.Potions;
import com.cryptic.model.content.items.combine.*;
import com.cryptic.model.content.items.combine.crystal.CrystalAxe;
import com.cryptic.model.content.items.combine.crystal.CrystalHarpoon;
import com.cryptic.model.content.items.combine.crystal.CrystalPickaxe;
import com.cryptic.model.content.skill.impl.firemaking.LogLighting;
import com.cryptic.model.content.skill.impl.herblore.HerbTar;
import com.cryptic.model.content.skill.impl.herblore.PestleAndMortar;
import com.cryptic.model.content.skill.impl.herblore.PotionBrewing;
import com.cryptic.model.content.skill.impl.herblore.SuperCombatPotions;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin
 * juni 15, 2020
 */
public class ItemOnItem {

    public static int slotOf(Player player, int item) {

        Item from = player.getAttrib(AttributeKey.FROM_ITEM);
        Item to = player.getAttrib(AttributeKey.TO_ITEM);
        if (from == null || to == null)
            return -1;

        if (from.getId() == item)
            return player.getAttrib(AttributeKey.ITEM_SLOT);
        if (to.getId() == item)
            return player.getAttrib(AttributeKey.ALT_ITEM_SLOT);

        return -1;
    }

    public static void itemOnItem(Player player, Item use, Item with) {
        if (PacketInteractionManager.checkItemOnItemInteraction(player, use, with)
            || LogLighting.onItemOnItem(player, use, with)
            || PotionBrewing.onItemOnItem(player, use, with)
            || PestleAndMortar.onItemOnItem(player, use, with)
            || HerbTar.onItemOnItem(player, use, with)
            || Potions.onItemOnItem(player, use, with)
            || SuperCombatPotions.makePotion(player, use, with)
            || player.getRunePouch().itemOnItem(use, with)
            || player.getLootingBag().itemOnItem(use, with)) {
            return;
        }

        if (player.getLootingBag().itemOnItem(use, with)) {
            return;
        }

        if (use.getId() == CRYSTAL_OF_IORWERTH || use.getId() == BOW_OF_FAERDHINEN) {
            player.getDialogueManager().start(new BowOfFaerdhenin());
            return;
        }

        if ((use.getId() == VOLATILE_ORB || with.getId() == VOLATILE_ORB) && (use.getId() == NIGHTMARE_STAFF || with.getId() == NIGHTMARE_STAFF)) {
                player.getDialogueManager().start(new VolatileNightmareStaff());
            return;
            }

        if ((use.getId() == ELDRITCH_ORB || with.getId() == ELDRITCH_ORB) && (use.getId() == NIGHTMARE_STAFF || with.getId() == NIGHTMARE_STAFF)) {
                player.getDialogueManager().start(new EldritchNightmareStaff());
            return;
            }

        if ((use.getId() == CRYSTAL_TOOL_SEED || with.getId() == CRYSTAL_TOOL_SEED) && (use.getId() == DRAGON_PICKAXE || with.getId() == DRAGON_PICKAXE)) {
                player.getDialogueManager().start(new CrystalPickaxe());
            return;
        }

        if ((use.getId() == CRYSTAL_TOOL_SEED || with.getId() == CRYSTAL_TOOL_SEED) && (use.getId() == DRAGON_HARPOON || with.getId() == DRAGON_HARPOON)) {
                player.getDialogueManager().start(new CrystalHarpoon());
            return;
        }

        if ((use.getId() == CRYSTAL_TOOL_SEED || with.getId() == CRYSTAL_TOOL_SEED) && (use.getId() == DRAGON_AXE || with.getId() == DRAGON_AXE)) {
                player.getDialogueManager().start(new CrystalAxe());
            return;
            }

        if ((use.getId() == HARMONISED_ORB || with.getId() == HARMONISED_ORB) && (use.getId() == NIGHTMARE_STAFF || with.getId() == NIGHTMARE_STAFF)) {
                player.getDialogueManager().start(new HarmonisedNightmareStaff());
            return;
            }

            player.message("Nothing interesting happens.");
    }
}
