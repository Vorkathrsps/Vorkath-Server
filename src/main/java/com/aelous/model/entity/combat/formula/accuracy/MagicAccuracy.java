package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.aelous.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.AUGURY;

/**
 * @Author Origin
 */
public class MagicAccuracy {
    @Getter
    @Setter
    float modifier;
    @Getter
    @Setter
    Entity attacker, defender;
    CombatType combatType;
    byte[] seed = new byte[16];
    SecureRandom random = new SecureRandom(seed);

    public MagicAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean doesHit() {
        return successful();
    }

    private boolean successful() {
        final int attackBonus = getAttackRoll();
        final int defenceBonus = getDefenceRoll();

        random.nextBytes(seed);

        val accuracy = attackBonus > defenceBonus ? (1F - (defenceBonus + 2F) / (2F * (attackBonus + 1F))) : (attackBonus / (2F * (defenceBonus + 1F)));

        double selectedChance = random.nextFloat();

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(accuracy) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (accuracy > selectedChance ? "YES" : "NO"));

        return accuracy > selectedChance;
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

    private int getEquipmentBonusDefender() {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        return defender instanceof NPC ? defender.getAsNpc().getCombatInfo().bonuses.magicdefence : defenderBonus.magedef;
    }

    private int getEffectiveDefenceDefender() {
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).getCombatInfo().stats.defence : (int) Math.floor(defender.getSkills().level(Skills.DEFENCE) * getPrayerBonusDefender());
        var fightStyle = defender.getCombat().getFightType().getStyle();
        int magicLevel = getMagicLevelDefender();

        effectiveLevel = Math.round(effectiveLevel);

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel += 3;
            case CONTROLLED -> effectiveLevel += 1;
        }

        effectiveLevel += magicLevel;

        effectiveLevel += 9;
        System.out.println(effectiveLevel);
        return (int) Math.floor(effectiveLevel);
    }

    private int getMagicLevelAttacker() {
        return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo() != null ? attacker.getAsNpc().getCombatInfo().getStats().magic : attacker.getSkills().level(Skills.MAGIC);
    }

    private int getMagicLevelDefender() {
        return defender instanceof NPC ? defender.getAsNpc().getCombatInfo().getStats().magic : defender.getSkills().level(Skills.MAGIC);
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

    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    private int getEffectiveLevelAttacker() {
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getMagicLevelAttacker() * getPrayerBonus());

        if (attacker instanceof Player a)
            handler.triggerMagicAccuracyModificationAttacker(a, combatType, this);

        float modification = modifier;

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                double specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
                if (attacker.getAsPlayer().isSpecialActivated()) {
                    effectiveLevel = (int) (effectiveLevel * specialMultiplier);
                }
            }
        }

        switch (fightStyle) {
            case ACCURATE ->
                effectiveLevel += weapon != null && weapon.getId() != ItemIdentifiers.TRIDENT_OF_THE_SEAS ? 3 : 2;
            case CONTROLLED -> effectiveLevel += 1;
        }

        effectiveLevel = modification > 0 ? (int) Math.floor(effectiveLevel * modification) : effectiveLevel;

        effectiveLevel += 8F;

        return (int) Math.floor(effectiveLevel);
    }

    private int getAttackRoll() {
        int equipmentAttackBonus = getEquipmentBonusAttacker();
        return (int) Math.floor(getEffectiveLevelAttacker() * (equipmentAttackBonus + 64));
    }


    private int getDefenceRoll() {
        int eDef = (int) Math.floor(getEffectiveDefenceDefender() * 0.3F);
        int eAtt = (int) Math.floor(getEffectiveLevelAttacker() * 0.7F);
        int finalDef = eDef + eAtt;
        int equipmentDefenceBonus = getEquipmentBonusDefender();
        return (int) Math.floor(finalDef * (equipmentDefenceBonus + 64));
    }
}
