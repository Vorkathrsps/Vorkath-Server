package com.cryptic.model.content.skill.impl.slayer.slayer_task;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.QuestTab;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_REWARD_POINTS;
import static com.cryptic.model.entity.player.QuestTab.InfoTab.*;
import static com.cryptic.model.entity.player.QuestTab.InfoTab.SLAYER_POINTS;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 2/16/24
 */
@Data
@SuppressWarnings("ALL")
public class SlayerTask {
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(World.class);
    String taskName;
    int[] slayerMasters, npcs;
    int combatReq, slayerReq;
    int weight, min, max, extendedMin, extendedMax, uid;
    ObjectList<SlayerTask> cached = new ObjectArrayList<>();
    final int[] emblems = new int[]{ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_1, ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_2, ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_3, ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_4, ItemIdentifiers.MYSTERIOUS_EMBLEM_TIER_5};
    final int[] pvp_equipment = new int[]{VESTAS_LONGSWORD_BH, STATIUSS_WARHAMMER_BH, VESTAS_SPEAR_BH, ZURIELS_STAFF_BH};
    final int[] sigils = new int[]{};

    public void loadSlayerTasks(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<ObjectArrayList<SlayerTask>>() {
            }.getType();
            cached = gson.fromJson(reader, linkedData);
            logger.info("Loaded {} Slayer Task Information", cached.size());
        }
    }

    public void getRandomTask(@Nonnull Player player, int slayerMasterId) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment != null) {
            player.message(Color.RED.wrap("You must finish your current assignment."));
            player.message(Color.BLUE.wrap("Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + assignment.getRemainingTaskAmount(player)));
            return;
        }
        ObjectList<Integer> eligibleTasks = new ObjectArrayList<>();
        String previousTask = player.getAttribOr(AttributeKey.PREVIOUS_SLAYER_TASK, "");
        for (SlayerTask task : this.cached) {
            if (hasTaskRequirements(player, task)) {
                if (task != null && ArrayUtils.contains(task.slayerMasters, slayerMasterId)) {
                    if (!this.isTaskBlocked(player, task)) {
                        if (!Objects.equals(task.taskName, previousTask)) {
                            eligibleTasks.add(task.uid);
                            System.out.println("adding eligible task: " + task.getTaskName());
                        }
                    }
                }
            }
        }
        int randomIndex = World.getWorld().random().nextInt(eligibleTasks.size());
        int uid = eligibleTasks.get(randomIndex);
        SlayerTask task = this.cached.get(uid);
        System.out.println("task found: " + task.getTaskName());
        int amount = this.generateRandomTaskAmount(task);
        boolean isWildTask = slayerMasterId == NpcIdentifiers.KRYSTILIA;
        applyTaskAttributes(player, task.uid, task, amount, isWildTask);
        player.message(Color.BLUE.wrap("You have been assigned " + task.getTaskName() + " - Amount: " + task.getRemainingTaskAmount(player)));
    }

    private void applyTaskAttributes(@NotNull Player player, int uid, SlayerTask task, int amount, boolean isWildTask) {
        player.putAttrib(AttributeKey.PREVIOUS_SLAYER_TASK, task.taskName);
        player.putAttrib(AttributeKey.CURRENT_SLAYER_TASK, task.taskName);
        player.putAttrib(AttributeKey.SLAYER_TASK_UID, uid);
        player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, amount);
        player.putAttrib(AttributeKey.IS_WILDERNESS_TASK, isWildTask);
    }

    public boolean isTaskBlocked(@Nonnull Player player, SlayerTask task) {
        return player.getSlayerRewards().getBlockedSlayerTask().contains(task.uid);
    }

    public void blockTask(@Nonnull Player player, SlayerTask task) {
        player.getSlayerRewards().getBlockedSlayerTask().add(task.uid);
    }

    public void displayCurrentAssignment(@Nonnull Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment == null) {
            player.message("You currently have no active slayer task.");
            player.getPacketSender().sendString(63208, "None");
            return;
        }
        String name = this.getCurrentAssignment(player).getTaskName();
        int remainingTaskAmount = this.getRemainingTaskAmount(player);
        player.getPacketSender().sendString(63208, "" + remainingTaskAmount + " x " + name);
    }

    public void sendTaskInformation(@Nonnull Player player) {
        SlayerTask slayer = World.getWorld().getSlayerTasks();
        SlayerTask assignment = slayer.getCurrentAssignment(player);
        this.displayCurrentAssignment(player);
        if (assignment != null)
            player.message(Color.BLUE.wrap("Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + assignment.getRemainingTaskAmount(player)));
        for (int index = 0; index < 6; index++) {
            player.getPacketSender().sendString(63232 + index, "<col=ffa500>Unblock Task </col>");
            player.getPacketSender().sendString(63220 + index, "Empty");
        }
        int count = 0;
        for (int uid : player.getSlayerRewards().getBlockedSlayerTask()) {
            if (player.getSlayerRewards().getBlockedSlayerTask().size() > 0 && count <= player.getSlayerRewards().getBlockedSlayerTask().size() && player.getSlayerRewards().getBlockedSlayerTask().size() > count) {
                player.getPacketSender().sendString(63220 + count, this.cached.get(uid).taskName);
            } else {
                player.getPacketSender().sendString(63220 + count, "Empty");
            }
            count++;
        }
    }

    public boolean isWildernessTask(@Nonnull Player player) {
        return player.<Boolean>getAttrib(AttributeKey.IS_WILDERNESS_TASK);
    }

    public boolean hasSlayerTask(@Nonnull Player player) {
        return this.getCurrentAssignment(player) != null;
    }

    public void cancelSlayerTask(Player player, boolean isBlocking) {
        int slayerPoints = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        int decrement = isBlocking ? 100 : 30;
        player.clearAttrib(AttributeKey.CURRENT_SLAYER_TASK);
        player.clearAttrib(AttributeKey.SLAYER_TASK_UID);
        player.clearAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
        player.clearAttrib(AttributeKey.IS_WILDERNESS_TASK);
        player.putAttrib(SLAYER_REWARD_POINTS, slayerPoints - decrement);
    }

    public void sendCancelTaskDialouge(@Nonnull Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Would you like to reset your task?", "Yes.", "No.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                SlayerTask slayer = World.getWorld().getSlayerTasks();
                if (isPhase(0)) {
                    if (option == 1) {
                        send(DialogueType.OPTION, "Reset slayer task with BM or Slayer points?", "Coins (1 Million)", "Slayer Points. (30)");
                        setPhase(1);
                    } else {
                        stop();
                    }
                } else if (isPhase(1)) {
                    if (option == 1) {
                        boolean canReset = false;
                        int resetAmount = 1_000_000;
                        int bmInInventory = player.inventory().count(COINS_995);
                        if (bmInInventory > 0) {
                            if (bmInInventory >= resetAmount) {
                                canReset = true;
                                player.inventory().remove(COINS_995, resetAmount);
                            }
                        }

                        if (!canReset) {
                            player.message("You do not have enough coins to do this.");
                            stop();
                            return;
                        }
                        slayer.cancelSlayerTask(player, false);
                        slayer.displayCurrentAssignment(player);
                        player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
                        player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
                        player.message("You have successfully cancelled your task.");
                    } else {
                        int pts = player.<Integer>getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
                        int required = 30;

                        if (pts < 30) {
                            player.message("You need " + required + " points to cancel your task.");
                            return;
                        }
                        slayer.cancelSlayerTask(player, false);
                        slayer.displayCurrentAssignment(player);
                        player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
                        player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
                        player.getPacketSender().sendString(SLAYER_POINTS.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_POINTS.childId).fetchLineData(player));
                        player.message("You have successfully cancelled your task.");
                    }
                    stop();
                }
            }
        });
    }

    public void handleSlayerDeath(Player player, NPC npc) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        int slayerPoints = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        if (assignment != null && this.isLinkedById(player, npc.id())) {
            double experience = this.getSlayerExperience(npc);
            int increment = this.getSlayerTaskCompletionPoints(player);
            int amount = this.getRemainingTaskAmount(player);
            Map<Integer, String> slayerPerks = player.getSlayerRewards().getUnlocks();
            boolean inWilderness = WildernessArea.inWilderness(player.tile());
            if (this.isWildernessTask(player) && !inWilderness) return;
            if (inWilderness && this.isWildernessTask(player)) {
                isSlayerPerkEnabled(player, npc, slayerPerks);
            }
            if (inWilderness) {
                this.upgradeEmblem(player);
            }
            if (slayerPerks.containsKey(SlayerConstants.DOUBLE_SLAYER_POINTS)) {
                increment *= 2;
            }
            increment += incrementMemberBonusSlayerPoints(player, increment);
            if (this.isRemoveSlayerTask(player)) {
                player.getSkills().addXp(Skill.SLAYER.getId(), experience);
                player.message(Color.BLUE.wrap("You have completed your slayer task!"));
                player.message(Color.PURPLE.wrap("You have been awarded " + increment + " Slayer points!"));
                this.incrementTaskCompletionSpree(player);
                this.cancelSlayerTask(player, false);
                return;
            }
            player.getSlayerKillLog().addKill(npc);
            player.getSkills().addXp(Skill.SLAYER.getId(), experience);
            player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, amount - 1);
            player.putAttrib(SLAYER_REWARD_POINTS, slayerPoints + increment);
        }
    }

    void isSlayerPerkEnabled(Player player, NPC npc, Map<Integer, String> slayerPerks) {
        if (slayerPerks.containsKey(SlayerConstants.SIGIL_DROPPER)) {
            rollForSigil(player, npc);
        }
        if (slayerPerks.containsKey(SlayerConstants.PVP_ARMOURS)) {
            rollForPvpEquipment(player, npc);
        }
        if (slayerPerks.containsKey(SlayerConstants.LARRANS_LUCK)) {
            rollForLarransKey(player, npc);
        }
        if (slayerPerks.containsKey(SlayerConstants.EMBLEM_HUNTER)) {
            rollForEmblem(player, npc);
        }
        if (slayerPerks.containsKey(SlayerConstants.SLAYERS_GREED)) {
            dropBloodMoney(player, npc);
        }
    }

    void incrementTaskCompletionSpree(Player player) {
        int taskCompletionSpree = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_SPREE, 0) + 1;
        player.putAttrib(AttributeKey.SLAYER_TASK_SPREE, taskCompletionSpree);
        player.putAttrib(AttributeKey.COMPLETED_SLAYER_TASKS, player.<Integer>getAttribOr(AttributeKey.COMPLETED_SLAYER_TASKS, 0) + 1);
    }

    int incrementMemberBonusSlayerPoints(Player player, int slayerPoints) {
        switch (player.getMemberRights()) {
            case RUBY_MEMBER, SAPPHIRE_MEMBER -> slayerPoints += 2;
            case EMERALD_MEMBER, DIAMOND_MEMBER -> slayerPoints += 4;
            case DRAGONSTONE_MEMBER -> slayerPoints += 6;
            case ONYX_MEMBER -> slayerPoints += 8;
            case ZENYTE_MEMBER -> slayerPoints += 10;
        }
        return slayerPoints;
    }

    public int getSlayerTaskCompletionPoints(@Nonnull Player player) {
        return this.isWildernessTask(player) ? 40 : 25;
    }

    public double getSlayerExperience(@Nonnull NPC npc) {
        return npc.maxHp();
    }

    public boolean isRemoveSlayerTask(Player player) {
        return this.getRemainingTaskAmount(player) <= 0;
    }

    public int getRemainingTaskAmount(@Nonnull Player player) {
        return player.<Integer>getAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
    }

    public SlayerTask getCurrentAssignment(@Nonnull Player player) {
        int id = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_UID, -1);
        return id != -1 ? this.cached.get(id) : null;
    }

    public boolean hasTaskRequirements(@Nonnull Player player, SlayerTask task) {
        if (task == null || player.getSkills().combatLevel() < task.combatReq) return false;
        if (player.getSkills().level(Skill.SLAYER.getId()) < task.slayerReq) return false;
        return true;
    }

    public boolean isLinkedById(@Nonnull Player player, int npcId) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment != null) return ArrayUtils.contains(assignment.npcs, npcId);
        else return false;
    }

    public boolean isExtendable(Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment != null) return assignment.extendedMin != -1 && assignment.extendedMax != -1;
        else return false;
    }

    int generateRandomTaskAmount(SlayerTask task) {
        return Utils.random(task.min, task.max);
    }

    int getExtendedTaskAmount(Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment == null) return -1;
        if (!isExtendable(player)) return -1;
        else return Utils.random(assignment.extendedMin, assignment.extendedMax);
    }

    String getTaskName(Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment == null) return "None";
        else return assignment.taskName;
    }

    int totalTaskWeight() {
        return IntStream.of(getTaskWeight()).sum();
    }

    public int getTaskWeight() {
        int weight = 0;
        for (SlayerTask task : this.cached) weight += task.weight;
        return weight;
    }

    void rollForPvpEquipment(Player killer, NPC npc) {
        var cb = npc.def().combatlevel;
        var chance = calculatePvpEquipment(cb);
        var random = Utils.randomElement(pvp_equipment);
        if (Utils.rollDie(chance, 1)) {
            GroundItemHandler.createGroundItem(new GroundItem(new Item(random), npc.tile(), killer));
        }
    }

    void dropBloodMoney(Player killer, NPC npc) {
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

    void upgradeEmblem(Player killer) {
        for (int i = 0; i < emblems.length - 1; i++) {
            if (killer.getInventory().contains(emblems[i])) {
                int emblemToAdd = emblems[i + 1];
                killer.getInventory().remove(emblems[i]);
                killer.getInventory().add(emblemToAdd);
                break;
            }
        }
    }


    void rollForEmblem(Player killer, NPC npc) {
        int hp = npc.maxHp();
        int chance = calculateEmblem(hp);
        if (rollChance(chance)) {
            var randomEmblem = Utils.randomElement(emblems);
            GroundItemHandler.createGroundItem(new GroundItem(new Item(randomEmblem), npc.tile(), killer));
        }
    }

    void rollForLarransKey(Player killer, NPC npc) {
        int combatLevel = npc.def().combatlevel;
        int chance = calculateLarrans(combatLevel);
        int amount = 1;
        amount = getLarransKeyAmountToDrop(killer, amount);
        if (rollChance(chance)) {
            killer.message(Color.RAID_PURPLE.wrap(""));
            GroundItemHandler.createGroundItem(new GroundItem(new Item(ItemIdentifiers.LARRANS_KEY, amount), npc.tile(), killer));
        }
    }

    void rollForSigil(Player killer, NPC npc) {
        int chance = calculateSigilChance(npc);
        if (rollChance(chance)) {
            int id = Utils.randomElement(sigils);
            GroundItemHandler.createGroundItem(new GroundItem(new Item(id), npc.tile(), killer));
        }
    }

    int calculateSigilChance(NPC npc) {
        var combatLevel = npc.def().combatlevel;
        int chance;
        if (combatLevel <= 50) chance = 1000;
        else if (combatLevel >= 150) chance = 500;
        else chance = 1000;
        return chance;
    }

    int getLarransKeyAmountToDrop(Player killer, int amount) {
        switch (killer.getMemberRights()) {
            case RUBY_MEMBER -> amount += 1;
            case ONYX_MEMBER, ZENYTE_MEMBER -> amount += 2;
        }
        return amount;
    }

    boolean rollChance(int chance) {
        return World.getWorld().rollDie(chance, 1);
    }

    int calculatePvpEquipment(int monsterLevel) {
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

    int calculateLarrans(int combatLevel) {
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

    int calculateEmblem(int hitpoints) {
        double result = (1.0 / 155) - (hitpoints / 2.0);
        return (int) Math.round(Math.abs(result));
    }

    @Override
    public String toString() {
        return "SlayerTask{" +
            "taskName='" + taskName + '\'' +
            ", slayerMasters=" + Arrays.toString(slayerMasters) +
            ", npcs=" + Arrays.toString(npcs) +
            ", combatReq=" + combatReq +
            ", slayerReq=" + slayerReq +
            ", weight=" + weight +
            ", min=" + min +
            ", max=" + max +
            ", extendedMin=" + extendedMin +
            ", extendedMax=" + extendedMax +
            ", uid=" + uid +
            '}';
    }
}
