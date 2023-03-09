package com.aelous.model.content.skill.impl.magic;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.ItemIdentifiers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick van Elderen | March, 07, 2021, 12:40
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class JewelleryEnchantment {

    public enum Enchant {
        SAPPHIRE_RING(1637, 2550, 7, 18, 719, 114, 1),
        SAPPHIRE_AMULET(1694, 1727, 7, 18, 719, 114, 1),
        SAPPHIRE_NECKLACE(1656, 3853, 7, 18, 719, 114, 1),

        EMERALD_RING(1639, 2552, 27, 37, 719, 114, 2),
        EMERALD_AMULET(1696, 1729, 27, 37, 719, 114, 2),
        EMERALD_NECKLACE(1658, 5521, 27, 37, 719, 114, 2),

        RUBY_RING(1641, 2568, 47, 59, 720, 115, 3),
        RUBY_AMULET(1698, 1725, 47, 59, 720, 115, 3),
        RUBY_NECKLACE(1660, 11194, 47, 59, 720, 115, 3),

        DIAMOND_RING(1643, 2570, 57, 67, 720, 115, 4),
        DIAMOND_AMULET(1700, 1731, 57, 67, 720, 115, 4),
        DIAMOND_NECKLACE(1662, 11090, 57, 67, 720, 115, 4),

        DRAGONSTONE_RING(1645, 2572, 68, 78, 721, 116, 5),
        DRAGONSTONE_AMULET(1702, 1712, 68, 78, 721, 116, 5),
        DRAGONSTONE_NECKLACE(1664, 11105, 68, 78, 721, 116, 5),

        ONYX_RING(6575, 6583, 87, 97, 721, 452, 6),
        ONYX_AMULET(6581, 6585, 87, 97, 721, 452, 6),
        ONYX_NECKLACE(6577, 11128, 87, 97, 721, 452, 6),

        ZENYTE_RING(ItemIdentifiers.ZENYTE_RING, ItemIdentifiers.RING_OF_SUFFERING,93,110,721,452,7),
        ZENYTE_BRACELET(ItemIdentifiers.ZENYTE_BRACELET, ItemIdentifiers.TORMENTED_BRACELET,93,110, 721,452,7),
        ZENYTE_AMULET(ItemIdentifiers.ZENYTE_AMULET, ItemIdentifiers.AMULET_OF_TORTURE,93, 110,721,452,7),
        ZENYTE_NECKLACE(ItemIdentifiers.ZENYTE_NECKLACE, ItemIdentifiers.NECKLACE_OF_ANGUISH,110,97,721,452,7);

        private final int unenchanted;
        private final int enchanted;
        private final int levelReq;
        private final double exp;
        private final int anim;
        private final int gfx;
        private final int reqEnchantmentLevel;

        Enchant(int unenchanted, int enchanted, int levelReq, double exp, int anim, int gfx, int reqEnchantmentLevel) {
            this.unenchanted = unenchanted;
            this.enchanted = enchanted;
            this.levelReq = levelReq;
            this.exp = exp;
            this.anim = anim;
            this.gfx = gfx;
            this.reqEnchantmentLevel = reqEnchantmentLevel;
        }
        private static final Map<Integer, Enchant> ENCHANT = new HashMap<>();

        public static Enchant forId(int itemID) {
            return ENCHANT.get(itemID);
        }

        static {
            for (Enchant enchant : Enchant.values()) {
                ENCHANT.put(enchant.unenchanted, enchant);
            }
        }
    }

    private static int getEnchantmentLevel(int spellID) {
        return switch (spellID) {
            //Lvl-1 enchant sapphire
            case 1155 -> 1;
            //Lvl-2 enchant emerald
            case 1165 -> 2;
            //Lvl-3 enchant ruby
            case 1176 -> 3;
            //Lvl-4 enchant diamond
            case 1180 -> 4;
            //Lvl-5 enchant dragonstone
            case 1187 -> 5;
            //Lvl-6 enchant onyx
            case 6003 -> 6;
            //Lvl-7 enchant zenyte
            case 22674 -> 7;
            default -> 0;
        };
    }

    public static boolean check(Player player, int itemId, int spellId) {
        Enchant enchant = Enchant.forId(itemId);
        if (enchant == null) {
            player.message("Nothing interesting happens.");
            return false;
        }

        if (getEnchantmentLevel(spellId) != enchant.reqEnchantmentLevel) {
            player.message("You can only enchant this jewellery using a level " + enchant.levelReq + " enchantment spell.");
            return false;
        }

        if (!player.inventory().contains(enchant.unenchanted)) {
            ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, enchant.unenchanted);
            player.message("You must have a " + def.name + " in your inventory.");
            return false;
        }
        return true;
    }

    public static void enchantItem(Player player, int itemID) {
        Enchant enc = Enchant.forId(itemID);
        player.animate(enc.anim);
        player.graphic(enc.gfx, GraphicHeight.HIGH,15);
        player.getSkills().addXp(Skills.MAGIC,enc.exp);
        player.getClickDelay().reset();
        player.inventory().remove(enc.unenchanted,1);
        player.inventory().add(enc.enchanted,1);
        player.getPacketSender().sendTab(6);
    }
}
