package com.cryptic.model.content.areas.edgevile;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.player.*;
import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin | January, 19, 2021, 13:41
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class IronManTutor extends Dialogue {

    private static final Logger logger = LogManager.getLogger(IronManTutor.class);

    public static void armour(Player player) {
        switch (player.getIronManStatus()) {
            case REGULAR -> {
                if (player.ownsAny(IRONMAN_HELM, IRONMAN_PLATEBODY, IRONMAN_PLATELEGS)) {
                    player.message("Come back when you've lost your ironman armour.");
                    return;
                }
                player.inventory().add(new Item(IRONMAN_HELM, 1), true);
                player.inventory().add(new Item(IRONMAN_PLATEBODY, 1), true);
                player.inventory().add(new Item(IRONMAN_PLATELEGS, 1), true);
            }
            case HARDCORE -> {
                if (player.ownsAny(HARDCORE_IRONMAN_HELM, HARDCORE_IRONMAN_PLATEBODY, HARDCORE_IRONMAN_PLATELEGS)) {
                    player.message("Come back when you've lost your ironman armour.");
                    return;
                }
                player.inventory().add(new Item(HARDCORE_IRONMAN_HELM, 1), true);
                player.inventory().add(new Item(HARDCORE_IRONMAN_PLATEBODY, 1), true);
                player.inventory().add(new Item(HARDCORE_IRONMAN_PLATELEGS, 1), true);
            }
            case ULTIMATE -> {
                if (player.ownsAny(ULTIMATE_IRONMAN_HELM, ULTIMATE_IRONMAN_PLATEBODY, ULTIMATE_IRONMAN_PLATELEGS)) {
                    player.message("Come back when you've lost your ironman armour.");
                    return;
                }
                player.inventory().add(new Item(ULTIMATE_IRONMAN_HELM, 1), true);
                player.inventory().add(new Item(ULTIMATE_IRONMAN_PLATEBODY, 1), true);
                player.inventory().add(new Item(ULTIMATE_IRONMAN_PLATELEGS, 1), true);
            }
            default -> player.message("Only ironman players can claim their armour.");
        }

        if (player.getIronManStatus() != IronMode.NONE) {
           player.getDialogueManager().npcChat( Expression.HAPPY, NpcIdentifiers.IRON_MAN_TUTOR, "There you go. Wear it with pride.");
        }
    }

    @Override
    protected void start(Object... parameters) {
        if (!GameServer.properties().enableChangeAccountType) {
            player.message("You are currently unable to change your account type. Please try again later.");
            return;
        }

        setPhase(0);
        //sendNpcChat(player.getInteractingNpcId(), Expression.DEFAULT, "Would you like to change your account type to", (player.mode() == GameMode.INSTANT_PKER ? GameMode.TRAINED_ACCOUNT.toName() : GameMode.INSTANT_PKER.toName()) + "?");
    }

    @Override
    public void next() {
        if (isPhase(0)) {
            sendOption(DEFAULT_OPTION_TITLE, "Yes.", "No thanks.");
            setPhase(1);
        }
        if (isPhase(3)) {
                sendOption(DEFAULT_OPTION_TITLE, "Yes.", "No thanks.");
            setPhase(4);
        }
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            setPhase(2);
            if (option == 1) {
                if (player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
                    sendNpcChat(player.getInteractingNpcId(), Expression.DEFAULT, "Are you sure?", "All of your levels you have will be reset.");
                } else {
                    sendNpcChat(player.getInteractingNpcId(), Expression.DEFAULT, "Are you sure?");
                }
                setPhase(3);
            } else {
                stop();
            }
        }
        if (isPhase(4)) {
            if (option == 1) {
                player.resetSkills();
                // iron to trained
                GameMode gameMode = (player.getGameMode() == GameMode.TRAINED_ACCOUNT ? GameMode.TRAINED_ACCOUNT : GameMode.TRAINED_ACCOUNT);
                player.setGameMode(gameMode);
                player.getPacketSender().sendString(QuestTab.InfoTab.GAME_MODE.childId, QuestTab.InfoTab.INFO_TAB.get(QuestTab.InfoTab.GAME_MODE.childId).fetchLineData(player));
                //logger.info(player.toString() + " has changed their account type to " + gameMode.toName());
                Utils.sendDiscordInfoLog(player.toString() + " has changed their account type to " + gameMode.toName());
                    starter(player);
                }
            stop();
        }
    }

    public static void max(Player player) {
        for (int skill = 0; skill < 7; skill++) {
            player.getSkills().setXp(skill, Skills.levelToXp(99));
            player.getSkills().update();
            player.getSkills().recalculateCombat();
        }
    }

    public static void starter(Player player) {
        Item[] training_equipment = {
            new Item(ItemIdentifiers.BRONZE_ARROW, 10_000),
            new Item(ItemIdentifiers.IRON_KNIFE, 10_000),
            new Item(ItemIdentifiers.AIR_RUNE, 10_000),
            new Item(ItemIdentifiers.MIND_RUNE, 10_000),
            new Item(ItemIdentifiers.CHAOS_RUNE, 10_000),
            new Item(ItemIdentifiers.WATER_RUNE, 10_000),
            new Item(ItemIdentifiers.EARTH_RUNE, 10_000),
            new Item(ItemIdentifiers.FIRE_RUNE, 10_000),
            new Item(ItemIdentifiers.STAFF_OF_AIR, 1),
            new Item(ItemIdentifiers.SHORTBOW, 1),
            new Item(ItemIdentifiers.IRON_SCIMITAR, 1),
            new Item(ItemIdentifiers.IRON_FULL_HELM, 1),
            new Item(ItemIdentifiers.IRON_PLATEBODY, 1),
            new Item(ItemIdentifiers.IRON_PLATELEGS, 1),
            new Item(ItemIdentifiers.CLIMBING_BOOTS, 1),
            new Item(ItemIdentifiers.BLUE_WIZARD_HAT, 1),
            new Item(ItemIdentifiers.BLUE_WIZARD_ROBE, 1),
            new Item(ItemIdentifiers.BLUE_SKIRT, 1),
            new Item(ItemIdentifiers.LEATHER_BODY, 1),
            new Item(ItemIdentifiers.LEATHER_CHAPS, 1),
            new Item(ItemIdentifiers.TINDERBOX, 1),
            new Item(SMALL_FISHING_NET, 1),
            new Item(BRONZE_PICKAXE, 1),
            new Item(BRONZE_AXE, 1),
        };
        player.inventory().addAll(training_equipment);
        player.message("You have been given some training equipment.");
    }
}
