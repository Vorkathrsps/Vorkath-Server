package com.cryptic.model.content.areas.wilderness.bossrooms.memorial;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;

import static com.cryptic.utility.ItemIdentifiers.BLOOD_MONEY;

public class MemorialDialogue extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (!player.hasAttrib(AttributeKey.VETION_ENTRY_FEE)) {
            send(DialogueType.ITEM_STATEMENT,new Item(BLOOD_MONEY, 100000), "", "The base entry fee for these caves is 50,000 blood money,", "would you like too enter?");
            setPhase(0);
        } else {
            send(DialogueType.ITEM_STATEMENT,new Item(BLOOD_MONEY, 100000), "", "The base entry fee for these caves is 50,000 blood money,", "which you have paid. Killing wilderness bosses will", "reduce the fee by 10,000. You have a 50,000 discount", "towards your next fee.");
            setPhase(2);
        }
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.OPTION, "Pay 50,000 Blood Money For Entry?", "Yes", "No");
            setPhase(1);
        }
        if(isPhase(2)) {
            player.teleport(1888, 11547, 1);
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                if(!player.inventory().contains(BLOOD_MONEY, 50000)) {
                    player.message(Color.RED.wrap("You do not have 50,000 blood money in your inventory."));
                    stop();
                    return;
                }
                player.inventory().remove(new Item(BLOOD_MONEY, 50000),true);
                player.putAttrib(AttributeKey.VETION_ENTRY_FEE, true);
                setPhase(2);
            }
            if(option == 2) {
                stop();
            }
        }
    }
}

