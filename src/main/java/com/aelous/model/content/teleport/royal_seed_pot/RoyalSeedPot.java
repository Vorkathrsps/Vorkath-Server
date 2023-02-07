package com.aelous.model.content.teleport.royal_seed_pot;

import com.aelous.GameServer;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

public class RoyalSeedPot extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 1) {
            if(item.getId() == ItemIdentifiers.ROYAL_SEED_POD) {
                player.stopActions(true);
                if (!Teleports.canTeleport(player, true, TeleportType.ABOVE_20_WILD))
                    return true;
                player.graphic(767);
                player.animate(4544);
                player.lockNoDamage();
                Chain.bound(null).runFn(3, () -> player.looks().transmog(716)).then(1, () -> player.teleport(GameServer.properties().defaultTile)).then(2, () -> player.graphic(769)).then(2, () -> {
                    player.looks().transmog(-1);
                    player.animate(-1);
                    player.getTimers().cancel(TimerKey.FROZEN);
                    player.getTimers().cancel(TimerKey.REFREEZE);
                    player.unlock();
                });
                return true;
            }
        }
        return false;
    }

}
