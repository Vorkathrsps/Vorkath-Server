package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since November 03, 2021
 */
public class VihticCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://www.youtube.com/c/Vihtic");
        player.message("Opening Vihtic's channel in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    /**
     * @author Ynneh | 01/04/2022 - 10:01
     * <https://github.com/drhenny>
     */
    public static class SoundmodeCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            try {
                Integer soundId = null;
                if (parts.length < 2) {
                    soundId = 1;
                } else
                    soundId = Integer.valueOf(parts[1]);
                player.lastSoundId = soundId;
                player.soundmode = !player.soundmode;
                player.getPacketSender().sendMessage("You have " + (player.soundmode ? "<col=0000ff>ENABLED</col>" : "<col=ff0000>DISABLED</col>") + " sound mode. " + (player.soundmode ? "You can now use <col=ff0000>F1</col> to loop sounds startID: <col=ff0000>" + soundId : ""));
                if (player.soundmode)
                    player.getPacketSender().sendMessage("To disable this feature just reuse the command ::togglesoundmode");
            } catch (Exception e) {
                player.getPacketSender().sendMessage("Error.. try using ::togglesoundmode or ::togglesoundmode ID");
            }
        }

        public void setup() {

        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }
}
