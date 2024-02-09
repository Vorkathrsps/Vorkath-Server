package com.cryptic.model.entity.masks;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;
import lombok.val;

/**
 * A graphic propelled through the air by some sort of spell, weapon, or other
 * miscellaneous force.
 *
 * @author lare96
 */
public final class Projectile {

    /**
     * The starting position of the projectile.
     */
    private Tile start, target;

    /**
     * The offset position of the projectile.
     */
    private final Tile offset;

    private final int creatorSize, startDistanceOffset;

    /**
     * The speed of the projectile.
     */
    private final int speed;

    /**
     * The id of the projectile.
     */
    private final int projectileId;

    /**
     * The starting height of the projectile.
     */
    private final int startHeight;

    /**
     * The ending height of the projectile.
     */
    private final int endHeight;

    /**
     * The lock on value of the projectile.
     */
    private final int lockon;

    /**
     * The delay of the projectile.
     */
    private int delay;

    /**
     * The curve angle of the projectile.
     */
    public int angle;

    /**
     * The slope of the projectile.
     */
    public int slope;

    /**
     * The radius that the projectile is launched from.
     */
    public int radius;

    @Getter
    public final int stepMultiplier;

    public Projectile(Tile start, Tile end, int lockon,
                      int projectileId, int speed, int delay, int startHeight, int endHeight,
                      int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this.start = start;
        this.target = end;
        int offX = (start.getY() - end.getY()) * -1; // yes inverted
        int offY = (start.getX() - end.getX()) * -1;
        this.offset = new Tile(offX, offY, start.getZ());
        this.creatorSize = creatorSize;
        this.startDistanceOffset = startDistanceOffset;
        this.lockon = lockon;
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.slope = curve;
        this.stepMultiplier = stepMultiplier;
    }

    /**
     * Old method NOT USED
     *
     * @param start
     * @param end
     * @param lockon
     * @param projectileId
     * @param speed
     * @param delay
     * @param startHeight
     * @param endHeight
     * @param curve
     */
    public Projectile(Tile start, Tile end, int lockon, int projectileId, int speed, int delay, int startHeight, int endHeight, int curve) {
        this(start, end, lockon, projectileId, speed, delay, startHeight, endHeight, curve, 1, 64, 0);
    }

    /**
     * Entity to Entity Lockon
     *
     * @param source
     * @param victim
     * @param projectileId
     * @param delay
     * @param speed
     * @param startHeight
     * @param endHeight
     * @param curve
     * @param creatorSize
     * @param stepMultiplier
     */
    public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source.getCentrePosition(),
            victim.getCentrePosition(),
            victim.getProjectileLockonIndex(),
            projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    public Projectile(Entity source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this(source.getCentrePosition(),
            victim.getCentrePosition(),
            victim.getProjectileLockonIndex(),
            projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, startDistanceOffset, stepMultiplier);
    }

    public Projectile(Tile source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this(source,
            victim.getCentrePosition(),
            victim.getProjectileLockonIndex(),
            projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, startDistanceOffset, stepMultiplier);
    }

    public Projectile(Tile source, Tile victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this(source,
            victim,
            0,
            projectileId,
            speed,
            delay,
            startHeight,
            endHeight,
            curve,
            creatorSize,
            startDistanceOffset,
            stepMultiplier);
    }

