package com.aelous.model.content.skill.impl.slayer;

import com.aelous.GameServer;
import com.aelous.model.content.skill.impl.slayer.slayer_reward_interface.SlayerExtendable;
import com.aelous.model.content.skill.impl.slayer.slayer_reward_interface.SlayerRewardActions;
import com.aelous.model.content.skill.impl.slayer.slayer_reward_interface.SlayerRewardButtons;
import com.aelous.model.content.skill.impl.slayer.slayer_reward_interface.SlayerUnlockable;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerTaskDef;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import static com.aelous.model.content.skill.impl.slayer.SlayerConstants.DRAKE;
import static com.aelous.model.content.skill.impl.slayer.SlayerConstants.*;
import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.model.entity.player.QuestTab.InfoTab.SLAYER_POINTS;

/**
 * @author Patrick van Elderen | December, 21, 2020, 13:20
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

    /**
     * The list of all blocked slayer tasks.
     */
    private ArrayList<Integer> blocked = new ArrayList<>();

    public ArrayList<Integer> getBlocked() {
        return blocked;
    }

    public void setBlocked(ArrayList<Integer> blocked) {
        this.blocked = blocked;
    }

    public boolean isTaskBlocked(SlayerTaskDef task) {
        return player.getSlayerRewards().blocked.contains(task.getCreatureUid());
    }

    /**
     * The list of all slayer tasks extensions.
     */

    private HashMap<Integer, String> extendable = new HashMap<>();

    public HashMap<Integer, String> getExtendable() {
        return extendable;
    }

    public void setExtendable(HashMap<Integer, String> extension) {
        this.extendable = extension;
    }

    public int slayerTaskAmount(Player player, SlayerTaskDef def) {
        if(extendable.containsKey(ADAMIND_SOME_MORE)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(RUUUUUNE)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(BARRELCHEST)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(FLUFFY)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(PURE_CHAOS)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(CORPOREAL_LECTURE)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(CRAZY_SCIENTIST)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(GORILLA_DEMON)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(DRAGON_SLAYER)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(SCYLLA)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(JUMPING_JACKS)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(SPOOKY_SCARY_SKELETONS)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(ATOMIC_BOMB)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(VORKI)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(NAGINI)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(WYVER_ANOTHER_ONE)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(ARAGOG)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(BEWEAR)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(DRAKE)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(WYRM_ME_ON)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(DR_CHAOS)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(DIG_ME_UP)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(LAVA)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(WORLD_BOSSILONGER)) {
            return (int) (def.range(player) * 1.4);
        }
        if(extendable.containsKey(GOD_WAR)) {
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
        if (!player.getSlayerRewards().unlocks.containsKey(SlayerConstants.TZTOK_JAD) && task.getCreatureUid() == 97) {
            return false;
        }
        return true;
    }

    /**
     * Stores all unlocked HashMaps
     */
    private HashMap<Integer, String> unlocks = new HashMap<>();

    public HashMap<Integer, String> getUnlocks() {
        return unlocks;
    }

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

    public void setUnlocks(HashMap<Integer, String> unlocks) {
        this.unlocks = unlocks;
    }

    public void open() {
        sendTaskInformation();
        Slayer.displayCurrentAssignment(player);
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

        player.getPacketSender().sendString(63110, "Pay "+selectedButton.getRewardPoints()+" points?");
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

        player.getPacketSender().sendString(63110, "Pay "+selectedButton.getRewardPoints()+" points?");
    }

    private void blockWidget() {
        player.putAttrib(SLAYER_UI_ACTION,1);
        String name = Slayer.taskName(player.getAttribOr(SLAYER_TASK_ID, 0));
        player.getPacketSender().sendString(63106, "You are about to block: " + name);
        player.getPacketSender().sendString(63107, "This costs 100 Slayer Points");
        player.getPacketSender().sendString(63110, "<col=ca0d0d>Are you sure you want to pay?</col>");
        player.getInterfaceManager().open(63100);
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
            Slayer.displayCurrentAssignment(player);
            for (int index = 0; index < 6; index++) {
                player.getPacketSender().sendString(63220 + index, "Empty");
                player.getPacketSender().sendString(63232 + index, "<col=-8434673>Unblock Task</col>");
                if (player.getSlayerRewards().getBlocked().size() > 0 && player.getSlayerRewards().getBlocked().size() > index) {
                    int task = player.getSlayerRewards().getBlocked().get(index);
                    SlayerCreature assignedTask = SlayerCreature.lookup(task);
                    player.getPacketSender().sendString(63220 + index, Utils.formatEnum(assignedTask.name()));
                } else {
                    player.getPacketSender().sendString(63220 + index, "Empty");
                }
                player.getPacketSender().sendString(63232 + index, "<col=ffa500>Unblock Task </col>");
            }
        } catch (Exception e) {
            logger.catching(e);
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

        switch (slayerRewardButtons.getAction()) {
            case UNLOCK_INTERFACE:
                if (GameServer.properties().debugMode && player.getPlayerRights().isOwner(player)) {
                    player.message("Opening unlock interface");
                }
                SlayerUnlockable.updateInterface(player);
                for (SlayerUnlockable unlockable : SlayerUnlockable.values()) {
                    player.getPacketSender().sendConfig(750 + unlockable.ordinal(), player.getSlayerRewards().getUnlocks().containsKey(unlockable.getButtonId()) ? 1 : 0);
                }
                player.getInterfaceManager().open(slayerRewardButtons.getInterface());
                setPreviousInterface(slayerRewardButtons.getInterface());
                return true;

            case BUY_INTERFACE:
                if (GameServer.properties().debugMode && player.getPlayerRights().isDeveloper(player)) {
                    player.message("Opening buy interface");
                }
                World.getWorld().shop(7).open(player);
                return true;
            case CANCEL:
                Slayer.cancelTask(player,false);
                return true;

            case BLOCK:
                int num = player.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);
                if (num > 0) {
                    blockWidget();
                } else {
                    player.message("You do not have a slayer task at this time.");
                }
                return true;

            case UNBLOCK:
                player.getSlayerRewards().unblock(button);
                return true;

            case TASK_INTERFACE:
                player.debugMessage("Opening task interface");
                sendTaskInformation();
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
                    if (slayerRewardButtons.getAction() == SlayerRewardActions.UNLOCK_BUTTON) {
                        player.getPacketSender().sendConfig(750 + (slayerRewardButtons.ordinal() - 10), 1);
                        player.message("You cannot undo this purchase!");
                        return false;
                    }
                }

                openUnlockWidget(button);
                player.slayerWidgetActions(button, slayerRewardButtons.name(), slayerRewardButtons.getConfigId(), 0);
                return true;

            case EXTEND_BUTTON:
                if (GameServer.properties().debugMode && player.getPlayerRights().isDeveloper(player)) {
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
                        if(unlockable == null) {
                            return false;
                        }

                        if (!player.getSlayerRewards().getUnlocks().containsKey(selectedChild)) {
                            if (purchase(unlockable.getRewardPoints())) {
                                player.getPacketSender().sendConfig(750 + unlockable.ordinal(), 0);
                                player.message("You do not have enough reward points.");
                                return false;
                            }

                            player.getSlayerRewards().getUnlocks().put(selectedChild, name);
                            player.getPacketSender().sendConfig(750 + unlockable.ordinal(), 1);
                            player.getInterfaceManager().open(getPreviousInterface());
                            player.message("You successfully purchased " + Utils.capitalizeJustFirst(name).replaceAll("_", " ")+".");
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
                        player.message("You successfully purchased " + Utils.capitalizeJustFirst(name).replaceAll("_", " ")+".");
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
                        logger.catching(e);
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

    /**
     * Blocks the current assigned slayer task.
     */
    public void block() {
        int pts = player.getAttribOr(SLAYER_REWARD_POINTS, 0);
        int required = 100;
        boolean extremeMember = player.getMemberRights().isExtremeMemberOrGreater(player);

        if (pts < 100 && !extremeMember) {
            player.message("You need " + required + " points to block your task.");
        } else {
            SlayerCreature assignedTask = SlayerCreature.lookup(player.slayerTaskId());

            //Firstly we check to see if the task has already been blocked!
            if (player.getSlayerRewards().getBlocked() != null && player.getSlayerRewards().getBlocked().contains(assignedTask.uid)) {
                player.message("This task is already blocked... Report to a Administrator"); //Huh how can we block a task that was already blocked?
                return;
            }

            //Next, we check if we have any available slots, if we do, BLOCK! :^ )
            if (blocked.size() >= 6) {
                player.message("You can only block up to 6 tasks.");
                player.getInterfaceManager().open(getPreviousInterface());
                return;
            }

            blocked.add(assignedTask.uid);
            if (!extremeMember) {
                player.putAttrib(SLAYER_REWARD_POINTS, pts - required);
            }
            player.putAttrib(SLAYER_TASK_ID, 0);
            player.putAttrib(AttributeKey.SLAYER_TASK_AMT,0);
            player.message("You have successfully blocked your task.");
            sendTaskInformation();
            player.getInterfaceManager().open(getPreviousInterface());
            player.putAttrib(SLAYER_UI_ACTION,0);
        }
    }

    /**
     * Unblocks the slayer task.
     */
    public void unblock(int button) {
        if (blocked.isEmpty()) {
            //System.out.println("Nothing to block");
            return;
        }

        int size = blocked.size();
        int max = 63226 + size;
        if (button == 63226) {
            if (max >= 63226)
                blocked.remove(0);
        } else if (button == 63227 && size >= 2) {
            if (max >= 63227)
                blocked.remove(1);
        } else if (button == 63228 && size >= 3) {
            if (max >= 63228)
                blocked.remove(2);
        } else if (button == 63229 && size >= 4) {
            if (max >= 63229)
                blocked.remove(3);
        } else if (button == 63230 && size >= 5) {
            if (max >= 63230)
                blocked.remove(4);
        } else if (button == 63231 && size >= 6) {
            if (max >= 63231)
                blocked.remove(5);
        }
        blocked.trimToSize();
        sendTaskInformation();
    }
}
