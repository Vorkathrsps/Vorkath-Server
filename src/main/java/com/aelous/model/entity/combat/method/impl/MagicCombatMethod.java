package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.data.AncientSpells;
import com.aelous.model.entity.combat.magic.data.ModernSpells;
import com.aelous.model.entity.combat.magic.impl.CombatEffectSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.masks.impl.graphics.Priority;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;

import java.util.*;
import java.util.stream.IntStream;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Represents the combat method for magic attacks.
 *
 * @author Professor Oak
 */
public class MagicCombatMethod extends CommonCombatMethod {


    private static final String MODERN = "./data/combat/magic/modern.toml";
    private static final String ANCIENTS = "./data/combat/magic/ancients.toml";


    @Override
    public void prepareAttack(Entity entity, Entity target) {
        try {
            CombatSpell spell = entity.getCombat().getCastSpell() != null ? entity.getCombat().getCastSpell() : entity.getCombat().getAutoCastSpell();
            IntStream dataStore = Arrays.stream(new int[]{Integer.parseInt(String.valueOf(spell.spellId()))});

            boolean canCast = spell.canCast(entity.getAsPlayer(), target, true);
            boolean modernSpellbook = entity.getAsPlayer().getSpellbook() == MagicSpellbook.NORMAL;
            boolean ancientSpellbook = entity.getAsPlayer().getSpellbook() == MagicSpellbook.ANCIENT;
            //final var list = parseMagicDataModerns;
            var spellID = spell.spellId();
            var projectileObject = new Object() {
                int projectile;
                int startgraphic;
                int castAnimation;
                int startSpeed;
                int startHeight;
                int endHeight;
                int endGraphic;
                int stepMultiplier;
                int duration;
            };

            int distance = entity.tile().getChevDistance(target.tile());

            GraphicHeight startGraphicHeight = GraphicHeight.HIGH;
            GraphicHeight endGraphicHeight = GraphicHeight.HIGH;
            ModernSpells findProjectileDataModern = ModernSpells.findSpellProjectileData(spellID, endGraphicHeight);
            AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellID, startGraphicHeight, endGraphicHeight);

            if (!target.dead() && !entity.dead()) {
                if (canCast) {
                    if (modernSpellbook) {
                        if (findProjectileDataModern != null) {
                            if (dataStore.anyMatch(f -> f == findProjectileDataModern.spellID)) {
                                projectileObject.projectile = (findProjectileDataModern.projectile);
                                projectileObject.startgraphic = (findProjectileDataModern.startGraphic);
                                projectileObject.castAnimation = (findProjectileDataModern.castAnimation);
                                projectileObject.startSpeed = (findProjectileDataModern.startSpeed);
                                projectileObject.startHeight = (findProjectileDataModern.startHeight);
                                projectileObject.endHeight = (findProjectileDataModern.endHeight);
                                projectileObject.endGraphic = (findProjectileDataModern.endGraphic);
                                projectileObject.stepMultiplier = (findProjectileDataModern.stepMultiplier);
                                projectileObject.duration = (projectileObject.startSpeed + -5 + (projectileObject.stepMultiplier * distance));
                                endGraphicHeight = findProjectileDataModern.endGraphicHeight;
                            }
                        }
                    }
                }
                if (ancientSpellbook) {
                    if (findProjectileDataAncients != null) {
                        if (dataStore.anyMatch(f -> f == findProjectileDataAncients.spellID)) {
                            projectileObject.projectile = (findProjectileDataAncients.projectile);
                            projectileObject.startgraphic = (findProjectileDataAncients.startGraphic);
                            projectileObject.castAnimation = (findProjectileDataAncients.castAnimation);
                            projectileObject.startSpeed = (findProjectileDataAncients.startSpeed);
                            projectileObject.startHeight = (findProjectileDataAncients.startHeight);
                            projectileObject.endHeight = (findProjectileDataAncients.endHeight);
                            projectileObject.endGraphic = (findProjectileDataAncients.endGraphic);
                            projectileObject.stepMultiplier = (findProjectileDataAncients.stepMultiplier);
                            projectileObject.duration = (projectileObject.startSpeed + -5 + (projectileObject.stepMultiplier * distance));
                            endGraphicHeight = findProjectileDataAncients.endGraphicHeight;
                        }
                    }
                }

                entity.animate(new Animation(projectileObject.castAnimation));

                entity.performGraphic(new Graphic(projectileObject.startgraphic, startGraphicHeight, 0));

                Projectile p = new Projectile(entity, target, projectileObject.projectile, projectileObject.startSpeed, projectileObject.duration, projectileObject.startHeight, projectileObject.endHeight, 0, target.getSize(), projectileObject.stepMultiplier);

                final int delay = entity.executeProjectile(p);

                Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

                hit.submit();

                if (hit.isAccurate()) {
                    target.performGraphic(new Graphic(projectileObject.endGraphic, endGraphicHeight, p.getSpeed()));
                } else {
                    target.performGraphic(new Graphic(85, GraphicHeight.LOW, p.getSpeed(), Priority.HIGH));
                }
                if (spell instanceof CombatEffectSpell) {
                    if (hit.isAccurate()) {
                        CombatEffectSpell combatEffectSpell = (CombatEffectSpell) spell;
                        combatEffectSpell.whenSpellCast(entity, target);
                        combatEffectSpell.spellEffect(entity, target, hit);
                    }
                }
                spell.finishCast(entity, target, hit.isAccurate(), hit.getDamage());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
