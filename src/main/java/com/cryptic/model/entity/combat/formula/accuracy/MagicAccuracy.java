package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import lombok.Getter;
import lombok.Setter;

import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

/**
 * @Author Origin
 */
public final class MagicAccuracy {

    @Getter @Setter public float modifier;
    @Getter private final Entity attacker;
    @Getter private final Entity defender;
    private final CombatType combatType;
    private final PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());
    public double attackRoll = 0;
    public double defenceRoll = 0;
    @Getter public double chance = 0;

    public MagicAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean successful(double selectedChance) {
        this.attackRoll = getAttackRoll();
        this.defenceRoll = getDefenceRoll();
        if (this.attackRoll > this.defenceRoll) this.chance = 1D - (this.defenceRoll + 2D) / (2D * (this.attackRoll + 1D));
        else this.chance = this.attackRoll / (2D * (this.defenceRoll + 1D));
        return this.chance > selectedChance;
    }

    private int getEquipmentBonusAttacker() {
        return this.attacker instanceof Player ? EquipmentInfo.totalBonuses(this.attacker, World.getWorld().equipmentInfo()).mage  : this.attacker.getAsNpc().getCombatInfo().getBonuses().getMagic();
    }

    private int getMagicLevelAttacker() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo() != null ? npc.getCombatInfo().getStats().magic : this.attacker.getSkills().level(Skills.MAGIC);
    }

    private int getMagicLevelDefender() {
        return this.defender instanceof NPC npc && npc.getCombatInfo() != null ? npc.getCombatInfo().getStats().magic : this.defender.getSkills().level(Skills.MAGIC);
    }

    private double getPrayerBonus() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.attacker, MYSTIC_WILL)) prayerBonus *= 1.05D; // 5% magic level boost
            else if (Prayers.usingPrayer(this.attacker, MYSTIC_LORE)) prayerBonus *= 1.10D; // 10% magic level boost
            else if (Prayers.usingPrayer(this.attacker, MYSTIC_MIGHT)) prayerBonus *= 1.15D; // 15% magic level boost
            else if (Prayers.usingPrayer(this.attacker, AUGURY)) prayerBonus *= 1.25D; // 25% magic level boost
        }
        return prayerBonus;
    }

    private double getPrayerBonusDefender() {
        double prayerBonus = 1D;
        if (this.defender instanceof Player) {
            if (Prayers.usingPrayer(this.defender, AUGURY)) prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    public double getAttackRoll() {
        double magicLevel = getMagicLevelAttacker();
        double attackBonus = getEquipmentBonusAttacker();
        double effectiveLevel;
        effectiveLevel = getEffectiveLevel(magicLevel);
        return effectiveLevel * (attackBonus + 64);
    }

    private double getEffectiveLevel(double magicLevel) {
        double effectiveLevel;
        if (this.attacker instanceof Player a) {
            effectiveLevel = magicLevel * getPrayerBonus() + 8;
            this.handler.triggerMagicAccuracyModificationAttacker(a, this.combatType, this);
            float modification = this.modifier;
            if (modification > 0) effectiveLevel *= modification;
            if (a.getCombatSpecial() != null && a.isSpecialActivated()) {
                double specialMultiplier = a.getCombatSpecial().getAccuracyMultiplier();
                effectiveLevel *= specialMultiplier;
            }
        } else {
            effectiveLevel = magicLevel + 9;
        }
        return effectiveLevel;
    }

    private int getEquipmentBonusDefender() {
        return this.defender instanceof NPC npc ? npc.getCombatInfo().getBonuses().getMagicdefence() : EquipmentInfo.totalBonuses(this.defender, World.getWorld().equipmentInfo()).getMagedef();
    }

    public double getDefenceRoll() {
        double magicLevel = getMagicLevelDefender();
        double defenceBonus = getEquipmentBonusDefender();
        double effectiveLevel;
        if (this.defender instanceof Player player) {
            magicLevel *= getPrayerBonusDefender();
            double defenceLevel = player.getSkills().level(Skill.DEFENCE.getId());
            switch (player.getCombat().getFightType().getStyle()) {
                case DEFENSIVE -> defenceLevel += 3;
                case CONTROLLED -> defenceLevel += 1;
            }
            effectiveLevel = magicLevel * 0.7 + defenceLevel * 0.3 + 8;
        } else {
            effectiveLevel = magicLevel + 9;
        }
        return effectiveLevel * (defenceBonus + 64);
    }

}
