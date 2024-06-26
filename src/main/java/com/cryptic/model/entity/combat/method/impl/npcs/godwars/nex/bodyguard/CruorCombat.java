package com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.bodyguard;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

/**
 * Cruor is one of the four mages guarding Nex in the Ancient Prison, within in the God Wars Dungeon. Having mastered blood magic, he enhances Nex's powers over blood.
 * As with all of Nex's mages, he is immobile. Cruor is invulnerable to damage until Nex reaches below 40% of her hitpoints.
 * This is indicated by her shouting, Cruor, don't fail me! He attacks using Blood Barrage and can heal himself a percentage of the damage inflicted.
 * @author Sharky
 * @Since January 13, 2023
 */
public class CruorCombat extends CommonCombatMethod {

    @Override
    public void init(NPC npc) {
        npc.getCombatInfo().scripts.agro_ = (n, t) -> false;
    }
    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        if(!mob.<Boolean>getAttribOr(AttributeKey.BARRIER_BROKEN,false)) {
            return false;
        }
        mob.animate(mob.attackAnimation());
        Hit hit = target.hit(mob, CombatFactory.calcDamageFromType(mob, target, CombatType.MAGIC), 1, CombatType.MAGIC);
        hit.checkAccuracy(true).postDamage(h -> {
            if(h.isAccurate()) {
                target.graphic(377);
                mob.heal(hit.getDamage() / 4);
            }
        }).submit();
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
        ZarosGodwars.cruor = null;
        if(ZarosGodwars.nex != null) {
            ZarosGodwars.nex.progressNextPhase();
        }
    }
}
