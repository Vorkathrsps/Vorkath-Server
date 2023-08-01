package com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import java.util.Optional;

/**
 * @author Sharky
 * @Since January 18, 2023
 */
public class Stalagmite extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            if(object.getId() == 42944) {
                Nex nex = ZarosGodwars.nex;
                if(nex != null) {
                    if(nex.stalagmite != null) {
                        Optional<GameObject> stal = nex.stalagmite.stream().filter(o -> o.tile().equals(object.tile())).findFirst();
                        player.animate(player.attackAnimation());
                        Chain.bound(null).runFn(1, () -> {
                            stal.ifPresent(GameObject::remove);
                            nex.stalagmiteDestroyed = true;
                            player.resetFreeze();
                        });
                    }
                }
                return true;
            }
        }
        return false;
    }
}
