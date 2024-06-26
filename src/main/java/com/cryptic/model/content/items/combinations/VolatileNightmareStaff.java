package com.cryptic.model.content.items.combinations;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * The volatile nightmare staff is a staff that is created by attaching a volatile orb to the Nightmare staff.
 * Requiring level 75 Magic and 50 Hitpoints to wield, it can autocast offensive standard spells as well as Ancient Magicks.
 *
 * @author Origin | Zerikoth | PVE
 * @date februari 10, 2020 13:09
 */
public class VolatileNightmareStaff extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        if(player.inventory().containsAll(NIGHTMARE_STAFF, VOLATILE_ORB)) {
            boolean dontAskAgain = player.getAttribOr(AttributeKey.VOLATILE_NIGHTMARE_STAFF_QUESTION, false);
            if(dontAskAgain) {
                player.inventory().remove(new Item(NIGHTMARE_STAFF, 1));
                player.inventory().remove(new Item(VOLATILE_ORB, 1));
                player.inventory().add(new Item(VOLATILE_NIGHTMARE_STAFF, 1));
                send(DialogueType.ITEM_STATEMENT, new Item(VOLATILE_NIGHTMARE_STAFF), "", "You add the orb to the staff.");
                setPhase(1);
            } else {
                send(DialogueType.OPTION, "Add the orb to the staff?", "Yes.", "Yes, and don't ask again.", "No.");
                setPhase(0);
            }
        }
    }

    @Override
    protected void next() {
        if(getPhase() == 1) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 0) {
            if(option == 1) {
                if(!player.inventory().containsAll(NIGHTMARE_STAFF, VOLATILE_ORB)) {
                    stop();
                    return;
                }
                player.inventory().remove(new Item(NIGHTMARE_STAFF, 1));
                player.inventory().remove(new Item(VOLATILE_ORB, 1));
                player.inventory().add(new Item(VOLATILE_NIGHTMARE_STAFF, 1));
                send(DialogueType.ITEM_STATEMENT, new Item(VOLATILE_NIGHTMARE_STAFF), "", "You add the orb to the staff.");
                setPhase(1);
            } else if(option == 2) {
                if(!player.inventory().contains(NIGHTMARE_STAFF, VOLATILE_ORB)) {
                    stop();
                    return;
                }
                player.inventory().remove(new Item(NIGHTMARE_STAFF, 1));
                player.inventory().remove(new Item(VOLATILE_ORB, 1));
                player.inventory().add(new Item(VOLATILE_NIGHTMARE_STAFF, 1));
                player.putAttrib(AttributeKey.VOLATILE_NIGHTMARE_STAFF_QUESTION, true);
                send(DialogueType.ITEM_STATEMENT, new Item(VOLATILE_NIGHTMARE_STAFF), "", "You add the orb to the staff.");
                setPhase(1);
            } else if(option == 3) {
                stop();
            }
        }
    }

    public static boolean dismantle(Player player, Item item) {
        if(item.getId() != VOLATILE_NIGHTMARE_STAFF) {
            return false;
        }

        if(player.inventory().getFreeSlots() < 2) {
            player.message("You need at least 2 free inventory slots.");
            return false;
        }

        player.inventory().remove(new Item(VOLATILE_NIGHTMARE_STAFF, 1));
        player.inventory().add(new Item(VOLATILE_ORB, 1));
        player.inventory().add(new Item(NIGHTMARE_STAFF, 1));

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, new Item(NIGHTMARE_STAFF), "", "You remove the orb from the staff.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if(getPhase() == 0) {
                    stop();
                }
            }
        });
        return true;
    }
}
