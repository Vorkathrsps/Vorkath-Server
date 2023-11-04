package com.cryptic.model.entity.combat.method.impl.npcs.dragons;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.utility.Utils;

public class BasicDragon extends CommonCombatMethod {

    boolean fire;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) {

            return false;
        }

        if (entity == null || target == null) {
            return false;
        }

        if (withinDistance(1)) {
            if (!fire && Utils.rollDie(6, 1)) {
                breathFire(entity, target);
            } else {
                basicAttack(entity, target);
            }
            return true; //shouldnt even reach here if within distance oh just saw true rght
        }

        return false;
    }

    private void basicAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) {
            return;
        }

        entity.animate(entity.attackAnimation());

        fire = false;
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void breathFire(Entity entity, Entity target) {
        fire = true;

        if (World.getWorld().clipAt(entity.tile()) != 0) {
            return;
        }

        if (target instanceof Player player) {
            double max = 50.0;
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;


            boolean memberEffect = player.getMemberRights().isExtremeMemberOrGreater(player) && !WildernessArea.isInWilderness(player);
            if (max > 0 && player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false) || memberEffect) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max = 0.0;
            }

            //Does our player have an anti-dragon shield?
            if (max > 0 && (player.getEquipment().hasAt(EquipSlot.SHIELD, 11283) || player.getEquipment().hasAt(EquipSlot.SHIELD, 11284) || player.getEquipment().hasAt(EquipSlot.SHIELD, 1540))) {
                player.message("Your shield absorbs most of the dragon fire!");
                max *= 0.3;
            }

            //Has our player recently consumed an antifire potion?
            if (max > 0 && antifire_charges > 0) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max *= 0.3;
            }

            //Is our player using protect from magic?
            if (max > 0 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max *= 0.6;
            }

            if (hasShield && hasPotion) {
                max = 0.0;
            }
            int hit = Utils.random((int) max);
            player.hit(entity, hit, CombatType.MAGIC).submit();
            if (max == 50 && hit > 0) {
                player.message("You are badly burned by the dragon fire!");
            }
            entity.animate(81);
            entity.graphic(1, GraphicHeight.HIGH, 0);
        }
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1; //This may sound incorrect but 1 is the proper attack distance for combat following of basic dragons.
    }
}
