package com.cryptic.model.content.packet_actions.interactions.items;


import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.weapon.WeaponInterfaces;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import com.cryptic.model.items.container.looting_bag.LootingBag;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.timers.TimerKey;
import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.ItemType;

import java.util.Arrays;

import static com.cryptic.model.items.container.equipment.Equipment.getAudioId;

/**
 * @author Origin
 * juni 24, 2020
 */
public class ItemActionTwo {

    public static void click(Player player, Item item) {
        final int id = item.getId();
        final int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, -1);
        if (slot == -1) {
            return;
        }

        ItemType definition = CacheManager.INSTANCE.getItem(id);
        if (definition.getInterfaceOptions().get(1) == null) {
            return;
        }

        if (definition.getInterfaceOptions().get(1).equalsIgnoreCase("wear") || definition.getInterfaceOptions().get(1).equalsIgnoreCase("wield")) {
            player.debugMessage("Equip ItemId=" + id + " Slot=" + slot);

            player.getSkills().stopSkillable();

            EquipmentInfo info = World.getWorld().equipmentInfo();

            if (info != null) {
                if (player.getEquipment().equip(slot)) {
                    player.sendPrivateSound(getAudioId(item.name()), 0);
                    player.getBonusInterface().sendBonuses();
                    player.getCombat().setRangedWeapon(null);
                    player.getTimers().cancel(TimerKey.SOTD_DAMAGE_REDUCTION);
                    player.setSpecialActivated(false);
                    player.putAttrib(AttributeKey.GRANITE_MAUL_SPECIALS, 0);
                    player.getCombat().reset();
                    CombatSpecial.updateBar(player);
                    WeaponInterfaces.updateWeaponInterface(player);
                    player.getInventory().refresh();
                    player.getEquipment().refresh();
                    if (player.getEquipment().getWeapon() != null && player.getTimers().has(TimerKey.SOTD_DAMAGE_REDUCTION)) {
                        player.getTimers().cancel(TimerKey.SOTD_DAMAGE_REDUCTION);
                        player.getPacketSender().sendMessage(Color.RED.wrap("Your Staff of the dead special de-activated because you unequipped the staff."));
                    }
                }
            }
            return;
        }

        if (item.getId() == ItemIdentifiers.LOOTING_BAG || item.getId() == LootingBag.OPEN_LOOTING_BAG) {
            player.getLootingBag().open();
            return;
        }
    }
}
