package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.model.content.mechanics.MultiwayCombat;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Skills;

/**
 * Handles Venenatis' combat.
 *
 * @author Professor Oak
 */
public class Venenatis extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        // Determine if we do a special hit, or a regular hit.
        if (World.getWorld().rollDie(14, 1)) {
            hurlWeb((NPC) entity, target);
        }

        // Determine if we're going to melee or mage
        if(CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
            entity.animate(entity.attackAnimation());
            //mob.forceChat("MELEE");
        } else {
            // Grab all players in a radius and do our magic projectile on them.
            World.getWorld().getPlayers().forEachInArea(entity.bounds(6), enemy -> {
                //If the target is currently in multi we..
                if (MultiwayCombat.includes(enemy.tile())) {
                    magicAttack(entity, enemy);
                    //mob.forceChat("MULTI PLAYER");
                } else if (enemy == target) {
                    //mob.forceChat("SINGLE PLAYER");
                    magicAttack(entity, enemy);
                }
            });
            // Do an animation..
            entity.animate(5322);
        }

        if (World.getWorld().rollDie(20, 1)) {
            drainPrayer(entity, target);
        }
        return true;
    }

    private void magicAttack(Entity npc, Entity target) {
        // Throw a magic projectile
        var tileDist = npc.tile().transform(3, 3, 0).distance(target.tile());
        new Projectile(npc, target,165, 20,12 * tileDist,30, 30, 0, true).sendProjectile();
        var delay = Math.max(1, (20 + (tileDist * 12)) / 30);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
    }

    private void hurlWeb(NPC npc, Entity target) {
        //npc.forceChat("WEB");
        var tileDist = npc.tile().transform(3, 3, 0).distance(target.tile());
        new Projectile(npc, target,1254, 20,12 * tileDist,20, 5, 0, true).sendProjectile();
        var delay = Math.max(1, (20 + (tileDist * 12)) / 30);
        target.message("Venenatis hurls her web at you, sticking you to the ground.");
        target.stun(6, false,true,true);
        target.hit(npc, npc.getCombatInfo().maxhit, delay);// Cannot protect from this.
    }

    private void drainPrayer(Entity npc, Entity target) {
        //npc.forceChat("PRAYER");
        if (target.isPlayer()) {
            var tileDist = npc.tile().transform(3, 3, 0).distance(target.tile());
            new Projectile(npc, target,171, 30,12 * tileDist,25, 25, 0, true).sendProjectile();
            var player = target.getAsPlayer();
            var curpray = player.getSkills().level(Skills.PRAYER);
            var add = curpray / 5 + 1;
            var drain = 10 + add; // base 10 drain + 20% of current prayer + 1. Example 50 prayer becomes 30. Best tactic to keep prayer low.
            player.getSkills().alterSkill(Skills.PRAYER, (drain > curpray) ? -curpray : -drain);

            if (curpray > 0) {
                target.message("Your prayer was drained!");
            }
        }
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
