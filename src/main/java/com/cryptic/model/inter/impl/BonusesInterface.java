package com.cryptic.model.inter.impl;

import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.InterfaceID;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.World;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import lombok.Getter;

/**
 * Created by Bart on 10/2/2015.
 * <p>
 * Handles the bonuses interface and stats panel.
 */
public class BonusesInterface extends PacketInteraction {

    @Getter Player player;
    int bloodMoneyDrop = 0;
    public BonusesInterface(Player player) {
        this.player = player;
    }

    public void showEquipmentInfo() {
        if (player.locked()) {
            System.out.println("blocking here");
            return;
        }

        player.getUpdateFlag().flag(Flag.APPEARANCE);
        player.getUpdateFlag().flag(Flag.ANIMATION);
        player.stopActions(false);
        var target = player.getCombat().getTarget();

        if (target != null && target.isPlayer()) {
            bloodMoneyDrop = player.bloodMoneyAmount(target.getAsPlayer());
        } else {
            bloodMoneyDrop = player.bloodMoneyAmount(null);
        }

        this.sendBonuses();
        player.getInterfaceManager().open(21172);
    }

    private String plusify(int bonus) {
        return (bonus < 0) ? Integer.toString(bonus) : "+" + bonus;
    }

    public void sendBonuses() {
        EquipmentBonuses b = player.getBonuses().totalBonuses(player, World.getWorld().equipmentInfo());

        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,24, STR."Stab: \{plusify(b.getStab())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,25, STR."Slash: \{plusify(b.getSlash())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,26,STR."Crush: \{plusify(b.getCrush())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,27,STR."Magic: \{plusify(b.getMage())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,28, STR."Range: \{plusify(b.getRange())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,30,STR."Stab: \{plusify(b.getStabdef())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,31, STR."Slash: \{plusify(b.getSlashdef())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,32, STR."Crush: \{plusify(b.getCrushdef())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,33, STR."Magic: \{plusify(b.getMagedef())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,34, STR."Range: \{plusify(b.getRangedef())}");


        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,36,STR."Melee STR: \{plusify(b.getStr())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,37,STR."Ranged STR: \{plusify(b.getRangestr())}");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,38,STR."Magic DMG: \{plusify(b.getMagestr())}%");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,39,STR."Prayer: \{plusify(b.getPray())}");

        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS,41,STR."Undead: \{plusify(getUndead())}%");
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS, 42, STR."Slayer: \{plusify(getSlay())}%");


        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
        if (weapon == null) {
            player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS, 53,"Base: 4s");
        } else {
            player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS, 53, STR."Base: \{World.getWorld().getEquipmentLoader().getInfo(weapon.getId()).getEquipment().getAspeed()}s");
        }
        player.getPacketSender().setComponentText(InterfaceID.EQUIPMENT_STATS, 54, STR."Actual: \{player.getBaseAttackSpeed()}s");


        player.getPacketSender().runClientScriptNew(7065, 5505075, 5505064, "Increases your effective accuracy and damage against undead creatures. For multi-target Ranged and Magic attacks, this applies only to the primary target. It does not stack with the Slayer multiplier.");

    }

    public int getSlay() {
        int slay = 0;
        Item helmItem = player.getEquipment().get(EquipSlot.HEAD);
        if (helmItem != null) {
            if (FormulaUtils.hasSlayerHelmet(player) || FormulaUtils.wearingBlackMask(player)) {
                slay += 16;
            } else if (FormulaUtils.hasSlayerHelmetImbued(player)) { // 15% from normal and 15% from imbue
                slay += 29;
            }
        }
        return slay;
    }

    public int getUndead() {
        int undead = 0;
        Item amuletItem = player.getEquipment().get(EquipSlot.AMULET);
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

}
