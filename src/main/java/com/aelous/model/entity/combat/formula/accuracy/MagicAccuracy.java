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
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

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

    byte[] seed = new byte[16];
    SecureRandom random = new SecureRandom(seed);

    public boolean doesHit(final Entity attacker, final Entity defender, CombatType style) {
        return successful(attacker, defender, style);
    }

    private boolean successful(final Entity attacker, final Entity defender, CombatType combatType) {
        final int attackBonus = getAttackRoll(attacker, defender, combatType);
        final int defenceBonus = getDefenceRoll(defender);
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

    private int getEquipmentBonusAttacker(final Entity attacker) {
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

    private int getEquipmentBonusDefender(final Entity defender) {
        EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
        return defender instanceof NPC ? defender.getAsNpc().getCombatInfo().bonuses.magicdefence : defenderBonus.magedef;
    }

    private int getEffectiveDefenceDefender(final Entity defender) {
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

    private double getPrayerBonus(final Entity attacker, CombatType style) {
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

    private int getEffectiveLevelAttacker(final Entity attacker, final Entity defender, CombatType style) {
        final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
        double effectiveLevel = Math.floor(getMagicLevelAttacker(attacker) * getPrayerBonus(attacker, style));

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


        effectiveLevel = (int) Math.floor(effectiveLevel);

        if (defender != null) {
            if (attacker.isPlayer() && defender.isPlayer()) {
                if (attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.BRIMSTONE_RING)) {
                    if (Utils.securedRandomChance(0.25F)) {
                        effectiveLevel *= 1.10F;
                        attacker.message(Color.RED.wrap("Your attack ignored 10% of your opponent's magic defence."));
                    }
                }
            }

            if (attacker.isPlayer()) {
                if (style == CombatType.MAGIC) {
                    if (FormulaUtils.regularVoidEquipmentBaseMagic((Player) attacker)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.45F);
                    }
                    if (FormulaUtils.eliteVoidEquipmentBaseMagic((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic((Player) attacker)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.70F);
                    }
                    if (attacker.getAsPlayer().getSpellbook().equals(MagicSpellbook.ANCIENT) && FormulaUtils.hasZurielStaff((Player) attacker)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.10F);
                    }
                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TUMEKENS_SHADOW)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 3);
                    }
                    if (defender.isNpc() && FormulaUtils.isUndead(attacker.getCombat().getTarget())) { //UNDEAD BONUSES
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.2F);
                        }
                        if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                            effectiveLevel = (int) Math.floor(effectiveLevel * 1.15F);
                        }
                    }
                    if (defender instanceof NPC) {
                        if (defender.isNpc() && defender.getAsNpc().id() == NpcIdentifiers.REVENANT_CYCLOPS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DEMON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DRAGON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_GOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HELLHOUND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DARK_BEAST || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HOBGOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_KNIGHT || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_PYREFIEND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_MALEDICTUS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP) {
                            if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                                effectiveLevel = (int) Math.floor(effectiveLevel * 1.2F);
                            }
                            if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                                effectiveLevel = (int) Math.floor(effectiveLevel * 1.15F);
                            }
                            if (defender.isNpc() && WildernessArea.inWilderness(attacker.tile())) {
                                if (weapon != null && FormulaUtils.hasMagicWildernessWeapon(attacker.getAsPlayer())) {
                                    effectiveLevel = (int) Math.floor(effectiveLevel * 1.5F);
                                }
                            }
                            if (task != null) {
                                if (Slayer.creatureMatches((Player) attacker, defender.getAsNpc().id())) {
                                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.15F);
                                    }
                                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.18F);
                                    }
                                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.20F);
                                    }
                                    if (((Player) attacker).getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.25F);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        effectiveLevel += 9;
        return (int) Math.floor(effectiveLevel);
    }

    private int getAttackRoll(final Entity attacker, final Entity defender, CombatType style) {
        int getEffectiveMagicAttacker = (int) Math.floor(getEffectiveLevelAttacker(attacker, defender, style));
        int equipmentAttackBonus = getEquipmentBonusAttacker(attacker);
        return (int) Math.floor(getEffectiveMagicAttacker * (equipmentAttackBonus + 64));
    }


    private int getDefenceRoll(final Entity defender) {
        int equipmentDefenceBonus = getEquipmentBonusDefender(defender);
        return (int) Math.floor((getEffectiveDefenceDefender(defender) * (equipmentDefenceBonus + 64)) * 0.825);
    }
}
