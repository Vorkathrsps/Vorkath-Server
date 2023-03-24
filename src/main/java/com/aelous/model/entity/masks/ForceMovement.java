package com.aelous.model.entity.masks;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class ForceMovement {

    private Tile start;
    private Tile end;
    private int speed;
    private int reverseSpeed;
    private int direction;

    public Tile finish;

    private int animation;

    public ForceMovement(Player player, Tile start, Tile end, int cycleDelay, int cycleEnd, int animation, int direction) {
        this.start = start;
        this.end = end;
        this.speed = cycleDelay;
        this.reverseSpeed = cycleEnd;
        this.animation = animation;
        this.direction = direction;
        int x = start.getX() + end.getX();
        int y = start.getY() + end.getY();
        player.setTile(new Tile(x, y));
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public ForceMovement(Tile start, @Nullable Tile end, int speed, int reverseSpeed, int direction) {
        this.setStart(start);
        this.setEnd(end);
        this.setSpeed((short)speed);
        this.setReverseSpeed((short)reverseSpeed);
        this.setDirection((byte)direction);
    }

    public ForceMovement(Tile start, @Nullable Tile end, int speed, int reverseSpeed, int direction, int animation) {
        this.setStart(start);
        this.setEnd(end);
        this.setSpeed((short)speed);
        this.setReverseSpeed((short)reverseSpeed);
        this.setDirection((byte)direction);
        this.setAnimation((short)animation);
    }

    public ForceMovement(Tile start, @Nullable Tile end, int speed, int reverseSpeed, int animation, FaceDirection direction) {
        this.setStart(start);
        this.setEnd(end);
        this.setSpeed((short)speed);
        this.setReverseSpeed((short)reverseSpeed);
        this.setAnimation((short)animation);
        this.setDirection((byte)direction.direction);
    }

   /* public ForceMovement(Player player, Tile start, @Nullable Tile end, int cycleStart, int reverseSpeed, int animation, int movementDirection) {
        if (player == null || start == null) {
            throw new IllegalArgumentException("player and start cannot be null");
        }

        this.setStart(new Tile(start.getX(), start.getY()));
        if (end != null) {
            this.setEnd(new Tile(end.getX(), end.getY()));
        }
        this.setSpeed((short)cycleStart);
        this.setReverseSpeed((short)reverseSpeed);
        this.setDirection((byte)movementDirection);
        this.setAnimation((short)animation);

        int x = start.getX() + (end != null ? end.getX() : 0);
        int y = start.getY() + (end != null ? end.getY() : 0);
        player.setTile(new Tile(x, y));
        player.getUpdateFlag().flag(Flag.APPEARANCE);
        player.setNeedsPlacement(true);
        player.setResetMovementQueue(true);
        player.getMovementQueue().clear();
    }*/


    public ForceMovement(int dx, int dy, int dx2, int dy2, int speed1, int speed2, FaceDirection direction, int animation) {
        this.setStart(new Tile(dx, dy));
        this.setEnd(new Tile(dx2, dy2));
        this.setSpeed((short)speed1);
        this.setReverseSpeed((short)speed2);
        this.setDirection((byte)direction.direction);
        this.setAnimation(animation);
    }

    public Tile getStart() {
        return start;
    }

    public void setStart(Tile start) {
        this.start = start;
    }

    public @Nullable Tile getEnd() {
        return end;
    }

    public Tile getStart(Player player) {
        int dx = player.tile().getLocalX();
        int dy = player.tile().getLocalY();
        return new Tile(dx, dy, start.getLevel());
    }

    public Tile getEnd(Player player) {
        int dx = player.tile().getLocalX();
        int dy = player.tile().getLocalY();
        return new Tile(dx, dy, end.getLevel());
    }

    public void setEnd(Tile end) {
        this.end = end;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getReverseSpeed() {
        return reverseSpeed;
    }

    public void setReverseSpeed(int reverseSpeed) {
        this.reverseSpeed = reverseSpeed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

}
