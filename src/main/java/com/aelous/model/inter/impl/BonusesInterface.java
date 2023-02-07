package com.aelous.model.inter.impl;

import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.World;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.Color;

/**
 * Created by Bart on 10/2/2015.
 * <p>
 * Handles the bonuses interface and stats panel.
 */
public class BonusesInterface {

    public static void showEquipmentInfo(Player player) {
        // Cannot do this while locked.
        if (player.locked())
            return;

        // Has introduced dupes previously..
        player.stopActions(false);
        sendBonuses(player);

        var dropRateBonus = player.dropRateBonus();
        var target = player.getCombat().getTarget();
        var bloodMoneyDrop = 0;

        if(target != null && target.isPlayer()) {
            bloodMoneyDrop = player.bloodMoneyAmount(target.getAsPlayer());
        } else {
            bloodMoneyDrop = player.bloodMoneyAmount(null);
        }

        player.getInterfaceManager().open(InterfaceConstants.EQUIPMENT_SCREEN_INTERFACE_ID);
        player.getPacketSender().sendInterfaceDisplayState(15150,true).sendString(15122, "Drop rate bonus: "+ Color.GREEN.wrap("+"+dropRateBonus)+"<col=65280>%</col>").sendString(15123, "Blood money rate: "+ Color.GREEN.wrap("+"+bloodMoneyDrop));
        player.getUpdateFlag().flag(Flag.APPEARANCE); //Update the players looks
    }

    private static String plusify(int bonus) {
        return (bonus < 0) ? Integer.toString(bonus) : "+" + bonus;
    }

    public static void sendBonuses(Player player) {
        EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());

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

        player.getPacketSender().sendString(15115, "Melee strength: " + plusify(b.str));
        player.getPacketSender().sendString(15116, "Ranged strength: " + plusify(b.rangestr));
        player.getPacketSender().sendString(15117, "Magic damage: " + plusify(b.magestr) + "%");

        player.getPacketSender().sendString(15118, "Prayer: " + plusify(b.pray));
        player.getPacketSender().sendString(15119, "Undead: " + getUndead(player) + "%");
        player.getPacketSender().sendString(15120, "Slayer: " + getSlay(player) + "%");
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
