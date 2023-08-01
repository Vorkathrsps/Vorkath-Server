package com.cryptic.model.content.items.combine.crystal;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

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
