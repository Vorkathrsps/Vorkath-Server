package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Skills;

public class AbyssalBludgeon extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(3299);
        //TODO it.player().sound(2715, 10)
        //TODO it.player().sound(1930, 30)

        int damage = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        damage *= 1 + (((entity.getSkills().xpLevel(Skills.PRAYER) - entity.getSkills().level(Skills.PRAYER)) * 0.5) / 100.0);
        Hit hit = target.hit(entity, damage,1, CombatType.MELEE).checkAccuracy(true);
        hit.submit();
        target.graphic(1284, GraphicHeight.LOW, 15);
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_BLUDGEON.getDrainAmount());
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
