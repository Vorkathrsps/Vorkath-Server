package com.aelous.model.content.items.combine;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.Arrays;
import java.util.List;

/**
 * @author Patrick van Elderen | March, 16, 2021, 15:06
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class SteamStaffUpgrade extends PacketInteraction {

    private static final int KIT = 12798;
    private static final int normal_battlestaff = 11787;
    private static final int mystic_battlestaff = 11789;
    private static final int cosmetic_battlestaff = 12795;
    private static final int cosmetic_mysticstaff = 12796;

    private static final List<Integer> raw = Arrays.asList(normal_battlestaff, mystic_battlestaff);
    private static final List<Integer> result = Arrays.asList(cosmetic_battlestaff, cosmetic_mysticstaff);

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        for (int i = 0; i < raw.size(); i++) {
            if ((use.getId() == raw.get(i) || usedWith.getId() == raw.get(i)) && (use.getId() == KIT || usedWith.getId() == KIT)) {
                // Apply upgrade
                String name = new Item(raw.get(i)).name();
                int finalI = i;
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(KIT), new Item(raw.get(finalI)), "", "Do you want to apply this upgrade to your " + name + ".");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes, apply the upgrade.", "Never mind.");
                            setPhase(1);
                        } else if (isPhase(2)) {
                            stop();
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(1)) {
                            if (option == 1) {
                                if (player.inventory().containsAll(KIT, raw.get(finalI))) {
                                    if (player.inventory().remove(new Item(KIT), true) && player.inventory().remove(new Item(raw.get(finalI)), true)) {
                                        player.inventory().add(new Item(result.get(finalI)), true);
                                        send(DialogueType.ITEM_STATEMENT, result.get(finalI), "", "You apply the upgrade to obtain the cosmetic version.");
                                        setPhase(2);
                                    }
                                }
                            } else if (option == 2) {
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

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 4) {
            for (int i = 0; i < result.size(); i++) {
                if (item.getId() == result.get(i)) {
                    int r = i;
                    int finalI = i;
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(result.get(r)), new Item(KIT), "Do you want to revert the item to its normal form", "and get the kit back?");
                            setPhase(0);
                        }

                        @Override
                        protected void next() {
                            if (isPhase(0)) {
                                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                                setPhase(1);
                            }
                        }

                        @Override
                        protected void select(int option) {
                            if (isPhase(1)) {
                                if (option == 1) {
                                    if (player.inventory().contains(SteamStaffUpgrade.result.get(finalI)) && player.inventory().getFreeSlots() >= 1) {
                                        if (player.inventory().remove(new Item(SteamStaffUpgrade.result.get(finalI)), true)) {
                                            player.inventory().add(new Item(KIT), true);
                                            player.inventory().add(new Item(raw.get(finalI)), true);
                                        }
                                    }
                                    stop();
                                } else if (option == 2) {
                                    stop();
                                }
                            }
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }
}
