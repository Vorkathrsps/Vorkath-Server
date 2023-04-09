package com.aelous.model.entity.combat.method.impl.npcs.bosses.custom;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.Direction;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | June, 14, 2021, 14:44
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class BrutalLavaDragon extends CommonCombatMethod {

    private static final int FLYING_DRAGONFIRE = 7871;
    private static final int HEADBUTT = 91;

    private boolean headbutt; //prevents headbutt twice in a row

    private void knockback() {
        int vecX = (target.getAbsX() - getClosestX());
        if (vecX != 0)
            vecX /= Math.abs(vecX); // determines X component for knockback
        int vecY = (target.getAbsY() - getClosestY());
        if (vecY != 0)
            vecY /= Math.abs(vecY); // determines Y component for knockback
        int endX = target.getAbsX();
        int endY = target.getAbsY();
        for (int i = 0; i < 4; i++) {
            if (DumbRoute.getDirection(endX, endY, entity.getZ(), target.getSize(), endX + vecX, endY + vecY) != null) { // we can take this step!
                endX += vecX;
                endY += vecY;
            } else
                break; // cant take the step, stop here
        }
        Direction dir;
        if (vecX == -1)
            dir = Direction.EAST;
        else if (vecX == 1)
            dir = Direction.WEST;
        else if (vecY == -1)
            dir = Direction.NORTH;
        else
            dir = Direction.SOUTH;

        if (endX != target.getAbsX() || endY != target.getAbsY()) { // only do movement if we can take at least one step
            if (target != null) {
                Chain.bound(null).runFn(1, () -> {
                    final Player p = target.getAsPlayer();
                    p.lock();
                    p.animate(1157);
                    p.graphic(245, GraphicHeight.LOW, 124);
                    p.hit(entity,20);
                    p.stun(2, true);
                    int diffX = World.getWorld().random(2);
                    int diffY = World.getWorld().random(2);
                    TaskManager.submit(new ForceMovementTask(p, 1, new ForceMovement(p.tile().clone(), new Tile(diffX, diffY), 10, 60, dir.clientValue)));
                    p.message("The brutal lava dragon roars and throws you backwards.");
                    p.unlock();
                });
            }
        } else {
            target.hit(entity,20);
            target.animate(1157);
            target.graphic(245, GraphicHeight.LOW, 124);
            target.stun(2, true);
            if (target != null)
                target.message("The brutal lava dragon roars and throws you backwards.");
        }
    }

    private int getClosestX() {
        if (target.getAbsX() < entity.getAbsX())
            return entity.getAbsX();
        else if (target.getAbsX() >= entity.getAbsX() && target.getAbsX() <= entity.getAbsX() + entity.getSize() - 1)
            return target.getAbsX();
        else
            return entity.getAbsX() + entity.getSize() - 1;
    }

    private int getClosestY() {
        if (target.getAbsY() < entity.getAbsY())
            return entity.getAbsY();
        else if (target.getAbsY() >= entity.getAbsY() && target.getAbsY() <= entity.getAbsY() + entity.getSize() - 1)
            return target.getAbsY();
        else
            return entity.getAbsY() + entity.getSize() - 1;
    }


    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        //10% chance that the wold boss skulls you!
        if(World.getWorld().rollDie(10,1)) {
            Skulling.assignSkullState(((Player) target), SkullType.WHITE_SKULL);
            target.message("The "+entity.getMobName()+" has skulled you, be careful!");
        }

        if (World.getWorld().rollDie(15, 1) && !headbutt && target.tile().isWithinDistance(entity.tile(),3)) {
            entity.forceChat("HEADBUTT");
            entity.animate(HEADBUTT);
            knockback();
            headbutt = true;
        }

        magicAttack((NPC) entity, target);
        return true;
    }

    private void magicAttack(NPC npc, Entity target) {
        npc.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if(p != null && target.tile().inSqRadius(p.tile(),12)) {
                boolean dragon_fire = World.getWorld().rollDie(2, 1);
                var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
                var delay = Math.max(1, (20 + (tileDist * 12)) / 30);

                Projectile DRAGONFIRE_PROJ_FLYING = new Projectile(entity, p, 54, 50, 12 * tileDist, 150, 32, 0);
                Projectile FIRE_PROJ_FLYING = new Projectile(entity, p, 1465, 51, 12 * tileDist, 160, 31, 0);

                (dragon_fire ? DRAGONFIRE_PROJ_FLYING : FIRE_PROJ_FLYING).sendProjectile();
                entity.animate(FLYING_DRAGONFIRE);
                entity.getAsNpc().getCombatInfo().maxhit = dragon_fire ? 58 : 21;

                p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
                headbutt = false;
            }
        });

        npc.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
