package com.aelous.model.entity.combat.hit;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import lombok.Getter;

public enum HitMark {
    MISSED(0, 10),
    REGULAR(1, 11),
    DEFAULT(1, 11),
    MELEE(1, 11),
    MAGIC(1, 11),
    RANGED(1, 11),
    POISON(2, 2),
    DISEASED(4, 4),
    VENOM(3, 3),
    HEALED(5, 5),
    VERZIK_SHIELD_HITSPLAT(6, 6),
    MAX_HIT(18, 1),
    YELLOW_TINTED_UP(12, -1),
    PURPLE_DOWN(16, 17);

    @Getter
    private final int primary_mark;
    @Getter
    private final int secondary_mark;
    public static final HitMark[] values = values();

    HitMark(int primary_mark, int secondary_mark) {
        this.primary_mark = primary_mark;
        this.secondary_mark = secondary_mark;
    }

    public int getObservedType(boolean maxHit, Entity source, Entity target, Player observer) {
        if (maxHit && source == observer) {
            return MAX_HIT.primary_mark;
        } else if (source == observer || target == observer) {
            return primary_mark;
        }
        return secondary_mark;
    }
}
