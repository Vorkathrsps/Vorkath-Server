package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;

import static com.aelous.utility.ItemIdentifiers.TOXIC_BLOWPIPE;

public class ToxicBlowpipeSpecialAttack extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(player.getEquipment().hasAt(EquipSlot.WEAPON, TOXIC_BLOWPIPE) ? 5061 : 11901);

        // Send projectiles
        new Projectile(player, target, 1043, 32, 65, 35, 36, 0).sendProjectile();

        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),2, CombatType.RANGED).checkAccuracy();
        hit.submit();

        if (hit.getDamage() > 0) {
            player.heal(hit.getDamage() / 2);
            boolean venom = Venom.attempt(player, target, CombatType.RANGED, true);
            if (venom)
                target.venom(player);
        }
        CombatSpecial.drain(entity, CombatSpecial.TOXIC_BLOWPIPE.getDrainAmount());
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        int delay = entity.getBaseAttackSpeed();
        if (entity.isNpc())
            return delay - 1;
        return delay;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return CombatFactory.RANGED_COMBAT.getAttackDistance(entity);
    }
}
