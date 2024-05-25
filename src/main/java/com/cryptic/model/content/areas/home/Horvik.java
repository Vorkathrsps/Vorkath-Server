package com.cryptic.model.content.areas.home;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.cache.definitions.identifiers.NumberUtils;

import static com.cryptic.utility.ItemIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.HORVIK;

/**
 * @author Origin | April, 23, 2021, 13:34
 * 
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
