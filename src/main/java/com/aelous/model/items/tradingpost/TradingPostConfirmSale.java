package com.aelous.model.items.tradingpost;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.utility.Utils;

import static com.aelous.model.items.tradingpost.TradingPost.getListings;
import static com.aelous.model.items.tradingpost.TradingPost.sales;

public class TradingPostConfirmSale extends Dialogue {

    private final TradingPostListing selected;

    private final int amount;
    private final long price;

    public TradingPostConfirmSale(int amount, long price, TradingPostListing selected) {
        super();
        this.selected = selected;
        this.amount = amount;
        this.price = price;
    }

    @Override
    protected void start(Object... parameters) {
        this.send(DialogueType.OPTION, "Buy offer?",
            ("Yes, purchase <col=ff0000>" + amount + "x " + selected.getSaleItem().unnote().name() + "</col> for (<col=ff0000>" + Utils.formatRunescapeStyle(price) + " Blood Money</col>)"),
            "No, I don't want to buy this.");
        setPhase(0);
    }

    @Override
    public void select(int option) {
       // System.out.println(getPhase());
        if (isPhase(0)) {
            if (option == 1) {
                send(DialogueType.OPTION, "Would you like to receive your " + selected.getSaleItem().unnote().name() + " noted?", "Yes.", "No.");
                setPhase(1);
            }
            if (option == 2) {
                stop();
            }
        } else if (isPhase(1)) {
            String seller = selected.getSellerName().toLowerCase();
            PlayerListing sellerListing = sales.getOrDefault(seller, getListings(seller));

            if(option == 1) {
                //Safety, people could dupe without this check. Using reflection.
                if (!sellerListing.getListedItems().contains(selected) || selected.getRemaining() == 0) {
                    player.message("<col=ff0000>This offer no longer exists.");
                    stop();
                    return;
                }

                TradingPost.finishPurchase(player, selected, price, amount,true);
                stop();
            }

            if(option == 2) {
                //Safety, people could dupe without this check. Using reflection.
                if (!sellerListing.getListedItems().contains(selected) || selected.getRemaining() == 0) {
                    player.message("<col=ff0000>This offer no longer exists.");
                    stop();
                    return;
                }

                TradingPost.finishPurchase(player, selected, price, amount,false);
                stop();
            }
        }
    }
}
