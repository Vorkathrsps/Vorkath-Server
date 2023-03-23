package com.aelous.model.entity.combat.formula.accuracy;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;

import java.security.SecureRandom;
import java.text.DecimalFormat;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.AUGURY;
import static com.aelous.utility.ItemIdentifiers.SALVE_AMULET_E;

/**
 * @Author Origin
 */
public class MagicAccuracy {

    public static final SecureRandom srand = new SecureRandom();

    public static boolean doesHit(Entity attacker, Entity defender, CombatType style) {
        return successful(attacker, defender, style);
    }

    public static boolean successful(Entity attacker, Entity defender, CombatType style) {
        int attackBonus = getAttackRoll(attacker, defender,style);
        int defenceBonus = getDefenceRoll(defender, style);
        double successfulRoll;

        byte[] seed = new byte[16];
        new SecureRandom().nextBytes(seed);
        SecureRandom random = new SecureRandom(seed);

        if (attackBonus > defenceBonus) {
            successfulRoll = (int) 1D - ((defenceBonus + 2D) / (2D * (attackBonus + 1D)));
        } else {
            successfulRoll = attackBonus / (2D * (defenceBonus + 1D));
        }

        double selectedChance = random.nextDouble();

        System.out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

        return successfulRoll > selectedChance;
    }

    public static int getEquipmentBonusAttacker(Entity attacker, CombatType style) {
        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
        int bonus = 0;
        if (attacker instanceof Player) {
            if (!WildernessArea.inWild((Player) attacker) && ((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                bonus = attackerBonus.mage += Math.min(attackerBonus.mage * 3, attackerBonus.mage * attackerBonus.mage);
            } else {
                bonus = attackerBonus.mage;
            }
        } else if (attacker instanceof NPC) {
            bonus = attacker.getAsNpc().combatInfo().bonuses.magic;
        }
        return bonus;
    }

    public static int getEquipmentBonusDefender(Entity defender, CombatType style) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        return defender instanceof NPC ? defender.getAsNpc().combatInfo().bonuses.magicdefence : defenderBonus.magedef;
    }

    public static int getDefenceLevelDefender(Entity defender, FightStyle style) {
        int effectiveLevel = defender instanceof NPC ? ((NPC) defender).combatInfo().stats.defence : (int) Math.floor(defender.getSkills().level(Skills.DEFENCE) * getPrayerBonusDefender(defender));
        switch (style) {
            case DEFENSIVE -> effectiveLevel = (int) Math.floor(effectiveLevel + 3);
            case CONTROLLED -> effectiveLevel = (int) Math.floor(effectiveLevel + 1);
        }
        effectiveLevel = (int) Math.floor(effectiveLevel + 8);
        return effectiveLevel;
    }

    public static int getMagicLevelAttacker(Entity attacker) {
        return attacker instanceof NPC && attacker.getAsNpc().combatInfo() != null ? attacker.getAsNpc().combatInfo().stats.magic : attacker.getSkills().level(Skills.MAGIC);
    }

    public static int getMagicLevelDefender(Entity defender) {
        return defender instanceof NPC ? defender.getAsNpc().combatInfo().stats.magic : defender.getSkills().level(Skills.MAGIC);
    }

    public static double getPrayerBonus(Entity attacker, CombatType style) {
        double prayerBonus = 1D;
        if (style == CombatType.MAGIC) {
            if (Prayers.usingPrayer(attacker, MYSTIC_WILL))
                prayerBonus *= 1.05D; // 5% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_LORE))
                prayerBonus *= 1.10D; // 10% magic level boost
            else if (Prayers.usingPrayer(attacker, MYSTIC_MIGHT))
                prayerBonus *= 1.15D; // 15% magic level boost
            else if (Prayers.usingPrayer(attacker, AUGURY))
                prayerBonus *= 1.25D; // 25% magic level boost
        }
        return prayerBonus;
    }

    public static double getPrayerBonusDefender(Entity defender) {
        double prayerBonus = 1D;
        if (Prayers.usingPrayer(defender, AUGURY))
            prayerBonus *= 1.25D; //
        return prayerBonus;
    }

    public static int getEffectiveLevelAttacker(Entity attacker, Entity defender, CombatType style) {
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        int effectiveLevel = (int) Math.floor(getMagicLevelAttacker(attacker) * getPrayerBonus(attacker, style));

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
            case ACCURATE -> effectiveLevel += 3;
            case CONTROLLED -> effectiveLevel += 1;
        }

        effectiveLevel += 8;

        effectiveLevel = (int) Math.floor(effectiveLevel);

        if (attacker.isPlayer()) {
            if (style == CombatType.MAGIC) {
                if (FormulaUtils.regularVoidEquipmentBaseMagic((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.45D);
                }
                if (FormulaUtils.eliteVoidEquipmentBaseMagic((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.70);
                }
                if (attacker.getAsPlayer().getSpellbook().equals(MagicSpellbook.ANCIENT) && FormulaUtils.hasZurielStaff((Player) attacker)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.10);
                }
                if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                    effectiveLevel = (int) Math.floor(effectiveLevel * 3);
                }
                if (defender.isNpc() && FormulaUtils.isUndead(attacker.getCombat().getTarget())) { //UNDEAD BONUSES
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.2D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
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
                        if (weapon != null && FormulaUtils.hasMagicWildernessWeapon(attacker.getAsPlayer())) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.5D);
                        }
                    }
                }
                if (task != null && Slayer.creatureMatches((Player) attacker, attacker.getAsNpc().id())) {
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.15D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.18D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.20D);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.25D);
                    }
                }
            }
            effectiveLevel = (int) Math.floor(effectiveLevel);
        }
        return effectiveLevel;
    }

    public static int getAttackRoll(Entity attacker, Entity defender, CombatType style) {
        int effectiveMagicLevel = (int) Math.floor(getEffectiveLevelAttacker(attacker, defender, style));
        int equipmentAttackBonus = getEquipmentBonusAttacker(attacker, style);
        return (int) Math.floor(effectiveMagicLevel * (equipmentAttackBonus + 64));
    }


    public static int getDefenceRoll(Entity defender, CombatType style) {
        int magicLevel = getMagicLevelDefender(defender);
        int magicDefence = getDefenceLevelDefender(defender, FightStyle.DEFENSIVE);
        int effectiveLevel = (int) Math.floor(((magicDefence * getPrayerBonusDefender(defender) * 0.3D) * 0.7D) + magicLevel);
        int equipmentDefenceBonus = getEquipmentBonusDefender(defender, style);
        return (int) Math.floor(effectiveLevel * (equipmentDefenceBonus + 64));
    }
}
