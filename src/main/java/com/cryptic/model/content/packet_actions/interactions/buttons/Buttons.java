package com.cryptic.model.content.packet_actions.interactions.buttons;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.content.DropsDisplay;
import com.cryptic.model.content.achievements.AchievementButtons;
import com.cryptic.model.content.achievements.AchievementWidget;
import com.cryptic.model.content.bank_pin.BankPin;
import com.cryptic.model.content.collection_logs.CollectionLogButtons;
import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.model.content.emote.Emotes;
import com.cryptic.model.content.items_kept_on_death.ItemsKeptOnDeath;
import com.cryptic.model.content.skill.impl.smithing.Smelting;
import com.cryptic.model.content.teleport.OrnateJewelleryBox;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.content.tournaments.TournamentManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.magic.autocasting.Autocasting;
import com.cryptic.model.entity.combat.magic.spells.MagicClickSpells;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.weapon.WeaponInterfaces;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.QuestTab;
import com.cryptic.model.inter.clan.ClanButtons;
import com.cryptic.model.inter.dialogue.ItemActionDialogue;
import com.cryptic.model.inter.impl.BonusesInterface;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.impl.ButtonClickPacketListener;

import java.util.Arrays;

import static com.cryptic.model.content.areas.lumbridge.dialogue.Hans.getTimeDHS;
import static com.cryptic.model.content.collection_logs.LogType.BOSSES;
import static com.cryptic.model.entity.combat.magic.autocasting.Autocasting.ANCIENT_SPELL_AUTOCAST_STAFFS;
import static com.cryptic.model.items.container.shop.ShopUtility.*;
import static com.cryptic.network.packet.incoming.impl.ButtonClickPacketListener.LOGOUT;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * Handles button actions.
 */
public class Buttons {

    private static final int OPEN_COLLECTION_LOG = 78901;
    public static final int OPEN_NPC_DROPS = 78903;
    //combat tasks button
    private static final int OPEN_COMBAT_TASKS = 78902;

    public static final int ADVANCED_OPTIONS_BUTTON = 42524;

    //Auto retal buttons
    private static final int TOGGLE_AUTO_RETALIATE = 22845;
    private static final int TOGGLE_AUTO_RETALIATE_2 = 24115;
    private static final int TOGGLE_AUTO_RETALIATE_3 = 24048;
    private static final int TOGGLE_AUTO_RETALIATE_4 = 24509;

    private static final int TOGGLE_RUN_ENERGY_ORB = 1050;
    public static final int RUN_BUTTON = 42507;
    private static final int OPEN_PRICE_CHECKER = 27651;
    private static final int OPEN_ITEMS_KEPT_ON_DEATH_SCREEN = 27654;
    private static final int DESTROY_ITEM = 14175;

    private static final int MYSTERY_BOX_OPEN = 71003;
    private static final int CANCEL_DESTROY_ITEM = 14176;
    private static final int PRICE_CHECKER_WITHDRAW_ALL = 18255;
    private static final int PRICE_CHECKER_DEPOSIT_ALL = 18252;
    private static final int TOGGLE_EXP_LOCK = 476;

    // Trade buttons
    private static final int TRADE_ACCEPT_BUTTON_1 = 52100;
    private static final int TRADE_ACCEPT_BUTTON_2 = 52319;
    private static final int TRADE_DECLINE_BUTTON_1 = 52101;
    private static final int TRADE_DECLINE_BUTTON_2 = 52320;
    private static final int TRADE_SCREEN_CLOSE_BUTTON = 52102;

    // Autocast buttons
    private static final int AUTOCAST_BUTTON_1 = 349;
    private static final int AUTOCAST_BUTTON_2 = 24111;

    // Duel buttons
    private static final int DUEL_ACCEPT_BUTTON_1 = 6674;
    private static final int DUEL_ACCEPT_BUTTON_2 = 6520;

    // Close buttons
    private static final int CLOSE_BUTTON_1 = 18247;
    private static final int CLOSE_BUTTON_2 = 38117;
    private static final int CLOSE_BUTTON_3 = 54002;
    private static final int CLOSE_BUTTON_4 = 54112;
    private static final int CLOSE_BUTTON_5 = 28056;
    private static final int CLOSE_BUTTON_6 = 29175;
    private static final int CLOSE_LOOTING_BAG_OPEN = 26702;
    private static final int CLOSE_LOOTING_BAG_ADD = 26802;
    private static final int CLOSE_LOOTING_BAG_BANK = 26902;

    // Settings tab
    private static final int OPEN_SETTINGS = 42511;
    private static final int CLOSE_SETTINGS = 23020;
    private static final int OPEN_KEYBINDINGS = 42552;

