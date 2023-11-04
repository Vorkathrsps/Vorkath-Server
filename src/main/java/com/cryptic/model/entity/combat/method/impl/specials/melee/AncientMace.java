package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;

public class AncientMace extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6147);
        entity.graphic(1027);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy(true);
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
