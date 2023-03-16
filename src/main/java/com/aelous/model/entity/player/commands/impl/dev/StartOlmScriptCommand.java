package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

/**
 * @author Patrick van Elderen | May, 16, 2021, 22:37
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class StartOlmScriptCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        //Get the party
        Party party = player.raidsParty;

        if (party != null) {
            //Set raid finished
            party.setRaidStage(7);

            //Teleport to boss
            Tile bossRoomTile = new Tile(3232, 5730, party.getHeight());
            player.teleport(bossRoomTile);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isDeveloper(player);
    }
}
