package com.cryptic.model.entity.combat.method;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;

/**
 * Represents a combat method.
 */
public interface CombatMethod {
    boolean prepareAttack(Entity entity, Entity target);
    int getAttackSpeed(Entity entity);
    int moveCloseToTargetTileRange(Entity entity);
    default boolean customOnDeath(Hit hit) {
        return false;
    }
    default boolean canMultiAttackInSingleZones() { return false; }
    default boolean ignoreEntityInteraction() { return false; }
}
