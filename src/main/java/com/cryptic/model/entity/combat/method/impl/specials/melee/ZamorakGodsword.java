package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

public class ZamorakGodsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = (Player) entity;
        player.animate(player.getEquipment().contains(ItemIdentifiers.SARADOMIN_GODSWORD_OR) ? 7639 : 7638);
        boolean gfx_gold = player.getAttribOr(AttributeKey.ZGS_GFX_GOLD, false);
        player.graphic(gfx_gold ? 1746 : 1210);
        new Hit(entity, target, 1, this).checkAccuracy(true).submit().postDamage(hit -> {
            if (!hit.isAccurate()) {
                hit.block();
                return;
            }
            target.graphic(369);
            target.freeze(33, entity, true);
        });
        CombatSpecial.drain(entity, CombatSpecial.ZAMORAK_GODSWORD.getDrainAmount());
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
