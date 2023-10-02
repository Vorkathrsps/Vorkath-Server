package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;

public class DragonWarhammer extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1378);
        entity.graphic(1292, GraphicHeight.LOW, 0);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        // Nerf a player's def if it's a player
        if (target.isPlayer()) {
            Player playerTarget = (Player) target;
            if (hit.isAccurate()) {
                playerTarget.getSkills().alterSkill(Skills.DEFENCE, (int) -(playerTarget.getSkills().level(Skills.DEFENCE) * 0.3));
            } else {
                playerTarget.getSkills().alterSkill(Skills.DEFENCE, (int) -(playerTarget.getSkills().level(Skills.DEFENCE) * 0.05));
            }
        } else if (target.isNpc()) {
            NPC npcTarget = (NPC) target;
            int currentDefence = npcTarget.getCombatInfo().stats.defence;
            System.out.println(currentDefence);
            int newDefence;
            if (hit.isAccurate()) {
                npcTarget.getCombatInfo().stats.defence = (int) Math.max(0, npcTarget.getCombatInfo().stats.defence - (npcTarget.getCombatInfo().stats.defence * 0.3));
            } else {
                npcTarget.getCombatInfo().stats.defence = (int) Math.max(0, npcTarget.getCombatInfo().stats.defence - (npcTarget.getCombatInfo().stats.defence * 0.05));
            }
        }

        CombatSpecial.drain(entity, CombatSpecial.DRAGON_WARHAMMER.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
