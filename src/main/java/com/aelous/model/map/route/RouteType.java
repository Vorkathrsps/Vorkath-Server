package com.aelous.model.map.route;

import com.aelous.model.map.position.Tile;

public abstract class RouteType {

    public int x, y;
    public int absX, absY;
    public int finishX, finishY;
    public int xLength, yLength;

    public boolean reachable;

    public abstract boolean method4274(int size, int x, int y, ClipUtils clipUtils);

    public void localize(ClipUtils clipUtils) {
        absX = x;
        absY = y;
        x -= clipUtils.baseX;
        y -= clipUtils.baseY;
    }

    public boolean finished(Tile src) {
        return src.getX() == finishX && src.getY() == finishY;
    }
}
