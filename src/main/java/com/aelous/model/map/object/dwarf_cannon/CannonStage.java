package com.aelous.model.map.object.dwarf_cannon;

/**
 * The cannon build stages.
 *
 * <p>Gabriel || Wolfsdarker
 */
public enum CannonStage {
    BASE(7, DwarfCannon.BASE),
    STAND(8, DwarfCannon.BASE, DwarfCannon.STAND),
    BARREL(9, DwarfCannon.BASE, DwarfCannon.STAND, DwarfCannon.BARRELS),
    FURNACE(6, DwarfCannon.CANNON_PARTS),
    FIRING(6, DwarfCannon.CANNON_PARTS),
    BROKEN(5, DwarfCannon.CANNON_PARTS);

    private final int objectId;
    private final int[] parts;

    public int getObjectId() {
        return objectId;
    }

    public int[] getParts() {
        return parts;
    }

    CannonStage(int objectId, int... parts) {
        this.objectId = objectId;
        this.parts = parts;
    }

    public static CannonStage forId(int objectId) {
        for (CannonStage stage : values()) {
            if (stage.getObjectId() == objectId) return stage;
        }
        return null;
    }

    public CannonStage next() {
        return values()[forId(objectId).ordinal() + 1];
    }
}
