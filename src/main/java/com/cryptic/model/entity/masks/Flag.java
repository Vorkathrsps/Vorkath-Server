package com.cryptic.model.entity.masks;

/**
 * Represents a Flag that a mob entity can update.
 * 
 * @author relex lawl
 */
public enum Flag {

    APPEARANCE,
    CHAT,
    FORCED_CHAT,
    FORCED_MOVEMENT,
    ENTITY_INTERACTION,
    FACE_TILE,
    ANIMATION,
    GRAPHIC,
    FIRST_SPLAT,
    LUMINANCE,

    /**
     * Update flag used to transform npc to another.
     */
    TRANSFORM, VISIBLE_MENU_OPTIONS;
}
