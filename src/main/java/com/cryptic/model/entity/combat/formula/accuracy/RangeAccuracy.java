package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;

import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;

/**
 * @Author Origin
 */
public class RangeAccuracy {

    @Getter
    @Setter
    public float modifier;
    @Getter
    @Setter
    Entity attacker, defender;
    CombatType combatType;
    @Getter public double attackRoll = 0;
    @Getter public double defenceRoll = 0;
    @Getter public double chance = 0;
    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    public RangeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean successful(double selectedChance) {
        attackRoll = getAttackRoll(this.attacker);
        defenceRoll = getDefenceRoll(this.defender);
        if (attackRoll > defenceRoll) chance = 1D - (defenceRoll + 2D) / (2D * (attackRoll + 1D));
        else chance = attackRoll / (2D * (defenceRoll + 1D));
        return chance > selectedChance;
    }

    private double getPrayerAttackBonus(Entity attacker) {
        double prayerBonus = 1D;
        if (attacker instanceof Player) {
            if (Prayers.usingPrayer(attacker, SHARP_EYE)) prayerBonus *= 1.05D; // 5% range level boost
            else if (Prayers.usingPrayer(attacker, HAWK_EYE)) prayerBonus *= 1.10D; // 10% range level boost
            else if (Prayers.usingPrayer(attacker, EAGLE_EYE)) prayerBonus *= 1.15D; // 15% range level boost
            else if (Prayers.usingPrayer(attacker, RIGOUR)) prayerBonus *= 1.20D; // 20% range level boost
        }
        return prayerBonus;
    }

    private double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 1D;
        if (defender instanceof Player) {
            if (Prayers.usingPrayer(defender, RIGOUR)) prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    private int getEffectiveDefence(Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel(defender) * getPrayerDefenseBonus(defender));
        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }
        effectiveLevel = (int) Math.floor(effectiveLevel + 8);
        return effectiveLevel;
    }

    private int getEffectiveRanged(Entity attacker) {
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = (int) Math.floor(getRangeLevel(attacker) * getPrayerAttackBonus(attacker));
        double specialMultiplier;
        float modification = modifier;
        if (attacker instanceof Player player) {
            handler.triggerRangeAccuracyModificationAttacker(player, combatType, this);
            if (fightStyle == FightStyle.ACCURATE) effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
            if (player.getCombatSpecial() != null && player.isSpecialActivated()) effectiveLevel *= specialMultiplier;
        }
        effectiveLevel = modification > 0 ? Math.floor(effectiveLevel * modification) : effectiveLevel;
        effectiveLevel = (int) Math.floor(effectiveLevel + 8);
        return (int) Math.floor(effectiveLevel);
    }

    private int getRangeLevel(Entity attacker) {
        return attacker instanceof NPC npc && npc.getCombatInfo() != null && npc.getCombatInfo().stats != null ? npc.getCombatInfo().stats.ranged : attacker.getSkills().level(Skills.RANGED);
    }

    private int getGearAttackBonus(Entity attacker) {
        return attacker instanceof Player ? EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo()).range : attacker.getAsNpc().getCombatInfo().getBonuses().ranged;
    }

    private int getGearDefenceBonus(Entity defender) {
        return defender instanceof Player ? EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo()).rangedef : defender.getAsNpc().getCombatInfo().getBonuses().rangeddefence;
    }

    private int getAttackRoll(Entity attacker) {
        int effectiveRangeLevel = (int) Math.floor(getEffectiveRanged(attacker));
        int equipmentRangeBonus = getGearAttackBonus(attacker);
        return (int) Math.floor(effectiveRangeLevel * (equipmentRangeBonus + 64));
    }

    private int getDefenceRoll(Entity defender) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence(defender));
        int equipmentRangeBonus = getGearDefenceBonus(defender);
        return (int) Math.floor(effectiveDefenceLevel * (equipmentRangeBonus + 64));
    }
}
