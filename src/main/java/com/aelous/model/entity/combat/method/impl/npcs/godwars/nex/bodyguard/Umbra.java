package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.bodyguard;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

/**
 * Umbra is one of the four mages guarding Nex in the Ancient Prison, within in the God Wars Dungeon.
 * Having mastered shadow magic, she enhances Nex's powers overshadow. As with all of Nex's mages,
 * she is immobile. Umbra is invulnerable to damage until Nex reaches below 60% of her hitpoints.
 * This is indicated by her shouting, Umbra, don't fail me! She attacks using Shadow Barrage and can
 * lower players' attack levels by 15%.
 *
 * @author Sharky
 * @Since January 13, 2023
 */
public class Umbra extends CommonCombatMethod {

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
        if (target.isPlayer()) {
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 2, CombatType.MAGIC);
            hit.checkAccuracy().postDamage(h -> {
                if (h.isAccurate()) {
                    target.graphic(383);

                    if (target.getSkills().level(Skills.ATTACK) < target.getSkills().xpLevel(Skills.ATTACK)) {
                        return;
                    }

                    int decrease = (int) (0.15 * (target.getSkills().level(Skills.ATTACK)));
                    target.getSkills().setLevel(Skills.ATTACK, target.getSkills().level(Skills.ATTACK) - decrease);
                    target.getSkills().update(Skills.ATTACK);
                }
            }).submit();
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
        ZarosGodwars.umbra = null;
        if(ZarosGodwars.nex != null) {
            ZarosGodwars.nex.progressNextPhase();
        }
    }
}
