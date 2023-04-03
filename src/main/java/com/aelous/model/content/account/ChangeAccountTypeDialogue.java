package com.aelous.model.content.account;

import com.aelous.GameServer;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick van Elderen | May, 30, 2021, 12:11
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class ChangeAccountTypeDialogue extends Dialogue {

    private static final Logger logger = LogManager.getLogger(ChangeAccountTypeDialogue.class);

    @Override
    protected void start(Object... parameters) {
        if (!GameServer.properties().enableChangeAccountType) {
            player.message("You are currently unable to change your account type. Please try again later.");
            return;
        }
        setPhase(0);
        send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.DEFAULT, "Would you like to change your account type to", (player.getGameMode() == GameMode.TRAINED_ACCOUNT));
    }

    @Override
    public void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No thanks.");
            setPhase(1);
        }
        if (isPhase(3)) {
            if (player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No, I don't want to lose my levels!");
            } else {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No thanks.");
            }
            setPhase(4);
        }
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            setPhase(2);
            if (option == 1) {
                if (player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
                    send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.DEFAULT, "Are you sure?", "All of your levels and", "any Hall of Fame entries you have will be reset.");
                } else {
                    send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.DEFAULT, "Are you sure?");
                }
                setPhase(3);
            } else {
                stop();
            }
        }
        if (isPhase(4)) {
            if (option == 1) {
                player.resetSkills();
                GameMode accountType = player.getGameMode(GameMode.TRAINED_ACCOUNT);
                player.getGameMode(accountType);
                player.setIronmanStatus(IronMode.NONE);
                //logger.info(player.toString() + " has changed their account type to "+player.mode().toName());
                Utils.sendDiscordInfoLog(player.toString() + " has changed their account type to "+player.getGameMode().toName());
                /*if (accountType == GameMode.INSTANT_PKER) {
                    player.getPresetManager().open();
                }*/
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
                    };
                    player.getInventory().addAll(training_equipment);
                    player.message("You have been given some training equipment.");
                }
                player.message("You can also spawn items with the spawn tab in the bottom right.");
            }
            stop();
        }

}
