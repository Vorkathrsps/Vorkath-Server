package com.aelous.model.entity.combat.method.impl.specials.melee;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

public class AncientMace extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6147);
        entity.graphic(1027);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        //TODO in combat ignore prayer, mace ignores overheads
        if (target.isPlayer()) {
            Player t = (Player) target;
            Player p = (Player) entity;
            t.getSkills().alterSkill(Skills.PRAYER, -hit.getDamage());
            p.getSkills().alterSkill(Skills.PRAYER, hit.getDamage());
        }
        CombatSpecial.drain(entity, CombatSpecial.ANCIENT_MACE.getDrainAmount());
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
