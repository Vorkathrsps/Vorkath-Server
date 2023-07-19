package com.aelous.model.content.raids.theatre.boss.sotetseg.handler;

import com.aelous.model.entity.masks.Direction;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateTileHash {
   /* public static List<Tile> generateMazePath() {
        final List<Tile> pathOffsets = new ArrayList<>();

        // the minimum and maximum amount of tiles to go in one direction before being able to potentially change into another direction
        final int minimumStreak = 2;
        final int maximumStreak = 6;

        //start at a random location on the x-axis
        Tile last = Tile.create(Utils.random(1, WIDTH - 1), 0);
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
                final List<Direction> possibleDirections = Arrays.stream(Direction.LOGICAL)
                    .filter(dir -> dir != Direction.SOUTH && dir != lastDir && dir != lastDir.getOpposite())
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
    }*/
}
