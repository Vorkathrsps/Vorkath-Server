package com.aelous.model.content.areas.wilderness.content;

import com.aelous.GameServer;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.areas.wilderness.content.activity.WildernessActivityManager;
import com.aelous.model.content.areas.wilderness.content.activity.impl.EdgevileActivity;
import com.aelous.model.content.areas.wilderness.content.activity.impl.PureActivity;
import com.aelous.model.content.areas.wilderness.content.activity.impl.ZerkerActivity;
import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.content.tasks.Requirements;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aelous.model.entity.player.QuestTab.InfoTab.*;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * The class which represents functionality for the BM rewards.
 * Credits go to the developers from OSS.
 * <p>
 * Update: December, 12, 2020, 18:33
 * Added support for new features such as wilderness events.
 * Redskull, trained accounts and many more. Also optimized the class.
 *
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 * Elderen</a>
 */
public class PlayerKillingRewards {

    private static final Logger logger = LogManager.getLogger(PlayerKillingRewards.class);

    public static void reward(Player killer, Player target, boolean valid) {
        // Add a death. Only when dying to a player.
        int dc = (Integer) target.getAttribOr(AttributeKey.PLAYER_DEATHS, 0) + 1;
        target.putAttrib(AttributeKey.PLAYER_DEATHS, dc);
        try {
            // Let's reward...

            // Add a kill when the kill is valid (not farming) and it's not in duel arena/FFA
            if (valid) {

                //Update daily tasks
                updateDailyTask(killer);

                //Update achievements
                updateAchievement(killer, target);

                //check for tasks
                checkForTask(killer);

                //Refill the killers special attack on kills.
                if (GameServer.properties().playerKillFillsSpec) {
                    killer.restoreSpecialAttack(100);
                }

                // Ruin his kill streak. Only when dying to a player.
                int target_killstreak = target.getAttribOr(AttributeKey.KILLSTREAK, 0);
                target.clearAttrib(AttributeKey.KILLSTREAK);

                //Update target killstreak
                target.getPacketSender().sendString(CURRENT_KILLSTREAK.childId, QuestTab.InfoTab.INFO_TAB.get(CURRENT_KILLSTREAK.childId).fetchLineData(target));

                //Increase the player killcount
                int killcount = (Integer) killer.getAttribOr(AttributeKey.PLAYER_KILLS, 0) + 1;
                killer.putAttrib(AttributeKey.PLAYER_KILLS, killcount);

                //Update the kills and deaths
                killer.getPacketSender().sendString(KILLS.childId, QuestTab.InfoTab.INFO_TAB.get(KILLS.childId).fetchLineData(killer));
                target.getPacketSender().sendString(DEATHS.childId, QuestTab.InfoTab.INFO_TAB.get(DEATHS.childId).fetchLineData(target));

                //Update the kdr
                killer.getPacketSender().sendString(KD_RATIO.childId, QuestTab.InfoTab.INFO_TAB.get(KD_RATIO.childId).fetchLineData(killer));
                target.getPacketSender().sendString(KD_RATIO.childId, QuestTab.InfoTab.INFO_TAB.get(KD_RATIO.childId).fetchLineData(target));

                // Elo rating check.
                EloRating.modify(killer, target);

                int killstreak = (Integer) killer.getAttribOr(AttributeKey.KILLSTREAK, 0) + 1;
                killer.putAttrib(AttributeKey.KILLSTREAK, killstreak);

                //Update the killstreak
                killer.getPacketSender().sendString(CURRENT_KILLSTREAK.childId, QuestTab.InfoTab.INFO_TAB.get(CURRENT_KILLSTREAK.childId).fetchLineData(killer));

                // Did we reach a new high in terms of KS?
                int ksRecord = killer.getAttribOr(AttributeKey.KILLSTREAK_RECORD, 0);
                if (killstreak > ksRecord) {
                    killer.putAttrib(AttributeKey.KILLSTREAK_RECORD, killstreak);
                    killer.getPacketSender().sendString(QuestTab.InfoTab.KILLSTREAK_RECORD.childId, QuestTab.InfoTab.INFO_TAB.get(QuestTab.InfoTab.KILLSTREAK_RECORD.childId).fetchLineData(killer));
                }

                // Killstreak going on?
                if (killstreak > 1) {
                    killer.message("You're currently on a killing spree of " + killstreak + "!");

                    if (killstreak % 5 == 0 || killstreak > 15) {
                        World.getWorld().getPlayers().forEach(player -> player.message("<col=ca0d0d><img=2011> " + killer.getUsername() + " has a killing spree of " + killstreak + " and can be shut down for " + (100 + player.shutdownValueOf(killstreak)) + " BM!"));
                    }
                }

                // Announce if you shut down a killstreak
                if (target_killstreak >= 5) {
                    World.getWorld().getPlayers().forEach(player -> player.message("<col=ca0d0d><img=2011> " + killer.getUsername() + " has shut down " + target.getUsername() + " with a killing spree of " + target_killstreak + "."));
                }

                // If this passes our shutdown record, change it
                int record = killer.getAttribOr(AttributeKey.SHUTDOWN_RECORD, 0);
                if (target_killstreak > record) {
                    killer.putAttrib(AttributeKey.SHUTDOWN_RECORD, target_killstreak);
                }

                //Update the wilderness streak
                int wilderness_killstreak = (Integer) killer.getAttribOr(AttributeKey.WILDERNESS_KILLSTREAK, 0) + 1;
                killer.putAttrib(AttributeKey.WILDERNESS_KILLSTREAK, wilderness_killstreak);

                //Only increase if the killer was infact in the risk arena
                if(killer.tile().insideRiskArea()) {
                    var increaseBy = 1;
                    //If u wanne edit this put it here
                    switch (killer.getMemberRights()) {
                        case RUBY_MEMBER -> increaseBy = 1;
                        case SAPPHIRE_MEMBER -> increaseBy = 1;
                        default -> increaseBy = 1;
                    }
                    var riskzonePoints = killer.<Integer>getAttribOr(AttributeKey.RISKZONE_POINTS, 0) + increaseBy;
                    killer.putAttrib(AttributeKey.RISKZONE_POINTS, riskzonePoints);
                    killer.message(Color.PURPLE.wrap("You have received "+increaseBy+" riskzone points, you now have a total of "+riskzonePoints+" riskzone points."));
                }

                boolean edgeActivity = WildernessActivityManager.getSingleton().isActivityCurrent(EdgevileActivity.class);
                boolean pureActivity = WildernessActivityManager.getSingleton().isActivityCurrent(PureActivity.class);
                boolean zerkerActivity = WildernessActivityManager.getSingleton().isActivityCurrent(ZerkerActivity.class);

                var bm = killer.bloodMoneyAmount(target);


                GroundItem bloodMoney = new GroundItem(new Item(BLOOD_MONEY, bm), target.tile(), killer);
                GroundItemHandler.createGroundItem(bloodMoney);

                killer.message(Color.RED.tag() + "<shad=0>[Blood Money]</col></shad> " + Color.BLUE.tag() + "You earn " + Color.VIOLET.tag() + "(+" + bm + ") blood money " + Color.BLUE.tag() + " after killing " + Color.VIOLET.tag() + "" + target.getUsername() + "" + Color.BLUE.tag() + "!");

                var risk = killer.<Long>getAttribOr(AttributeKey.RISKED_WEALTH, 0L);

                //If a player is risking over 50.000 BM roll for a extra reward
                if (World.getWorld().rollDie(35, 1) && risk > 50_000) {
                    killer.getRisk().reward();
                }

                //1 in 35 chance to receive a blood money casket
/*                if(World.getWorld().rollDie(35,1)) {
                    killer.inventory().addOrBank(new Item(BLOOD_MONEY_CASKET));
                    killer.message(Color.PURPLE.wrap("You've found a blood money casket searching the corpse of "+target.getUsername()+"."));
                }*/

                //1 in 10 chance to receive a mystery box
                if(World.getWorld().rollDie(10,1)) {
                    killer.inventory().addOrBank(new Item(MYSTERY_BOX));
                    killer.message(Color.PURPLE.wrap("You've found a mystery box searching the corpse of "+target.getUsername()+"."));
                }

                //1 in 1000 chance to receive a epic mystery box
                if(World.getWorld().rollDie(1000,1)) {
                    killer.inventory().addOrBank(new Item(VESTAS_LONGSWORD));
                    killer.message(Color.PURPLE.wrap("You've found a epic pet mystery box searching the corpse of "+target.getUsername()+"."));
                    World.getWorld().sendWorldMessage("<img=2013>" + killer.getUsername() + " " + "found a epic pet mystery box searching the corpse of "+target.getUsername()+".");
                }

                var whosKeysAreThese = killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WHOS_KEYS_ARE_THESE);

                //1 in 250 chance to receive a key of drops
                if(whosKeysAreThese && World.getWorld().rollDie(250,1)) {
                    killer.inventory().addOrBank(new Item(DONATOR_MYSTERY_BOX));
                    killer.message(Color.PURPLE.wrap("You've found a key of drops searching the corpse of "+target.getUsername()+"."));
                    World.getWorld().sendWorldMessage("<img=2013>" + killer.getUsername() + " " + "found a "+Color.BLUE.wrap("key of drops")+" searching the corpse of "+target.getUsername()+".");
                }
            }
        } catch (Exception e) {
            logger.error("fk", e);
        }
    }

    private static void updateDailyTask(Player killer) {
        int combatLevel = killer.getSkills().combatLevel();
        int defenceLevel = killer.getSkills().level(Skills.DEFENCE);
        boolean edgevile = killer.tile().region() == 12343 || killer.tile().region() == 12087;
        boolean revCave = killer.tile().region() == 12701 || killer.tile().region() == 12702 || killer.tile().region() == 12703 || killer.tile().region() == 12957 || killer.tile().region() == 12958 || killer.tile().region() == 12959;
        boolean above30Wild = WildernessArea.wildernessLevel(killer.tile()) > 30;
        boolean isPure = defenceLevel == 1 && combatLevel >= 80;
        boolean isZerker = defenceLevel == 45 && combatLevel >= 95;
        boolean wearing_body = killer.getEquipment().hasChest();
        boolean wearing_legs = killer.getEquipment().hasLegs();
        boolean noArm = !wearing_body && !wearing_legs;
        boolean wearingDharok = CombatConstants.wearingDharoksArmour(killer);

        //Edgeville area
        if (edgevile) {
            DailyTaskManager.increase(DailyTasks.EDGEVILE_KILLS, killer);
        }

        //Rev cave
        if (revCave) {
            DailyTaskManager.increase(DailyTasks.REV_CAVE_KILLS, killer);
        }

        //Deep wilderness
        if (above30Wild) {
            DailyTaskManager.increase(DailyTasks.DEEP_WILDERNESS, killer);
        }

        //Pure
        if (isPure && edgevile) {
            DailyTaskManager.increase(DailyTasks.PURE_KILLS, killer);
        }

        //Zerker
        if (isZerker && edgevile) {
            DailyTaskManager.increase(DailyTasks.ZERKER_KILLS, killer);
        }

        if(noArm) {
            DailyTaskManager.increase(DailyTasks.NO_ARM, killer);
        }

        if(wearingDharok) {
            DailyTaskManager.increase(DailyTasks.DHAROK, killer);
        }
    }

    private static void updateAchievement(Player killer, Player target) {
        // Starter trade prevention
        if (killer.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L) < 3000L && !killer.getPlayerRights().isDeveloper(killer) && !target.getPlayerRights().isDeveloper(target)) {
            killer.message("You are restricted from completing achievements until 30 minutes of play time.");
            killer.message("Only " + Math.ceil((int) (3000.0 - killer.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L)) / 100.0) + "minutes left.");
            return;
        }

        if (target.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L) < 3000L && !target.getPlayerRights().isDeveloper(target) && !killer.getPlayerRights().isDeveloper(killer)) {
            killer.message("Your partner is restricted from completing achievements until 30 minutes of play time.");
            killer.message("Only " + Math.ceil((int) (3000.0 - killer.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L)) / 100.0) + "minutes left.");
            return;
        }

        AchievementsManager.activate(killer, Achievements.PVP_I, 1);
        AchievementsManager.activate(killer, Achievements.PVP_II, 1);
        AchievementsManager.activate(killer, Achievements.PVP_III, 1);


        boolean usedSpecialAttack = killer.getAttribOr(AttributeKey.SPECIAL_ATTACK_USED, false);

        if (!usedSpecialAttack) {
            AchievementsManager.activate(killer, Achievements.KEEP_IT_100_I, 1);
            AchievementsManager.activate(killer, Achievements.KEEP_IT_100_II, 1);
            AchievementsManager.activate(killer, Achievements.KEEP_IT_100_III, 1);
        }

        boolean wearing_body = killer.getEquipment().hasChest();
        boolean wearing_legs = killer.getEquipment().hasLegs();
        boolean wielding_weapon = killer.getEquipment().hasWeapon();

        if (!wielding_weapon) {
            AchievementsManager.activate(killer, Achievements.PUNCHING_BAGS_I, 1);
            AchievementsManager.activate(killer, Achievements.PUNCHING_BAGS_II, 1);
            AchievementsManager.activate(killer, Achievements.PUNCHING_BAGS_III, 1);
        }

        if (!wearing_body && !wearing_legs) {
            AchievementsManager.activate(killer, Achievements.AMPUTEE_ANNIHILATION_I, 1);
            AchievementsManager.activate(killer, Achievements.AMPUTEE_ANNIHILATION_II, 1);
            AchievementsManager.activate(killer, Achievements.AMPUTEE_ANNIHILATION_III, 1);
        }

        //Killer needs killstreak of +25 to unlock
        int killstreak = killer.getAttribOr(AttributeKey.KILLSTREAK, 0);
        if (killstreak >= 25) {
            AchievementsManager.activate(killer, Achievements.BLOODTHIRSTY_I, 1);
        }

        //Killer needs killstreak of +50 to unlock
        if (killstreak >= 50) {
            AchievementsManager.activate(killer, Achievements.BLOODTHIRSTY_II, 1);
        }
        //Killer needs to end a killstreak of +50 to unlock
        int target_killstreak = target.getAttribOr(AttributeKey.KILLSTREAK, 0);
        if (target_killstreak >= 50) {
            AchievementsManager.activate(killer, Achievements.BLOODTHIRSTY_III, 1);
        }

        int wilderness_killstreak = (Integer) killer.getAttribOr(AttributeKey.WILDERNESS_KILLSTREAK, 0) + 1;
        if (wilderness_killstreak >= 5) {
            AchievementsManager.activate(killer, Achievements.SURVIVOR_I, 1);
        }

        if (wilderness_killstreak >= 10) {
            AchievementsManager.activate(killer, Achievements.SURVIVOR_II, 1);
        }

        if (WildernessArea.wildernessLevel(killer.tile()) >= 30) {
            AchievementsManager.activate(killer, Achievements.DEEP_WILD_I, 1);
            AchievementsManager.activate(killer, Achievements.DEEP_WILD_II, 1);
            AchievementsManager.activate(killer, Achievements.DEEP_WILD_III, 1);
        }

        if (WildernessArea.wildernessLevel(killer.tile()) >= 50) {
            AchievementsManager.activate(killer, Achievements.EXTREME_DEEP_WILD_I, 1);
            AchievementsManager.activate(killer, Achievements.EXTREME_DEEP_WILD_II, 1);
            AchievementsManager.activate(killer, Achievements.EXTREME_DEEP_WILD_III, 1);
        }

        int combatLevel = killer.getSkills().combatLevel();
        int defenceLevel = killer.getSkills().level(Skills.DEFENCE);

        if (defenceLevel == 1 && combatLevel >= 80) {
            AchievementsManager.activate(killer, Achievements.PURE_I, 1);
            AchievementsManager.activate(killer, Achievements.PURE_II, 1);
            AchievementsManager.activate(killer, Achievements.PURE_III, 1);
            AchievementsManager.activate(killer, Achievements.PURE_IV, 1);
        }

        if (defenceLevel == 45 && combatLevel >= 95) {
            AchievementsManager.activate(killer, Achievements.ZERKER_I, 1);
            AchievementsManager.activate(killer, Achievements.ZERKER_II, 1);
            AchievementsManager.activate(killer, Achievements.ZERKER_III, 1);
            AchievementsManager.activate(killer, Achievements.ZERKER_IV, 1);
        }

        if (CombatConstants.wearingFullDharoks(killer)) {
            if (killer.hp() < 25) {
                AchievementsManager.activate(killer, Achievements.DHAROK_BOMBER_I, 1);
            }

            if (killer.hp() < 15) {
                AchievementsManager.activate(killer, Achievements.DHAROK_BOMBER_II, 1);
            }
        }
    }

    private static void checkForTask(Player player) {

        if (CombatConstants.wearingFullDharoks(player)) {
            player.getTaskMasterManager().increase(Tasks.WEAR_FULL_DH_TASK);
        }

        if (Requirements.bmRisk(player) > 20_000 && !Prayers.usingPrayer(player, Prayers.PROTECT_ITEM)) {
            player.getTaskMasterManager().increase(Tasks.KILL_WITH_20K_BM_RISK);
        }

        if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.DRAGON_SCIMITAR_OR)) {
            player.getTaskMasterManager().increase(Tasks.KILL_WITH_DRAGON_SCIMITAR_OR);
        }

        if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.INQUISITORS_MACE)) {
            player.getTaskMasterManager().increase(Tasks.KILL_WITH_INQUISITORS_MACE);
        }

        if (!player.getEquipment().hasHead() || !player.getEquipment().hasChest() || !player.getEquipment().hasLegs()) {
            player.getTaskMasterManager().increase(Tasks.KILL_WITHOUT_HEAD_BODY_AND_LEGS);
        }

        if (!player.getEquipment().hasRing() || !player.getEquipment().hasAmulet() || !player.getEquipment().hasHands()) {
            player.getTaskMasterManager().increase(Tasks.KILL_WITHOUT_RING_AMULET_AND_GLOVES);
        }

        if (player.getEquipment().hasAt(EquipSlot.HEAD, OBSIDIAN_HELMET) || player.getEquipment().hasAt(EquipSlot.BODY, OBSIDIAN_PLATEBODY) || player.getEquipment().hasAt(EquipSlot.LEGS, OBSIDIAN_PLATELEGS)) {
            player.getTaskMasterManager().increase(Tasks.KILL_WEARING_FULL_OBSIDIAN);
        }

        if (!player.getSkills().combatStatsBoosted()) {
            player.getTaskMasterManager().increase(Tasks.KILL_WITHOUT_BOOSTED_STATS);
        }
    }
}
