package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.Utils;

public class DerangedArchaeologist extends CommonCombatMethod {

    private static final String[] misc_shout = {
        "Respect me!",
        "Get out of here!",
        "No-one messes with us!",
        "These books are all mine!",
        "Taste my knowledge!",
        "You belong in a museum!",
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;
        //Check to see if we're able to melee the target..
        //  if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
        //Send the explosive books!
        //   if (Utils.rollDie(20, 1)) { // 5% chance the target sends explosive books
        //Animate the NPC
        //      npc.animate(3353);
        //      Chain.bound(null).runFn(1, () -> special_attack(npc, target));

        //..take a nap
        // //      entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);
        //   }

        //Attack the player
        //    melee_attack(npc, target);
//
        //..and take a quick nap
        //   entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);

        //Else we check if we can range the target..
        //  if (CombatFactory.canReach(entity, CombatFactory.RANGED_COMBAT, target)) {

        //Send the explosive books!
        //     if (Utils.rollDie(20, 1)) { // 5% chance the target sends explosive books
        //Animate the NPC
        //       npc.animate(3353);

        special_attack(npc, target);

        //..take a nap
        //       entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);
        //    }
        //Attack the player
        //     ranged_attack(npc, target);

        //..and take a quick nap
        //       entity.getTimers().register(TimerKey.COMBAT_ATTACK, 3);
        //    }
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
        //Shout!
        npc.forceChat(misc_shout[Utils.random(5)]);

        //Send the projectile and animate the NPC
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1259, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        npc.animate(3353);

        //Determine the damage dealt to the target
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();

        //Roll a die to see if we send the target graphic
        if (Utils.rollDie(3, 1))
            target.graphic(305, GraphicHeight.LOW, 24 * tileDist);
    }

    //Handle the special attack
    private void special_attack(NPC npc, Entity target) {
      /*  var targ = (Player) target;
        int x = targ.tile().x;
        int y = targ.tile().y;

        String special_shout = "Rain of knowledge!";
        npc.forceChat(special_shout);

        Tile explosive_book_one = new Tile(x + Utils.random(2) + 1, y + Utils.random(2) + 1);

        Tile explosive_book_two = new Tile(x + Utils.random(2), y + Utils.random(2) + 1);

        Tile explosive_book_three = new Tile(x + Utils.random(1), y);

        List<Tile> tileList = new ArrayList<>();

        var tileDist1 = npc.tile().distance(explosive_book_one);
        int duration1 = (40 + -5 + (10 * tileDist1));
        Projectile p = new Projectile(npc, targ, 1260, 40, duration1, 50, 0, 0, 0, 0);
        p.send(npc, explosive_book_one);
        var tileDist2 = npc.tile().distance(explosive_book_one);
        int duration2 = (40 + -5 + (10 * tileDist2));
        Projectile p2 = new Projectile(npc, explosive_book_two, 1260, 40, duration2, 50, 0, 0, 0, 0);
        p2.send(npc, explosive_book_two);
        var tileDist3 = npc.tile().distance(explosive_book_two);
        int duration3 = (40 + -5 + (10 * tileDist3));
        Projectile p3 = new Projectile(npc, explosive_book_three, 1260, 40, duration3, 50, 0, 0, 0, 0);
        p3.send(npc, explosive_book_three);

        targ.getPacketSender().sendTileGraphic(157, explosive_book_one, 0, p.getSpeed());
        targ.getPacketSender().sendTileGraphic(157, explosive_book_two, 0, p2.getSpeed());
        targ.getPacketSender().sendTileGraphic(157, explosive_book_three, 0, p3.getSpeed());

        var tileDist4 = npc.tile().distance(explosive_book_two);
        int duration4 = (110 + -5 + (15 * tileDist4));
        Projectile p4 = new Projectile(explosive_book_two, World.getWorld().randomTileAround(explosive_book_two, 6), 1260, 110, duration4, 50, 0, 0, 0, 0);
        p4.send(explosive_book_two, explosive_book_three);
        targ.getPacketSender().sendTileGraphic(157, explosive_book_three, 0, p4.getSpeed());

        npc.getCombat().delayAttack(6);

         /*   Chain.bound(null).waitForTile(new Tile(ricochet_explosive_book_one.getX(), ricochet_explosive_book_one.getY(), ricochet_explosive_book_one.getZ()), () -> {
                if (target.tile().inSqRadius(explosive_book_one, 1))
                    target.hit(npc, Math.min(20, Utils.random(23)));
                if (target.tile().inSqRadius(explosive_book_two, 1))
                    target.hit(npc, Math.min(20, Utils.random(23)));
                if (target.tile().inSqRadius(explosive_book_three, 1))
                    target.hit(npc, Math.min(20, Utils.random(23)));
                target.getAsPlayer().getPacketSender().sendTileGraphic(157, ricochet_explosive_book_one, 1, p.getSpeed());
                target.getAsPlayer().getPacketSender().sendTileGraphic(157, ricochet_explosive_book_two, 1, p2.getSpeed());
            }).waitForTile(new Tile(ricochet_explosive_book_two.getX(), ricochet_explosive_book_two.getY(), ricochet_explosive_book_two.getZ()), () -> {

                Projectile p4 = new Projectile(entity, explosive_book_two, 1260, 24, duration3, 50, 0, 0, target.getSize(), 10);
                p4.send(entity, explosive_book_two);

                Projectile p5 = new Projectile(entity, explosive_book_three, 1260, 24, duration3, 50, 0, 0, target.getSize(), 10);
                p5.send(entity, explosive_book_three);

                target.getAsPlayer().getPacketSender().sendTileGraphic(157, ricochet_explosive_book_one, 1, p4.getSpeed());
                target.getAsPlayer().getPacketSender().sendTileGraphic(157, ricochet_explosive_book_two, 1, p5.getSpeed());

            }).waitForTile(new Tile(explosive_book_three.getX(), explosive_book_three.getY(), explosive_book_three.getZ()), () -> {
                //Check to see if the player's tile is the same as the first explosive book..
                if (target.tile().inSqRadius(ricochet_explosive_book_one, 1))
                    target.hit(npc, Math.min(20, Utils.random(23)));
                //Check to see if the player's tile is the same as the second explosive book..
                if (target.tile().inSqRadius(ricochet_explosive_book_two, 1))
                    target.hit(npc, Math.min(20, Utils.random(23)));
            });*/

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 5;
    }
}
