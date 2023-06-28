package com.aelous.model.content.items.combine.crystal;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;

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
                send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.CRYSTAL_BODY), "", "You create a crystal body.");
            }).then(1, () -> player.unlock());
        }
    }
}
