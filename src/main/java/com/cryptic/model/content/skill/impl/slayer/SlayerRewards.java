package com.cryptic.model.content.skill.impl.slayer;

import com.cryptic.GameServer;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.skill.impl.slayer.slayer_reward_interface.SlayerExtendable;
import com.cryptic.model.content.skill.impl.slayer.slayer_reward_interface.SlayerRewardActions;
import com.cryptic.model.content.skill.impl.slayer.slayer_reward_interface.SlayerRewardButtons;
import com.cryptic.model.content.skill.impl.slayer.slayer_reward_interface.SlayerUnlockable;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTaskDef;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.QuestTab;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.cryptic.model.content.skill.impl.slayer.SlayerConstants.DRAKE;
import static com.cryptic.model.content.skill.impl.slayer.SlayerConstants.*;
import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.model.entity.player.QuestTab.InfoTab.SLAYER_POINTS;

/**
 * @author Origin | December, 21, 2020, 13:20
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class SlayerRewards {

    /**
     * The local class logger
     */
    private static final Logger logger = LogManager.getLogger(SlayerRewards.class);

    private final Player player;

    public SlayerRewards(Player player) {
        this.player = player;
    }
    @Getter @Setter
    List<Integer> blockedSlayerTask = new ArrayList<>();


    /**
     * The list of all slayer tasks extensions.
     */

    @Getter
    @Setter
    private HashMap<Integer, String> extendable = new HashMap<>();

    public int slayerTaskAmount(Player player, SlayerTaskDef def) {
        if (extendable.containsKey(ADAMIND_SOME_MORE)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(RUUUUUNE)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(BARRELCHEST)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(FLUFFY)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(PURE_CHAOS)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(CORPOREAL_LECTURE)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(CRAZY_SCIENTIST)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(GORILLA_DEMON)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(DRAGON_SLAYER)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(SCYLLA)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(JUMPING_JACKS)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(SPOOKY_SCARY_SKELETONS)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(ATOMIC_BOMB)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(VORKI)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(NAGINI)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(WYVER_ANOTHER_ONE)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(ARAGOG)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(BEWEAR)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(DRAKE)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(WYRM_ME_ON)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(DR_CHAOS)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(DIG_ME_UP)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(LAVA)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(WORLD_BOSSILONGER)) {
            return (int) (def.range(player) * 1.4);
        }
        if (extendable.containsKey(GOD_WAR)) {
            return (int) (def.range(player) * 1.4);
        }
        return def.range(player);
    }

    public boolean canAssign(SlayerTaskDef task) {
        if (!player.getSlayerRewards().unlocks.containsKey(GOD_WAR) && task.getCreatureUid() == 130) {
            return false;
        }
        if (!player.getSlayerRewards().unlocks.containsKey(SlayerConstants.GOD_WAR) && task.getCreatureUid() == 137) {
            return false;
        }
        if (!player.getSlayerRewards().unlocks.containsKey(SlayerConstants.GOD_WAR) && task.getCreatureUid() == 133) {
            return false;
        }
        if (!player.getSlayerRewards().unlocks.containsKey(SlayerConstants.GOD_WAR) && task.getCreatureUid() == 127) {
            return false;
        }
        if (!player.getSlayerRewards().unlocks.containsKey(SlayerConstants.LIKE_A_BOSS) && task.getCreatureUid() == 145) {
            return false;
        }
        return true;
    }

    /**
     * Stores all unlocked HashMaps
     */
    @Setter
    @Getter
    private HashMap<Integer, String> unlocks = new HashMap<>();

    /**
     * Stores previous interface being viewed
     */
    private static int prevInterfaceId = 63400;

    /**
     * Gets the previous interface
     */
    public static int getPreviousInterface() {
        return prevInterfaceId;
    }

    /**
     * Sets the previous interface
     */
    private void setPreviousInterface(int interfaceId) {
        prevInterfaceId = interfaceId;
    }

    public void open() {
        SlayerTask task = World.getWorld().getSlayerTasks();
        SlayerTask assignment = task.getCurrentAssignment(player);
        task.sendTaskInformation(player);
        if (assignment == null) player.message("You currently do not have an assigned Slayer task.");
        else
            player.message(Color.BLUE.wrap("Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + assignment.getRemainingTaskAmount(player)));
        player.getInterfaceManager().open(63200);
    }

    private void openUnlockWidget(int id) {
        final SlayerUnlockable selectedButton = SlayerUnlockable.byButton(id);
        if (selectedButton == null)
            return;

        player.getInterfaceManager().open(63100);
        player.getPacketSender().sendString(63106, Utils.optimizeText(selectedButton.getName()));
        player.getPacketSender().sendString(63107, selectedButton.getDescription());

        for (int lineId = 63108; lineId <= 63110; lineId++)
            player.getPacketSender().sendString(lineId, "");

        player.getPacketSender().sendString(63110, "Pay " + selectedButton.getRewardPoints() + " points?");
    }

    private void toggleExtendState(int id) {
        final SlayerExtendable selectedButton = SlayerExtendable.byButton(id);
        if (selectedButton == null)
            return;

        player.getInterfaceManager().open(63100);
        player.getPacketSender().sendString(63106, Utils.optimizeText(selectedButton.getName()));
        player.getPacketSender().sendString(63107, selectedButton.getDescription());

        for (int lineId = 63108; lineId <= 63110; lineId++)
            player.getPacketSender().sendString(lineId, "");

        player.getPacketSender().sendString(63110, "Pay " + selectedButton.getRewardPoints() + " points?");
    }

    private void blockWidget() {
        player.putAttrib(SLAYER_UI_ACTION, 1);
        SlayerTask task = World.getWorld().getSlayerTasks();
        SlayerTask assignment = task.getCurrentAssignment(player);
        if (assignment != null) {
            String name = assignment.getTaskName();
            player.getPacketSender().sendString(63106, "You are about to block: " + name);
            player.getPacketSender().sendString(63107, "This costs 100 Slayer Points");
            player.getPacketSender().sendString(63110, "<col=ca0d0d>Are you sure you want to pay?</col>");
            player.getInterfaceManager().open(63100);
        }
    }

    private boolean purchase(int amount) {
        var slayerRewardPoints = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        if (slayerRewardPoints >= amount) {
            player.putAttrib(SLAYER_REWARD_POINTS, slayerRewardPoints - amount);
            player.getPacketSender().sendString(63014, "Reward Points: " + Utils.formatNumber(slayerRewardPoints));
            player.getPacketSender().sendString(SLAYER_POINTS.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_POINTS.childId).fetchLineData(player));
            return false;
        } else {
            return true;
        }
    }

    private void sendTaskInformation() {
        try {
            SlayerTask slayer = World.getWorld().getSlayerTasks();
            SlayerTask assignment = slayer.getCurrentAssignment(player);
            if (assignment == null) player.message("You currently do not have an assigned Slayer task.");
            else
                player.message(Color.BLUE.wrap("Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + assignment.getRemainingTaskAmount(player)));

            for (int index = 0; index < 6; index++) {
                player.getPacketSender().sendString(63220 + index, "Empty");
                player.getPacketSender().sendString(63232 + index, "<col=-8434673>Unblock Task</col>");
                if (!player.getSlayerRewards().getBlockedSlayerTask().isEmpty() && player.getSlayerRewards().getBlockedSlayerTask().size() > index) {
                    if (assignment != null) {
                        player.getPacketSender().sendString(63220 + index, Utils.formatEnum(assignment.getTaskName()));
                    }
                } else {
                    player.getPacketSender().sendString(63220 + index, "Empty");
                }
                player.getPacketSender().sendString(63232 + index, "<col=ffa500>Unblock Task </col>");
            }
        } catch (Exception e) {
            logger.error("sadge", e);
        }
    }

    public boolean handleButtonInteraction(Player player, int button) {
        SlayerRewardButtons slayerRewardButtons = SlayerRewardButtons.rewardButtonsHashMap.get(button);
        if (slayerRewardButtons == null) {
            return false;
        }

        var slayerWidgetAction = player.<Integer>getAttribOr(SLAYER_UI_ACTION, 0);
        var selectedChild = player.<Integer>getAttribOr(SLAYER_WIDGET_BUTTON_ID, 0);
        var configId = player.<Integer>getAttribOr(SLAYER_WIDGET_CONFIG, 0);
        var type = player.<Integer>getAttribOr(SLAYER_WIDGET_TYPE, 0);
        var name = player.<String>getAttribOr(SLAYER_WIDGET_NAME, "");
        SlayerTask task = World.getWorld().getSlayerTasks();
        SlayerTask assignment = task.getCurrentAssignment(player);

        switch (slayerRewardButtons.getAction()) {
            case UNLOCK_INTERFACE:
                if (GameServer.properties().debugMode && player.getPlayerRights().isOwner(player)) {
                    player.message("Opening unlock interface");
                }
                SlayerUnlockable.updateInterface(player);
                for (SlayerUnlockable unlockable : SlayerUnlockable.values) {
                    player.getPacketSender().sendConfig(750 + unlockable.ordinal(), player.getSlayerRewards().getUnlocks().containsKey(unlockable.getButtonId()) ? 1 : 0);
                }
                player.getInterfaceManager().open(slayerRewardButtons.getInterface());
                setPreviousInterface(slayerRewardButtons.getInterface());
                return true;

            case BUY_INTERFACE:
                if (GameServer.properties().debugMode && player.getPlayerRights().isCommunityManager(player)) {
                    player.message("Opening buy interface");
                }
                World.getWorld().shop(7).open(player);
                return true;
            case CANCEL:
                task.sendCancelTaskDialouge(player);
                return true;

            case BLOCK:
                if (assignment != null) {
                    int num = assignment.getRemainingTaskAmount(player);
                    if (num > 0) {
                        blockWidget();
                    }
                } else {
                    player.message("You do not have a slayer task at this time.");
                }
                return true;

            case UNBLOCK:
                player.getSlayerRewards().unblock(button);
                return true;

            case TASK_INTERFACE:
                player.debugMessage("Opening task interface");
                SlayerTask slayer = World.getWorld().getSlayerTasks();
                slayer.sendTaskInformation(player);
                setPreviousInterface(slayerRewardButtons.getInterface());
                player.getInterfaceManager().open(slayerRewardButtons.getInterface());
                return true;

            case EXTEND_INTERFACE:
                for (SlayerExtendable extendable : SlayerExtendable.values()) {
                    player.getPacketSender().sendConfig(560 + extendable.ordinal(), player.getSlayerRewards().getExtendable().containsKey(extendable.getButtonId()) ? 1 : 0);
                }
                SlayerExtendable.updateInterface(player);
                player.getInterfaceManager().open(slayerRewardButtons.getInterface());
                setPreviousInterface(slayerRewardButtons.getInterface());

                int streak = player.getAttribOr(AttributeKey.SLAYER_TASK_SPREE, 0);
                int record = player.getAttribOr(AttributeKey.SLAYER_TASK_SPREE_RECORD, 0);
                player.getPacketSender().sendString(63399, "Current Streak: " + streak + " Record: " + record);
                return true;

            case UNLOCK_BUTTON:
                player.debugMessage("Button: " + button + " trying to open interface " + slayerRewardButtons.getInterface());
                if (player.getSlayerRewards().getUnlocks().containsKey(button)) {
                    player.getPacketSender().sendConfig(750 + (slayerRewardButtons.ordinal() - 10), 1);
                    player.message("You cannot undo this purchase!");
                    return false;
                }

                openUnlockWidget(button);
                player.slayerWidgetActions(button, slayerRewardButtons.name(), slayerRewardButtons.getConfigId(), 0);
                return true;

            case EXTEND_BUTTON:
                if (GameServer.properties().debugMode && player.getPlayerRights().isCommunityManager(player)) {
                    player.message("Opening extend button");
                }

                toggleExtendState(button);
                player.slayerWidgetActions(button, slayerRewardButtons.name(), slayerRewardButtons.getConfigId(), 1);
                return true;

            case CONFIRM:

                if (slayerWidgetAction == 0) {
                    //Unlock feature
                    if (type == 0) {
                        SlayerUnlockable unlockable = SlayerUnlockable.byButton(selectedChild);
                        if (unlockable == null) {
                            return false;
                        }

                        if (!player.getSlayerRewards().getUnlocks().containsKey(selectedChild)) {
                            if (purchase(unlockable.getRewardPoints())) {
                                player.getPacketSender().sendConfig(750 + unlockable.ordinal(), 0);
                                player.message("You do not have enough reward points.");
                                return false;
                            }

                            handleAchievements(player, name);
                            player.getSlayerRewards().getUnlocks().put(selectedChild, name);
                            player.getPacketSender().sendConfig(750 + unlockable.ordinal(), 1);
                            player.getInterfaceManager().open(getPreviousInterface());
                            player.message("You successfully purchased " + Utils.capitalizeJustFirst(name).replaceAll("_", " ") + ".");
                            return true;
                        }
                    }

                    if (type == 1) {
                        final SlayerExtendable extendable = SlayerExtendable.byButton(selectedChild);

                        if (purchase(extendable.getRewardPoints())) {
                            player.getPacketSender().sendConfig(560 + extendable.ordinal(), 0);
                            player.message("You do not have enough reward points.");
                            return false;
                        }

                        player.getSlayerRewards().getExtendable().put(selectedChild, name);
                        player.message("You successfully purchased " + Utils.capitalizeJustFirst(name).replaceAll("_", " ") + ".");
                        player.getPacketSender().sendConfig(560 + extendable.ordinal(), 1);
                        player.getInterfaceManager().open(getPreviousInterface());
                        return true;
                    }
                }

                //Block tasks
                if (slayerWidgetAction == 1) {
                    try {
                        player.getSlayerRewards().block();
                        return true;
                    } catch (Exception e) {
                        logger.error("sadge", e);
                    }
                }

            case BACK:
                player.getPacketSender().sendConfig(configId, 0);
                player.getInterfaceManager().open(getPreviousInterface());
                return true;

            case CLOSE:
                player.getInterfaceManager().close();
                return true;
        }
        return false;
    }

    private static void handleAchievements(Player player, String name) {
        if (name.equalsIgnoreCase("attuned_luck")) {
            AchievementsManager.activate(player, Achievements.SIGIL_HUNTER, 1);
        } else if (name.equalsIgnoreCase("ancient_blessing")) {
            AchievementsManager.activate(player, Achievements.WHAT_A_BLESSING, 1);
        } else if (name.equalsIgnoreCase("deaths_touch")) {
            AchievementsManager.activate(player, Achievements.GRIM, 1);
        } else if (name.equalsIgnoreCase("slayers_greed")) {
            AchievementsManager.activate(player, Achievements.GREEDY, 1);
        }
    }

    /**
     * Blocks the current assigned slayer task.
     */
    public void block() {
        int pts = player.<Integer>getAttribOr(SLAYER_REWARD_POINTS, 0);
        int required = 100;
        SlayerTask slayer = World.getWorld().getSlayerTasks();
        SlayerTask assignment = slayer.getCurrentAssignment(player);
        if (pts < 100) {
            player.message(Color.RED.wrap("You need " + required + " points to block your task."));
            return;
        }
        if (player.getSlayerRewards().getBlockedSlayerTask() != null) {
            if (player.getSlayerRewards().getBlockedSlayerTask().contains(assignment.getUid())) {
                player.message("This task is already blocked... Report to a Administrator");
                return;
            }
        }
        if (blockedSlayerTask.size() >= 6) {
            player.message("You can only block up to 6 tasks.");
            player.getInterfaceManager().open(getPreviousInterface());
            return;
        }
        if (assignment != null) {
            slayer.blockTask(player, assignment);
            slayer.cancelSlayerTask(player, true, false);
            slayer.sendTaskInformation(player);
            player.getInterfaceManager().open(getPreviousInterface());
            player.putAttrib(SLAYER_UI_ACTION, 0);
            player.message(Color.BLUE.wrap("You have successfully blocked your task."));
        }
    }

    /**
     * Unblocks the slayer task.
     */
    public void unblock(int button) {
        SlayerTask slayerTask = World.getWorld().getSlayerTasks();
        if (blockedSlayerTask.isEmpty()) {
            return;
        }

        int size = blockedSlayerTask.size();
        int max = 63226 + size;
        if (button == 63226) {
            if (max >= 63226)
                blockedSlayerTask.remove(0);
        } else if (button == 63227 && size >= 2) {
            if (max >= 63227)
                blockedSlayerTask.remove(1);
        } else if (button == 63228 && size >= 3) {
            if (max >= 63228)
                blockedSlayerTask.remove(2);
        } else if (button == 63229 && size >= 4) {
            if (max >= 63229)
                blockedSlayerTask.remove(3);
        } else if (button == 63230 && size >= 5) {
            if (max >= 63230)
                blockedSlayerTask.remove(4);
        } else if (button == 63231 && size >= 6) {
            if (max >= 63231)
                blockedSlayerTask.remove(5);
        }
        slayerTask.sendTaskInformation(player);
    }
}
