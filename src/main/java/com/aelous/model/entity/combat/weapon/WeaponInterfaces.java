package com.aelous.model.entity.combat.weapon;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;

/**
 * A static utility class that displays holds and displays data for weapon
 * interfaces.
 *
 * @author lare96
 */
public class WeaponInterfaces {

    /**
     * The method executed when weapon {@code item} is equipped or unequipped that
     * assigns a weapon item container to {@code player}.
     *
     * @param player the player equipping the item.
     */
    public static void updateWeaponInterface(Player player) {
        Item equippedWeapon = player.getEquipment().getItems()[EquipSlot.WEAPON];
        WeaponType weaponType = WeaponType.UNARMED;
        EquipmentInfo info = World.getWorld().equipmentInfo();

        // Get the currently equipped weapon's interface
        if (equippedWeapon != null) {
            if (info != null) {
                weaponType = info.weaponType(equippedWeapon.getId());
                //System.out.println("weapon interface assign: " + weaponType);
            }
        }

        if (weaponType == WeaponType.UNARMED) {
            player.getPacketSender().sendString(weaponType.getNameLineId(), "Unarmed");
            player.getCombat().setWeapon(WeaponType.UNARMED);
        }

        //Send current weapon interface to client (hardcoded interfaces)
        int weapon = player.getEquipment().hasWeapon() ? player.getEquipment().getWeapon().getId() : -1;
        int ammo = player.getEquipment().hasAmmo() ? player.getEquipment().getAmmo().getId() : -1;
        player.getPacketSender().sendWeapon(weapon, ammo);

        //Set the tab and send the string
        player.getInterfaceManager().setSidebar(0, weaponType.getInterfaceId());
        player.getPacketSender().sendString(weaponType.getNameLineId(), (weaponType == WeaponType.UNARMED ? "Unarmed" : equippedWeapon.name()));

        //player.debugMessage("Weapon interface: "+weaponInterface);

        //Assign the weapon for combat
        player.getCombat().setWeapon(weaponType);
        CombatSpecial.assign(player);
        CombatSpecial.updateBar(player);

        int length = player.getCombat().getWeaponType().getFightType().length;
        int varp = player.getCombat().getFightType().getParentId(); //Lets just not assume the varp is always 43. Client can also send custom code.
        int varpState = player.getCombat().getFightType().getChildId();
        varpState = varpState >= length ? 0 : varpState;
        //Set attack style based on varp
        if (weaponType == WeaponType.SCYTHE) { //For some reason the varp states on this interface are different
            switch (varpState) {
                case 0 -> player.getCombat().setFightType(FightType.SCYTHE_REAP);
                case 1 -> player.getCombat().setFightType(FightType.SCYTHE_CHOP);
                case 2 -> player.getCombat().setFightType(FightType.SCYTHE_JAB);
                case 3 -> player.getCombat().setFightType(FightType.SCYTHE_BLOCK);
            }
        } else {
            player.getCombat().setFightType(player.getCombat().getWeaponType().getFightType()[varpState]);
        }

        if (weaponType == WeaponType.SCYTHE) { //For some reason the varp states on this interface are different
            switch (player.getCombat().getFightType()) {
                case SCYTHE_REAP -> player.getPacketSender().sendConfig(varp, 0);
                case SCYTHE_CHOP -> player.getPacketSender().sendConfig(varp, 2);
                case SCYTHE_JAB -> player.getPacketSender().sendConfig(varp, 1);
                case SCYTHE_BLOCK -> player.getPacketSender().sendConfig(varp, 3);
            }
        } else {
            player.getPacketSender().sendConfig(varp, varpState);
        }

        //Save varp switch
        player.getCombat().getFightType().setParentId(varp);
        player.getCombat().getFightType().setChildId(varpState);
    }

