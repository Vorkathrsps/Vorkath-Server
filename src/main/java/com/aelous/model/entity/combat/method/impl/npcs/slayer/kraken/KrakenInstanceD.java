package com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 26, 2020
 */
public class KrakenInstanceD extends Dialogue {

    String currency = "BM";
    int currencyReq = 1000;

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.ITEM_STATEMENT, new Item(BLOOD_MONEY), "", "Would you like to pay the fee to enter an instanced area?", "<col=FF0000>Warning: any items dropped on death are permanently lost.", "When you leave, you'll have to pay again to enter.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            send(DialogueType.OPTION,DEFAULT_OPTION_TITLE, "Pay " + currencyReq + " " + currency + " to enter.", "Never mind.");
            setPhase(1);
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                boolean canEnter = false;
                int bmInInventory = player.inventory().count(BLOOD_MONEY);
                if (bmInInventory > 0) {
                    if(bmInInventory >= currencyReq) {
                        canEnter = true;
                        player.inventory().remove(BLOOD_MONEY, currencyReq);
                    }
                }

                if(!canEnter) {
                    player.message("You do not have enough BM to do this.");
                    stop();
                    return;
                }

                player.message("You pay " + currencyReq + " " + currency + " to enter an instance room.");
                player.getKrakenInstance().enterKrakenInstance(player);
                stop();
            } else if(option == 2) {
                stop();
            }
        }
    }
}
