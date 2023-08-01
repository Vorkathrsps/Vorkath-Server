package com.cryptic.model.entity.combat.method;

import com.cryptic.model.entity.Entity;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

public class BuildHandler {

    private final int delayAttack;
    private final Entity entity;
    private final Random random;
    private final Build build;

    public BuildHandler(Entity entity, Build build, int delayAttack) {
        Objects.requireNonNull(entity, "entity cannot be null");

        this.entity = entity;
        this.delayAttack = delayAttack;
        this.build = build;
        random = new Random();
    }

    private Consumer<Entity> selectRandomAttackConsumer(List<Consumer<Entity>> attackConsumers) {
        int index = random.nextInt(attackConsumers.size());
        return attackConsumers.get(index);
    }

}
