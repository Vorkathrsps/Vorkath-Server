package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

public class CrazyArchaeologist extends CommonCombatMethod {

    private static final String[] misc_shout = {
        "I'm Bellock - respect me!",
        "Get off my site!",
        "No-one messes with Bellock's dig!",
        "These ruins are mine!",
        "Taste my knowledge!",
        "You belong in a museum!",
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;
        //Check to see if we're able to melee the target..
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            //Send the explosive books!
            if (Utils.rollDie(20, 1)) { // 5% chance the target sends explosive books
                //Animate the NPC
                npc.animate(3353);
                Chain.bound(null).runFn(1, () -> special_attack(npc, target));

                //..take a nap
                entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);
            }

            //Attack the player
            melee_attack(npc, target);

            //..and take a quick nap
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);

            //Else we check if we can range the target..
        } else if (CombatFactory.canReach(entity, CombatFactory.RANGED_COMBAT, target)) {

            //Send the explosive books!
            if (Utils.rollDie(20, 1)) { // 5% chance the target sends explosive books
                //Animate the NPC
                npc.animate(3353);

                Chain.bound(null).runFn(1, () -> special_attack(npc, target));

                //..take a nap
                entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);
            }
            //Attack the player
            ranged_attack(npc, target);

            //..and take a quick nap
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);
        }
        return true;
    }

    //Handle the melee attack
    private void melee_attack(NPC npc, Entity target) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();

        //Shout!
        npc.forceChat(misc_shout[Utils.random(5)]);

        //Animate the NPC
        npc.animate(npc.attackAnimation());
    }

    //Handle the ranged attack
    private void ranged_attack(NPC npc, Entity target) {
        int tileDist = npc.tile().distance(target.tile());
        int delay = Math.max(1, (20 + tileDist * 12) / 30);

        //Shout!
        npc.forceChat(misc_shout[Utils.random(5)]);

        //Send the projectile and animate the NPC
        new Projectile(npc, target, 1259, 35, 6 * tileDist, 25, 25, 0).sendProjectile();
        npc.animate(3353);

        //Determine the damage dealt to the target
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();

        //Roll a die to see if we send the target graphic
        if (Utils.rollDie(3, 1))
            target.graphic(305, GraphicHeight.LOW, 24 * tileDist);
    }

    //Handle the special attack
    private void special_attack(NPC npc, Entity target) {
        int x = target.tile().x; //The target's x tile
        int y = target.tile().y; //The target's y tile

        //Handle the first explosive
        Tile explosive_book_one = new Tile(x + Utils.random(2) + 1, y + Utils.random(2) + 1);
        int explosive_book_one_distance = npc.tile().distance(explosive_book_one);
        int explosive_book_one_delay = Math.max(1, (20 + explosive_book_one_distance * 12) / 30);

        //Handle the second explosive
        Tile explosive_book_two = new Tile(x + Utils.random(2), y + Utils.random(2) + 1);
        int explosive_book_two_distance = npc.tile().distance(explosive_book_two);
        int explosive_book_two_delay = Math.max(1, (20 + explosive_book_two_distance * 12) / 30);

        //Handle the third explosive
        Tile explosive_book_three = new Tile(x + Utils.random(1), y);
        int explosive_book_three_distance = npc.tile().distance(explosive_book_three);
        int explosive_book_three_delay = Math.max(1, (20 + explosive_book_three_distance * 12) / 30);

        //Shout!
        String special_shout = "Rain of knowledge!";
        npc.forceChat(special_shout);

        //Send the projectiles
        new Projectile(npc.tile(), explosive_book_one, 0, 1260, 24 * explosive_book_one_distance, explosive_book_one_delay, 50, 0, 0).sendProjectile();
        new Projectile(npc.tile(), explosive_book_two, 0, 1260, 24 * explosive_book_two_distance, explosive_book_two_delay, 50, 0, 0).sendProjectile();
        new Projectile(npc.tile(), explosive_book_three, 0, 1260, 24 * explosive_book_three_distance, explosive_book_three_delay, 50, 0, 0).sendProjectile();

        //Send the tile graphic
        target.getAsPlayer().getPacketSender().sendTileGraphic(157, explosive_book_one, 1, 24 * explosive_book_one_distance);
        target.getAsPlayer().getPacketSender().sendTileGraphic(157, explosive_book_two, 1, 24 * explosive_book_two_distance);
        target.getAsPlayer().getPacketSender().sendTileGraphic(157, explosive_book_three, 1, 24 * explosive_book_three_distance);

        //Create a delay before checking if the player is on the explosive tile
        Chain.bound(null).name("explosive_book_one_task").runFn(explosive_book_one_distance, () -> {
            //For each player in the world we..
            //Check to see if the player's tile is the same as the first explosive book..
            if (target.tile().inSqRadius(explosive_book_one, 1))
                target.hit(npc, Math.min(20, Utils.random(23)));
            //Check to see if the player's tile is the same as the second explosive book..
            if (target.tile().inSqRadius(explosive_book_two, 1))
                target.hit(npc, Math.min(20, Utils.random(23)));
            //Check to see if the player's tile is the same as the third explosive book..
            if (target.tile().inSqRadius(explosive_book_three, 1))
                target.hit(npc, Math.min(20, Utils.random(23)));
        });

        //Grab the coordinates of the tile our ricochets come from..
        int explosive_x = explosive_book_two.x; //The x coordinates of explosive book two
        int explosive_z = explosive_book_two.y; //The z coordinates of explosive book two

        //Handle the first ricochet
        Tile ricochet_explosive_book_one = new Tile(explosive_x + 2, explosive_z + 1 + Utils.random(2));
        int ricochet_explosive_book_one_distance = explosive_book_two.distance(ricochet_explosive_book_one);
        int ricochet_explosive_book_one_delay = Math.max(1, (20 + ricochet_explosive_book_one_distance * 12) / 30);

        //Handle the second ricochet
        Tile ricochet_explosive_book_two = new Tile(explosive_x + 1, explosive_z + Utils.random(2) + 2);
        int ricochet_explosive_book_two_distance = explosive_book_two.distance(ricochet_explosive_book_two);
        int ricochet_explosive_book_two_delay = Math.max(1, (20 + ricochet_explosive_book_two_distance * 12) / 30);

        //Create a delay before sending the ricochet explosives
        Chain.bound(null).name("explosive_book_two_task").runFn(explosive_book_two_distance, () -> {

            //Send the projectiles
            new Projectile(explosive_book_two, ricochet_explosive_book_one, 0, 1260, 50 * ricochet_explosive_book_one_distance, ricochet_explosive_book_one_delay, 0, 0, 0).sendProjectile();
            new Projectile(explosive_book_two, ricochet_explosive_book_two, 0, 1260, 50 * ricochet_explosive_book_two_distance, ricochet_explosive_book_two_delay, 0, 0, 0).sendProjectile();

            //Send the tile graphic
            target.getAsPlayer().getPacketSender().sendTileGraphic(157, ricochet_explosive_book_one, 1, 50 * ricochet_explosive_book_one_distance);
            target.getAsPlayer().getPacketSender().sendTileGraphic(157, ricochet_explosive_book_two, 1, 50 * ricochet_explosive_book_two_distance);

            //Create a delay before checking if the player is on the explosive tile
        });

        Chain.bound(null).name("ricochet_explosive_book_one_task").runFn(ricochet_explosive_book_one_delay, () -> {
            //Check to see if the player's tile is the same as the first explosive book..
            if (target.tile().inSqRadius(ricochet_explosive_book_one, 1))
                target.hit(npc, Math.min(20, Utils.random(23)));
            //Check to see if the player's tile is the same as the second explosive book..
            if (target.tile().inSqRadius(ricochet_explosive_book_two, 1))
                target.hit(npc, Math.min(20, Utils.random(23)));
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 5;
    }
}
