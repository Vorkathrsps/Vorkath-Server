package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;

import com.cryptic.model.content.sigils.Sigil;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
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

import javax.management.Attribute;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public double attackRoll = 0;
    public double defenceRoll = 0;
    @Getter public double chance = 0;
    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    public RangeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
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

    private double getPrayerAttackBonus() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.attacker, SHARP_EYE)) prayerBonus *= 1.05D; // 5% range level boost
            else if (Prayers.usingPrayer(this.attacker, HAWK_EYE)) prayerBonus *= 1.10D; // 10% range level boost
            else if (Prayers.usingPrayer(this.attacker, EAGLE_EYE)) prayerBonus *= 1.15D; // 15% range level boost
            else if (Prayers.usingPrayer(this.attacker, RIGOUR)) prayerBonus *= 1.20D; // 20% range level boost
        }
        return prayerBonus;
    }

    private double getPrayerDefenseBonus(Entity defender) {
        double prayerBonus = 1D;
        if (defender instanceof Player) if (Prayers.usingPrayer(attacker, RIGOUR)) prayerBonus *= 1.25D;
        return prayerBonus;
    }

    private int getEffectiveDefence() {
        FightStyle fightStyle = this.defender.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel() * getPrayerDefenseBonus(this.defender));
        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }
        effectiveLevel = (int) Math.floor(effectiveLevel + 8);
        return effectiveLevel;
    }

    private int getEffectiveRanged() {
        FightStyle fightStyle = this.attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = (int) Math.floor(getRangeLevel() * getPrayerAttackBonus());
        double specialMultiplier;
        float modification = this.modifier;
        if (this.attacker instanceof Player player) {
            this.handler.triggerRangeAccuracyModificationAttacker(player, this.combatType, this);
            if (fightStyle == FightStyle.ACCURATE) effectiveLevel = (int) Math.floor(effectiveLevel + 3);

            if (player.getCombatSpecial() != null && player.isSpecialActivated()) {
                specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
                effectiveLevel *= specialMultiplier;
            }
        }
        effectiveLevel = modification > 0 ? Math.floor(effectiveLevel * modification) : effectiveLevel;
        effectiveLevel = (int) Math.floor(effectiveLevel + 8);
        return (int) Math.floor(effectiveLevel);
    }

    private int getRangeLevel() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo() != null && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().ranged : this.attacker.getSkills().level(Skills.RANGED);
    }

    private int getGearAttackBonus() {
        return this.attacker instanceof Player ? EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo()).getRange() : this.attacker.getAsNpc().getCombatInfo().getBonuses().getRanged();
    }

    private int getGearDefenceBonus() {
        return this.defender instanceof Player ? EquipmentInfo.totalBonuses(this.defender, World.getWorld().equipmentInfo()).getRangedef() : this.defender.getAsNpc().getCombatInfo().getBonuses().getRangeddefence();
    }

    private int getAttackRoll() {
        int effectiveRangeLevel = getEffectiveRanged();
        int equipmentRangeBonus = getGearAttackBonus();
        return effectiveRangeLevel * (equipmentRangeBonus + 64);
    }

    private int getDefenceRoll() {
        int effectiveDefenceLevel = getEffectiveDefence();
        int equipmentRangeBonus = getGearDefenceBonus();
        return effectiveDefenceLevel * (equipmentRangeBonus + 64);
    }
}
