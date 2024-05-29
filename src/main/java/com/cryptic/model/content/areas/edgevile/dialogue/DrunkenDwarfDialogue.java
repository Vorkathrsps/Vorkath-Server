package com.cryptic.model.content.areas.edgevile.dialogue;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.items.Item;

import static com.cryptic.model.map.object.dwarf_cannon.DwarfCannon.*;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.DRUNKEN_DWARF_2408;

/**
 * @author Origin | April, 17, 2021, 15:21
 * 
 */
public class DrunkenDwarfDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        var reclaim = player.<Boolean>getAttribOr(AttributeKey.LOST_CANNON,false);
        if (reclaim) {
            sendPlayerChat(Expression.HAPPY, "I've lost my cannon.");
            setPhase(0);
        } else {
            sendNpcChat(DRUNKEN_DWARF_2408, Expression.SAD, "Oh dear, I'm only allowed to replace cannons that were", " stolen in action. I'm sorry, but you'll have to buy", "a new set.");
            setPhase(3);
        }
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            sendNpcChat(DRUNKEN_DWARF_2408, Expression.HAPPY, "That's unfortunate... but don't worry, I can sort you out.");
            setPhase(1);
        } else if(isPhase(1)) {
            player.inventory().addOrBank(new Item(BASE), new Item(STAND), new Item(BARRELS), new Item(FURNACE));
            player.putAttrib(AttributeKey.LOST_CANNON,false);
            sendNpcChat(DRUNKEN_DWARF_2408, Expression.HAPPY, "Keep that quiet or I'll be in real trouble!");
            setPhase(2);
        } else if(isPhase(2)) {
            sendPlayerChat(Expression.HAPPY, "Of course.");
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
