package com.cryptic.model.content.skill.impl.slayer.slayer_task;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.content.skill.impl.slayer.slayer_reward_interface.SlayerUnlockable;
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
import com.cryptic.model.map.position.Tile;
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
import java.util.HashMap;
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
    final int[] sigils = new int[]{SIGIL_OF_THE_FERAL_FIGHTER_26075, SIGIL_OF_THE_MENACING_MAGE_26078, SIGIL_OF_THE_RUTHLESS_RANGER_26072, SIGIL_OF_DEFT_STRIKES_26012, SIGIL_OF_THE_METICULOUS_MAGE_26003, SIGIL_OF_CONSISTENCY_25994, SIGIL_OF_THE_FORMIDABLE_FIGHTER_25997, SIGIL_OF_RESISTANCE_28490, SIGIL_OF_PRECISION_28514, SIGIL_OF_FORTIFICATION_26006, SIGIL_OF_STAMINA_26042, SIGIL_OF_THE_ALCHEMANIAC_28484, SIGIL_OF_EXAGGERATION_26057, SIGIL_OF_DEVOTION_26099, SIGIL_OF_LAST_RECALL_26144, SIGIL_OF_REMOTE_STORAGE_26141, SIGIL_OF_THE_NINJA_28526, SIGIL_OF_THE_INFERNAL_SMITH_28505, SIGIL_OF_PIOUS_PROTECTION_26129, SIGIL_OF_AGGRESSION_26132, SIGIL_OF_THE_TREASURE_HUNTER_26051};
    final int[] wildernessBossUids = new int[]{57, 58, 60};

    public void loadSlayerTasks(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<ObjectArrayList<SlayerTask>>() {
            }.getType();
            cached = gson.fromJson(reader, linkedData);
            logger.info("Loaded {} Slayer Task Information", cached.size());
        }
    }

    public void getRandomTask(@Nonnull final Player player, int slayerMasterId) {
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
                        }
                    }
                }
            }
        }
        int randomIndex = World.getWorld().random().nextInt(eligibleTasks.size());
        int uid = eligibleTasks.get(randomIndex);
        SlayerTask task = this.cached.get(uid);
        int amount = this.generateRandomTaskAmount(task);
        boolean isWildTask = slayerMasterId == NpcIdentifiers.KRYSTILIA;
        boolean isBossTask = slayerMasterId == NpcIdentifiers.KONAR_QUO_MATEN;
        applyTaskAttributes(player, task.uid, task, amount, isWildTask, isBossTask);
        player.message(Color.BLUE.wrap("You have been assigned " + task.getTaskName() + " - Amount: " + task.getRemainingTaskAmount(player)));
    }

    private void applyTaskAttributes(@NotNull final Player player, int uid, SlayerTask task, int amount, boolean isWildTask, boolean isBossTask) {
        player.putAttrib(AttributeKey.PREVIOUS_SLAYER_TASK, task.taskName);
        player.putAttrib(AttributeKey.CURRENT_SLAYER_TASK, task.taskName);
        player.putAttrib(AttributeKey.SLAYER_TASK_UID, uid);
        player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, amount);
        player.putAttrib(AttributeKey.IS_WILDERNESS_TASK, isWildTask);
        player.putAttrib(AttributeKey.IS_BOSS_SLAYER_TASK, isBossTask);
    }

    public boolean isTaskBlocked(@Nonnull final Player player, SlayerTask task) {
        return player.getSlayerRewards().getBlockedSlayerTask().contains(task.uid);
    }

    public void blockTask(@Nonnull final Player player, SlayerTask task) {
        player.getSlayerRewards().getBlockedSlayerTask().add(task.uid);
    }

    public void displayCurrentAssignment(@Nonnull final Player player) {
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

    public void sendTaskInformation(@Nonnull final Player player) {
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

    public boolean isWildernessTask(@Nonnull final Player player) {
        return player.<Boolean>getAttrib(AttributeKey.IS_WILDERNESS_TASK);
    }

    public boolean isBossTask(@Nonnull final Player player) {
        return player.<Boolean>getAttrib(AttributeKey.IS_BOSS_SLAYER_TASK);
    }

    public boolean hasSlayerTask(@Nonnull final Player player) {
        return this.getCurrentAssignment(player) != null;
    }

    public void cancelSlayerTask(final Player player, boolean isBlocking, boolean isCoins) {
        if (player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.SKIPPY)) {
            player.clearAttrib(AttributeKey.CURRENT_SLAYER_TASK);
            player.clearAttrib(AttributeKey.SLAYER_TASK_UID);
            player.clearAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
            player.clearAttrib(AttributeKey.IS_WILDERNESS_TASK);
            player.clearAttrib(AttributeKey.IS_BOSS_SLAYER_TASK);
            return;
        }
        int slayerPoints = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        if (!isCoins && slayerPoints < 30) {
            player.message(Color.RED.wrap("You do not have enough coins to do this."));
            return;
        }
        int decrement = isBlocking ? 100 : 30;
        player.clearAttrib(AttributeKey.CURRENT_SLAYER_TASK);
        player.clearAttrib(AttributeKey.SLAYER_TASK_UID);
        player.clearAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
        player.clearAttrib(AttributeKey.IS_WILDERNESS_TASK);
        player.clearAttrib(AttributeKey.IS_BOSS_SLAYER_TASK);
        if (!isCoins) {
            player.putAttrib(SLAYER_REWARD_POINTS, slayerPoints - decrement);
        }
    }

    public void clearSlayerTask(final Player player) {
        int slayerPoints = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        player.clearAttrib(AttributeKey.CURRENT_SLAYER_TASK);
        player.clearAttrib(AttributeKey.SLAYER_TASK_UID);
        player.clearAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
        player.clearAttrib(AttributeKey.IS_WILDERNESS_TASK);
    }

    public void sendCancelTaskDialouge(@Nonnull final Player player) {
        if (player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.SKIPPY)) {
            SlayerTask slayer = World.getWorld().getSlayerTasks();
            slayer.cancelSlayerTask(player, false, true);
            slayer.displayCurrentAssignment(player);
            player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
            player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
            player.message("You have successfully cancelled your task.");
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
                        slayer.cancelSlayerTask(player, false, true);
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

                        slayer.cancelSlayerTask(player, false, false);
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

    public void handleSlayerDeath(final Player player, final NPC npc) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        int slayerPoints = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        if (assignment != null && this.isLinkedById(player, npc.id())) {
            double experience = this.getSlayerExperience(npc);
            int amount = this.getRemainingTaskAmount(player);
            HashMap<Integer, String> slayerPerks = player.getSlayerRewards().getUnlocks();
            boolean inWilderness = WildernessArea.inWilderness(player.tile());
            if (this.isWildernessTask(player) && !inWilderness) return;
            if (inWilderness && this.isWildernessTask(player)) {
                isSlayerPerkEnabled(player, npc, slayerPerks);
            }
            player.getSlayerKillLog().addKill(npc);
            player.getSkills().addXp(Skill.SLAYER.getId(), experience);
            amount = isUsingExpeditiousBracelet(player, amount);
            decrementTaskAmount(player, amount);
            if (this.isRemoveSlayerTask(player)) {
                int increment = 0;
                increment += this.getSlayerTaskCompletionPoints(player);
                if (slayerPerks.containsKey(SlayerConstants.DOUBLE_SLAYER_POINTS)) increment *= 2;
                increment += incrementMemberBonusSlayerPoints(player, increment);
                player.message(Color.BLUE.wrap("You have completed your slayer task!"));
                player.message(Color.PURPLE.wrap("You have been awarded " + increment + " Slayer points!"));
                slayerPoints += increment;
                player.putAttrib(SLAYER_REWARD_POINTS, slayerPoints);
                this.rewardCoins(player);
                this.incrementTaskCompletionSpree(player);
                this.clearSlayerTask(player);
                this.upgradeEmblem(player);
                return;
            }
        }
    }

    private void decrementTaskAmount(Player player, int amount) {
        if (player.getEquipment().contains(BRACELET_OF_SLAUGHTER) && Utils.rollDie(25, 1)) {
            player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, Math.max(0, amount));
        } else {
            player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, Math.max(0, amount - 1));
        }
    }

    private int isUsingExpeditiousBracelet(Player player, int amount) {
        if (player.getEquipment().contains(EXPEDITIOUS_BRACELET) && Utils.rollDie(25, 1)) amount = amount - 1;
        return amount;
    }

    private void rewardCoins(final Player player) {
        var coinAmount = 1_000_000;
        if (player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.SPARE_CHANGE)) coinAmount *= 1.40;
        player.getInventory().addOrDrop(new Item(995, coinAmount));
    }

    void isSlayerPerkEnabled(final Player player, final NPC npc, HashMap<Integer, String> slayerPerks) {
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

    void incrementTaskCompletionSpree(final Player player) {
        int taskCompletionSpree = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_SPREE, 0) + 1;
        player.putAttrib(AttributeKey.SLAYER_TASK_SPREE, taskCompletionSpree);
        player.putAttrib(AttributeKey.COMPLETED_SLAYER_TASKS, player.<Integer>getAttribOr(AttributeKey.COMPLETED_SLAYER_TASKS, 0) + 1);
    }

    int incrementMemberBonusSlayerPoints(final Player player, int slayerPoints) {
        switch (player.getMemberRights()) {
            case RUBY_MEMBER, SAPPHIRE_MEMBER -> slayerPoints += 2;
            case EMERALD_MEMBER, DIAMOND_MEMBER -> slayerPoints += 4;
            case DRAGONSTONE_MEMBER -> slayerPoints += 6;
            case ONYX_MEMBER -> slayerPoints += 8;
            case ZENYTE_MEMBER -> slayerPoints += 10;
        }
        return slayerPoints;
    }

    public int getSlayerTaskCompletionPoints(@Nonnull final Player player) {
        return this.isWildernessTask(player) ? 40 : this.isBossTask(player) ? 30 : 25;
    }

    public double getSlayerExperience(@Nonnull final NPC npc) {
        return npc.maxHp();
    }

    public boolean isRemoveSlayerTask(final Player player) {
        return this.getRemainingTaskAmount(player) <= 0;
    }

    public int getRemainingTaskAmount(@Nonnull final Player player) {
        return player.<Integer>getAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
    }

    public SlayerTask getCurrentAssignment(@Nonnull final Player player) {
        int id = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_UID, -1);
        return id != -1 ? this.cached.get(id) : null;
    }

    public boolean hasTaskRequirements(@Nonnull final Player player, SlayerTask task) {
        if ((task == null || (player.getSkills().combatLevel() < task.combatReq) || (player.getSkills().level(Skill.SLAYER.getId()) < task.slayerReq)))
            return false;
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.LIKE_A_BOSS) && ArrayUtils.contains(wildernessBossUids, task.getUid()))
            return false;
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.REVVED_UP) && task.getUid() == 47)
            return false;
        return true;
    }

    public boolean isLinkedById(@Nonnull final Player player, int npcId) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment != null) return ArrayUtils.contains(assignment.npcs, npcId);
        else return false;
    }

    public boolean isExtendable(final Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment != null) return assignment.extendedMin != -1 && assignment.extendedMax != -1;
        else return false;
    }

    int generateRandomTaskAmount(SlayerTask task) {
        int adjustedMax = task.max;
        if (adjustedMax > 100) adjustedMax /= 2;
        if (adjustedMax <= task.min) adjustedMax = task.min + 1;
        return World.getWorld().random().nextInt(task.min, adjustedMax);
    }

    int getExtendedTaskAmount(final Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment == null) return -1;
        if (!isExtendable(player)) return -1;
        else return Utils.random(assignment.extendedMin, assignment.extendedMax);
    }

    String getTaskName(final Player player) {
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

    void rollForPvpEquipment(final Player killer, NPC npc) {
        var chance = calculatePvpEquipment(npc);
        var random = Utils.randomElement(pvp_equipment);
        chance *= 3;
        if (Utils.rollDie(chance, 1)) {
            GroundItemHandler.createGroundItem(new GroundItem(new Item(random), npc.tile(), killer));
        }
    }

    void dropBloodMoney(final Player killer, NPC npc) {
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
        final Item item = new Item(BLOOD_MONEY, Utils.random(1, cap));
        if (killer.getEquipment().contains(RING_OF_WEALTH_I) && !killer.getInventory().isFull()) {
            killer.getInventory().add(item, item.getAmount());
            return;
        }
        GroundItemHandler.createGroundItem(new GroundItem(item, npc.tile(), killer));
    }

    void upgradeEmblem(final Player killer) {
        for (int i = 0; i < emblems.length - 1; i++) {
            if (killer.getInventory().contains(emblems[i])) {
                int emblemToAdd = emblems[i + 1];
                killer.getInventory().remove(emblems[i]);
                killer.getInventory().add(emblemToAdd);
                break;
            }
        }
    }

    void rollForEmblem(final Player killer, final NPC npc) {
        int hp = npc.maxHp();
        if (rollChance(300)) {
            var randomEmblem = Utils.randomElement(emblems);
            GroundItemHandler.createGroundItem(new GroundItem(new Item(randomEmblem), npc.tile(), killer));
        }
    }

    void rollForLarransKey(final Player killer, final NPC npc) {
        var def = NpcDefinition.cached.get(npc.id());
        int combatLevel = def.combatLevel;
        int chance = calculateLarrans(combatLevel);
        int amount = 1;
        amount = getLarransKeyAmountToDrop(killer, amount);
        if (rollChance(chance)) {
            killer.message(Color.RAID_PURPLE.wrap(""));
            GroundItemHandler.createGroundItem(new GroundItem(new Item(ItemIdentifiers.LARRANS_KEY, amount), npc.tile(), killer));
        }
    }

    void rollForSigil(final Player killer, final NPC npc) {
        int chance = calculateSigilChance(npc);
        if (rollChance(chance)) {
            int id = Utils.randomElement(sigils);
            GroundItemHandler.createGroundItem(new GroundItem(new Item(id), npc.tile(), killer));
            if (ArrayUtils.contains(sigils, id)) {
                var def = ItemDefinition.cached.get(id);
                var inWild = WildernessArea.inWilderness(killer.tile());
                var level = WildernessArea.getWildernessLevel(killer.tile());
                World.getWorld().sendWorldMessage("<img=2010> " + Color.BURNTORANGE.wrap("<shad=0>" + killer.getUsername() + " has received a " + def.name + " from a " + npc.getMobName() + (!inWild ? "." : " Level: " + level + " wilderness.") + "</shad>"));
            }
        }
    }

    int calculateSigilChance(final NPC npc) {
        var combatLevel = npc.def().combatLevel;
        int chance;
        if (combatLevel <= 50) chance = 1000;
        else if (combatLevel >= 150) chance = 500;
        else chance = 1000;
        return chance;
    }

    int getLarransKeyAmountToDrop(final Player killer, int amount) {
        switch (killer.getMemberRights()) {
            case RUBY_MEMBER -> amount += 1;
            case ONYX_MEMBER, ZENYTE_MEMBER -> amount += 2;
        }
        return amount;
    }

    boolean rollChance(int chance) {
        return World.getWorld().rollDie(chance, 1);
    }

    int calculatePvpEquipment(final NPC npc) {
        var combatLevel = npc.def().combatLevel;
        int chance;
        if (combatLevel <= 50) chance = 1000;
        else if (combatLevel >= 150) chance = 500;
        else chance = 1000;
        return chance;
    }

    int calculateLarrans(int combatLevel) {
        int probability;
        if (combatLevel >= 1 && combatLevel <= 80) {
            probability = (int) (1972.0 / combatLevel);
        } else if (combatLevel > 80 && combatLevel <= 350) {
            probability = (int) (99 - (combatLevel - 80) / 3.0);
            probability = Math.max(probability, 50);
        } else {
            probability = 50;
        }
        return probability;
    }

    int calculateEmblem(int hitpoints) {
        double result = (1.0 / 155) - (hitpoints / 2.0);
        return (int) Math.round(Math.abs(result));
    }

    public final Tile getLocation(boolean isWildernessTask) {
        Tile location = null;
        switch (this.getTaskName()) {
            case "Rune Dragons" -> location = new Tile(1573, 5074, 0);
            case "Adamant Dragons" -> location = new Tile(1562, 5075, 0);
            case "Aviansies" -> {
                if (isWildernessTask) return new Tile(3064, 10126, 0);
                location = new Tile(2838, 5291, 2);
            }
            case "Lizardmen" -> location = new Tile(1453, 3694, 0);
            case "Mithril Dragons" -> location = new Tile(1777, 5349, 1);
            case "Tzhaars" -> location = new Tile(2456, 5159, 0);
            case "Kalphites" -> location = new Tile(3499, 9525, 2);
            case "Ankou" -> {
                if (isWildernessTask) return new Tile(3361, 10078, 0);
                location = new Tile(1641, 9995, 0);
            }
            case "Trolls" -> location = new Tile(2849, 3674, 0);
            case "Blue Dragons" -> location = new Tile(2907, 9812, 0);
            case "Fire Giants" -> location = new Tile(2568, 9892, 0);
            case "Red Dragons" -> location = new Tile(2708, 9537, 0);
            case "Elves" -> location = new Tile(3189, 12409, 0);
            case "Dagannoths" -> location = new Tile(2443, 10146, 0);
            case "Hellhounds" -> location = new Tile(1644, 10066, 0);
            case "Black Demons" -> location = new Tile(1719, 10084, 0);
            case "Greater Demons" -> location = new Tile(1693, 10098, 0);
            case "Black Dragons" -> location = new Tile(1613, 10085, 0);
            case "Steel Dragons" -> location = new Tile(1606, 10048, 0);
            case "Suqahs" -> location = new Tile(2112, 3862, 0);
            case "Basilisks" -> location = new Tile(2747, 10012, 0);
            case "Brine Rats" -> location = new Tile(2708, 10133, 0);
            case "Bloodvelds" -> location = new Tile(1679, 10075, 0);
            case "Turoths" -> location = new Tile(2728, 10002, 0);
            case "Cave Horrors" -> location = new Tile(3826, 9425, 0);
            case "Aberrant Spectres" -> location = new Tile(2464, 9783, 0);
            case "Wyrms" -> location = new Tile(1279, 10187, 0);
            case "Dust Devils" -> location = new Tile(1714, 10032, 0);
            case "Wyverns" -> location = new Tile(3060, 9560, 0);
            case "Kurasks" -> location = new Tile(2705, 9977, 0);
            case "Gargoyles" -> location = new Tile(3444, 3539, 2);
            case "Nechryael" -> location = new Tile(1706, 10081, 0);
            case "Drakes" -> location = new Tile(1313, 10235, 0);
            case "Abyssal Demons" -> {
                if (isWildernessTask) return new Tile(3352, 10147, 0);
                location = new Tile(1676, 10059, 0);
            }
            case "Cave Krakens" -> location = new Tile(2277, 10003, 0);
            case "Dark Beasts" -> location = new Tile(1991, 4648, 0);
            case "Smoke Devils" -> location = new Tile(2388, 9449, 0);
            case "Banshees" -> location = new Tile(1617, 9997, 0);
            case "Bats" -> location = new Tile(2914, 9833, 0);
            case "Bears" -> location = new Tile(3705, 3339, 0);
            case "Crawling Hands" -> location = new Tile(3419, 3557, 0);
            case "Hill Giants" -> location = new Tile(1652, 10036, 0);
            case "Crabs" -> location = new Tile(2706, 3713, 0);
            case "Skeletons" -> location = new Tile(1642, 10048, 0);
            case "Spiders" -> location = new Tile(3169, 3885, 0);
            case "Ghosts" -> location = new Tile(1663, 10023, 0);
            case "Revenants" -> location = new Tile(3255, 10187, 0);
            case "Scorpions" -> location = new Tile(3299, 3300, 0);
            case "Lava Dragons" -> location = new Tile(3202, 3857, 0);
            case "Green Dragons" -> location = new Tile(3343, 3664, 0);
            case "Magic Axes" -> location = new Tile(3190, 3960, 0);
            case "Pirates" -> location = new Tile(3041, 3958, 0);
            case "Rogues" -> location = new Tile(3292, 3940, 0);
            case "Black Knights" -> location = new Tile(3022, 3513, 0);
            case "Waterfiends" -> location = new Tile(1760, 5358, 0);
            case "Hydras" -> location = new Tile(1311, 10266, 0);
            case "Callisto" -> location = new Tile(3293, 3847, 0);
            case "Vetion" -> location = new Tile(3222, 3791, 0);
            case "Venenatis" -> location = new Tile(3316, 3795, 0);
        }
        return location;
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
