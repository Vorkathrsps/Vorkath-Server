package com.aelous.model.entity.combat.magic.spells;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.impl.CombinationRunes;
import com.aelous.model.entity.combat.magic.impl.PlayerMagicStaff;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Debugs;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * A parent class represented by any generic spell able to be cast by an
 * {@link Entity}.
 *
 * @author lare96
 */
public abstract class Spell {

    /**
     * Determines if this spell is able to be cast by the argued {@link Player}.
     * We do not include {@link NPC}s here since no checks need to be made for
     * them when they cast a spell.
     *
     * @param player the player casting the spell.
     * @return <code>true</code> if the spell can be cast by the player,
     * <code>false</code> otherwise.
     */
    public boolean canCast(Player player, Entity target, boolean delete) {
        try {
            // We first check the level required.
            boolean canCast = player.getSkills().level(Skills.MAGIC) < levelRequired();
            if (canCast) {
                if (player.getSkills().level(Skills.MAGIC) < levelRequired()) {
                    player.message("You need a Magic level of " + levelRequired() + " to cast this spell.");
                    return false;
                }

                boolean autoCastSelected = player.getAttribOr(AttributeKey.AUTOCAST_SELECTED, false);
                boolean hasActivePoweredStaffSpell = player.getCombat().getPoweredStaffSpell() != null;
                var setPoweredStaffSpell = CombatSpells.getCombatSpell(spellId());
                //Reset auto casting if we were autocasting

                if (hasActivePoweredStaffSpell) {
                    player.getCombat().setPoweredStaffSpell(setPoweredStaffSpell);
                } else {
                    player.getCombat().reset();
                    return false;
                }

                if (autoCastSelected) {
                    Autocasting.setAutocast(player, null);
                } else {
                    player.getCombat().reset();
                    return false;
                }
            }

                if (target != null && target.isPlayer()) {
                    if (name().equalsIgnoreCase("Ice burst") || name().equalsIgnoreCase("Ice blitz") || name().equalsIgnoreCase("Ice barrage") || name().equalsIgnoreCase("Bind") || name().equalsIgnoreCase("Snare") || name().equalsIgnoreCase("Entangle")) {
                        if (target.stunned()) {
                            player.message("That player is currently immune to this spell.");
                            return false;
                        }
                    }
                }

                CombatSpell combatSpell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell() != null ? player.getCombat().getAutoCastSpell() : player.getCombat().getPoweredStaffSpell() != null ? player.getCombat().getPoweredStaffSpell() : null;
                boolean ignoreBookCheck =
                    combatSpell == CombatSpells.ELDRITCH_NIGHTMARE_STAFF.getSpell() ||
                    combatSpell == CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell() ||
                        combatSpell == CombatSpells.TRIDENT_OF_THE_SEAS.getSpell() ||
                        combatSpell == CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell() ||
                        combatSpell == CombatSpells.SANGUINESTI_STAFF.getSpell() ||
                        combatSpell == CombatSpells.TUMEKENS_SHADOW.getSpell() ||
                        combatSpell == CombatSpells.ACCURSED_SCEPTRE.getSpell();

                // Secondly we check if they have proper magic spellbook
                // If not, reset all magic attributes such as current spell
                // Aswell as autocast spell
                final CombatSpell finalCombatSpell = combatSpell;
                if (combatSpell != null && !ignoreBookCheck && Arrays.stream(player.getCombat().AUTOCAST_SPELLS).noneMatch(combatSpell1 -> combatSpell1 == finalCombatSpell)) {
                    if (!player.getSpellbook().equals(combatSpell.spellbook())) {
                        Autocasting.setAutocast(player, null);
                        player.getCombat().setPoweredStaffSpell(null);
                        Debugs.CMB.debug(player, "bad book", target, true);
                        player.message("This spell belongs to a different spellbook.");
                        return false;
                    }
                }

                // Then we check the items required.
                final var itemsRequired = itemsRequired(player);
                final var equipmentRequired = equipmentRequired(player);

                if (!itemsRequired.isEmpty()) {
                    // Suppress the runes based on the staff, we then use the new array
                    // of items that don't include suppressed runes.
                    List<Item> items = PlayerMagicStaff.suppressRunes(player, itemsRequired);

                    Map<Integer, Integer> runeCosts = new HashMap<>();
                    items.forEach(rune -> runeCosts.put(rune.getId(), rune.getAmount()));
                    HashMap<Integer, Integer> comboRunes = new HashMap<>();
                    CombinationRunes.COMBO_RUNES.keySet().forEach(r -> {
                        if (player.getRunePouch().containsId(r)) {
                            comboRunes.put(r, player.getRunePouch().getRuneAmount(r));
                        } else if (player.inventory().contains(r)) {
                            comboRunes.put(r, player.inventory().count(r));
                        }
                    });

                    // Check combo runes
                    if (!comboRunes.isEmpty()) {
                        comboRunes.forEach((k, v) -> {
                            CombinationRunes.ComboRune comboRune = CombinationRunes.get(k);
                            comboRune.elements().forEach(element -> {
                                int remainingCost = runeCosts.getOrDefault(element, 0);
                                if (remainingCost > 0) {
                                    runeCosts.put(element, remainingCost - 1);
                                }
                            });
                        });
                    }

                    //First check rune pouch
                    for (Item item : items) {
                        final int runeId = item.getId();
                        if (player.getRunePouch().containsId(runeId) && (player.inventory().contains(RUNE_POUCH))) {
                            runeCosts.put(runeId, Math.max(0, runeCosts.get(runeId) - player.getRunePouch().getRuneAmount(runeId)));
                        } else {
                            runeCosts.put(runeId, Math.max(0, runeCosts.get(runeId) - player.inventory().count(runeId)));
                        }
                    }

                    if (delete && player.getEquipment().contains(ItemIdentifiers.KODAI_WAND)) {
                        delete = World.getWorld().random(100) > 15;
                    }

                    // Now check if we have all of the runes.
                    if (runeCosts.values().stream().mapToInt(cost -> cost).sum() > 0) {
                        // We don't, so we can't cast.
                        player.message("You do not have the required runes to cast this spell.");
                        return false;
                    }

                    // Finally, we check the equipment required.
                    if (!equipmentRequired.isEmpty()) {
                        if (!player.getEquipment().containsAny(equipmentRequired)) {
                            player.message("You do not have the required equipment to cast this spell.");
                            return false;
                        }
                    }

                    //Check staff of the dead and don't delete runes at a rate of 1/8
                    if (player.getEquipment().hasAt(EquipSlot.WEAPON, STAFF_OF_THE_DEAD) || player.getEquipment().hasAt(EquipSlot.WEAPON, TOXIC_STAFF_OF_THE_DEAD) || player.getEquipment().hasAt(EquipSlot.WEAPON, STAFF_OF_LIGHT)) {
                        if (Utils.securedRandomChance(0.125F)) {
                            player.message(Color.RED.wrap("Your staff negated your runes for this cast."));
                            delete = false;
                        }
                    }

                    // We've made it through the checks, so we have the items and can
                    // remove them now.
                    if (delete) {
                        return deleteRequiredRunes(player, comboRunes);
                    }
                }
        } catch (
            Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean canCastOn(Player player, Player target) {
        return true;
    }

    public abstract String name();

    public abstract int spellId();

    /**
     * The level required to cast this spell.
     *
     * @return the level required to cast this spell.
     */
    public abstract int levelRequired();

    /**
     * The base experience given when this spell is cast.
     *
     * @return the base experience given when this spell is cast.
     */
    public abstract int baseExperience();

    /**
     * The items required to cast this spell.
     *
     * @param player the player's inventory to check for these items.
     * @return the items required to cast this spell, or <code>null</code> if
     * there are no items required.
     */
    public abstract List<Item> itemsRequired(Player player);

    /**
     * The equipment required to cast this spell.
     *
     * @param player the player's equipment to check for these items.
     * @return the equipment required to cast this spell, or <code>null</code>
     * if there is no equipment required.
     */
    public abstract List<Item> equipmentRequired(Player player);

    /**
     * The equipment required to cast this spell.
     *
     * @return the equipment required to cast this spell, or <code>null</code>
     * if there is no equipment required.
     */
    public boolean hasToContainAllEquipment() {
        return true;
    }

    public boolean deleteRunes() {
        return true;
    }

    public boolean deleteRequiredRunes(Player player, HashMap<Integer, Integer> comboRunes) {
        if (!deleteRunes()) {
            return true;
        }

        final var itemsRequired = itemsRequired(player);

        // Then we check the items required.
        if (!itemsRequired.isEmpty()) {
            // Suppress the runes based on the staff, we then use the new array
            // of items that don't include suppressed runes.
            List<Item> items = PlayerMagicStaff.suppressRunes(player, itemsRequired);
            HashMap<Integer, Integer> runeCosts = new HashMap<>();
            items.forEach(rune -> runeCosts.put(rune.getId(), rune.getAmount()));
            boolean usingRunePouch = false;
            if (player.inventory().contains(RUNE_POUCH)) {
                usingRunePouch = true;
            }

            for (int id : comboRunes.keySet()) {
                CombinationRunes.ComboRune comboRune = CombinationRunes.get(id);
                int matches = 0;
                if (usingRunePouch && player.getRunePouch().containsId(comboRune.id())) {
                    for (int r : comboRune.elements()) {
                        if (runeCosts.getOrDefault(r, 0) == 0)
                            continue;
                        if (items.stream().anyMatch(rune -> rune.getId() == r)) {
                            matches++;
                            runeCosts.put(r, Math.max(0, runeCosts.get(r) - 1));
                        }
                    }
                    if (matches > 0)
                        player.getRunePouch().remove(new Item(id));
                } else if (player.inventory().contains(id)) {
                    for (int r : comboRune.elements()) {
                        if (runeCosts.get(r) != null && runeCosts.get(r) == 0)
                            continue;
                        if (items.stream().anyMatch(rune -> rune.getId() == r)) {
                            matches++;
                            runeCosts.put(r, Math.max(0, runeCosts.get(r) - 1));
                        }
                    }
                    if (matches > 0)
                        player.inventory().remove(id);
                }
            }

            //First check rune pouch
            for (Item item : items) {
                final int runeId = item.getId();
                if (runeCosts.get(runeId) == 0)
                    continue;
                if (usingRunePouch && player.getRunePouch().containsId(runeId)) {
                    runeCosts.put(runeId, Math.max(0, runeCosts.get(runeId) - 1));
                    player.getRunePouch().remove(item);
                } else {
                    runeCosts.put(runeId, Math.max(0, runeCosts.get(runeId) - player.inventory().count(runeId)));
                    player.inventory().remove(item);
                }
            }

            return true;
        }
        return false;
    }

    /**
     * The method invoked when the spell is cast.
     *
     * @param cast   the entity casting the spell.
     * @param castOn the target of the spell.
     */
    public abstract void startCast(Entity cast, Entity castOn);
}
