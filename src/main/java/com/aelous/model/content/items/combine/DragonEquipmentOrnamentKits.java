package com.aelous.model.content.items.combine;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick van Elderen | March, 16, 2021, 14:14
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DragonEquipmentOrnamentKits extends PacketInteraction {

    // raw items
    private final static int SQSHIELD = 1187;
    private final static int dfull = 11335;
    private final static int dchain = 3140;
    private final static int legs = 4087;
    private final static int skirt = 4585;

    // 4 kits
    private final static int dsq_kit = 12532;
    private final static int dchain_kit = 12534;
    private final static int legskirt_kit = 12536;
    private final static int dfull_kit = 12538;

    // 5 trimmed items (legs and skirt share a kit)
    private final static int dchain_g = 12414;
    private final static int dlegs_g = 12415;
    private final static int dskirt_g = 12416;
    private final static int dfullhelm_g = 12417;
    private final static int dsq_gold = 12418;

    private final static List<Integer> raw = Arrays.asList(SQSHIELD, dfull, dchain, legs, skirt);
    private final static List<Integer> kits = Arrays.asList(dsq_kit, dfull_kit, dchain_kit, legskirt_kit, legskirt_kit);
    private final static List<Integer> results = Arrays.asList(dsq_gold, dfullhelm_g, dchain_g, dlegs_g, dskirt_g);

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        for (int i = 0; i < raw.size(); i++) {
            if ((use.getId() == raw.get(i) || usedWith.getId() == raw.get(i)) && (use.getId() == kits.get(i) || usedWith.getId() == kits.get(i))) {
                if (player.inventory().containsAll(raw.get(i), kits.get(i))) {
                    if (player.inventory().remove(new Item(raw.get(i)), true) && player.inventory().remove(new Item(kits.get(i)), true)) {
                        player.inventory().add(new Item(results.get(i)), true);
                        String name = new Item(raw.get(i)).name();
                        int result = i;
                        player.getDialogueManager().start(new Dialogue() {
                            @Override
                            protected void start(Object... parameters) {
                                send(DialogueType.ITEM_STATEMENT, results.get(result), "", "You combine the ornament kit with the "+name+".");
                                setPhase(0);
                            }

                            @Override
                            protected void next() {
                                if(isPhase(0)) {
                                    stop();
                                }
                            }
                        });
                    }
                }
                return true;
            }
        }
        return false;
    }
}
