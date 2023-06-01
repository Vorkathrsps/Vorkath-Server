package com.aelous.model.entity.combat.method;

import com.aelous.model.entity.Entity;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityCombatBuilder {

    private final Entity entity;
    private final Entity target;
    private final Map<CombatPhase, List<Consumer<Entity>>> attackConsumers;
    private int delayAttack;

    public EntityCombatBuilder(Entity entity, Entity target) {
        Objects.requireNonNull(entity, "entity cannot be null");
        Objects.requireNonNull(target, "target cannot be null");
        this.entity = entity;
        this.target = target;
        attackConsumers = new EnumMap<>(CombatPhase.class);
    }

    public EntityCombatBuilder addAttackConsumer(CombatPhase phase, Consumer<Entity> attackConsumer) {
        Objects.requireNonNull(phase, "phase cannot be null");
        Objects.requireNonNull(attackConsumer, "attackConsumer cannot be null");
        attackConsumers.computeIfAbsent(phase, k -> new ArrayList<>()).add(attackConsumer);
        return this;
    }

    public EntityCombatBuilder setDelayAttack(int delayAttack) {
        this.delayAttack = delayAttack;
        return this;
    }

    public EntityCombatHandler build() {
        return new EntityCombatHandler(entity, attackConsumers, delayAttack);
    }

    public enum CombatPhase {
        PHASE_1,
        PHASE_2,
        PHASE_3,
        PHASE_4,
        ALL
    }
}
