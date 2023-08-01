package com.cryptic.model.entity.combat.method;

import com.cryptic.model.entity.Entity;

import java.util.*;
import java.util.function.Consumer;

public class Build {

    private int delayAttack;
    private final Entity entity;
    private final Entity target;

    public Build(Entity entity, Entity target, int delayAttack) {
        this.entity = entity;
        this.target = target;
        this.delayAttack = delayAttack;
     }

    public Build addMethodListener(Entity entity, Consumer<Entity> addEntityCombatListener) {
        Objects.requireNonNull(addEntityCombatListener);
        addEntityCombatListener.andThen(k -> new ArrayList<>()).accept(entity);
        return this;
    }

    public Build setDelayAttack(int delayAttack) {
        this.delayAttack = delayAttack;
        return this;
    }

    public BuildHandler Build() {
        return new BuildHandler(this.entity, this, this.delayAttack);
    }

}
