package com.cryptic.model.content.items.combinations.crystal;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class CrystalHelm extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_ARMOUR_SEED, 1)) && player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_SHARD, 50))) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.CRYSTAL_ARMOUR_SEED, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_SHARD, 50);
                player.inventory().add(new Item(ItemIdentifiers.CRYSTAL_HELM, 1));
            }).then(1, () -> {
                send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.CRYSTAL_HELM), "", "You create a crystal helmet.");
            }).then(1, () -> player.unlock());
        }
    }
}
