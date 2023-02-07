package com.aelous.model.entity.combat.magic.impl;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jak
 * @date Jan 7 2018
 */
public class CombinationRunes {
    
    private static final int LAVA_RUNE = 4699;
    private static final int MUD_RUNE = 4698;
    private static final int MIST_RUNE = 4695;
    private static final int STEAM_RUNE = 4694;
    private static final int DUST_RUNE = 4696;
    private static final int SMOKE_RUNE = 4697;

    private static final int AIR_RUNE = 556;
    private static final int FIRE_RUNE = 554;
    private static final int WATER_RUNE = 555;
    private static final int EARTH_RUNE = 557;

    public static class ComboRune {

        public ComboRune(int combo, ArrayList<Integer> elementals) {
            this.combo = combo;
            this.elementals = elementals;
        }

        private final int combo;
        private final ArrayList<Integer> elementals;

        public int consumes(List<Item> runes) {
            int matches = 0;
            for (int id : elements()) {
                if (runes.stream().anyMatch(r -> r.getId() == id)) {
                    matches++;
                }
            }
            return Math.max(2, matches);
        }

        public ArrayList<Integer> elements() {
            return elementals;
        }

        public Item toItem(int amount) {
            return new Item(combo, amount);
        }

        public int id() {
            return combo;
        }
    }

    public static final HashMap<Integer, ComboRune> COMBO_RUNES = new HashMap<>();

    public static ComboRune get(int comboId) {
        return COMBO_RUNES.get(comboId);
    }

    static {
        COMBO_RUNES.put(LAVA_RUNE, new ComboRune(LAVA_RUNE, new ArrayList<>(List.of(FIRE_RUNE, EARTH_RUNE))));
        COMBO_RUNES.put(MUD_RUNE, new ComboRune(MUD_RUNE, new ArrayList<>(List.of(EARTH_RUNE, WATER_RUNE))));
        COMBO_RUNES.put(MIST_RUNE, new ComboRune(MIST_RUNE, new ArrayList<>(List.of(WATER_RUNE, AIR_RUNE))));
        COMBO_RUNES.put(STEAM_RUNE, new ComboRune(STEAM_RUNE, new ArrayList<>(List.of(FIRE_RUNE, WATER_RUNE))));
        COMBO_RUNES.put(DUST_RUNE, new ComboRune(DUST_RUNE, new ArrayList<>(List.of(EARTH_RUNE, AIR_RUNE))));
        COMBO_RUNES.put(SMOKE_RUNE, new ComboRune(SMOKE_RUNE, new ArrayList<>(List.of(FIRE_RUNE, AIR_RUNE))));
    }

   /* public static int numberOfAlternative(int id, Item available) {
        switch (id) {
            case ItemIdentifiers.FIRE_RUNE -> available.getId() in intArrayOf(LAVA_RUNE, STEAM_RUNE)) return available.getAmount();
            case ItemIdentifiers.AIR_RUNE -> available.getId() in intArrayOf(MIST_RUNE, DUST_RUNE, SMOKE_RUNE)) return available.getAmount();
            case ItemIdentifiers.EARTH_RUNE -> available.getId() in intArrayOf(LAVA_RUNE, MUD_RUNE, DUST_RUNE)) return available.getAmount();
            case ItemIdentifiers.WATER_RUNE -> available.getId() in intArrayOf(MUD_RUNE, STEAM_RUNE)) return available.getAmount();
        }
        return 0;
    }
*/
    public static int numberOfAlternative(Player player, int id) {
        return switch (id) {
            case ItemIdentifiers.FIRE_RUNE -> player.inventory().count(LAVA_RUNE, STEAM_RUNE);
            case ItemIdentifiers.AIR_RUNE -> player.inventory().count(MIST_RUNE, DUST_RUNE, SMOKE_RUNE);
            case ItemIdentifiers.EARTH_RUNE -> player.inventory().count(LAVA_RUNE, MUD_RUNE, DUST_RUNE);
            case ItemIdentifiers.WATER_RUNE -> player.inventory().count(MUD_RUNE, STEAM_RUNE);
            default -> 0;
        };
    }

    // Note: runepouch boolean will need to be changed to int/enum if >2 sources become available, currently only inventory + RP (staffs don't count - they're unlimited)
    public static class ComboRuneSource {

        private final int id, amt, slot;
        private final boolean runepouch;

        public ComboRuneSource(int id, int amt, int slot, boolean runepouch) {
            this.id = id;
            this.amt = amt;
            this.slot = slot;
            this.runepouch = runepouch;
        }

        @Override
        public String toString() {
            return String.format("combo:%d x%d at %d, rp:%s", id, amt, slot, runepouch);
        }
    }

    public static class ComboRuneDeduction {
        private final ArrayList<Item> remainingRequired;
        private final ArrayList<ComboRuneSource> usedComboRunes;

