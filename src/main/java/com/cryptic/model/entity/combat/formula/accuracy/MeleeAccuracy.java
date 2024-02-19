package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import lombok.Getter;
import lombok.Setter;

import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

/**
 * @Author Origin
 */
public class MeleeAccuracy {

    @Getter @Setter public float modifier;
    @Getter @Setter Entity attacker, defender;
    CombatType combatType;
    public double attackRoll = 0;
    public double defenceRoll = 0;
    @Getter public double chance = 0;
    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    public MeleeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean successful(double selectedChance) {
        this.attackRoll = getAttackRoll();
        this.defenceRoll = getDefenceRoll();
        if (this.attackRoll > this.defenceRoll) this.chance = 1D - (this.defenceRoll + 2D) / (2D * (this.attackRoll + 1D));
        else this.chance = this.attackRoll / (2D * (this.defenceRoll + 1D));
        if (Hit.isDebugAccuracy()) this.attacker.message("[Melee] Chance To Hit: [" + String.format("%.2f%%", this.chance * 100) + "]");
        if (Hit.isDebugAccuracy() &&this.defender instanceof Player player && this.attacker instanceof NPC) {
            player.message("[Melee] Chance To Hit: [" + String.format("%.2f%%", this.chance * 100) + "]");
        }
        return this.chance > selectedChance;
    }

    private double getPrayerDefenseBonus() {
        double prayerBonus = 1F;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.defender, THICK_SKIN)) prayerBonus *= 1.05D; // 5% def level boost
            else if (Prayers.usingPrayer(this.defender, ROCK_SKIN)) prayerBonus *= 1.10D; // 10% def level boost
            else if (Prayers.usingPrayer(this.defender, STEEL_SKIN)) prayerBonus *= 1.15D; // 15% def level boost
            else if (Prayers.usingPrayer(this.defender, CHIVALRY)) prayerBonus *= 1.20D; // 20% def level boost
            else if (Prayers.usingPrayer(this.defender, PIETY)) prayerBonus *= 1.25D; // 25% def level boost
        }
        return prayerBonus;
    }

    private double getPrayerAttackBonus() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.attacker, CLARITY_OF_THOUGHT)) prayerBonus *= 1.05D; // 5% attack level boost
            else if (Prayers.usingPrayer(this.attacker, IMPROVED_REFLEXES)) prayerBonus *= 1.10D; // 10% attack level boost
            else if (Prayers.usingPrayer(this.attacker, INCREDIBLE_REFLEXES)) prayerBonus *= 1.15D; // 15% attack level boost
            else if (Prayers.usingPrayer(this.attacker, CHIVALRY)) prayerBonus *= 1.15D; // 15% attack level boost
            else if (Prayers.usingPrayer(this.attacker, PIETY)) prayerBonus *= 1.20D; // 20% attack level boost
        }
        return prayerBonus;
    }

    private int getEffectiveAttack() {
        FightStyle fightStyle = this.attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) (getAttackLevel() * getPrayerAttackBonus());
        if (this.attacker instanceof Player a) {
            this.handler.triggerMeleeAccuracyModificationAttacker(a, this.combatType, this);
            switch (fightStyle) {
                case ACCURATE -> effectiveLevel += 3;
                case CONTROLLED -> effectiveLevel += 1;
            }
            if (a.getCombatSpecial() != null) {
                double specialMultiplier = a.getCombatSpecial().getAccuracyMultiplier();
                if (a.isSpecialActivated()) {
                    effectiveLevel *= specialMultiplier;
                }
            }
        }
        effectiveLevel += 8;
        return effectiveLevel;
    }

    private int getAttackLevel() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().attack : this.attacker.getSkills().level(Skills.ATTACK);
    }

    private int getDefenceLevel() {
        return this.defender instanceof NPC npc && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().defence : this.defender.getSkills().level(Skills.DEFENCE);
    }

    private int getGearDefenceBonus() {
        int bonus = 0;
        AttackType type;
        if (this.defender instanceof NPC npc) {
            var stats = npc.getCombatInfo().bonuses;
            type = this.attacker instanceof Player player ? player.getCombat().getAttackType() : npc.getCombat().getAttackType();
            if (type != null) {
                if (npc.getCombatInfo() != null) {
                    if (npc.getCombatInfo().stats != null) {
                        switch (type) {
                            case STAB -> bonus = stats.stabdefence;
                            case CRUSH -> bonus = stats.crushdefence;
                            case SLASH -> bonus = stats.slashdefence;
                        }
                    }
                }
            }
        } else if (this.defender instanceof Player) {
            var stats = EquipmentInfo.totalBonuses(this.defender, World.getWorld().equipmentInfo());
            type = this.defender.getCombat().getAttackType();
            if (type != null) {
                switch (type) {
                    case STAB -> bonus = stats.stabdef;
                    case CRUSH -> bonus = stats.crushdef;
                    case SLASH -> bonus = stats.slashdef;
                }
            }
        }
        return bonus;
    }

    public int getGearAttackBonus() {
        int bonus = 0;
        if (this.attacker instanceof Player) {
            EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(this.attacker, World.getWorld().equipmentInfo());
            final AttackType type = this.attacker.getCombat().getFightType().getAttackType();
            switch (type) {
                case STAB -> bonus = attackerBonus.getStab();
                case CRUSH -> bonus = attackerBonus.getCrush();
                case SLASH -> bonus = attackerBonus.getSlash();
            }
        } else if (this.attacker instanceof NPC npc) {
            bonus = npc.getCombatInfo().getBonuses().getAttack();
        }
        return bonus;
    }

    public int getAttackRoll() {
        float modification = this.modifier;
        int effectiveLevel = getEffectiveAttack();
        int attackBonus = getGearAttackBonus();
        var roll = effectiveLevel * (attackBonus + 64);
        if (modification > 0) roll *= modification;
        return roll;
    }

    public int getDefenceRoll() {
        int defenceLevel = getDefenceLevel();
        int defenceBonus = getGearDefenceBonus();
        if (this.defender instanceof Player) {
            defenceLevel *= getPrayerDefenseBonus();
            switch (this.defender.getCombat().getFightType().getStyle()) {
                case DEFENSIVE -> defenceLevel += 3;
                case CONTROLLED -> defenceLevel += 1;
            }
        } else {
            defenceLevel += 9;
        }
        return defenceLevel * (defenceBonus + 64);
    }

}
