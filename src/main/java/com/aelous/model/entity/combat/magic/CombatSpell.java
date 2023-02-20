package com.aelous.model.entity.combat.magic;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.data.AncientSpells;
import com.aelous.model.entity.combat.magic.data.ModernSpells;
import com.aelous.model.entity.combat.magic.impl.CombatEffectSpell;
import com.aelous.model.entity.combat.magic.impl.CombatNormalSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.magic.spells.Spell;
import com.aelous.model.entity.combat.method.impl.MagicCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * A {@link Spell} implementation used for combat related spells.
 *
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

    @Override
    public void startCast(Entity cast, Entity castOn) {
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
}
