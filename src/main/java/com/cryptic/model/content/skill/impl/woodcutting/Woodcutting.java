package com.cryptic.model.content.skill.impl.woodcutting;

import com.cryptic.model.content.areas.zeah.woodcutting_guild.WoodcuttingGuild;
import com.cryptic.model.content.skill.impl.woodcutting.impl.Axe;
import com.cryptic.model.content.skill.impl.woodcutting.impl.Trees;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;

import java.util.Map;
import java.util.Random;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 10/7/2023
 */
public class Woodcutting extends PacketInteraction {
    double experienceMultiplier = 15;
    Random random = new Random();
    int[] birdNest = new int[]{5070, 5071, 5072, 5073, 5074, 5075};
    private final Map<Trees, Integer> TREE_MAP = Map.of(
        Trees.LOGS, TREE_STUMP_1342,
        Trees.OAK_TREE, TREE_STUMP_1342,
        Trees.WILLOW_TREE, TREE_STUMP_9711,
        Trees.TEAK_TREE, TREE_STUMP_9037,
        Trees.MAPLE_TREE, TREE_STUMP_9712,
        Trees.MAHOGANY_TREE, TREE_STUMP_9035,
        Trees.YEW_TREE, TREE_STUMP_9714,
        Trees.MAGIC_TREE, TREE_STUMP_9713,
        Trees.REDWOOD, REDWOOD_29671
    );

    boolean success(int woodcuttingLevel, Trees tree, Axe axe) {

        double successChance = calculateSuccessChance(woodcuttingLevel, tree, axe);
        double randomValue = random.nextDouble();

        return successChance > randomValue;
    }

    double calculateSuccessChance(int level, Trees tree, Axe type) {
        int[][] hatchetValues = type.getValues();
        final int[] l = {0};
        final int[] h = {0};
        int treeOrdinal = tree.ordinal();
        if (treeOrdinal < hatchetValues.length) {
            l[0] = hatchetValues[treeOrdinal][0];
            h[0] = hatchetValues[treeOrdinal][1];
        }
        return (1D + (Math.floor((l[0] * (99D - level)) / 98D) + Math.floor((h[0] * (level - 1D)) / 98D))) / 256D;
    }

    boolean collapseTree(Player player, Trees tree, int trunkObjectId) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
        GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
        GameObject spawned = new GameObject(trunkObjectId, obj.tile(), obj.getType(), obj.getRotation());
        ObjectManager.replace(old, spawned, tree.cycle);
        player.getSkills().addExperience(Skills.WOODCUTTING, tree.experience, experienceMultiplier, true);
        return true;
    }

    void cut(Player player, Trees tree, int trunkObjectId) {
        Axe axe = Axe.findAxe(player).orElse(null);

        if (axe == null) {
            player.message("You do not have an axe that you can use at your Woodcutting level.");
            return;
        }

        int woodcuttingLevel = player.getSkills().levels()[Skills.WOODCUTTING];
        if (woodcuttingLevel < tree.level) {
            player.message("You need a Woodcutting level of " + tree.level + " to chop down this tree.");
            return;
        }

        if (player.inventory().isFull()) {
            player.message("Your inventory is too full to hold any more logs.");
            return;
        }

        player.message("You swing your axe at the tree.");
        player.stopActions(false);
        player.animate(axe.anim);

        player.repeatingTask(4, work -> {
            if (player.getMovement().isMoving() || player.inventory().isFull()) {
                player.looks().resetRender();
                player.message("Your inventory is too full to hold any more logs.");
                work.stop();
                return;
            }

            if (Utils.percentageChance((int) 12.5D)) {
                player.inventory().add(new Item(tree.item));
                collapseTree(player, tree, trunkObjectId);
                work.stop();
                return;
            }

            player.animate(axe.anim);

            int modifiedLevel = woodcuttingLevel;
            if (player.tile().inArea(WoodcuttingGuild.AREA_EAST) || player.tile().inArea(WoodcuttingGuild.AREA_WEST)) {
                modifiedLevel += 7;
            }

            var success = success(modifiedLevel, tree, axe);

            if (success) {
                player.getSkills().addExperience(Skills.WOODCUTTING, tree.experience, experienceMultiplier, true);
                player.inventory().add(new Item(tree.item));
            }
        });
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            for (var tree : Trees.values()) {
                for (var object : tree.getObjects()) {
                    if (obj.getId() == object) {
                        var trunk = TREE_MAP.get(tree);
                        if (trunk != null) {
                            player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> cut(player, tree, trunk));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
