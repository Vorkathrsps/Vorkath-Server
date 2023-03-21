package com.aelous.model.entity.combat.method.impl.npcs.vasilas;

public enum NylocasCombatFormula {
    SMALL(1, 1, 1, 0.2),
    BIG(1, 1, 1, 0.5),
    BOSS(0.1, 0.01, 0.1, 0.8);

    double protectFromMageFailChance;
    double protectFromRangedFailChance;
    double protectFromMeleeFailChance;
    double doubleHitChance;

    NylocasCombatFormula(double protectFromMageFailChance, double protectFromRangedFailChance, double protectFromMeleeFailChance, double doubleHitChance) {
        this.protectFromMageFailChance = protectFromMageFailChance;
        this.protectFromRangedFailChance = protectFromRangedFailChance;
        this.protectFromMeleeFailChance = protectFromMeleeFailChance;
    }

    public double getProtectFromMageFailChance() {
        return protectFromMageFailChance;
    }

    public double getProtectFromRangedFailChance() {
        return protectFromRangedFailChance;
    }

    public double getProtectFromMeleeFailChance() {
        return protectFromMeleeFailChance;
    }

    public double getDoubleHitChance() {
        return doubleHitChance;
    }
}
