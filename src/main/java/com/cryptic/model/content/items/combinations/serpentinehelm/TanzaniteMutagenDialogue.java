package com.cryptic.model.content.items.combinations.serpentinehelm;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class TanzaniteMutagenDialogue extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.inventory().containsAll(ItemIdentifiers.SERPENTINE_HELM, ItemIdentifiers.TANZANITE_MUTAGEN)) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.TANZANITE_MUTAGEN, 1);
                player.inventory().remove(ItemIdentifiers.SERPENTINE_HELM, 1);
                player.inventory().add(new Item(ItemIdentifiers.TANZANITE_HELM, 1));
            }).then(1, () -> {
                sendItemStatement(new Item(ItemIdentifiers.TANZANITE_HELM), "", "You create a Tanzanite helm.");
            }).then(1, () -> player.unlock());
        }
    }
}
