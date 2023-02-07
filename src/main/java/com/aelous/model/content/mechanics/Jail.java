package com.aelous.model.content.mechanics;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.BLURITE_ORE;
import static com.aelous.utility.ItemIdentifiers.BRONZE_PICKAXE;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.IRENA;

/**
 * Created by Jak on 19/01/2016. #Gundrilla
 */
public class Jail extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == IRENA) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Pickaxe", "Give ores");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if(isPhase(1)) {
                            stop();
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if(isPhase(0)) {
                            if(option == 1) {
                                // Make sure people can mine their way out.
                                if (!player.inventory().contains(BRONZE_PICKAXE)) {
                                    player.inventory().add(new Item(BRONZE_PICKAXE,1),true);
                                    send(DialogueType.ITEM_STATEMENT, new Item(BRONZE_PICKAXE), "", "Irena hands you a pickaxe.");// bronze axe, not a freaking rune pickaxe on eco LOL
                                } else {
                                    send(DialogueType.NPC_STATEMENT, IRENA, Expression.HAPPY, "You've got a pickaxe on you!");
                                }
                                setPhase(1);
                            }
                            if (option == 2) {
                                // Give ores to the NPC. Remove from inv and add to total ores mined.
                                offerOres(player);
                            }
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnNpc(Player player, Item item, NPC npc) {
        if(npc.id() == IRENA && item.getId() == BLURITE_ORE) {
            offerOres(player);
            return true;
        }
        return false;
    }

    private void offerOres(Player player) {
        player.putAttrib(AttributeKey.JAIL_ORES_MINED, player.<Integer>getAttribOr(AttributeKey.JAIL_ORES_MINED, 0) + Math.max(0, player.inventory().count(BLURITE_ORE)));
        var amountOfOresMined = player.<Integer>getAttribOr(AttributeKey.JAIL_ORES_MINED, 0);

        if (amountOfOresMined >= player.<Integer>getAttribOr(AttributeKey.JAIL_ORES_TO_ESCAPE, 0)) {
            // You're free!
            player.itemBox("You can now leave! Don't break the rules ye? Sick.", 668);
            player.putAttrib(AttributeKey.JAILED, 0);
            player.putAttrib(AttributeKey.JAIL_ORES_MINED, 0);
        } else {
            // Tell the player how many more ores they are required to mine to exit.
            player.inventory().remove(new Item(BLURITE_ORE, amountOfOresMined), true);
            player.itemBox("You try giving her ores. " + Math.max(0, player.<Integer>getAttribOr(AttributeKey.JAIL_ORES_TO_ESCAPE, 0) - amountOfOresMined) + " left...", BLURITE_ORE);
        }
    }
}
