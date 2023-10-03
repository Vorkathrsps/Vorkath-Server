package com.cryptic.model.inter.impl;

import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.World;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import com.cryptic.utility.Color;

/**
 * Created by Bart on 10/2/2015.
 * <p>
 * Handles the bonuses interface and stats panel.
 */
public class BonusesInterface {

    static int bloodMoneyDrop = 0;
    public static void showEquipmentInfo(Player player) {
        if (player.locked())
            return;

        player.stopActions(false);
        sendBonuses(player);
        var target = player.getCombat().getTarget();

        if(target != null && target.isPlayer()) {
            bloodMoneyDrop = player.bloodMoneyAmount(target.getAsPlayer());
        } else {
            bloodMoneyDrop = player.bloodMoneyAmount(null);
        }

        player.getInterfaceManager().open(21172);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    private static String plusify(int bonus) {
        return (bonus < 0) ? Integer.toString(bonus) : "+" + bonus;
    }

    public static void sendBonuses(Player player) {
        EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        var dropRateBonus = player.getDropRateBonus();

        player.getPacketSender().sendString(1675, "Stab: " + plusify(b.stab));
        player.getPacketSender().sendString(1676, "Slash: " + plusify(b.slash));
        player.getPacketSender().sendString(1677, "Crush: " + plusify(b.crush));
        player.getPacketSender().sendString(1678, "Magic: " + plusify(b.mage));
        player.getPacketSender().sendString(1679, "Range: " + plusify(b.range));

        player.getPacketSender().sendString(1680, "Stab: " + plusify(b.stabdef));
        player.getPacketSender().sendString(1681, "Slash: " + plusify(b.slashdef));
        player.getPacketSender().sendString(1682, "Crush: " + plusify(b.crushdef));
        player.getPacketSender().sendString(1683, "Range: " + plusify(b.rangedef));
        player.getPacketSender().sendString(1684, "Magic: " + plusify(b.magedef));

        player.getPacketSender().sendString(1686, "Melee strength: " + plusify(b.str));
        player.getPacketSender().sendString(24751,"Ranged strength: " + plusify(b.rangestr));
        player.getPacketSender().sendString(24752,"Magic damage: " + plusify(b.magestr) + "%");

        player.getPacketSender().sendString(1687, "Prayer: " + plusify(b.pray));
        player.getPacketSender().sendString(24754,"Undead: " + plusify(getUndead(player)) + "%");
        player.getPacketSender().sendString(24755,"Slayer: " + plusify(getSlay(player)) + "%");
        player.getPacketSender().sendString(24757,"Base: " + plusify(player.getBaseAttackSpeed()) + "s");
        player.getPacketSender().sendString(24774, "BM: " + (bloodMoneyDrop > 0 ? Color.GREEN.wrap(plusify(bloodMoneyDrop)) : plusify(bloodMoneyDrop)));
        player.getPacketSender().sendString(24775, "DR: " + (dropRateBonus > 0 ? Color.GREEN.wrap(dropRateBonus + "%") : dropRateBonus + "%"));
    }

    public static int getSlay(Player player) {
        int slay = 0;
        Item helmItem = player.getEquipment().get(EquipSlot.HEAD);
        if (helmItem != null) {
            int helmId = helmItem.getId();
            String helmName = helmItem.definition(World.getWorld()).name;
            if (helmId == 11864 || helmId == 19647 || helmId == 19643 || helmId == 19639) { // Normal slayer helm
                slay += 15;
            } else if (helmName.startsWith("Black mask")) {
                slay += 15;
            } else if (helmName.toLowerCase().contains("slayer helmet (i)")) { // 15% from normal and 15% from imbue
                slay += 30;
            }
        }
        return slay;
    }

    public static int getUndead(Player player) {
        int undead = 0;
        Item amuletItem = player.getEquipment().get(EquipSlot.AMULET);
       // System.out.println((amuletItem == null ? "no amulet found" : "amulet found"));
        if (amuletItem != null) {
            String amuletName = amuletItem.definition(World.getWorld()).name.toLowerCase();
            if (amuletName.equalsIgnoreCase("salve amulet")) {
                undead += 15;
            } else if (amuletName.equalsIgnoreCase("salve amulet (e)")) {
                undead += 20;
            } else if (amuletName.equalsIgnoreCase("salve amulet(i)")) {
                undead += 20;
            } else if (amuletName.equalsIgnoreCase("salve amulet(ei)")) {
                undead += 20;
            }
        }
        return undead;
    }

    public static boolean bonusesButtons(Player player, int button) {
        if (button == 27653) {
            ItemWeight.calculateWeight(player);
            showEquipmentInfo(player);
            return true;
        }
        return false;
    }

    public static boolean onContainerAction(Player player, int id, int slot) {
        if (id == InterfaceConstants.EQUIPMENT_DISPLAY_ID) {
            if (slot == 0) {
                player.getEquipment().unequip(EquipSlot.HEAD);
                sendBonuses(player);
            } else if (slot == 1) {
                player.getEquipment().unequip(EquipSlot.CAPE);
                sendBonuses(player);
            } else if (slot == 2) {
                player.getEquipment().unequip(EquipSlot.AMULET);
                sendBonuses(player);
            } else if (slot == 3) {
                player.getEquipment().unequip(EquipSlot.WEAPON);
                sendBonuses(player);
            } else if (slot == 4) {
                player.getEquipment().unequip(EquipSlot.BODY);
                sendBonuses(player);
            } else if (slot == 5) {
                player.getEquipment().unequip(EquipSlot.SHIELD);
                sendBonuses(player);
            } else if (slot == 7) {
                player.getEquipment().unequip(EquipSlot.LEGS);
                sendBonuses(player);
            } else if (slot == 9) {
                player.getEquipment().unequip(EquipSlot.HANDS);
                sendBonuses(player);
            } else if (slot == 10) {
                player.getEquipment().unequip(EquipSlot.FEET);
                sendBonuses(player);
            } else if (slot == 12) {
                player.getEquipment().unequip(EquipSlot.RING);
                sendBonuses(player);
            } else if (slot == 13) {
                player.getEquipment().unequip(EquipSlot.AMMO);
                sendBonuses(player);
            }
            return true;
        }
        return false;
    }

}
