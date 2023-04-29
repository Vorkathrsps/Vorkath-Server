package com.aelous.model.entity.combat.method.impl.npcs.dragons;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;


/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 14, 2020 13:28
 */
public class BrutalDragons extends CommonCombatMethod {

    boolean fire;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollDie(5, 4))
            basicAttack(entity, target);
        else if (!fire && Utils.rollDie(2, 1))
            meleeDragonfire(entity, target);
        else
            magicAttack(((NPC) entity), target);
        return true;
    }

    private void meleeDragonfire(Entity entity, Entity target) {
        fire = true;
        entity.animate(81);
        entity.graphic(1, GraphicHeight.HIGH, 0);
        if (target instanceof Player player) {
            if (!CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
                return;
            }
            double max = 50.0;
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;

            boolean memberEffect = player.getMemberRights().isExtremeMemberOrGreater(player) && !WildernessArea.inWild(player);
            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false) || memberEffect) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max = 0.0;
            }

            //Does our player have an anti-dragon shield?
            if (max > 0 && (player.getEquipment().hasAt(EquipSlot.SHIELD, 11283) || player.getEquipment().hasAt(EquipSlot.SHIELD, 11284) ||
                player.getEquipment().hasAt(EquipSlot.SHIELD, 1540))) {
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
            player.hit(entity, hit, entity.getProjectileHitDelay(player), CombatType.MAGIC).submit();
            if (max == 50 && hit > 0) {
                player.message("You are badly burned by the dragon fire!");
            }
        }
    }

    private void basicAttack(Entity entity, Entity target) {
        fire = false;
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    private void magicAttack(NPC npc, Entity entity) {
        fire = false;
        npc.animate(6722);
        entity.animate(81);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        switch (npc.id()) {
            case NpcIdentifiers.BRUTAL_GREEN_DRAGON, NpcIdentifiers.BRUTAL_GREEN_DRAGON_8081 -> {
                Projectile p1 = new Projectile(entity, target, 133, 51, duration, 43, 31, 0, target.getSize(), 5);
                final int delay = entity.executeProjectile(p1);
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).submit();

            }
            case NpcIdentifiers.BRUTAL_BLUE_DRAGON, NpcIdentifiers.BRUTAL_RED_DRAGON -> {
                Projectile p1 = new Projectile(entity, target, 136, 51, duration, 43, 31, 0, target.getSize(), 5);
                final int delay = entity.executeProjectile(p1);
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).submit();

            }
            case NpcIdentifiers.BRUTAL_RED_DRAGON_8087, NpcIdentifiers.BRUTAL_BLACK_DRAGON, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8093 -> {
                Projectile p1 = new Projectile(entity, target, 130, 51, duration, 43, 31, 0, target.getSize(), 5);
                final int delay = entity.executeProjectile(p1);
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).submit();

            }
            default -> System.err.println("Assigned brutal dragon script with no projectile, npc id " + npc.id());
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
