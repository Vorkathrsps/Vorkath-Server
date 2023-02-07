package com.aelous.model.content.skill.impl.slayer.slayer_reward_interface;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick van Elderen | December, 21, 2020, 13:17
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum SlayerExtendable {

    ADAMIND_SOME_MORE(64306, new Item(7621), 50, "Ada'mind some more", "Whenever you get an Adamant Dragon " + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    RUUUUUNE(64307, new Item(8797), 50, "RUUUUUNE", "Whenever you get an Rune Dragon " + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    BARRELCHEST(64308, new Item(ItemIdentifiers.BARRELCHEST_ANCHOR), 50, "Barrelchest", "Whenever you get an Barrelchest " + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    FLUFFY(64309, new Item(ItemIdentifiers.HELLPUPPY), 50, "Fluffy", "Whenever you get an Cerberus " + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    PURE_CHAOS(64310, new Item(ItemIdentifiers.CHAOS_ELEMENTAL), 50, "Pure Chaos", "Whenever you get a Chaos Elemental task," + "<br>it will be a bigger task. <col=ca0d0d>(50 points)"),

    CORPOREAL_LECTURE(64311, new Item(ItemIdentifiers.PET_CORPOREAL_CRITTER), 50, "Corporeal lecture", "Whenever you get a Corporeal beast task," + "<br>it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    CRAZY_SCIENTIST(64312, new Item(ItemIdentifiers.MIXED_CHEMICALS), 50, "Crazy Scientist", "Whenever you get an Crazy archaeologist " + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    GORILLA_DEMON(64313, new Item(ItemIdentifiers.GORILLA_GREEGREE), 50, "Gorilla Demon", "Whenever you get an Demonic gorilla " + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    DRAGON_SLAYER(64314, new Item(ItemIdentifiers.PRINCE_BLACK_DRAGON), 50, "Dragon Slayer", "Whenever you get a King Black Dragon" + "<br>task it will will be a bigger task." + "<col=ca0d0d>(50 points)"),

    SCYLLA(64315, new Item(ItemIdentifiers.CAVE_KRAKEN), 50, "Scylla", "Whenever you get a Kraken task, it will be" + "<br>a bigger task." + "<col=ca0d0d>(50 points)"),

    JUMPING_JACKS(64316, new Item(ItemIdentifiers.OLD_TOOTH), 50, "Jumping Jacks", "Whenever you get a Lizardman shaman" + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    SPOOKY_SCARY_SKELETONS(64317, new Item(ItemIdentifiers.VETION_JR), 50, "Spooky Scary Skeletons", "Whenever you get a Vet'ion task, it will" + "<br>be a bigger task." + "<col=ca0d0d>(50 points)"),

    ATOMIC_BOMB(64318, new Item(ItemIdentifiers.SMOKE_DEVIL), 50, "Atomic Bomb", "Whenever you get a Thermonuclear Smoke" + "<br>Devil task, it will be a bigger task." + "<br><col=ca0d0d>(50 points)"),

    VORKI(64319, new Item(ItemIdentifiers.VORKI), 50, "Vorki", "Whenever you get a Vorkath task, it will" + "<br>be a bigger task." + "<col=ca0d0d>(50 points)"),

    NAGINI(64320, new Item(ItemIdentifiers.PET_SNAKELING), 50, "Nagini", "Whenever you get a Zulrah task, it will" + "<br>be a bigger task." + "<col=ca0d0d>(50 points)"),

    WYVER_ANOTHER_ONE(64321, new Item(ItemIdentifiers.FOSSIL_ISLAND_WYVERN), 50, "Wyver-nother one", "Whenever you get a Fossil Island Wyvern" + "<br>task, it will be a bigger task." + "<col=ca0d0d>(50 points)"),

    ARAGOG(64322, new Item(ItemIdentifiers.VENENATIS_SPIDERLING), 50, "Aragog", "Whenever you get a Venenatis task, it" + "<br>will be a bigger task." + "<col=ca0d0d>(50 points)"),

    BEWEAR(64323, new Item(ItemIdentifiers.CALLISTO_CUB), 50, "Bewear", "Whenever you get a Callisto task, it will" + "<br>be a bigger task." + "<col=ca0d0d>(50 points)"),

    DRAKE(64324, new Item(ItemIdentifiers.DRAKE), 50, "Drake", "Whenever you get a Drakes task, it will" + "<br>be a bigger task." + "<col=ca0d0d>(50 points)"),

    WYRM_ME_ON(64325, new Item(ItemIdentifiers.WYRM), 50, "Wyrm me on", "Whenever you get an Wyrms task, it will" + "<br>be a bigger task." + "<col=ca0d0d>(50 points)"),

    DR_CHAOS(64326, new Item(ItemIdentifiers.CHAOS_GAUNTLETS), 50, "DR Chaos", "Whenever you get a Chaos fanatic task, it" + "<br>will be a bigger task." + "<col=ca0d0d>(50 points)"),

    DIG_ME_UP(64327, new Item(ItemIdentifiers.SPADE), 50, "Dig me up", "Whenever you get a Barrows task, it will" + "<br>be a bigger task. <col=ca0d0d>(50 points)"),

    LAVA(64328, new Item(ItemIdentifiers.LAVA_DRAGON_BONES), 50, "Lava", "Whenever you get a Lava dragons task," + "<br>it will be a bigger task. <col=ca0d0d>(50 points)"),

    WORLD_BOSSILONGER(64329, new Item(ItemIdentifiers.SKOTOS), 50, "World Bossilonger", "Whenever you get a World boss task, it will be" + "<br>a bigger task. <col=ca0d0d>(50 points)"),

    GOD_WAR(64330, new Item(ItemIdentifiers.GODSWORD_BLADE), 50, "God War", "Whenever you get a General Graardor," + "<br>Commander Zilyana, K'ril Tsutsaroth or" + "<br>Kree'Arra task, it will be a bigger task."+ "<br><col=ca0d0d>(50 points)");

    private final int buttonId;
    private final Item item;
    private final int rewardPoints;
    private final String name;
    private final String description;

    SlayerExtendable(int buttonId, Item item, int rewardPoints, String name, String description) {
        this.buttonId = buttonId;
        this.item = item;
        this.rewardPoints = rewardPoints;
        this.name = name;
        this.description = description;
    }

    public int getButtonId() {
        return buttonId;
    }

    public Item getItem() {
        return item;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * A map of extendable buttons.
     */
    private static final Map<Integer, SlayerExtendable> extendable = new HashMap<>();

    public static SlayerExtendable byButton(int id) {
        return extendable.get(id);
    }

    static {
        for (SlayerExtendable extendButtons : values()) {
            extendable.put(extendButtons.getButtonId(), extendButtons);
        }
    }

    public static void updateInterface(Player player) {
        for (SlayerExtendable slayerExtendable : SlayerExtendable.values()) {
            player.getPacketSender().sendItemOnInterface(64331 + slayerExtendable.ordinal(), slayerExtendable.getItem());
            player.getPacketSender().sendString(64356 + slayerExtendable.ordinal(), slayerExtendable.getName());
            player.getPacketSender().sendString(64381 + slayerExtendable.ordinal(), slayerExtendable.getDescription());
        }
    }
}
