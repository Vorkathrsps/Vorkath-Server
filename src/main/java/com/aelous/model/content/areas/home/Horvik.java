package com.aelous.model.content.areas.home;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NumberUtils;

import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.HORVIK;

/**
 * @author Patrick van Elderen | April, 23, 2021, 13:34
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Horvik extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == HORVIK) {
                player.message("I don't talk, I only convert Zamorakian spears!");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnNpc(Player player, Item item, NPC npc) {
        /*
         * Zamorakian spear/hasta conversion
         */
        if (item.getId() == ZAMORAKIAN_SPEAR || item.getId() == ZAMORAKIAN_HASTA && npc.id() == HORVIK) {
            convertZamorakianWeapon(player, item);
            return true;
        }
        return false;
    }

    private void convertZamorakianWeapon(Player player, Item item) {
        int currencyId;
        String currencyName;
        int price;
        currencyId = BLOOD_MONEY;
        currencyName = "bm";
        price = bmPrice(player);
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, item, "", "Convert your " + item.name() + " for " + NumberUtils.formatNumber(price) + " " + currencyName + "?");
                setPhase(0);
            }

            @Override
            protected void next() {
                if(isPhase(0)) {
                    send(DialogueType.OPTION, "Are you sure you want to do this?", "Yes", "No");
                    setPhase(1);
                } else if(isPhase(2)) {
                    stop();
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if (!player.inventory().contains(currencyId) || player.inventory().count(currencyId) < price) {
                            player.message("You don't have enough " + currencyName + " for me to upgrade that.");
                            setPhase(2);
                            return;
                        }
                        if (!player.inventory().contains(item)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(currencyId, price));
                        player.inventory().remove(item);
                        player.inventory().add(new Item(item.getId() == 11824 ? 11889 : 11824));
                        stop();
                    }
                    if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private int bmPrice(Player player) {
        double smithingLevel = player.getSkills().level(Skills.SMITHING);
        double smithingMultiplier = 1D - (smithingLevel / 200D);
        return (int) (smithingMultiplier * 25000);
    }
}
