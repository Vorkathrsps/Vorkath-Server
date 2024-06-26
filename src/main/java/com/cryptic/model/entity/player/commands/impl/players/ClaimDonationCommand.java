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
        World.getWorld().sendWorldMessage("<lsprite=993>" + Color.MITHRIL.wrap("<shad=0>The Donator boss 'Xamphur' has spawned! Gear up for the ultimate challenge! ::xamphur </shad></lsprite>"));
        NPC xamphur = new NPC(10951, new Tile(3333, 3333, 0));
        xamphur.spawn(false);
    }

    private static void checkDonatorBoss() {
        if (totalDonated >= 150) {
            World.getWorld().sendWorldMessage("<lsprite=993>" + Color.MITHRIL.wrap("<shad=0>Only $50 Left until The Donator boss 'Xamphur' will spawn!</shad></lsprite>"));
        }
        if (totalDonated >= 100) {
            World.getWorld().sendWorldMessage("<lsprite=993>" + Color.MITHRIL.wrap("<shad=0>Only $100 Left until The Donator boss 'Xamphur' will spawn!</shad></lsprite>"));
        }
        if (totalDonated >= 50) {
            World.getWorld().sendWorldMessage("<lsprite=993>" + Color.MITHRIL.wrap("<shad=0>Only $200 Left until The Donator boss 'Xamphur' will spawn!</shad></lsprite>"));
        } else if (totalDonated >= 100) {
            World.getWorld().sendWorldMessage("<lsprite=993>" + Color.MITHRIL.wrap("<shad=0>Only $100 Left until The Donator boss 'Xamphur' will spawn!</shad></lsprite>"));
        } else if (totalDonated >= 50) {
            World.getWorld().sendWorldMessage("<lsprite=993>" + Color.MITHRIL.wrap("<shad=0>Only $200 Left until The Donator boss 'Xamphur' will spawn!</shad></lsprite>"));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
