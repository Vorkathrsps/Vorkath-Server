package com.cryptic.model.content.areas.edgevile;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.DialogueManager;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;

import static com.cryptic.model.cs2.impl.dialogue.util.Expression.HAPPY;
import static com.cryptic.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.cryptic.utility.ItemIdentifiers.COINS_995;

/**
 * @author Origin
 * juni 20, 2020
 */
public class Mac extends PacketInteraction {

    // We're 1 skill short, Construction.
    public static int TOTAL_LEVEL_FOR_MAXED = 2178 + 1; // 22 99's and 1 last level you can't level up (construction)

    //NPC dialogue ID's used during the chat
    private static final int MAC = 6481;
    private static final int MAXCAPE = 13280;
    private static final int MAXHOOD = 13281;

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == MAC) {
                npc.setPositionToFace(player.tile());
                initiate(player);
                return true;
            }
        }

        if(option == 2) {
            if(npc.id() == MAC) {
                World.getWorld().shop(20).open(player);
                return true;
            }
        }
        return false;
    }

    private void initiate(Player player) {
        displayOptions(player);
    }

    private void displayOptions(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendOption(DEFAULT_OPTION_TITLE, "Can I buy a 'Mac's cape'?", "Why are you so dirty?", "Bye.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if(isPhase(1)) {
                    if (!success(player)) {
                        return;
                    }

                    onSuccess(player);
                } else if(isPhase(2)) {
                    sendNpcChat(MAC, HAPPY, "Well, better get to it then.");
                    setPhase(3);
                } else if (isPhase(3)) {
                    stop();
                }
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        sendPlayerChat(HAPPY, "Yes, I'd like to buy a cape.");
                        setPhase(1);
                    }
                    if(option == 2) {
                        sendPlayerChat(HAPPY, "No thanks, I have more training to do.");
                        setPhase(2);
                    }
                    if(option == 3) {
                        sendPlayerChat(HAPPY, "Bye.");
                        setPhase(3);
                    }
                }
            }
        });
    }

    private void onSuccess(Player player) {
        String currency = GameServer.properties().pvpMode ? "bm" : "coins";
        int amount = GameServer.properties().pvpMode ? 50_000 : 50_000_000;
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendNpcChat(MAC, HAPPY, "Hmm... very well. You seem like a suitable adventurer", "to wear my cape. However, this is not a cheap cape.", "To purchase one of my crafted signature capes", "it'll cost ye "+ Utils.formatNumber(amount)+" "+currency+".");
                setPhase(0);
            }

            @Override
            protected void next() {
                if(isPhase(0)) {
                    sendOption("Pay Mac the fee?", "Yes, I understand. Take my BM.", "On second thought, I don't think I can afford that.");
                    setPhase(1);
                } else if(isPhase(2)) {
                    var canAfford = false;
                    int currencyInInventory = player.inventory().count(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995);
                    if (currencyInInventory > 0) {
                        if(currencyInInventory >= amount) {
                            canAfford = true;
                            player.inventory().remove(new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, amount),true);
                        }
                    }

                    if (canAfford) {
                        player.inventory().addOrDrop(new Item(MAXCAPE));
                        player.inventory().addOrDrop(new Item(MAXHOOD));
                        sendItemStatement(new Item(MAXCAPE), new Item(MAXHOOD), "Mac carefully removes a cape and hood from his bag.", "The cape is heavy and made from fine cloth.");
                        setPhase(4);
                    } else {
                        sendNpcChat(MAC, HAPPY, "Sorry, but it appears as if you do not have enough", ""+currency+" to afford this cape.");
                        setPhase(3);
                    }
                } else if (isPhase(3)) {
                    stop();
                } else if (isPhase(4)) {
                    sendNpcChat(MAC, HAPPY, "Here you are. Hold onto it dearly as these capes", "are not easy to make.");
                    setPhase(3);
                }
            }

            @Override
            protected void select(int option) {
                String currency = GameServer.properties().pvpMode ? "bm" : "coins";
                if(isPhase(1)) {
                    if(option == 1) {
                        sendPlayerChat(HAPPY, "Yes, I understand. Take my "+currency+".");
                        setPhase(2);
                    }
                    if(option == 2) {
                        sendPlayerChat(HAPPY, "On second thought, I don't think I can afford that.");
                        setPhase(3);
                    }
                }
            }
        });
    }

    private boolean success(Player player) {
        var counter = 0;
        for (int skillId = 0; skillId < Skills.SKILL_COUNT - 1; skillId++) {
            if (player.getSkills().xpLevel(skillId) < 99) {
                counter++;
            }
        }
        if (counter > 0) {
            var pluralOr = counter == 1 ? "skill" : "skills";
            DialogueManager.npcChat(player, HAPPY, MAC, "Sorry but you require a skill level of 99 in "+counter+" more "+pluralOr+".", "Better get to it.");
            return false;
        }
        return true;
    }

    public static int totalLevel(Player player) {
        return player.getSkills().totalLevel();
    }
}
