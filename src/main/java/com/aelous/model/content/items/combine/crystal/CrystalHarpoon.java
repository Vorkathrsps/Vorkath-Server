package com.aelous.model.content.items.combine.crystal;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;

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