        public ComboRuneDeduction(ArrayList<Item> remainingRequired, ArrayList<ComboRuneSource> usedComboRunes) {
            this.remainingRequired = remainingRequired;
            this.usedComboRunes = usedComboRunes;
        }
    }

    /*public static ComboRuneDeduction findComboRuneUses(Player player, ArrayList<Item> immutableReqs) {

        // Container with the runes needed to cast a spell.
        var requiredContainer = new ItemContainer(immutableReqs.copyOf(), ItemContainer.StackPolicy.STANDARD);

        // Combo runes on our person, from the inventory
        var available = new ItemContainer(player.world(), player.inventory().filter { i -> i != null && (i.id() in 4694 .. 4699) }.toTypedArray(), ItemContainer.Type.REGULAR)

        var usedRunes = mutableListOf<ComboRuneSource>()

        findCombosInContainer(requiredContainer, available, usedRunes)

        // Make sure we're carrying the rune pouch before taking into consideration its contents.
        if (player.inventory().contains(RUNE_POUCH)) {
            findCombosInContainer(requiredContainer, ItemContainer(player.world(), RunePouch.allRunesOf(player).filter { r -> r != null }.toTypedArray(), ItemContainer.Type.REGULAR), usedRunes, true)
        }

        /*val left = Arrays.toString(requiredContainer.items().filter { i -> i != null }.map { i -> i.name(player.world())+"x"+i.amount() }.toTypedArray())
        val used = Arrays.toString(usedRunes.map { r -> Item(r.id).name(player.world())+"x"+r.amt }.toTypedArray())
        val avail = Arrays.toString(available.items().filter { i -> i != null }.map { i -> i.name(player.world())+"x"+i.amount() }.toTypedArray())
        player.message("required: ${Arrays.toString(immutableReqs.map { i -> i.name(player.world())+"x"+i.amount() }.toTypedArray())}")
        player.message("left after combos: $left")
        player.message("combos used: $used".red())
        player.message("remaining available: $avail")*/
        //return ComboRuneDeduction(requiredContainer.items(), usedRunes.toTypedArray())
    //}

    /*private fun findCombosInContainer(requiredContainer: ItemContainer, available: ItemContainer, usedRunes: MutableList<ComboRuneSource>, runepouch: Boolean = false) {

        val comboIterator = available.items().iterator()

        // Loop all combo runes available to us, use their pairs to reduce required runes as we go
        lookup@ while (comboIterator.hasNext()) {
            val comboRune = comboIterator.next()
            // we have a combo rune, let's see if it can remove 1 or even 2 required runes
            val comboSet = combo(comboRune) ?: continue

                // find a combo match AND another required match of the same combo
                val ele1 = comboSet.elementals[0]
            val ele2 = comboSet.elementals[1]

            // There will only be 1 match of the elemental rune of THE CURRENT COMBO RUNE BEING CHECKED
            val matchEle1 = requiredContainer.items().toList().stream().filter { r2 -> r2 != null && r2.id() == ele1 }.findFirst()
            if (matchEle1.isPresent) {
                val matched2nd = requiredContainer.items().toList().stream().filter { r2 -> r2 != null && r2.id() == ele2 }.findFirst()
                if (matched2nd.isPresent) {
                    val amt = Math.min(matchEle1.get().amount(), matched2nd.get().amount())
                    requiredContainer.remove(Item(matchEle1.get().id(), amt), false)
                    requiredContainer.remove(Item(matched2nd.get().id(), amt), false)
                    usedRunes.add(ComboRuneSource(comboSet.combo, amt, available.slotOf(comboSet.combo), runepouch))
                    available.remove(Item(comboSet.combo, amt), false)
                    // Met all requirements (unlikely to happen but w.e)
                    if (requiredContainer.size() == 0)
                        break@lookup
                    // Next combo rune check.
                    continue
                }
            }
            val matchEle2 = requiredContainer.items().toList().stream().filter { r2 -> r2 != null && r2.id() == ele2 }.findFirst()
            if (matchEle2.isPresent) {
                val matched2nd = requiredContainer.items().toList().stream().filter { r2 -> r2 != null && r2.id() == ele1 }.findFirst()
                if (matched2nd.isPresent) {
                    val amt = Math.min(matchEle1.get().amount(), matched2nd.get().amount())
                    requiredContainer.remove(Item(matchEle2.get().id(), amt), false)
                    requiredContainer.remove(Item(matched2nd.get().id(), amt), false)
                    usedRunes.add(ComboRuneSource(comboSet.combo, amt, available.slotOf(comboSet.combo), runepouch))
                    available.remove(Item(comboSet.combo, amt), false)
                    if (requiredContainer.size() == 0)
                        break@lookup
                    continue
                }
            }
        }
    }*/

    private static ComboRune combo(Item i) {
        return COMBO_RUNES.get(i.getId());
    }
}