    public static boolean changeCombatSettings(Player player, int button) {
        Item staff = player.getEquipment().get(EquipSlot.WEAPON);
        switch (button) {
            // shortbow & longbow
            case 1772 -> {
                player.getCombat().setFightType(FightType.ARROW_ACCURATE);
                return true;
            }
            case 1771 -> {
                player.getCombat().setFightType(FightType.ARROW_RAPID);
                return true;
            }
            case 1770 -> {
                player.getCombat().setFightType(FightType.ARROW_LONGRANGE);
                return true;
            }
            // dagger & sword
            case 2282 -> {
                if (player.getCombat().getWeaponType() == WeaponType.DAGGER) {
                    player.getCombat().setFightType(FightType.DAGGER_STAB);
                } else if (player.getCombat().getWeaponType() == WeaponType.SWORD) {
                    player.getCombat().setFightType(FightType.SWORD_STAB);
                }
                return true;
            }
            case 2285 -> {
                if (player.getCombat().getWeaponType() == WeaponType.DAGGER) {
                    player.getCombat().setFightType(FightType.DAGGER_LUNGE);
                } else if (player.getCombat().getWeaponType() == WeaponType.SWORD) {
                    player.getCombat().setFightType(FightType.SWORD_LUNGE);
                }
                return true;
            }
            case 2284 -> {
                if (player.getCombat().getWeaponType() == WeaponType.DAGGER) {
                    player.getCombat().setFightType(FightType.DAGGER_SLASH);
                } else if (player.getCombat().getWeaponType() == WeaponType.SWORD) {
                    player.getCombat().setFightType(FightType.SWORD_SLASH);
                }
                return true;
            }
            case 2283 -> {
                if (player.getCombat().getWeaponType() == WeaponType.DAGGER) {
                    player.getCombat().setFightType(FightType.DAGGER_BLOCK);
                } else if (player.getCombat().getWeaponType() == WeaponType.SWORD) {
                    player.getCombat().setFightType(FightType.SWORD_BLOCK);
                }
                return true;
            }
            // scimitar & longsword
            case 2429 -> {
                if (player.getCombat().getWeaponType() == WeaponType.LONGSWORD) {
                    player.getCombat().setFightType(FightType.LONGSWORD_CHOP);
                }
                return true;
            }
            case 2432 -> {
                if (player.getCombat().getWeaponType() == WeaponType.LONGSWORD) {
                    player.getCombat().setFightType(FightType.LONGSWORD_SLASH);
                }
                return true;
            }
            case 2431 -> {
                if (player.getCombat().getWeaponType() == WeaponType.LONGSWORD) {
                    player.getCombat().setFightType(FightType.LONGSWORD_LUNGE);
                }
                return true;
            }
            case 2430 -> {
                if (player.getCombat().getWeaponType() == WeaponType.LONGSWORD) {
                    player.getCombat().setFightType(FightType.LONGSWORD_BLOCK);
                }
                return true;
            }
            // mace
            case 3802 -> {
                player.getCombat().setFightType(FightType.MACE_POUND);
                return true;
            }
            case 3805 -> {
                player.getCombat().setFightType(FightType.MACE_PUMMEL);
                return true;
            }
            case 3804 -> {
                player.getCombat().setFightType(FightType.MACE_SPIKE);
                return true;
            }
            case 3803 -> {
                player.getCombat().setFightType(FightType.MACE_BLOCK);
                return true;
            }
            // knife, thrownaxe, dart & javelin
            case 4454 -> {
                player.getCombat().setFightType(FightType.THROWING_ACCURATE);
                return true;
            }
            case 4453 -> {
                player.getCombat().setFightType(FightType.THROWING_RAPID);
                return true;
            }
            case 4452 -> {
                player.getCombat().setFightType(FightType.THROWING_LONGRANGE);
                return true;
            }
            // spear
            case 4685 -> {
                player.getCombat().setFightType(FightType.SPEAR_LUNGE);
                return true;
            }
            case 4688 -> {
                player.getCombat().setFightType(FightType.SPEAR_SWIPE);
                return true;
            }
            case 4687 -> {
                player.getCombat().setFightType(FightType.SPEAR_POUND);
                return true;
            }
            case 4686 -> {
                player.getCombat().setFightType(FightType.SPEAR_BLOCK);
                return true;
            }
            // 2h sword
            case 4711 -> {
                player.getCombat().setFightType(FightType.TWOHANDEDSWORD_CHOP);
                return true;
            }
            case 4714 -> {
                player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SLASH);
                return true;
            }
            case 4713 -> {
                player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SMASH);
                return true;
            }
            case 4712 -> {
                player.getCombat().setFightType(FightType.TWOHANDEDSWORD_BLOCK);
                return true;
            }
            // pickaxe
            case 5576 -> {
                player.getCombat().setFightType(FightType.PICKAXE_SPIKE);
                return true;
            }
            case 5579 -> {
                player.getCombat().setFightType(FightType.PICKAXE_IMPALE);
                return true;
            }
            case 5578 -> {
                player.getCombat().setFightType(FightType.PICKAXE_SMASH);
                return true;
            }
            case 5577 -> {
                player.getCombat().setFightType(FightType.PICKAXE_BLOCK);
                return true;
            }
            // claws
            case 7768 -> {
                player.getCombat().setFightType(FightType.CLAWS_CHOP);
                return true;
            }
            case 7771 -> {
                player.getCombat().setFightType(FightType.CLAWS_SLASH);
                return true;
            }
            case 7770 -> {
                player.getCombat().setFightType(FightType.CLAWS_LUNGE);
                return true;
            }
            case 7769 -> {
                player.getCombat().setFightType(FightType.CLAWS_BLOCK);
                return true;
            }
            // halberd
            case 8466 -> {
                player.getCombat().setFightType(FightType.HALBERD_JAB);
                return true;
            }
            case 8468 -> {
                player.getCombat().setFightType(FightType.HALBERD_SWIPE);
                return true;
            }
            case 8467 -> {
                player.getCombat().setFightType(FightType.HALBERD_FEND);
                return true;
            }
            // unarmed
            case 5861 -> {
                player.getCombat().setFightType(FightType.UNARMED_BLOCK);
                return true;
            }
            case 5862 -> {
                player.getCombat().setFightType(FightType.UNARMED_KICK);
                return true;
            }
            case 5860 -> {
                player.getCombat().setFightType(FightType.UNARMED_PUNCH);
                return true;
            }
            // whip
            case 12298 -> {
                player.getCombat().setFightType(FightType.WHIP_FLICK);
                return true;
            }
            case 12297 -> {
                player.getCombat().setFightType(FightType.WHIP_LASH);
                return true;
            }
            case 12296 -> {
                player.getCombat().setFightType(FightType.WHIP_DEFLECT);
                return true;
            }

