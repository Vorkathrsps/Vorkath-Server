package com.aelous.model.content.skill.impl.smithing;

import com.aelous.model.content.skill.ItemCreationSkillable;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.AnimationLoop;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.RequiredItem;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import static com.aelous.utility.ItemIdentifiers.RING_OF_FORGING;

/**
 * Handles smelting ores to combine them into bars.
 * @author Professor Oak
 */
public class Smelting extends ItemCreationSkillable {
    /**
     * The {@link Animation} the mob will perform
     * when smelting.
     */
    private static final Animation ANIMATION = new Animation(896);

    /**
     * The bar being smelted.
     */
    private final Bar bar;

    /**
     * Constructs this {@link Smelting} instance.
     * @param bar
     * @param amount
     */
    public Smelting(Bar bar, int amount) {
        super(Arrays.asList(bar.getOres()), new Item(bar.getBar()), amount, Optional.of(new AnimationLoop(ANIMATION, 4)), bar.getLevelReq(), bar.getXpReward(), Skills.SMITHING);
        this.bar = bar;
    }

    private final Random random = new Random();

    //Override finishedCycle because we need to handle special cases
    //such as Iron ore 50% chance of failing to smelt.
    @Override
    public void finishedCycle(Player player) {
        //Handle iron bar. It has a 50% chance of failing.
        if (bar == Bar.IRON_BAR) {

            // By default, roll a die with true or false
            boolean success = random.nextBoolean();

            // If we have a ring, remove a charge and set 'success' to true.
            if (player.getEquipment().contains(RING_OF_FORGING)) {
                int charges = (int) player.getAttribOr(AttributeKey.RING_OF_FORGING_CHARGES, 140) - 1;

                if (charges <= 0) {
                    player.putAttrib(AttributeKey.RING_OF_FORGING_CHARGES, 140);
                    player.getEquipment().remove(new Item(RING_OF_FORGING), true);
                    player.message("Your Ring Of Forging has melted.");
                } else {
                    player.putAttrib(AttributeKey.RING_OF_FORGING_CHARGES, charges);
                }

                success = true;
            }

            // Add a bar or make the player cry about losing one precious metal piece :'(
            if (success) {
                player.message("You retrieve a bar of iron.");
            } else {
                //We still need to delete the ore and decrement amount.
                filterRequiredItems(RequiredItem::isDelete).forEach(r -> player.inventory().remove(r.getItem()));
                decrementAmount();
                player.message("The ore is too impure and you fail to refine it.");
                return;
            }
        }
        super.finishedCycle(player);
    }

    /**
     * Handles buttons related to the Smithing skill.
     * @param player
     * @param button
     * @return
     */
    public static boolean handleButton(Player player, int button) {
        //Handle bar creation interface..
        for (Bar bar : Bar.values()) {
            for (int[] b : bar.getButtons()) {
                if (b[0] == button) {
                    int amount = b[1];
                    if (amount == -1) {
                        player.setAmountScript("Enter amount of bars to smelt:", new InputScript() {

                            @Override
                            public boolean handle(Object value) {
                                int amount = (Integer) value;
                                if (amount <= 0 || amount > Integer.MAX_VALUE) {
                                    /**
                                     * TODO
                                     */
                                    return false;
                                }
                                player.getSkills().startSkillable(new Smelting(bar, amount));
                                return true;
                            }
                        });
                    } else {
                        player.getSkills().startSkillable(new Smelting(bar, amount));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
