package com.cryptic.model.entity.combat.method.impl.npcs.dragons;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;


/**
 * @author Origin | Zerikoth | PVE
 * @date maart 14, 2020 13:28
 */
public class BrutalDragonsCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var random = World.getWorld().random().nextInt(0, 4);
        NPC npc = (NPC) entity;
        switch (random) {
            case 0, 1 -> {
                if (isReachable()) basicAttack(npc, target);
                else if (isReachable()) breathFire(npc, target);
            }
            case 2, 3 -> magicAttack(npc, target);
            case 4, 5 -> {
                if (isReachable()) breathFire(npc, target);
            }
        }
        return true;
    }

    private void breathFire(Entity entity, Entity target) {
        if (World.getWorld().clipAt(entity.tile()) != 0) return;
        entity.animate(81);
        entity.graphic(1, GraphicHeight.HIGH, 0);
        if (target instanceof Player player) {
            int antifire = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire > 0;

            var max_damage = 50.0D;

            Hit hit = new Hit(entity, target, 2, CombatType.MAGIC).checkAccuracy(true).submit();

            if (player.getEquipment().containsAny(ItemIdentifiers.DRAGONFIRE_WARD, ItemIdentifiers.DRAGONFIRE_WARD_22003, ItemIdentifiers.DRAGONFIRE_SHIELD, ItemIdentifiers.DRAGONFIRE_SHIELD_11284, ItemIdentifiers.ANTIDRAGON_SHIELD, ItemIdentifiers.ANTIDRAGON_SHIELD_8282)) {
                player.message("Your shield absorbs most of the dragon fire!");
                max_damage *= 0.10D;
            }

            if (Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max_damage *= 0.20D;
            }

            if (antifire > 0) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max_damage *= 0.30D;
            }

            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false)) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max_damage = 0;
            }

            if (hasShield && hasPotion) {
                max_damage = 0;
            }

            var damage = Utils.random(1, (int) max_damage);

            if (max_damage == 0) {
                damage = 0;
            }

            if (damage == 50) player.message("You are badly burned by the dragon fire!");

            int finalDamage = damage;
            hit.postDamage(post -> {
                if (post.isAccurate() && post.getDamage() > 0) post.setDamage(finalDamage);
                else hit.block();
            });
        }
    }

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    private void magicAttack(NPC npc, Entity entity) {
        npc.animate(6722);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        switch (npc.id()) {
            case NpcIdentifiers.BRUTAL_GREEN_DRAGON, NpcIdentifiers.BRUTAL_GREEN_DRAGON_8081 -> {
                Projectile p1 = new Projectile(entity, target, 133, 51, duration, 43, 31, 0, entity.getSize(), 5);
                final int delay = entity.executeProjectile(p1);
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).submit();

            }
            case NpcIdentifiers.BRUTAL_BLUE_DRAGON, NpcIdentifiers.BRUTAL_RED_DRAGON -> {
                Projectile p1 = new Projectile(entity, target, 136, 51, duration, 43, 31, 0, entity.getSize(), 5);
                final int delay = entity.executeProjectile(p1);
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).submit();

            }
            case NpcIdentifiers.BRUTAL_RED_DRAGON_8087, NpcIdentifiers.BRUTAL_BLACK_DRAGON, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092, NpcIdentifiers.BRUTAL_BLACK_DRAGON_8093 -> {
                Projectile p1 = new Projectile(entity, target, 130, 51, duration, 43, 31, 0, entity.getSize(), 5);
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
