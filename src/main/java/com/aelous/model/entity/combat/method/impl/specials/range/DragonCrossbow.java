package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.ranged.drawback.BoltDrawBack;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;

/**
 * The crossbow has a special attack, Annihilate, which drains 60% of the special attack bar and hits up to 9 enemies in a 3x3 area.
 * The primary target of Annihilate will take 20% extra damage, all other targets will take 20% less damage.
 * If used in a single-way combat area, the attack will still work but only hit one target with 20% extra damage.
 * It ignores enchanted bolt effects.
 */
public class DragonCrossbow extends CommonCombatMethod {

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

        player.animate(4230);

        if (boltDrawBack != null) {
            entity.performGraphic(new Graphic(boltDrawBack.gfx, GraphicHeight.HIGH, 0));
            startSpeed = boltDrawBack.startSpeed;
            startHeight = boltDrawBack.startHeight;
            endHeight = boltDrawBack.endHeight;
            stepMultiplier = boltDrawBack.stepMultiplier;
            duration = startSpeed + 11 + (stepMultiplier * distance);
        }

        Projectile projectile = new Projectile(entity, target, 698, startSpeed, duration, startHeight, endHeight, 0, entity.getSize(), stepMultiplier);

        final int hitDelay = entity.executeProjectile(projectile);


        target.performGraphic(new Graphic(1466, GraphicHeight.HIGH, projectile.getSpeed()));

        //Decrement ammo by 1
        CombatFactory.decrementAmmo(player);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),hitDelay, CombatType.RANGED).checkAccuracy();
        hit.submit();
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_CROSSBOW.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}
