package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.data.AncientSpells;
import com.aelous.model.entity.combat.magic.data.ModernSpells;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.masks.impl.graphics.Priority;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Words;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.moandjiezana.toml.Toml;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import lombok.Value;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
            InputStream dataStreamModern = new FileInputStream(MODERN);
            Toml parseMagicDataModerns = new Toml().read(dataStreamModern);
            InputStream dataStreamAncients = new FileInputStream(ANCIENTS);
            Toml parseMagicDataAncients = new Toml().read(dataStreamAncients);
            CombatSpell spell = entity.getCombat().getCastSpell() != null ? entity.getCombat().getCastSpell() : entity.getCombat().getAutoCastSpell();
            int distance = entity.tile().getChevDistance(target.tile());

            AtomicInteger projectile = new AtomicInteger();
            AtomicInteger castAnimation = new AtomicInteger();
            AtomicInteger startSpeed = new AtomicInteger();
            AtomicInteger startHeight = new AtomicInteger();
            AtomicInteger endHeight = new AtomicInteger();
            AtomicInteger startGraphic = new AtomicInteger();
            AtomicInteger endGraphic = new AtomicInteger();
            AtomicInteger stepMultiplier = new AtomicInteger();
            AtomicInteger duration = new AtomicInteger();

            var spellID = spell.spellId();

            IntStream dataStore = Arrays.stream(new int[]{Integer.parseInt(String.valueOf(spell.spellId()))});

            List<Integer> spellIdentificationsModern = parseMagicDataModerns.getList("spellid");
            List<Integer> spellIdentificationsAncients = parseMagicDataAncients.getList("spellid");

            GraphicHeight startGraphicHeight = GraphicHeight.HIGH;
            final GraphicHeight[] endGraphicHeight = {GraphicHeight.HIGH};
            ModernSpells findProjectileDataModern = ModernSpells.findSpellProjectileData(spellID, endGraphicHeight[0]);
            AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellID, startGraphicHeight, endGraphicHeight[0]);

            if (!target.dead() && !entity.dead()) {
                if (spell.canCast(entity.getAsPlayer(), target, true)) {
                    if (entity.getAsPlayer().getSpellbook() == MagicSpellbook.NORMAL) {
                        if (findProjectileDataModern != null) {
                            dataStore.forEach(key -> {
                                if (spellIdentificationsModern.stream().findAny().isPresent()) {
                                    projectile.set(findProjectileDataModern.projectile);
                                    startGraphic.set(findProjectileDataModern.startGraphic);
                                    castAnimation.set(findProjectileDataModern.castAnimation);
                                    startSpeed.set(findProjectileDataModern.startSpeed);
                                    startHeight.set(findProjectileDataModern.startHeight);
                                    endHeight.set(findProjectileDataModern.endHeight);
                                    endGraphic.set(findProjectileDataModern.endGraphic);
                                    stepMultiplier.set(findProjectileDataModern.stepMultiplier);
                                    duration.set(startSpeed.get() + -5 + (stepMultiplier.get() * distance));
                                    endGraphicHeight[0] = findProjectileDataModern.endGraphicHeight;
                                }
                            });
                        }
                    }
                    if (entity.getAsPlayer().getSpellbook() == MagicSpellbook.ANCIENT) {
                        if (spellIdentificationsAncients.stream().findAny().isPresent()) {
                            if (findProjectileDataAncients != null) {
                                dataStore.forEach(key -> {
                                    if (spellIdentificationsModern.stream().findAny().isPresent()) {
                                        projectile.set(findProjectileDataAncients.projectile);
                                        startGraphic.set(findProjectileDataAncients.startGraphic);
                                        castAnimation.set(findProjectileDataAncients.castAnimation);
                                        startSpeed.set(findProjectileDataAncients.startSpeed);
                                        startHeight.set(findProjectileDataAncients.startHeight);
                                        endHeight.set(findProjectileDataAncients.endHeight);
                                        endGraphic.set(findProjectileDataAncients.endGraphic);
                                        stepMultiplier.set(findProjectileDataAncients.stepMultiplier);
                                        duration.set(startSpeed.get() + -5 + (stepMultiplier.get() * distance));
                                        endGraphicHeight[0] = findProjectileDataAncients.endGraphicHeight;
                                    }
                                });
                            }
                        }
                    }
                }
            }

            entity.animate(new Animation(castAnimation.get()));

            entity.performGraphic(new Graphic(startGraphic.get(), startGraphicHeight, 0));

            Projectile p = new Projectile(entity, target, projectile.get(), startSpeed.get(), duration.get(), startHeight.get(), endHeight.get(), 0, target.getSize(), stepMultiplier.get());

            final int delay = entity.executeProjectile(p);

            Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

            hit.submit();

            if (hit.isAccurate()) {
                target.performGraphic(new Graphic(endGraphic.get(), endGraphicHeight[0], p.getSpeed(), Priority.HIGH));
            } else {
                target.performGraphic(new Graphic(85, GraphicHeight.LOW, p.getSpeed(), Priority.HIGH));
            }
            spell.finishCast(entity, target, hit.isAccurate(), hit.getDamage());
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
