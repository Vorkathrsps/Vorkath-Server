package com.aelous.model.content.areas.edgevile.dialogue;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.items.Item;

import static com.aelous.model.map.object.dwarf_cannon.DwarfCannon.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.DRUNKEN_DWARF_2408;

/**
 * @author Patrick van Elderen | April, 17, 2021, 15:21
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DrunkenDwarfDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        var reclaim = player.<Boolean>getAttribOr(AttributeKey.LOST_CANNON,false);
        if (reclaim) {
            send(DialogueType.PLAYER_STATEMENT,Expression.HAPPY, "I've lost my cannon.");
            setPhase(0);
        } else {
            send(DialogueType.NPC_STATEMENT,DRUNKEN_DWARF_2408, Expression.SAD, "Oh dear, I'm only allowed to replace cannons that were", " stolen in action. I'm sorry, but you'll have to buy", "a new set.");
            setPhase(3);
        }
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.NPC_STATEMENT,DRUNKEN_DWARF_2408, Expression.HAPPY, "That's unfortunate... but don't worry, I can sort you out.");
            setPhase(1);
        } else if(isPhase(1)) {
            player.inventory().addOrBank(new Item(BASE), new Item(STAND), new Item(BARRELS), new Item(FURNACE));
            player.putAttrib(AttributeKey.LOST_CANNON,false);
            send(DialogueType.NPC_STATEMENT,DRUNKEN_DWARF_2408, Expression.HAPPY, "Keep that quiet or I'll be in real trouble!");
            setPhase(2);
        } else if(isPhase(2)) {
            send(DialogueType.PLAYER_STATEMENT,Expression.HAPPY, "Of course.");
            setPhase(3);
        } else if(isPhase(3)) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        super.select(option);
    }
}
