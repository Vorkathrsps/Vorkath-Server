package com.cryptic.model.content.skill.impl.slayer.slayer_task;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.utility.Color;
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
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @Author: Origin
 * @Date: 2/16/24
 */
@Data
public class SlayerTask {
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(World.class);
    String taskName;
    int[] slayerMasters, npcs;
    int combatReq, slayerReq;
    int weight, min, max, extendedMin, extendedMax;
    ObjectList<SlayerTask> cached = new ObjectArrayList<>();

    public void loadSlayerTasks(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<ObjectArrayList<SlayerTask>>() {}.getType();
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
        ObjectList<SlayerTask> eligibleTasks = new ObjectArrayList<>();
        String previousTask = player.getAttribOr(AttributeKey.PREVIOUS_SLAYER_TASK, "");
        for (SlayerTask task : this.cached) {
            if (task != null && ArrayUtils.contains(task.slayerMasters, slayerMasterId) && hasTaskRequirements(player, task)) {
                if (!Objects.equals(task.taskName, previousTask)) {
                    eligibleTasks.add(task);
                }
            }
        }
        int randomIndex = World.getWorld().random().nextInt(eligibleTasks.size());
        SlayerTask task = eligibleTasks.get(randomIndex);
        int amount = this.generateRandomTaskAmount(task);
        boolean isWildTask = slayerMasterId == NpcIdentifiers.KRYSTILIA;
        applyTaskAttributes(player, randomIndex, task, amount, isWildTask);
        player.message(Color.BLUE.wrap("You have been assigned " + task.getTaskName() + " - Amount: " + task.getRemainingTaskAmount(player)));
    }

    private void applyTaskAttributes(@NotNull Player player, int randomIndex, SlayerTask task, int amount, boolean isWildTask) {
        player.putAttrib(AttributeKey.PREVIOUS_SLAYER_TASK, task.taskName);
        player.putAttrib(AttributeKey.CURRENT_SLAYER_TASK, task.taskName);
        player.putAttrib(AttributeKey.SLAYER_TASK_UID, randomIndex);
        player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, amount);
        player.putAttrib(AttributeKey.IS_WILDERNESS_TASK, isWildTask);
    }

    public boolean isWildernessTask(@Nonnull Player player) {
        return player.<Boolean>getAttrib(AttributeKey.IS_WILDERNESS_TASK);
    }

    public boolean hasSlayerTask(@Nonnull Player player) {
        return this.getCurrentAssignment(player) != null;
    }

    public void cancelSlayerTask(Player player) {
        player.clearAttrib(AttributeKey.CURRENT_SLAYER_TASK);
        player.clearAttrib(AttributeKey.SLAYER_TASK_UID);
        player.clearAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
    }

    public void handleSlayerDeath(Player player, NPC npc) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment != null && this.isLinkedById(player, npc.id())) {
            double experience = this.getSlayerExperience(player, npc);
            int slayerPoints = this.getSlayerPoints(player);
            int amount = this.getRemainingTaskAmount(player);
            if (isRemoveSlayerTask(player)) {
                player.getSkills().addXp(Skill.SLAYER.getId(), experience);
                player.message(Color.BLUE.wrap("You have completed your slayer task!"));
                player.message(Color.PURPLE.wrap("You have been awarded " + slayerPoints + " Slayer points!"));
                this.cancelSlayerTask(player);
                return;
            }
            player.getSlayerKillLog().addKill(npc);
            player.getSkills().addXp(Skill.SLAYER.getId(), experience);
            player.putAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING, amount - 1);
        }
    }

    public int getSlayerPoints(@Nonnull Player player) {
        return this.isWildernessTask(player) ? 40 : 25;
    }

    public double getSlayerExperience(@Nonnull Player player, @Nonnull NPC npc) {
        return npc.maxHp() * player.getGameMode().multiplier;
    }

    public boolean isRemoveSlayerTask(Player player) {
        return this.getRemainingTaskAmount(player) <= 0;
    }

    public int getRemainingTaskAmount(@Nonnull Player player) {
        return player.<Integer>getAttrib(AttributeKey.SLAYER_TASK_AMOUNT_REMAINING);
    }

    public SlayerTask getCurrentAssignment(@Nonnull Player player) {
        int id = player.getAttribOr(AttributeKey.SLAYER_TASK_UID, -1);
        return id != -1 ? this.cached.get(id) : null;
    }

    public boolean hasTaskRequirements(@Nonnull Player player, SlayerTask task) {
        if (task == null) return false;
        int slayerLevel = player.getSkills().level(Skill.SLAYER.getId());
        return player.getSkills().combatLevel() >= task.combatReq && slayerLevel >= task.slayerReq;
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

    public int generateRandomTaskAmount(SlayerTask task) {
        if (task == null) return -1;
        return Utils.random(task.min, task.max);
    }

    public int getExtendedTaskAmount(Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment == null) return -1;
        if (!isExtendable(player)) return -1;
        else return Utils.random(assignment.extendedMin, assignment.extendedMax);
    }

    public String getTaskName(Player player) {
        SlayerTask assignment = this.getCurrentAssignment(player);
        if (assignment == null) return "None";
        else return assignment.taskName;
    }

    public int totalTaskWeight() {
        return IntStream.of(getTaskWeight()).sum();
    }

    public int getTaskWeight() {
        int weight = 0;
        for (SlayerTask task : this.cached) weight += task.weight;
        return weight;
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
            '}';
    }
}
