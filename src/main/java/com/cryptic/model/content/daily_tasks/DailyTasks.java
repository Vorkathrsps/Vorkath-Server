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
import static com.cryptic.utility.CustomItemIdentifiers.*;
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
        "Daily Mining",
        "",
        100,
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
        PRAYER_DAILY_COMPLETION_AMOUNT,
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
    },

    REV_CAVE_KILLS("Rev Cave PvP", "Kill 15 players in the <br> Revenant cave.", 15, REVENANT_CAVE_PVP_DAILY_TASK_COMPLETION_AMOUNT, REVENANT_CAVE_PVP_DAILY_TASK_COMPLETED, REVENANT_CAVE_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(ANCIENT_MEDALLION, 1), new

        Item(ANCIENT_STATUETTE, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    DEEP_WILDERNESS("Deep Wild", "Kill 15 players above level 30<br>wilderness.", 15, DEEP_WILD_PVP_DAILY_TASK_COMPLETION_AMOUNT, DEEP_WILD_PVP_DAILY_TASK_COMPLETED, DEEP_WILD_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(DONATOR_MYSTERY_BOX, 1), new

        Item(BLOOD_MONEY, 35_000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    PURE_KILLS("Pure PvP", "Kill 15 players as a pure in<br>Edgeville.", 15, PURE_PVP_DAILY_TASK_COMPLETION_AMOUNT, PURE_PVP_DAILY_TASK_COMPLETED, PURE_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(BLOOD_MONEY, 75_000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    ZERKER_KILLS("Zerker PvP", "Kill 15 players as a zerker in<br>Edgeville.", 15, ZERKER_PVP_DAILY_TASK_COMPLETION_AMOUNT, ZERKER_PVP_DAILY_TASK_COMPLETED, ZERKER_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(BLOOD_MONEY, 75_000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    TIER_UPGRADE("Target Hunt", "Upgrade 3 emblems to a tier<br>10 emblem.", 3, TIER_UPGRADE_DAILY_TASK_COMPLETION_AMOUNT, TIER_UPGRADE_DAILY_TASK_COMPLETED, TIER_UPGRADE_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(DONATOR_MYSTERY_BOX, 1), new

        Item(ANTIQUE_EMBLEM_TIER_10, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    NO_ARM("Armourless", "Kill 15 players without wearing a<br>body and legs.", 15, NO_ARM_DAILY_TASK_COMPLETION_AMOUNT, NO_ARM_DAILY_TASK_COMPLETED, NO_ARM_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(BLOOD_MONEY, 15000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    DHAROK("Dharok", "Kill 10 players using Dharok's<br>armour. You do not have to wield<br>the greataxe.", 10, DHAROK_DAILY_TASK_COMPLETION_AMOUNT, DHAROK_DAILY_TASK_COMPLETED, DHAROK_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(WEAPON_MYSTERY_BOX, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    BOTS("Bots", "Kill 50 bots.", 50, BOTS_DAILY_TASK_COMPLETION_AMOUNT, BOTS_DAILY_TASK_COMPLETED, BOTS_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(BLOOD_MONEY, 10_000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    TOURNEY_PARTICIPATION("Tourney", "Participate in 2 daily tournaments.", 2, TOURNEY_PARTICIPATION_DAILY_TASK_COMPLETION_AMOUNT, TOURNEY_PARTICIPATION_DAILY_TASK_COMPLETED, TOURNEY_PARTICIPATION_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(BLOOD_MONEY, 10000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    DAILY_RAIDS("Daily Raiding", "Complete 5 raids.", 5, DAILY_RAIDS_DAILY_TASK_COMPLETION_AMOUNT, DAILY_RAIDS_DAILY_TASK_COMPLETED, DAILY_RAIDS_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(WEAPON_MYSTERY_BOX, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    WORLD_BOSS("Daily World boss", "Kill 3 world bosses.", 3, WORLD_BOSS_DAILY_TASK_COMPLETION_AMOUNT, WORLD_BOSS_DAILY_TASK_COMPLETED, WORLD_BOSS_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(BLOOD_MONEY, 15000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    REVENANTS("Daily Revenants", "Kill 100 revenants", 100, DAILY_REVENANTS_TASK_COMPLETION_AMOUNT, DAILY_REVENANTS_TASK_COMPLETED, DAILY_REVENANTS_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(ANCIENT_RELIC, 1), new

        Item(ANCIENT_EFFIGY, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    BATTLE_MAGE("Daily Battle mage", "Kill 100 battle mages.", 100, BATTLE_MAGE_DAILY_TASK_COMPLETION_AMOUNT, BATTLE_MAGE_DAILY_TASK_COMPLETED, BATTLE_MAGE_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(STAFF_OF_THE_DEAD, 1), new

        Item(OCCULT_NECKLACE, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    WILDERNESS_BOSS("Daily Wildy Boss", "Kill 50 wilderness bosses.<br>The following bosses count:<br>Callisto, Venenatis, Scorpia,<br>Chaos Ele, Barrelchest,<br>and Vet'ion.", 50, WILDERNESS_BOSS_DAILY_TASK_COMPLETION_AMOUNT, WILDERNESS_BOSS_DAILY_TASK_COMPLETED, WILDERNESS_BOSS_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(WEAPON_MYSTERY_BOX, 1), new

        Item(ARMOUR_MYSTERY_BOX, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    ZULRAH("Daily Zulrah", "Kill 25 Zulrah.", 25, ZULRAH_DAILY_TASK_COMPLETION_AMOUNT, ZULRAH_DAILY_TASK_COMPLETED, ZULRAH_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(BLOOD_MONEY, 20000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    SLAYER("Daily Slayer", "Complete 15 wilderness tasks.", 15, SLAYER_DAILY_TASK_COMPLETION_AMOUNT, SLAYER_DAILY_TASK_COMPLETED, SLAYER_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(SLAYER_KEY, 5)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    CORRUPTED_NECHRYARCHS("Daily Nechryarch", "Kill 10 Corrupted Nechryarchs.", 10, CORRUPTED_NECHRYARCHS_DAILY_TASK_COMPLETION_AMOUNT, CORRUPTED_NECHRYARCHS_DAILY_TASK_COMPLETED, CORRUPTED_NECHRYARCHS_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(WEAPON_MYSTERY_BOX, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    VORKATH("Daily Vorkath", "Kill 10 Vorkaths.", 10, VORKATH_DAILY_TASK_COMPLETION_AMOUNT, VORKATH_DAILY_TASK_COMPLETED, VORKATH_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(BLOOD_MONEY, 30000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    CORPOREAL_BEAST("Daily Corp", "Kill 10 Corporeal Beasts.", 10, CORPOREAL_BEAST_DAILY_TASK_COMPLETION_AMOUNT, CORPOREAL_BEAST_DAILY_TASK_COMPLETED, CORPOREAL_BEAST_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(DONATOR_MYSTERY_BOX), new

        Item(BLESSED_SPIRIT_SHIELD, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    WILDY_AGLITY("Wildy Runner", "Complete 25 laps of wilderness <br> agility.", 25, WILDY_RUNNER_DAILY_TASK_COMPLETION_AMOUNT, WILDY_RUNNER_DAILY_TASK_COMPLETED, WILDY_RUNNER_DAILY_TASK_REWARD_CLAIMED, TaskCategory.OTHER, new Item(BLOOD_MONEY, 25000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    };

    public String taskName;
    public String taskDescription;
    public int completionAmount;
    public final AttributeKey key;
    public final AttributeKey completed;
    public final AttributeKey rewardClaimed;
    public final TaskCategory category;
    public final Item[] rewards;
    public Skill type;
    public List<Integer> requirements;
    public static final DailyTasks[] values = values();

    DailyTasks(Skill type, List<Integer> requirements, String taskName, String taskDescription, int completionAmount, AttributeKey key, AttributeKey completed, AttributeKey rewardClaimed, TaskCategory category, Item... rewards) {
        this.type = type;
        this.requirements = requirements;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.completionAmount = completionAmount;
        this.key = key;
        this.completed = completed;
        this.rewardClaimed = rewardClaimed;
        this.category = category;
        this.rewards = rewards;
    }

    DailyTasks(String taskName, String taskDescription, int completionAmount, AttributeKey key, AttributeKey completed, AttributeKey rewardClaimed, TaskCategory category, Item... rewards) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.completionAmount = completionAmount;
        this.key = key;
        this.completed = completed;
        this.rewardClaimed = rewardClaimed;
        this.category = category;
        this.rewards = rewards;
    }

    public static List<DailyTasks> asList(TaskCategory category) {
        return Arrays.stream(values()).filter(a -> a.category == category).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
    }

    public static DailyTasks generate(Player player, DailyTasks task) {
        if (task.type != null) {
            int highestRequirement = getHighestRequirement(player, task.type);
            int randomAmount = World.getWorld().random(25, 100);
            if (task.type.equals(Skill.COOKING)) return findCookingType(task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.WOODCUTTING)) return findWoodcuttingType(task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.SLAYER)) return findSlayerType(player, task);
            if (task.type.equals(Skill.MINING)) return findMiningType(task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.FISHING)) return findFishingType(task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.THIEVING)) return findThievingType(task, highestRequirement, randomAmount);
            if (task.type.equals(Skill.PRAYER)) return findPrayerType(task, randomAmount);
        }
        if (task.equals(DailyTasks.BOSSING)) return findBossType(task);
        return null;
    }

    public static DailyTasks findBossType(DailyTasks task) {
        List<BossKillLog.Bosses> temp = new ArrayList<>(Arrays.asList(BossKillLog.Bosses.values));
        BossKillLog.Bosses boss = get(temp);
        int randomAmount = World.getWorld().random(5, 15);
        task.taskName = "Daily " + Utils.formatEnum(boss.name());
        task.taskDescription = "Kill " + randomAmount + " " + Utils.formatEnum(boss.name()) + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    public static DailyTasks findPrayerType(DailyTasks task, int randomAmount) {
        List<Bone> temp = new ArrayList<>(Arrays.asList(Bone.values));
        Bone bone = get(temp);
        task.taskName = "Daily " + Utils.formatEnum(bone.name());
        task.taskDescription = "Sacrifice " + randomAmount + " " + Utils.formatEnum(bone.name()) + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    public static DailyTasks findThievingType(DailyTasks task, int highestRequirement, int randomAmount) {
        List<Pickpocketing.PickPocket> temp = new ArrayList<>();
        for (var thieving : Pickpocketing.PickPocket.values) {
            if (thieving.levelReq <= highestRequirement) {
                temp.add(thieving);
            }
        }
        Pickpocketing.PickPocket thieving = get(temp);
        task.taskName = "Daily " + Utils.formatEnum(thieving.name());
        task.taskDescription = "Pickpocket " + randomAmount + " " + Utils.formatEnum(thieving.name()) + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    public static DailyTasks findFishingType(DailyTasks task, int highestRequirement, int randomAmount) {
        List<Fish> temp = new ArrayList<>();
        for (var fish : Fish.values) {
            if (fish.lvl <= highestRequirement) {
                temp.add(fish);
            }
        }
        Fish fish = get(temp);
        task.taskName = "Daily " + Utils.formatEnum(fish.name());
        task.taskDescription = "Fish " + randomAmount + " " + Utils.formatEnum(fish.name()) + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    public static DailyTasks findMiningType(DailyTasks task, int highestRequirement, int randomAmount) {
        List<Ore> temp = new ArrayList<>();
        for (var ore : Ore.values) {
            if (ore.level_req <= highestRequirement) {
                temp.add(ore);
            }
        }
        Ore ore = get(temp);
        task.taskName = "Daily " + Utils.formatEnum(ore.name());
        task.taskDescription = "Mine " + randomAmount + " " + Utils.formatEnum(ore.name()) + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    private static DailyTasks findSlayerType(Player player, DailyTasks task) { //TODO make nieve assign whatever your daily slayer may be
        List<SlayerTask> temp = new ArrayList<>();
        for (SlayerTask slayerTask : SlayerTask.cached) {
            if (slayerTask.hasTaskRequirements(player, slayerTask)) {
                temp.add(slayerTask);
            }
        }

        SlayerTask slayerTask = get(temp);
        final int amount = World.getWorld().random(1, 2);
        task.taskName = "Daily " + Utils.formatEnum(slayerTask.getTaskName());
        task.taskDescription = "Complete " + amount + " " + Utils.formatEnum(slayerTask.getTaskName()) + " Slayer Task.";
        task.completionAmount = amount;
        return task;
    }

    private static DailyTasks findCookingType(DailyTasks task, int highestRequirement, int randomAmount) {
        List<Cookable> temp = new ArrayList<>();
        for (var cookable : Cookable.values) {
            if (cookable.lvl <= highestRequirement) {
                temp.add(cookable);
            }
        }

        Cookable cookable = get(temp);
        task.taskName = "Daily " + Utils.formatEnum(cookable.name());
        task.taskDescription = "Cook " + randomAmount + " " + Utils.formatEnum(cookable.name()) + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    private static DailyTasks findWoodcuttingType(DailyTasks task, int highestRequirement, int randomAmount) {
        List<Trees> temp = new ArrayList<>();
        for (var tree : Trees.values) {
            if (tree.level <= highestRequirement) {
                temp.add(tree);
            }
        }

        Trees tree = get(temp);
        task.taskName = "Daily " + Utils.formatEnum(tree.name());
        task.taskDescription = "Chop " + randomAmount + " " + tree.name + ".";
        task.completionAmount = randomAmount;
        return task;
    }

    public static int getHighestRequirement(Player player, Skill type) {
        List<Integer> list = build(player, type).reversed();
        return list.getFirst();
    }

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
            ", completionAmount=" + completionAmount +
            ", key=" + key +
            ", completed=" + completed +
            ", rewardClaimed=" + rewardClaimed +
            ", category=" + category +
            ", rewards=" + Arrays.toString(rewards) +
            ", type=" + type +
            ", requirements=" + requirements +
            '}';
    }
}
