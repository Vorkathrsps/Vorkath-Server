package com.aelous.model.content.areas.zeah.catacombs;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 29, 2020 22:03
 */
public class Catacombs extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == VINE_LADDER) {
                Ladders.ladderUp(player, new Tile(1639, 3673), true);
                return true;
            }

            if (obj.getId() == VINE_28895) {
                Ladders.ladderUp(player, new Tile(1562, 3791), true);
                return true;
            }

            if (obj.getId() == STATUE_27785) {
                Ladders.ladderDown(player, new Tile(1665, 10050), true);
                return true;
            }

            if (obj.getId() == 28921) {
                Ladders.ladderDown(player, new Tile(1617, 10101), true);
                return true;
            }

            if (obj.getId() == VINE_28896) {
                Ladders.ladderUp(player, new Tile(1469, 3653), true);
                return true;
            }

            if (obj.getId() == 28919) {
                Ladders.ladderDown(player, new Tile(1650, 9987), true);
                return true;
            }

            if (obj.getId() == VINE_28897) {
                Ladders.ladderUp(player, new Tile(1667, 3565), true);
                return true;
            }

            if (obj.getId() == 28918) {
                Ladders.ladderDown(player, new Tile(1725, 9993), true);
                return true;
            }

            if (obj.getId() == VINE_28898) {
                Ladders.ladderUp(player, new Tile(1696, 3864), true);
                return true;
            }

            if (obj.getId() == 28920) {
                Ladders.ladderDown(player, new Tile(1719, 10101), true);
                return true;
            }

            if (obj.getId() == STONE_28893) {
                if (!player.getSkills().check(Skills.AGILITY, 34, "use this shortcut"))
                    return true;
                if (player.tile().equals(1613, 10069)) {
                    Chain.bound(player).name("Shortcut1Task").runFn(2, () -> {
                        player.lock();
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x, player.tile().y - 1)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x, player.tile().y - 1)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x, player.tile().y - 1)).then(1, () -> {
                        player.setPositionToFace(obj.tile());
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x - 1, player.tile().y)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x - 1, player.tile().y)).then(1, () -> {
                        player.setPositionToFace(obj.tile());
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x, player.tile().y - 1)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x, player.tile().y - 1)).then(1, () -> {
                        player.setPositionToFace(obj.tile());
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x - 1, player.tile().y)).then(1, () -> {
                        player.setPositionToFace(obj.tile());
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x, player.tile().y - 1)).then(1, () -> player.animate(741)).then(1, () -> {
                        player.teleport(player.tile().x, player.tile().y - 2);
                        player.unlock();
                    });
                } else if (player.tile().equals(1610, 10062)) {
                    Chain.bound(player).name("Shortcut2Task").runFn(2, () -> {
                        player.lock();
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x, player.tile().y + 1)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x, player.tile().y + 1)).then(1, () -> {
                        player.setPositionToFace(new Tile(1611, 10064));
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x + 1, player.tile().y)).then(1, () -> {
                        player.setPositionToFace(new Tile(1611, 10065));
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x, player.tile().y + 1)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x, player.tile().y + 1)).then(1, () -> {
                        player.setPositionToFace(new Tile(1612, 10066));
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x + 1, player.tile().y)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x + 1, player.tile().y)).then(1, () -> {
                        player.setPositionToFace(new Tile(1613, 10067));
                        player.animate(741);
                    }).then(1, () -> player.teleport(player.tile().x, player.tile().y + 1)).then(1, () -> player.animate(741)).then(1, () -> player.teleport(player.tile().x, player.tile().y + 1)).then(1, () -> player.animate(741)).then(1, () -> {
                        player.teleport(player.tile().x, player.tile().y + 2);
                        player.unlock();
                    });
                }
                return true;
            }

            if (obj.getId() == CRACK_28892 && obj.tile().equals(1706, 10077)) {
                squeezeThroughCrack(player, obj, new Tile(1716, 10056, 0), 34);
                return true;
            }

            if (obj.getId() == CRACK_28892 && obj.tile().equals(1716, 10057)) {
                squeezeThroughCrack(player, obj, new Tile(1706, 10078, 0), 34);
                return true;
            }

            if (obj.getId() == CRACK_28892 && obj.tile().equals(1646, 10001)) {
                squeezeThroughCrack(player, obj, new Tile(1648, 10009, 0), 25);
                return true;
            }

            if (obj.getId() == CRACK_28892 && obj.tile().equals(1648, 10008)) {
                squeezeThroughCrack(player, obj, new Tile(1646, 10000, 0), 25);
                return true;
            }
        }
        return false;
    }

    private static void squeezeThroughCrack(Player player, GameObject crack, Tile destination, int levelReq) {
        if (!player.getSkills().check(Skills.AGILITY, levelReq, "use this shortcut"))
            return;
        player.lock();
        Chain.bound(null).runFn(1, () -> {
            player.animate(746);
        }).then(1, () -> {
            player.teleport(destination);
            player.animate(748);
        }).then(1, player::unlock);
    }

    public static int getNextTotemPiece(Player player) {
        int base = 0, middle = 0, top = 0;
        for (Item item : player.inventory().getItems()) {
            if (item == null)
                continue;
            if (item.getId() == 19679)
                base += item.getAmount();
            else if (item.getId() == 19681)
                middle += item.getAmount();
            else if (item.getId() == 19683)
                top += item.getAmount();
        }
        for (Item item : player.getBank().getItems()) {
            if (item == null)
                continue;
            if (item.getId() == 19679)
                base += item.getAmount();
            else if (item.getId() == 19681)
                middle += item.getAmount();
            else if (item.getId() == 19683)
                top += item.getAmount();
        }

        int lowest = Math.min(base, Math.min(middle, top));
        if (lowest == base)
            return 19679;
        else if (lowest == middle)
            return 19681;
        else return 19683;
    }
}
