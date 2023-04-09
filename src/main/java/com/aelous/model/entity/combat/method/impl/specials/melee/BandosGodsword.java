package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.ItemIdentifiers;

public class BandosGodsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = (Player) entity;
        player.animate(player.getEquipment().contains(ItemIdentifiers.BANDOS_GODSWORD_OR) ? 7643 : 7642);
        boolean gfx_gold = player.getAttribOr(AttributeKey.BGS_GFX_GOLD, false);
        player.graphic(gfx_gold ? 1748 : 1212);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        if(hit.getDamage() > 0) {
            var skills = new int[]{Skills.DEFENCE, Skills.STRENGTH, Skills.PRAYER, Skills.ATTACK, Skills.MAGIC, Skills.RANGED};
            var deductionTotal = hit.getDamage();
            for (int i = 0; i <= skills.length; i++) {
                if (deductionTotal <= 0) break;
                var take = deductionTotal;

                // Identify the targets current level in this stat
                var targetCurrentStat = target.isPlayer() ? target.getSkills().level(skills[i])
                    :
                    new int[] {
                        target.getAsNpc().getCombatInfo().stats.attack,
                        target.getAsNpc().getCombatInfo().stats.defence,
                        target.getAsNpc().getCombatInfo().stats.strength,
                        0,
                        target.getAsNpc().getCombatInfo().stats.ranged,
                        0,
                        target.getAsNpc().getCombatInfo().stats.magic
                    }[i];

                if (targetCurrentStat - take < 0) // Cap the amount we can take away to that available.
                    take = targetCurrentStat;

                // Now reduce the stat.
                if (target.isPlayer()) {
                    target.getSkills().setLevel(skills[i], target.getSkills().level(skills[i]) - take);
                } else {
                    switch (i) {
                        case 0 -> target.getAsNpc().getCombatInfo().stats.attack -= take;
                        case 1 -> target.getAsNpc().getCombatInfo().stats.defence -= take;
                        case 2 -> target.getAsNpc().getCombatInfo().stats.strength -= take;
                        case 4 -> target.getAsNpc().getCombatInfo().stats.ranged -= take;
                        case 6 -> target.getAsNpc().getCombatInfo().stats.magic -= take;
                    }

                }
                deductionTotal -= take;
            }
        }
        CombatSpecial.drain(entity, CombatSpecial.BANDOS_GODSWORD.getDrainAmount());
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
