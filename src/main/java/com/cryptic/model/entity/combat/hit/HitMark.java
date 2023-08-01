package com.cryptic.model.entity.combat.hit;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
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
    MAX_HIT(18, 11),
    YELLOW_TINTED_UP(12, -1),
    PURPLE_DOWN(16, 17);

    @Getter
    private final int primary_mark;
    @Getter
    private final int secondary_mark;

    HitMark(int primary_mark, int secondary_mark) {
        this.primary_mark = primary_mark;
        this.secondary_mark = secondary_mark;
    }

    public int getObservedType(Hit hit, Entity source, Entity target, Player observer, boolean isMaxHit) {
        if (isMaxHit && source == observer) {
            return MAX_HIT.primary_mark;
        }
        if (source == observer || target == observer) {
            if (hit.getDamage() == 0) {
                return MISSED.primary_mark;
            }
            return primary_mark;
        }
        return hit.getDamage() == 0 ? MISSED.secondary_mark : secondary_mark;
    }
}
