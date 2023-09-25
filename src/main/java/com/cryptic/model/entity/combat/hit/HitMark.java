package com.cryptic.model.entity.combat.hit;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
import lombok.Getter;

public enum HitMark {
    MISSED(0, 10, 0),
    REGULAR(1, 11, 18),
    DEFAULT(1, 11, 18),
    POISON(2, 2, 34),
    DISEASED(4, 4, 4),
    VENOM(3, 3, 3),
    HEALED(5, 5, 5),
    SHIELD_HITSPLAT(6, 25, 21),
    YELLOW_ARROW_UP(26, 12, 19),
    PURPLE_DOWN(16, 17, 24),
    CORRUPTION(31, 31, 31),
    CORRUPTION_TWO(33, 32, 33),
    INSANE(35, 36, 35);

    @Getter private final int non_tinted;
    @Getter private final int tinted;
    @Getter private final int max_hit;

    HitMark(int non_tinted, int tinted, int max_hit) {
        this.non_tinted = non_tinted;
        this.tinted = tinted;
        this.max_hit = max_hit;
    }

    public int getObservedType(Hit hit, Entity source, Entity target, Player observer, boolean isMaxHit) {
        if (isMaxHit && source == observer) {
            if (this.equals(YELLOW_ARROW_UP)) {
                return YELLOW_ARROW_UP.max_hit;
            } else if (this.equals(SHIELD_HITSPLAT)) {
                return SHIELD_HITSPLAT.max_hit;
            } else if (this.equals(POISON)) {
                return POISON.max_hit;
            }
            return DEFAULT.max_hit;
        }
        if (source == observer || target == observer) {
            if (hit.getDamage() == 0) {
                if (this.equals(YELLOW_ARROW_UP)) {
                    return YELLOW_ARROW_UP.non_tinted;
                } else if (this.equals(SHIELD_HITSPLAT)) {
                    return SHIELD_HITSPLAT.non_tinted;
                } else if (this.equals(POISON)) {
                    return POISON.non_tinted;
                }
                return MISSED.non_tinted;
            }
            return non_tinted;
        }
        return hit.getDamage() == 0 ? MISSED.tinted : tinted;
    }
}
