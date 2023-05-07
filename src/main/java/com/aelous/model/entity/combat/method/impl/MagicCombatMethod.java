package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.data.AncientSpells;
import com.aelous.model.entity.combat.magic.data.AutoCastWeaponSpells;
import com.aelous.model.entity.combat.magic.data.ModernSpells;
import com.aelous.model.entity.combat.magic.impl.CombatEffectSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;

import java.util.Objects;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Represents the combat method for magic attacks.
 *
 * @author Professor Oak
 */
public class MagicCombatMethod extends CommonCombatMethod {


    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        try {
            CombatSpell spell = entity.getCombat().getCastSpell();
            if (spell == null) {
                spell = entity.getCombat().getAutoCastSpell();
                if (spell == null) {
                    spell = entity.getCombat().getPoweredStaffSpell();
                }
            }

            if (spell != null) {
                int spellID = spell.spellId();

                boolean modernSpellbook = entity.getAsPlayer().getSpellbook() == MagicSpellbook.NORMAL;
                boolean ancientSpellbook = entity.getAsPlayer().getSpellbook() == MagicSpellbook.ANCIENT;
                boolean isWearingPoweredStaff = entity.getAsPlayer().getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER, ACCURSED_SCEPTRE_A);
                boolean canCast = spell.canCast(entity.getAsPlayer(), target, true);

                int projectile = -1;
                int startgraphic = -1;
                int castAnimation = -1;
                int startSpeed = -1;
                int startHeight = -1;
                int endHeight = -1;
                int endGraphic = -1;
                int stepMultiplier = -1;
                int duration = -1;

                int distance = entity.tile().getChevDistance(target.tile());

                if (canCast && !target.dead() && !entity.dead() && entity instanceof Player player) {
                    GraphicHeight startGraphicHeight = GraphicHeight.HIGH;
                    GraphicHeight endGraphicHeight = GraphicHeight.HIGH;
                    ModernSpells findProjectileDataModern = ModernSpells.findSpellProjectileData(spellID, endGraphicHeight);
                    AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellID, startGraphicHeight, endGraphicHeight);
                    AutoCastWeaponSpells findAutoCastWeaponsData = AutoCastWeaponSpells.findSpellProjectileData(spellID, endGraphicHeight);

                    if (findProjectileDataModern != null && modernSpellbook && player.getCombat().getCastSpell() != null && player.getCombat().getCastSpell().spellId() == findProjectileDataModern.spellID) {
                        projectile = findProjectileDataModern.projectile;
                        startgraphic = findProjectileDataModern.startGraphic;
                        castAnimation = findProjectileDataModern.castAnimation;
                        startSpeed = findProjectileDataModern.startSpeed;
                        startHeight = findProjectileDataModern.startHeight;
                        endHeight = findProjectileDataModern.endHeight;
                        endGraphic = findProjectileDataModern.endGraphic;
                        stepMultiplier = findProjectileDataModern.stepMultiplier;
                        duration = (startSpeed + -5 + (stepMultiplier * distance));
                        endGraphicHeight = findProjectileDataModern.endGraphicHeight;
                    } else if (findProjectileDataAncients != null && ancientSpellbook && player.getCombat().getCastSpell() != null && player.getCombat().getCastSpell().spellId() == findProjectileDataAncients.spellID) {
                        projectile = findProjectileDataAncients.projectile;
                        startgraphic = findProjectileDataAncients.startGraphic;
                        castAnimation = findProjectileDataAncients.castAnimation;
                        startSpeed = findProjectileDataAncients.startSpeed;
                        startHeight = findProjectileDataAncients.startHeight;
                        endHeight = findProjectileDataAncients.endHeight;
                        endGraphic = findProjectileDataAncients.endGraphic;
                        stepMultiplier = findProjectileDataAncients.stepMultiplier;
                        duration = (startSpeed + -5 + (stepMultiplier * distance));
                        endGraphicHeight = findProjectileDataAncients.endGraphicHeight;
                    } else if (isWearingPoweredStaff && findAutoCastWeaponsData != null && player.getCombat().getPoweredStaffSpell() != null && player.getCombat().getPoweredStaffSpell().spellId() == findAutoCastWeaponsData.spellID) {
                        projectile = findAutoCastWeaponsData.projectile;
                        startgraphic = findAutoCastWeaponsData.startGraphic;
                        castAnimation = findAutoCastWeaponsData.castAnimation;
                        startSpeed = findAutoCastWeaponsData.startSpeed;
                        startHeight = findAutoCastWeaponsData.startHeight;
                        endHeight = findAutoCastWeaponsData.endHeight;
                        endGraphic = findAutoCastWeaponsData.endGraphic;
                        stepMultiplier = findAutoCastWeaponsData.stepMultiplier;
                        duration = (startSpeed + -5 + (stepMultiplier * distance));
                        endGraphicHeight = findAutoCastWeaponsData.endGraphicHeight;
                    }

                    entity.animate(new Animation(castAnimation));
                    entity.performGraphic(new Graphic(startgraphic, startGraphicHeight, 0));

                    Projectile p = new Projectile(entity, target, projectile, startSpeed, duration, startHeight, endHeight, 0, target.getSize(), stepMultiplier);

                    final int delay = entity.executeProjectile(p);

                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
                    hit.submit();

                    if (hit.isAccurate()) {
                        target.performGraphic(new Graphic(endGraphic, endGraphicHeight, p.getSpeed()));
                    } else {
                        target.performGraphic(new Graphic(85, GraphicHeight.LOW, p.getSpeed()));
                    }

                    if (spell instanceof CombatEffectSpell combatEffectSpell && hit.isAccurate()) {
                        combatEffectSpell.whenSpellCast(entity, target);
                        combatEffectSpell.spellEffect(entity, target, hit);
                    }

                    spell.finishCast(entity, target, hit.isAccurate(), hit.getDamage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        CombatSpell spell = entity.getCombat().getCastSpell() != null ? entity.getCombat().getCastSpell() : entity.getCombat().getAutoCastSpell() != null ? entity.getCombat().getAutoCastSpell() : entity.getCombat().getPoweredStaffSpell() != null ? entity.getCombat().getPoweredStaffSpell() : null;
        if (spell != null) {
            return spell.getAttackSpeed(entity);
        }
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            if (player.getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER, ACCURSED_SCEPTRE_A)) {
                return 8;
            }
        }
        return 10;
    }

    @Override
    public void postAttack() {
        boolean spellWeapon = entity.getCombat().getCastSpell() == CombatSpells.ELDRITCH_NIGHTMARE_STAFF.getSpell() || entity.getCombat().getCastSpell() == CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.TRIDENT_OF_THE_SEAS.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.SANGUINESTI_STAFF.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.TUMEKENS_SHADOW.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.ACCURSED_SCEPTRE.getSpell();
        if (entity.getCombat().getAutoCastSpell() == null && !spellWeapon) {
            entity.getCombat().reset();
        }
        entity.setEntityInteraction(target);
        entity.getCombat().setCastSpell(null);
    }
}
