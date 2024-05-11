package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.DamageModifyingHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageModifying;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface AbstractAccuracy {

    Logger logger = LogManager.getLogger(AbstractAccuracy.class);

    DamageModifyingHandler handler = new DamageModifyingHandler(new EquipmentDamageModifying());

    Entity attacker();

    Entity defender();

    CombatType getCombatType();

    double modifier();

    int getEquipmentBonusForAttacker();

    int getEquipmentBonusForDefender();

    int getOffensiveSkillLevelAttacker();

    int getDefensiveSKillLevelDefender();

    double getPrayerBonusAttacker();

    double getPrayerBonusDefender();

    int getOffensiveStyleBonus();

    int getDefensiveStyleBonus();

    default double getBoost() {
        if (this.getCombatType() == null) return 0.0D;
        if (this.getCombatType().equals(CombatType.MELEE)) return 0.80D;
        else if (this.getCombatType().equals(CombatType.MAGIC)) return 0.90D;
        else if (this.getCombatType().equals(CombatType.RANGED)) return 0.90D;
        else return 0.0D;
    }

    default boolean success(double selectedChance) {
        double chance;
        double specialMultiplier = 0.0D;

        if (this.attacker() instanceof Player player && player.getCombatSpecial() != null && player.isSpecialActivated()) {
            specialMultiplier = getSpecialMultiplier(player);
        }

        int attackRoll = this.getAttackRoll();

        if (specialMultiplier > 0.0D) {
            attackRoll *= specialMultiplier;
        }

        int defenceRoll = this.getDefenceRoll();

        if (attackRoll > defenceRoll) chance = 1D - (defenceRoll + 2D) / (2D * (attackRoll + 1D));
        else chance = attackRoll / (2D * (defenceRoll + 1D));

        if (Hit.isDebugAccuracy()) {
            sendDebugPrints(chance, attackRoll, defenceRoll);
        }

        return chance > selectedChance;
    }

    default int getAttackRoll() {
        double modification = this.modifier();
        double prayerBonus = this.getPrayerBonusAttacker();
        int attackLevel = this.getOffensiveSkillLevelAttacker();
        attackLevel *= prayerBonus;
        attackLevel += this.getOffensiveStyleBonus();
        attackLevel += 8;
        int effectiveAttack = attackLevel;
        int equipmentBonus = this.getEquipmentBonusForAttacker();
        int attackRoll = effectiveAttack * (equipmentBonus + 64);
        if (this.getSpecificBonus() > 0.0D) {
            attackRoll = (int) (attackRoll * this.getSpecificBonus());
        }
        if (modification > 0.0D) attackRoll *= modification;
        return attackRoll;
    }

    default int getDefenceRoll() {
        int effectiveDefence;
        double prayerBonus = this.getPrayerBonusDefender();
        int defenceLevel = this.getDefensiveSKillLevelDefender();
        defenceLevel *= prayerBonus;
        int equipmentBonus = this.getEquipmentBonusForDefender();
        if (this.defender() instanceof Player) {
            effectiveDefence = defenceLevel;
            effectiveDefence += getDefensiveStyleBonus();
        } else effectiveDefence = defenceLevel + 9;
        return effectiveDefence * (equipmentBonus + 64);
    }

    default double getSpecificBonus() {
        double bonus = 1.0D;
        if (this.attacker() instanceof Player player) {
            bonus = this.handler.getAccuracyModification(player, this.getCombatType(), this);
            return bonus;
        }
        return bonus;
    }

    default double getSpecialMultiplier(Player player) {
        return player.getCombatSpecial().getAccuracyMultiplier();
    }

    private void sendDebugPrints(double chance, int attackRoll, int defenceRoll) {
        this.attacker().message("*<shad=0>[" + Color.PURPLE.wrap("" + this.getCombatType()) + "]</shad>" + " Attack Roll: [" + attackRoll + "]");
        this.attacker().message("*<shad=0>[" + Color.PURPLE.wrap("" + this.getCombatType()) + "]</shad>" + " Defence Roll: [" + defenceRoll + "]");
        this.attacker().message("*<shad=0>[" + Color.PURPLE.wrap("" + this.getCombatType()) + "]</shad>" + " Chance To Hit: [" + String.format("%.2f%%", chance * 100) + "]");
    }

}
