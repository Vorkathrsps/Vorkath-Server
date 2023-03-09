package com.aelous.model.content.skill.impl.magic;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.World;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;

import java.util.HashMap;
import java.util.Map;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | March, 07, 2021, 00:05
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class BoltEnchantment extends PacketInteraction {

    private static final HashMap<Integer, Integer> cosmic = new HashMap<>() {
        {
            put(39012, 1);
            put(39020, 1);
            put(39028, 1);
            put(39036, 1);
            put(39044, 1);
            put(39052, 1);
            put(39060, 1);
            put(39068, 1);
            put(39076, 1);
            put(39084, 1);
        }
    };

    private static final HashMap<Integer, Integer> air = new HashMap<>() {
        {
            put(39013, 2);
            put(39045, 3);
        }
    };

    private static final HashMap<Integer, Integer> earth = new HashMap<>() {
        {
            put(39029, 2);
            put(39069, 10);
            put(39077, 15);
        }
    };

    private static final HashMap<Integer, Integer> water = new HashMap<>() {
        {
            put(39021, 1);
            put(39037, 2);
        }
    };

    private static final int saphire_mind_id = 39088;
    private static final int emerald_nature_id = 39090;
    private static final int ruby_blood_rune = 39092;
    private static final int diamond_law_rune = 39094;
    private static final int dragonstone_soul_id = 39096;
    private static final int onyx_death_id = 39098;

    private static final HashMap<Integer, Integer> fire = new HashMap<>() {
        {
            put(39053, 2);
            put(39061, 5);
            put(39085, 20);
        }
    };

    public enum Bolts {
        OPAL(39006, OPAL_BOLTS, OPAL_BOLTS_E, new Item[]{new Item(AIR_RUNE, 2), new Item(COSMIC_RUNE, 1)}, 4, 9),
        SAPPHIRE(39015, SAPPHIRE_BOLTS, SAPPHIRE_BOLTS_E, new Item[]{new Item(WATER_RUNE, 1), new Item(COSMIC_RUNE, 1), new Item(MIND_RUNE, 1)}, 7, 17),
        JADE(39023, JADE_BOLTS, JADE_BOLTS_E, new Item[]{new Item(EARTH_RUNE, 2), new Item(COSMIC_RUNE, 1)}, 14, 19),
        PEARL(39031, PEARL_BOLTS, PEARL_BOLTS_E, new Item[]{new Item(WATER_RUNE, 2), new Item(COSMIC_RUNE, 1)}, 24, 29),
        EMERALD(39039, EMERALD_BOLTS, EMERALD_BOLTS_E, new Item[]{new Item(AIR_RUNE, 5), new Item(COSMIC_RUNE, 1), new Item(NATURE_RUNE, 1)}, 27, 37),
        TOPAZ(39047, TOPAZ_BOLTS, TOPAZ_BOLTS_E, new Item[]{new Item(FIRE_RUNE, 2), new Item(COSMIC_RUNE, 1)}, 29, 33),
        RUBY(39055, RUBY_BOLTS, RUBY_BOLTS_E, new Item[]{new Item(FIRE_RUNE, 5), new Item(BLOOD_RUNE, 1), new Item(COSMIC_RUNE, 1)}, 49, 59),
        DIAMOND(39063, DIAMOND_BOLTS, DIAMOND_BOLTS_E, new Item[]{new Item(EARTH_RUNE, 10), new Item(COSMIC_RUNE, 1), new Item(LAW_RUNE, 1)}, 57, 67),
        DRAGONSTONE(39071, DRAGONSTONE_BOLTS, DRAGONSTONE_BOLTS_E, new Item[]{new Item(EARTH_RUNE, 15), new Item(COSMIC_RUNE, 1), new Item(LAW_RUNE, 2)}, 68, 78),
        ONYX(39079, ONYX_BOLTS, ONYX_BOLTS_E, new Item[]{new Item(FIRE_RUNE, 20), new Item(COSMIC_RUNE, 1), new Item(DEATH_RUNE, 1)}, 87, 97);

        private final int buttonId;
        private final int boltsToEnchant;
        private final Item[] runes;
        private final int enchantedBoltsId;
        private final int levelReq;
        private final double exp;

        Bolts(int buttonId, int boltsToEnchant, int enchantedBoltsId, Item[] runes, int levelReq, double exp) {
            this.buttonId = buttonId;
            this.boltsToEnchant = boltsToEnchant;
            this.enchantedBoltsId = enchantedBoltsId;
            this.runes = runes;
            this.levelReq = levelReq;
            this.exp = exp;
        }

        public static Bolts forId(int id) {
            for (Bolts bolt : Bolts.values()) {
                if (bolt.buttonId == id)
                    return bolt;
            }
            return null;
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (button == 19207) {
            open(player);
            return true;
        }

        Bolts bolt = Bolts.forId(button);
        if (bolt == null) {
            return false;
        }

        if (handleEnchantBolt(player, button)) {
            return true;
        }
        return false;
    }

    private void open(Player player) {
        for (Map.Entry<Integer, Integer> entry : cosmic.entrySet()) {
            int interfaceId = entry.getKey();
            int requiredAmount = entry.getValue();
            int invAmount = player.inventory().count(COSMIC_RUNE);
            String color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
            String toSend;
            if (color.equalsIgnoreCase("<col=65280>")) {
                toSend = color + "" + requiredAmount + "/" + requiredAmount;
            } else {
                toSend = color + invAmount + "/" + requiredAmount;
            }
            player.getPacketSender().sendString(interfaceId, toSend);
        }

        for (Map.Entry<Integer, Integer> entry : air.entrySet()) {
            int interfaceId = entry.getKey();
            int requiredAmount = entry.getValue();
            int invAmount = player.inventory().count(AIR_RUNE);
            String color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
            String toSend;
            if (color.equalsIgnoreCase("<col=65280>")) {
                toSend = color + "" + requiredAmount + "/" + requiredAmount;
            } else {
                toSend = color + invAmount + "/" + requiredAmount;
            }
            player.getPacketSender().sendString(interfaceId, toSend);
        }

        for (Map.Entry<Integer, Integer> entry : water.entrySet()) {
            int interfaceId = entry.getKey();
            int requiredAmount = entry.getValue();
            int invAmount = player.inventory().count(WATER_RUNE);
            String color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
            String toSend;
            if (color.equalsIgnoreCase("<col=65280>")) {
                toSend = color + "" + requiredAmount + "/" + requiredAmount;
            } else {
                toSend = color + invAmount + "/" + requiredAmount;
            }
            player.getPacketSender().sendString(interfaceId, toSend);
        }

        for (Map.Entry<Integer, Integer> entry : earth.entrySet()) {
            int interfaceId = entry.getKey();
            int requiredAmount = entry.getValue();
            int invAmount = player.inventory().count(EARTH_RUNE);
            String color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
            String toSend;
            if (color.equalsIgnoreCase("<col=65280>")) {
                toSend = color + "" + requiredAmount + "/" + requiredAmount;
            } else {
                toSend = color + invAmount + "/" + requiredAmount;
            }
            player.getPacketSender().sendString(interfaceId, toSend);
        }

        for (Map.Entry<Integer, Integer> entry : fire.entrySet()) {
            int interfaceId = entry.getKey();
            int requiredAmount = entry.getValue();
            int invAmount = player.inventory().count(FIRE_RUNE);
            String color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
            String toSend;
            if (color.equalsIgnoreCase("<col=65280>")) {
                toSend = color + "" + requiredAmount + "/" + requiredAmount;
            } else {
                toSend = color + invAmount + "/" + requiredAmount;
            }
            player.getPacketSender().sendString(interfaceId, toSend);
        }

        int requiredAmount = 1;
        int invAmount = player.inventory().count(MIND_RUNE);
        String color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
        String toSend;
        if (color.equalsIgnoreCase("<col=65280>")) {
            toSend = color + "" + requiredAmount + "/" + requiredAmount;
        } else {
            toSend = color + invAmount + "/" + requiredAmount;
        }
        player.getPacketSender().sendString(saphire_mind_id, toSend);

        invAmount = player.inventory().count(NATURE_RUNE);
        color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
        if (color.equalsIgnoreCase("<col=65280>")) {
            toSend = color + "" + requiredAmount + "/" + requiredAmount;
        } else {
            toSend = color + invAmount + "/" + requiredAmount;
        }
        player.getPacketSender().sendString(emerald_nature_id, toSend);

        invAmount = player.inventory().count(BLOOD_RUNE);
        color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
        if (color.equalsIgnoreCase("<col=65280>")) {
            toSend = color + "" + requiredAmount + "/" + requiredAmount;
        } else {
            toSend = color + invAmount + "/" + requiredAmount;
        }
        player.getPacketSender().sendString(ruby_blood_rune, toSend);

        invAmount = player.inventory().count(LAW_RUNE);
        requiredAmount = 2;
        color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
        if (color.equalsIgnoreCase("<col=65280>")) {
            toSend = color + "" + requiredAmount + "/" + requiredAmount;
        } else {
            toSend = color + invAmount + "/" + requiredAmount;
        }
        player.getPacketSender().sendString(diamond_law_rune, toSend);

        invAmount = player.inventory().count(SOUL_RUNE);
        color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
        if (color.equalsIgnoreCase("<col=65280>")) {
            toSend = color + "" + requiredAmount + "/" + requiredAmount;
        } else {
            toSend = color + invAmount + "/" + requiredAmount;
        }
        player.getPacketSender().sendString(dragonstone_soul_id, toSend);

        invAmount = player.inventory().count(DEATH_RUNE);
        color = (invAmount >= requiredAmount) ? "<col=65280>" : "<col=ff0000>";
        if (color.equalsIgnoreCase("<col=65280>")) {
            toSend = color + "" + requiredAmount + "/" + requiredAmount;
        } else {
            toSend = color + invAmount + "/" + requiredAmount;
        }
        player.getPacketSender().sendString(onyx_death_id, toSend);

        player.getInterfaceManager().open(39000);
    }

    private boolean handleEnchantBolt(Player player, int button) {
        Bolts bolt = Bolts.forId(button);
        if (bolt == null) {
            return false;
        }

        enchant(player, bolt);
        return true;
    }

    /**
     * Handles enchanting the bolts.
     */
    private void enchant(Player player, Bolts bolt) {
        if (player.getSkills().level(Skills.MAGIC) < bolt.levelReq) {
            player.message("You need a crafting level of " + bolt.levelReq + " to craft this!");
            return;
        }

        for (Item rune : bolt.runes) {
            if (!player.inventory().contains(rune.getId(), rune.getAmount())) {
                player.getPacketSender().sendMessage("You need " + rune.getAmount() + " " + rune.name() + (rune.getAmount() > 1 ? "s" : "") + " to enchant this bolt!");
                return;
            }
        }

        final var id = bolt.boltsToEnchant;
        if (!player.inventory().contains(new Item(id, 10))) {
            ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, id);
            String name = def.name;
            player.message("You need at least 10 " + Utils.getAOrAn(name) + "s " + name + " to do this!");
            return;
        }

        player.action.execute(action(player, bolt), true);
    }

    /**
     * Handles blowing the glass data.
     */
    private Action<Player> action(Player player, Bolts bolt) {
        return new Action<>(player, 1, true) {

            @Override
            public void execute() {
                final var enchanted_id = bolt.enchantedBoltsId;
                final var remove_id = bolt.boltsToEnchant;
                player.getInterfaceManager().close();
                player.inventory().remove(remove_id, 10);
                player.inventory().removeAll(bolt.runes);
                player.animate(4462);
                player.graphic(759, GraphicHeight.LOW, 0);
                player.inventory().add(new Item(enchanted_id, 10));
                player.getSkills().addXp(Skills.MAGIC, bolt.exp);

                String name = new Item(enchanted_id).name();
                player.message("You make " + Utils.getAOrAn(name) + " " + name + ".");
                stop();
            }

            @Override
            public String getName() {
                return "EnchantBolts";
            }

            @Override
            public boolean prioritized() {
                return false;
            }

            @Override
            public WalkablePolicy getWalkablePolicy() {
                return WalkablePolicy.NON_WALKABLE;
            }
        };
    }

}
