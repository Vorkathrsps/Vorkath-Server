package com.cryptic.model.content.account;

import com.cryptic.GameConstants;
import com.cryptic.model.content.title.TitleColour;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.GameMode;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.rights.MemberRights;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.GameConstants.BANK_ITEMS;
import static com.cryptic.GameConstants.TAB_AMOUNT;
import static com.cryptic.model.entity.attributes.AttributeKey.STARTER_GIVEN;
import static com.cryptic.utility.ItemIdentifiers.*;

public class AccountSelection extends PacketInteraction {

    public static boolean hasCompletedSelection = false;
    public static boolean isDonatorPromo = true;
    public static void open(Player player) {
        player.lock();
        player.getInterfaceManager().open(42400);
        player.putAttrib(AttributeKey.GAME_MODE_SELECTED, 42812);
        player.getPacketSender().sendString(42802, "Regular");
        player.getPacketSender().sendString(42803, "No Ironman restrictions will be applied to this account.");
        player.getPacketSender().sendString(42804, "Ironman");
        player.getPacketSender().sendString(42805, "Restrictions on trading, dueling, and trading post.<br>"+"5% Drop rate boost.");
        player.getPacketSender().sendString(42806, "Hardcore Ironman");
        player.getPacketSender().sendString(42807, "Restrictions on trading, dueling, and trading post.<br>"+"6.5% Drop rate boost.");
        player.getPacketSender().sendString(42808, "Realism");
        player.getPacketSender().sendString(42809, "No Ironman restrictions. 10% Drop rate boost.");
        player.getPacketSender().sendString(42810, "Hardcore Realism");
        player.getPacketSender().sendString(42811, "No ironman restrictions, status is lost on death.<br>" + "11.5% Drop rate boost.");
        player.getPacketSender().sendString(42418, "Please set your pin.");
        player.getPacketSender().sendString(42410, "");
        player.getPacketSender().sendString(42411, "");
        player.getPacketSender().sendString(42415, "");
        player.getPacketSender().sendString(42416, "");
        refreshOptions(player);
    }

    private static void refreshOptions(Player player) {
        switch (player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42812)) {
            case 42812 -> {
                player.getPacketSender().sendChangeSprite(42812, (byte) 2);
                player.getPacketSender().sendChangeSprite(42813, (byte) 0);
                player.getPacketSender().sendChangeSprite(42814, (byte) 0);
                player.getPacketSender().sendChangeSprite(42815, (byte) 0);
                player.getPacketSender().sendChangeSprite(42816, (byte) 0);
                player.setIronmanStatus(IronMode.NONE);
                player.setGameMode(GameMode.TRAINED_ACCOUNT);
            }
            case 42813 -> {
                player.getPacketSender().sendChangeSprite(42812, (byte) 0);
                player.getPacketSender().sendChangeSprite(42813, (byte) 2);
                player.getPacketSender().sendChangeSprite(42814, (byte) 0);
                player.getPacketSender().sendChangeSprite(42815, (byte) 0);
                player.getPacketSender().sendChangeSprite(42816, (byte) 0);
                player.setIronmanStatus(IronMode.REGULAR);
                player.setGameMode(GameMode.TRAINED_ACCOUNT);
            }
            case 42814 -> {
                player.getPacketSender().sendChangeSprite(42812, (byte) 0);
                player.getPacketSender().sendChangeSprite(42813, (byte) 0);
                player.getPacketSender().sendChangeSprite(42814, (byte) 2);
                player.getPacketSender().sendChangeSprite(42815, (byte) 0);
                player.getPacketSender().sendChangeSprite(42816, (byte) 0);
                player.setIronmanStatus(IronMode.HARDCORE);
                player.setGameMode(GameMode.TRAINED_ACCOUNT);
            }
            case 42815 -> {
                player.getPacketSender().sendChangeSprite(42812, (byte) 0);
                player.getPacketSender().sendChangeSprite(42813, (byte) 0);
                player.getPacketSender().sendChangeSprite(42814, (byte) 0);
                player.getPacketSender().sendChangeSprite(42815, (byte) 2);
                player.getPacketSender().sendChangeSprite(42816, (byte) 0);
                player.setIronmanStatus(IronMode.NONE);
                player.setGameMode(GameMode.REALISM);
            }
            case 42816 -> {
                player.getPacketSender().sendChangeSprite(42812, (byte) 0);
                player.getPacketSender().sendChangeSprite(42813, (byte) 0);
                player.getPacketSender().sendChangeSprite(42814, (byte) 0);
                player.getPacketSender().sendChangeSprite(42815, (byte) 0);
                player.getPacketSender().sendChangeSprite(42816, (byte) 2);
                player.setIronmanStatus(IronMode.NONE);
                player.setGameMode(GameMode.HARDCORE_REALISM);
            }
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        for (AccountType type : AccountType.values()) {
            if (type.getButtonId() == button) {
                if (button == 42812) {
                    player.putAttrib(AttributeKey.GAME_MODE_SELECTED, 42812);
                } else if (button == 42813) {
                    player.putAttrib(AttributeKey.GAME_MODE_SELECTED, 42813);
                } else if (button == 42814) {
                    player.putAttrib(AttributeKey.GAME_MODE_SELECTED, 42814);
                } else if (button == 42815) {
                    player.putAttrib(AttributeKey.GAME_MODE_SELECTED, 42815);
                } else if (button == 42816) {
                    player.putAttrib(AttributeKey.GAME_MODE_SELECTED, 42816);
                }
                refreshOptions(player);
                return true;
            }
        }
        if (button == 42419) {
            confirm(player);
            return true;
        }
        return false;
    }

