package com.cryptic.model.content.skill.impl.slayer;

import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.skill.impl.slayer.master.SlayerMaster;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.content.skill.impl.slayer.superior_slayer.SuperiorSlayer;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.player.GameMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.QuestTab;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.cryptic.model.entity.player.QuestTab.InfoTab.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author PVE
 * @Since juli 20, 2020
 */
public class Slayer {
    private static final Logger logger = LogManager.getLogger(Slayer.class);
    private static List<SlayerMaster> masters = new ArrayList<>();

    public static SlayerMaster master(int npc) {
        for (SlayerMaster master : masters) {
            if (master.npcId == npc) {
                return master;
            }
        }
        return null;
    }

    public static int findIdByMaster(int npc) {
        for (SlayerMaster master : masters) {
            if (master.npcId == npc) {
                return master.id;
            }
        }
        return 0;
    }

    public static SlayerMaster lookup(int id) {
        for (SlayerMaster master : masters) {
            if (master.id == id) {
                return master;
            }
        }
        return null;
    }

    public void loadMasters() {
        long start = System.currentTimeMillis();
        // Load all masters from the json file
        masters = Utils.jsonArrayToList(Paths.get("data", "def", "slayermasters.json"), SlayerMaster[].class);

        if (masters == null) return;
        // Verify integrity, make sure matches are made.
        masters.forEach(master -> master.defs.forEach(taskdef -> {
            if (taskdef != null) {
                if (SlayerCreature.lookup(taskdef.getCreatureUid()) == null) {
                    throw new RuntimeException("could not load slayer task def " + taskdef.getCreatureUid() + " could not resolve uid; " + master.npcId);
                }
            }
        }));
        long elapsed = System.currentTimeMillis() - start;
        logger.info("Loaded slayer masters for {}. It took {}ms.", "./data/def/slayermasters.json", elapsed);
    }

