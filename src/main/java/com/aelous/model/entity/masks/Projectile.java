package com.aelous.model.entity.masks;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
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
        int offX = (start.getY() - end.getY()) * -1; // yes inverted
        int offY = (start.getX() - end.getX()) * -1;
        Tile offset = new Tile(offX, offY, start.getZ());
        this.offset = offset;
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
        this(start, end, lockon, projectileId, speed, delay, startHeight, endHeight, curve, 1, 64, 0);
    }

    public Projectile(Entity source, Entity victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source.getCentrePosition(),
            victim.getCentrePosition(),
            (victim.isPlayer() ? -victim.getIndex() - 1 : victim.getIndex() + 1),
            projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    public Projectile(Tile source, Tile victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source, victim,
            0, projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    public Projectile(Entity source, Tile victim, int projectileId,
                      int delay, int speed, int startHeight, int endHeight, int curve, int creatorSize, int stepMultiplier) {
        this(source.getCentrePosition(), victim,
            0, projectileId, speed, delay,
            startHeight, endHeight, curve, creatorSize, 64, stepMultiplier);
    }

    /**
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
            (victim.isPlayer() ? -victim.getIndex() - 1
                : victim.getIndex() + 1), projectileId, speed, delay,
            startHeight, endHeight, curve, source.getSize(), 0, 0);
    }

    public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int angle, boolean forNpc) {
        this(source.getCentrePosition(), victim.getCentrePosition(), victim.getProjectileLockonIndex(), projectileId, speed, delay, startHeight, endHeight, angle, 16, 64, 0);
    }

    public Projectile(Tile npc, Tile transform, int lockon, int projectileId, int speed, int delay, int duration, int startHeight, int endHeight, int curve, int stepMultiplier) {
        this(npc, transform, lockon, projectileId, speed, delay, startHeight, endHeight, curve, 1, 0, stepMultiplier);
    }


    public void sendProjectile() {
        for (Player player : World.getWorld().getPlayers()) {

            if (player == null || !start.isViewableFrom(player.tile())) {
                continue;
            }

            player.getPacketSender().sendProjectile(start, offset, 0,
                speed, projectileId, startHeight, endHeight, lockon, delay,
                creatorSize, startDistanceOffset);
        }
    }

    public void sendProjectile(Tile x, Tile y, Tile x2, Tile y2) {
        for (Player player : World.getWorld().getPlayers()) {

            if (player == null || !start.isViewableFrom(player.tile())) {
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
        return (int) Math.floor((getDuration(distance) / 30D) + 1);
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
     * @param durationStart
     * @param durationIncrement
     * @param curve
     * @param idk
     */
    public Projectile(int gfxId, int startHeight, int endHeight, int delay, int durationStart, int durationIncrement, int curve, int idk) {
        this.projectileId = gfxId;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.delay = delay;
        this.angle = 0;
        this.speed = durationStart;
        this.creatorSize = curve;
        startDistanceOffset = 64;
        this.slope = curve;
        this.lockon = 0;
        this.radius = 0;
        this.stepMultiplier = 0;
        this.offset = null; // set in send()
    }

    public int send(Entity mob, Tile pos) {
        return send(mob, pos.getX(), pos.getY());
    }

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
        return projectile.getHitDelay(projectile.start.getChevDistance(projectile.getEnd()));
    }

    public Projectile sendMagicProjectile(Entity source, Entity victim, int projectileId) {
        var tileDist = source.tile().distance(victim.tile());
        int duration = (51 + -5 + (10 * tileDist));
        return new Projectile(source, victim, projectileId, 51, duration, 43, 31, 0, creatorSize, 10);
    }
}
