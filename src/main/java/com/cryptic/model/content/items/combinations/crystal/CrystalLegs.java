package com.cryptic.model.content.items.combinations.crystal;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class CrystalLegs extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_ARMOUR_SEED, 2)) && player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_SHARD, 100))) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.CRYSTAL_ARMOUR_SEED, 2);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_SHARD, 100);
                player.inventory().add(new Item(ItemIdentifiers.CRYSTAL_LEGS, 1));
            }).then(1, () -> {
                sendItemStatement(new Item(ItemIdentifiers.CRYSTAL_LEGS), "", "You create a pair of crystal legs.");
            }).then(1, () -> player.unlock());
        }
    }
}
