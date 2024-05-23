package com.cryptic.model.content.events;

import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.timers.TimerKey;

public enum Events {

    NONE(null, null, null),
    DOUBLE_DROPS(TimerKey.DOUBLE_DROPS, AttributeKey.DOUBLE_DROPS, EffectTimer.DROP_LAMP),
    DOUBLE_XP(TimerKey.DOUBLE_EXPERIENCE, AttributeKey.DOUBLE_XP, EffectTimer.DOUBLE_EXP),
    REVENANT_DROP_BOOST(TimerKey.REVENANT_DROPS_BOOST, AttributeKey.REVENANT_DROP_BOOST, EffectTimer.REVENANT_DROP_RATE);

    final TimerKey key;
    final AttributeKey attribute;
    final EffectTimer timer;

    Events(TimerKey key, AttributeKey attribute, EffectTimer timer) {
        this.key = key;
        this.attribute = attribute;
        this.timer = timer;
    }
}
