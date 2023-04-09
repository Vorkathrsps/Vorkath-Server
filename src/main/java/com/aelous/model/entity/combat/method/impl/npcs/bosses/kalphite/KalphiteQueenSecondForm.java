package com.aelous.model.entity.combat.method.impl.npcs.bosses.kalphite;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 24, 2020
 */
public class KalphiteQueenSecondForm extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        int distance = getAttackDistance(target);
        boolean inDistance = target.boundaryBounds().within(entity.tile(), entity.getSize(), distance);
        if (inDistance) {
            if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollDie(4, 1)) {
                attack(((NPC)entity), ((Player)target), CombatType.MELEE);
                return true;
            } else {
                int random = Utils.RANDOM_GEN.nextInt(100);
                attack(((NPC)entity), ((Player)target), random < 50 ? CombatType.MAGIC : CombatType.RANGED);
                return true;
            }
        }
        return false;
    }

    private void attack(NPC npc, Player target, CombatType combatType) {

        int attackAnimation = KalphiteQueen.animation(npc.id(), combatType);

        npc.animate(attackAnimation);

        switch(combatType) {
            case MELEE:
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
                break;
            case RANGED:
                for(Player player : World.getWorld().getPlayers()) {
                    if(player != null && player.tile().inArea(KalphiteQueen.getArea())) {
                        new Projectile(npc, target, 473, 41, 60, 45, 30, 0, 10, 15).sendProjectile();
                        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
                    }
                }
                break;
            case MAGIC:
                npc.graphic(278);
                for(Player player : World.getWorld().getPlayers()) {
                    if (player != null && player.tile().inArea(KalphiteQueen.getArea())) {
                        new Projectile(npc, target, 280, 41, 60, 45, 30, 0, 10, 15).sendProjectile();
                        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();

                        target.performGraphic(new Graphic(281, GraphicHeight.LOW,2));
                    }
                }
                break;
        }
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, 4);
    }

    public static void death(NPC npc) {
        NPC spawn = new NPC(6500, npc.tile());

        Chain.bound(null).runFn(spawn.getCombatInfo().respawntime, () -> {
            spawn.respawns(false);
            World.getWorld().registerNpc(spawn);
            npc.animate(6240);
            //here? not sure show ingame again
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