    public Projectile(Tile source, Tile victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source, victim,
            0, projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    /**
     * Entity TO Target Tile no-lockon
     *
     * @param source
     * @param victim
     * @param projectileId
     * @param delay
     * @param speed
     * @param startHeight
     * @param endHeight
     * @param curve
     * @param creatorSize
     * @param stepMultiplier
     */
    public Projectile(Entity source, Tile victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source.getCentrePosition(), victim,
            0, projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }


    /**
     * Larger Entity Tile to Target Lockon
     *
     * @param source
     * @param victim
     * @param projectileId
     * @param delay
     * @param speed
     * @param startHeight
     * @param endHeight
     * @param curve
     * @param creatorSize
     * @param stepMultiplier
     */
    public Projectile(Tile source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source, victim.getCentrePosition(),
            victim.getProjectileLockonIndex(), projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    /**
     * OLD NOT USED
     *
     * @param source
     * @param victim
     * @param projectileId
     * @param delay
     * @param speed
     * @param startHeight
     * @param endHeight
     * @param curve
     */
    public Projectile(Entity source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve) {
        this(source.getCentrePosition(), victim.getCentrePosition(),
            victim.getProjectileLockonIndex(), projectileId, speed, delay,
            startHeight, endHeight, curve, source.getSize(), 0, 0);
    }

    /**
     * OLD NOT USED
     *
     * @param source
     * @param victim
     * @param projectileId
     * @param delay
     * @param speed
     * @param startHeight
     * @param endHeight
     * @param angle
     * @param forNpc
     */
    public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int angle, boolean forNpc) {
        this(source.getCentrePosition(), victim.getCentrePosition(), victim.getProjectileLockonIndex(), projectileId, speed, delay, startHeight, endHeight, angle, 16, 64, 0);
    }

    public void sendProjectile() {
        for (var p : this.getStart().getRegion().getPlayers()) {
            if (p == null || !start.isViewableFrom(p.tile())) continue;
            if (start.getZ() != p.getZ()) continue;
            p.getPacketSender().sendProjectile(start.x, start.y, offset.x, offset.y, 0,
                speed, projectileId, startHeight, endHeight, lockon, delay, slope,
                creatorSize, startDistanceOffset);
        }
    }

    /**
     * Gets the starting position of the projectile.
     *
     * @return the starting position of the projectile.
     */
    public Tile getStart() {
        return start;
    }

    /**
     * Gets the ending position of the projectile.
     *
     * @return the starting position of the projectile.
     */
    public Tile getEnd() {
        return target;
    }

    /**
     * Gets the offset position of the projectile.
     *
     * @return the offset position of the projectile.
     */
    public Tile getOffset() {
        return offset;
    }

    /**
     * Gets the speed of the projectile.
     *
     * @return the speed of the projectile.  ANYTHING UNDER 40 MIGHT BE TOO FAST TO SEE ON SCREEN
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Gets the id of the projectile.
     *
     * @return the id of the projectile.
     */
    public int getProjectileId() {
        return projectileId;
    }

    /**
     * Gets the starting height of the projectile.
     *
     * @return the starting height of the projectile.
     */
    public int getStartHeight() {
        return startHeight;
    }

    /**
     * Gets the ending height of the projectile.
     *
     * @return the ending height of the projectile
     */
    public int getEndHeight() {
        return endHeight;
    }

    /**
     * Gets the lock on value of the projectile.
     *
     * @return the lock on value of the projectile.
     */
    public int getLockon() {
        return lockon;
    }

    /**
     * Gets the delay of the projectile.
     *
     * @return the delay of the projectile.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Gets the curve angle of the projectile.
     *
     * @return the curve angle of the projectile.
     */
    public int getAngle() {
        return angle;
    }

    public int getSlope() {
        return slope;
    }

    public int getRadius() {
        return radius;
    }

    public int getDuration(int distance) {
        if (distance > 0) {
            return this.delay + (distance * this.stepMultiplier);
        }
        return 0;
    }

    public int getHitDelay(int distance) {
        return (int) Math.floor((getDuration(distance) / 30D));
    }

    public static final float TICK = 600F;

    public static final float CLIENT_CYCLE = 20F;

    public static final float CYCLES_PER_TICK = TICK / CLIENT_CYCLE;

    /**
     * @return GAME TICKS UNTIL PROJECTILE REACHES END TILE
     */
    public int getTime(final Tile from, final Tile to) {
        float duration = getProjectileDuration(from, to) / CYCLES_PER_TICK;
        if (duration - (int) duration > 0.5F) {
            duration++;
        }
        return Math.max(0, (int) duration - 1);
    }

    public int getProjectileDuration(final Tile from, final Tile to) {
        val flightDuration = Math.max(Math.abs(from.getX() - to.getX()), Math.abs(from.getY() - to.getY()));
        return delay + speed + flightDuration;
    }

    public Tile getTarget() {
        return target;
    }

    public int getCreatorSize() {
        return creatorSize;
    }

    public int getProjectileID() {
        return projectileId;
    }

    public int getStartDistanceOffset() {
        return startDistanceOffset;
    }

    @Override
    public String toString() {
        return "Projectile{" +
            "start=" + start +
            ", offset=" + offset +
            ", speed=" + speed +
            ", projectileId=" + projectileId +
            ", startHeight=" + startHeight +
            ", endHeight=" + endHeight +
            ", lockon=" + lockon +
            ", delay=" + delay +
            ", angle=" + angle +
            ", slope=" + slope +
            ", radius=" + radius +
            '}';
    }

    /**
     * constructor for runite formatted projectiles, aim to match them 1:1 with existing fields
     *
     * @param gfxId
     * @param startHeight
     * @param endHeight
     * @param delay
     * @param speed
     * @param durationIncrement
     * @param curve
     * @param idk
     */
    public Projectile(int gfxId, int startHeight, int endHeight, int delay, int speed, int durationIncrement, int curve, int idk) {
        this.projectileId = gfxId;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.delay = delay;
        this.angle = 0;
        this.speed = speed;
        this.creatorSize = curve;
        startDistanceOffset = 64;
        this.slope = curve;
        this.lockon = 0;
        this.radius = 0;
        this.stepMultiplier = 0;
        this.offset = null; // set in send()
    }

    /**
     * @return GAME TICKS UNTIL PROJECTILE REACHES END TILE
     */
    public int send(Entity mob, Tile pos) {
        return send(mob, pos.getX(), pos.getY());
    }

    /**
     * @return GAME TICKS UNTIL PROJECTILE REACHES END TILE
     */
    public int send(Entity mob, int targetX, int targetY) {
        Projectile projectile = new Projectile(
            mob.getCentrePosition(),
            new Tile(targetX, targetY, mob.getZ()),
            this.lockon,
            this.projectileId, this.speed, this.delay, this.startHeight, this.endHeight,
            this.slope, this.creatorSize, this.startDistanceOffset, this.stepMultiplier);
        return mob.executeProjectile(projectile);
    }

    public int send(Entity mob, Entity target) {
        Projectile projectile = new Projectile(
            mob.getCentrePosition(),
            target.getCentrePosition(),
            (target.isPlayer() ? -target.getIndex() - 1 : target.getIndex() + 1),
            this.projectileId, this.speed, this.delay, this.startHeight, this.endHeight,
            this.slope, this.creatorSize, this.startDistanceOffset, this.stepMultiplier);
        return mob.executeProjectile(projectile);
    }

    public int send(Tile src, Tile dest) {
        return send(src.getX(), src.getY(), dest.getX(), dest.getY(), src.getZ());
    }

    public int send(int startX, int startY, int destX, int destY, int z) {
        Projectile projectile = new Projectile(new Tile(startX, startY, z),
            new Tile(destX, destY, z),
            this.lockon, this.projectileId, this.speed, this.delay, this.startHeight, this.endHeight,
            this.slope, this.creatorSize, this.startDistanceOffset, this.stepMultiplier);
        projectile.sendProjectile();
        return projectile.getTime(projectile.getStart(), projectile.getEnd());
    }

    public Projectile sendMagicProjectile(Entity source, Entity victim, int projectileId) {
        var tileDist = source.tile().distance(victim.tile());
        int duration = (51 + -5 + (10 * tileDist));
        return new Projectile(source, victim, projectileId, 51, duration, 43, 31, 0, creatorSize, 10);
    }
}
