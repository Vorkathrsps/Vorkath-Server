package com.aelous.model.content.bountyhunter;

import com.aelous.GameServer;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.areas.wilderness.content.PlayerKillingRewards;
import com.aelous.model.content.areas.wilderness.content.todays_top_pkers.TopPkers;
import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.mechanics.Death;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.content.bountyhunter.emblem.BountyHunterEmblem;
import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.aelous.model.entity.attributes.AttributeKey.EMBLEM_WEALTH;
import static com.aelous.utility.ItemIdentifiers.ANTIQUE_EMBLEM_TIER_1;
import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.aelous.utility.Utils.formatNumber;

/**
 * The bounty hunter minigame as of 4 November 2019.
 * 
 * @author Professor Oak
 * @author Zerikoth
 */
public class BountyHunter {

    /**
     * All players currently in the wilderness.
     */
    public static final List<Player> PLAYERS_IN_WILD = new ArrayList<>();

    /**
     * Target pairs.
     */
    private static final List<TargetPair> TARGET_PAIRS = new ArrayList<>();

    /**
     * Processes the bounty hunter system for the specified
     * player.
     */
    public static void sequence(Player player) {

        //Get our target..
        Optional<Player> target = getTargetfor(player);

        target.ifPresent(value -> player.getPacketSender().sendString(53723, "Target: <col=65280>" + value.getUsername() + " (" + WildernessArea.wildernessLevel(value.tile()) + ")"));

        //Is player in the wilderness?
        if (WildernessArea.inWild(player)) {
            //Check if the player has a target.
            //If not, search for a new one.
            if (target.isEmpty()) {
                //Only search for a target every {@code TARGET_DELAY_SECONDS}.
                if (!player.getTargetSearchTimer().active()) {
                    //Make sure we're a valid target..
                    if (!validTargetContester(player)) {
                        return;
                    }

                    //Search for a new target for the player..
                    for (final Player potential : PLAYERS_IN_WILD) {
                        //Check if player2 is a valid target..
                        if (validTargetContester(potential)) {
                            //Check other stuff...

                           if(potential.getHostAddress().equalsIgnoreCase(player.getHostAddress())) {
                                continue;
                            }

                            if(potential.looks().hidden()) {
                                continue;
                            }

                            //Skip risk arena
                            if(potential.tile().insideRiskArea() || player.tile().insideRiskArea()) {
                                continue;
                            }

                            //Check if we aren't looping ourselves..
                            if (player.equals(potential)) {
                                continue;
                            }

                            //Check that we haven't killed this player before..
                            if (player.getRecentKills().contains(potential.getHostAddress()) && GameServer.properties().production) {
                                continue;
                            }

                            //Skip clan mates
                            if(player.getClanChat().equalsIgnoreCase(potential.getClanChat())) {
                                continue;
                            }

                            if(Math.abs(player.getSkills().combatLevel() - potential.getSkills().combatLevel()) > 5) {
                                continue;
                            }

                            assign(player, potential);
                            break;
                        }
                    }
                    player.getTargetSearchTimer().start(TARGET_SEARCH_DELAY_SECONDS);
                }
            }
        } else {
            //Player leaves wilderness remove target, otherwise it takes to long to assign new target.
            if (target.isPresent()) {
                unassign(player);
                target.get().message("You have lost your target and will be given a new one shortly.");
            }
        }
    }

    public static void skip(Player player) {
        /**
         * TODO
         */
    }

    /**
     * Assign a new {@link TargetPair} of the two specified players.
     */
    public static void assign(Player player, Player target) {
        if (getPairFor(player).isEmpty() && getPairFor(target).isEmpty()) {

            //Create a new pair..
            final TargetPair pair = new TargetPair(player, target);

            //Add the pair to our list..
            TARGET_PAIRS.add(pair);

            //Send messages..
            player.getPacketSender().sendMessage("You've been assigned "+target.getUsername()+" as your target!");

            target.getPacketSender().sendMessage("You've been assigned "+player.getUsername()+" as your target!");

            //Send hints..
            player.getPacketSender().sendEntityHint(target);
            target.getPacketSender().sendEntityHint(player);

            //Reset attributes
            player.clearAttrib(AttributeKey.SPECIAL_ATTACK_USED);
            target.clearAttrib(AttributeKey.SPECIAL_ATTACK_USED);
        }
    }

