package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameEngine;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import com.everythingrs.donate.Donation;

public class ClaimDonationCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        GameEngine.getInstance().addSyncTask(() -> {
            try {
                final String key = "1xqEgV4QtvySYVIXyXLOm22aNn2TIBoB75ojmAHbvj2slniJbKt7RZVO8vCTFn1rqmcKsCy5";
                Donation[] donations = Donation.donations(key, player.getUsername());
                if (donations.length == 0) {
                    player.message("You currently don't have any items waiting. You must donate first!");
                    return;
                }
                if (donations[0].message != null) {
                    player.message(donations[0].message);
                    return;
                }
                double rank = player.<Double>getAttribOr(AttributeKey.TOTAL_PAYMENT_AMOUNT, 0D);
                for (Donation donate : donations) {
                    int id = donate.product_id;
                    int amount = donate.product_amount;
                    double price = donate.amount_purchased * donate.product_price;
                    final Item item = new Item(id, amount);
                    player.getInventory().addOrBank(item);
                    rank += price;
                }
                player.putAttrib(AttributeKey.TOTAL_PAYMENT_AMOUNT, rank);
                player.getMemberRights().update(player, false);
                player.message(Color.PURPLE.wrap("<img=993><shad=0>Thank you for donating! Your new total donated amount is $" + rank + "</shad></img>"));
                World.getWorld().sendWorldMessage("<img=993>" + Color.MITHRIL.wrap("<shad=0>" + player.getUsername() + " has just donated! Thank them for supporting Valor!</shad></img>"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
