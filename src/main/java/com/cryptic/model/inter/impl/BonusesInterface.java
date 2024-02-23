package com.cryptic.model.inter.impl;

import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.World;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ItemWeight;
import com.cryptic.model.items.container.equipment.Equipment;
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
        EquipmentInfo.Bonuses b = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        var dropRateBonus = player.getDropRateBonus();
        player.getPacketSender().sendString(1675, "Stab: " + plusify(b.getStab()));
        player.getPacketSender().sendString(1676, "Slash: " + plusify(b.getSlash()));
        player.getPacketSender().sendString(1677, "Crush: " + plusify(b.getCrush()));
        player.getPacketSender().sendString(1678, "Magic: " + plusify(b.getMage()));
        player.getPacketSender().sendString(1679, "Range: " + plusify(b.getRange()));
        player.getPacketSender().sendString(1680, "Stab: " + plusify(b.getStabdef()));
        player.getPacketSender().sendString(1681, "Slash: " + plusify(b.getSlashdef()));
        player.getPacketSender().sendString(1682, "Crush: " + plusify(b.getCrushdef()));
        player.getPacketSender().sendString(1683, "Range: " + plusify(b.getRangedef()));
        player.getPacketSender().sendString(1684, "Magic: " + plusify(b.getMagedef()));
        player.getPacketSender().sendString(1686, "Melee strength: " + plusify(b.getStr()));
        player.getPacketSender().sendString(24751, "Ranged strength: " + plusify(b.getRangestr()));
        player.getPacketSender().sendString(24752, "Magic damage: " + plusify(b.getMagestr()) + "%");
        player.getPacketSender().sendString(1687, "Prayer: " + plusify(b.getPray()));
        player.getPacketSender().sendString(24754, "Undead: " + plusify(getUndead()) + "%");
        player.getPacketSender().sendString(24755, "Slayer: " + plusify(getSlay()) + "%");
        player.getPacketSender().sendString(24757, "Base: " + plusify(player.getBaseAttackSpeed()) + "s");
        player.getPacketSender().sendString(24774, "BM: " + (bloodMoneyDrop > 0 ? Color.GREEN.wrap(plusify(bloodMoneyDrop)) : plusify(bloodMoneyDrop)));
        player.getPacketSender().sendString(24775, "Drop Rate: " + (dropRateBonus > 0 ? Color.GREEN.wrap(Utils.formatpercent(dropRateBonus)) : dropRateBonus + "%"));
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
