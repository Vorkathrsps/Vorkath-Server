package com.cryptic.model.entity.combat.magic;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.magic.spells.Spell;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * A {@link Spell} implementation used for combat related spells.
 *
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

    @Override
    public void cast(Entity cast, Entity castOn) {
    }


    public int getAttackSpeed(Entity attacker) {
        int speed = 5;
        if (attacker.isPlayer()) {

            Player player = (Player) attacker;

            if (player.getEquipment().hasAt(EquipSlot.WEAPON, HARMONISED_NIGHTMARE_STAFF) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || player.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF) || player.getEquipment().hasAt(EquipSlot.WEAPON, HOLY_SANGUINESTI_STAFF)) {
                speed = 4;
            }
        }
        return speed;
    }

    public abstract String name();

    public abstract int spellId();

    public abstract int baseMaxHit();

    public abstract MagicSpellbook spellbook();

    public abstract void finishCast(Entity cast, Entity castOn, boolean accurate, int damage);

    @Override
    public String toString() {
        return "CombatSpell{"+name()+", "+spellbook()+" "+spellId()+"}";
    }
}
