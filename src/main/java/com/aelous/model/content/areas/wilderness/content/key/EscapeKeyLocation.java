package com.aelous.model.content.areas.wilderness.content.key;

import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import static com.google.common.base.Preconditions.checkNotNull;

public enum EscapeKeyLocation {

    //Multi
    FARWESTCORNER("back of the Escape Zone", new Tile(3277, 4850)),
    LAVA_PITS("back of the Escape Zone", new Tile(3300, 4829)),
    BROKEN_WALLS("back of the Escape Zone", new Tile(3364, 4848)),
    DOWNSTAIRS_BACKBUILDING("back of the Escape Zone", new Tile(3370, 4813)),
    NORTH("back of the Escape Zone", new Tile(3331, 4850)),
    ;

    private static final EscapeKeyLocation[] VALUES = values();

    public static EscapeKeyLocation findRandom() {
        return VALUES[Utils.RANDOM_GEN.get().nextInt(VALUES.length)];
    }

    private final Tile tile;
    private final String description;

    EscapeKeyLocation(String description, Tile tile) {
        this.description = checkNotNull(description, "description");
        this.tile = checkNotNull(tile, "tile");
    }

    public Tile tile() {
        return tile;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

}
