package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.EventConstants;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.args.OptionArgs;
import com.cryptic.utility.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

public class DialogueOptions extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_OPTIONS;
    }

    @Override
    public void beforeOpen(Player player) {
        var record = player.dialogueRecord.getType();
        if (record instanceof OptionArgs dialogueOptionRecord) {
            setEvents(new EventNode(WidgetUtil.componentToId(ComponentID.DIALOG_OPTION_OPTIONS), 0, dialogueOptionRecord.options().length, new ArrayList<>(List.of(EventConstants.PAUSE))));
            player.varps().sendTempVarbit(10670, 1);
            player.getPacketSender().runClientScriptNew(2379);
            String options = joinWithPipe(dialogueOptionRecord.options());
            player.getPacketSender().runClientScriptNew(58, dialogueOptionRecord.title(), options);
        }
    }

    public static String joinWithPipe(String... chats) {
        if (chats == null || chats.length == 0) {
            return "";
        }
        return String.join("|", chats);
    }

}
