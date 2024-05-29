package com.cryptic.model.content.items.combinations.crystal;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class CrystalBody extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_ARMOUR_SEED, 3)) && player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_SHARD, 150))) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.CRYSTAL_ARMOUR_SEED, 3);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_SHARD, 150);
                player.inventory().add(new Item(ItemIdentifiers.CRYSTAL_BODY, 1));
            }).then(1, () -> {
                sendItemStatement(new Item(ItemIdentifiers.CRYSTAL_BODY), "", "You create a crystal body.");
            }).then(1, () -> player.unlock());
        }
    }
}
