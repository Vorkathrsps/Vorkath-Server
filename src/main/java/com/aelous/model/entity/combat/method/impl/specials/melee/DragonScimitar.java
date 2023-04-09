package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;

public class DragonScimitar extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1872);
        entity.graphic(347, GraphicHeight.HIGH, 0);
        //TODO it.sound(2540)
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        if(target.isPlayer()) {
            if (hit.getDamage() > 0) {
                Player player = (Player) entity;
                Player playerAttacker = (Player) target;
                CombatFactory.disableProtectionPrayers(playerAttacker);
                player.message("Your target can no longer use protection prayers.");
            }
        }
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_SCIMITAR.getDrainAmount());
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
