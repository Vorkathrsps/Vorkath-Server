package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

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
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    private void jumpAttack(Entity entity, Entity target) {
        entity.animate(2989);
        entity.graphic(499);
        new Projectile(entity, target, 500, 50, entity.projectileSpeed(target), 15, 31, 0).sendProjectile();
        int delay = entity.getProjectileHitDelay(target);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
    }

    private void rangedAttack(Entity entity, Entity target) {
        entity.animate(2985);
        new Projectile(entity, target, 500, 50, entity.projectileSpeed(target), 15, 31, 0).sendProjectile();
        int delay = entity.getProjectileHitDelay(target);
        target.performGraphic(new Graphic(502, GraphicHeight.LOW, delay));
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
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
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollDie(3, 2)) {
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
    public int getAttackDistance(Entity entity) {
        return attackStyle == AttackStyle.MELEE ? 1 : attackStyle == AttackStyle.RANGED ? 6 : 5;
    }
}
