package com.cryptic.model.entity.combat.method;

import com.cryptic.model.entity.Entity;
import com.cryptic.utility.timers.TimerKey;

import java.util.*;
import java.util.function.Consumer;

public class EntityCombatHandler {
    private final Map<EntityCombatBuilder.CombatPhase, List<Consumer<Entity>>> attackConsumers;
    private final int delayAttack;
    private final Entity entity;
    private final Random random;

    public EntityCombatHandler(Entity entity, Map<EntityCombatBuilder.CombatPhase, List<Consumer<Entity>>> attackConsumers, int delayAttack) {
        Objects.requireNonNull(entity, "entity cannot be null");
        Objects.requireNonNull(attackConsumers, "attackConsumers cannot be null");

        this.entity = entity;
        this.attackConsumers = attackConsumers;
        this.delayAttack = delayAttack;
        random = new Random();
    }

    public void distributeAttacks(EntityCombatBuilder.CombatPhase phase) {
        if (attackConsumers.containsKey(phase)) {
            List<Consumer<Entity>> phaseAttackConsumers = attackConsumers.get(phase);
            if (!phaseAttackConsumers.isEmpty()) {
                Consumer<Entity> attackConsumer = selectRandomAttackConsumer(phaseAttackConsumers);
                attackConsumer.accept(entity);
                delayAttack(delayAttack);
            }
        }
    }

    private Consumer<Entity> selectRandomAttackConsumer(List<Consumer<Entity>> attackConsumers) {
        int index = random.nextInt(attackConsumers.size());
        return attackConsumers.get(index);
    }

    private void delayAttack(int delay) {
        entity.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, delay);
    }
}
