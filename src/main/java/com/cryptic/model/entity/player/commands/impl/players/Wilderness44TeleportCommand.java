package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | January, 11, 2021, 18:08
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Wilderness44TeleportCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {

        Tile tile = new Tile(2972, 3863);

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
            return;
        }

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendStatement("This teleport will send you to a dangerous area.", "Do you wish to continue?");
                setPhase(1);
            }

            @Override
            protected void next() {
                if (isPhase(1)) {
                    sendOption(DEFAULT_OPTION_TITLE, "Yes.", "No.");
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
                    player.message("You have been teleported to level 44 wilderness.");
                } else if (option == 2) {
                    stop();
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    /**
     * @author Ynneh | 10/03/2022 - 04:35
     * <https://github.com/drhenny>
     */
    public static class SoundCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            try {
                int sound = Integer.parseInt(parts[1]);
                player.sendPublicSound(sound, 0);
                player.getPacketSender().sendMessage("Sending soundId="+sound);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean canUse(Player player) {
            return PlayerRights.is(player, PlayerRights.PLAYER);
        }
    }
}
