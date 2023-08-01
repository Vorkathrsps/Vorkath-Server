package com.cryptic.model.content.items.combine.crystal;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class CrystalHarpoon extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.inventory().containsAll(ItemIdentifiers.DRAGON_HARPOON, ItemIdentifiers.CRYSTAL_TOOL_SEED) && player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_SHARD, 120))) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.DRAGON_HARPOON, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_TOOL_SEED, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_SHARD, 120);
                player.inventory().add(new Item(ItemIdentifiers.CRYSTAL_HARPOON, 1));
            }).then(1, () -> {
                send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.CRYSTAL_HARPOON), "", "You create a crystal harpoon.");
            }).then(1, () -> player.unlock());
        }
    }
}
