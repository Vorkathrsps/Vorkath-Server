package com.aelous.model.entity.combat.method.impl.npcs.vasilas;

public enum NylocasSize {
    SMALL(NylocasCombatFormula.SMALL),
    BIG(NylocasCombatFormula.BIG),
    BOSS(NylocasCombatFormula.BOSS);

    private final NylocasCombatFormula formula;

    NylocasSize(NylocasCombatFormula formula) {
        this.formula = formula;
    }

    public NylocasCombatFormula getFormula() {
        return this.formula;
    }

    public static NylocasSize fromName(String name) {
        if (name.equalsIgnoreCase("small")) {
            return NylocasSize.SMALL;
        } else if (name.equalsIgnoreCase("big")) {
            return NylocasSize.BIG;
        }

        return NylocasSize.SMALL;
    }
}
