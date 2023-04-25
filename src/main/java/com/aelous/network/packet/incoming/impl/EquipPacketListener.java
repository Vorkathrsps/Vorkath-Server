package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.inter.impl.BonusesInterface;
import com.aelous.model.content.mechanics.item_simulator.ItemSimulatorUtility;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.items.container.looting_bag.LootingBag;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.ACCURSED_SCEPTRE_A;

/**
 * This packet listener manages the equip action a player
 * executes when wielding or equipping an item.
 *
 * @author relex lawl
 */

public class EquipPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int id = packet.readShort();
        int slot = packet.readShortA();
        int interfaceId = packet.readShortA();

        boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);

        if (newAccount) {
            player.message("You have to select your game mode before you can continue.");
            return;
        }

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (slot < 0 || slot > 27)
            return;
        Item item = player.inventory().get(slot);
        if (item != null && item.getId() == id && !player.locked() && !player.dead()) {
            if (player.getInterfaceManager().isInterfaceOpen(ItemSimulatorUtility.WIDGET_ID)) {
                player.message("Close this interface before trying to equip your " + item.unnote().name() + ".");
                return;
            }

            //Close all other interfaces except for the {@code Equipment.EQUIPMENT_SCREEN_INTERFACE_ID} one..
            if (!player.getInterfaceManager().isClear() && !player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.EQUIPMENT_SCREEN_INTERFACE_ID)) {
                player.getInterfaceManager().close(false);
            }

            if (item.getId() == ItemIdentifiers.LOOTING_BAG || item.getId() == LootingBag.OPEN_LOOTING_BAG) {
                player.getLootingBag().open();
                return;
            }

            if (player.getTimers().has(TimerKey.SOTD_DAMAGE_REDUCTION)) {
                player.getPacketSender().sendMessage(Color.RED.wrap("Your Staff of the dead special de-activated because you unequipped the staff."));
                return;
            }

            if (interfaceId == InterfaceConstants.INVENTORY_INTERFACE) {
                player.debugMessage("Equip ItemId=" + id + " Slot=" + slot + " InterfaceId=" + interfaceId);

                //Stop skilling..
                player.getSkills().stopSkillable();

                EquipmentInfo info = World.getWorld().equipmentInfo();
                if (info != null) {
                    player.getEquipment().equip(slot);
                    BonusesInterface.sendBonuses(player);
                    player.getInventory().refresh();
                    player.getEquipment().refresh();
                    player.getCombat().setRangedWeapon(null);
                    Autocasting.setAutocast(player, null);
                    player.getCombat().setCastSpell(null);
                    player.getCombat().setPoweredStaffSpell(null);
                    player.getTimers().cancel(TimerKey.SOTD_DAMAGE_REDUCTION);
                    player.setSpecialActivated(false);
                    player.putAttrib(AttributeKey.GRANITE_MAUL_SPECIALS, 0);
                    player.getCombat().reset();
                    CombatSpecial.updateBar(player);
                    WeaponInterfaces.updateWeaponInterface(player);
                    if (player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS_FULL)) {
                        if (player.getCombat().getPoweredStaffSpell() != null) {
                            player.getCombat().setPoweredStaffSpell(null);
                        }
                        player.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SEAS.getSpell());
                    } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP)) {
                        if (player.getCombat().getPoweredStaffSpell() != null) {
                            player.getCombat().setPoweredStaffSpell(null);
                        }
                        player.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell());
                    } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
                        if (player.getCombat().getPoweredStaffSpell() != null) {
                            player.getCombat().setPoweredStaffSpell(null);
                        }
                        player.getCombat().setPoweredStaffSpell(CombatSpells.SANGUINESTI_STAFF.getSpell());
                    } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, TUMEKENS_SHADOW)) {
                        if (player.getCombat().getPoweredStaffSpell() != null) {
                            player.getCombat().setPoweredStaffSpell(null);
                        }
                        player.getCombat().setPoweredStaffSpell(CombatSpells.TUMEKENS_SHADOW.getSpell());
                    } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ACCURSED_SCEPTRE_A)) {
                        if (player.getCombat().getPoweredStaffSpell() != null) {
                            player.getCombat().setPoweredStaffSpell(null);
                        }
                        player.getCombat().setPoweredStaffSpell(CombatSpells.ACCURSED_SCEPTRE.getSpell());
                    } else {
                        if (player.getCombat().getAutoCastSpell() != null) {
                            Autocasting.setAutocast(player, null);
                            player.getPacketSender().sendMessage("Autocast spell cleared.");
                        }
                    }
                }
            }
        }
    }
}
