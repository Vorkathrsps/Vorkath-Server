package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Represents the combat method for magic attacks.
 *
 * @author Professor Oak
 */
public class MagicCombatMethod extends CommonCombatMethod {

    public static final Graphic SPLASH_GRAPHIC = new Graphic(85, GraphicHeight.MIDDLE);

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        CombatSpell spell = entity.getCombat().getCastSpell() != null ? entity.getCombat().getCastSpell() : entity.getCombat().getAutoCastSpell();

        if (spell == null) {
            entity.message("What spell is that?");
            return;
        }

        if (target != null && !target.dead() && !entity.dead()) {

            int delay = (int) (1 + Math.floor(1 + entity.tile().getChevDistance(target.tile()) / 3D));
            delay = (int) Math.min(Math.max(1.0 , delay), 5.0);

            // delete runes here using the canCast method. doesnt check canCast, that is already done before.
            spell.canCast(entity.getAsPlayer(), target, true);
            spell.startCast(entity, target);

            //Hit hit = target.hit(target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().spell(spell).postDamage(((MagicCombatMethod) CombatFactory.MAGIC_COMBAT)::handleAfterHit);

            Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().spell(spell).postDamage(((MagicCombatMethod) CombatFactory.MAGIC_COMBAT)::handleAfterHit);

            hit.submit();
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        CombatSpell spell = entity.getCombat().getCastSpell() != null ? entity.getCombat().getCastSpell() : entity.getCombat().getAutoCastSpell();
        if (spell != null) {
            return spell.getAttackSpeed(entity);
        }
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            //Trident of the seas and Trident of the swamp have a default range of 8, but also allow longrange attack style.
            if (player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP) || player.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
                return 8;
            }
        }
        //All combat magic spells have an attack range of 10 regardless of the level of the spell of which to cast it.
        return 10;
    }

    public void handleAfterHit(Hit hit) {
        Entity attacker = hit.getAttacker();
        Entity target = hit.getTarget();
        boolean accurate = hit.isAccurate();
        int damage = hit.getDamage();

        if (attacker.dead() || target.dead()) {
            return;
        }

        CombatSpell spell = hit.spell;

        if (spell != null) {
                if (accurate) {
                    spell.endGraphic().ifPresent(target::performGraphic);
                } else {
                    target.performGraphic(SPLASH_GRAPHIC);
                }

                spell.finishCast(attacker, target, accurate, damage);

            }
        }

    @Override
    public void postAttack() {
        boolean spellWeapon = entity.getCombat().getCastSpell() == CombatSpells.ELDRITCH_NIGHTMARE_STAFF.getSpell() || entity.getCombat().getCastSpell() == CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell();

        if (entity.getCombat().getAutoCastSpell() == null && !spellWeapon) {
            entity.getCombat().reset();// combat is stopped for magic when not autocasting. spell on entity is a 1-time attack.
        }
        entity.getCombat().setCastSpell(null);
    }
}
