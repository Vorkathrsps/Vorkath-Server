package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;

import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
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

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.stream.Stream;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.RANGED;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 */
public class RangeAccuracy {

    byte[] seed = new byte[16];
    SecureRandom random = new SecureRandom(seed);

    public boolean doesHit(final Entity attacker, final Entity defender, CombatType style) {
        return successful(attacker, defender, style);//doesHit(entity, enemy, style, 1);
    }

    private boolean successful(final Entity attacker, final Entity defender, CombatType style) {
        final int attackBonus = getAttackRoll(attacker, defender, style);
        final int defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;

        random.nextBytes(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
        } else {
            successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
        }

        double selectedChance = random.nextDouble();

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }
    private double getPrayerAttackBonus(final Entity attacker) {
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

    private double getPrayerDefenseBonus(final Entity defender) {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(defender, RIGOUR)) {
            prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    private int getEffectiveDefence(final Entity defender) {
        FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel(defender) * getPrayerDefenseBonus(defender));

        switch (fightStyle) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        return effectiveLevel;
    }

    private int getEffectiveRanged(final Entity attacker, final Entity defender, CombatType style) {
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getRangeLevel(attacker) * getPrayerAttackBonus(attacker));
        double specialMultiplier = 1;

        if (attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            if (player.getCombatSpecial() != null) {
                specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
            }
        }

        if (fightStyle == FightStyle.ACCURATE) {
            effectiveLevel = (int) Math.floor(effectiveLevel + 3);
        }

        effectiveLevel = (int) Math.floor(effectiveLevel + 8);

        if(attacker.isPlayer()) { //additional bonuses here
            if (style.equals(RANGED)) {
                if ((FormulaUtils.hasBowOfFaerdhenin((Player) attacker))) {
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRYSTAL_HELM)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.05D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRYSTAL_BODY)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.CRYSTAL_LEGS)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.10D);
                    }
                }
                if (defender instanceof NPC) {
                    if (defender.isNpc() && defender.getAsNpc().id() == NpcIdentifiers.REVENANT_CYCLOPS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DEMON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DRAGON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_GOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HELLHOUND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DARK_BEAST || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HOBGOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_KNIGHT || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_PYREFIEND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_MALEDICTUS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP) {
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.2D);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                        }
                    }
                    if (defender.isNpc() && WildernessArea.inWilderness(attacker.tile())) {
                        if (weapon != null && FormulaUtils.hasRangedWildernessWeapon(attacker.getAsPlayer())) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.5D);
                        }
                    }
                }
                if (attacker.getAsPlayer().getEquipment().contains(DRAGON_HUNTER_CROSSBOW)) {
                    if (defender instanceof NPC && FormulaUtils.isDragon(defender)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.25D);
                    } else {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.30D);
                    }
                }
                if (FormulaUtils.regularVoidEquipmentBaseRanged((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.10D);
                }

                if (FormulaUtils.eliteVoidEquipmentRanged((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.125D);
                }

                if (attacker.isPlayer() && attacker.getAsPlayer().isSpecialActivated()) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * specialMultiplier);
                }

                double bonus = 1;
                Player player = (Player) attacker;
                if (weapon != null) {
                    if (Stream.of(TWISTED_BOW).anyMatch(w -> w == weapon.getId())) {

                        double magicLevel = 1;

                        if (attacker.isPlayer()) {
                            if (defender instanceof NPC n) {
                                if (n.getCombatInfo() != null && n.getCombatInfo().stats != null)
                                    magicLevel = n.getCombatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : n.getCombatInfo().stats.magic > 250D ? 250D : n.getCombatInfo().stats.magic;
                            } else {
                                magicLevel = defender.getAsPlayer().getSkills().getMaxLevel(Skills.MAGIC);
                            }

                            bonus += 140 + (((10 * 3 * magicLevel) / 10) - 10) - ((Math.floor(3 * magicLevel / 10 - 100)) * 2);
                            bonus = Math.floor(bonus / 100);
                            if (bonus > 2.4D)
                                bonus = (int) 2.4;
                        }
                        if (attacker.isPlayer() && defender.isNpc()) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * bonus);
                        }
                    }
                }
            }
        }

        return effectiveLevel;
    }

    private int getRangeLevel(final Entity attacker) {
        int rangeLevel = 1;
        if (attacker instanceof NPC npc) {
            if (npc.getCombatInfo() != null && npc.getCombatInfo().stats != null)
                rangeLevel = npc.getCombatInfo().stats.ranged;
        } else {
            rangeLevel = attacker.getSkills().level(Skills.RANGED);
        }
        return rangeLevel;
    }

    private int getGearAttackBonus(final Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == RANGED) {
            bonus = attackerBonus.range;
        }
        return bonus;
    }

    private int getGearDefenceBonus(final Entity defender, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (style == RANGED) {
            bonus = attackerBonus.rangedef;
        }
        return bonus;
    }

    private int getAttackRoll(final Entity attacker, final Entity defender, CombatType style) {
        int effectiveRangeLevel = (int) Math.floor(getEffectiveRanged(attacker, defender, style));
        int equipmentRangeBonus = getGearAttackBonus(attacker, style);
        return (int) Math.floor(effectiveRangeLevel * (equipmentRangeBonus + 64));
    }

    private int getDefenceRoll(Entity defender, CombatType style) {
        int effectiveDefenceLevel = (int) Math.floor(getEffectiveDefence(defender));
        int equipmentRangeBonus = getGearDefenceBonus(defender, style);
        return (int) Math.floor(effectiveDefenceLevel * (equipmentRangeBonus + 64));
    }
}
