package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.RaidsType;
import com.cryptic.model.content.raids.chamber_of_xeric.ChamberOfXerics;
import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.inter.clan.ClanMember;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | May, 16, 2021, 22:37
 * 
 */
public class StartOlmScriptCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        Party party = player.raidsParty;
        if (party != null) {
            for (NPC monster : party.monsters) {
                monster.remove();
            }
            party.monsters.clear();
        }

        Party.createParty(player);
        if (player.getClan() == null) {
            player.message("not in cc");
            return;
        }
        party = player.raidsParty;
        party.setRaidsSelected(RaidsType.CHAMBER_OF_XERICS);
        for (ClanMember member : player.getClan().members()) {
            var mp = member.getPlayer();
            if (!mp.isRegistered())
                mp = World.getWorld().getPlayerByName(mp.getUsername()).orElse(null);
            if (mp == null || !mp.isRegistered())
                return;
            party.addMember(mp);
            mp.raidsParty = party;
            mp.setRaids(party.getRaidsSelected() == RaidsType.CHAMBER_OF_XERICS ? new ChamberOfXerics() : null);
        }
        player.getRaids().startup(player);

        //Set raid finished
        party.setRaidStage(7);

        //Teleport to boss
        Tile bossRoomTile = new Tile(3232, 5730, party.getHeight());
        for (Player m : party.getMembers()) {
            m.teleport(bossRoomTile);
        }

    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isDeveloper(player);
    }
}
