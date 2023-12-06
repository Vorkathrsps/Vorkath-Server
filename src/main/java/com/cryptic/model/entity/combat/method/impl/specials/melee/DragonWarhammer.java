package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;

public class DragonWarhammer extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1378);
        entity.graphic(1292, GraphicHeight.LOW, 0);
        var hit1 = entity.submitHit(target, 1, this).postDamage(hit -> {
            if (!hit.isAccurate()) {
                if (target instanceof Player player)
                    player.getSkills().alterSkill(Skills.DEFENCE, (int) -(player.getSkills().level(Skills.DEFENCE) * 0.05));
                else
                    target.getAsNpc().getCombatInfo().stats.defence = (int) Math.max(0, target.getAsNpc().getCombatInfo().stats.defence - (target.getAsNpc().getCombatInfo().stats.defence * 0.05));
                hit.block();
                return;
            }
            if (target instanceof Player player)
                player.getSkills().alterSkill(Skills.DEFENCE, (int) -(player.getSkills().level(Skills.DEFENCE) * 0.3));
            else
                target.getAsNpc().getCombatInfo().stats.defence = (int) Math.max(0, target.getAsNpc().getCombatInfo().stats.defence - (target.getAsNpc().getCombatInfo().stats.defence * 0.3));
        });
        entity.sendPublicSound(2520, hit1.getDelay());
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
