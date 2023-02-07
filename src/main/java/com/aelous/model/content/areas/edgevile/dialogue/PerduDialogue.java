package com.aelous.model.content.areas.edgevile.dialogue;

import com.aelous.model.content.mechanics.break_items.BreakItemsOnDeath;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

public class PerduDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Repair broken items.", "Automatically repair on death.", "Disable automatic repair on death.", "Nevermind.");
        setPhase(0);
    }

    @Override
    public void next() {
        if (isPhase(2)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Repair the items.", "No thanks.");
            setPhase(3);
        } else if (isPhase(4)) {
            stop();
        }
    }

    @Override
    public void select(int option) {
        if(isPhase(0)) {
            if (option == 1) {
                int cost = BreakItemsOnDeath.getRepairCost(player);
                if(cost > 0) {
                    send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.HAPPY, "It will cost you " + Color.RED.tag() + "" + Utils.insertCommasToNumber(Integer.toString(cost)) + "</col> BM to fix your", "broken items.");
                    setPhase(2);
                } else {
                    send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.NOT_INTERESTED, "You do not have any broken items.");
                    setPhase(4);
                }
            } else if (option == 2) {
                player.putAttrib(AttributeKey.REPAIR_BROKEN_ITEMS_ON_DEATH,true);
                player.message(Color.BLUE.tag()+"Perdu will now automatically repair your items on death.");
                stop();
            } else if (option == 3) {
                player.putAttrib(AttributeKey.REPAIR_BROKEN_ITEMS_ON_DEATH,false);
                player.message(Color.RED.tag()+"Perdu will no longer automatically repair your items on death.");
                stop();
            } else if (option == 4) {
                stop();
            }
        } else if (isPhase(3)) {
            if (option == 1) {
                stop();//Break the dialogue
                BreakItemsOnDeath.repair(player);
            } else if (option == 2) {
                stop();
            }
        }
    }

}
