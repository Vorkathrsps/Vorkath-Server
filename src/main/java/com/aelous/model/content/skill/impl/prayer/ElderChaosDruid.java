package com.aelous.model.content.skill.impl.prayer;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.ELDER_CHAOS_DRUID_7995;

public class ElderChaosDruid extends PacketInteraction {

    private void swap(Player player, int original, int result) {
        var count = player.inventory().count(original);
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Un-Note 1", "Un-Note 5", "Un-Note 10", "Un-Note All");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        if (count <= 0) {
                            stop();
                            return;
                        }
                        unnote(player, 1, original, result);
                    }
                    if (option == 2) {
                        if (count <= 0) {
                            stop();
                            return;
                        }
                        unnote(player, 5, original, result);
                    }
                    if (option == 3) {
                        if (count <= 0) {
                            stop();
                            return;
                        }
                        unnote(player, 10, original, result);
                    }
                    if (option == 4) {
                        if (count <= 0) {
                            stop();
                            return;
                        }
                        unnote(player, count, original, result);
                    }
                }
            }
        });
    }

    private void unnote(Player player, int amount, int original, int result) {
        var toRemove = amount;

        if (player.inventory().count(original) < toRemove) {
            toRemove = player.inventory().count(original);
        }

        if (toRemove == 0) {
            player.message("You do not have any bones.");
            return;
        }
        if (player.inventory().getFreeSlots() == 0) {
            player.message("You have no room in in your inventory to do this.");
            return;
        }
        if (player.inventory().getFreeSlots() < toRemove) {
            toRemove = player.inventory().getFreeSlots();
        }
        var currency = 13307;
        var name = "blood money";

        if (!player.inventory().contains(new Item(currency, toRemove * 50))) {
            DialogueManager.npcChat(player, Expression.VERY_SAD, ELDER_CHAOS_DRUID_7995, "Unfortunately, you don't have enough " + name + " right now to do that.");
        } else {
            player.inventory().remove(new Item(currency, toRemove * 50));
            player.inventory().remove(new Item(original, toRemove));
            player.inventory().add(new Item(result, toRemove));
            player.itemBox("The Druid converts your banknotes to items.", original);
        }
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == ELDER_CHAOS_DRUID_7995) {
            var name = "blood money";
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.NPC_STATEMENT, ELDER_CHAOS_DRUID_7995, Expression.CALM_TALK, "Hello. I can convert items to banknotes, for 50 " + name + "", "per item. Just hand me the items you'd like me to", "convert.");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Who are you?", "Thanks");
                        setPhase(1);
                    } else if (isPhase(2)) {
                        send(DialogueType.NPC_STATEMENT, ELDER_CHAOS_DRUID_7995, Expression.CALM_TALK, "I'm just a druid, My real name is Yaz. I'm lazy but", "I make money converting your noted bones", "to unnoted bones.");
                        setPhase(3);
                    } else if (isPhase(3)) {
                        send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Thanks");
                        setPhase(4);
                    } else if (isPhase(4)) {
                        stop();
                    }
                }

                @Override
                protected void select(int option) {
                    if (isPhase(1)) {
                        if (option == 1) {
                            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Who are you?");
                            setPhase(2);
                        }
                        if (option == 2) {
                            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Thanks");
                            setPhase(4);
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnNpc(Player player, Item item, NPC npc) {
        if (npc.id() == ELDER_CHAOS_DRUID_7995) {
            if (new Item(item).noted() && new Item(item).unnote().definition(World.getWorld()).name.toLowerCase().endsWith("bones")) {
                swap(player, item.getId(), new Item(item).unnote().getId());
            } else {
                DialogueManager.npcChat(player, Expression.VERY_SAD, ELDER_CHAOS_DRUID_7995, "Sorry, I wasn't expecting anyone to want to convert", "that sort of item, so I haven't any banknotes for it.");
            }
            return true;
        }
        return false;
    }
}
