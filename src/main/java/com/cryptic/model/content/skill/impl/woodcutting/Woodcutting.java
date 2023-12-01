package com.cryptic.model.content.skill.impl.woodcutting;

import com.cryptic.core.task.Task;
import com.cryptic.model.content.areas.zeah.woodcutting_guild.WoodcuttingGuild;
import com.cryptic.model.content.skill.impl.firemaking.LogLighting;
import com.cryptic.model.content.skill.impl.woodcutting.impl.Axe;
import com.cryptic.model.content.skill.impl.woodcutting.impl.Trees;
import com.cryptic.model.content.skill.perks.SkillingItems;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 10/7/2023
 */
public class Woodcutting extends PacketInteraction {
    static double experienceMultiplier = 15;
    static Item[] leaves = new Item[]
        {
            new Item(ItemIdentifiers.LEAVES),
            new Item(ItemIdentifiers.OAK_LEAVES),
            new Item(ItemIdentifiers.WILLOW_LEAVES),
            new Item(ItemIdentifiers.MAPLE_LEAVES),
            new Item(ItemIdentifiers.YEW_LEAVES),
            new Item(ItemIdentifiers.MAGIC_LEAVES)
        };
    static HashMap<Item, Trees> leafMap = new HashMap<>();
    static Random random = new Random();
    int[] birdNest = new int[]{5070, 5071, 5072, 5073, 5074, 5075};
    private final Map<Trees, Integer> TREE_MAP = Map.of(
        Trees.TREE, TREE_STUMP_1342,
        Trees.OAK_TREE, TREE_STUMP_1342,
        Trees.WILLOW_TREE, TREE_STUMP_9711,
        Trees.TEAK_TREE, TREE_STUMP_9037,
        Trees.MAPLE_TREE, TREE_STUMP_9712,
        Trees.MAHOGANY_TREE, TREE_STUMP_9035,
        Trees.YEW_TREE, TREE_STUMP_9714,
        Trees.MAGIC_TREE, TREE_STUMP_9713,
        Trees.REDWOOD, REDWOOD_29671
    );

    private static final Map<Item, Trees> LEAF_MAP = Map.of(
        new Item(ItemIdentifiers.LEAVES), Trees.TREE,
        new Item(ItemIdentifiers.OAK_LEAVES), Trees.OAK_TREE,
        new Item(ItemIdentifiers.WILLOW_LEAVES), Trees.WILLOW_TREE,
        new Item(ItemIdentifiers.MAPLE_LEAVES), Trees.MAPLE_TREE,
        new Item(ItemIdentifiers.YEW_LEAVES), Trees.YEW_TREE,
        new Item(ItemIdentifiers.MAGIC_LEAVES), Trees.MAGIC_TREE
    );

    public static boolean success(int woodcuttingLevel, Trees tree, Axe axe) {
        double successChance = calculateSuccessChance(woodcuttingLevel, tree, axe);
        double randomValue = random.nextDouble();
        return successChance > randomValue;
    }

    public static double calculateSuccessChance(int level, Trees tree, Axe type) {
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

    public static void collapseTree(Player player, Trees tree, int trunkObjectId) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
        GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
        GameObject spawned = new GameObject(trunkObjectId, obj.tile(), obj.getType(), obj.getRotation());
        ObjectManager.replace(old, spawned, tree.cycle);
        addExperience(player, tree);
    }

