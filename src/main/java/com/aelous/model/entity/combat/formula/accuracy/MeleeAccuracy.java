package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.MELEE;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 */
public class MeleeAccuracy {
    byte[] seed = new byte[16];
    SecureRandom random = new SecureRandom(seed);

    public boolean doesHit(final Entity attacker, final Entity defender, CombatType style) {
        return successful(attacker, defender, style);
    }

    private boolean successful(final Entity attacker, final Entity defender, CombatType style) {
        final int attackBonus = getAttackRoll(attacker, defender, style);
        final int defenceBonus = getDefenceRoll(attacker, defender);
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


    private int getEffectiveDefence(final Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).getCombatInfo().stats.defence : (int) Math.floor(getDefenceLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = effectiveLevel + 3;
            case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
        }

        effectiveLevel = effectiveLevel + 8;

        return effectiveLevel;
    }

    private int getEffectiveAttack(final Entity attacker, final Entity defender, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.floor(getAttackLevel(attacker) * getPrayerAttackBonus(attacker));

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                double specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
                if (attacker.getAsPlayer().isSpecialActivated()) {
                    effectiveLevel *= specialMultiplier;
                }
            }
        }

        switch (fightStyle) {
            case ACCURATE -> effectiveLevel = effectiveLevel + 3;
            case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
        }

        effectiveLevel = effectiveLevel + 8;

        effectiveLevel = (int) Math.floor(effectiveLevel);

        if (attacker.isPlayer()) {
            if (style.equals(MELEE)) {
                if (FormulaUtils.regularVoidEquipmentBaseMelee((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1F);
                }
                if (FormulaUtils.eliteVoidEquipmentMelee((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.125F);
                }
                if (FormulaUtils.obbyArmour(attacker.getAsPlayer()) && FormulaUtils.hasObbyWeapon(attacker.getAsPlayer())) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.1F);
                }
                if (defender instanceof NPC) {
                    if (defender.isNpc() && defender.getAsNpc().id() == NpcIdentifiers.REVENANT_CYCLOPS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DEMON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DRAGON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_GOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HELLHOUND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DARK_BEAST || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HOBGOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_KNIGHT || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_PYREFIEND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_MALEDICTUS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP) {
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.2F);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.15F);
                        }
                    }
                    if (defender.isNpc() && WildernessArea.inWilderness(attacker.tile())) {
                        if (weapon != null && FormulaUtils.hasMeleeWildernessWeapon(attacker.getAsPlayer())) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.5F);
                        }
                    }
                }
            }
        }
        return (int) Math.floor(effectiveLevel);
    }

    private int getAttackLevel(final Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo().stats != null ? attacker.getAsNpc().getCombatInfo().stats.attack : attacker.getSkills().level(Skills.ATTACK);
    }

    private int getDefenceLevel(final Entity defender) {
        return defender instanceof NPC && defender.getAsNpc().getCombatInfo().stats != null ? defender.getAsNpc().getCombatInfo().stats.defence : defender.getSkills().level(Skills.DEFENCE);
    }

    private int getGearDefenceBonus(final Entity defender) {
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

    private int getGearAttackBonus(final Entity attacker) {
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

    private int getAttackRoll(final Entity attacker, final Entity defender, CombatType style) {
        return (int) Math.floor(getEffectiveAttack(attacker, defender, style) * (getGearAttackBonus(attacker) + 64));
    }

    private int getDefenceRoll(final Entity attacker, final Entity defender) {
        if ((attacker.isPlayer() && attacker.getAsPlayer().getEquipment().contains(VESTAS_BLIGHTED_LONGSWORD) && attacker.isSpecialActivated())) {
            return (int) Math.floor((getEffectiveDefence(defender) * (getGearDefenceBonus(defender) + 64)) * 0.80F);
        }
        return (int) Math.floor(getEffectiveDefence(defender) * (getGearDefenceBonus(defender) + 64));
    }
}
