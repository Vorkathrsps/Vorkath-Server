package com.aelous.model.content.daily_tasks;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | June, 15, 2021, 16:06
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public enum DailyTasks {


    EDGEVILE_KILLS("Edge PvP", "Kill 15 players in Edgeville.", 15, EDGE_PVP_DAILY_TASK_COMPLETION_AMOUNT, EDGE_PVP_DAILY_TASK_COMPLETED, EDGE_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(BLOOD_MONEY, 50_000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    REV_CAVE_KILLS("Rev Cave PvP", "Kill 15 players in the Revenant<br>cave.", 15, REVENANT_CAVE_PVP_DAILY_TASK_COMPLETION_AMOUNT, REVENANT_CAVE_PVP_DAILY_TASK_COMPLETED, REVENANT_CAVE_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(ANCIENT_MEDALLION, 1), new Item(ANCIENT_STATUETTE, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    DEEP_WILDERNESS("Deep Wild", "Kill 15 players above level 30<br>wilderness.", 15, DEEP_WILD_PVP_DAILY_TASK_COMPLETION_AMOUNT, DEEP_WILD_PVP_DAILY_TASK_COMPLETED, DEEP_WILD_PVP_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(DONATOR_MYSTERY_BOX, 1), new Item(BLOOD_MONEY, 35_000)) {
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

    TIER_UPGRADE("Target Hunt", "Upgrade 3 emblems to a tier<br>10 emblem.", 3, TIER_UPGRADE_DAILY_TASK_COMPLETION_AMOUNT, TIER_UPGRADE_DAILY_TASK_COMPLETED, TIER_UPGRADE_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVP, new Item(DONATOR_MYSTERY_BOX, 1), new Item(ANTIQUE_EMBLEM_TIER_10, 1)) {
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

    REVENANTS("Daily Revenants", "Kill 100 revenants", 100, DAILY_REVENANTS_TASK_COMPLETION_AMOUNT, DAILY_REVENANTS_TASK_COMPLETED, DAILY_REVENANTS_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(ANCIENT_RELIC, 1), new Item(ANCIENT_EFFIGY, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    BATTLE_MAGE("Daily Battle mage", "Kill 100 battle mages.", 100, BATTLE_MAGE_DAILY_TASK_COMPLETION_AMOUNT, BATTLE_MAGE_DAILY_TASK_COMPLETED, BATTLE_MAGE_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(STAFF_OF_THE_DEAD, 1), new Item(OCCULT_NECKLACE, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    WILDERNESS_BOSS("Daily Wildy Boss", "Kill 50 wilderness bosses.<br>The following bosses count:<br>Callisto, Venenatis, Scorpia,<br>Chaos Ele, Barrelchest,<br>and Vet'ion.", 50, WILDERNESS_BOSS_DAILY_TASK_COMPLETION_AMOUNT, WILDERNESS_BOSS_DAILY_TASK_COMPLETED, WILDERNESS_BOSS_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM, new Item(WEAPON_MYSTERY_BOX, 1), new Item(ARMOUR_MYSTERY_BOX, 1)) {
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

    CORPOREAL_BEAST("Daily Corp", "Kill 10 Corporeal Beasts.", 10, CORPOREAL_BEAST_DAILY_TASK_COMPLETION_AMOUNT, CORPOREAL_BEAST_DAILY_TASK_COMPLETED, CORPOREAL_BEAST_DAILY_TASK_REWARD_CLAIMED, TaskCategory.PVM,new Item(DONATOR_MYSTERY_BOX), new Item(BLESSED_SPIRIT_SHIELD, 1)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    },

    WILDY_AGLITY("Wildy Runner", "Complete 25 laps of wilderness<br>agility.", 25, WILDY_RUNNER_DAILY_TASK_COMPLETION_AMOUNT, WILDY_RUNNER_DAILY_TASK_COMPLETED, WILDY_RUNNER_DAILY_TASK_REWARD_CLAIMED, TaskCategory.OTHER, new Item(BLOOD_MONEY, 25000)) {
        @Override
        public boolean canIncrease(Player player) {
            return !player.<Boolean>getAttribOr(completed, false);
        }
    };

    public final String taskName;
    public final String taskDescription;
    public final int completionAmount;
    public final AttributeKey key;
    public final AttributeKey completed;
    public final AttributeKey rewardClaimed;
    public final TaskCategory category;
    public final Item[] rewards;

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

    public abstract boolean canIncrease(Player player);
}
