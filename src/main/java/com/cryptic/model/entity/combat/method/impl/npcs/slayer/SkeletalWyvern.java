package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author PVE
 * @Since augustus 08, 2020
 */
public class SkeletalWyvern extends CommonCombatMethod {

    private enum AttackStyle {
        MELEE, RANGED, ICE_BREATH
    }

    private AttackStyle attackStyle = AttackStyle.MELEE;
    private final List<Integer> SHIELDS = Arrays.asList(ANCIENT_WYVERN_SHIELD, ANCIENT_WYVERN_SHIELD_21634, DRAGONFIRE_WARD, DRAGONFIRE_WARD_22003, MIND_SHIELD, ELEMENTAL_SHIELD, DRAGONFIRE_SHIELD, DRAGONFIRE_SHIELD_11284);
    private final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC};

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    private void jumpAttack(Entity entity, Entity target) {
        entity.animate(2989);
        entity.graphic(499);
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 500, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
    }

    private void rangedAttack(Entity entity, Entity target) {
        entity.animate(2985);
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 500, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.performGraphic(new Graphic(502, GraphicHeight.LOW, delay));
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
    }

    private void iceBreath(Entity entity, Entity target) {
        entity.animate(2988);
        entity.graphic(502);
        int maxDamage = 60;
        if (SHIELDS.contains(target.getAsPlayer().getEquipment().getId(EquipSlot.SHIELD))) {
            maxDamage = 10;
        } else {
            for (int skill : DRAIN) {
                target.getAsPlayer().getSkills().alterSkill(skill, -9);
            }
            target.getAsPlayer().message("The wyvern's ice breath drains your stats!");
        }
        if (Utils.rollDie(3, 1))
            target.freeze(3, entity);
        target.hit(entity, World.getWorld().random(maxDamage), CombatType.MAGIC).submit();
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        if (withinDistance(1) && Utils.rollDie(3, 2)) {
            attackStyle = AttackStyle.MELEE;
            basicAttack(entity, target);
        } else if (Utils.rollDie(5, 1)) {
            attackStyle = AttackStyle.ICE_BREATH;
            iceBreath(entity, target);
        } else if (Utils.rollDie(2, 1)) {
            attackStyle = AttackStyle.RANGED;
            rangedAttack(entity, target);
        } else {
            attackStyle = AttackStyle.RANGED;
            jumpAttack(entity, target);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return attackStyle == AttackStyle.MELEE ? 1 : attackStyle == AttackStyle.RANGED ? 6 : 5;
    }
}
