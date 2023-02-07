package com.aelous.utility;

import com.aelous.model.map.position.Tile;

public class FillTiles {
    //This class can be used as a utility type class for drop parties and many other cool things.
    public static void main(String[] args) {

        int startX = 2333;
        int startY = 3639;
        int offset = 3;

        Tile[] tiles = new Tile[(int) Math.pow((2 * offset + 1), 2) - 1];
        System.out.println("tiles: "+tiles.length);

        int index = 0;

        for (int x = startX - offset; x <= startX + offset; x++) {

            for (int y = startY - offset; y <= startY + offset; y++) {

                if (x == startX && y == startY) {
                    continue;
                }
                tiles[index] = new Tile(x, y);

                index++;
            }
        }

        for (Tile pos : tiles) {
            System.out.println("X: " + pos.getX() + " | Y: " + pos.getY());
        }

        System.out.println("Index: " + index);

    }
}
