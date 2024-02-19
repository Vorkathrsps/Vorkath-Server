package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.cryptic.model.entity.attributes.AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE;

/**
 * Handles Venenatis' combat.
 *
 * @author Professor Oak
 */
public class VenenatisCombat extends CommonCombatMethod {

    @Override
    public void init(NPC npc) {
        npc.putAttrib(ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) {
            if (Utils.rollDie(3, 1)) {
                rangeAttack(entity, target);
            } else {
                magicAttack(entity, target);
            }
        } else {
            meleeAttack(entity, target);
        }

        //constructWeb(entity, target);
        return true;
    }

    private void spawnSpiderlings(@Nonnull Entity entity) {

    }

    public void meleeAttack(@NonNull final Entity entity, @NonNull Entity target) {
        entity.animate(9991);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    public void magicAttack(@Nonnull final Entity entity, @Nonnull Entity target) {

        entity.animate(9990);

        var tile = entity.tile().transform(4, 4, 0);

        var tileDist = tile.distance(target.tile());

        var duration = (tileDist * 2) + 25;

        Projectile p = new Projectile(entity, target, 2358, 25, duration, 37, 22, 14, 4, 48, 2);
        final int delay = entity.executeProjectile(p);

        target.graphic(2359, GraphicHeight.MIDDLE, p.getSpeed());

        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
        hit.submit();
    }

    private void rangeAttack(@Nonnull final Entity entity, @Nonnull Entity target) {
        entity.animate(9989);

        var tile = entity.tile().transform(4, 4, 0);

        var tileDist = tile.distance(target.tile());

        var duration = (tileDist * 2) + 25;

        Projectile p = new Projectile(entity, target, 2356, 25, duration, 37, 22, 14, 4, 2);

        final int delay = entity.executeProjectile(p);

        target.graphic(2357, GraphicHeight.LOW, p.getSpeed());

        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();

    }

    private void drainPrayer(Entity npc, Entity target) {
        if (target.isPlayer()) {
            var tileDist = npc.tile().transform(3, 3, 0).distance(target.tile());
            new Projectile(npc, target, 171, 30, 12 * tileDist, 25, 25, 0, true).sendProjectile();
            var player = target.getAsPlayer();
            var curpray = player.getSkills().level(Skills.PRAYER);
            var add = curpray / 5 + 1;
            var drain = 10 + add; // base 10 drain + 20% of current prayer + 1. Example 50 prayer becomes 30. Best tactic to keep prayer low.
            player.getSkills().alterSkill(Skills.PRAYER, (drain > curpray) ? -curpray : -drain);

            if (curpray > 0) {
                target.message("Your prayer was drained!");
            }
        }
    }

    public void constructWeb(Entity entity, Entity target) {

        entity.animate(9989);

        final Tile finalTile = target.tile();

        int tileDist = entity.tile().distance(target.tile());

        int duration = (51 + -5 + (10 * tileDist));

        Projectile p = new Projectile(entity, finalTile, 2360, 51, duration, 105, 0, 0, entity.getSize(), 10);

        p.send(entity, finalTile);

        World.getWorld().tileGraphic(2361, finalTile, 0, p.getSpeed());

        var webTile = p.getTarget().copy();

        int sideLength = 4;

        int centerX = webTile.getX();
        int centerY = webTile.getY();
        int tileZ = webTile.getZ();

        final int CENTER_OBJECT_ID = 47084;
        final int BORDER_OBJECT_ID = 47085;
        final int CORNER_OBJECT_ID = 47086;
        final int type = 10;

        int borderLength = sideLength + 2;
        int borderStartX = centerX - borderLength / 2;
        int borderStartY = centerY - borderLength / 2;
        int borderEndX = centerX + borderLength / 2;
        int borderEndY = centerY + borderLength / 2;

        int centerRotation = getRotation(centerX, centerY, centerX, centerY, sideLength, sideLength);

        int numObjects = sideLength * sideLength + 4 * sideLength + 4;

        List<GameObject> weblist = new ArrayList<>(numObjects);

        GameObject gameObject;

        gameObject = new GameObject(CENTER_OBJECT_ID, new Tile(centerX, centerY, tileZ), type, centerRotation);
        weblist.add(gameObject);

        int topLeftCornerRotation = 1;
        int leftCornerRotation = 2;
        int topRightCornerRotation = 0;
        int bottomRightCornerRotation = 3;

        for (int x = borderStartX; x <= borderEndX; x++) {
            for (int y = borderStartY; y <= borderEndY; y++) {
                boolean b = x == centerX || y == centerY || y == centerY - 1 || y == centerY + 1 || x == centerX - 1 || x == centerX + 1;

                int rotation;
                int objectId;
                int param;

                if (x >= centerX - sideLength / 2 && x <= centerX + sideLength / 2 &&
                    y >= centerY - sideLength / 2 && y <= centerY + sideLength / 2) {

                    if (b) {
                        objectId = CENTER_OBJECT_ID;
                        rotation = 0;
                        param = type;
                    } else if (y == centerY + 2 || y == centerY - 2) {
                        objectId = CORNER_OBJECT_ID;
                        param = type;

                        if (x == centerX - sideLength / 2 && y == centerY - sideLength / 2) {
                            rotation = topLeftCornerRotation;
                        } else if (x < centerX && y >= centerY) {
                            rotation = leftCornerRotation;
                        } else if (x <= centerX - sideLength / 2 && y >= centerY + sideLength / 2) {
                            rotation = leftCornerRotation;
                        } else if (x >= centerX && y < centerY) {
                            rotation = topRightCornerRotation;
                        } else {
                            rotation = bottomRightCornerRotation;
                        }
                    } else {
                        objectId = -1;
                        rotation = getRotation(x, y, centerX, centerY, sideLength, sideLength);
                        param = type;
                    }
                } else {
                    if (b) {
                        objectId = BORDER_OBJECT_ID;
                        rotation = getBorderRotation(x, y, centerX, centerY, sideLength);
                        param = type;
                    } else {
                        continue;
                    }
                }

                gameObject = new GameObject(objectId, new Tile(x, y, tileZ), param, rotation);
                weblist.add(gameObject);
            }
        }

        for (var t : weblist) {
            if (MovementQueue.dumbReachable(t.getX(), t.getY(), entity.tile())) {
                gameObject.spawn();
        GameObject finalGameObject = gameObject;
                Chain.noCtx().delay(20, () -> {
                    finalGameObject.remove();
                    weblist.clear();
                });
            }
        }

        weblist.forEach(GameObject::spawn);

    }

    public int getBorderRotation(int x, int y, int centerX, int centerY, int sideLength) {
        int rotation = 0;
        if (y == centerY + sideLength / 2 + 1 && x >= centerX - sideLength / 2 && x <= centerX + sideLength / 2) {
            // Object is in the northern direction
            rotation = 3;
        } else if (y == centerY - sideLength / 2 - 1 && x >= centerX - sideLength / 2 && x <= centerX + sideLength / 2) {
            // Object is in the southern direction
            rotation = 1;
        } else if (x == centerX - sideLength / 2 - 1 && y >= centerY - sideLength / 2 && y <= centerY + sideLength / 2) {
            // Object is in the western direction
            rotation = 2;
        } else if (x == centerX + sideLength / 2 + 1 && y >= centerY - sideLength / 2 && y <= centerY + sideLength / 2) {
            // Object is in the eastern direction
            rotation = 0;
        }
        return rotation;
    }

    public int getRotation(int tileX, int tileY, int startX, int startY, int squareWidth, int squareHeight) {
        if (tileX == startX + squareWidth && tileY == startY - 1) {
            return 0; // Bottom right corner - Rotate East
        } else if (tileX == startX - 1 && tileY == startY + squareHeight) {
            return 2; // Top left corner - Rotate West
        } else if (tileX == startX + squareWidth && tileY == startY + squareHeight) {
            return 3; // Bottom left corner - Rotate South
        } else if (tileX == startX - 1 && tileY == startY - 1) {
            return 1; // Top right corner - Rotate North
        } else if (tileX == startX - 1) {
            return 2; // West border outline - Rotate South
        } else if (tileX == startX + squareWidth) {
            return 0; // East border outline - Rotate North
        } else if (tileY == startY - 1) {
            return 1; // North border outline - Rotate West
        } else if (tileY == startY + squareHeight) {
            return 3; // South border outline - Rotate East
        }
        return 0;
    }

    @Override
    public int getAttackSpeed(@NonNull final Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(@NonNull final Entity entity) {
        return 10;
    }

}
