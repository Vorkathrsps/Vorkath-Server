package com.aelous.model.entity;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;

/**
 * The enumerated type whose elements represent the different types of
 * node implementations.
 *
 * @author lare96 <http://github.com/lare96>
 */
public enum NodeType {

    /**
     * The element used to represent the {@link Player} implementation.
     */
    PLAYER,

    /**
     * The element used to represent the {@link com.aelous.model.entity.npc.NPC} implementation.
     */
    NPC
}
