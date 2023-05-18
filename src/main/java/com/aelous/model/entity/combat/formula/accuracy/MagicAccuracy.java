package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.effects.PreDamageEffectHandler;
import com.aelous.model.entity.combat.method.effects.equipment.EquipmentDamageEffect;
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
import lombok.Getter;
import lombok.Setter;

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

    public boolean doesHit(CombatType combatType) {
        return successful(combatType);
    }

    private boolean successful(CombatType combatType) {
        final int attackBonus = getAttackRoll( combatType);
        final int defenceBonus = getDefenceRoll(combatType);
        double successfulRoll;

        random.nextBytes(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
        } else {
            successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
        }

        double selectedChance = random.nextFloat();

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    private int getEquipmentBonusAttacker() {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (attacker instanceof Player) {
            if (!WildernessArea.inWild((Player) attacker) && ((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                bonus = attackerBonus.mage += Math.min(attackerBonus.mage * 3, attackerBonus.mage * attackerBonus.mage);
            } else {
                bonus = attackerBonus.mage;
            }
        } else if (attacker instanceof NPC) {
            bonus = attacker.getAsNpc().getCombatInfo().getBonuses().getMagic();
        }
        return bonus;
    }

    private int getEquipmentBonusDefender() {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        return defender instanceof NPC ? defender.getAsNpc().getCombatInfo().bonuses.magicdefence : defenderBonus.magedef;
    }

    private int getEffectiveDefenceDefender(CombatType style) {
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).getCombatInfo().stats.defence : (int) Math.floor(defender.getSkills().level(Skills.DEFENCE) * getPrayerBonusDefender(defender));
        var fightStyle = defender.getCombat().getFightType().getStyle();
        int magicLevel = getMagicLevelDefender(defender);
        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel += 3;
            case CONTROLLED -> effectiveLevel += 1;
        }
        effectiveLevel = (int) Math.floor(effectiveLevel + magicLevel);
        effectiveLevel = (int) Math.floor(effectiveLevel * 0.3F);
        effectiveLevel *= 1.7F;
        effectiveLevel = (int) Math.floor(effectiveLevel);
        effectiveLevel += 9;
        return (int) Math.floor(effectiveLevel);
    }

    private int getMagicLevelAttacker(final Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo() != null ? attacker.getAsNpc().getCombatInfo().getStats().magic : attacker.getSkills().level(Skills.MAGIC);
    }

    private int getMagicLevelDefender(final Entity defender) {
        return defender instanceof NPC ? defender.getAsNpc().getCombatInfo().getStats().magic : defender.getSkills().level(Skills.MAGIC);
    }

    private double getPrayerBonus(CombatType style) {
        double prayerBonus = 1F;
        if (style == CombatType.MAGIC) {
            if (Prayers.usingPrayer(attacker, MYSTIC_WILL))
                prayerBonus *= 1.05F; // 5% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_LORE))
                prayerBonus *= 1.10F; // 10% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_MIGHT))
                prayerBonus *= 1.15F; // 15% magic level boost
            else if (Prayers.usingPrayer(attacker, AUGURY))
                prayerBonus *= 1.25F; // 25% magic level boost
        }
        return prayerBonus;
    }

    private double getPrayerBonusDefender(final Entity defender) {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(defender, AUGURY))
            prayerBonus *= 1.25F; //
        return prayerBonus;
    }

    private int getEffectiveLevelAttacker(CombatType combatType) {
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.floor(getMagicLevelAttacker(attacker) * getPrayerBonus(combatType));
        PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

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

        effectiveLevel = modification > 0 ? Math.floor(effectiveLevel * modification) : effectiveLevel;

        effectiveLevel += 9;

        return (int) Math.floor(effectiveLevel);
    }

    private int getAttackRoll(CombatType combatType) {
        int getEffectiveMagicAttacker = (int) Math.floor(getEffectiveLevelAttacker(combatType));
        int equipmentAttackBonus = getEquipmentBonusAttacker();
        return (int) Math.floor(getEffectiveMagicAttacker * (equipmentAttackBonus + 64));
    }


    private int getDefenceRoll(CombatType combatType) {
        int equipmentDefenceBonus = getEquipmentBonusDefender();
        return (int) Math.floor((getEffectiveDefenceDefender(combatType) * (equipmentDefenceBonus + 64)));
    }
}
