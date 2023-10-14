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

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

/**
 * @Author Origin
 */
public final class MagicAccuracy {

    private static final SecureRandom random = new SecureRandom();

    @Getter
    @Setter
    public float modifier;

    @Getter
    private final Entity attacker;

    @Getter
    private final Entity defender;

    private final CombatType combatType;

    private final PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    public MagicAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean doesHit() {
        return successful();
    }

    private boolean successful() {
        final double attackRoll = getAttackRoll();
        final double defenceRoll = getDefenceRoll();

        double chance;
        if (attackRoll > defenceRoll)
            chance = 1D - (defenceRoll + 2D) / (2D * (attackRoll + 1D));
        else
            chance = attackRoll / (2D * (defenceRoll + 1D));

        double selectedChance = random.nextDouble();

       // System.out.println("PlayerStats - Attack=" + attackRoll + " Def=" + defenceRoll + " chanceOfSucess="
           // + new DecimalFormat("0.000").format(chance) + " rolledChance="
           // + new DecimalFormat("0.000").format(selectedChance) + " successful="
           // + (chance > selectedChance ? "YES" : "NO"));

        return chance > selectedChance;
    }

    private int getEquipmentBonusAttacker() {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (attacker instanceof Player) {
            bonus = attackerBonus.mage;
        } else if (attacker instanceof NPC) {
            bonus = attacker.getAsNpc().getCombatInfo().getBonuses().getMagic();
        }
        return bonus;
    }

    private int getMagicLevel(Entity entity) {
        return entity instanceof NPC && entity.getAsNpc().getCombatInfo() != null
            ? entity.getAsNpc().getCombatInfo().getStats().magic
            : entity.getSkills().level(Skills.MAGIC);
    }

    private double getPrayerBonus() {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(attacker, MYSTIC_WILL))
            prayerBonus *= 1.05F; // 5% magic level boost
        else if (Prayers.usingPrayer(attacker, MYSTIC_LORE))
            prayerBonus *= 1.10F; // 10% magic level boost
        else if (Prayers.usingPrayer(attacker, MYSTIC_MIGHT))
            prayerBonus *= 1.15F; // 15% magic level boost
        else if (Prayers.usingPrayer(attacker, AUGURY))
            prayerBonus *= 1.25F; // 25% magic level boost
        return prayerBonus;
    }

    private double getPrayerBonusDefender() {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(defender, AUGURY))
            prayerBonus *= 1.25F; //
        return prayerBonus;
    }

    public double getAttackRoll() {
        double magicLevel = getMagicLevel(attacker);
        double attackBonus = getEquipmentBonusAttacker();

        double effectiveLevel;
        if (attacker instanceof Player a) {
            effectiveLevel = magicLevel * getPrayerBonus() + 8;

            handler.triggerMagicAccuracyModificationAttacker(a, combatType, this);
            float modification = modifier;
            if (modification > 0) {
                effectiveLevel *= modification;
            }

            if (a.getCombatSpecial() != null && a.isSpecialActivated()) {
                double specialMultiplier = a.getCombatSpecial().getAccuracyMultiplier();
                effectiveLevel *= specialMultiplier;
            }
        } else {
            effectiveLevel = magicLevel + 9;
        }

        return effectiveLevel * (attackBonus + 64);
    }

    private int getEquipmentBonusDefender() {
        return defender instanceof NPC
            ? defender.getAsNpc().getCombatInfo().bonuses.magicdefence
            : EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo()).magedef;
    }

    public double getDefenceRoll() {
        double magicLevel = getMagicLevel(defender);
        double defenceBonus = getEquipmentBonusDefender();

        double effectiveLevel;
        if (defender instanceof Player) {
            magicLevel *= getPrayerBonusDefender();

            double defenceLevel = defender.getSkills().level(Skill.DEFENCE.getId());
            switch (defender.getCombat().getFightType().getStyle()) {
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
