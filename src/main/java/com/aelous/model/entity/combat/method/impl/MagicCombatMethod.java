package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.data.AncientSpells;
import com.aelous.model.entity.combat.magic.data.ModernSpells;
import com.aelous.model.entity.combat.magic.data.SpellType;
import com.aelous.model.entity.combat.magic.impl.CombatEffectSpell;
import com.aelous.model.entity.combat.magic.impl.CombatNormalSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.masks.impl.graphics.Priority;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

import java.util.Arrays;
import java.util.Optional;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Represents the combat method for magic attacks.
 *
 * @author Professor Oak
 */
public class MagicCombatMethod extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        CombatSpell spell = entity.getCombat().getCastSpell() != null ? entity.getCombat().getCastSpell() : entity.getCombat().getAutoCastSpell();

        int projectile = 0, castAnimation = 0, startSpeed = 0, startHeight = 0, endHeight = 0, startGraphic = 0, endGraphic = 0, stepMultiplier = 0, duration = 0;
        int distance = entity.tile().getChevDistance(target.tile());

        var spellID = spell.spellId();

        GraphicHeight startGraphicHeight = GraphicHeight.HIGH;
        GraphicHeight endGraphicHeight = GraphicHeight.HIGH;
        ModernSpells findProjectileDataModern = ModernSpells.findSpellProjectileData(spellID, endGraphicHeight);
        AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellID, startGraphicHeight, endGraphicHeight);

        if (!target.dead() && !entity.dead()) {
            if (spell.canCast(entity.getAsPlayer(), target, true)) {
                if (entity.getAsPlayer().getSpellbook() == MagicSpellbook.NORMAL) {
                    if (findProjectileDataModern != null) {
                        switch (spell.spellId()) {
                            case 1152, 1154, 1156, 1158, 1160, 1163, 1169, 1172, 1175,
                                1181, 1166, 1177, 1190, 1191, 1192, 1183, 1185, 1188,
                                1189, 22644, 22658, 22628, 22608, 12445 -> {
                                projectile = findProjectileDataModern.projectile;
                                startGraphic = findProjectileDataModern.startGraphic;
                                castAnimation = findProjectileDataModern.castAnimation;
                                startSpeed = findProjectileDataModern.startSpeed;
                                startHeight = findProjectileDataModern.startHeight;
                                endHeight = findProjectileDataModern.endHeight;
                                endGraphic = findProjectileDataModern.endGraphic;
                                stepMultiplier = findProjectileDataModern.stepMultiplier;
                                duration = startSpeed + -5 + (stepMultiplier * distance);
                            }
                        }
                    }
                }
            }
            if (entity.getAsPlayer().getSpellbook() == MagicSpellbook.ANCIENT) {
                if (findProjectileDataAncients != null) {
                    switch (spell.spellId()) {
                        case 12939, 12987, 12901, 12861, 12963, 13011,
                            12919, 12881, 12951, 12999, 12911, 12871,
                            12975, 13023, 12929, 12891 -> {
                            projectile = findProjectileDataAncients.projectile;
                            startGraphic = findProjectileDataAncients.startGraphic;
                            castAnimation = findProjectileDataAncients.castAnimation;
                            startSpeed = findProjectileDataAncients.startSpeed;
                            startHeight = findProjectileDataAncients.startHeight;
                            endHeight = findProjectileDataAncients.endHeight;
                            endGraphic = findProjectileDataAncients.endGraphic;
                            stepMultiplier = findProjectileDataAncients.stepMultiplier;
                            duration = (startSpeed + -5 + (distance * stepMultiplier));
                            startGraphicHeight = findProjectileDataAncients.startGraphicheight;
                            endGraphicHeight = findProjectileDataAncients.endGraphicHeight;
                        }
                    }
                }
            }

            entity.animate(new Animation(castAnimation));

            entity.performGraphic(new Graphic(startGraphic, startGraphicHeight, 0));

            Projectile p = new Projectile(entity, target, projectile, startSpeed, duration, startHeight, endHeight, 0, target.getSize(), stepMultiplier);

            final int delay = entity.executeProjectile(p);

            Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

            hit.submit();

            if (hit.isAccurate()) {
                target.performGraphic(new Graphic(endGraphic, endGraphicHeight, p.getSpeed(), Priority.HIGH));
            } else {
                target.performGraphic(new Graphic(85, GraphicHeight.LOW, p.getSpeed(), Priority.HIGH));
            }
            spell.finishCast(entity, target, hit.isAccurate(), hit.getDamage());
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
            if (player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP) || player.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
                return 8;
            }
        }
        return 10;
    }

    @Override
    public void postAttack() {
        boolean spellWeapon = entity.getCombat().getCastSpell() == CombatSpells.ELDRITCH_NIGHTMARE_STAFF.getSpell() || entity.getCombat().getCastSpell() == CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell();

        if (entity.getCombat().getAutoCastSpell() == null && !spellWeapon) {
            entity.getCombat().reset();
        }
        entity.setEntityInteraction(target);
        entity.getCombat().setCastSpell(null);
    }
}
