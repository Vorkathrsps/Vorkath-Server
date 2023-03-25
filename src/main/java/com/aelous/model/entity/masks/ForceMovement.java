package com.aelous.model.entity.masks;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import com.aelous.utility.chainedwork.Chain;
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

    /**
     *
     * @param start the starting position
     * @param end the ending position
     * @param cycleDelay the task cycle delay
     * @param cycleEnd the task cycle end delay
     * @param animation the animation we're updating
     * @param direction the directions that we are force moving the player too! NOT FACING.
     */
    public ForceMovement(Tile start, Tile end, int cycleDelay, int cycleEnd, int animation, int direction) {
        this.start = start;
        this.end = end;
        this.speed = cycleDelay;
        this.reverseSpeed = cycleEnd;
        this.animation = animation;
        this.direction = direction;
    }

    public ForceMovement(Tile start, @Nullable Tile end, int speed, int reverseSpeed, int direction) {
        this.setStart(start);
        this.setEnd(end);
        this.setSpeed((short)speed);
        this.setReverseSpeed((short)reverseSpeed);
        this.setDirection((byte)direction);
    }

    public ForceMovement(Tile start, @Nullable Tile end, int speed, int reverseSpeed, int animation, FaceDirection direction) {
        this.setStart(start);
        this.setEnd(end);
        this.setSpeed((short)speed);
        this.setReverseSpeed((short)reverseSpeed);
        this.setAnimation((short)animation);
        this.setDirection((byte)direction.direction);
    }


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
