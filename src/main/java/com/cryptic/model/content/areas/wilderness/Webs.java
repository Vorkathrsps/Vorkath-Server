package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.WEB;

public class Webs extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == WEB) {
                int weapon = player.getEquipment().hasWeapon() ? player.getEquipment().getWeapon().getId() : -1;
                String wepName = weapon == -1 ? "" : new Item(weapon).name().toLowerCase();
                boolean hasSharpEdge = wepName.contains("scythe") || wepName.contains("sword") || wepName.contains("dagger") || wepName.contains("axe") || wepName.contains("whip") || wepName.contains("scimitar") || wepName.contains("of light") || wepName.contains("dead") || wepName.contains("tent") || wepName.contains("claw") || wepName.contains("blade of saeldor") || wepName.contains("rapier") || wepName.contains("staff_of_light") || wepName.contains("toxic_staff") || wepName.contains("voidwaker") || wepName.contains("longsword");

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
        boolean wearingWildernessSword = player.getEquipment().containsAny(ItemIdentifiers.WILDERNESS_SWORD_1, ItemIdentifiers.WILDERNESS_SWORD_2, ItemIdentifiers.WILDERNESS_SWORD_3, ItemIdentifiers.WILDERNESS_SWORD_4);
        if (wearingWildernessSword) {
            player.message("You slash the web apart.");
            var originalId = obj.getId();
            int replacementId = 734;
            Chain.noCtx()
                .runFn(1, () -> obj.setId(replacementId))
                .then(30, () -> obj.setId(originalId));
            return;
        }
        if (Utils.random(100) >= 50) {
            player.message("You slash the web apart.");
            var originalId = obj.getId();
            int replacementId = 734;
            Chain.noCtx().runFn(1, () -> obj.setId(replacementId)).then(30, () -> obj.setId(originalId));
        } else {
            player.message("You fail to cut through it.");
        }
    }
}
