package com.aelous.model.content.skill;

import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.masks.impl.animations.AnimationLoop;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.RequiredItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An implementation of {@link DefaultSkillable}.
 *
 * This sub class handles the creation of an item.
 * It's used by many skills such as Fletching.
 *
 * @author Professor Oak
 */
public class ItemCreationSkillable extends DefaultSkillable {

    /**
     * A {@link List} containing all the {@link RequiredItem}s.
     */
    private final List<RequiredItem> requiredItems;

    /**
     * The item we're making.
     */
    private final Item product;

    /**
     * The amount to make.
     */
    private int amount;

    /**
     * The {@link AnimationLoop} the player will perform whilst
     * performing this skillable.
     */
    private final Optional<AnimationLoop> animLoop;

    /**
     * The level required to make this item.
     */
    private final int requiredLevel;

    /**
     * The experience a player will receive
     * in the said skill for making
     * this item.
     */
    private final double experience;

    /**
     * The skill to reward the player experience in.
     */
    private final int skill;

    public ItemCreationSkillable(List<RequiredItem> requiredItems, Item product, int amount, Optional<AnimationLoop> animLoop, int requiredLevel, double experience, int skill) {
        this.requiredItems = requiredItems;
        this.product = product;
        this.amount = amount;
        this.animLoop = animLoop;
        this.requiredLevel = requiredLevel;
        this.experience = experience;
        this.skill = skill;
    }

    @Override
    public void startAnimationLoop(Player player) {
        if (!animLoop.isPresent()) {
            return;
        }
        Task animLoopTask = new Task("ItemCreationAnimationTask", animLoop.get().getLoopDelay(), player, true) {
            @Override
            protected void execute() {
                player.animate(animLoop.get().getAnim());
            }
        };
        TaskManager.submit(animLoopTask);
        getTasks().add(animLoopTask);
    }

    @Override
    public int cyclesRequired(Player player) {
        return 2;
    }

    @Override
    public void onCycle(Player player) {
    }

    @Override
    public void finishedCycle(Player player) {
        //Decrement amount to make and stop if we hit 0.
        //Random discord code audit, this is incorrect: https://i.imgur.com/t5A5WiM.png
        //if (amount-- <= 0) {
        if (--amount <= 0) {
            cancel(player);
        }

        //remove items required..
        filterRequiredItems(RequiredItem::isDelete).forEach(r -> {
            player.inventory().remove(r.getItem());
            if(r.getReplaceWith() != null) {
                player.inventory().add(r.getReplaceWith());
            }
        });

        //Add product..
        player.inventory().add(product);

        if(product.name().equalsIgnoreCase("Adamant platebody")) {
            player.getTaskMasterManager().increase(Tasks.MAKE_ADAMANT_PLATEBODY);
        }

        //Add exp..
        player.getSkills().addXp(skill, experience);
    }

    @Override
    public boolean hasRequirements(Player player) {
        //Validate amount..
        if (amount <= 0) {
            return false;
        }

        //Check if we have required stringing level..
        if (player.getSkills().levels()[skill] < requiredLevel) {
            player.message("You need a "+Skills.SKILL_NAMES[skill]+" level of at least "+ requiredLevel +" to do this.");
            return false;
        }

        //Validate required items..
        //Check if we have the required ores..
        boolean hasItems = true;
        for (RequiredItem item : requiredItems) {
            if (!player.inventory().contains(item.getItem())) {
                String prefix = item.getItem().getAmount() > 1 ? Integer.toString(item.getItem().getAmount()) : "some";
                player.message("You "+(!hasItems ? "also need" : "need")+" "+prefix+" "+item.getItem().unnote().name()+".");
                hasItems = false;
            }
        }
        if (!hasItems) {
            return false;
        }

        return super.hasRequirements(player);
    }

    @Override
    public boolean loopRequirements() {
        return true;
    }

    @Override
    public boolean allowFullInventory() {
        return true;
    }

    public void decrementAmount() {
        amount--;
    }

    public int getAmount() {
        return amount;
    }

    public List<RequiredItem> filterRequiredItems(Predicate<RequiredItem> criteria) {
        return requiredItems.stream().filter(criteria).collect(Collectors.toList());
    }

    public List<RequiredItem> getRequiredItems() {
        return requiredItems;
    }
}
