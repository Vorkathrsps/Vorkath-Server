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

import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;

/**
 * @Author Origin
 */
public class RangeAccuracy {

    @Getter
    @Setter
    float modifier;
    @Getter
    @Setter
    Entity attacker, defender;
    CombatType combatType;
    byte[] seed = new byte[16];
    SecureRandom random = new SecureRandom(seed);

    public RangeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean doesHit() {
        return successful();//doesHit(entity, enemy, style, 1);
    }

    private boolean successful() {
        final int attackBonus = getAttackRoll();
        final int defenceBonus = getDefenceRoll();
        double successfulRoll;

        random.nextBytes(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
        } else {
            successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
        }

        double selectedChance = random.nextFloat();

        return successfulRoll > selectedChance;
    }

    private double getPrayerAttackBonus() {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(attacker, SHARP_EYE))
            prayerBonus *= 1.05D; // 5% range level boost
        else if (Prayers.usingPrayer(attacker, HAWK_EYE))
            prayerBonus *= 1.10D; // 10% range level boost
        else if (Prayers.usingPrayer(attacker, EAGLE_EYE))
            prayerBonus *= 1.15D; // 15% range level boost
        else if (Prayers.usingPrayer(attacker, RIGOUR))
            prayerBonus *= 1.20D; // 20% range level boost
        return prayerBonus;
    }

    private double getPrayerDefenseBonus() {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(defender, RIGOUR)) {
            prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    private int getEffectiveDefence() {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel() * getPrayerDefenseBonus());

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        return effectiveLevel;
    }

    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    private int getEffectiveRanged() {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = (int) Math.floor(getRangeLevel() * getPrayerAttackBonus());
        double specialMultiplier = 1;

        if (attacker instanceof Player a)
            handler.triggerRangeAccuracyModificationAttacker(a, combatType, this);

        float modification = modifier;

        if (fightStyle == FightStyle.ACCURATE) {
            effectiveLevel = (int) Math.floor(effectiveLevel + 3);
        }

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
            }
        }

        if (attacker.isPlayer() && attacker.getAsPlayer().isSpecialActivated()) {
            effectiveLevel *= specialMultiplier;
        }

        effectiveLevel = modification > 0 ? Math.floor(effectiveLevel * modification) : effectiveLevel;

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        return (int) Math.floor(effectiveLevel);
    }

    private int getRangeLevel() {
        int rangeLevel = 1;
        if (attacker instanceof NPC npc) {
            if (npc.getCombatInfo() != null && npc.getCombatInfo().stats != null)
                rangeLevel = npc.getCombatInfo().stats.ranged;
        } else {
            rangeLevel = attacker.getSkills().level(Skills.RANGED);
        }
        return rangeLevel;
    }

    private int getGearAttackBonus() {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus;
        bonus = attackerBonus.range;
        return bonus;
    }

    private int getGearDefenceBonus() {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus;
        bonus = attackerBonus.rangedef;
        return bonus;
    }

    private int getAttackRoll() {
        int effectiveRangeLevel = (int) Math.floor(getEffectiveRanged());
        int equipmentRangeBonus = getGearAttackBonus();
        return (int) Math.floor(effectiveRangeLevel * (equipmentRangeBonus + 64));
    }

    private int getDefenceRoll() {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence());
        int equipmentRangeBonus = getGearDefenceBonus();
        return (int) Math.floor(effectiveDefenceLevel * (equipmentRangeBonus + 64));
    }
}
