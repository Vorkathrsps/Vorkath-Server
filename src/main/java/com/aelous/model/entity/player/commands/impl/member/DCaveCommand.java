package com.aelous.model.entity.player.commands.impl.member;

import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;

public class DCaveCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (WildernessArea.inWild(player) && !player.getPlayerRights().isDeveloper(player)) {
            player.message("You can't use this command in the wilderness.");
            return;
        }

        Tile tile = new Tile(2335, 9795);

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
            return;
        }

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, "This teleport will send you to a dangerous area.", "Do you wish to continue?");
                setPhase(1);
            }

            @Override
            protected void next() {
                if (isPhase(1)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                    setPhase(2);
                }
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, tile);
                } else if (option == 2) {
                    stop();
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getMemberRights().isRegularMemberOrGreater(player) || player.getPlayerRights().isStaffMember(player);
    }
}
