package com.aelous.model.content.items.combine.crystal;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;

public class CrystalPickaxe extends Dialogue {
    @Override
    protected void start(Object... parameters) {
        if (player.inventory().containsAll(ItemIdentifiers.DRAGON_PICKAXE, ItemIdentifiers.CRYSTAL_TOOL_SEED) && player.getInventory().contains(new Item(ItemIdentifiers.CRYSTAL_SHARD, 120))) {
            Chain.noCtx().runFn(1, () -> {
                player.lockMovement();
                player.inventory().remove(ItemIdentifiers.DRAGON_PICKAXE, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_TOOL_SEED, 1);
                player.inventory().remove(ItemIdentifiers.CRYSTAL_SHARD, 120);
                player.inventory().add(new Item(ItemIdentifiers.CRYSTAL_PICKAXE, 1));
            }).then(1, () -> {
                send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.CRYSTAL_PICKAXE), "", "You create a crystal pickaxe.");
            }).then(1, () -> player.unlock());
        }
    }
}
