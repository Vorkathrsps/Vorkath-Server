package com.cryptic.model.content.chests;

import com.cryptic.model.content.items.loot.CollectionItemHandler;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.ItemIdentifiers.*;

public class CrystalChest extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == 37342) {
            if (option == 1) {
                if (player.getInventory().contains(ENHANCED_CRYSTAL_KEY)) {
                    player.lock();
                    CollectionItemHandler.rollKeyReward(player, ItemIdentifiers.ENHANCED_CRYSTAL_KEY);
                    Chain.noCtx().runFn(1, () -> {
                        player.varps().setVarbit(9296, 1);
                    }).then(1, () -> {
                        player.varps().setVarbit(9296, 0);
                        player.unlock();
                    });
                } else {
                    player.message(Color.BLUE.wrap("You do not Enhanced crystal key inside of your inventory."));
                }
            }
            return true;
        }
        return false;
    }
}
