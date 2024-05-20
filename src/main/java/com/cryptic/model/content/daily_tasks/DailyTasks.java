package com.cryptic.model.content.daily_tasks;

import com.cryptic.model.World;
import com.cryptic.model.content.kill_logs.BossKillLog;
import com.cryptic.model.content.skill.impl.cooking.Cookable;
import com.cryptic.model.content.skill.impl.fishing.Fish;
import com.cryptic.model.content.skill.impl.mining.Ore;
import com.cryptic.model.content.skill.impl.prayer.Bone;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.content.skill.impl.thieving.Pickpocketing;
import com.cryptic.model.content.skill.impl.woodcutting.impl.Trees;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin | June, 15, 2021, 16:06
 */
public enum DailyTasks {
    WOODCUTTING(Skill.WOODCUTTING,

        List.of(
            1,
            15,
            30,
            35,
            45,
            50,
            60,
            75,
            90
        ),

        "Daily Woodcutting",
        "Chop 100 magic logs.",
        100,
        DAILY_WOODCUTTING_NAME, DAILY_WOODCUTTING_DESC,
        WOODCUTTING_DAILY_TASK_COMPLETION_AMOUNT,
        WOODCUTTING_DAILY_TASK_COMPLETED,
        WOODCUTTING_DAILY_TASK_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },
    COOKING(Skill.COOKING,

        List.of
            (
                1,
                5,
                10,
                15,
                20,
                25,
                28,
                30,
                40,
                43,
                45,
                53,
                62,
                80,
                82,
                84,
                85,
                91
            ),

        "Daily Cooking",
        "Cook 250 Monkfish.",
        250,
        DAILY_COOKING_NAME, DAILY_COOKING_DESC,
        MONKFISH_DAILY_TASK_COMPLETION_AMOUNT,
        MONKFISH_DAILY_TASK_COMPLETED,
        MONKFISH_DAILY_TASK_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    DAILY_SLAYER(Skill.SLAYER,
        List.of
            (
                1
            ),
        "Daily Slayer",
        "",
        100,
        DAILY_SLAYER_NAME, DAILY_SLAYER_DESC,
        SLAYER_DAILY_TASK_COMPLETION_AMOUNT,
        SLAYER_DAILY_TASK_COMPLETED,
        SLAYER_DAILY_TASK_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    MINING(Skill.MINING,
        List.of
            (
                1,
                15,
                20,
                30,
                40,
                55,
                65,
                70,
                92
            ),
        "Daily Fishing",
        "",
        100,
        DAILY_MINING_NAME, DAILY_MINING_DESC,
        MINING_DAILY_COMPLETION_AMOUNT,
        MINING_DAILY_COMPLETED,
        MINING_DAILY_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    FISHING(Skill.FISHING,
        List.of
            (
                1,
                5,
                10,
                15,
                16,
                20,
                23,
                25,
                28,
                30,
                35,
                38,
                40,
                46,
                50,
                53,
                62,
                65,
                76,
                79,
                80,
                81,
                82,
                85,
                87
            ),
        "Daily Fishing",
        "",
        100,
        DAILY_FISHING_NAME, DAILY_FISHING_DESC,
        FISHING_DAILY_COMPLETION_AMOUNT,
        FISHING_DAILY_COMPLETED,
        FISHING_DAILY_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },


    THIEVING(Skill.THIEVING,
        List.of
            (
                1,
                10,
                15,
                25,
                32,
                38,
                40,
                53,
                55,
                70,
                75,
                80,
                85,
                90
            ),
        "Daily Thieving",
        "",
        100,
        DAILY_THIEVING_NAME, DAILY_THIEVING_DESC,
        THIEVING_DAILY_COMPLETION_AMOUNT,
        THIEVING_DAILY_COMPLETED,
        THIEVING_DAILY_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    PRAYER(Skill.PRAYER,
        List.of
            (
                1
            ),
        "Daily Prayer",
        "",
        100,
        DAILY_BONE_NAME, DAILY_BONE_DESC,
        PRAYER_DAILY_COMPLETION_AMOUNT, // OH this is ur dynamic value hold on
        PRAYER_DAILY_COMPLETED,
        PRAYER_DAILY_REWARD_CLAIMED,
        TaskCategory.OTHER,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    BOSSING(
        "Daily Bossing",
        "",
        100,
        DAILY_BOSS_NAME, DAILY_BOSS_DESC,
        BOSSING_DAILY_COMPLETION_AMOUNT,
        BOSSING_DAILY_COMPLETED,
        BOSSING_DAILY_REWARD_CLAIMED,
        TaskCategory.PVM,
        new Item(COINS_995, 500000),
        new Item(28551, 1)
    ) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    };

    public final String taskName;
    public final String taskDescription;
    public final int maximumAmt; // TODO replace with intrange or int high, int low
    public final AttributeKey assignmentName;
    public final AttributeKey assignmentDesc;
    public final AttributeKey completionAmt;
    public final AttributeKey completed;
    public final AttributeKey rewardClaimed;
    public final TaskCategory category;
    public final Item[] rewards;
    /**
     * IDENTIFIER
     */
    public final Skill type;
    public final List<Integer> requirements;
    public static final DailyTasks[] values = values();

    DailyTasks(Skill type, List<Integer> requirements, String taskName, String taskDescription, int maximumAmt,      AttributeKey name,
               AttributeKey desc, AttributeKey completionAmt, AttributeKey completed, AttributeKey rewardClaimed, TaskCategory category, Item... rewards) {
        this.type = type;
        this.requirements = requirements;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.maximumAmt = maximumAmt;
        this.assignmentName = name;
        this.assignmentDesc = desc;
        this.completionAmt = completionAmt;
        this.completed = completed;
        this.rewardClaimed = rewardClaimed;
        this.category = category;
        this.rewards = rewards;
    }

    DailyTasks(String taskName, String taskDescription, int maximumAmt,
               AttributeKey name,
               AttributeKey desc,
               AttributeKey completionAmt, AttributeKey completed, AttributeKey rewardClaimed, TaskCategory category, Item... rewards) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.maximumAmt = maximumAmt;
        this.completionAmt = completionAmt;
        this.completed = completed;
        this.rewardClaimed = rewardClaimed;
        this.category = category;
        this.type = null;
        this.requirements = null;
        this.rewards = rewards;
        this.assignmentName = name;
        this.assignmentDesc = desc;
    }

    public static List<DailyTasks> asList(TaskCategory category) {
        return Arrays.stream(values()).filter(a -> a.category == category).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
    }

    public static DailyTasks verifyCanPerform(Player player, DailyTasks task) {
        if (task.type != null) {
            int highestRequirement = getHighestRequirement(player, task.type);
            int randomAmount = World.getWorld().random(Math.max(25, task.maximumAmt - 25), task.maximumAmt);
            if (task.type.equals(Skill.COOKING)) return findCookingType(player, task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.WOODCUTTING))
                return findWoodcuttingType(player, task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.SLAYER)) return findSlayerType(player, task);
            if (task.type.equals(Skill.MINING)) return findMiningType(player, task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.FISHING)) return findFishingType(player, task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.THIEVING))
                return findThievingType(player, task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.PRAYER)) return findPrayerType(player, task, randomAmount);
        }
        if (task.equals(DailyTasks.BOSSING)) return findBossType(player, task);
        return null;
    }

    public static DailyTasks findBossType(Player player, DailyTasks task) {
        List<BossKillLog.Bosses> temp = new ArrayList<>(Arrays.asList(BossKillLog.Bosses.values));
        BossKillLog.Bosses boss = (BossKillLog.Bosses) get(temp);
        int randomAmount = World.getWorld().random(5, 15); // leaving for now wont use enuum.max
        task.completionAmt.set(player, randomAmount); // hereee we go
        return task;
    }

    public static DailyTasks findPrayerType(Player player, DailyTasks task, int randomAmount) {
        List<Bone> temp = new ArrayList<>(Arrays.asList(Bone.values));
        Bone bone = (Bone) get(temp);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(bone.name()));
        task.assignmentDesc.set(player, "Sacrifice " + randomAmount + " " + Utils.formatEnum(bone.name()) + ".");
        task.completionAmt.set(player, randomAmount); // ye follow this syntax and add 2x6 new attribs for the other 6 skill things. ight pce
        return task;
    }

    public static DailyTasks findThievingType(Player player, DailyTasks task, int highestRequirement, int randomAmount) {
        List<Pickpocketing.PickPocket> temp = new ArrayList<>();
        for (var thieving : Pickpocketing.PickPocket.values) {
            if (thieving.levelReq <= highestRequirement) {
                temp.add(thieving);
            }
        }
        Pickpocketing.PickPocket thieving = (Pickpocketing.PickPocket) get(temp);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(thieving.name()));
        task.assignmentDesc.set(player, "Pickpocket " + randomAmount + " " + Utils.formatEnum(thieving.name()) + ".");
        task.completionAmt.set(player, randomAmount);
        return task;
    }

    public static DailyTasks findFishingType(Player player, DailyTasks task, int highestRequirement, int randomAmount) {
        List<Fish> temp = new ArrayList<>();
        for (var fish : Fish.values) {
            if (fish.lvl <= highestRequirement) {
                temp.add(fish);
            }
        }
        Fish fish = (Fish) get(temp);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(fish.name()));
        task.assignmentDesc.set(player, "Fish " + randomAmount + " " + Utils.formatEnum(fish.name()) + ".");
        task.completionAmt.set(player, randomAmount);
        return task;
    }

    public static DailyTasks findMiningType(Player player, DailyTasks task, int highestRequirement, int randomAmount) {
        List<Ore> temp = new ArrayList<>();
        for (var ore : Ore.values) {
            if (ore.level_req <= highestRequirement) {
                temp.add(ore);
            }
        }
        Ore ore = (Ore) get(temp);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(ore.name()));
        task.assignmentDesc.set(player, "Mine " + randomAmount + " " + Utils.formatEnum(ore.name()) + ".");
        task.completionAmt.set(player, randomAmount);
        return task;
    }

    private static DailyTasks findSlayerType(Player player, DailyTasks task) { //TODO make nieve assign whatever your daily slayer may be
        List<SlayerTask> temp = new ArrayList<>();
        for (SlayerTask slayerTask : SlayerTask.cached) {
            if (slayerTask.hasTaskRequirements(player, slayerTask)) {
                temp.add(slayerTask);
            }
        }

        SlayerTask slayerTask = (SlayerTask) get(temp);
        final int amount = World.getWorld().random(1, 2);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(slayerTask.getTaskName()));
        task.assignmentDesc.set(player, "Complete " + amount + " " + Utils.formatEnum(slayerTask.getTaskName()) + " Slayer Task.");
        task.completionAmt.set(player, amount);
        return task;
    }

    private static DailyTasks findCookingType(Player player, DailyTasks task, int highestRequirement, int randomAmount) {
        List<Cookable> temp = new ArrayList<>();
        for (var cookable : Cookable.values) {
            if (cookable.lvl <= highestRequirement) {
                temp.add(cookable);
            }
        }

        Cookable cookable = (Cookable) get(temp);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(cookable.name()));
        task.assignmentDesc.set(player, "Cook " + randomAmount + " " + Utils.formatEnum(cookable.name()) + ".");
        task.completionAmt.set(player, randomAmount);
        return task;
    }

    /**
     * Handler for Woodcutting (Enum) Type
     *
     * @param player
     * @param task
     * @param highestRequirement
     * @param randomAmount
     * @return
     */
    private static DailyTasks findWoodcuttingType(Player player, DailyTasks task, int highestRequirement, int randomAmount) {
        List<Trees> temp = new ArrayList<>();
        for (var tree : Trees.values) {
            if (tree.level <= highestRequirement) {
                temp.add(tree);
            }
        }

        Trees tree = (Trees) get(temp);
        task.assignmentName.set(player, "Daily " + Utils.formatEnum(tree.name()));
        task.assignmentDesc.set(player, "Chop " + randomAmount + " " + tree.name + ".");
        task.completionAmt.set(player, randomAmount);
        return task;
    }

    /**
     * Gets highest requirement for task (threshold so you dont exceed task level reqs only
     * provid the player with what they are eligible to do)
     *
     * @param player
     * @param type
     * @return
     */
    public static int getHighestRequirement(Player player, Skill type) {
        List<Integer> list = build(player, type).reversed();
        return list.getFirst();
    }

    /**
     * Builds task level requirements adds to an arraylist
     *
     * @param player
     * @param type
     * @return
     */
    public static List<Integer> build(Player player, Skill type) {
        int level = player.getSkills().level(type.getId());
        List<Integer> temp = new ArrayList<>();
        for (var task : values) {
            if (task.type == type) {
                for (var requirement : task.requirements) {
                    if (requirement > level) continue;
                    temp.add(requirement);
                }
            }
        }
        return temp;
    }

    /**
     * Returns Enum <T> @Value</T>
     *
     * @param temp
     * @param <T>
     * @return
     */
    private static <T> T get(List<T> temp) {
        Collections.shuffle(temp);
        temp = temp.reversed();
        Collections.shuffle(temp);
        return Utils.randomElement(temp);
    }

    public abstract boolean canIncrease(Player player);

    @Override
    public String toString() {
        return "DailyTasks{" +
            "taskName='" + taskName + '\'' +
            ", taskDescription='" + taskDescription + '\'' +
            ", completionAmount=" + maximumAmt +
            ", key=" + completionAmt +
            ", completed=" + completed +
            ", rewardClaimed=" + rewardClaimed +
            ", category=" + category +
            ", rewards=" + Arrays.toString(rewards) +
            ", type=" + type +
            ", requirements=" + requirements +
            '}';
    }
}
