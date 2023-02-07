package com.aelous.model.entity.player.commands.impl.owner;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.masks.impl.tinting.Tinting;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class TintingTest implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        byte hue = 0;
        byte sat = 6;
        byte lum = 28;
        byte opac = 112;

        Tinting tinting = new Tinting((short) 100, (short) 100, hue, sat, lum, opac);
        player.setTinting(tinting, player);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
