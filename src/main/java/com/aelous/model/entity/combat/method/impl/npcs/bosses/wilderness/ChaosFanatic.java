package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

public class ChaosFanatic extends CommonCombatMethod {

    private static final String[] QUOTES = {
        "Burn!",
        "WEUGH!",
        "Develish Oxen Roll!",
        "All your wilderness are belong to them!",
        "AhehHeheuhHhahueHuUEehEahAH",
        "I shall call him squidgy and he shall be my squidgy!",
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!entity.isNpc() || !target.isPlayer())
            return false;

        entity.forceChat(QUOTES[Utils.getRandom(QUOTES.length)]);

        NPC npc = (NPC) entity;
        if (Utils.securedRandomChance(0.10)) {
            explosives(entity, target);
        } else if (Utils.securedRandomChance(0.05)) {
            disarm(target);
        } else {
            attack(npc, target);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

    private void attack(NPC npc, Entity target) {
        var tileDist = npc.tile().distance(target.tile());
        int duration = (64 + 11 + (10 * tileDist));

        Projectile p = new Projectile(entity, target, 554, 64, duration, 43, 31, 0, target.getSize(), 10);

        final int delay = entity.executeProjectile(p);

        npc.animate(811);

        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

        hit.submit();
    }

    private void disarm(Entity target) {
        Player player = (Player) target;
        final Item item = player.getEquipment().get(EquipSlot.WEAPON);
        if (item != null && player.inventory().hasCapacity(item)) {
            player.getEquipment().remove(item, EquipSlot.WEAPON, true);
            player.getEquipment().unequip(EquipSlot.WEAPON);
            WeaponInterfaces.updateWeaponInterface(player);
            Autocasting.setAutocast(player, null);
            player.looks().resetRender();
            if (player.inventory().isFull()) {
                return;
            } else {
                player.inventory().add(item);
            }
            target.message("The fanatic disarms you!");
        }
    }

    private void explosives(Entity npc, Entity target) {
        var x = target.tile().x; //The target's x tile
        var z = target.tile().y; //The target's z tile
        var tileDist = npc.tile().distance(target.tile());
        int duration = (64 + 11 + (10 * tileDist));
        npc.animate(811);
        var explosive_one = new Tile(x + World.getWorld().random(2), z);
        Projectile p1 = new Projectile(entity, target, 551, 64, duration, 43, 0, 0, target.getSize(), 10);
        final int delay1 = entity.executeProjectile(p1);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay1, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        World.getWorld().tileGraphic(157, explosive_one, 0, p1.getSpeed());
    }
}
