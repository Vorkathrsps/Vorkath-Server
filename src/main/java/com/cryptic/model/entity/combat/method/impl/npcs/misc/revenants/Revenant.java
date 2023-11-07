package com.cryptic.model.entity.combat.method.impl.npcs.misc.revenants;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.Utils;

/**
 * @author Origin | Zerikoth
 * <p>
 * Revenants use all three forms of attack.
 * Their attacks have very high if not 100% accuracy, and will often deal high damage.
 * They will react to a player's overhead prayers and defensive bonuses.
 * By default, all revenants attack with Magic, but can quickly adapt based on the player's defensive bonuses and prayers.
 */
public class Revenant extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }

        NPC npc = (NPC) entity;

        if (npc.hp() < npc.maxHp() / 2) {
            if (Utils.percentageChance(50)) {
                npc.graphic(1221);
                npc.heal(npc.maxHp() / 3);
            }
        }

        if (Utils.percentageChance(50) && withinDistance(1)) {
            meleeAttack(npc, target);
        } else if (Utils.percentageChance(50)) {
            magicAttack(npc, target);
        } else {
            rangedAttack(npc, target);
        }

        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

    private void meleeAttack(NPC npc, Entity target) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy(true).submit();
        npc.animate(npc.attackAnimation());
    }

    private void rangedAttack(NPC npc, Entity target) {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 206, 41, duration, 43, 31, 16, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        npc.animate(npc.attackAnimation());
        Hit hit = target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true);
        hit.submit();
    }

    private void magicAttack(NPC npc, Entity target) {
        npc.animate(npc.attackAnimation());
        var tileDist = npc.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(npc, target, 1415, 51, duration, 43, 31, 16, target.getSize(), 10);
        final int delay = npc.executeProjectile(p);
        int damage = CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC);
        target.hit(npc, damage, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        target.performGraphic(damage > 0 ? new Graphic(1454, GraphicHeight.HIGH, p.getSpeed()) : new Graphic(85, GraphicHeight.HIGH, p.getSpeed()));
        npc.freeze(8, target, false);
    }
}
