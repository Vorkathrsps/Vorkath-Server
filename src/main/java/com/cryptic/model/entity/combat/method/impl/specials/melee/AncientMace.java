package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import org.jetbrains.annotations.NotNull;

public class AncientMace extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(@NotNull Entity entity, Entity target) {
        entity.animate(6147);
        entity.graphic(1052, GraphicHeight.HIGH, 0);
        new Hit(entity, target, 0, this).checkAccuracy(true).submit().ignorePrayer().postDamage(h -> {
            if (!h.isAccurate()) {
                h.block();
                return;
            }
            if (target instanceof Player player) {
                var attacker = (Player) entity;
                var p_t = player.getSkills().level(Skills.PRAYER);
                if(p_t > 0) player.getSkills().alterSkill(Skills.PRAYER, -h.getDamage());
                var p_a = attacker.getSkills().getMaxLevel(Skills.PRAYER);
                if(p_a + h.getDamage() <= 99) attacker.getSkills().alterSkill(Skills.PRAYER, +h.getDamage());
            }
        });
        CombatSpecial.drain(entity, CombatSpecial.ANCIENT_MACE.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(@NotNull Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
