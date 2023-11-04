package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.ranged.drawback.DblArrowDrawBack;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import static com.cryptic.utility.ItemIdentifiers.DRAGON_ARROW;

public class DarkBow extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!(entity instanceof Player player)) return false;
        Item ammo = player.getEquipment().get(EquipSlot.AMMO);
        if (ammo == null || ammo.getAmount() < 2) {
            player.message("You need at least two arrows in your quiver to use this special attack.");
            return false;
        }
        player.animate(426);
        var db2 = DblArrowDrawBack.find(ammo.getId());
        if (db2 != null) player.graphic(db2.gfx, GraphicHeight.HIGH, 0);
        var gfx = 1101;
        var gfx2 = 1102; //non drag arrow 2nd arrow has another graphic id
        int endgfx = 1103; // small puff
        var ref = new Object() {
            int min = 5;
        };
        if (ammo.getId() == DRAGON_ARROW) {
            gfx = 1099;
            gfx2 = 1099;
            endgfx = 1100;
            ref.min = 8;
        }
        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration1 = (41 + 11 + (5 * tileDist));
        int duration2 = (41 + 11 + (10 * tileDist));
        Projectile p1 = new Projectile(player, target, gfx, 41, duration1, 40, 36, 5, 1, 5);
        Projectile p2 = new Projectile(player, target, gfx2, 41, duration2, 40, 36, 25, 1, 10);
        final int delay1 = player.executeProjectile(p1);
        final int delay2 = player.executeProjectile(p2);
        new Hit(player, target, delay1, true, CombatType.RANGED, this)
            .rollAccuracyAndDamage()
            .submit()
            .conditions(hit -> {
                if (hit.isAccurate() && hit.getDamage() == 0) hit.setAccurate(true).setHitMark(HitMark.DEFAULT).setDamage(ref.min);
            });
        new Hit(player, target, delay2, true, CombatType.RANGED, this)
            .rollAccuracyAndDamage()
            .submit()
            .conditions(hit -> {
                if (hit.isAccurate() && hit.getDamage() == 0) hit.setAccurate(true).setHitMark(HitMark.DEFAULT).setDamage(ref.min);
            });
        target.graphic(endgfx, GraphicHeight.MIDDLE, p1.getSpeed());
        target.graphic(endgfx, GraphicHeight.MIDDLE, p2.getSpeed());
        CombatFactory.decrementAmmo(player);
        CombatSpecial.drain(player, CombatSpecial.DARK_BOW.getDrainAmount());
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
