package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

public class StatiusWarhammer extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1378);
        entity.graphic(844);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        // Nerf a player's def if it's a player
        if (target.isPlayer()) {
            Player playerTarget = (Player) target;
            playerTarget.getSkills().alterSkill(Skills.DEFENCE, (int) -(playerTarget.getSkills().level(Skills.DEFENCE) * 0.3));
        } else if (target.isNpc()) {
            NPC npcTarget = (NPC) target;
            npcTarget.getCombatInfo().stats.defence = (int) Math.max(0, npcTarget.getCombatInfo().stats.defence - (npcTarget.getCombatInfo().stats.defence * 0.3));
        }
        CombatSpecial.drain(entity, CombatSpecial.STATIUS_WARHAMMER.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
