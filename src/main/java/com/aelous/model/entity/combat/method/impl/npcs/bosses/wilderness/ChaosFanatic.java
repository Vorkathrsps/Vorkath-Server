package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
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
    public void prepareAttack(Entity entity, Entity target) {
        if (!entity.isNpc() || !target.isPlayer())
            return;

        entity.forceChat(QUOTES[Utils.getRandom(QUOTES.length)]);

        //Send the explosives!
        NPC npc = (NPC) entity;
        if (World.getWorld().rollDie(20, 1)) //5% chance the npc sends explosives
            explosives(entity, target);

        if (World.getWorld().rollDie(30, 1)) //3.3% chance of getting disarmed
            disarm(target);

        // Attack the player
        attack(npc, target);
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
        new Projectile(npc, target, 554, 35, 12 * tileDist, 40, 25, 0, 15, 10).sendProjectile();
        var delay = Math.max(1, (20 + tileDist * 12) / 30);

        npc.animate(811);
        target.hit(npc, delay, CombatType.MAGIC).checkAccuracy().submit();
    }

    private void disarm(Entity target) {
        Player player = (Player) target;
        final Item item = player.getEquipment().get(EquipSlot.WEAPON);
        if (item != null && player.inventory().hasCapacityFor(item)) {
            player.getEquipment().remove(item, EquipSlot.WEAPON, true);
            player.getEquipment().unequip(EquipSlot.WEAPON);
            Autocasting.setAutocast(player, null);
            player.looks().resetRender();
            player.inventory().add(item);
            target.message("The fanatic disarms you!");
        }
    }

    private void explosives(Entity npc, Entity target) {
        var x = target.tile().x; //The target's x tile
        var z = target.tile().y; //The target's z tile

        //Handle the first explosive
        var explosive_one = new Tile(x + World.getWorld().random(2), z);
        var explosive_one_distance = npc.tile().distance(explosive_one);
        var explosive_one_delay = Math.max(1, (20 + explosive_one_distance * 12) / 30);

        //Handle the second explosive
        var explosive_two = new Tile(x, z + World.getWorld().random(2));
        var explosive_two_distance = npc.tile().distance(explosive_two);
        var explosive_two_delay = Math.max(1, (20 + explosive_two_distance * 12) / 30);

        //Handle the third explosive
        var explosive_three = new Tile(x, z + World.getWorld().random(2));
        var explosive_three_distance = npc.tile().distance(explosive_three);
        var explosive_three_delay = Math.max(1, (20 + explosive_three_distance * 12) / 30);

        //Send the projectiles
        new Projectile(npc.tile(), explosive_one, 0, 551, 24 * explosive_one_distance, explosive_one_delay, 50, 0, 0, 35, 10).sendProjectile();
        new Projectile(npc.tile(), explosive_two, 0, 551, 24 * explosive_two_distance, explosive_two_delay, 50, 0, 0, 35, 10).sendProjectile();
        new Projectile(npc.tile(), explosive_three, 0, 551, 24 * explosive_three_distance, explosive_three_delay, 50, 0, 0, 35, 10).sendProjectile();

        //Send the tile graphic
        World.getWorld().tileGraphic(157, explosive_one, 1, 24 * explosive_one_distance);
        World.getWorld().tileGraphic(157, explosive_two, 1, 24 * explosive_two_distance);
        World.getWorld().tileGraphic(552, explosive_three, 1, 24 * explosive_three_distance);
        //Create a delay before checking if the player is on the explosive tile
        target.runFn(6, () -> {
            //For each player in the world we..
            var target_x = target.tile().x;
            var target_z = target.tile().y;
            //Check to see if the player's tile is the same as the first explosive..
            if (target_x == explosive_one.x && target_z == explosive_one.y)
                target.hit(npc, World.getWorld().random(15), CombatType.MAGIC).submit();
            //Check to see if the player's tile is the same as the second explosive..
            if (target_x == explosive_two.x && target_z == explosive_two.y)
                target.hit(npc, World.getWorld().random(15), CombatType.MAGIC).submit();
        });
    }
}
