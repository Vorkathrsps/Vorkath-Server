package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class FpkMerkCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://www.youtube.com/channel/UCeVDQGzCI3dxp8NAT_YBIkQ");
        player.message("Opening FPK Merk's channel in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
