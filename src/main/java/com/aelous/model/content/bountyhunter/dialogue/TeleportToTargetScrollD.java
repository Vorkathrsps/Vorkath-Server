package com.aelous.model.content.bountyhunter.dialogue;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author Patrick van Elderen | December, 25, 2020, 18:18
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TeleportToTargetScrollD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.TARGET_TELEPORT_SCROLL), "", "The 'Teleport to Bounty Target' spell requires <col=800000>level 85", "<col=800000>magic</col>, and can be cast only <col=800000>within the Wilderness</col>,", "while you have a Bounty Target. This scroll will be", "destroyed when it has been read.");
        setPhase(0);
    }

    @Override
    public void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION,"Are you sure you want to learn the spell?", "Yes, I want to learn the spell.", "No, I'll keep the scroll.");
            setPhase(1);
        }
        else if (isPhase(2)) stop();
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            if(option == 1) {
                send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.TARGET_TELEPORT_SCROLL), "", "You have learnt the 'Teleport to Bounty Target' spell.");
                player.inventory().remove(ItemIdentifiers.TARGET_TELEPORT_SCROLL);
                player.putAttrib(AttributeKey.BOUNTY_HUNTER_TARGET_TELEPORT_UNLOCKED, true);
                setPhase(2);
            } else if(option == 2) {
                stop();
            }
        }
    }
}
