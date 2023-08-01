package com.cryptic.model.content.teleport.royal_seed_pot;

import com.cryptic.GameServer;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

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
                Chain.bound(null).runFn(3, () -> player.looks().transmog(716)).then(1, () -> player.teleport(GameServer.properties().defaultTile.tile())).then(2, () -> player.graphic(769)).then(2, () -> {
                    player.looks().transmog(-1);
                    player.animate(-1);
                    player.getTimers().cancel(TimerKey.FROZEN);
                    player.getTimers().cancel(TimerKey.FREEZE_IMMUNITY);
                    player.unlock();
                });
                return true;
            }
        }
        return false;
    }

}
