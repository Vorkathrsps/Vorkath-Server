package com.aelous.model.content.skill.impl.thieving;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.aelous.utility.ItemIdentifiers.COINS_995;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.WALL_SAFE;

/**
 * @author Patrick van Elderen | March, 26, 2021, 10:56
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WallSafe extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            if(object.getId() == WALL_SAFE) {
                attempt(player, object);
                return true;
            }
        }
        return false;
    }

    private static final LootTable table = new LootTable().addTable(1,
        new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 50 : 5000, GameServer.properties().pvpMode ? 200 : 20000, 295),
        new LootItem(1623, 1, 100),     //sapphire 1/5
        new LootItem(1621, 1, 50),      //emerald 1/10
        new LootItem(1619, 1, 33),      //ruby 1/15
        new LootItem(1617, 1, 16),      //diamond 1/30
        new LootItem(1631, 1, 5),       //dragonstone 1/100
        new LootItem(6571, 1, 1)        //onyx 1/500
    );

    private void attempt(Player player, GameObject wallSafe) {
        if (!player.getSkills().check(Skills.THIEVING, 50, "attempt this"))
            return;
        if (player.inventory().isFull()) {
            player.message("You don't have enough inventory space to do that.");
            return;
        }
        Chain.bound(player).runFn(1, () -> {
            player.message("You start cracking the safe.");
            player.animate(2247);
        }).then(2, () -> {
            double chance = 0.5 + (double) (player.getSkills().level(Skills.THIEVING) - 50) * 0.01;
            if (Utils.get() > Math.min(chance, 0.85)) {
                player.lock();
                player.message("You slip and trigger a trap!");
                player.hit(player, World.getWorld().random(1, 6));
                player.animate(1113);
                Chain.bound(player).runFn(2, () -> {
                    player.resetAnimation();
                    player.unlock();
                });
            } else {
                player.animate(2248);
                Chain.bound(player).runFn(2, () -> {
                    player.message("You get some loot.");
                    player.getSkills().addXp(Skills.THIEVING,70,true);
                    player.inventory().add(getLoot(player));
                    openSafe(wallSafe);
                });
            }
        });
    }

    private void openSafe(GameObject wallSafe) {
        ObjectManager.replace(wallSafe, new GameObject(7238, wallSafe.tile()),3);
    }

    private Item getLoot(Player player) {
        Item item = table.rollItem();
        if (item.getId() == BLOOD_MONEY) {
            item.setAmount((int) (item.getAmount() * ((1 + (player.getSkills().xpLevel(Skills.THIEVING) - 49) * 0.02))));
        }
        return item;
    }
}
