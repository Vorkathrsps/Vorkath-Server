package com.aelous.model.entity.combat.method.impl.npcs.bosses.kalphite;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
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

import java.lang.ref.WeakReference;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.KALPHITE_QUEEN_6501;

/**
 * Created by Jason MacKeigan on 2016-06-29.
 *
 * The purpose of this singleton is to represent the second form of the Kalphite Queen
 * npc.
 */
public class KalphiteQueenFirstForm extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        int distance = getAttackDistance(target);
        boolean inDistance = target.boundaryBounds().within(entity.tile(), entity.getSize(), distance);
        if (!withinDistance(8)) {
            return false;
        }

        if (inDistance) {
            if (withinDistance(1) && Utils.rollDie(4, 1)) {
                attack(((NPC)entity), ((Player)target), CombatType.MELEE);
                return true;
            } else {
                attack(((NPC)entity), ((Player)target), Utils.percentageChance(50) ? CombatType.MAGIC : CombatType.RANGED);
                return true;
            }
        }
        return false;
    }

    private void attack(NPC npc, Player target, CombatType combatType) {
        int attackAnimation = KalphiteQueen.animation(npc.id(), combatType);

        npc.animate(attackAnimation);

        switch (combatType) {
            case MELEE ->
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
            case RANGED -> {
                for (Player player : World.getWorld().getPlayers()) {
                    if (player != null && player.tile().inArea(KalphiteQueen.getArea())) {
                        var tileDist = entity.tile().distance(target.tile());
                        int duration = (41 + 11 + (5 * tileDist));
                        Projectile p = new Projectile(entity, target, 473, 41, duration, 43, 31, 16, target.getSize(), 5);
                        final int delay = entity.executeProjectile(p);
                        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
                    }
                }
            }
            case MAGIC -> {
                npc.graphic(278);
                for (Player player : World.getWorld().getPlayers()) {
                    if (player != null && player.tile().inArea(KalphiteQueen.getArea())) {
                        var tileDist = entity.tile().distance(target.tile());
                        int duration = (51 + -5 + (10 * tileDist));
                        Projectile p = new Projectile(entity, target, 280, 51, duration, 43, 31, 16, target.getSize(), 10);
                        final int delay = entity.executeProjectile(p);
                        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
                        target.performGraphic(new Graphic(281, GraphicHeight.LOW, p.getSpeed()));
                    }
                }
            }
        }
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, 4);
    }

    public static void death(NPC form1) {
        form1.respawns(false);
        form1.lock();
        var targ = form1.<WeakReference<Entity>>getAttribOr(AttributeKey.TARGET, new WeakReference<Entity>(null)).get();
        Chain.bound(null).runFn(4, () -> {
            form1.transmog(6501);
            form1.animate(6270);
            form1.graphic(1055);
        }).then(13, () -> {
            form1.setCombatInfo(World.getWorld().combatInfo(KALPHITE_QUEEN_6501));
            form1.def(World.getWorld().definitions().get(NpcDefinition.class, KALPHITE_QUEEN_6501));
            form1.heal(form1.maxHp());
            form1.unlock();
            if (targ != null) {
                form1.setPositionToFace(targ.tile());
                form1.getCombat().attack(targ);
                form1.cloneDamage(form1);
            }
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
