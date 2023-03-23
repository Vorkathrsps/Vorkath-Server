package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.inter.clan.Clan;
import com.aelous.model.inter.clan.ClanRepository;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

import java.util.Arrays;

public class ClanOutpostCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
            if (player.getClanChat() != null) {
                Clan clan = ClanRepository.get(player.getClanChat());

                if (clan != null) {
                    if (clan.meetingRoom == null) {
                        clan.meetingRoom = InstancedAreaManager.getSingleton().createInstancedArea(new Area(1, 2, 3, 4));
                        NPC pvpDummy = new NPC(NpcIdentifiers.UNDEAD_COMBAT_DUMMY, new Tile(2454, 2846,2 + clan.meetingRoom.getzLevel()));
                        pvpDummy.spawnDirection(1);
                        NPC slayerDummy = new NPC(NpcIdentifiers.COMBAT_DUMMY, new Tile(2454, 2848,2 + clan.meetingRoom.getzLevel()));
                        slayerDummy.spawnDirection(6);
                        clan.dummys = Arrays.asList(pvpDummy, slayerDummy);
                        World.getWorld().registerNpc(clan.dummys.get(0));
                        World.getWorld().registerNpc(clan.dummys.get(1));
                    }

                    Teleports.basicTeleport(player, new Tile(2452, 2847, 2 + clan.meetingRoom.getzLevel()));
                    player.message("You teleport to the " + player.getClanChat() + " clan outpost.");
                }
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
