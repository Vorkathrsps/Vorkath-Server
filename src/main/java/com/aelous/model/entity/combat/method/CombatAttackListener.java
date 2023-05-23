package com.aelous.model.entity.combat.method;

import com.aelous.model.entity.Entity;

import java.util.function.Consumer;

public abstract class CombatAttackListener {
    public abstract Consumer<Entity> performAttack(Entity entity, Entity target);

}
