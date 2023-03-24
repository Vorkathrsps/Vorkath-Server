package com.aelous.model.content.areas.wilderness.content;

import com.aelous.GameServer;
import com.aelous.model.content.items_kept_on_death.ItemsKeptOnDeath;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

/**
 * This class represents the "wealth risk" you bring to the wilderness for bonus
 * rewards.
 *
 * @author Patrick van Elderen | Zerikoth (PVE) | 30 aug. 2019 : 07:27
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class RiskManagement {

    /**
     * The player instance of this class
     */
    private final Player player;

    /**
     * The RiskManagement constructor
     *
     * @param player The player
     */
    public RiskManagement(Player player) {
        this.player = player;
    }

    private static final int REWARD_ROLL = 100;
    private String REWARD_TYPE = "COMMON";

    /**
     * Updates our 'carried wealth' attribute. The wealth is used in the anti-rag Risk Protection mechanic.
     * This identifies the item which costs the most, and excludes it, as it's likely our +1 protected item.
     * <p>
     * This does not consider if you're skulled as not, it assumes you'll lose all but your +1
     * If we were to add skull checks, we'd have to hook into skulling/unskulling, adding a bit more weight to the system
     * performance, which isn't needed.
     * <p>
     * There are TWO 'carriedWealth' values - one which includes items lost above 20+ wild and one that doesn't.
     * Depending on the wild level you were at when in combat, it'll decide which value to use.
     */
    public void updateCarriedWealthProtection() {
        ItemsKeptOnDeath.clearAndRecalc(player);


        //Get risked value.
        long risked = ItemsKeptOnDeath.getLostItemsValue();
        
        // Store values
        player.putAttrib(AttributeKey.RISKED_WEALTH, risked);
        player.getPacketSender().sendString(QuestTab.InfoTab.RISKED_WEALTH.childId, QuestTab.InfoTab.INFO_TAB.get(QuestTab.InfoTab.RISKED_WEALTH.childId).fetchLineData(player));
        player.debugMessage("Your wealth risked is "+ Utils.formatNumber(risked)+" bm.");
    }

    private void roll() {
        int roll = Utils.random(REWARD_ROLL);
        //System.out.println("Current roll: "+roll);

        int wild_level = WildernessArea.wildernessLevel(player.tile());

        // When we're above level 40 wilderness add 5 on the roll. Thus a better chance to get better rewards.
        if (wild_level > 40) {
            roll += 5;
        }

        if (roll > 100)
            roll = 100;

        int[] veryRareRange = Utils.convertRollRangeStringToIntArray(GameServer.properties().riskManagementVeryRareRollRange);
        int[] rareRange = Utils.convertRollRangeStringToIntArray(GameServer.properties().riskManagementRareRollRange);
        int[] uncommonRange = Utils.convertRollRangeStringToIntArray(GameServer.properties().riskManagementUncommonRollRange);

        if (roll > veryRareRange[1] && roll <= veryRareRange[0]) {
            if (GameServer.properties().debugMode && player.getPlayerRights().isAdministrator(player)) {
                player.message("Very rare reward difference of: " + (veryRareRange[0] - veryRareRange[1]));
            }
            REWARD_TYPE = "VERY_RARE_REWARD";
        } else if (roll > rareRange[1] && roll <= rareRange[0]) {
            if (GameServer.properties().debugMode && player.getPlayerRights().isAdministrator(player)) {
                player.message("Rare reward difference of: " + (rareRange[0] - rareRange[1]));
            }
            REWARD_TYPE = "RARE_REWARD";
        } else if (roll >= uncommonRange[1] && roll <= uncommonRange[0]) {
            if (GameServer.properties().debugMode && player.getPlayerRights().isAdministrator(player)) {
                player.message("Uncommon reward difference of: " + (uncommonRange[0] - uncommonRange[1]));
            }
            REWARD_TYPE = "UNCOMMON_REWARD";
        } else {
            if (GameServer.properties().debugMode && player.getPlayerRights().isAdministrator(player)) {
                player.message("Common reward difference of: " + (uncommonRange[1]));
            }
            REWARD_TYPE = "COMMON_REWARD";
        }
    }

    public void update() {
        player.getRisk().updateCarriedWealthProtection(); // Make sure wealth attribs are up to date!
    }

    public void reward() {
        //First roll the random number
        roll();

        Item reward = null;
        String message;
        String amount = "";

        //Set rewards
        switch (REWARD_TYPE) {
            case "VERY_RARE_REWARD" -> {
                reward = Utils.randomElement(very_rare_rewards);
                amount = reward.getAmount() > 1 ? "" + Utils.kOrMil(reward.getAmount()) : Utils.getAOrAn(reward.name());
            }

            case "RARE_REWARD" -> {
                reward = Utils.randomElement(rare_rewards);
                amount = reward.getAmount() > 1 ? "" + Utils.kOrMil(reward.getAmount()) : Utils.getAOrAn(reward.name());
            }

            case "UNCOMMON_REWARD" -> {
                reward = Utils.randomElement(uncommon_rewards);
                amount = reward.getAmount() > 1 ? "" + Utils.kOrMil(reward.getAmount()) : Utils.getAOrAn(reward.name());
            }

            case "COMMON_REWARD" -> {
                reward = Utils.randomElement(common_rewards);
                amount = reward.getAmount() > 1 ? "" + Utils.kOrMil(reward.getAmount()) : Utils.getAOrAn(reward.name());
            }
        }

        if (reward != null) {
            message = "[<col=" + Color.MEDRED.getColorValue() + ">Daredevil</col>]:</col> You took a risk and was rewarded with <col=" + Color.PURPLE.getColorValue() + ">" + amount + " " + reward.name() + "</col>!";
            player.message(message);

            //Send a world message for very rare or rare rewards.
            boolean worldMessage = REWARD_TYPE.equalsIgnoreCase("VERY_RARE_REWARD") || REWARD_TYPE.equalsIgnoreCase("RARE_REWARD");
            if (worldMessage) {
                message = "[<col=" + Color.MEDRED.getColorValue() + ">Daredevil</col>]:</col> <col=" + Color.BLUE.getColorValue() + ">" + player.getUsername() + "</col> took a risk and was rewarded with <col=" + Color.PURPLE.getColorValue() + ">" + amount + " " + reward.name() + "</col>!";
                World.getWorld().sendWorldMessage(message);
            }
        }

        player.inventory().addOrDrop(reward);
    }

    private final Item[] very_rare_rewards = {
        new Item(ItemIdentifiers.ARMADYL_GODSWORD),
        new Item(ItemIdentifiers.TOXIC_BLOWPIPE),
        new Item(ItemIdentifiers.DRAGON_CLAWS),
        new Item(ItemIdentifiers.HEAVY_BALLISTA),
        new Item(ItemIdentifiers.VESTAS_LONGSWORD),
        new Item(ItemIdentifiers.STATIUSS_WARHAMMER),
        new Item(ItemIdentifiers.VESTAS_SPEAR)
    };

    private final Item[] rare_rewards = {
        new Item(ItemIdentifiers.BANDOS_CHESTPLATE),
        new Item(ItemIdentifiers.BANDOS_TASSETS),
        new Item(ItemIdentifiers.ARMADYL_CHESTPLATE),
        new Item(ItemIdentifiers.ARMADYL_CHAINSKIRT),
        new Item(ItemIdentifiers.ARMADYL_HELMET),
        new Item(ItemIdentifiers.SERPENTINE_HELM),
        new Item(ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD)
    };

    private final Item[] uncommon_rewards = {
        new Item(ItemIdentifiers.BANDOS_GODSWORD),
        new Item(ItemIdentifiers.SARADOMIN_GODSWORD),
        new Item(ItemIdentifiers.ZAMORAK_GODSWORD),
        new Item(ItemIdentifiers.OCCULT_NECKLACE),
        new Item(ItemIdentifiers.STAFF_OF_THE_DEAD)
    };

    private final Item[] common_rewards = {
        new Item(ItemIdentifiers.AMULET_OF_FURY),
        new Item(ItemIdentifiers.SARADOMIN_SWORD),
        new Item(ItemIdentifiers.DHAROKS_ARMOUR_SET),
        new Item(ItemIdentifiers.DHAROKS_ARMOUR_SET),
        new Item(ItemIdentifiers.GUTHANS_ARMOUR_SET),
        new Item(ItemIdentifiers.VERACS_ARMOUR_SET),
        new Item(ItemIdentifiers.TORAGS_ARMOUR_SET),
        new Item(ItemIdentifiers.KARILS_ARMOUR_SET),
        new Item(ItemIdentifiers.AHRIMS_ARMOUR_SET),
    };

}
