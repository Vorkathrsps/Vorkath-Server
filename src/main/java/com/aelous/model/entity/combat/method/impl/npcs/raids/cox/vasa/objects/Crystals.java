package com.aelous.model.entity.combat.method.impl.npcs.raids.cox.vasa.objects;

import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;

public enum Crystals {

    ACTIVE1(new GameObject(29774, new Tile(3270, 5303, 0))),
    ACTIVE2(new GameObject(29774, new Tile(3270, 5285, 0))),
    ACTIVE3(new GameObject(29774, new Tile(3287, 5285, 0))),
    ACTIVE4(new GameObject(29774, new Tile(3287, 5303, 0))),
    INACTIVE1(new GameObject(29774, new Tile(3270, 5303, 0))),
    INACTIVE2(new GameObject(29774, new Tile(3270, 5285, 0))),
    INACTIVE3(new GameObject(29774, new Tile(3287, 5285, 0))),
    INACTIVE4(new GameObject(29774, new Tile(3287, 5303, 0)));

    public final GameObject object;

    Crystals(GameObject object) {
        this.object = object;
    }

    public static boolean getActiveCrystals(GameObject objectId) {
        return objectId.getCrystalObjects().ordinal() <= ACTIVE4.ordinal();
    }

    public static boolean getInactiveCrystals(GameObject objectId) {
        return objectId.getCrystalObjects().ordinal() >= INACTIVE1.ordinal();
    }

    public static Crystals find(GameObject objectId) {
        for (Crystals crystal : Crystals.values()) {
            if (crystal.object == objectId) {
                return crystal;
            }
        }
        return null;
    }
}
