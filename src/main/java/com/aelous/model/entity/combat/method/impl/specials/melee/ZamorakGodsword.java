package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

public class ZamorakGodsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = (Player) entity;
        player.animate(player.getEquipment().contains(ItemIdentifiers.SARADOMIN_GODSWORD_OR) ? 7639 : 7638);
        boolean gfx_gold = player.getAttribOr(AttributeKey.ZGS_GFX_GOLD, false);
        player.graphic(gfx_gold ? 1746 : 1210);
        //TODO it.player().world().spawnSound(it.player().tile(), 3869, 0, 10)

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        if (hit.getDamage() > 0) {
            target.graphic(369);
            target.freeze(33, entity);
        }
        CombatSpecial.drain(entity, CombatSpecial.ZAMORAK_GODSWORD.getDrainAmount());
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
