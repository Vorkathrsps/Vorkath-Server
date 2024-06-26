package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameEngine;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.inventory.Inventory;
import com.cryptic.model.map.position.Tile;
import com.cryptic.services.database.donation.DonationRecord;
import com.cryptic.utility.Color;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClaimDonationCommand implements Command {

    public static int totalDonated = 0;


    @Override
    public void execute(Player player, String command, String[] parts) {

    }

    public static void spawnDonatorBoss() {
        World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>The Donator boss 'Xamphur' has spawned! Gear up for the ultimate challenge! ::xamphur </shad></img>"));
        NPC xamphur = new NPC(10951, new Tile(3333, 3333, 0));
        xamphur.spawn(false);
    }

    private static void checkDonatorBoss() {
        if (totalDonated >= 150) {
            World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>Only $50 Left until The Donator boss 'Xamphur' will spawn!</shad></img>"));
        } else if (totalDonated >= 100) {
            World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>Only $100 Left until The Donator boss 'Xamphur' will spawn!</shad></img>"));
        } else if (totalDonated >= 50) {
            World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>Only $200 Left until The Donator boss 'Xamphur' will spawn!</shad></img>"));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
