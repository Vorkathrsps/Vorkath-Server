package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.interfaces.EventConstants;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.DialogueDestroyItemRecord;
import com.cryptic.model.inter.dialogue.records.DialogueDoubleItemRecord;
import com.cryptic.model.inter.dialogue.records.DialogueProduceItemItemRecord;
import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.ItemType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogueProduceItem extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.PRODUCE_ITEM;
    }

    @Override
    public void beforeOpen(Player player) {
        DialogueProduceItemItemRecord activeDialogueProduceItemItemRecord = player.activeDialogueProduceItemItemRecord;
        if (activeDialogueProduceItemItemRecord == null) return;

        int baseChild = 14;
        int[] itemArray = new int[10];
        String[] nameArray = new String[10];

        Arrays.fill(itemArray, -1);
        Arrays.fill(nameArray, "|");

        for (int i = 0; i < activeDialogueProduceItemItemRecord.items().length; i++) {
            ItemType def = CacheManager.INSTANCE.getItem(activeDialogueProduceItemItemRecord.items()[i]);
            itemArray[i] = def.getId();
            nameArray[i] = STR."|\{def.getName()}";
        }

        player.varps().sendTempVarbit(10670, 1);
        player.getPacketSender().runClientScriptNew(2046, 0, activeDialogueProduceItemItemRecord.title() + String.join("", nameArray), 3,
            itemArray[0], itemArray[1], itemArray[2], itemArray[3], itemArray[4], itemArray[5], itemArray[6], itemArray[7], itemArray[8], itemArray[9], Math.max(2, 1));

    }


}
