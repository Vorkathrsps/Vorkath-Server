package com.aelous.model.content.sound;

import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

/**
 * Created by Situations on 2/8/2016.
 */
public class CombatSounds {

    public static int weapon_attack_sounds(Player player) {
        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

        WeaponType byInterfaceType = player.getCombat().getWeaponType();
        if (weapon == null) {
            return -1;
        }
        // Special cases that don't match the below
        switch (weapon.getId()) {
        // Godswords
        case 11802:
        case 20593:
        case 20368:
        case 11804:
        case 20370:
        case 11806:
        case 20372:
        case 11808:
        case 20374:
            return 3846;
            
        // Wands
        case 6908:
        case 6910:
        case 6912:
        case 6914:
        case 10150:
        case 11012:
        case 12422:
            return 2563;

        // Misc items
        case ItemIdentifiers.GRANITE_MAUL_24225:
            return 2714;
        case 4726:
            return 1328;
        case 4755:
            return 1323;
        case 4747:
            return 1332;
        case 4718:
            return 1321;
        case 6528:
            return 2520;

        }
        
        // Fallback cases
        
        switch(byInterfaceType) {
        case LONGSWORD:
            return 2500;
        case DAGGER:
            return 2517;
        case PICKAXE:
            return 2498;
            case MAGIC_STAFF:
            return 2555;
        case AXE:
            return 2498;
        case MACE:
            return 2508; 
        case HAMMER:
            return 2567;
        case CROSSBOW:
            return 2695;
        case BOW:
            return 2693;
        case THROWN:
            return 2696;
        case WHIP:
            return 2720;
        case CLAWS:
            break;
        case DINHS_BULWARK:
            break;
        case GHRAZI_RAPIER:
            break;
        case HALBERD:
            break;
        case SALAMANDER:
            break;
        case SCYTHE:
            break;
        case SPEAR:
            break;
        case SWORD:
            break;
        case TWOHANDED:
            break;
        case UNARMED:
            break;
        default:
            break;
        }
        return -1;
    }

}
