package com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

import java.lang.ref.WeakReference;

//TODO tents use a special combat mechanic where it does magic attacks with their range accuracy against the players magic defence
public class EnormousTentacle extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Projectile(entity, target, 162, 32,65, 30, 30, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy().submit();
        target.graphic(163);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 16;
    }

    public static void onHit(Player player, NPC npc) {
        // This hook is only relevent when we're in whirlpool form.
        if (npc.id() != KrakenBoss.TENTACLE_WHIRLPOOL || npc.transmog() == KrakenBoss.TENTACLE_NPCID || npc.hidden()) { // Not transformed yet
            return;
        }
        //We only want the NPC to transmog once
        if (npc.transmog() != KrakenBoss.TENTACLE_NPCID) {
            npc.transmog(KrakenBoss.TENTACLE_NPCID);
            npc.animate(3860);
            npc.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 1);
            npc.combatInfo(World.getWorld().combatInfo(5535));
            npc.setCombatMethod(World.getWorld().combatInfo(KrakenBoss.TENTACLE_NPCID).scripts.newCombatInstance());
            npc.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(player));
        }
    }
}
