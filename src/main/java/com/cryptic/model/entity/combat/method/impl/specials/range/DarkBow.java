package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.ranged.drawback.DblArrowDrawBack;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import static com.cryptic.utility.ItemIdentifiers.DRAGON_ARROW;

public class DarkBow extends CommonCombatMethod {

    private int endgfx;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        Item ammo = player.getEquipment().get(EquipSlot.AMMO);
        if(ammo == null || ammo.getAmount() < 2) {
            player.message("You need at least two arrows in your quiver to use this special attack.");
            return false;
        }

        player.animate(426);

        var db2 = DblArrowDrawBack.find(ammo.getId());
        if (db2 != null) {
            player.graphic(db2.gfx, GraphicHeight.HIGH, 0);
        }

        var gfx = 1101;
        var gfx2 = 1102; //non drag arrow 2nd arrow has another graphic id
        endgfx = 1103; // small puff
        var min = 5;

        if (ammo.getId() == DRAGON_ARROW) {
            // dragon arrows
            gfx = 1099; // dragon spec
            gfx2 = 1099; // drag again
            endgfx = 1100; // large puff
            min = 8;
        }

        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration1 = (45 + 10 + (7 * tileDist));
        int duration2 = (45 + 12 + (15 * tileDist));
        Projectile p1 = new Projectile(entity, target, gfx, 45, duration1, 43, 38, 5, target.getSize(), 5);
        Projectile p2 = new Projectile(entity, target, gfx2, 45, duration2, 49, 31, 15, target.getSize(), 5);

        final int delay1 = entity.executeProjectile(p1);
        final int delay2 = entity.executeProjectile(p2);

        // Decrement 2 arrows from ammunition
        CombatFactory.decrementAmmo(player);

        // Note: Dark bow first hit does have PID applied, but the delay varies (not always delay-1) depending on dist. It's custom.
        Hit hit1 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay1, CombatType.RANGED).checkAccuracy();

        hit1.submit();
        target.graphic(endgfx, GraphicHeight.MIDDLE, p1.getSpeed());

        // Minimum damages depending on arrow type
        if (hit1.getDamage() < min) {
            hit1.setDamage(min);
        }

        // The second hit is pid adjusted.
        Hit hit2 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),delay2, CombatType.RANGED).checkAccuracy();
        if (hit2.getDamage() < min) {
            hit2.setDamage(min);
        }
        hit2.submit();

        target.graphic(endgfx, GraphicHeight.MIDDLE, p2.getSpeed());

        CombatSpecial.drain(entity, CombatSpecial.DARK_BOW.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return CombatFactory.RANGED_COMBAT.moveCloseToTargetTileRange(entity);
    }

    public void handleAfterHit(Hit hit) {
        hit.getTarget().performGraphic(new Graphic(endgfx, GraphicHeight.HIGH));
    }
}
