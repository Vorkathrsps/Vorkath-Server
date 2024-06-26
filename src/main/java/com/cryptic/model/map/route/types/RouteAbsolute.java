package com.cryptic.model.map.route.types;

import com.cryptic.model.map.route.ClipUtils;
import com.cryptic.model.map.route.RouteType;

/** Used for player movement. (Basic walking) */
public class RouteAbsolute extends RouteType {

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
        this.xLength = 1;
        this.yLength = 1;
    }

    @Override
    public boolean method4274(int size, int x, int y, ClipUtils clipUtils) {
        return x == this.x && y == this.y;
    }
}
