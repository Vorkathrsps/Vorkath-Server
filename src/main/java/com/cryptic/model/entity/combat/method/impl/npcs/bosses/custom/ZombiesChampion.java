package com.cryptic.model.entity.combat.method.impl.npcs.bosses.custom;

import com.cryptic.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

/**
 * NPC id = 3359
 * A custom boss which spawns random in the wild by the {@link WildernessBossEvent}
 *
 * @author Patrick van Elderen | Zerikoth (PVE) | 06 feb. 2020 : 11:13
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class ZombiesChampion extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        //10% chance that the wold boss skulls you!
        if(World.getWorld().rollDie(10,1)) {
            Skulling.assignSkullState(((Player) target), SkullType.WHITE_SKULL);
            target.message("The "+entity.getMobName()+" has skulled you, be careful!");
        }

        if(World.getWorld().rollDie(5, 1)) {
            rangeAttack((NPC) entity, target);
        } else {
            magicAttack((NPC) entity, target);
        }
        return true;
    }

    private void rangeAttack(NPC npc, Entity target) {
        npc.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if(p != null && target.tile().inSqRadius(p.tile(),12)) {
                var delay = entity.getProjectileHitDelay(target);
                new Projectile(npc, p, 298, 32, entity.projectileSpeed(target), 30, 30, 0).sendProjectile();
                p.hit(npc, CombatFactory.calcDamageFromType(npc, p, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
            }
        });

        npc.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void magicAttack(NPC npc, Entity target) {
        npc.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if(p != null && target.tile().inSqRadius(p.tile(),12)) {
                new Projectile(npc, p, 448, 32, entity.projectileSpeed(target), 30, 30, 0).sendProjectile();
                var delay = entity.getProjectileHitDelay(target);
                p.hit(npc, CombatFactory.calcDamageFromType(npc, p, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
            }
        });

        npc.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }
}
