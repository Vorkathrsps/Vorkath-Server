package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.WEB;

public class Webs extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == WEB) {
                int weapon = player.getEquipment().hasWeapon() ? player.getEquipment().getWeapon().getId() : -1;
                String wepName = weapon == -1 ? "" : new Item(weapon).name().toLowerCase();
                boolean hasSharpEdge = wepName.contains("scythe") || wepName.contains("sword") || wepName.contains("dagger") || wepName.contains("axe") || wepName.contains("whip") || wepName.contains("scimitar") || wepName.contains("of light") || wepName.contains("dead") || wepName.contains("tent") || wepName.contains("claw") || wepName.contains("blade of saeldor");

                int KNIFE = 946;
                if (player.inventory().contains(KNIFE)) {
                    player.animate(911);
                    slashWeb(player, obj);
                } else if (hasSharpEdge) {
                    player.animate(player.attackAnimation());
                    slashWeb(player, obj);
                } else {
                    player.message("Only a sharp blade can cut through this sticky web.");
                }
                return true;
            }
        }
        return false;
    }

    private void slashWeb(Player player, GameObject obj) {
        if (Utils.random(100) >= 50) {
            player.message("You slash the web apart.");
            player.lockDamageOk();
            Chain.bound(null).name("SlashWebTask").runFn(1, () -> {
                ObjectManager.removeObj(obj);
                ObjectManager.addObj(new GameObject(734, obj.tile(), obj.getType(), obj.getRotation()));
            }).then(100, () -> {
                ObjectManager.removeObj(new GameObject(734, obj.tile(), obj.getType(), obj.getRotation()));
                ObjectManager.addObj(obj);
            });
            player.unlock();
        } else {
            player.message("You fail to cut through it.");
        }
    }
}
