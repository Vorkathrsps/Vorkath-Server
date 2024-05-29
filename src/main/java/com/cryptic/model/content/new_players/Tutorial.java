package com.cryptic.model.content.new_players;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.account.AccountSelection;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.GameMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Color;

import static com.cryptic.GameConstants.BANK_ITEMS;
import static com.cryptic.GameConstants.TAB_AMOUNT;

public class Tutorial extends Dialogue {

    GameMode accountType = GameMode.TRAINED_ACCOUNT;

    public static void start(Player player) {
        player.lock();
        player.teleport(GameServer.settings().getHomeTile());
        player.getDialogueManager().start(new Tutorial());
    }

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.HAPPY, "Welcome to " + GameServer.settings().getName() + "!", "Let's start off by picking your game mode...");
        setPhase(1);
    }

    @Override
    protected void next() {
        if (getPhase() == 1) {
            player.getMovementQueue().step(3096, 3504, MovementQueue.StepType.FORCED_WALK);
            player.waitForTile(new Tile(3096, 3504, 0), () -> {
                player.getInterfaceManager().open(88000);
                player.getnewteleInterface().drawInterface(88005);
              //  send(DialogueType. );
            });
            player.waitUntil(() -> AccountSelection.hasCompletedSelection, () -> {
                setPhase(2);
            });
        } else if (getPhase() == 5) {
            sendOption(DEFAULT_OPTION_TITLE, "Confirm", "Cancel");
            setPhase(6);
        } else if (isPhase(7)) {
            player.teleport(new Tile(3092, 3495));
            sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "To start off you should ::vote for starter money.", "Every first week of the month you get double points.", "You can sell the vote tickets in the trading post for around", "40-50K blood money. You also get a double drops lamp");
            setPhase(8);
        } else if (isPhase(8)) {
            sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "which has a 20% chance of doubling your drop", "for 60 minutes.");
            setPhase(9);
        } else if (isPhase(9)) {
            sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "After that there are two very effective ways to make money", "early on. Slayer and revenants both are " + Color.RED.wrap("(dangerous)") + ".", "Both money makers are in the wilderness.");
            setPhase(10);
        } else if (isPhase(10)) {
            player.teleport(new Tile(3099, 3503));
            sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "You can find the slayer master here.", "If you would like a full guide for slayer ::slayerguide.", "We offer various perks to make your game experience better.");
            setPhase(11);
        } else if (isPhase(11)) {
            player.teleport(new Tile(3246, 10169));
            sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "And the revenants can be found here deep in the wilderness.", "You can use the teleporting mage or a quick access", "command for both entrances. ::revs offers to teleport you", "to the level 17 or level 39 entrance.");
            setPhase(12);
        } else if (isPhase(12)) {
            player.teleport(GameServer.settings().getHomeTile());
            sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.CALM_TALK, "Enjoy your stay here at " + GameServer.settings().getName() + "!");
            setPhase(13);
        } else if (isPhase(13)) {
            stop();
            player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
            player.unlock();
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
            if (AccountSelection.hasCompletedSelection) {
                accountType = player.getGameMode();
                //player.getGameMode(GameMode.TRAINED_ACCOUNT);
                sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.DEFAULT, "Are you sure you wish to play as a " + player.getGameMode() + ".?");
                setPhase(5);
            }
        } else if (getPhase() == 6) {
            if (option == 1) {

                player.getBank().addAll(BANK_ITEMS);
                System.arraycopy(TAB_AMOUNT, 0, player.getBank().tabAmounts, 0, TAB_AMOUNT.length);
                player.getBank().shift();

                sendNpcChat(NpcIdentifiers.COMBAT_INSTRUCTOR, Expression.HAPPY, "Let me show you how to get started in " + GameServer.settings().getName() + ".");
                setPhase(7);
            } else {
                AccountSelection.open(player);
                if (AccountSelection.hasCompletedSelection) {
                    setPhase(2);
                }
            }
        } else if (isPhase(8)) {

        }
    }
}
