package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.DRAGON_ARROW;

public class DarkBow extends CommonCombatMethod {

    private int endgfx;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(426);

        Item ammo = player.getEquipment().get(EquipSlot.AMMO);

        var gfx = 1101;
        var gfx2 = 1102; //non drag arrow 2nd arrow has another graphic id
        endgfx = 1103; // small puff
        var min = 5;

        if (ammo != null && ammo.getId() == DRAGON_ARROW) {
            // dragon arrows
            gfx = 1099; // dragon spec
            gfx2 = 1099; // drag again
            endgfx = 1100; // large puff
            min = 8;
        }

        var dist = player.tile().distance(target.tile());
        var speed1 = 16 + (dist * 8);
        var speed2 = 25 + (dist * 10);
        var delay = (int) Math.round(Math.floor(32 / 30.0) + ((double)dist * (5 * 0.020) / 0.6));

        // Send 2 arrow projectiles
        new Projectile(player, target, gfx, 40, speed1, 40, 36, 0).sendProjectile();
        new Projectile(entity, target, gfx2, 40, speed2, 40, 36, 0).sendProjectile();

        // Decrement 2 arrows from ammunition
        CombatFactory.decrementAmmo(player);

        // Note: Dark bow first hit does have PID applied, but the delay varies (not always delay-1) depending on dist. It's custom.
        Hit hit1 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();

        // Minimum damages depending on arrow type
        if (hit1.getDamage() < min) {
            hit1.setDamage(min);
        }
        hit1.postDamage(this::handleAfterHit).submit();

        // Extra delay which the second arrow has
        var extraDelay = 2;
        if (dist <= 5)
            extraDelay -= 1;

        // The second hit is pid adjusted.
        Hit hit2 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),extraDelay + delay, CombatType.RANGED).checkAccuracy();
        if (hit2.getDamage() < min) {
            hit2.setDamage(min);
        }
        hit2.postDamage(this::handleAfterHit).submit();

        CombatSpecial.drain(entity, CombatSpecial.DARK_BOW.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return CombatFactory.RANGED_COMBAT.getAttackDistance(entity);
    }

    public void handleAfterHit(Hit hit) {
        hit.getTarget().performGraphic(new Graphic(endgfx, GraphicHeight.HIGH));
    }
}