    /**
     * Unassign an existing {@link TargetPair}.
     */
    public static void unassign(Player player) {
        final Optional<TargetPair> pair = getPairFor(player);
        if (pair.isPresent()) {

            TARGET_PAIRS.remove(pair.get());

            final Player p1 = pair.get().getPlayer1();
            final Player p2 = pair.get().getPlayer2();

            //Reset hints..
            p1.getPacketSender().sendEntityHintRemoval(true);
            p2.getPacketSender().sendEntityHintRemoval(true);

            //Reset name

            //Set timers
            p2.getTargetSearchTimer().start(TARGET_SEARCH_DELAY_SECONDS);
            p1.getTargetSearchTimer().start(TARGET_SEARCH_DELAY_SECONDS);
        }
    }

    /**
     * Gets the {@link Player} target for the specified player.
     */
    public static Optional<Player> getTargetfor(Player player) {
        Optional<TargetPair> pair = getPairFor(player);
        if (pair.isPresent()) {

            //Check if player 1 in the pair is us.
            //If so, return the other player.
            if (pair.get().getPlayer1().equals(player)) {
                return Optional.of(pair.get().getPlayer2());
            }

            //Check if player 2 in the pair is us.
            //If so, return the other player.
            if (pair.get().getPlayer2().equals(player)) {
                return Optional.of(pair.get().getPlayer1());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the {@link TargetPair} for the specfied player.
     */
    public static Optional<TargetPair> getPairFor(final Player p) {
        for (TargetPair pair : TARGET_PAIRS) {
            if (p.equals(pair.getPlayer1()) ||
                    p.equals(pair.getPlayer2())) {
                return Optional.of(pair);
            }
        }
        return Optional.empty();
    }

    /**
     * Handles death for a player.
     * Rewards the killer.
     */
    public static void onDeath(Player killer, Player killed) {
        //Remove first index if we've killed 1
        if (killer.getRecentKills().size() >= 2) {
            killer.getRecentKills().remove(0);
        }

        //Should the player be rewarded for this kill?
        boolean rewardPlayer = true;

        if(killer.getRecentKills().contains(killed.getHostAddress())) {
            rewardPlayer = false;
            //System.out.println("Let's not reward the player, already killed before.");
        } else if (killer.getHostAddress().equals(killed.getHostAddress())) {
            rewardPlayer = false;
            //System.out.println("Let's not reward the player, same IP.");
        } else if (!WildernessArea.inWilderness(killer.tile())) {
            rewardPlayer = false;
            //System.out.println("Let's not reward the player, not in wild.");
        } else {
            //System.out.println("Let's reward the player");
            //Clear out the old kills so the player can kill other players, since it is recent kills, not lifetime kills.
            killer.getRecentKills().clear();
            killer.getRecentKills().add(killed.getHostAddress());
        }

        Optional<Player> target = getTargetfor(killer);

        if(killer.getPlayerRights().isDeveloper(killer)) {
            rewardPlayer = true;
        }

        if (rewardPlayer) {
            TopPkers.SINGLETON.increase(killer.getUsername());

            //Other rewards
            if(WildernessArea.inWilderness(killer.tile())) { // Only reward if in wild
                PlayerKillingRewards.reward(killer, killed,true);
            }
        } else {
            killer.message("You don't get any rewards for that kill.");
            //Player is probably farming kills.
        }

        //Check if the player killed was our target..
        if (target.isPresent() && target.get().equals(killed)) {
            if (!rewardPlayer) {
                killed.message("Being killed by targets on the same address does not count.");
                killer.message("Killing targets on the same address does not count towards rewards.");
            }

            //Reset targets
            unassign(killer);

            //If player isn't farming kills..
            if (rewardPlayer) {
                //Send messages
                killed.getPacketSender().sendMessage("You were defeated by your target!");

                int targetKills = (Integer) killer.getAttribOr(AttributeKey.TARGET_KILLS, 0) + 1;
                killer.putAttrib(AttributeKey.TARGET_KILLS, targetKills);

                boolean trainedAccount = killer.getGameMode() == GameMode.TRAINED_ACCOUNT;
                if (trainedAccount) {
                    killer.message("(" + Color.RED.tag() + "+1</col>) Extra target point received! (Trained account bonus)");
                }
                var increaseBy = trainedAccount ? 2 : 1;
                var targetPoints = killer.<Integer>getAttribOr(AttributeKey.TARGET_POINTS, 0) + increaseBy;
                killer.putAttrib(AttributeKey.TARGET_POINTS, targetPoints);

                killer.message("You now have " + Color.BLUE.tag() + "" + targetPoints + "</col> target points. (" + Color.RED.tag() + "+" + increaseBy + "</col>)");
                killer.getPacketSender().sendString(QuestTab.InfoTab.TARGET_KILLS.childId, QuestTab.InfoTab.INFO_TAB.get(QuestTab.InfoTab.TARGET_KILLS.childId).fetchLineData(killer));

                Optional<BountyHunterEmblem> emblem = BountyHunterEmblem.getBest(killer, true);

                if (emblem.isPresent()) {
                    killer.inventory().remove(new Item(emblem.get().getItemId()));
                    killer.inventory().add(new Item(emblem.get().getNextOrLast().getItemId()));

                    boolean tierTen = emblem.get().getNextOrLast().getItemId() == ItemIdentifiers.ANTIQUE_EMBLEM_TIER_10;
                    if(tierTen) {
                        DailyTaskManager.increase(DailyTasks.TIER_UPGRADE, killer);
                    }
                } else {
                    killer.inventory().addOrBank(new Item(ANTIQUE_EMBLEM_TIER_1));
                }

                AchievementsManager.activate(killer, Achievements.BOUNTY_HUNTER_I, 1);
                AchievementsManager.activate(killer, Achievements.BOUNTY_HUNTER_II, 1);
                AchievementsManager.activate(killer, Achievements.BOUNTY_HUNTER_III, 1);
            }
        }

        killer.message(String.format(Death.randomKillMessage(), killed.getUsername()));
    }

    /**
     * Gets the amount of value for a player's emblems.
     */
    public static int exchange(Player player, boolean performSale) {
        ArrayList<BountyHunterEmblem> list = new ArrayList<>();
        for(BountyHunterEmblem emblem : BountyHunterEmblem.values()) {
            if(player.inventory().contains(emblem.getItemId())) {
                list.add(emblem);
            }
        }

        if(list.isEmpty()) {
            return 0;
        }

        int value = 0;
        int targetPoints = 0;

        for(BountyHunterEmblem emblem : list) {
            int amount = player.inventory().count(emblem.getItemId());
            if(amount > 0) {
                targetPoints += (emblem.getTargetPoints() * amount);
                value += (emblem.getBm() * amount);
                player.putAttrib(EMBLEM_WEALTH,formatNumber(value)+" BM and "+formatNumber(targetPoints)+" target points");

                if(performSale) {
                    if(!player.inventory().contains(emblem.getItemId())) {
                        return 0;
                    }
                    player.inventory().remove(emblem.getItemId(), amount);
                    var increaseTargetPointsBy = player.<Integer>getAttribOr(AttributeKey.TARGET_POINTS,0) + targetPoints;
                    player.putAttrib(AttributeKey.TARGET_POINTS, increaseTargetPointsBy);
                    player.inventory().add(new Item(BLOOD_MONEY, value));
                    player.clearAttrib(EMBLEM_WEALTH);
                }
            }
        }
        return value;
    }

    /***
     * Checks if the specified player is in a state of being able
     * to receive/be set a target.
     */
    private static boolean validTargetContester(Player p) {
        //A minimum of 30 combat is now required to receive targets in the minigame.
        if (p.getSkills().combatLevel() < 30) {
            return false;
        }

        return !(!p.isRegistered() || !(WildernessArea.inWilderness(p.tile())) || WildernessArea.wildernessLevel(p.tile()) <= 0 || p.isNullifyDamageLock() || p.dead() || p.isNeedsPlacement() || getPairFor(p).isPresent());
    }

    /**
     * The delay between each search for a new target.
     */
    private static final int TARGET_SEARCH_DELAY_SECONDS = GameServer.properties().production ? 30 : 5;

}
