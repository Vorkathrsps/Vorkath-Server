package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.aelous.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;

/**
 * @Author Origin
 */
public class MeleeAccuracy {
    @Getter
    @Setter
    float modifier;
    @Getter
    @Setter
    Entity attacker, defender;
    CombatType combatType;
    byte[] seed = new byte[16];
    SecureRandom random = new SecureRandom(seed);

    public MeleeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
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
        double successfulRoll;

        random.nextBytes(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
        } else {
            successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
        }

        double selectedChance = random.nextFloat();

        //System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    private double getPrayerDefenseBonus(final Entity defender) {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(defender, THICK_SKIN))
            prayerBonus *= 1.05F; // 5% def level boost
        else if (Prayers.usingPrayer(defender, ROCK_SKIN))
            prayerBonus *= 1.10F; // 10% def level boost
        else if (Prayers.usingPrayer(defender, STEEL_SKIN))
            prayerBonus *= 1.15F; // 15% def level boost
        if (Prayers.usingPrayer(defender, CHIVALRY))
            prayerBonus *= 1.20F; // 20% def level boost
        else if (Prayers.usingPrayer(defender, PIETY))
            prayerBonus *= 1.25F; // 25% def level boost
        return prayerBonus;
    }

    private double getPrayerAttackBonus(final Entity attacker) {
        double prayerBonus = 1F;
        if (Prayers.usingPrayer(attacker, CLARITY_OF_THOUGHT))
            prayerBonus *= 1.05F; // 5% attack level boost
        else if (Prayers.usingPrayer(attacker, IMPROVED_REFLEXES))
            prayerBonus *= 1.10F; // 10% attack level boost
        else if (Prayers.usingPrayer(attacker, INCREDIBLE_REFLEXES))
            prayerBonus *= 1.15F; // 15% attack level boost
        else if (Prayers.usingPrayer(attacker, CHIVALRY))
            prayerBonus *= 1.15F; // 15% attack level boost
        else if (Prayers.usingPrayer(attacker, PIETY))
            prayerBonus *= 1.20F; // 20% attack level boost
        return prayerBonus;
    }


    private int getEffectiveDefence() {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).getCombatInfo().stats.defence : (int) Math.floor(getDefenceLevel() * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = effectiveLevel + 3;
            case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
        }

        effectiveLevel = effectiveLevel + 8;

        return effectiveLevel;
    }

    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    private int getEffectiveAttack() {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.floor(getAttackLevel(attacker) * getPrayerAttackBonus(attacker));

        if (attacker instanceof Player a)
            handler.triggerMeleeAccuracyModificationAttacker(a, combatType, this);

        float modification = modifier;

        switch (fightStyle) {
            case ACCURATE -> effectiveLevel = effectiveLevel + 3;
            case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
        }

        effectiveLevel = modification > 0 ? Math.floor(effectiveLevel * modification) : effectiveLevel;

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                double specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
                if (attacker.getAsPlayer().isSpecialActivated()) {
                    effectiveLevel *= specialMultiplier;
                }
            }
        }

        effectiveLevel = effectiveLevel + 8;

        effectiveLevel = (int) Math.floor(effectiveLevel);

        return (int) Math.floor(effectiveLevel);
    }

    private int getAttackLevel(final Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo().stats != null ? attacker.getAsNpc().getCombatInfo().stats.attack : attacker.getSkills().level(Skills.ATTACK);
    }

    private int getDefenceLevel() {
        return defender instanceof NPC && defender.getAsNpc().getCombatInfo().stats != null ? defender.getAsNpc().getCombatInfo().stats.defence : defender.getSkills().level(Skills.DEFENCE);
    }

    private int getGearDefenceBonus() {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        final AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();
        int bonus = 0;
        if (type == AttackType.STAB)
            bonus = defenderBonus.stabdef;
        else if (type == AttackType.CRUSH)
            bonus = defenderBonus.crushdef;
        else if (type == AttackType.SLASH)
            bonus = defenderBonus.slashdef;
        return bonus;
    }

    private int getGearAttackBonus() {
        final AttackType type = attacker.getCombat().getFightType().getAttackType();
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (type == AttackType.STAB)
            bonus = attackerBonus.stab;
        else if (type == AttackType.CRUSH)
            bonus = attackerBonus.crush;
        else if (type == AttackType.SLASH)
            bonus = attackerBonus.slash;
        return bonus;
    }

    private int getAttackRoll() {
        return (int) Math.floor(getEffectiveAttack() * (getGearAttackBonus() + 64));
    }

    private int getDefenceRoll() {
        //float modification = modifier;
        //var mod = modification > 0 ? modification : 1;
        ///System.out.println(mod);
        return (int) Math.floor(getEffectiveDefence() * (getGearDefenceBonus() + 64));
    }
}
