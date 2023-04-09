package com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Area;
import com.aelous.utility.Utils;

public class KreeArra extends CommonCombatMethod {

    public static boolean isMinion(NPC n) {
        return n.id() >= 3164 && n.id() <= 3163;
    }

    private static final Area ENCAMPMENT = new Area(2823, 5295, 2843, 5309);

    public static Area getENCAMPMENT() {
        return ENCAMPMENT;
    }

    private static Entity lastBossDamager = null;

    public static Entity getLastBossDamager() {
        return lastBossDamager;
    }

    public static void setLastBossDamager(Entity lastBossDamager) {
        KreeArra.lastBossDamager = lastBossDamager;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        int roll = Utils.random(2);
        int melee_distance = entity.tile().distance(target.tile());
        boolean melee_range = melee_distance <= 1;
        var tileDist = entity.tile().distance(target.tile());
        int duration = (43 + 11 + (5 * tileDist));
        int durationMagic = (51 + -5 + (10 * tileDist));
        if (melee_range && roll == 0) {
            entity.animate(6981);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
        } else if (roll == 1) {
            entity.animate(6980);
            Projectile p = new Projectile(entity, target, 1200, 51, durationMagic, 0, 0, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
        } else {
            entity.animate(6980);
            Projectile p = new Projectile(entity, target, 1199, 43, duration, 0, 0, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
