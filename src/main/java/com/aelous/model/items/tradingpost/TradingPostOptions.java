package com.aelous.model.items.tradingpost;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;

import java.util.List;

import static com.aelous.model.items.tradingpost.TradingPost.getSalesByUsername;

public class TradingPostOptions extends Dialogue {

    private final int index;

    public TradingPostOptions(int index) {
        this.index = index;
    }

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Claim Sale", /*"Edit Quantity", "Edit Price",*/ "Cancel Listing", "Never mind");
        setPhase(0);
    }

    @Override
    public void next() {

    }

    @Override
    public void select(int option) {
        if (getPhase() == 0) {
            //Safety, people could dupe without this check. Using reflection.
            List<TradingPostListing> list = getSalesByUsername(player.getUsername().toLowerCase());
            int size = list.size();
            if (index > size) {
                player.message("<col=ff0000>No offers found to claim..");
                stop();
                return;
            }
            stop();//Close dialogue first before opening another.
            TradingPost.modifyListing(player, index, option);
        }
    }


}
