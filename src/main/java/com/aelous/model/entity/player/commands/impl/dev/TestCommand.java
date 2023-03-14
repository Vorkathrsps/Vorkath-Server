
package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.lootkeys.LootKey;
import com.aelous.model.items.Item;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.aelous.model.entity.attributes.AttributeKey.LOST_ITEMS_ON_DEATH;
import static com.aelous.utility.ItemIdentifiers.ARMADYL_GODSWORD;
import static com.aelous.utility.ItemIdentifiers.*;

public class TestCommand implements Command {

    @Override//debug mode? its in debug yh
    public void execute(Player player, String command, String[] parts) {
        Optional<Player> test = World.getWorld().getPlayerByName("Origin6");
        List<Item> items = Arrays.asList(new Item(ARMADYL_GODSWORD), new Item(SHARK, 10), new Item(ELYSIAN_SPIRIT_SHIELD), new Item(DRAGON_CLAWS), new Item(SUPER_COMBAT_POTION4), new Item(SARADOMIN_BREW4, 3), new Item(SUPER_RESTORE4, 2),
            new Item(ANCESTRAL_HAT), new Item(IMBUED_SARADOMIN_CAPE), new Item(DHAROKS_HELM), new Item(VERACS_PLATESKIRT), new Item(TORAGS_HELM), new Item(VIGGORAS_CHAINMACE), new Item(DRAGONFIRE_SHIELD),
            new Item(SPECTRAL_SIGIL), new Item(ANCESTRAL_ROBE_TOP), new Item(ANCESTRAL_ROBE_BOTTOM), new Item(ELDER_MAUL), new Item(AMULET_OF_TORTURE), new Item(NECKLACE_OF_ANGUISH), new Item(OCCULT_NECKLACE),
            new Item(TOXIC_BLOWPIPE), new Item(SERPENTINE_HELM), new Item(TOXIC_STAFF_OF_THE_DEAD), new Item(TUMEKENS_SHADOW), new Item(MASORI_BODY), new Item(MASORI_CHAPS), new Item(MASORI_MASK));

        LinkedList<Item> list = new LinkedList<>(items);

        test.ifPresent(t -> t.putAttrib(LOST_ITEMS_ON_DEATH, list));
        test.ifPresent(dead -> LootKey.handleDeath(dead, player));
    }

    @Override
    public boolean canUse(Player player) { //kkl
        return (player.getPlayerRights().isDeveloper(player));
    }

}
