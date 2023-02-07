package com.aelous.model.map.object.dwarf_cannon;

import com.aelous.model.map.position.Tile;

/**
 * Rotations for cannons.
 *
 * @author Patrick van Elderen | April, 16, 2021, 13:37
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public enum CannonDirection {
    NORTH(0, 515),
    NORTH_EAST(1, 516),
    EAST(2, 517),
    SOUTH_EAST(3, 518),
    SOUTH(4, 519),
    SOUTH_WEST(5, 520),
    WEST(6, 521),
    NORTH_WEST(7, 514);

    CannonDirection(int direction, int animationId) {
        this.direction = direction;
        this.animationId = animationId;
    }

    public static CannonDirection forId(int direction) {
        for (CannonDirection facingState : CannonDirection.values()) {
            if (facingState.getDirection() == direction) {
                return facingState;
            }
        }
        return CannonDirection.NORTH;
    }

    public CannonDirection next() {
        return forId(direction + 1);
    }

    public boolean validArea(Tile center, Tile location) {
        switch (this) {
            case NORTH:
                return (location.getY() > center.getY()
                        && location.getX() >= center.getX() - 1
                        && location.getX() <= center.getX() + 1);
            case NORTH_EAST:
                return (location.getX() >= center.getX() + 1
                        && location.getY() >= center.getY() + 1);
            case EAST:
                return (location.getX() > center.getX()
                        && location.getY() >= center.getY() - 1
                        && location.getY() <= center.getY() + 1);
            case SOUTH_EAST:
                return (location.getY() <= center.getY() - 1
                        && location.getX() >= center.getX() + 1);
            case SOUTH:
                return (location.getY() < center.getY()
                        && location.getX() >= center.getX() - 1
                        && location.getX() <= center.getX() + 1);
            case SOUTH_WEST:
                return (location.getX() <= center.getX() - 1
                        && location.getY() <= center.getY() - 1);
            case WEST:
                return (location.getX() < center.getX()
                        && location.getY() >= center.getY() - 1
                        && location.getY() <= center.getY() + 1);
            case NORTH_WEST:
                return (location.getX() <= center.getX() - 1
                        && location.getY() >= center.getY() + 1);
            default:
                return false;
        }
    }

    private final int direction;
    private final int animationId;

    public int getDirection() {
        return direction;
    }

    public int getAnimationId() {
        return animationId;
    }
}
