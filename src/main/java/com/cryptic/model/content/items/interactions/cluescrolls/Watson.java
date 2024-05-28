package com.cryptic.model.content.items.interactions.cluescrolls;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.WATSON;
import static com.cryptic.utility.ItemIdentifiers.*;

public class Watson extends PacketInteraction {

    public static final int[] CLUES = new int[]{ItemIdentifiers.CLUE_SCROLL_BEGINNER, ItemIdentifiers.CLUE_SCROLL_EASY, ItemIdentifiers.CLUE_SCROLL_MEDIUM, ItemIdentifiers.CLUE_SCROLL_HARD, ItemIdentifiers.CLUE_SCROLL_ELITE};

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (option == 1) {
            if (npc.id() == NpcIdentifiers.WATSON) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendPlayerChat(Expression.NODDING_ONE, "Who are you?");
                        setPhase(0);
                    }
                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            sendNpcChat(WATSON, Expression.CALM_TALK, "I am Watson, The master of clue scrolls.", "I have lived a long life here in Kourend.", "The lands are beautiful here.");
                            setPhase(1);
                            return;
                        }

                        if (isPhase(1)) {
                            sendPlayerChat(Expression.HAPPY, "So what are master clue scrolls anyway?");
                            setPhase(2);
                            return;
                        }

                        if (isPhase(2)) {
                            sendNpcChat(WATSON, Expression.CALM_TALK, "That is for you to find out, " + player.getUsername() + ".");
                            setPhase(3);
                            return;
                        }

                        if (isPhase(3)) {
                            if (!player.getInventory().containsAll(CLUES)) {
                                List<String> invalid = getStrings(player);
                                sendNpcChat(WATSON, Expression.CALM_TALK, "You are not worthy, " + player.getUsername() + "...");
                                sendMessage(player, invalid);
                                stop();
                            } else {
                                for (var clue : CLUES) player.getInventory().remove(clue, 1);
                                player.getInventory().add(new Item(CLUE_SCROLL_MASTER, 1));
                            }
                        }
                    }

                });
                return true;
            }
        }
        return false;
    }

    private static void sendMessage(Player player, List<String> invalid) {
        player.message("<lsprite=13><shad=0>" + Color.RED.wrap(" You do not have all the required clue scrolls to exchange.") + "</shad></img>");
        StringBuilder builder = buildString(invalid);
        player.message(Color.RED.wrap("<lsprite=13><shad=0>" + builder + "</shad></img>"));
    }

    @NotNull
    private static List<String> getStrings(Player player) {
        List<String> invalid = new ArrayList<>();
        for (var clue : CLUES) {
            if (!player.getInventory().contains(clue)) {
                ItemDefinition definition = ItemDefinition.cached.get(clue);
                invalid.add(definition.name);
            }
        }
        return invalid;
    }

    @NotNull
    private static StringBuilder buildString(List<String> invalid) {
        StringBuilder builder = new StringBuilder();
        builder.append(" Missing: ");
        for (var clue : invalid) {
            clue = clue.replaceAll("Clue scroll", "");
            clue = clue.replaceAll("\\(", "");
            clue = clue.replaceAll("\\)", "");
            builder.append(clue).append(",");
        }
        return builder;
    }
}