    private static final int[] CLOSE_BUTTONS = {CLOSE_BUTTON_1, CLOSE_BUTTON_2, CLOSE_BUTTON_3, CLOSE_BUTTON_4, CLOSE_BUTTON_5, CLOSE_BUTTON_6};
    private static int WEAPON_SHOP_BUTTON;
    private static int ARMOR_SHOP_BUTTON;
    private static int RANGED_SHOP;
    private static int SHOP_CLOSE_BUTTON;
    private static int POTION_SHOP;
    private static int MAGIC_SHOP_BUTTON_ID;

    /**
     * Handles the button click for a player.
     *
     * @param player The player clicking the button
     * @param button The id of the button being clicked.
     */
    public static void handleButton(Player player, int button) {

        Item staff = player.getEquipment().get(EquipSlot.WEAPON);
        boolean full_ahrim_effect = CombatFactory.fullAhrims(player) && Equipment.hasAmmyOfDamned(player);
        boolean onAncients = player.getSpellbook() == MagicSpellbook.ANCIENTS;
        boolean onNormals = player.getSpellbook() == MagicSpellbook.NORMAL;

        if (player.getMysteryBox().onButton(button)) {
            return;
        }

        switch (button) {
            case 15151 -> {
                if (WildernessArea.isInWilderness(player)) {
                    return;
                }
                player.getPacketSender().sendInterfaceDisplayState(21172, true);
                player.getBank().open();
            }
            case 19210, 21741, 40301 -> {
                if (player.getDueling().inDuel()) {
                    return;
                }
                Tile tile = GameServer.properties().defaultTile.tile();
                if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                    Teleports.basicTeleport(player, tile);
                    player.message("You have been teleported to home.");
                }
            }
            case TRADE_ACCEPT_BUTTON_1, TRADE_ACCEPT_BUTTON_2 -> player.getTrading().acceptTrade();
            case TRADE_DECLINE_BUTTON_1, TRADE_DECLINE_BUTTON_2, TRADE_SCREEN_CLOSE_BUTTON ->
                player.getTrading().abortTrading();
            case 21299 -> player.getInterfaceManager().close();
            case CLOSE_LOOTING_BAG_OPEN, CLOSE_LOOTING_BAG_ADD, CLOSE_LOOTING_BAG_BANK ->
                player.getLootingBag().close();
            case 14921 -> player.getBankPinSettings().dontKnowPin();
            case 80001 -> {
                //QuestTab.updatePlayerPanel(player);
            }

            case OPEN_COMBAT_TASKS -> AchievementWidget.openEasyJournal(player);

            case OPEN_COLLECTION_LOG -> player.getCollectionLog().open(BOSSES);

            case OPEN_NPC_DROPS -> DropsDisplay.start(player);

            case 78904 -> {
                boolean showplaytime = player.getAttribOr(AttributeKey.SHOWPLAYTIME, false);

                if (!showplaytime) {
                    player.getPacketSender().sendString(32578, "@lre@Time Played:@gre@" + getTimeDHS(player));
                    player.putAttrib(AttributeKey.SHOWPLAYTIME, true);
                } else {
                    player.getPacketSender().sendString(32578, "Time played: Click to reveal.");
                    player.putAttrib(AttributeKey.SHOWPLAYTIME, false);
                }
            }

            case 14922, CANCEL_DESTROY_ITEM, 35002 -> player.getInterfaceManager().close();
            case TOGGLE_RUN_ENERGY_ORB, RUN_BUTTON -> {
                if (player.looks().trans() > -1) {
                    player.message("You can't run as an npc.");
                    return;
                }
                double energy = player.getAttribOr(AttributeKey.RUN_ENERGY, 0);
                if (energy > 0 && !player.busy()) {
                    boolean running = player.getAttribOr(AttributeKey.IS_RUNNING, false);
                    player.putAttrib(AttributeKey.IS_RUNNING, !running);
                    player.getPacketSender().sendRunStatus();
                } else {
                    player.message("You cannot do that right now.");
                }
            }
            case OPEN_SETTINGS -> { // 42500
                if (!player.busy()) {
                    player.getInterfaceManager().setSidebar(11, 23000);
                } else {
                    player.message("You cannot do that right now.");
                }
            }
            case OPEN_KEYBINDINGS -> {
                if (!player.busy()) {
                    player.getInterfaceManager().open(53000);
                } else {
                    player.message("You cannot do that right now.");
                }
            }
            case OPEN_PRICE_CHECKER -> {
                if (!player.busy()) {
                    player.getPriceChecker().open();
                } else {
                    player.message("You cannot do that right now.");
                }
            }
            case PRICE_CHECKER_WITHDRAW_ALL -> player.getPriceChecker().withdrawAll();
            case PRICE_CHECKER_DEPOSIT_ALL -> player.getPriceChecker().depositAll();
            case OPEN_ITEMS_KEPT_ON_DEATH_SCREEN -> {
                if (!player.busy()) {
                    ItemsKeptOnDeath.open(player);
                } else {
                    player.message("You cannot do that right now.");
                }
            }
            case AUTOCAST_BUTTON_1 -> {
                player.putAttrib(AttributeKey.DEFENSIVE_AUTOCAST, false);
                if (!GameServer.properties().rightClickAutocast) {
                    if (player.getSpellbook() == MagicSpellbook.LUNAR) {
                        player.message("You can't autocast lunar magic.");
                        return;
                    }

                    if (staff != null && ANCIENT_SPELL_AUTOCAST_STAFFS.contains(staff.getId()) && !full_ahrim_effect) {
                        if (player.getSpellbook() == MagicSpellbook.ANCIENTS) {
                            //It can autocast offensive standard spells, but cannot autocast Ancient Magicks unlike its other variants.
                            if (player.getEquipment().getWeapon().getId() != HARMONISED_NIGHTMARE_STAFF) {
                                player.getInterfaceManager().setSidebar(0, 1689);
                            } else {
                                player.message("You can only autocast regular offensive spells with this staff.");
                                return;
                            }
                        } else {
                            if (player.getEquipment().getWeapon().getId() != ANCIENT_STAFF) {
                                player.getInterfaceManager().setSidebar(0, 1829);
                            } else {
                                player.message("You can only autocast ancient magicks with that.");
                                return;
                            }
                        }
                    } else {
                        if (player.getSpellbook() == MagicSpellbook.NORMAL) {
                            player.getInterfaceManager().setSidebar(0, 1829);
                        } else {
                            player.message("You can only autocast normal magic with that.");
                            return;
                        }
                    }
                } else {
                    player.getPacketSender().sendMessage("A spell can be autocast by simply right-clicking on it in your Magic spellbook and ").sendMessage("selecting the \"Autocast\" option.");
                }
            }
            case AUTOCAST_BUTTON_2 -> {
                player.putAttrib(AttributeKey.DEFENSIVE_AUTOCAST, true);
                if (!GameServer.properties().rightClickAutocast) {
                    if (player.getSpellbook() == MagicSpellbook.LUNAR) {
                        player.message("You can't autocast lunar spells.");
                        player.getPacketSender().setDefensiveAutocastState(0);
                        return;
                    }
                    if (player.getEquipment().get(3) != null && player.getEquipment().containsAny(ANCIENT_STAFF, MASTER_WAND, STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, TOXIC_STAFF_OF_THE_DEAD, KODAI_WAND)) {
                        player.getInterfaceManager().setSidebar(0, 1689);
                    } else {
                        if (player.getSpellbook() != MagicSpellbook.NORMAL) {
                            player.message("You can't autocast ancient magicks with this staff.");
                            player.getPacketSender().setDefensiveAutocastState(0);
                            return;
                        }
                        player.getInterfaceManager().setSidebar(0, 1829);
                    }
                } else {
                    player.getPacketSender()
                        .sendMessage(
                            "A spell can be autocast by simply right-clicking on it in your Magic spellbook and ")
                        .sendMessage("selecting the \"Autocast\" option.");
                }
            }
            case DUEL_ACCEPT_BUTTON_1, DUEL_ACCEPT_BUTTON_2 -> player.getDueling().acceptDuel();
            case ADVANCED_OPTIONS_BUTTON -> player.getInterfaceManager().open(43000);
            case TOGGLE_AUTO_RETALIATE, TOGGLE_AUTO_RETALIATE_2, TOGGLE_AUTO_RETALIATE_3, TOGGLE_AUTO_RETALIATE_4 ->
                player.getCombat().setAutoRetaliate(!player.getCombat().hasAutoReliateToggled());
            case DESTROY_ITEM -> {
                int id = player.getDestroyItem();
                Item itemToDestroy = new Item(id);
                if (!player.inventory().contains(itemToDestroy)) {
                    return;
                }
                player.inventory().remove(itemToDestroy, true);
                player.getInterfaceManager().close();
                return;
            }
            case TOGGLE_EXP_LOCK -> {
                boolean locked = player.getAttribOr(AttributeKey.XP_LOCKED, false);
                if (locked) {
                    player.putAttrib(AttributeKey.XP_LOCKED, false);
                    player.message("Your experience is now unlocked.");
                } else {
                    player.putAttrib(AttributeKey.XP_LOCKED, true);
                    player.message("Your experience is now locked.");
                }
            }
            default -> {
                if (Arrays.stream(CLOSE_BUTTONS).anyMatch(b -> b == button)) {
                    player.getInterfaceManager().close();
                    return;
                }
                if (button == LOGOUT) {
                    // Handle this here and not in canLogout() so that x-logging doesn't "break" the
                    // attack timer.
                    if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_FORFEIT.ordinal()]) {
                        player.message("You cannot log out at the moment.");
                        return;
                    }
                    player.putAttrib(AttributeKey.LOGOUT_CLICKED, true);
                    return;
                }
                if (button == 54763) {
                    player.getTaskMasterManager().claimReward();
                    return;
                }
                if (player.getQuickPrayers().handleButton(button)) {
                    return;
                }
                if (TradingPost.handleButtons(player, button))
                    return;
                if (player.getSlayerRewards().handleButtonInteraction(player, button)) {
                    return;
                }
                if (player.getRunePouch().onButton(button)) {
                    return;
                }
                if (player.getSkills().pressedSkill(button)) {
                    return;
                }
                if (QuestTab.onButton(player, button)) {
                    return;
                }
                if (Smelting.handleButton(player, button)) {
                    return;
                }
                if (BonusesInterface.bonusesButtons(player, button)) {
                    return;
                }
                if (player.getTheatreInterface() != null) {
                    if (player.getTheatreInterface().close(player, button)) {
                        return;
                    }
                    if (player.tile().region() == 14642) {
                        if (player.getTheatreInterface().create(player, button)) {
                            return;
                        }
                    }
                    if (player.getTheatreInterface().abandon(player, button)) {
                        return;
                    }
                    if (player.getTheatreParty() != null && player.getTheatreInterface() != null) {
                        if (player.getTheatreInterface().kick(player, button)) {
                            return;
                        }
                    }
                }
                if (CollectionLogButtons.onButtonClick(player, button)) {
                    return;
                }
                WEAPON_SHOP_BUTTON = 73156;
                if (button == WEAPON_SHOP_BUTTON) {
                    World.getWorld().shop(5004).open(player);
                    return;
                }
                ARMOR_SHOP_BUTTON = 73157;
                if (button == ARMOR_SHOP_BUTTON) {
                    World.getWorld().shop(5003).open(player);
                    return;
                }
                MAGIC_SHOP_BUTTON_ID = 73159;
                if (button == MAGIC_SHOP_BUTTON_ID) {
                    World.getWorld().shop(AUBURYS_MAGIC_SHOP_ID).open(player);
                    return;
                }
                RANGED_SHOP = 73158;
                if (button == RANGED_SHOP) {
                    World.getWorld().shop(LOWES_ARCHERY_SHOP_ID).open(player);
                    return;
                }
                POTION_SHOP = 73160;
                if (button == POTION_SHOP) {
                    World.getWorld().shop(KAQEMEEX_POTIONS_SHOP_ID).open(player);
                    return;
                }
                SHOP_CLOSE_BUTTON = 73153;
                if (button == SHOP_CLOSE_BUTTON) {
                    Shop.closeShop(player);
                    return;
                }
                if (OrnateJewelleryBox.teleport(player, button)) {
                    return;
                }
                if (DropsDisplay.clickActions(player, button)) {
                    return;
                }
                if (AchievementButtons.handleButtons(player, button)) {
                    return;
                }
                if (player.getBank().buttonAction(button)) {
                    return;
                }
                if (player.chatBoxItemDialogue != null) {
                    if (player.chatBoxItemDialogue.clickButton(button)) {
                        player.chatBoxItemDialogue = null;
                        return;
                    }
                }
                if (ItemActionDialogue.clickButton(player, button)) {
                    return;
                }
                BankPin bankPin = player.getBankPin();
                if (bankPin.isEnteringPin() && bankPin.getPinInterface().enterDigit(button)) {
                    return;
                }
                if (Prayers.togglePrayer(player, button)) {
                    return;
                }
                if (Autocasting.handleLegacyAutocast(player, button)) {
                    return;
                }
                if (Autocasting.toggleAutocast(player, button)) {
                    return;
                }
                if (WeaponInterfaces.changeCombatSettings(player, button)) {
                    return;
                }
                if (MagicClickSpells.handleSpell(player, button)) {
                    return;
                }
                if (player.getPriceChecker().buttonActions(button)) {
                    return;
                }
                if (Emotes.doEmote(player, button)) {
                    return;
                }
                if (ClanButtons.handle(player, button)) {
                    return;
                }
                if (player.getDueling().checkRule(button)) {
                    return;
                }
                if (TournamentManager.handleWidgetButton(player, button)) {
                    return;
                }
                if (player.getPresetManager().handleButton(button, 0)) {
                    return;
                }
                if (Arrays.stream(CLOSE_BUTTONS).anyMatch(b -> b == button)) {
                    return;
                }
                if (Arrays.stream(ButtonClickPacketListener.ALL).anyMatch(b -> b == button)) {
                    return;
                }
            }
        }
    }

}
