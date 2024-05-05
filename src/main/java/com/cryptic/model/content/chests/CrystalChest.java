package com.cryptic.model.content.chests;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.*;

public class CrystalChest extends PacketInteraction {
    CustomLootTable.InnerTable common = new CustomLootTable.InnerTable("UNCOMMON", List.of(
        new Item(AIR_RUNE, 50),
        new Item(EARTH_RUNE),
        new Item(WATER_RUNE),
        new Item(FIRE_RUNE),
        new Item(BODY_RUNE),
        new Item(MIND_RUNE),
        new Item(CHAOS_RUNE),
        new Item(DEATH_RUNE),
        new Item(COSMIC_RUNE),
        new Item(NATURE_RUNE),
        new Item(LAW_RUNE),
        new Item(UNCUT_RUBY + 1),
        new Item(UNCUT_DIAMOND + 1),
        new Item(UNCUT_DRAGONSTONE + 1),
        new Item(RAW_MONKFISH + 1),
        new Item(RAW_SWORDFISH + 1),
        new Item(IRON_ORE + 1),
        new Item(COAL + 1),
        new Item(UNCUT_SAPPHIRE + 1)));

    CustomLootTable.InnerTable uncommon = new CustomLootTable.InnerTable("UNCOMMON", List.of(
        new Item(CRYSTAL_KEY + 1, 2),
        new Item(RUNITE_BAR + 1),
        new Item(RAW_SHARK + 1),
        new Item(RUNE_FULL_HELM + 1),
        new Item(RUNE_PLATEBODY + 1),
        new Item(RUNE_PLATELEGS + 1),
        new Item(AMULET_OF_GLORY6),
        new Item(ADAMANTITE_BAR + 1),
        new Item(SUPER_COMBAT_POTION4 + 1),
        new Item(PRAYER_POTION4 + 1),
        new Item(CHAOS_RUNE),
        new Item(BLUE_DRAGONHIDE + 1),
        new Item(DEATH_RUNE),
        new Item(WRATH_RUNE),
        new Item(GREEN_DRAGONHIDE + 1),
        new Item(RED_DRAGONHIDE + 1),
        new Item(BLACK_DRAGONHIDE + 1),
        new Item(RUNE_KITESHIELD + 1),
        new Item(DRAGON_SCIMITAR),
        new Item(BATTLESTAFF + 1),
        new Item(DRAGON_BONES + 1),
        new Item(RUNE_2H_SWORD + 1)));

    CustomLootTable.InnerTable rare = new CustomLootTable.InnerTable("UNCOMMON", List.of(
        new Item(CRYSTAL_PICKAXE),
        new Item(CRYSTAL_AXE),
        new Item(CRYSTAL_HARPOON)));

    CustomLootTable tableOne = new CustomLootTable(common, 25);
    CustomLootTable tableTwo = new CustomLootTable(uncommon, 50);
    CustomLootTable tableThree = new CustomLootTable(rare, 128);
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == 37342) {
            if (option == 1) {
                if (player.getInventory().contains(ENHANCED_CRYSTAL_KEY)) {
                    player.lock();
                    player.animate(832);
                    Chain.noCtx().runFn(1, () -> {
                        player.varps().varbit(9296, 1);
                        if (Utils.rollDie(tableOne.getChance(), 1)) {

                        } else if (Utils.rollDie(tableTwo.getChance(), 1)) {

                        } else if (Utils.rollDie(tableThree.getChance(), 1)) {

                        }
                        player.getInventory().remove(ENHANCED_CRYSTAL_KEY);
                    }).then(2, () -> {
                        player.varps().varbit(9296, 0);
                        player.unlock();
                    });
                } else {
                    player.message(Color.BLUE.wrap("You do not Enhanced crystal key inside of your inventory."));
                }
            }
            return true;
        }
        return false;
    }
}
