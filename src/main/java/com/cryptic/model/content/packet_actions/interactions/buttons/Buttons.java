package com.cryptic.model.content.packet_actions.interactions.buttons;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
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
import com.cryptic.model.entity.player.*;
import com.cryptic.model.inter.clan.ClanButtons;
import com.cryptic.model.inter.dialogue.ItemActionDialogue;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.impl.ButtonClickPacketListener;
import com.cryptic.utility.Color;

import java.util.Arrays;

import static com.cryptic.model.content.areas.lumbridge.dialogue.Hans.getTimeDHS;
import static com.cryptic.model.content.collection_logs.LogType.BOSSES;
import static com.cryptic.model.entity.combat.magic.autocasting.Autocasting.ANCIENT_SPELL_AUTOCAST_STAFFS;
import static com.cryptic.model.entity.combat.magic.autocasting.Autocasting.MODERN_SPELL_AUTOCAST_STAFFS;
import static com.cryptic.model.items.container.shop.ShopUtility.*;
import static com.cryptic.network.packet.incoming.impl.ButtonClickPacketListener.LOGOUT;

/**
 * Handles button actions.
 */
public class Buttons {

    private static final int OPEN_COLLECTION_LOG = 80026;
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

    private static int GENERAL_SHOP_BUTTON_ID;
    private static int PKP_SHOP;
    private static int VOTE_SHOP;

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
                Tile tile = GameServer.getServerType().getHomeTile();
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
                    if (MagicSpellbook.LUNAR.equals(player.getSpellbook())) {
                        player.message(Color.RED.wrap("You cannot use Autocast on lunar spells."));
                        return;
                    }

                    if (staff != null) {
                        if (MagicSpellbook.ANCIENTS.equals(player.getSpellbook()) && ANCIENT_SPELL_AUTOCAST_STAFFS.contains(staff.getId())) {
                            if (player.getEquipment().contains(HARMONISED_NIGHTMARE_STAFF)) {
                                player.message(Color.RED.wrap("You cannot use Autocast with a " + ItemDefinition.cached.get(HARMONISED_NIGHTMARE_STAFF).name + "."));
                                return;
                            }
                            player.getInterfaceManager().setSidebar(0, 1689);
                        } else if (MagicSpellbook.NORMAL.equals(player.getSpellbook()) && MODERN_SPELL_AUTOCAST_STAFFS.contains(staff.getId())) {
                            player.getInterfaceManager().setSidebar(0, 1829);
                        } else {
                            player.message(Color.RED.wrap("You cannot use Autocast with a " + ItemDefinition.cached.get(staff.getId()).name + "."));
                        }
                    }
                }
            }
            case AUTOCAST_BUTTON_2 -> {
                player.putAttrib(AttributeKey.DEFENSIVE_AUTOCAST, true);
                if (!GameServer.properties().rightClickAutocast) {
                    if (MagicSpellbook.LUNAR.equals(player.getSpellbook())) {
                        player.message(Color.RED.wrap("You cannot use Autocast on lunar spells."));
                        player.getPacketSender().setDefensiveAutocastState(0);
                        return;
                    }
                    if (staff != null) {
                        if (MagicSpellbook.ANCIENTS.equals(player.getSpellbook()) && ANCIENT_SPELL_AUTOCAST_STAFFS.contains(staff.getId())) {
                            if (player.getEquipment().contains(HARMONISED_NIGHTMARE_STAFF)) {
                                player.message(Color.RED.wrap("You cannot use Autocast with a " + ItemDefinition.cached.get(HARMONISED_NIGHTMARE_STAFF).name + "."));
                                return;
                            }
                            player.getInterfaceManager().setSidebar(0, 1689);
                        } else if (MagicSpellbook.NORMAL.equals(player.getSpellbook()) && MODERN_SPELL_AUTOCAST_STAFFS.contains(staff.getId())) {
                            player.getInterfaceManager().setSidebar(0, 1829);
                        } else {
                            player.message(Color.RED.wrap("You cannot use Autocast with a " + ItemDefinition.cached.get(staff.getId()).name + "."));
                        }
                    }
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
                    if (player.getRaidParty() != null && player.getTheatreInterface() != null) {
                        if (player.getTheatreInterface().kick(player, button)) {
                            return;
                        }
                    }
                }
                if (CollectionLogButtons.onButtonClick(player, button)) {
                    return;
                }
                if (button == 73156) {
                    player.getPacketSender().sendConfig(1206, 0);
                    if (!IronMode.NONE.equals(player.getIronManStatus())) {
                        World.getWorld().shop(759).open(player);
                    } else {
                        World.getWorld().shop(5004).open(player);
                    }
                    return;
                }
                if (button == 73157) {
                    player.getPacketSender().sendConfig(1206, 1);
                    if (!IronMode.NONE.equals(player.getIronManStatus())) {
                        World.getWorld().shop(760).open(player);
                    } else {
                        World.getWorld().shop(5003).open(player);
                    }
                    return;
                }
                if (button == 73159) {
                    player.getPacketSender().sendConfig(1206, 3);
                    if (!IronMode.NONE.equals(player.getIronManStatus())) {
                        World.getWorld().shop(762).open(player);
                    } else {
                        World.getWorld().shop(AUBURYS_MAGIC_SHOP_ID).open(player);
                    }
                    return;
                }
                if (button == 73161) {
                    player.getPacketSender().sendConfig(1206, 5);
                    World.getWorld().shop(GENERAL_STORE_SHOP_ID).open(player);
                    return;
                }
                if (button == 73158) {
                    player.getPacketSender().sendConfig(1206, 2);
                    if (!IronMode.NONE.equals(player.getIronManStatus())) {
                        World.getWorld().shop(761).open(player);
                    } else {
                        World.getWorld().shop(LOWES_ARCHERY_SHOP_ID).open(player);
                    }
                    return;
                }
                if (button == 73162) {
                    player.getPacketSender().sendConfig(1206, 6);
                    World.getWorld().shop(PKP_SHOP_ID).open(player);
                    return;
                }
                if (button == 73163) {
                    player.getPacketSender().sendConfig(1206, 7);
                    World.getWorld().shop(VOTE_SHOP_ID).open(player);
                    return;
                }
                if (button == 73160) {
                    player.getPacketSender().sendConfig(1206, 4);
                    if (!IronMode.NONE.equals(player.getIronManStatus())) {
                        World.getWorld().shop(763).open(player);
                    } else {
                        World.getWorld().shop(KAQEMEEX_POTIONS_SHOP_ID).open(player);
                    }
                    return;
                }

                if (button == 73153) {
                    Shop.closeShop(player);
                    return;
                }

                if (button == 73164) {
                    World.getWorld().shop(350).open(player);
                    return;
                }
                if (button == 73165) {
                    if (!IronMode.NONE.equals(player.getIronManStatus())) {
                        World.getWorld().shop(764).open(player);
                    } else {
                        World.getWorld().shop(351).open(player);
                    }
                    return;
                }
                if (button == 73166) {
                    World.getWorld().shop(352).open(player);
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
