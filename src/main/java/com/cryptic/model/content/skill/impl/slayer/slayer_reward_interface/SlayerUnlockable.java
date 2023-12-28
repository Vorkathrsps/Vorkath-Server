package com.cryptic.model.content.skill.impl.slayer.slayer_reward_interface;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import java.util.HashMap;
import java.util.Map;

import static com.cryptic.model.content.treasure.TreasureRewardCaskets.MASTER_CASKET;
import static com.cryptic.utility.CustomItemIdentifiers.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin | December, 21, 2020, 13:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum SlayerUnlockable {
    DOUBLE_SLAYER_POINTS(63406, new Item(SLAYER_TOME), 750, "Double slayer points", "Automatically doubles your points when" + "<br>completing any slayer task. <br><col=ca0d0d>(750 points)</col>"),
    SLAYERS_GREED(63407, new Item(BLOOD_MONEY, 10000), 500, "Slayers Greed", "Generate blood money whilst killing <br>monsters on a slayer task.<br><col=ca0d0d>(500 <col=ca0d0d>points)"),
    DEATHS_TOUCH(63408, new Item(SKULL_OF_VETION), 2500, "Deaths Touch", "Have a chance at dealing a finishing blow" + "<br>on any monster.<br><col=ca0d0d>(2500 points)"),
    EMBLEM_HUNTER(63409, new Item(MYSTERIOUS_EMBLEM_TIER_5), 500, "Emblem Hunter", "Monsters have a chance at dropping <br>emblems" + " whilst on a slayer task. <br><col=ca0d0d>(500 points)"),
    SLAYERS_NODE(63410, new Item(CRYSTALLINE_PORTAL_NEXUS), 500, "Slayers Node", "Learn to teleport directly to your <br>slayer task." + "<br><col=ca0d0d>(500 points)"),
    LARRANS_LUCK(63411, new Item(LARRANS_KEY, 10), 150, "Larrans Luck", "Chance to recieve larrans keys" + " during <br>your slayer task. " + "<br><col=ca0d0d>(150 points)"),
    PVP_ARMOURS(63412, new Item(VESTAS_LONGSWORD_BH), 2500, "Ancient Blessing", "Chance to receive ancient warrior <br>equipment whilst on a slayer task. <br><col=ca0d0d>(2500 points)"),
    BIGGER_AND_BADDER(63413, new Item(ABYSSAL_DEMON), 250, "Bigger And Badder", "Chance to encounter a superior foe whilst<br>on your slayer task. <br><col=ca0d0d>(250 points)"),

    SIGIL_DROPPER(63414, new Item(SIGIL_OF_THE_GUARDIAN_ANGEL_26147), 1200, "Attuned Luck", "Chance at receiving sigils whilst on a <br>slayer task. <br><col=ca0d0d>(1200 points)"),

    LIKE_A_BOSS(63415, new Item(3064), 250, "Like a boss", "Slayer master will be able to assign<br>wilderness boss monsters as your task. <br><col=ca0d0d>(250 <col=ca0d0d>points)"),

    BONE_HUNTER(63416, new Item(LAVA_DRAGON_BONES + 1), 250, "Bone Hunter", "Dragon bones will be dropped noted" + "<br>form while killed inside the wilderness." + "<br><col=ca0d0d>(250 points)"),

    SLAYER_HELM(63417, new Item(SLAYER_HELMET_I), 500, "Malevolant Masquerade", "Learn how to combine the protective Slayer<br>headgear and Slayer gem into one<br>universal helmet, with level 55 Crafting.<br><col=ca0d0d>(500 points)"),

    KING_BLACK_BONNET(63418, new Item(BLACK_SLAYER_HELMET), 1000, "King Black Bonnet", "Learn how to combine a KBD head with your<br>slayer helm to colour it black. <br><col=ca0d0d>(1000 points)"),

    KALPHITE_KHAT(63419, new Item(GREEN_SLAYER_HELMET), 1000, "Kalphite Khat", "Learn how to combine a Kalphite Queen<br>head with your slayer helm to colour it<br>green. <col=ca0d0d>(1000 points)"),

    UNHOLY_HELMET(63420, new Item(RED_SLAYER_HELMET), 1000, "Unholy Helmet", "Learn how to combine an Abyssal Demon<br>head with your slayer helm to colour it red.<br><col=ca0d0d>(1000 points)"),

    DARK_MANTLE(63421, new Item(PURPLE_SLAYER_HELMET), 1000, "Dark Mantle", "Learn how to combine a Dark Claw with<br>your slayer helm to colour it purple. <br><col=ca0d0d>(1000 <col=ca0d0d>points)"),

    UNDEAD_HEAD(63422, new Item(TURQUOISE_SLAYER_HELMET), 1000, "Undead Head", "Learn how to combine Vorkath's head with<br>your slayer helm to colour it turqouise. <br><col=ca0d0d>(1000 points)"),

    USE_MORE_HEAD(63423, new Item(HYDRA_SLAYER_HELMET), 1000, "Use more head", "Learn how to combine a Hydra head with<br>your slayer helm to theme it like the<br>Alchemical Hydra. <col=ca0d0d>(1000 points)"),

    WEAK_SPOT(63424, new Item(DRAGON_CLAWS), 150, "Weak spot", "10% Increased accuracy whilst fighting<br>against monsters on a Slayer task." + "<br><col=ca0d0d>(150 points)"),

    STRONK(63425, new Item(STRENGTH_CAPET), 250, "Too Stronk", "1-5 Extra max hits whilst fighting against<br>monsters on a Slayer task.<br><col=ca0d0d>(250 points)"),
    REVVED_UP(63426, new Item(REVENANT_ETHER, 5000), 150, "Revved Up!", "Learn the ability to recieve Revenants<br>as a Slayer task.<br><col=ca0d0d>(150 points)");

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
