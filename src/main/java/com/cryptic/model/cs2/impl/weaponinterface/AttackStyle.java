package com.cryptic.model.cs2.impl.weaponinterface;

import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.weapon.AttackType;

public final class AttackStyle {
    private final AttackType type;
    private final AttackExperienceType experienceType;

    public AttackStyle(AttackType type, AttackExperienceType experienceType) {
        this.type = type;
        this.experienceType = experienceType;
    }

    public AttackType getType() {
        return this.type;
    }

    public AttackExperienceType getExperienceType() {
        return this.experienceType;
    }

    public static enum AttackExperienceType {
        ATTACK_XP,
        STRENGTH_XP,
        DEFENCE_XP,
        RANGED_XP,
        MAGIC_XP,
        RANGED_DEFENCE_XP,
        MAGIC_DEFENCE_XP,
        SHARED_XP,
        NOT_AVAILABLE;

        private AttackExperienceType() {
        }
    }
}
