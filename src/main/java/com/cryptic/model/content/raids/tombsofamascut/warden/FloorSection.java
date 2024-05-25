package com.cryptic.model.content.raids.tombsofamascut.warden;

import com.cryptic.model.content.raids.tombsofamascut.warden.combat.RaisedFloor;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.map.position.Tile;

import java.util.HashSet;

public enum FloorSection {
    RIGHT(new Animation(9674), new Animation(9675)),
    LEFT(new Animation(9676), new Animation(9677)),
    CENTER(new Animation(9678), new Animation(9679));

    final Animation animation_one, animation_two;

    FloorSection(Animation one, Animation two) {
        this.animation_one = one;
        this.animation_two = two;
    }

    public HashSet<RaisedFloor> buildCenter(Entity entity, Tile start, boolean fast) {
        var locations = new HashSet<RaisedFloor>();
        for (int y = 0; y < 9; y++) {
            var delay = fast ? 60 : 90 + (y * 6);
            for (int x = 1; x < y + 6; x++) {
                Tile westTransform = start.transform(-x, y).transform(0, Direction.NORTH.y);
                Tile west = new Tile(westTransform.getX(), westTransform.getY(), entity.getInstancedArea().getzLevel() + 1);
                Tile eastTransform = start.transform(x, y).transform(0, Direction.NORTH.y);
                Tile east = new Tile(eastTransform.getX(), eastTransform.getY(), entity.getInstancedArea().getzLevel() + 1);
                locations.add(new RaisedFloor(west.x, west.y, west.level, delay));
                locations.add(new RaisedFloor(east.x, east.y, east.level, delay));
                if (x >= y) {
                    delay += 6;
                }
            }
        }
        return locations;
    }

    public HashSet<RaisedFloor> buildWest(Entity entity,Tile start, boolean fast) {
        var locations = new HashSet<RaisedFloor>();
        for (int y = 0; y < 9; y++) {
            var delay = fast ? 60 : 90 + (y * 6);
            for (int x = 0; x < y + 6; x++) {
                Tile transform = start.transform(-x, y).transform(0, Direction.NORTH.y);
                Tile tile = new Tile(transform.getX(), transform.getY(), entity.getInstancedArea().getzLevel() + 1);
                locations.add(new RaisedFloor(tile.x, tile.y, tile.level, delay));
                if (x >= y) {
                    delay += 6;
                }
            }
        }
        return locations;
    }


    public HashSet<RaisedFloor> buildEast(Entity entity, Tile start, boolean fast) {
        var locations = new HashSet<RaisedFloor>();
        for (int y = 0; y < 9; y++) {
            var delay = fast ? 60 : 90 + (y * 6);
            for (int x = 0; x < y + 6; x++) {
                Tile transform = start.transform(x, y).transform(0, Direction.NORTH.y);
                Tile tile = new Tile(transform.getX(), transform.getY(), entity.getInstancedArea().getzLevel() + 1);
                locations.add(new RaisedFloor(tile.x, tile.y, tile.level, delay));
                if (x >= y) {
                    delay += 6;
                }
            }
        }
        return locations;
    }
}
