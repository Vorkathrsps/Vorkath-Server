package com.cryptic.model.entity.combat.method;

import com.cryptic.model.entity.Entity;

import java.util.function.Consumer;

public abstract class CombatAttackListener {
    public abstract Consumer<Entity> performAttack(Entity entity, Entity target);

}
