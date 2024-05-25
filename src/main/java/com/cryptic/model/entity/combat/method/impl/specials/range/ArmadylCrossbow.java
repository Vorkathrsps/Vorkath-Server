package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.ranged.drawback.BoltDrawBack;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;

public class ArmadylCrossbow extends CommonCombatMethod {

    private static final Animation ANIMATION = new Animation(4230, Priority.HIGH);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        final Player player = entity.getAsPlayer();

        var graphic = -1;
        var weaponId = player.getEquipment().getId(EquipSlot.WEAPON);
        var boltDrawBack = BoltDrawBack.find(weaponId, graphic);
        int stepMultiplier = 0;
        int distance = entity.tile().getChevDistance(target.tile());
        int endHeight = 0;
        int startHeight = 0;
        int startSpeed = 0;
        int duration = 0;

        player.animate(ANIMATION);

        if (boltDrawBack != null) {
            entity.performGraphic(new Graphic(boltDrawBack.gfx, GraphicHeight.HIGH, 0));
            startSpeed = boltDrawBack.startSpeed;
            startHeight = boltDrawBack.startHeight;
            endHeight = boltDrawBack.endHeight;
            stepMultiplier = boltDrawBack.stepMultiplier;
            duration = startSpeed + 11 + (stepMultiplier * distance);
        }

        Projectile projectile = new Projectile(entity, target, 301, startSpeed, duration, startHeight, endHeight, 5, 1, stepMultiplier);

        final int hitDelay = entity.executeProjectile(projectile);

        CombatFactory.decrementAmmo(player);

        entity.submitHit(target, hitDelay, this);
        CombatSpecial.drain(entity, CombatSpecial.ARMADYL_CROSSBOW.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}
