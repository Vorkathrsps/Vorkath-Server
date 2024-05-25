package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.ItemIdentifiers;

public class SaradominGodsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = (Player) entity;
        player.animate(player.getEquipment().contains(ItemIdentifiers.SARADOMIN_GODSWORD_OR) ? 7641 : 7640);
        boolean gfx_gold = player.getAttribOr(AttributeKey.SGS_GFX_GOLD, false);
        player.graphic(gfx_gold ? 1745 : 1209);
        var hit1 = entity.submitHit(target, 1, this).postDamage(hit -> {
            if (!hit.isAccurate()) {
                hit.block();
                return;
            }
            player.heal(Math.max(10, hit.getDamage() / 2));
            player.getSkills().alterSkill(Skills.PRAYER, Math.max(5, hit.getDamage() / 4));
        });
        entity.sendPublicSound(3869, hit1.getDelay());
        CombatSpecial.drain(entity, CombatSpecial.SARADOMIN_GODSWORD.getDrainAmount());
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
