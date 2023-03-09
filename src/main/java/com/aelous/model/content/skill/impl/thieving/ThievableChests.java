package com.aelous.model.content.skill.impl.thieving;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jak on 23/08/2016.
 */
public class ThievableChests extends PacketInteraction {

    static class Loot {
        private final int id;
        private final int min;
        private final int max;

        Loot(int id, int min, int max) {
            this.id = id;
            this.min = min;
            this.max = max;
        }
    }

    private enum Chests {
        LEVEL_13(13, 7.8, 7, new ArrayList<>(List.of(new Loot(ItemIdentifiers.COOKED_KARAMBWAN + 1, 1, 3))), new ArrayList<>(List.of(new Tile(2671, 3299, 1), new Tile(2612, 3314, 1)))),
        NATURE(28, 25.0, 8, new ArrayList<>(List.of(new Loot(ItemIdentifiers.SUPER_COMBAT_POTION4 + 1, 3, 3), new Loot(561, 1, 1))), new ArrayList<>(List.of(new Tile(2671, 3301, 1), new Tile(2614, 3314, 1)))),
        LEVEL_43(43, 125.0, 50, new ArrayList<>(List.of(new Loot(ItemIdentifiers.ANGLERFISH + 1, 5, 5))), new ArrayList<>(List.of(new Tile(3188, 3962), new Tile(3184, 3962), new Tile(3193, 3962)))),
        ARROWTIP(47, 150.0, 210, new ArrayList<>(List.of(new Loot(41, 5, 5))), new ArrayList<>(List.of(new Tile(0, 0)))),
        DORGESH_KAAN(52, 200.0,210, new ArrayList<>(List.of(new Loot(ItemIdentifiers.COOKED_KARAMBWAN + 1, 10, 15), new Loot(4548, 1, 1), new Loot(10981, 1, 1), new Loot(5013, 1, 1), new Loot(10192, 1, 1))), new ArrayList<>(List.of(new Tile(0, 0)))),
        BLOOD(59, 250.0, 135, new ArrayList<>(List.of(new Loot(ItemIdentifiers.SUPER_COMBAT_POTION4 + 1, 10, 20), new Loot(565, 2, 2))), new ArrayList<>(List.of(new Tile(2586, 9737), new Tile(2586, 9734)))),
        PALADIN(72, 500.0, 400, new ArrayList<>(List.of(new Loot(ItemIdentifiers.ANGLERFISH + 1, 10, 25), new Loot(383, 1, 1), new Loot(449, 1, 1), new Loot(1623, 1, 1))), new ArrayList<>(List.of(new Tile(2588, 3302), new Tile(2588, 3291)))),
        DORG_RICH(78, 650.0, 300, new ArrayList<>(List.of(new Loot(ItemIdentifiers.COOKED_KARAMBWAN + 1, 10, 35), new Loot(1623, 1, 1), new Loot(1621, 1, 1), new Loot(1619, 1, 1), new Loot(1617, 1, 1), new Loot(1625, 1, 1), new Loot(1627, 1, 1), new Loot(1629, 1, 1), new Loot(4548, 1, 1), new Loot(5013, 1, 1), new Loot(10954, 1, 1), new Loot(10956, 1, 1), new Loot(10958, 1, 1), new Loot(2351, 1, 1), new Loot(10973, 1, 1), new Loot(10980, 1, 1))), new ArrayList<>(List.of(new Tile(0, 0))));
        // ROGUE_CHEST - has its own impl for w1-3

        private final int level;
        private final double xp;
        private final int respawnTime;
        private final ArrayList<Loot> loot;
        private final ArrayList<Tile> tiles;

        Chests(int level, double xp, int respawnTime, ArrayList<Loot> loot, ArrayList<Tile> tiles) {
            this.level = level;
            this.xp = xp;
            this.respawnTime = respawnTime;
            this.loot = loot;
            this.tiles = tiles;
        }

        public static Chests forTile(Tile tile) {
            for(Chests c : Chests.values()) {
                for(Tile t : c.tiles) {
                    if(t.equals(tile)) {
                        return c;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        int[] chests = new int[] {11735, 11736, 11738};
        for(int chestId : chests) {
            if(chestId == obj.getId()) {
                int op = player.getAttribOr(AttributeKey.INTERACTION_OPTION, -1);
                Chests chest = Chests.forTile(obj.tile());

                if (chest == null) {
                    player.message("This chest cannot be opened.");
                    return true;
                }

                if (op == 1) {
                    player.lockDamageOk();
                    player.message("You begin to open the chest...");
                    Chain.bound(player).runFn(1, () -> player.message("You trigger a trap!")).then(1, () -> {
                        player.hit(player,Utils.random(2, 3));
                        player.unlock();
                    });
                } else if (op == 2) {
                    // Check level requirement
                    if (player.getSkills().levels()[Skills.THIEVING] < chest.level) {
                        player.message("You need a Thieving level of " + chest.level + " to pickpocket the chest.");
                        return true;
                    }

                    player.lockDamageOk();
                    player.message("You begin to open the chest...");
                    Chain.bound(player).runFn(1, () -> {
                        if (Utils.rollDie(100, 85)) {
                            player.message("You successfully disarm the trap.");
                            Chain.bound(player).runFn(1, () -> {
                                int lootidx = Utils.random(chest.loot.size() - 1);
                                Loot lootval = chest.loot.get(lootidx);
                                Item loot = new Item(lootval.id, Utils.random(lootval.min, lootval.max));
                                player.inventory().addOrDrop(new Item(loot));
                                player.message("You steal %d x %s.", loot.getAmount(), loot.unnote().name());
                                player.getSkills().addXp(Skills.THIEVING, chest.xp);
                                player.unlock();
                            });
                        } else {
                            player.message("You trigger a trap!");
                            Chain.bound(player).runFn(1, () -> {
                                player.hit(player,Utils.random(2, 3));
                                if (Utils.rollDie(100, 15)) {
                                    player.poison(2);
                                }
                                player.unlock();
                            });
                        }
                    });
                }
                return true;
            }
        }
        return false;
    }

}
