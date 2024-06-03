package com.cryptic.clientscripts.impl.dialogue.impl;

import com.cryptic.clientscripts.impl.dialogue.information.types.impl.ProduceItemType;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.ItemType;

import java.util.Arrays;

public class DialogueProduceItem extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.PRODUCE_ITEM;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof ProduceItemType activeDialogueProduceItemItemRecord) {
            int baseChild = 14;
            int[] itemArray = new int[10];
            String[] nameArray = new String[10];

            Arrays.fill(itemArray, -1);
            Arrays.fill(nameArray, "|");

            for (int i = 0; i < activeDialogueProduceItemItemRecord.items().length; i++) {
                ItemType def = CacheManager.INSTANCE.getItem(activeDialogueProduceItemItemRecord.items()[i]);
                itemArray[i] = def.getId();
                nameArray[i] = def.getName();
            }

            player.varps().sendTempVarbit(10670, 1);
            player.getPacketSender().runClientScriptNew(2046, 0, activeDialogueProduceItemItemRecord.title(), 3,
                itemArray[0], itemArray[1], itemArray[2], itemArray[3], itemArray[4], itemArray[5], itemArray[6], itemArray[7], itemArray[8], itemArray[9], Math.max(2, 1));

        }
    }
}