    public static boolean creatureMatches(Player player, int id) {
        SlayerCreature task = SlayerCreature.lookup(player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
        return task != null && task.matches(id);
    }

    public static String taskName(int id) {
        return SlayerCreature.lookup(id) != null ? Utils.formatEnum(SlayerCreature.lookup(id).name()) : "None";
    }

    public static void displayCurrentAssignment(Player player) {
        String name = taskName(player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
        int num = player.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);
        if (num == 0) {
            player.message("You currently have no active slayer task.");
        }
        if (num == 0) {
            player.getPacketSender().sendString(63208, "None");
        } else {
            player.getPacketSender().sendString(63208, "" + num + " x " + name);
        }
    }

    public static void cancelTask(Player player, boolean adminCancel) {
        if (adminCancel) {
            player.putAttrib(AttributeKey.SLAYER_TASK_ID, 0);
            player.putAttrib(AttributeKey.SLAYER_TASK_AMT, 0);
            player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
            return;
        }

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Would you like to reset your task?", "Yes.", "No.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        send(DialogueType.OPTION, "Reset slayer task with BM or Slayer points?", "BM. (5.000)", "Slayer Points. (10)");
                        setPhase(1);
                    } else {
                        stop();
                    }
                } else if (isPhase(1)) {
                    if (option == 1) {
                        boolean canReset = false;
                        int resetAmount = 5000;
                        int bmInInventory = player.inventory().count(BLOOD_MONEY);
                        if (bmInInventory > 0) {
                            if (bmInInventory >= resetAmount) {
                                canReset = true;
                                player.inventory().remove(BLOOD_MONEY, resetAmount);
                            }
                        }

                        if (!canReset) {
                            player.message("You do not have enough BM to do this.");
                            stop();
                            return;
                        }
                        player.putAttrib(AttributeKey.SLAYER_TASK_ID, 0);
                        player.putAttrib(AttributeKey.SLAYER_TASK_AMT, 0);
                        player.putAttrib(AttributeKey.SLAYER_TASK_SPREE, 0);
                        player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
                        player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
                        Slayer.displayCurrentAssignment(player);
                        player.message("You have successfully cancelled your task.");
                    } else {
                        int pts = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
                        int required = 10;

                        if (10 > pts) {
                            player.message("You need " + required + " points to cancel your task.");
                        } else {
                            player.putAttrib(AttributeKey.SLAYER_TASK_ID, 0);
                            player.putAttrib(AttributeKey.SLAYER_TASK_AMT, 0);
                            player.putAttrib(AttributeKey.SLAYER_TASK_SPREE, 0);
                            player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
                            player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
                            player.putAttrib(AttributeKey.SLAYER_REWARD_POINTS, pts - required);
                            player.getPacketSender().sendString(SLAYER_POINTS.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_POINTS.childId).fetchLineData(player));
                            Slayer.displayCurrentAssignment(player);
                            player.message("You have successfully cancelled your task.");
                        }
                    }
                    stop();
                }
            }
        });
    }

    static int[] emblems = new int[]
        {
            ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_1,
            ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_2,
            ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_3,
            ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_4,
            ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_5
        };

    static int[] pvp_equipment = new int[]
        {
            VESTAS_LONGSWORD_BH,
            STATIUSS_WARHAMMER_BH,
            VESTAS_SPEAR_BH,
            ZURIELS_STAFF_BH
        };

    public static void reward(Player killer, NPC npc) {
        if (killer.slayerTaskAmount() > 0) {

            // Check our task. Decrease. Reward. leggo
            int task = killer.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);
            int amt = killer.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);

            if (task > 0) {
                SlayerCreature taskdef = SlayerCreature.lookup(task);
                if (taskdef != null && taskdef.matches(npc.id())) {

                    if (WildernessArea.inWilderness(killer.tile())) {
                        rollPvpEquipment(killer, npc);
                        rollLarrans(killer, npc);
                        rollEmblem(killer, npc);
                        dropBloodMoney(killer, npc);
                    }

                    killer.getSkills().addXp(Skills.SLAYER, npc.getCombatInfo().slayerxp != 0 ? npc.getCombatInfo().slayerxp : npc.maxHp());
                    killer.putAttrib(AttributeKey.SLAYER_TASK_AMT, amt - 1);
                    amt -= 1;

                    killer.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(killer));

                    Slayer.displayCurrentAssignment(killer);

                    if (amt == 0) {

                        var doublePointsUnlocked = killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.DOUBLE_SLAYER_POINTS);
                        var weekendBonus = World.getWorld().slayerRewardPointsMultiplier > 1;
                        var trainedAccount = killer.getGameMode() == GameMode.REALISM || killer.getGameMode() == GameMode.HARDCORE_REALISM;

                        if (WildernessArea.inWilderness(killer.tile())) {
                            upgradeEmblem(killer);
                        }

                        int base = 5;

                        if (taskdef.bossTask) {
                            base += 5;
                        }

                        //If you have the double perk unlocked base * 2
                        if (doublePointsUnlocked) {
                            base *= 2;
                        }

                        //Trained accounts get + 30 slayer points on the total points reward
                        if (trainedAccount) {
                            base += 5;
                        }

                        //Legendary account bonus
                        base += switch (killer.getMemberRights()) {
                            case NONE -> 0;
                            case RUBY_MEMBER -> 1;
                            case SAPPHIRE_MEMBER -> 2;
                            case EMERALD_MEMBER -> 3;
                            case DIAMOND_MEMBER -> 4;
                            case DRAGONSTONE_MEMBER -> 5;
                            case ONYX_MEMBER -> 6;
                            case ZENYTE_MEMBER -> 7;
                        };

                        //Weekend bonus
                        if (weekendBonus) {
                            base += 25;
                        }

                        int spree = (int) killer.getAttribOr(AttributeKey.SLAYER_TASK_SPREE, 0) + 1;

                        if (spree % 1000 == 0) {
                            base += 10000;
                            killer.message("<col=7F00FF>+10000 bonus slayer reward points, for having a 1000 tasks streak.");
                        } else if (spree % 250 == 0) {
                            base += 2500;
                            killer.message("<col=7F00FF>+2500 bonus slayer reward points, for having a 250 tasks streak.");
                        } else if (spree % 100 == 0) {
                            base += 1000;
                            killer.message("<col=7F00FF>+1000 bonus slayer reward points, for having a 100 tasks streak.");
                        } else if (spree % 50 == 0) {
                            base += 500;
                            killer.message("<col=7F00FF>+500 bonus slayer reward points, for having a 50 tasks streak.");
                        } else if (spree % 10 == 0) {
                            base += 150;
                            killer.message("<col=7F00FF>+150 bonus slayer reward points, for having a 10 tasks streak.");
                        }

                        var slayerRewardPoints = killer.<Integer>getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0) + base;
                        killer.putAttrib(AttributeKey.SLAYER_REWARD_POINTS, slayerRewardPoints);

                        killer.message("<col=7F00FF>You've completed " + spree + " tasks in a row and received " + base + " points; return to a Slayer Master.");
                        killer.getPacketSender().sendString(SLAYER_POINTS.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_POINTS.childId).fetchLineData(killer));

                        //TODO achievements here
                        killer.getTaskMasterManager().increase(Tasks.COMPLETE_SLAYER_TASKS);
                        DailyTaskManager.increase(DailyTasks.SLAYER, killer);

                        killer.putAttrib(AttributeKey.SLAYER_TASK_SPREE, spree);
                        killer.putAttrib(AttributeKey.COMPLETED_SLAYER_TASKS, (int) killer.getAttribOr(AttributeKey.COMPLETED_SLAYER_TASKS, 0) + 1);
                        AchievementsManager.activate(killer, Achievements.SLAYER_I, 1);
                        AchievementsManager.activate(killer, Achievements.SLAYER_II, 1);
                        AchievementsManager.activate(killer, Achievements.SLAYER_III, 1);
                        AchievementsManager.activate(killer, Achievements.SLAYER_IV, 1);
                    } else {
                        SuperiorSlayer.trySpawn(killer, taskdef, npc);
                    }
                }
            }
        }
    }

    private static void rollPvpEquipment(Player killer, NPC npc) {
        var cb = npc.def().combatlevel;
        var chance = calculatePvpEquipment(cb);
        var random = Utils.randomElement(pvp_equipment);
        if (Utils.rollDie(chance, 1)) {
            killer.message(Color.RAID_PURPLE.wrap(""));
            GroundItemHandler.createGroundItem(new GroundItem(new Item(random), npc.tile(), killer));
        }
    }

    private static void dropBloodMoney(Player killer, NPC npc) {
        int cap = 100;
        switch (killer.getMemberRights()) {
            case RUBY_MEMBER -> cap = 150;
            case SAPPHIRE_MEMBER -> cap = 200;
            case EMERALD_MEMBER -> cap = 250;
            case DIAMOND_MEMBER -> cap = 300;
            case DRAGONSTONE_MEMBER -> cap = 350;
            case ONYX_MEMBER -> cap = 400;
            case ZENYTE_MEMBER -> cap = 500;
        }
        GroundItemHandler.createGroundItem(new GroundItem(new Item(BLOOD_MONEY, Utils.random(1, cap)), npc.tile(), killer));
    }

    private static void upgradeEmblem(Player killer) {
        for (int i = 0; i < emblems.length - 1; i++) {
            if (killer.getInventory().contains(emblems[i])) {
                var emblemToAdd = emblems[i + 1];
                killer.getInventory().remove(emblems[i]);
                killer.getInventory().add(emblemToAdd);
                break;
            }
        }
    }

    private static void rollEmblem(Player killer, NPC npc) {
        var hp = npc.maxHp();
        var chance = calculateEmblem(hp);
        if (rollChance(chance)) {
            var randomEmblem = Utils.randomElement(emblems);
            GroundItemHandler.createGroundItem(new GroundItem(new Item(randomEmblem), npc.tile(), killer));
        }
    }

    private static void rollLarrans(Player killer, NPC npc) {
        var combatLevel = npc.def().combatlevel;
        var chance = calculateLarrans(combatLevel);
        var amount = 1;
        amount = getAmount(killer, amount);
        if (rollChance(chance)) {
            killer.message(Color.RAID_PURPLE.wrap(""));
            GroundItemHandler.createGroundItem(new GroundItem(new Item(ItemIdentifiers.LARRANS_KEY, amount), npc.tile(), killer));
        }
    }

    private static int getAmount(Player killer, int amount) {
        switch (killer.getMemberRights()) {
            case RUBY_MEMBER -> amount += 1;
            case ONYX_MEMBER, ZENYTE_MEMBER -> amount += 2;
        }
        return amount;
    }

    private static boolean rollChance(int chance) {
        return Utils.rollDie(chance, 1);
    }

    public static int calculatePvpEquipment(int monsterLevel) {
        int damage;
        if (monsterLevel >= 1 && monsterLevel <= 50) {
            damage = (int) Math.ceil((50 - monsterLevel) * (monsterLevel + Math.floor((50 - monsterLevel) / 5.0)) / 5.0);
        } else if (monsterLevel > 50 && monsterLevel <= 150) {
            damage = (int) Math.ceil(1.0 / Math.ceil(337.5 - monsterLevel / 1.75));
        } else if (monsterLevel > 150) {
            damage = 175;
        } else {
            throw new IllegalArgumentException("Invalid monster level");
        }
        return damage;
    }

    public static int calculateLarrans(int combatLevel) {
        int probability;
        if (combatLevel > 0 && combatLevel <= 80) {
            probability = (int) (100 / Math.floor(310 * Math.pow((80 - combatLevel), 2)) + 100);
        } else if (combatLevel > 80 && combatLevel <= 350) {
            probability = (int) (100 / Math.floor(-527 * combatLevel) + 115);
        } else if (combatLevel > 350) {
            probability = 150;
        } else {
            probability = 0;
        }
        return probability;
    }

    public static int calculateEmblem(int hitpoints) {
        double result = (1.0 / 155) - (hitpoints / 2.0);
        return (int) Math.round(Math.abs(result));
    }

}
