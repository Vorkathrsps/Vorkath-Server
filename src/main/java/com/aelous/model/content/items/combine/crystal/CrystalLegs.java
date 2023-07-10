package com.aelous.model.content.items.combine.crystal;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;

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
                send(DialogueType.ITEM_STATEMENT, new Item(ItemIdentifiers.CRYSTAL_LEGS), "", "You create a pair of crystal legs.");
            }).then(1, () -> player.unlock());
        }
    }
}
