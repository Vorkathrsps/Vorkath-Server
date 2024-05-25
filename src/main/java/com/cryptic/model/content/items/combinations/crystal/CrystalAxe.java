package com.cryptic.model.content.items.combinations.crystal;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class CrystalAxe extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.inventory().containsAll(ItemIdentifiers.DRAGON_AXE, ItemIdentifiers.CRYSTAL_TOOL_SEED) && player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_SHARD, 120))) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.DRAGON_AXE, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_TOOL_SEED, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_SHARD, 120);
                player.inventory().add(new Item(ItemIdentifiers.CRYSTAL_AXE, 1));
            }).then(1, () -> {
                sendItemStatement(new Item(ItemIdentifiers.CRYSTAL_AXE), "", "You create a crystal axe.");
            }).then(1, () -> player.unlock());
        }
    }
}
