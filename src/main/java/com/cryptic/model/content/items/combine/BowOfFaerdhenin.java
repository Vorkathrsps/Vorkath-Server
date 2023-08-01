package com.cryptic.model.content.items.combine;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.utility.ItemIdentifiers.*;

public class BowOfFaerdhenin extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.getInventory().containsAll(ItemIdentifiers.BOW_OF_FAERDHINEN, ItemIdentifiers.CRYSTAL_OF_IORWERTH)) {
            boolean dontAskAgain = player.getAttribOr(AttributeKey.BOW_OF_FAERDHENIN_QUESTION, false);
            if (dontAskAgain) {
                player.inventory().remove(new Item(BOW_OF_FAERDHINEN, 1));
                player.inventory().remove(new Item(CRYSTAL_OF_IORWERTH, 1));
                player.inventory().add(new Item(BOW_OF_FAERDHINEN_C_25886, 1));
                send(DialogueType.ITEM_STATEMENT, new Item(BOW_OF_FAERDHINEN_C_25886), "", "You add the crystal to the bow of faerdhenin.");
                setPhase(1);
            } else {
                send(DialogueType.OPTION, "Add the crystal of iowerth to the bow?", "Yes.", "Yes, and don't ask again.", "No.");
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
                if(!player.inventory().containsAll(BOW_OF_FAERDHINEN, CRYSTAL_OF_IORWERTH)) {
                    stop();
                    return;
                }
                player.inventory().remove(new Item(BOW_OF_FAERDHINEN, 1));
                player.inventory().remove(new Item(CRYSTAL_OF_IORWERTH, 1));
                player.inventory().add(new Item(BOW_OF_FAERDHINEN_C_25886, 1));
                send(DialogueType.ITEM_STATEMENT, new Item(BOW_OF_FAERDHINEN_C_25886), "", "You add the crystal to the bow of faerdhenin.");
                setPhase(1);
            } else if(option == 2) {
                if(!player.inventory().contains(BOW_OF_FAERDHINEN, CRYSTAL_OF_IORWERTH)) {
                    stop();
                    return;
                }
                player.inventory().remove(new Item(BOW_OF_FAERDHINEN, 1));
                player.inventory().remove(new Item(CRYSTAL_OF_IORWERTH, 1));
                player.inventory().add(new Item(BOW_OF_FAERDHINEN_C_25886, 1));
                player.putAttrib(AttributeKey.BOW_OF_FAERDHENIN_QUESTION, true);
                send(DialogueType.ITEM_STATEMENT, new Item(BOW_OF_FAERDHINEN_C_25886), "", "You add the crystal to the bow of faerdhenin.");
                setPhase(1);
            } else if(option == 3) {
                stop();
            }
        }
    }
}
