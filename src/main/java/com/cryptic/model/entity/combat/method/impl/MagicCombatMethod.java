package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.magic.data.AncientSpells;
import com.cryptic.model.entity.combat.magic.data.AutoCastWeaponSpells;
import com.cryptic.model.entity.combat.magic.data.ModernSpells;
import com.cryptic.model.entity.combat.magic.impl.CombatEffectSpell;
import com.cryptic.model.entity.combat.magic.spells.CombatSpells;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin
 */
public class MagicCombatMethod extends CommonCombatMethod {
    public static final int[] immune_to_magic = new int[]{NpcIdentifiers.NYLOCAS_TOXOBOLOS_8343, NpcIdentifiers.NYLOCAS_TOXOBOLOS_8346, NpcIdentifiers.NYLOCAS_VASILIAS_8357, NYLOCAS_ISCHYROS_8342, NYLOCAS_ISCHYROS_8345, NYLOCAS_VASILIAS_8355};

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        Player player = (Player) entity;

        CombatSpell spell = player.getCombat().getCastSpell();

        if (spell == null) {

            spell = player.getCombat().getAutoCastSpell();

            if (spell == null) {

                spell = player.getCombat().getPoweredStaffSpell();

            }

        }

//        LogManager.getLogger("dev").info("spell {}", spell);

        if (spell == null) {
            return false;
        }

        int spellId = spell.spellId();

        boolean modernSpells = player.getSpellbook() == MagicSpellbook.NORMAL;
        boolean ancientSpells = player.getSpellbook() == MagicSpellbook.ANCIENTS;
        boolean isWearingPoweredStaff = player.getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER, ACCURSED_SCEPTRE_A, CORRUPTED_TUMEKENS_SHADOW);
        boolean canCast = spell.canCast(player, target, true);
        boolean hasTumeken = player.getEquipment().containsAny(TUMEKENS_SHADOW, CORRUPTED_TUMEKENS_SHADOW);

        int projectile = -1;
        int startgraphic = -1;
        int castAnimation = -1;
        int startSpeed = -1;
        int startHeight = -1;
        int endHeight = -1;
        int endGraphic = -1;
        int stepMultiplier = -1;
        int duration = -1;
        int curve = 16;

        int distance = player.tile().getChevDistance(target.tile());

        if (!canCast || target.dead() || player.dead()) {
            return false;
        }

        GraphicHeight startGraphicHeight = (hasTumeken && spell.spellId() == 6) ? GraphicHeight.LOW : GraphicHeight.HIGH;
        GraphicHeight endGraphicHeight = GraphicHeight.HIGH;
        ModernSpells findProjectileDataModern = ModernSpells.findSpellProjectileData(spellId, endGraphicHeight);
        AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellId, startGraphicHeight, endGraphicHeight);
        AutoCastWeaponSpells findAutoCastWeaponsData = AutoCastWeaponSpells.findSpellProjectileData(spellId, endGraphicHeight);

        if (findProjectileDataModern != null && modernSpells && spell.spellId() == findProjectileDataModern.spellID) {
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
        } else if (findProjectileDataAncients != null && ancientSpells && spell.spellId() == findProjectileDataAncients.spellID) {
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
        } else if (isWearingPoweredStaff && findAutoCastWeaponsData != null && spell.spellId() == findAutoCastWeaponsData.spellID) {
            projectile = findAutoCastWeaponsData.projectile;
            startgraphic = findAutoCastWeaponsData.startGraphic;
            castAnimation = findAutoCastWeaponsData.castAnimation;
            startSpeed = findAutoCastWeaponsData.startSpeed;
            startHeight = findAutoCastWeaponsData.startHeight;
            endHeight = findAutoCastWeaponsData.endHeight;
            endGraphic = findAutoCastWeaponsData.endGraphic;
            stepMultiplier = findAutoCastWeaponsData.stepMultiplier;
            duration = (hasTumeken && spell.spellId() == 6) ? (startSpeed + 10 + (stepMultiplier * distance)) : (startSpeed + -5 + (stepMultiplier * distance));
            endGraphicHeight = findAutoCastWeaponsData.endGraphicHeight;
        }

        var sound = World.getWorld().getSoundLoader();
        if (sound != null) {
            var soundInfo = sound.getSpellInfo(spellId);
            if (soundInfo != null) {
                player.sendPrivateSound(soundInfo.getCastSound(), 0);
            }
        }
        player.animate(new Animation(castAnimation, Priority.HIGH));
        player.performGraphic(new Graphic(startgraphic, startGraphicHeight, 0));

        var source = spell.spellId() == AncientSpells.ICE_BARRAGE.spellID ? target.tile() : player.tile();

        if (spell.spellId() == AncientSpells.ICE_BARRAGE.spellID) {
            projectile = 368;
            curve = 0;
            startHeight = 0;
            endHeight = 0;
        }

        Projectile p = new Projectile(source, target, projectile, startSpeed, duration, startHeight, endHeight, curve, entity.getSize(), stepMultiplier);

        final int delay = player.executeProjectile(p);

        Hit hit = new Hit(entity, target, delay, this);

        if (isImmune(target, hit)) {
            if (spell instanceof CombatEffectSpell combatEffectSpell) combatEffectSpell.whenSpellCast(player, target, spellId);
            spell.finishCast(player, target, hit.isAccurate(), hit.getDamage());
            return true;
        }

        hit.checkAccuracy(true).submit();

        if (sound != null) {
            var soundInfo = sound.getSpellInfo(spellId);
            if (soundInfo != null) {
                if (hit.isAccurate()) {
                    player.sendPublicSound(soundInfo.getHitSound(), (int) (hit.getDelay() * 30D));
                }
            }
        }

        if (hit.isAccurate()) {
            target.performGraphic(new Graphic(endGraphic, endGraphicHeight, p.getSpeed()));
            if (spell instanceof CombatEffectSpell combatEffectSpell) {
                combatEffectSpell.whenSpellCast(player, target, spellId);
                combatEffectSpell.spellEffect(player, target, hit);
            }
        } else {
            target.performGraphic(new Graphic(85, GraphicHeight.HIGH, p.getSpeed()));
        }

        spell.finishCast(player, target, hit.isAccurate(), hit.getDamage());
        return true;
    }

    private boolean isImmune(Entity target, Hit hit) {
        if (target instanceof NPC npc) {
            if (ArrayUtils.contains(immune_to_magic, npc.id())) {
                hit.checkAccuracy(false).block().submit();
                hit.setImmune(true);
                return true;
            }
        }
        return false;
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
    public int moveCloseToTargetTileRange(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            if (player.getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER, ACCURSED_SCEPTRE_A, CORRUPTED_TUMEKENS_SHADOW)) {
                return 8;
            }
        }
        return 10;
    }

    @Override
    public void postAttack() {
        boolean spellWeapon = entity.getCombat().getCastSpell() == CombatSpells.ELDRITCH_NIGHTMARE_STAFF.getSpell() || entity.getCombat().getCastSpell() == CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.TRIDENT_OF_THE_SEAS.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.SANGUINESTI_STAFF.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.TUMEKENS_SHADOW.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.DAWNBRINGER.getSpell() || entity.getCombat().getPoweredStaffSpell() == CombatSpells.ACCURSED_SCEPTRE.getSpell();
        if (entity.getCombat().getAutoCastSpell() == null && !spellWeapon) {
            entity.getCombat().reset();
        }
        entity.setEntityInteraction(target);
        entity.getCombat().setCastSpell(null);
    }
}
