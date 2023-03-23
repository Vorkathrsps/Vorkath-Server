package com.aelous.model.content.items;

import com.aelous.model.World;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | April, 02, 2021, 11:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class BagFullOfGems extends PacketInteraction {

    private static final int NOTED_UNCUT_SAPPHIRE = UNCUT_SAPPHIRE + 1;
    private static final int NOTED_UNCUT_EMERALD = UNCUT_EMERALD + 1;
    private static final int NOTED_UNCUT_RUBY = UNCUT_RUBY + 1;
    private static final int NOTED_UNCUT_DIAMOND = UNCUT_DIAMOND + 1;
    private static final int NOTED_UNCUT_DRAGONSTONE = UNCUT_DRAGONSTONE + 1;
    private static final int NOTED_UNCUT_ONYX = UNCUT_ONYX + 1;
    private static final int NOTED_UNCUT_ZENYTE = UNCUT_ZENYTE + 1;

    private static final LootTable chanceTable = new LootTable().addTable(1,
        new LootItem(NOTED_UNCUT_SAPPHIRE, 40, 2599),
        new LootItem(NOTED_UNCUT_EMERALD, 40, 1500),
        new LootItem(NOTED_UNCUT_RUBY, 40, 560),
        new LootItem(NOTED_UNCUT_DIAMOND, 40, 230),
        new LootItem(NOTED_UNCUT_DRAGONSTONE, 40, 110),
        new LootItem(NOTED_UNCUT_ONYX, 1, 25),
        new LootItem(NOTED_UNCUT_ZENYTE, 1, 1)
    );

    private void open(Player player, Item item) {
        if (freeSlots(player) < 5) {
            player.message("You will need up to 5 free inventory spaces to open the bag.");
            return;
        }

        Item uncut = chanceTable.rollItem();

        player.inventory().add(uncut);

        if(uncut.getId() == NOTED_UNCUT_ZENYTE) {
            World.getWorld().sendWorldMessage("<img=2013> " + player.getUsername() + " just received <col=" + Color.BLUE.getColorValue() + "> an uncut Zenyte</col> from the gem bag!");
        }
        player.message("You open the bag and find 40 uncut gems.");

        player.inventory().remove(item,true);
    }

    private int freeSlots(Player player) {
        int freeSlots = player.inventory().getFreeSlots();
        if (player.inventory().contains(NOTED_UNCUT_SAPPHIRE))
            freeSlots++;
        if (player.inventory().contains(NOTED_UNCUT_EMERALD))
            freeSlots++;
        if (player.inventory().contains(NOTED_UNCUT_RUBY))
            freeSlots++;
        if (player.inventory().contains(NOTED_UNCUT_DIAMOND))
            freeSlots++;
        if (player.inventory().contains(NOTED_UNCUT_DRAGONSTONE))
            freeSlots++;
        return freeSlots;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == BAG_FULL_OF_GEMS) {
                open(player, item);
                return true;
            }
        }
        if (option == 2) {
            if (item.getId() == BAG_FULL_OF_GEMS) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                        setPhase(0);
                    }

                    @Override
                    protected void select(int option) {
                        if(isPhase(0)) {
                            if(option == 1) {
                                if(!player.inventory().contains(BAG_FULL_OF_GEMS)) {
                                    stop();
                                    return;
                                }

                                player.inventory().remove(item);
                                for (Item i : player.getLootingBag().getItems()) {
                                    if (i != null)
                                        player.getLootingBag().remove(i);
                                }
                                stop();
                            }
                            if(option == 2) {
                                stop();
                            }
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }


}
