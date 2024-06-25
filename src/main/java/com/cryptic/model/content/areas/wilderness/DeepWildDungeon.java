package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_19043;

/**
 * @author Origin | January, 17, 2021, 19:16
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DeepWildDungeon extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            boolean hasWildernessSword = player.getEquipment().containsAny(ItemIdentifiers.WILDERNESS_SWORD_2, ItemIdentifiers.WILDERNESS_SWORD_3, ItemIdentifiers.WILDERNESS_SWORD_4) || player.getInventory().containsAny(ItemIdentifiers.WILDERNESS_SWORD_2, ItemIdentifiers.WILDERNESS_SWORD_3, ItemIdentifiers.WILDERNESS_SWORD_4) || player.getBank().containsAny(ItemIdentifiers.WILDERNESS_SWORD_2, ItemIdentifiers.WILDERNESS_SWORD_3, ItemIdentifiers.WILDERNESS_SWORD_4);
            if (object.getId() == CREVICE_19043) {
                if (!hasWildernessSword) {
                    player.sendInformationMessage("You cannot use this shortcut until you've purchased a Wilderness Sword 2.");
                    return true;
                }
                if (player.getSkills().xpLevel(Skills.AGILITY) < 46) {
                    player.sendInformationMessage("You need a Agility level of 46 to pass this gate.");
                    return true;
                }
                if(player.tile().equals(3046, 10326)) {
                    player.teleport(new Tile(3048, 10336));
                } else if(player.tile().equals(3048, 10336)) {
                    player.teleport(new Tile(3046, 10326));
                }
                return true;
            }
        }
        return false;
    }
}
