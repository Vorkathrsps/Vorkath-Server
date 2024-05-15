package com.cryptic.model.entity.combat.upgrades;

public record UpgradeData(int stabAttack, int slashAttack, int crushAttack, int magicAttack, int rangeAttack,
                          int defensiveStab, int defensiveSlash, int defensiveCrush, int defensiveMagic,
                          int defensiveRange, int strength, int rangeStrength, int magicDamage, int prayer,
                          int attackSpeed) {
}
