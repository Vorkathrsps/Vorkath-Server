package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.Venom;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.TOXIC_BLOWPIPE;

public class ToxicBlowpipeSpecialAttack extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        int distance = entity.tile().getChevDistance(target.tile());

        player.animate(player.getEquipment().hasAt(EquipSlot.WEAPON, TOXIC_BLOWPIPE) ? 5061 : 11901);

        int duration = 32 + (7 * distance);

        Projectile projectile = new Projectile(entity, target, 1043, 32, duration, 35, 36, 15, 1, 7);

        final int hitDelay = entity.executeProjectile(projectile);

        CombatFactory.decrementAmmo(player);

        var hit = entity.submitHit(target, hitDelay, this);

        player.sendPublicSound(800, hit.getDelay());

        if (hit.getDamage() > 0) {
            player.heal(hit.getDamage() / 2);
            boolean venom = Venom.attempt(player, target, CombatType.RANGED, true);
            if (venom)
                target.venom(player);
        }

        CombatSpecial.drain(entity, CombatSpecial.TOXIC_BLOWPIPE.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        int delay = entity.getBaseAttackSpeed();
        if (entity.isNpc())
            return delay - 1;
        return delay;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return CombatFactory.RANGED_COMBAT.moveCloseToTargetTileRange(entity);
    }
}
