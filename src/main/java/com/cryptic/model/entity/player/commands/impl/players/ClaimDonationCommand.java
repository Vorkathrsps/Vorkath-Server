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
import com.teamgames.endpoints.store.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClaimDonationCommand implements Command {

    public static int totalDonated = 0;

    final ListenableFuture<List<DonationRecord>> getResponse(final Transaction transaction, final List<DonationRecord> donationRecords) {
        return GameEngine.getInstance().submitLowPriority(() -> {
            try {
                final Transaction[] details = transaction.getTransactions();
                if (details.length == 0) {
                    return donationRecords;
                }
                for (var order : details) {
                    if (order.message.contains("There are currently no items to claim.")) break;
                    final DonationRecord rewardRecord = new DonationRecord(order.product_id, order.product_amount, order.amount_purchased, order.product_price, order.message);
                    donationRecords.add(rewardRecord);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return donationRecords;
        });
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        final String name = player.getUsername().toLowerCase();
        final String key = "1xqEgV4QtvySYVIXyXLOm22aNn2TIBoB75ojmAHbvj2slniJbKt7RZVO8vCTFn1rqmcKsCy5";
        final List<DonationRecord> records = new ArrayList<>();
        final Transaction transaction = new Transaction().setApiKey(key).setPlayerName(name);
        final ListenableFuture<List<DonationRecord>> response = getResponse(transaction, records);
        Futures.addCallback(response, new FutureCallback<>() {
            @Override
            public void onSuccess(List<DonationRecord> result) {
                GameEngine.getInstance().addSyncTask(() -> { // there now its game-thread safe, what about the getfuture ? i use a
                   if (result.isEmpty()) {
                        player.message(Color.RED.wrap("You currently don't have any items waiting. You must donate first!"));
                        return;
                    }
                    double rank = player.<Double>getAttribOr(AttributeKey.TOTAL_PAYMENT_AMOUNT, 0D);
                    final Inventory inventory = player.getInventory();
                    for (DonationRecord record : result) {
                        final double productPrice = record.productPrice();
                        final int amountPurchased = record.amountPurchased();
                        final double totalValue = amountPurchased * productPrice;
                        final Item reward = new Item(record.itemId(), record.itemAmount());
                        inventory.addOrBank(reward);
                        rank += totalValue;
                        totalDonated += (int) totalValue;
                        checkDonatorBoss();
                    }
                    if (totalDonated >= 200) {
                        spawnDonatorBoss();
                    }
                    player.putAttrib(AttributeKey.TOTAL_PAYMENT_AMOUNT, rank);
                    player.getMemberRights().update(player, false);
                    player.message(Color.PURPLE.wrap("<img=993><shad=0>Thank you for donating! Your new total donated amount is $" + rank + "</shad></img>"));
                    World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>" + player.getUsername() + " has just donated! Thank them for supporting Valor!</shad></img>"));
                });
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                log.info("Donation API Error - {}", (Object) t.getStackTrace());
            }
        }, MoreExecutors.directExecutor());
    }

    public static void spawnDonatorBoss() {
        World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>The Donator boss 'Xamphur' has spawned! Gear up for the ultimate challenge! ::xamphur </shad></img>"));
        NPC xamphur = new NPC(10951, new Tile(-1, -1, 0));
        xamphur.spawn(false);
        totalDonated = 0;
    }

    private static void checkDonatorBoss() {
        if (totalDonated >= 150) {
            World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>Only $50 Left until The Donator boss 'Xamphur' will spawn!</shad></img>"));
        }
        if (totalDonated >= 100) {
            World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>Only $100 Left until The Donator boss 'Xamphur' will spawn!</shad></img>"));
        }
        if (totalDonated >= 50) {
            World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>Only $200 Left until The Donator boss 'Xamphur' will spawn!</shad></img>"));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