    private void starter_package(Player player, int type) {
        switch (type) {
            case 0 -> {
                player.resetSkills();
                player.getInventory().addAll(GameConstants.STARTER_ITEMS_NEW);
                if (isDonatorPromo) {
                    player.setMemberRights(MemberRights.RUBY_MEMBER);
                }
            }
            case 1 -> {
                player.resetSkills();
                player.getInventory().addAll(GameConstants.STARTER_ITEMS_NEW);
                player.getInventory().add(new Item(IRONMAN_HELM, 1), true);
                player.getInventory().add(new Item(IRONMAN_PLATEBODY, 1), true);
                player.getInventory().add(new Item(IRONMAN_PLATELEGS, 1), true);
                player.setIronmanStatus(IronMode.REGULAR);
                player.message("You have been given some training equipment.");
                if (isDonatorPromo) {
                    player.setMemberRights(MemberRights.RUBY_MEMBER);
                }
            }
            case 2 -> {
                player.resetSkills();
                player.getInventory().addAll(GameConstants.STARTER_ITEMS_NEW);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_HELM, 1), true);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_PLATEBODY, 1), true);
                player.getInventory().add(new Item(HARDCORE_IRONMAN_PLATELEGS, 1), true);
                player.setIronmanStatus(IronMode.HARDCORE);
                player.message("You have been given some training equipment.");
                if (isDonatorPromo) {
                    player.setMemberRights(MemberRights.RUBY_MEMBER);
                }
            }
            case 3, 4 -> {
                player.getInventory().addAll(GameConstants.STARTER_ITEMS_NEW);
                player.message("You have been given some training equipment.");
                if (isDonatorPromo) {
                    player.setMemberRights(MemberRights.RUBY_MEMBER);
                }
            }
        }

        player.setSpellbook(MagicSpellbook.NORMAL);
        player.clearAttrib(AttributeKey.TUTORIAL);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public boolean confirm(Player player) {
        if (player.getTimers().has(TimerKey.CLICK_DELAY)) {
            return false;
        }

        if (player.getAttribOr(STARTER_GIVEN, false)) {
            return true;
        }

        player.putAttrib(STARTER_GIVEN, true);

        int validButtons = 42812;
        for (AccountType type : AccountType.values()) {
            validButtons = type.getButtonId();
        }

        if (validButtons < 42812 && validButtons > 42816) {
            return false;
        }

        switch (player.<Integer>getAttribOr(AttributeKey.GAME_MODE_SELECTED, 42812)) {
            case 42812 -> {
                starter_package(player, 0);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
                player.getInterfaceManager().close();
                player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
                player.getPacketSender().sendRights();
                player.looks().hide(false);
                return true;
            }
            case 42813 -> {
                starter_package(player, 1);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
                player.getInterfaceManager().close();
                player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
                player.getPacketSender().sendRights();
                player.looks().hide(false);
                return true;
            }
            case 42814 -> {
                starter_package(player, 2);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
                player.getInterfaceManager().close();
                player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
                player.getPacketSender().sendRights();
                player.looks().hide(false);
                return true;
            }
            case 42815 -> {
                starter_package(player, 3);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
                player.getInterfaceManager().close();
                player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
                player.putAttrib(AttributeKey.TITLE, "Realism");
                player.putAttrib(AttributeKey.TITLE_COLOR, new TitleColour("Orange", "<col=ff7000>").getFormat());
                player.getPacketSender().sendRights();
                player.looks().hide(false);
                return true;
            }
            case 42816 -> {
                starter_package(player, 4);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
                player.getInterfaceManager().close();
                player.putAttrib(AttributeKey.NEW_ACCOUNT, false);
                player.getPacketSender().sendRights();
                player.looks().hide(false);
                return true;
            }
        }
        return false;
    }

    public enum AccountType {

        REGULAR(42812),
        IRONMAN(42813),
        HARDCORE_IRONMAN(42814),
        REALISM(42815),
        HARDCORE_REALISM(42816);

        private final int button;

        /**
         * We don't have to set a constructor because the Enum only consists of Types
         */
        AccountType(int button) {
            this.button = button;
        }

        /**
         * The buttonId
         *
         * @return The button we receive from the client.
         */
        public int getButtonId() {
            return button;
        }
    }
}
