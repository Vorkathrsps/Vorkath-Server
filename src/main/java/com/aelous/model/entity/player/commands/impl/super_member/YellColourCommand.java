package com.aelous.model.entity.player.commands.impl.super_member;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class YellColourCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getMemberRights().isSuperMemberOrGreater(player) && !player.getPlayerRights().isAdministrator(player)) {
            player.message("<col=ca0d0d>Only Sapphire Members may use this feature.");
            return;
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... options) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "<col=255>Blue", "<col=ca0d0d>Red", "<col=ffffff>White", "<col=006601>Green", "<col=0>Default");
                setPhase(0);
            }

            @Override
            public void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        player.putAttrib(AttributeKey.YELL_COLOUR, "255"); //blue
                        stop();
                    } else if (option == 2) {
                        player.putAttrib(AttributeKey.YELL_COLOUR, "CA0D0D"); //red
                        stop();
                    } else if (option == 3) {
                        player.putAttrib(AttributeKey.YELL_COLOUR, "ffffff"); //white
                        stop();
                    } else if (option == 4) {
                        player.putAttrib(AttributeKey.YELL_COLOUR, "006601"); //green
                        stop();
                    } else if (option == 5) {
                        player.putAttrib(AttributeKey.YELL_COLOUR, "0"); //black
                        stop();
                    }
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
