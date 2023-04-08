package com.aelous.model.content.skill.impl.smithing;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.skill.ItemCreationSkillable;
import com.aelous.model.content.skill.Skillable;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.AnimationLoop;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.RequiredItem;
import com.aelous.utility.ItemIdentifiers;

import java.util.Arrays;
import java.util.Optional;

/**
 * Handles making equipment from bars.
 * @author Professor Oak
 */
public class EquipmentMaking {

    /**
     * The interface used for creating equipment using the
     * Smithing skill.
     */
    public static final int EQUIPMENT_CREATION_INTERFACE_ID = 994;

    /**
     * The interface ids used for selecting an item to create in the
     * {@code EQUIPMENT_CREATION_INTERFACE_ID}.
     */
    public static final int EQUIPMENT_CREATION_COLUMN_1 = 1119;
    public static final int EQUIPMENT_CREATION_COLUMN_2 = 1120;
    public static final int EQUIPMENT_CREATION_COLUMN_3 = 1121;
    public static final int EQUIPMENT_CREATION_COLUMN_4 = 1122;
    public static final int EQUIPMENT_CREATION_COLUMN_5 = 1123;

    /**
     * This method is triggered when a player clicks
     * on an anvil in the game.
     *
     * We will search for bars and then open the
     * corresponding interface if one was found.
     *
     * @param player
     */
    public static void openInterface(Player player) {
        //Search for bar..
        Optional<Bar> bar = Optional.empty();
        for (Bar b : Bar.values()) {
            if (b.getItems().isEmpty()) {
                continue;
            }
            if (player.inventory().contains(b.getBar())) {
                if (player.getSkills().levels()[Skills.SMITHING] >= b.getLevelReq()) {
                    bar = Optional.of(b);
                }
            }
        }

        //Did we find a bar in the player's inventory?
        if (bar.isPresent()) {
            //Go through the bar's items..
            if (bar.get().getItems().isPresent()) {
                for (SmithableEquipment b : bar.get().getItems().get()) {
                    player.getPacketSender().sendItemOnInterfaceSlot(b.getItemFrame(), b.getItemId(), b.getAmount(), b.getItemSlot());

                    int bars = player.inventory().count(b.getBarId());
                    int smithLevel = player.getSkills().levels()[Skills.SMITHING];
                    boolean meetsRequirementsForOilLamp = true;
                    player.getPacketSender().sendConfig(210, bars);
                    player.getPacketSender().sendConfig(211, smithLevel);
                    player.getPacketSender().sendConfig(262, meetsRequirementsForOilLamp ? 1 : 0);
                }
            }

            //Send interface..
            player.getInterfaceManager().open(EQUIPMENT_CREATION_INTERFACE_ID);
        } else {
            player.message("You don't have any bars in your inventory which can be used with your Smithing level.");
        }
    }

    /**
     * Attempts to initialize a new {@link Skillable
     * @param itemId
     * @param interfaceId
     * @param slot
     * @param amount
     */
    public static void initialize(Player player, int itemId, int interfaceId, int slot, int amount) {
        //First verify the item we're trying to make..
        for (SmithableEquipment smithable : SmithableEquipment.values()) {
            if (smithable.getItemId() == itemId && smithable.getItemFrame() == interfaceId
                && smithable.getItemSlot() == slot) {
                //Start making items..
                player.getSkills().startSkillable(new ItemCreationSkillable(Arrays.asList(new RequiredItem(new Item(ItemIdentifiers.HAMMER)), new RequiredItem(new Item(smithable.getBarId(), smithable.getBarsRequired()), true)),
                    new Item(smithable.getItemId(), smithable.getAmount()), amount, Optional.of(new AnimationLoop(new Animation(898), 3)), smithable.getRequiredLevel(), smithable.getExperience(), Skills.SMITHING));
                switch (smithable) {
                    case BRONZE_PLATEBODY -> AchievementsManager.activate(player, Achievements.SMELTING_I, 1);
                    case MITHRIL_PLATEBODY -> AchievementsManager.activate(player, Achievements.SMELTING_II, 1);
                    case ADAMANT_PLATEBODY -> AchievementsManager.activate(player, Achievements.SMELTING_III, 1);
                    case RUNE_PLATEBODY -> AchievementsManager.activate(player, Achievements.SMELTING_IV, 1);
                }
                break;
            }
        }
    }
}
