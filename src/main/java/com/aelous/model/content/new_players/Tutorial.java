package com.aelous.model.content.new_players;

import com.aelous.GameServer;
import com.aelous.GameConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.*;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import static com.aelous.GameConstants.BANK_ITEMS;
import static com.aelous.GameConstants.TAB_AMOUNT;

public class Tutorial extends Dialogue {

    GameMode accountType = GameMode.TRAINED_ACCOUNT;

    public static void start(Player player) {
        player.lock();
        player.looks().hide(true);
        player.teleport(GameServer.properties().defaultTile);
        player.getDialogueManager().start(new Tutorial());
    }

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.HAPPY, "Welcome to " + GameConstants.SERVER_NAME + "!", "Let's start off by picking your game mode...");
        setPhase(1);
    }

    @Override
    protected void next() {
        if (getPhase() == 1) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Trained account</col>.", "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Dark lord account</col>.", "I want to go straight to action with a <col=" + Color.BLUE.getColorValue() + ">PK account</col>.", "What's the difference between the three?");
            setPhase(2);
        } else if (getPhase() == 3) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.HAPPY, "As a <col=" + Color.BLUE.getColorValue() + ">PK account</col>, you dive straight into PKing and high", "level bossing by having the ability to set your combat levels.", "But, <col=" + Color.MEDRED.getColorValue() + ">this mode has no access to the max cape</col>");
            setPhase(4);
        } else if (getPhase() == 4) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Trained account</col>.", "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Dark lord account</col>.", "I want to go straight to action with a <col=" + Color.BLUE.getColorValue() + ">PK account</col>.", "What's the difference between the three?");
            setPhase(2);
        } else if (getPhase() == 5) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Confirm", "Cancel");
            setPhase(6);
        } else if (isPhase(7)) {
            player.teleport(new Tile(3092, 3495));
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "To start off you should ::vote for starter money.", "Every first week of the month you get double points.", "You can sell the vote tickets in the trading post for around", "40-50K blood money. You also get a double drops lamp");
            setPhase(8);
        } else if (isPhase(8)) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "which has a 20% chance of doubling your drop", "for 60 minutes.");
            setPhase(9);
        } else if (isPhase(9)) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "After that there are two very effective ways to make money", "early on. Slayer and revenants both are " + Color.RED.wrap("(dangerous)") + ".", "Both money makers are in the wilderness.");
            setPhase(10);
        } else if (isPhase(10)) {
            player.teleport(new Tile(3099, 3503));
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "You can find the slayer master here.", "If you would like a full guide for slayer ::slayerguide.", "We offer various perks to make your game experience better.");
            setPhase(11);
        } else if (isPhase(11)) {
            player.teleport(new Tile(3246, 10169));
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "And the revenants can be found here deep in the wilderness.", "You can use the teleporting mage or a quick access", "command for both entrances. ::revs offers to teleport you", "to the level 17 or level 39 entrance.");
            setPhase(12);
        } else if (isPhase(12)) {
            player.teleport(GameServer.properties().defaultTile);
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "Enjoy your stay here at " + GameConstants.SERVER_NAME + "!");
            setPhase(13);
        } else if (isPhase(13)) {
            stop();
            player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
            player.unlock();
            player.looks().hide(false);
            /*if (player.mode() == GameMode.INSTANT_PKER) {
                player.getPresetManager().open();
                player.message("Pick a preset to load to get started.");
            }*/
            player.message("You can also spawn items with the spawn tab in the bottom right.");
        }
    }

    @Override
    protected void select(int option) {
        if (getPhase() == 2) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Trained account</col>.", "I want to go straight to action with a <col=" + Color.BLUE.getColorValue() + ">PK account</col>.", "What's the difference between the two?");
            if (option == 1) {
                accountType = GameMode.TRAINED_ACCOUNT;
                player.getGameMode(GameMode.TRAINED_ACCOUNT);
                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.DEFAULT, "Are you sure you wish to play as a Trained Account?");
                setPhase(5);
            } else if (option == 2) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Dark lord with 3 lives", "Dark lord unlimited lives");
                setPhase(8);
            /*} else if (option == 3) {
                accountType = GameMode.INSTANT_PKER;
                player.mode(GameMode.INSTANT_PKER);
                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.DEFAULT, "Are you sure you wish to play as a Instant Pker?");
                setPhase(5);*/
            } else if (option == 4) {
                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.HAPPY, "As a <col=" + Color.BLUE.getColorValue() + ">Trained or Darklord account</col>, you have to train your", "account and earn all of the levels. As a benefit, you get", " slightly higher rewards from certain activity and have the", "chance to <col=" + Color.MEDRED.getColorValue() + ">obtain the max cape</col>.");
                setPhase(3);
            }
        } else if (getPhase() == 6) {
            if (option == 1) {
                if (accountType == GameMode.DARK_LORD) {
                    player.getPacketSender().sendRights();
                    player.getUpdateFlag().flag(Flag.APPEARANCE);
                    player.putAttrib(AttributeKey.DARK_LORD_LIVES,3);
                } else if(accountType == GameMode.TRAINED_ACCOUNT) {
                    player.getGameMode(GameMode.TRAINED_ACCOUNT);
                }

                player.getBank().addAll(BANK_ITEMS);
                System.arraycopy(TAB_AMOUNT, 0, player.getBank().tabAmounts, 0, TAB_AMOUNT.length);
                player.getBank().shift();

                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.HAPPY, "Let me show you how to get started in " + GameConstants.SERVER_NAME + ".");
                setPhase(7);
            } else {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Trained account</col>.", "I'd like to take my time and earn benefits of the <col=" + Color.BLUE.getColorValue() + ">Dark lord account</col>.", "I want to go straight to action with a <col=" + Color.BLUE.getColorValue() + ">PK account</col>.", "What's the difference between the three?");
                setPhase(2);
            }
        } else if(isPhase(8)) {
            if(option == 1) {
                accountType = GameMode.DARK_LORD;
                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.DEFAULT, "Are you sure you wish to play as a Dark Lord (3 lives)?");
                setPhase(5);
            }
        }
    }
}
