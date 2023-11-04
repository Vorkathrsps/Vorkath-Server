package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

/**
 * @Author Origin
 */
public class MeleeAccuracy {
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

    public MeleeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean successful(double selectedChance) {
        attackRoll = getAttackRoll(this.attacker);
        defenceRoll = getDefenceRoll(this.defender);
        if (attackRoll > defenceRoll) chance = 1F - ((defenceRoll + 2F) / (2F * (attackRoll + 1F)));
        else chance = attackRoll / (2F * (defenceRoll + 1F));
        return chance > selectedChance;
    }

    private double getPrayerDefenseBonus(final Entity defender) {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(defender, THICK_SKIN)) prayerBonus *= 1.05F; // 5% def level boost
        else if (Prayers.usingPrayer(defender, ROCK_SKIN)) prayerBonus *= 1.10F; // 10% def level boost
        else if (Prayers.usingPrayer(defender, STEEL_SKIN)) prayerBonus *= 1.15F; // 15% def level boost
        if (Prayers.usingPrayer(defender, CHIVALRY)) prayerBonus *= 1.20F; // 20% def level boost
        else if (Prayers.usingPrayer(defender, PIETY)) prayerBonus *= 1.25F; // 25% def level boost
        return prayerBonus;
    }

    private double getPrayerAttackBonus(final Entity attacker) {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(attacker, CLARITY_OF_THOUGHT)) prayerBonus *= 1.05F; // 5% attack level boost
        else if (Prayers.usingPrayer(attacker, IMPROVED_REFLEXES)) prayerBonus *= 1.10F; // 10% attack level boost
        else if (Prayers.usingPrayer(attacker, INCREDIBLE_REFLEXES)) prayerBonus *= 1.15F; // 15% attack level boost
        else if (Prayers.usingPrayer(attacker, CHIVALRY)) prayerBonus *= 1.15F; // 15% attack level boost
        else if (Prayers.usingPrayer(attacker, PIETY)) prayerBonus *= 1.20F; // 20% attack level boost
        return prayerBonus;
    }

    private double getEffectiveAttack(Entity attacker) {
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = getAttackLevel(attacker) * getPrayerAttackBonus(attacker);
        float modification = modifier;
        if (attacker instanceof Player a) {
            effectiveLevel = Math.floor(effectiveLevel);
            handler.triggerMeleeAccuracyModificationAttacker(a, combatType, this);
            switch (fightStyle) {
                case ACCURATE -> effectiveLevel += 3;
                case CONTROLLED -> effectiveLevel += 1;
            }
            if (modification > 0) effectiveLevel *= modification;
            double specialMultiplier = a.getCombatSpecial().getAccuracyMultiplier();
            if (a.getCombatSpecial() != null && a.isSpecialActivated()) effectiveLevel *= specialMultiplier;
        }
        effectiveLevel += 8;
        return Math.floor(effectiveLevel);
    }

    private int getAttackLevel(final Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo().stats != null ? attacker.getAsNpc().getCombatInfo().stats.attack : attacker.getSkills().level(Skills.ATTACK);
    }

    private int getDefenceLevel(Entity defender) {
        return defender instanceof NPC && defender.getAsNpc().getCombatInfo().stats != null ? defender.getAsNpc().getCombatInfo().stats.defence : defender.getSkills().level(Skills.DEFENCE);
    }

    @Getter
    @Setter
    public double percentage = 1;

    private int getGearDefenceBonus() {
        int bonus = 0;
        AttackType type = defender instanceof NPC npc && npc.getCombat().getAttackType() != null ? npc.getCombat().getAttackType() : defender.getCombat().getFightType().getAttackType();

        if (defender instanceof NPC npc) {
            var stats = npc.getCombatInfo().bonuses;
            if (npc.getCombatInfo() != null) {
                if (npc.getCombatInfo().stats != null) {
                    switch (type) {
                        case STAB -> bonus = stats.stabdefence;
                        case CRUSH -> bonus = stats.crushdefence;
                        case SLASH -> bonus = stats.slashdefence;
                    }
                }
            }
        } else if (defender instanceof Player player) {
            var stats = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
            switch (type) {
                case STAB -> bonus = stats.stabdef;
                case CRUSH -> bonus = stats.crushdef;
                case SLASH -> bonus = stats.slashdef;
            }
        }

        return bonus;
    }

    public int getGearAttackBonus(Entity attacker) {
        int bonus = 0;
        if (attacker instanceof Player player) {
            EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
            final AttackType type = player.getCombat().getFightType().getAttackType();
            switch (type) {
                case STAB -> bonus = attackerBonus.stab;
                case CRUSH -> bonus = attackerBonus.crush;
                case SLASH -> bonus = attackerBonus.slash;
            }
        } else if (attacker instanceof NPC npc) {
            bonus = npc.getCombatInfo().getBonuses().getAttack();
        }

        return bonus;
    }

    public double getAttackRoll(Entity attacker) {
        double effectiveLevel = getEffectiveAttack(attacker);
        double attackBonus = getGearAttackBonus(attacker);

        double roll = effectiveLevel * (attackBonus + 64);

        return Math.floor(roll);
    }

    public double getDefenceRoll(Entity defender) {
        double defenceLevel = getDefenceLevel(defender);
        double defenceBonus = getGearDefenceBonus();
        if (defender instanceof Player) {
            defenceLevel *= getPrayerDefenseBonus(defender);
            switch (defender.getCombat().getFightType().getStyle()) {
                case DEFENSIVE -> defenceLevel += 3;
                case CONTROLLED -> defenceLevel += 1;
            }
        } else {
            defenceLevel += 9;
        }
        return defenceLevel * (defenceBonus + 64);
    }

}
