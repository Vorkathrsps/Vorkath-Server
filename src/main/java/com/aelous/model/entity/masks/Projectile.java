package com.aelous.model.entity.masks;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

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
    private int speed;

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
    private final int angle;

    /**
     * The slope of the projectile.
     */
    private final int slope;

    /**
     * The radius that the projectile is launched from.
     */
    private final int radius;

    private final int stepMultiplier;

    public Projectile(Tile start, Tile end, int lockon,
                      int projectileId, int speed, int delay, int startHeight, int endHeight,
                      int curve, int creatorSize, int startDistanceOffset, int stepMultiplier) {
        this.start = start;
        this.target = end;
        this.offset = new Tile((end.getX() - start.getX()),
            (end.getY() - start.getY()));
        this.creatorSize = creatorSize;
        this.startDistanceOffset = startDistanceOffset;
        this.lockon = lockon;
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.slope = curve;
        this.angle = getAngle();
        this.radius = getRadius();
        this.stepMultiplier = stepMultiplier;
    }
    public Projectile(Tile start, Tile end, int lockon,
                      int projectileId, int speed, int delay, int startHeight, int endHeight,
                      int curve) {
        this(start, end, lockon, projectileId, speed, delay, startHeight, endHeight, curve, 1, 0, 0);
    }

    //public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int angle, int slope, int radius) {

       // this(new Tile(source.getX(), source.getY()), new Tile(victim.getX(), victim.getY()), victim.getProjectileLockonIndex(), projectileId, speed, delay, startHeight, endHeight, angle, slope, radius, 0);
  //  }

    public Projectile(Entity source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source.getCentrePosition(), victim.getCentrePosition(),
            (victim.isPlayer() ? -victim.getIndex() - 1
                : victim.getIndex() + 1), projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    /**
     *
     * @param source
     * @param victim
     * @param projectileId
     * @param delay
     * @param speed  ANYTHING UNDER 40 MIGHT BE TOO FAST TO SEE ON SCREEN
     * @param startHeight
     * @param endHeight
     * @param curve
     */
    public Projectile(Entity source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve) {
        this(source.getCentrePosition(), victim.getCentrePosition(),
            (victim.isPlayer() ? -victim.getIndex() - 1
                : victim.getIndex() + 1), projectileId, speed, delay,
            startHeight, endHeight, curve, source.getSize(), 0, 0);
    }

    public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int angle, boolean forNpc) {
        this(source.getCentrePosition(), victim.getCentrePosition(), victim.getProjectileLockonIndex(), projectileId, speed, delay, startHeight, endHeight, angle, 16, 64, 0);
    }

    /**
     * Sends one projectiles using the values set when the {@link Projectile}
     * was constructed.
     */
    public void sendProjectile() {
        for (Player player : World.getWorld().getPlayers()) {

            if (player == null) {
                continue;
            }

            player.getPacketSender().sendProjectile(start, offset, 0,
                speed, projectileId, startHeight, endHeight, lockon, delay,
                creatorSize, startDistanceOffset);
        }
    }

    public void sendFor(Player player) {
        if (start.isViewableFrom(player.tile())) {
            player.getPacketSender().sendProjectile(start, offset, 0,
                speed, projectileId, startHeight, endHeight, lockon, delay,
                creatorSize, startDistanceOffset);
        }
    }

    public int clientDelay() {
        return delay + speed;
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
        return (int) Math.floor(getDuration(distance) / 30D) + 1;
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
}
