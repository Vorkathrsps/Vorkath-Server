package com.aelous.model.content.skill.impl.slayer.slayer_reward_interface;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import java.util.HashMap;
import java.util.Map;

import static com.aelous.model.content.treasure.TreasureRewardCaskets.MASTER_CASKET;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | December, 21, 2020, 13:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum SlayerUnlockable {

    DOUBLE_SLAYER_POINTS(63406, new Item(SLAYER_TOME), 750, "Double slayer points", "Automatically doubles your points when" + "<br>completing any slayer task. <col=ca0d0d>(750 points)</col>"),

    BLOOD_MONEY_FROM_KILLING_BOSSES(63407, new Item(BLOOD_MONEY, 500), 750, "BM from killing bosses", "Generate BM whilst killing bosses.<col=ca0d0d>(750 <br><col=ca0d0d>points)"),
    KILL_BLOW(63409, new Item(ARMADYL_GODSWORD), 750, "Kill blow", "Have a chance at dealing a finishing blow" + "<br>on any monster.<col=ca0d0d>(750 points)"),

    BIGGER_AND_BADDER(63410, new Item(1451), 75, "Bigger and Badder", "Increase the risk against certain slayer" + "<br>monsters with the chance of a superior " + "<br>version spawning whilst on a slayer task.<br><col=ca0d0d>(75 points)"),

    REVENANT_TELEPORT(63411, new Item(REVENANT_CAVE_TELEPORT), 300, "Revenants cave teleport", "Learn to teleport directly to the revenants." + "<br><col=ca0d0d>(300 points)"),

    LARRANS_LUCK(63412, new Item(LARRANS_KEY, 10), 250, "Larrans luck", "Extra chance of receiving Larrans keys" + "<br>during your slayer tasks. Larrans key" + "<br>drop rate = 1/400 This perk gives you 12.5% <br>more chance. <col=ca0d0d>(250 points)"),

    NO_SLAYER_REQ(63413, new Item(SLAYERS_RESPITE), 400, "No slayer requirement", "You can kill any monster without the slayer<br>level requirement. <col=ca0d0d>(400 points)"),

    MORE_BM_THIEVING(63414, new Item(BLOOD_MONEY, 5000), 350, "Thieving boost", "Gives 10% more blood money whilst<br>thieving. <col=ca0d0d>(350 points)"),

    DROP_RATE_BOOST(63415, new Item(SPELL_SCROLL), 700, "Drop rate boost", "Boosts your drop rate by 3%. <br><col=ca0d0d>(700 points)"),

    LIKE_A_BOSS(63418, new Item(3064), 100, "Like a boss", "Slayer master will be able to assign<br>World boss monsters as your task. <col=ca0d0d>(100 <br><col=ca0d0d>points)"),

    NOTED_DRAGON_BONES(63420, new Item(LAVA_DRAGON_BONES + 1), 250, "Noted dragon bones", "Dragons drop dragon bones in banknote" + "<br>form while killed inside the wilderness." + "<br><col=ca0d0d>(250 points)"),

    GODWARS_ENTRY(63421, new Item(KREEARRA), 50, "Godwars dungeon entry", "Learn how to access the godwars dungeon.<br><col=ca0d0d>(50 points)"),

    WORLD_BOSS_TELEPORT(63422, new Item(SKOTOS), 250, "World Boss Teleport", "Ability to teleport directly to the world<br>boss. <col=ca0d0d>(250 points)"),

    RUNAWAY(63423, new Item(BOOTS_OF_LIGHTNESS), 400, "Runaway", "Infinite run energy outside of the<br>wilderness. <col=ca0d0d>(400 points)"),

    TREASURE_HUNT(63424, new Item(CASKET), 300, "Treasure hunt", "Learn how to find treasures whilst pvming.<br><col=ca0d0d>(300 points)"),

    TZTOK_JAD(63425, new Item(TZREKJAD), 100, "TzTok-Jad", "You unlock the ability to kill TzTok-Jad. <col=ca0d0d>(100 <br><col=ca0d0d>points)"),

    BIGGEST_AND_BADDEST(63426, new Item(SUPERIOR_DRAGON_BONES), 500, "Biggest and baddest", "Better chance to get a superior boss," + "<br>while on slayer task <col=ca0d0d>(500 points)"),

    ELECTION_DAY(63427, new Item(VOTE_TICKET), 750, "Election day", "20% Chance for double vote points when" + "<br>voting <col=ca0d0d>(750 points)"),

    WEAK_SPOT(63429, new Item(BANDOS_GODSWORD), 650, "Weak spot", "10% Increased damage to the task monster." + "<br><col=ca0d0d>(650 points)"),

    DIGGING_FOR_TREASURE(63430, new Item(MASTER_CASKET), 200, "Digging for treasure", "One extra loot when" + "<br>opening master caskets.<br><col=ca0d0d>(200 points)");

    private final int buttonId;
    private final Item item;
    private final int rewardPoints;
    private final String name;
    private final String description;

    SlayerUnlockable(int buttonId, Item item, int rewardPoints, String name, String description) {
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
     * A map of unlockable buttons.
     */
    private static final Map<Integer, SlayerUnlockable> unlockable = new HashMap<>();

    public static SlayerUnlockable byButton(int id) {
        return unlockable.get(id);
    }

    static {
        for (SlayerUnlockable unlockButtons : values())
            unlockable.put(unlockButtons.getButtonId(), unlockButtons);
    }

    public static void updateInterface(Player player) {
        for (SlayerUnlockable slayerUnlockable : SlayerUnlockable.values()) {
            player.getPacketSender().sendItemOnInterface(63431 + slayerUnlockable.ordinal(), slayerUnlockable.getItem());
            player.getPacketSender().sendString(63456 + slayerUnlockable.ordinal(), slayerUnlockable.getName());
            player.getPacketSender().sendString(63481 + slayerUnlockable.ordinal(), slayerUnlockable.getDescription());
        }
    }
}