            case 24502 -> {
                player.getCombat().setFightType(FightType.POWERED_STAFF_ACCURATE_1);
                return true;
            }
            case 24501 -> {
                player.getCombat().setFightType(FightType.POWERED_STAFF_ACCURATE_2);
                return true;
            }
            case 24500 -> {
                player.getCombat().setFightType(FightType.POWERED_STAFF_LONGRANGE);
                return true;
            }

            case 336 -> {
                if (Autocasting.SPECIAL_AUTOCAST_STAFFS.contains(staff.getId())) {
                    return false;
                }
                if (player.getAttribOr(AttributeKey.AUTOCAST_SELECTED, false) != null) {
                    Autocasting.setAutocast(player, null);
                }
                player.getCombat().setFightType(FightType.STAFF_BASH);
                return true;
            }
            case 335 -> {
                if (Autocasting.SPECIAL_AUTOCAST_STAFFS.contains(staff.getId())) {
                    return false;
                }
                if (player.getAttribOr(AttributeKey.AUTOCAST_SELECTED, false) != null) {
                    Autocasting.setAutocast(player, null);
                }
                player.getCombat().setFightType(FightType.STAFF_POUND);
                return true;
            }

            case 334 -> {
                if (Autocasting.SPECIAL_AUTOCAST_STAFFS.contains(staff.getId())) {
                    return false;
                }
                if (player.getAttribOr(AttributeKey.AUTOCAST_SELECTED, false) != null) {
                    Autocasting.setAutocast(player, null);
                }
                player.getCombat().setFightType(FightType.STAFF_FOCUS);
                return true;
            }
            // warhammer
            case 433 -> {
                player.getCombat().setFightType(FightType.HAMMER_POUND);
                return true;
            }
            case 432 -> {
                player.getCombat().setFightType(FightType.HAMMER_PUMMEL);
                return true;
            }
            case 431 -> {
                player.getCombat().setFightType(FightType.HAMMER_BLOCK);
                return true;
            }
            // scythe
            case 782 -> {
                player.getCombat().setFightType(FightType.SCYTHE_REAP);
                return true;
            }
            case 784 -> {
                player.getCombat().setFightType(FightType.SCYTHE_CHOP);
                return true;
            }
            case 785 -> {
                player.getCombat().setFightType(FightType.SCYTHE_JAB);
                return true;
            }
            case 783 -> {
                player.getCombat().setFightType(FightType.SCYTHE_BLOCK);
                return true;
            }
            // battle axe
            case 1704 -> {
                player.getCombat().setFightType(FightType.BATTLEAXE_CHOP);
                return true;
            }
            case 1707 -> {
                player.getCombat().setFightType(FightType.BATTLEAXE_HACK);
                return true;
            }
            case 1706 -> {
                player.getCombat().setFightType(FightType.BATTLEAXE_SMASH);
                return true;
            }
            case 1705 -> {
                player.getCombat().setFightType(FightType.BATTLEAXE_BLOCK);
                return true;
            }
        }
        return false;
    }
}
