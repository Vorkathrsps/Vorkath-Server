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

import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

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
        final double attackBonus = getAttackRoll();
        final double defenceBonus = getDefenceRoll();
        double successfulRoll;

        random.nextBytes(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
        } else {
            successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
        }

        double selectedChance = random.nextFloat();

       // System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

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

    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());

    private double getEffectiveAttack() {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = getAttackLevel(attacker) * getPrayerAttackBonus(attacker);

        if (attacker instanceof Player a) {
            effectiveLevel = Math.floor(effectiveLevel);

            handler.triggerMeleeAccuracyModificationAttacker(a, combatType, this);

            float modification = modifier;

            switch (fightStyle) {
                case ACCURATE -> effectiveLevel += 3;
                case CONTROLLED -> effectiveLevel += 1;
            }

            if (modification > 0) {
                effectiveLevel *= modification;
            }
        }

        effectiveLevel += 8;

        if (attacker instanceof Player a) {
            if (a.getCombatSpecial() != null) {
                double specialMultiplier = a.getCombatSpecial().getAccuracyMultiplier();
                if (a.isSpecialActivated()) {
                    effectiveLevel *= specialMultiplier;
                }
            }
        }

        return Math.floor(effectiveLevel);
    }

    private int getAttackLevel(final Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo().stats != null ? attacker.getAsNpc().getCombatInfo().stats.attack : attacker.getSkills().level(Skills.ATTACK);
    }

    private int getDefenceLevel() {
        return defender instanceof NPC && defender.getAsNpc().getCombatInfo().stats != null ? defender.getAsNpc().getCombatInfo().stats.defence : defender.getSkills().level(Skills.DEFENCE);
    }

    @Getter
    @Setter
    public double percentage = 1;

    private int getGearDefenceBonus() {
        int bonus = 0;
        AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();

        if (defender instanceof NPC npc) {
            var npcBonuses = npc.getCombatInfo().bonuses;
            if (type == AttackType.STAB)
                bonus = npcBonuses.stabdefence;
            else if (type == AttackType.CRUSH)
                bonus = npcBonuses.crushdefence;
            else if (type == AttackType.SLASH)
                bonus = npcBonuses.slashdefence;
           // System.out.println("npc=" + bonus);
        } else {
            EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(this.defender, World.getWorld().equipmentInfo());
            if (type == AttackType.STAB)
                bonus = defenderBonus.stabdef;
            else if (type == AttackType.CRUSH)
                bonus = defenderBonus.crushdef;
            else if (type == AttackType.SLASH)
                bonus = defenderBonus.slashdef;
            //System.out.println("player=" + bonus);
        }

        //System.out.println(bonus);
        return bonus;
    }

    public int getGearAttackBonus() {
        int bonus = 0;
        if (attacker instanceof Player) {
            EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
            final AttackType type = attacker.getCombat().getFightType().getAttackType();
            if (type == AttackType.STAB)
                bonus = attackerBonus.stab;
            else if (type == AttackType.CRUSH)
                bonus = attackerBonus.crush;
            else if (type == AttackType.SLASH)
                bonus = attackerBonus.slash;
           // System.out.println("player=" + bonus);
        } else if (attacker instanceof NPC n) {
            bonus = n.getCombatInfo().getBonuses().getAttack();
           // System.out.println("npc=" + bonus);
        }


        return bonus;
    }

    public double getAttackRoll() {
        double effectiveLevel = getEffectiveAttack();
        double attackBonus = getGearAttackBonus();

        double roll = effectiveLevel * (attackBonus + 64);

        return Math.floor(roll);
    }

    public double getDefenceRoll() {
        double defenceLevel = getDefenceLevel();
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
