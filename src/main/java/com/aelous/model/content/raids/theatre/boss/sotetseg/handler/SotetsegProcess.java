package com.aelous.model.content.raids.theatre.boss.sotetseg.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SotetsegProcess extends NPC {
    private static final int WIDTH = 14, HEIGHT = 15;
    Player player;
    Theatre theatre;
    TheatreArea theatreArea;
    int magicAttackCount = 0;
    private int intervalCount = 0;
    private int attackInterval = 5;
    private int randomAttack = 0;

    public SotetsegProcess(int id, Tile tile, Player player, Theatre theatre, TheatreArea theatreArea) {
        super(id, tile);
        this.player = player;
        this.theatre = theatre;
        this.theatreArea = theatreArea;
        this.setCombatMethod(null);
        this.spawnDirection(Direction.SOUTH.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.getMovementQueue().setBlockMovement(true);
    }

    public void sequenceCombat() {
        if (Utils.sequenceRandomInterval(randomAttack, 7, 14) && DumbRoute.withinDistance(this, player, 1)) {
            this.sendMeleeAttack();
        } else {
            if (magicAttackCount == 10) {
                this.sendSpecialMagicAttack();
            } else {
                this.sendRandomMageOrRange();
            }
        }
    }

    public void sendRandomMageOrRange() {
        int[] projectileIds = new int[]{1606, 1607};
        var randomProjectile = Utils.randomElement(projectileIds);
        this.animate(8139);
        int tileDist = this.tile().distance(player.tile());
        int duration = (70 + 30 + (20 * tileDist));
        Projectile p = new Projectile(this, player, randomProjectile, 70, duration, 43, 21, 25, 5, 10);
        final int delay = this.executeProjectile(p);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, randomProjectile == 1606 ? CombatType.MAGIC : CombatType.RANGED), delay, randomProjectile == 1606 ? CombatType.MAGIC : CombatType.RANGED).checkAccuracy().postDamage(d -> {
            if (randomProjectile == 1606) {
                magicAttackCount++;
            }
            if (randomProjectile == 1606 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MISSILES)) {
                Prayers.closeAllPrayers(player);
                player.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 2);
                d.setDamage(Utils.random(1, 50));
            } else if (randomProjectile == 1607 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                Prayers.closeAllPrayers(player);
                player.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 2);
                d.setDamage(Utils.random(1, 50));
            } else {
                if (d.getDamage() == 0) {
                    d.block();
                }
            }
        });
        hit.submit();
    }

    public void sendSpecialMagicAttack() {
        magicAttackCount = 0;
        this.animate(8139);
        int tileDist = this.tile().distance(player.tile());
        int duration = (70 + 25 + (25 * tileDist));
        Projectile p = new Projectile(this, player, 1604, 70, duration, 50, 0, 50, 5, 10);
        final int delay = this.executeProjectile(p);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, CombatType.MAGIC), delay, CombatType.MAGIC).setAccurate(true);
        hit.setDamage(121);
        hit.submit();
        this.graphic(101, GraphicHeight.MIDDLE, p.getSpeed());
    }

    public void sendMeleeAttack() {
        this.animate(8138);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy();
        hit.submit();
    }

    @Override
    public void postSequence() {
        intervalCount++;
        attackInterval--;
        if (intervalCount >= 5 && attackInterval <= 0 && !this.dead()) {
            this.sequenceCombat();
            intervalCount = 0;
            attackInterval = 5;
        }
    }

    @Override
    public void die() {
        World.getWorld().unregisterNpc(this);
    }

    public void sendShadowRealm() {
        for (var t : generateMazePath()) {

        }
    }

    public List<Tile> generateMazePath() {
        final List<Tile> pathOffsets = new ArrayList<>();

        // the minimum and maximum amount of tiles to go in one direction before being able to potentially change into another direction
        final int minimumStreak = 2;
        final int maximumStreak = 6;

        //start at a random location on the x-axis
        Tile last = Tile.create(Utils.random(1, WIDTH - 1), 0, theatreArea.getzLevel());
        pathOffsets.add(last);

        //System.out.println("Starting tile: " + last + ", " + (shadowMazeStart.transform(last)));

        Direction lastDirection = Direction.NORTH;
        int currentStreak = 1;
        while (last.getY() < HEIGHT - 1) {
            final Tile proposedLocation = last.transform(new Tile(lastDirection.x, lastDirection.y));

            //check if we need to find a new direction to go instead
            boolean changeDirections = false;
            if (proposedLocation.getX() == 0 || proposedLocation.getX() == WIDTH - 1) {
                //if we hit the borders of the maze
                changeDirections = true;
            } else if (currentStreak == maximumStreak) {
                //if the streak is too long
                changeDirections = true;
            } else if (currentStreak >= minimumStreak && Utils.random(10) >= 4) {
                //randomly change directions if we have the minimum streak
                changeDirections = true;
            }

            if (changeDirections) {
                //obtain a list of directions we can alternatively go in
                final Direction lastDir = lastDirection;
                final List<Direction> possibleDirections = Arrays.stream(Direction.ORTHOGONAL)
                    .filter(dir -> dir != Direction.SOUTH && dir != lastDir && dir != lastDir.opposite())
                    .collect(Collectors.toList());
                //shuffle
                Collections.shuffle(possibleDirections);
                //iterate and check the validity of minimum future tiles in that direction
                for (Direction direction : possibleDirections) {
                    final Tile stretchTo = last.transform(direction.toInteger(), minimumStreak);
                    if (stretchTo.getX() >= 1 && stretchTo.getX() <= WIDTH - 2) {
                        lastDirection = direction;
                        //System.out.println("--> SWITCHING DIRECTION TO " + direction.name());
                        break;
                    }
                }

                currentStreak = 1;
            } else {
                currentStreak++;
            }

            last = last.transform(new Tile(lastDirection.x, lastDirection.y));
            //System.out.println("Moving in direction " + lastDirection.name() + " to " + shadowMazeStart.transform(last));
            pathOffsets.add(last);
        }
        return pathOffsets;
    }

}
