package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.bodyguard;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;

/**
 * Glacies is one of the four mages guarding Nex in the Ancient Prison, within in the God Wars Dungeon.
 * Having mastered ice magic, she enhances Nex's powers over ice. As with all of Nex's mages, she is
 * immobile. Glacies is invulnerable to damage until Nex reaches below 20% of her hitpoints.
 * This is indicated by her shouting, Glacies, don't fail me!
 * @author Sharky
 * @Since January 13, 2023
 */
public class Glacies extends CommonCombatMethod {

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
        Projectile projectile = new Projectile(entity, target, 368, 0, 66, 43, 0, 0);
        projectile.sendProjectile();
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC);
        hit.checkAccuracy().postDamage(h -> {
            if(h.isAccurate() && World.getWorld().rollDie(2,1)) {
                target.graphic(369);
                target.freeze(33, entity);
            }
        }).submit();
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
        ZarosGodwars.glacies = null;
        if(ZarosGodwars.nex != null) {
            ZarosGodwars.nex.progressNextPhase();
        }
    }
}