    public static void cut(Player player, Trees tree, int trunkObjectId) {

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

        player.animate(axe.anim);

        player.repeatingTask(4, t -> {
            if (stopTask(player, tree, trunkObjectId, t)) return;

            player.animate(axe.anim);

            int modifiedLevel = woodcuttingLevel;
            experienceMultiplier = 15;

            if (player.getEquipment().containsAny(SkillingItems.WOODCUTTING_CAPE.getId())) {
                modifiedLevel += 1;
            }

            if (player.tile().inArea(WoodcuttingGuild.AREA_EAST) || player.tile().inArea(WoodcuttingGuild.AREA_WEST)) {
                modifiedLevel += 7;
            }

            var success = success(modifiedLevel, tree, axe);

            if (success) {
                if (hasInfernalAxe(player, tree)) return;
                int[] fellingAxes = {28196, 28199, 28202, 28205, 28208, 28211, 28214, 28217, 28220, 28226};
                if (player.getInventory().contains(ItemIdentifiers.FORESTERS_RATION)) {
                    boolean hasValidAxe = (player.getInventory().containsAny(fellingAxes) || player.getEquipment().containsAny(fellingAxes)) && ArrayUtils.contains(fellingAxes, axe.id);
                    if (hasValidAxe && player.getSkills().level(Skills.WOODCUTTING) >= axe.level) {
                        player.inventory().remove(ItemIdentifiers.FORESTERS_RATION, 1);
                        experienceMultiplier = experienceMultiplier * 1.10;
                        if (!Utils.rollDice(20)) {
                            addLog(player, tree);
                            checkBonus(player, tree);
                            addExperience(player, tree);
                            return;
                        }
                        if (tree.leaves != null) player.getInventory().add(new Item(tree.leaves.getId(), tree.leaves.getAmount()));
                        checkBonus(player, tree);
                        addExperience(player, tree);
                        return;
                    }
                }
                if (tree.leaves != null) player.getInventory().add(new Item(tree.leaves.getId(), tree.leaves.getAmount()));
                addLog(player, tree);
                checkBonus(player, tree);
                addExperience(player, tree);
            }
        });
    }

    private static void addLog(Player player, Trees tree) {
        player.inventory().add(new Item(tree.item));
    }

    private static void addExperience(Player player, Trees tree) {
        player.getSkills().addXp(Skills.WOODCUTTING, tree.experience);
    }

    private static boolean stopTask(Player player, Trees tree, int trunkObjectId, Task t) {
        if (player.getMovementQueue().hasMoved()) {
            player.stopActions(false);
            t.stop();
            return true;
        }

        if (player.inventory().isFull()) {
            player.looks().resetRender();
            player.stopActions(false);
            player.message("Your inventory is too full to hold any more logs.");
            t.stop();
            return true;
        }

        if (Utils.rollDie(12, 1)) {
            player.stopActions(false);
            addLog(player, tree);
            collapseTree(player, tree, trunkObjectId);
            t.stop();
            return true;
        }
        return false;
    }

    private static boolean hasInfernalAxe(Player player, Trees tree) {
        if (FormulaUtils.hasInfernalAxe(player)) {
            if (Utils.rollDie(3, 1)) {
                LogLighting.LightableLog log = LogLighting.LightableLog.logForId(tree.item);
                if (log != null) {
                    player.graphic(580, GraphicHeight.MIDDLE, 0);
                    player.getSkills().addXp(Skills.FIREMAKING, log.xp / 2);
                    return true;
                }
            }
        }
        return false;
    }

    private static void checkBonus(Player player, Trees tree) {
        if (player.getEquipment().containsAny(SkillingItems.KANDARIN_HELM.getId())) {
            if (Utils.rollDice(10)) {
                player.inventory().add(new Item(tree.item, 1));
            }
        } else if (player.getInventory().contains(ItemIdentifiers.NATURE_OFFERINGS)) {
            if (Utils.rollDie(100, Utils.random(60, 80))) {
                player.inventory().add(new Item(tree.item, 1));
                player.getInventory().remove(ItemIdentifiers.NATURE_OFFERINGS, 1);
            }
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            return cut(player, obj);
        }
        return false;
    }

    private boolean cut(Player player, GameObject obj) {
        for (var tree : Trees.values()) {
            if (tree == null) {
                break;
            }
            for (var object : tree.getObjects()) {
                if (object == -1) {
                    break;
                }
                if (obj.getId() == object) {
                    var trunk = TREE_MAP.get(tree);
                    if (trunk != null) {
                        cut(player, tree, trunk);
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
