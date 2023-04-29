package com.aelous.model.content.account;

import com.aelous.GameConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.*;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.GameConstants.BANK_ITEMS;
import static com.aelous.GameConstants.TAB_AMOUNT;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * The class which represents functionality for selecting your account type.
 *
 * @author Patrick van Elderen | 24 sep. 2021 : 19:56:14
 * @see <a href="https://github.com/PVE95/">Github profile</a>
 */
public class AccountSelection extends PacketInteraction {

    public static void open(Player player) {
        player.getInterfaceManager().open(42400);
        refreshOptions(player);
    }

    private static void refreshOptions(Player player) {
        switch (player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42405)) {
            case 42402 -> {
                player.getPacketSender().sendChangeSprite(42402, (byte) 2);
                player.getPacketSender().sendChangeSprite(42403, (byte) 0);
                player.getPacketSender().sendChangeSprite(42423, (byte) 0);
                player.getPacketSender().sendChangeSprite(42405, (byte) 0);
                player.getPacketSender().sendChangeSprite(42406, (byte) 0);
                player.setIronmanStatus(IronMode.REGULAR);
                player.getGameMode(GameMode.TRAINED_ACCOUNT);
            }
            case 42403 -> {
                    player.getPacketSender().sendChangeSprite(42402, (byte) 0);
                    player.getPacketSender().sendChangeSprite(42403, (byte) 2);
                    player.getPacketSender().sendChangeSprite(42423, (byte) 0);
                    player.getPacketSender().sendChangeSprite(42405, (byte) 0);
                    player.getPacketSender().sendChangeSprite(42406, (byte) 0);
                    player.setIronmanStatus(IronMode.HARDCORE);
                    player.getGameMode(GameMode.TRAINED_ACCOUNT);
            }
            case 42423 -> {
                player.getPacketSender().sendChangeSprite(42402, (byte) 0);
                player.getPacketSender().sendChangeSprite(42403, (byte) 0);
                player.getPacketSender().sendChangeSprite(42423, (byte) 2);
                player.getPacketSender().sendChangeSprite(42405, (byte) 0);
                player.getPacketSender().sendChangeSprite(42406, (byte) 0);
                player.setIronmanStatus(IronMode.NONE);
                player.getGameMode(GameMode.TRAINED_ACCOUNT);
            }
            }
        }

    private static final boolean DARK_LORD_MODE_ENABLED = false;

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if(button == 42406 && !DARK_LORD_MODE_ENABLED) {
            player.message(Color.RED.wrap("Disabled."));
            return true;
        }
        for (AccountType type : AccountType.values()) {
            if (type.getButtonId() == button) {
                if(player.getTimers().has(TimerKey.CLICK_DELAY)) {
                    return true;
                }

                if (player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED,42405) == button) {
                    player.message("<col=ff0000>Disabled.</col>");
                } else {
                    if (button == 42402) {
                        player.message(Color.RED.wrap("Your levels will be reset if you choose this game mode!"));
                        player.putAttrib(AttributeKey.GAME_MODE_SELECTED,42402);
                    } else if (button == 42403) {
                        player.message(Color.RED.wrap("Your levels will be reset if you choose this game mode!"));
                        player.putAttrib(AttributeKey.GAME_MODE_SELECTED,42403);
                    } else if (button == 42423) {
                        player.putAttrib(AttributeKey.GAME_MODE_SELECTED,42423);
                    } else if (button == 42405) {
                        player.putAttrib(AttributeKey.GAME_MODE_SELECTED,42405);
                    } else if (button == 42406) {
                        player.putAttrib(AttributeKey.GAME_MODE_SELECTED,42406);
                    }
                    player.getTimers().register(TimerKey.CLICK_DELAY,2);
                    refreshOptions(player);
                }
                return true;
            }
        }
        if(button == 42419) {
            if(player.getTimers().has(TimerKey.CLICK_DELAY)) {
                return true;
            }
            confirm(player);
            player.getTimers().register(TimerKey.CLICK_DELAY,2);
            return true;
        }
        return false;
    }

    private void starter_package(Player player, int type) {
        switch (type) {
            case 0 -> {
                player.resetSkills();
                player.getInventory().add(new Item(IRONMAN_HELM, 1), true);
                player.getInventory().add(new Item(IRONMAN_PLATEBODY, 1), true);
                player.getInventory().add(new Item(IRONMAN_PLATELEGS, 1), true);
                player.getInventory().addAll(GameConstants.STARTER_ITEMS);
                player.setIronmanStatus(IronMode.REGULAR);
                player.message("You have been given some training equipment.");
            }
            case 1 -> {
                player.resetSkills();
                player.getInventory().add(new Item(HARDCORE_IRONMAN_HELM, 1), true);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_PLATEBODY, 1), true);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_PLATELEGS, 1), true);
                player.getInventory().addAll(GameConstants.STARTER_ITEMS);
                player.setIronmanStatus(IronMode.HARDCORE);
                player.message("You have been given some training equipment.");
            }
            case 2 -> {
                player.getInventory().addAll(GameConstants.STARTER_ITEMS);
                player.message("You have been given some training equipment.");
            }
            case 3 -> {
                //Max out combat
                for (int skill = 0; skill < 7; skill++) {
                    player.getSkills().setXp(skill, Skills.levelToXp(99));
                    player.getSkills().update();
                    player.getSkills().recalculateCombat();
                }
            }
            case 4 -> {
                player.getInventory().add(new Item(HARDCORE_IRONMAN_HELM, 1), true);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_PLATEBODY, 1), true);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_PLATELEGS, 1), true);
                player.getInventory().addAll(GameConstants.STARTER_ITEMS);
                player.message("You have been given some training equipment.");
            }
        }

        //Set default spellbook
        player.setSpellbook(MagicSpellbook.NORMAL);
        //Remove tutorial flag.
        player.clearAttrib(AttributeKey.TUTORIAL);
        player.getUpdateFlag().flag(Flag.APPEARANCE);

        //Setup bank
        if(!player.getIronManStatus().isIronman() && !player.getIronManStatus().isHardcoreIronman()) {
            player.getBank().addAll(BANK_ITEMS);
            System.arraycopy(TAB_AMOUNT, 0, player.getBank().tabAmounts, 0, TAB_AMOUNT.length);
            player.getBank().shift();
        }
    }

    public boolean confirm(Player player) {
        if (player.getTimers().has(TimerKey.CLICK_DELAY)) {
            return false;
        }

        boolean validButtons = player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42405) >= 42402 && player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42405) <= 42406 || player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42405) == 42423;

        if (!validButtons) {
            player.message("You have yet to select an game mode.");
            return false;
        }

        switch (player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42405)) {
            case 42402 -> {
                starter_package(player, 0);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
            }
            case 42403 -> {

                starter_package(player, 1);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
            }
            case 42423 -> {

                starter_package(player, 2);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
            }
        }

        player.getInterfaceManager().close();
        player.putAttrib(AttributeKey.NEW_ACCOUNT,false);

        player.unlock();
        player.looks().hide(false);
        return true;
    }
}
