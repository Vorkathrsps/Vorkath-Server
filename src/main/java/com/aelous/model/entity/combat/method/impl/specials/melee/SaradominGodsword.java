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

public class SaradominGodsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = (Player) entity;
        player.animate(player.getEquipment().contains(ItemIdentifiers.SARADOMIN_GODSWORD_OR) ? 7641 : 7640);
        boolean gfx_gold = player.getAttribOr(AttributeKey.SGS_GFX_GOLD, false);
        player.graphic(gfx_gold ? 1745 : 1209);
        //TODO it.player().world().spawnSound(it.player().tile(), 3869, 0, 10)
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        // Heal the player & restore prayer and hit the enemy
        player.heal(Math.max(10, hit.getDamage() / 2)); // Min heal is 10

        player.getSkills().alterSkill(Skills.PRAYER, Math.max(5, hit.getDamage() / 4)); // Min heal is 5 for prayer
        CombatSpecial.drain(entity, CombatSpecial.SARADOMIN_GODSWORD.getDrainAmount());
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
