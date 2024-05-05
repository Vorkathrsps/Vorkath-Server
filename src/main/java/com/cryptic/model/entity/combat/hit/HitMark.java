package com.cryptic.model.entity.combat.hit;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
import lombok.Getter;

public enum HitMark {
    /**
     * Corruption has a chance to apply on the target of the player who is under the effects of either Lesser Corruption or Greater Corruption during a successful hit.
     * Corruption which drains prayer points over a short period of time.
     */
    MISS(12, 13, -1),
    IRONMAN_BLOCKED(1, -1, -1),
    HIT(16, 17, 43),
    POISON(65, 66, 2),
    DISEASE(3, -1, -1),
    CHARGE(22, 23, 46),
    NEGATIVE_CHARGE(24, 25, 47),
    SHIELD(18, 19, 44),
    ARMOUR(20, 21, 45),
    HEAL(6, -1, -1),
    VENOM(5, -1, -1),
    DISEASE_NEW(4, -1, -1),
    CORRUPTION(0, -1, -1),
    DEFLECT(8, 9, -1),
    BLUE_CHARGE(10, 11, -1),
    DEFLECT_DOWN(53, 54, 55),
    PURPLE_CHARGE_DOWN(59, 60, 61),
    BLEED(67, -1, -1),
    RESTORE_INSANITY(71, 72, -1),
    SCARAB_DAMAGE(73, -1, -1),
    SUN_DAMAGE(74, -1, -1);

    @Getter
    private final int non_tinted;
    @Getter
    private final int tinted;
    @Getter
    private final int max_hit;

    HitMark(int non_tinted, int tinted, int max_hit) {
        this.non_tinted = non_tinted;
        this.tinted = tinted;
        this.max_hit = max_hit;
    }

    public int getObservedType(Entity source, Entity target, Player observer, boolean isMaxHit) {
        if (isMaxHit && source == observer) {
            if (max_hit == -1) return non_tinted;
            return max_hit;
        }
        if (source == observer || target == observer) {
            if (non_tinted == -1) return tinted;
            return non_tinted;
        }
        return tinted;
    }
}
