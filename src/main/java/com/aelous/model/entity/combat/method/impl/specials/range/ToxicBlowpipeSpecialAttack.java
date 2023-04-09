package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.ranged.drawback.DartDrawback;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;

import static com.aelous.utility.ItemIdentifiers.TOXIC_BLOWPIPE;

public class ToxicBlowpipeSpecialAttack extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        var weaponId = player.getEquipment().getId(EquipSlot.WEAPON);
        var drawbackDart = DartDrawback.find(weaponId);
        int stepMultiplier = 0;
        int distance = entity.tile().getChevDistance(target.tile());
        int endHeight = 0;
        int startHeight = 0;
        int startSpeed = 0;
        int duration = 0;

        if (drawbackDart != null) {
            startSpeed = drawbackDart.startSpeed;
            startHeight = drawbackDart.startHeight;
            endHeight = drawbackDart.endHeight;
            stepMultiplier = drawbackDart.stepMultiplier;
            duration = startSpeed + 11 + (stepMultiplier * distance);
        }

        player.animate(player.getEquipment().hasAt(EquipSlot.WEAPON, TOXIC_BLOWPIPE) ? 5061 : 11901);

        Projectile projectile = new Projectile(entity, target, 1043, startSpeed, duration, startHeight, endHeight, 0, target.getSize(), stepMultiplier);

        final int hitDelay = entity.executeProjectile(projectile);

        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),hitDelay, CombatType.RANGED).checkAccuracy();
        hit.submit();

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
    public int getAttackDistance(Entity entity) {
        return CombatFactory.RANGED_COMBAT.getAttackDistance(entity);
    }
}
