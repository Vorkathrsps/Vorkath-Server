package com.cryptic.clientscripts.impl.dialogue.impl;

import com.cryptic.clientscripts.impl.dialogue.information.types.impl.OptionType;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.EventConstants;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class DialogueOptions extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_OPTIONS;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof OptionType dialogueOptionRecord) {
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
