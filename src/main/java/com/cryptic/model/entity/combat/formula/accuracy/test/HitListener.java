package com.cryptic.model.entity.combat.formula.accuracy.test;

import com.cryptic.model.entity.combat.hit.Hit;

import java.util.function.Consumer;

public interface HitListener {
    HitListener preDefend(Consumer<Hit> preDefend);
    HitListener postDefend(Consumer<Hit> postDefend);

    HitListener preDamage(Consumer<Hit> preDamage);
    HitListener postDamage(Consumer<Hit> postDamage);

    public void onHit(Hit hit);
}
