package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.bodyguard;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;

/**
 * Fumus is one of the four mages guarding Nex in the Ancient Prison, within the God Wars Dungeon. Having mastered smoke magic, he enhances Nex's powers over smoke.
 * As with all of Nex's mages, he is immobile. Fumus is invulnerable to damage until Nex reaches below 80% of her hitpoints.
 * This is indicated by her shouting, Fumus, don't fail me! He attacks using Smoke Barrage and can poison players, although players are usually using antipoison
 * anyway since Nex can also poison.
 * @author Sharky
 * @Since January 13, 2023
 */
public class Fumus extends CommonCombatMethod {

    @Override
    public void init(NPC npc) {
        npc.getCombatInfo().scripts.agro_ = (n, t) -> false;
    }
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if(!entity.<Boolean>getAttribOr(AttributeKey.BARRIER_BROKEN,false)) {
            return false;
        }
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        Projectile projectile = new Projectile(entity, target, 390, 35, 20 * tileDist, 43, 31, 0);
        projectile.sendProjectile();
        if (World.getWorld().rollDie(100, 25)) {
            target.hit(entity, 4, SplatType.POISON_HITSPLAT);
        }
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC);
        hit.checkAccuracy().submit();
        if(hit.isAccurate()) {
            target.graphic(391);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }


    @Override
    public void preDefend(Hit hit) {
        if(!hit.getTarget().<Boolean>getAttribOr(AttributeKey.BARRIER_BROKEN,false)) {
            hit.block();
        }
    }
    @Override
    public void doFollowLogic() {

    }

    @Override
    public void onDeath(Player killer, NPC npc) {
        ZarosGodwars.fumus = null;
        if(ZarosGodwars.nex != null) {
            ZarosGodwars.nex.progressNextPhase();
        }
    }
}
