package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import static com.cryptic.utility.ItemIdentifiers.DRAGON_ARROW;

public class DarkBowBH extends CommonCombatMethod {
    private int endgfx;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        player.animate(426);

        Item ammo = player.getEquipment().get(EquipSlot.AMMO);

        var gfx = 1101;
        var gfx2 = 1102; //non drag arrow 2nd arrow has another graphic id
        endgfx = 1103; // small puff
        var min = 7;

        if (ammo != null && ammo.getId() == DRAGON_ARROW) {
            // dragon arrows
            gfx = 1099; // dragon spec
            gfx2 = 1099; // drag again
            endgfx = 1100; // large puff
            min = 10;
        }

        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration1 = (41 + 11 + (5 * tileDist));
        int duration2 = (51 + 11 + (5 * tileDist));
        Projectile p1 = new Projectile(entity, target, gfx, 41, duration1, 40, 31, 0, target.getSize(), 5);
        Projectile p2 = new Projectile(entity, target, gfx2, 51, duration2, 55, 31, 0, target.getSize(), 5);

        final int delay1 = entity.executeProjectile(p1);
        final int delay2 = entity.executeProjectile(p2);

        // Decrement 2 arrows from ammunition
        CombatFactory.decrementAmmo(player);


        Hit hit1 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay1, CombatType.RANGED).checkAccuracy(true);

        target.graphic(endgfx, GraphicHeight.MIDDLE, p1.getSpeed());

        // Minimum damages depending on arrow type
        if (hit1.getDamage() < min) {
            hit1.setAccurate(true);
            hit1.setDamage(min);
        }

        hit1.submit();

        // The second hit is pid adjusted.
        Hit hit2 = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay2, CombatType.RANGED).checkAccuracy(true);
        if (hit2.getDamage() < min) {
            hit2.setAccurate(true);
            hit2.setDamage(min);
        }

        hit2.submit();

        target.graphic(endgfx, GraphicHeight.MIDDLE, p2.getSpeed());
        CombatSpecial.drain(entity, CombatSpecial.DARK_BOW_BH.getDrainAmount());
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
}
