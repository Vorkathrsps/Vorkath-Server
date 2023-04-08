package com.aelous.model.entity.combat.method;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.Hit;

/**
 * Represents a combat method.
 */
public interface CombatMethod {

    boolean prepareAttack(Entity entity, Entity target);
    int getAttackSpeed(Entity entity);
    int getAttackDistance(Entity entity);
    default boolean customOnDeath(Hit hit) {
        return false;
    }
    default boolean canMultiAttackInSingleZones() { return false; }
}
