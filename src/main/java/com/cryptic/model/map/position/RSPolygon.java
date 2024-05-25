package com.cryptic.model.map.position;

import com.cryptic.utility.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.awt.*;
import java.util.BitSet;

public final class RSPolygon {

    public RSPolygon(final int regionID) {
        this(calculateSWCornerX(regionID), calculateSWCornerY(regionID),
            calculateSWCornerX(regionID) + 64, calculateSWCornerY(regionID) + 64);
    }

    public RSPolygon(int swX, int swY, int neX, int neY) {
        this(new int[][]{
            {swX, swY},
            {neX, swY},
            {neX, neY},
            {swX, neY}
        });
    }

    public RSPolygon(Tile sw, Tile ne) {
        this(sw.getX(), sw.getY(), ne.getX(), ne.getY());
    }

    public RSPolygon(final int[][] points) {
        this(points, 0, 1, 2, 3);
    }

    public RSPolygon(final int[][] points, final int... planes) {
        final int[] xPoints = new int[points.length];
        final int[] yPoints = new int[points.length];
        for (int index = 0; index < points.length; index++) {
            final int[] area = points[index];
            xPoints[index] = area[0];
            yPoints[index] = area[1];
        }
        this.points = points;

        polygon = new EfficientPolygon(xPoints, yPoints, points.length);

        this.planes = planes;
        for (int plane : planes) {
            planeSet.set(plane);
        }
    }

    private final Polygon polygon;
    private final int[] planes;
    private final BitSet planeSet = new BitSet(4);
    private final int[][] points;

    public boolean contains(final int x, final int y) {
        return polygon.contains(x, y);
    }

    public boolean contains(final int x, final int y, final int plane) {
        return containsPlane(plane) && contains(x, y);
    }

    public boolean contains(final Tile location) {
        return contains(location.getX(), location.getY(), location.getZ());
    }

    public void addPoint(final int x, final int y) {
        polygon.addPoint(x, y);
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public boolean containsPlane(int plane) {
        return planeSet.get(plane);
    }

    public int[] getPlanes() {
        return planes;
    }

    public int[][] getPoints() {
        return points;
    }

    private static int calculateSWCornerX(final int regionId) {
        return (regionId >> 8) << 6;
    }

    private static int calculateSWCornerY(final int regionId) {
        return (regionId & 0xFF) << 6;
    }
    public static RSPolygon growRelative(Tile position, int grow) {
        int north = position.getY() + grow;
        int east = position.getX() + grow;
        int west = position.getX() - grow;
        int south = position.getY() - grow;
        return new RSPolygon(west, south, east, north);
    }
    public static RSPolygon growAbsolute(Tile position, int grow) {
        int north = position.getY() + grow;
        int east = position.getX() + grow;
        int west = position.getX() - grow;
        int south = position.getY() - grow;
        return new RSPolygon(west, south, east, north);
    }

    public ObjectArrayList<Tile> getBorderPositions(int z) {
        ObjectArrayList<Tile> positions = new ObjectArrayList<>();
        int west = (int) polygon.getBounds2D().getMinX();
        int east = (int) polygon.getBounds2D().getMaxX();
        int south = (int) polygon.getBounds2D().getMinY();
        int north = (int) polygon.getBounds2D().getMaxY();
        for(int x = west; x <= east; x++) {
            for(int y = south; y <= north; y++) {
                if(x == west || x == east || y == south || y == north)
                    positions.add(new Tile(x, y, z));
            }
        }
        return positions;
    }

    public ObjectArrayList<Tile> getAllpositions(int z) {
        ObjectArrayList<Tile> positions = new ObjectArrayList<>();
        int west = (int) polygon.getBounds2D().getMinX();
        int east = (int) polygon.getBounds2D().getMaxX();
        int south = (int) polygon.getBounds2D().getMinY();
        int north = (int) polygon.getBounds2D().getMaxY();
        for(int x = west; x <= east; x++) {
            for(int y = south; y <= north; y++) {
                positions.add(new Tile(x, y, z));
            }
        }
        return positions;
    }


}
