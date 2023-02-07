package com.aelous.model.items.tradingpost;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;

public class TradingPostConfirmDialogue extends Dialogue {

    private final Item sale;
    private final long price;

    public TradingPostConfirmDialogue(Item sale, long price) {
        this.sale = sale;
        this.price = price;
    }

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Submit sell offer: "+sale.getAmount()+"x "+sale.unnote().name()+" for <col=ff0000>"+Utils.formatRunescapeStyle(price)+" Blood money "+(sale.getAmount() > 1 ? "each" : "")+"</col>.", "Nevermind");
    }

    @Override
    public void next() {

    }

    @Override
    public void select(int option) {
        if (getPhase() == 0) {
            if (option == 1) {
                //Safety, people could dupe without this check. Using reflection.
                if(!player.inventory().contains(sale)) {
                    stop();
                    return;
                }
                TradingPost.listSale(player, sale, price);
            }
            stop();
        }
    }
}
