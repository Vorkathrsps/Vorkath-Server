package com.aelous.model.content.items.combine;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

/**
 * @author Patrick van Elderen | March, 16, 2021, 14:47
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class SmoulderingStone extends PacketInteraction {

    private final static int SMOULDERING_STONE = 13233;
    private final static int INFERNAL_PICKAXE = 13243;
    private final static int INFERNAL_AXE = 13241;

    private final static int DRAGON_PICKAXE = 11920;
    private final static int DRAGON_AXE = 6739;

    private final static int DRAGON_BOOTS = 11840;
    private final static int RANGER_BOOTS = 2577;
    private final static int INFINITY_BOOTS = 6920;

    private final static int PRIMORDIAL_CRYSTAL = 13231;
    private final static int PRIMORDIAL_BOOTS = 13239;

    private final static int PEGASIAN_CRYSTAL = 13229;
    private final static int PEGASIAN_BOOTS = 13237;

    private final static int ETERNAL_CRYSTAL = 13227;
    private final static int ETERNAL_BOOTS = 13235;

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 3) {
            if (item.getId() == INFERNAL_PICKAXE) {
                player.message("Your infernal pickaxe is fully charged.");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == SMOULDERING_STONE || usedWith.getId() == SMOULDERING_STONE) && (use.getId() == DRAGON_PICKAXE || usedWith.getId() == DRAGON_PICKAXE)) {
            makePickaxe(player);
            return true;
        }
        if ((use.getId() == SMOULDERING_STONE || usedWith.getId() == SMOULDERING_STONE) && (use.getId() == DRAGON_AXE || usedWith.getId() == DRAGON_AXE)) {
            makeAxe(player);
            return true;
        }
        if ((use.getId() == PRIMORDIAL_CRYSTAL || usedWith.getId() == PRIMORDIAL_CRYSTAL) && (use.getId() == DRAGON_BOOTS || usedWith.getId() == DRAGON_BOOTS)) {
            makeBoots(player, PRIMORDIAL_BOOTS, DRAGON_BOOTS, PRIMORDIAL_CRYSTAL);
            return true;
        }
        if ((use.getId() == PEGASIAN_CRYSTAL || usedWith.getId() == PEGASIAN_CRYSTAL) && (use.getId() == RANGER_BOOTS || usedWith.getId() == RANGER_BOOTS)) {
            makeBoots(player, PEGASIAN_BOOTS, RANGER_BOOTS, PEGASIAN_CRYSTAL);
            return true;
        }
        if ((use.getId() == ETERNAL_CRYSTAL || usedWith.getId() == ETERNAL_CRYSTAL) && (use.getId() == INFINITY_BOOTS || usedWith.getId() == INFINITY_BOOTS)) {
            makeBoots(player, ETERNAL_BOOTS, INFINITY_BOOTS, ETERNAL_CRYSTAL);
            return true;
        }
        return false;
    }

    private static void makePickaxe(Player player) {
        // Check requirements
        if (player.getSkills().level(Skills.MINING) < 61 || player.getSkills().level(Skills.SMITHING) < 85) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(SMOULDERING_STONE), new Item(DRAGON_PICKAXE), "You need level 61 Mining and level 85 Smithing to make an infernal pickaxe.", "");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
            return;
        }

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(SMOULDERING_STONE), new Item(DRAGON_PICKAXE), "Are you sure you wish to convert your dragon pickaxe<br>into an infernal pickaxe? This cannot be reversed.", "Infernal pickaxes are untradable.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Proceed with the infusion.", "Cancel.");
                    setPhase(1);
                } else if (isPhase(2)) {
                    stop();
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if(!player.inventory().containsAll(DRAGON_PICKAXE, SMOULDERING_STONE)) {
                            stop();
                            return;
                        }
                        // Do some nice graphics
                        player.animate(4513);
                        player.graphic(1240);

                        // Apply inv change
                        if (player.inventory().remove(new Item(DRAGON_PICKAXE), true) && player.inventory().remove(new Item(SMOULDERING_STONE), true))
                            player.inventory().add(new Item(INFERNAL_PICKAXE), true);
                        send(DialogueType.ITEM_STATEMENT, new Item(INFERNAL_PICKAXE), "", "You infuse the smouldering stone into the pickaxe<br>to make an infernal pickaxe.");
                        setPhase(2);
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private static void makeAxe(Player player) {
        // Check requirements
        if (player.getSkills().level(Skills.WOODCUTTING) < 61 || player.getSkills().level(Skills.FIREMAKING) < 85) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(SMOULDERING_STONE), new Item(DRAGON_AXE), "You need level 61 Woodcutting and level 85 Firemaking to make an infernal axe.", "");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
            return;
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(SMOULDERING_STONE), new Item(DRAGON_AXE), "Are you sure you wish to convert your dragon axe<br>into an infernal axe? This cannot be reversed.", "Infernal axes are untradable.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Proceed with the infusion.", "Cancel.");
                    setPhase(1);
                } else if (isPhase(2)) {
                    stop();
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if(!player.inventory().containsAll(DRAGON_AXE, SMOULDERING_STONE)) {
                            stop();
                            return;
                        }
                        // Do some nice graphics
                        player.animate(4513);
                        player.graphic(1240);

                        // Apply inv change
                        if (player.inventory().remove(new Item(DRAGON_AXE), true) && player.inventory().remove(new Item(SMOULDERING_STONE), true))
                            player.inventory().add(new Item(INFERNAL_AXE), true);

                        send(DialogueType.ITEM_STATEMENT, new Item(INFERNAL_AXE), "", "You infuse the smouldering stone into the axe<br>to make an infernal pickaxe.");
                        setPhase(2);
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private static void makeBoots(Player player, int result, int item1, int item2) {
        String item1Name = new Item(item1).name();
        String item2Name = new Item(item2).name();
        String resultName = new Item(result).name();

        // Check requirements
        if (player.getSkills().xpLevel(Skills.RUNECRAFTING) < 60 || player.getSkills().xpLevel(Skills.MAGIC) < 60) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(item1), new Item(item2), "You need level 60 Runecrafting and Magic to create " + resultName + "", "");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
            return;
        }

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.DOUBLE_ITEM_STATEMENT, new Item(item1), new Item(item2), "Are you sure you wish to infuse the " + item1Name + " and<br>"+ item2Name + " to create " + resultName + "?<br>This can not be reversed.", "");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Proceed with the infusion.", "Cancel.");
                    setPhase(1);
                } else if (isPhase(2)) {
                    stop();
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        // Do some nice graphics
                        if (item1 == DRAGON_BOOTS && item2 == PRIMORDIAL_CRYSTAL) {
                            player.animate(4513);
                            player.graphic(1240);
                        } else {
                            player.animate(4462);
                            player.graphic(759);
                        }

                        if(player.inventory().containsAll(new Item(item1), new Item(item2))) {
                            if (player.inventory().remove(new Item(item1), true) && player.inventory().remove(new Item(item2), true))
                                player.inventory().add(new Item(result), true);
                        }

                        player.getSkills().addXp(Skills.MAGIC, 200.0);
                        player.getSkills().addXp(Skills.RUNECRAFTING, 200.0);
                        stop();
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }
}
