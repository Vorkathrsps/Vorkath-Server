package com.aelous.model.content.items.combine;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;

import java.util.Arrays;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 28, 2020
 */
public class ItemCombination extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        ItemDefinition def = item.definition(World.getWorld());
        if(option == 1) {
            if(item.getId() == VORKATHS_HEAD_21907) {
                player.message("This blue dragon smells like it's been dead for a remarkably long time. Even by my...");
                player.message("standards, it smells awful.");
                return true;
            }
        }
        if(option == 2) {
            if(def != null && def.name.contains("Twisted slayer")) {
                if(item.getId() == TWISTED_SLAYER_HELMET_I) {
                    player.message("You can't disassemble this helmet.");
                    return true;
                }
                player.optionsTitled("Are you sure you want to remove the attachment", "Remove attachment. ("+ Color.RED.wrap("attachment will be lost")+")", "Nevermind.", () -> {
                    if(player.inventory().contains(item.getId())) {
                        player.inventory().remove(item);
                        player.inventory().add(new Item(TWISTED_SLAYER_HELMET_I));
                    }
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == TWISTED_ANCESTRAL_COLOUR_KIT || usedWith.getId() == TWISTED_ANCESTRAL_COLOUR_KIT) && (use.getId() == ANCESTRAL_HAT || usedWith.getId() == ANCESTRAL_HAT)) {
            combineTwistedAncestral(player);
            return true;
        }

        if ((use.getId() == TWISTED_ANCESTRAL_COLOUR_KIT || usedWith.getId() == TWISTED_ANCESTRAL_COLOUR_KIT) && (use.getId() == ANCESTRAL_ROBE_TOP || usedWith.getId() == ANCESTRAL_ROBE_TOP)) {
            combineTwistedAncestral(player);
            return true;
        }

        if ((use.getId() == TWISTED_ANCESTRAL_COLOUR_KIT || usedWith.getId() == TWISTED_ANCESTRAL_COLOUR_KIT) && (use.getId() == ANCESTRAL_ROBE_BOTTOM || usedWith.getId() == ANCESTRAL_ROBE_BOTTOM)) {
            combineTwistedAncestral(player);
            return true;
        }

        if ((use.getId() == VORKATHS_HEAD_21907 || usedWith.getId() == VORKATHS_HEAD_21907) && (use.getId() == AVAS_ACCUMULATOR || usedWith.getId() == AVAS_ACCUMULATOR)) {
            combineAssembler(player);
            return true;
        }

        if ((use.getId() == DARK_CLAW || usedWith.getId() == DARK_CLAW) && (use.getId() == TWISTED_SLAYER_HELMET_I || usedWith.getId() == TWISTED_SLAYER_HELMET_I)) {
            combineTwistedSkot(player);
            return true;
        }

        if ((use.getId() == ALCHEMICAL_HYDRA_HEADS || usedWith.getId() == ALCHEMICAL_HYDRA_HEADS) && (use.getId() == TWISTED_SLAYER_HELMET_I || usedWith.getId() == TWISTED_SLAYER_HELMET_I)) {
            combineTwistedHydra(player);
            return true;
        }

        if ((use.getId() == BANDOS_BOOTS || usedWith.getId() == BANDOS_BOOTS) && (use.getId() == BLACK_TOURMALINE_CORE || usedWith.getId() == BLACK_TOURMALINE_CORE)) {
            combineGuardianBoots(player);
            return true;
        }
        List<Integer> dkite_products = Arrays.asList(DRAGON_METAL_SHARD, DRAGON_METAL_SLICE);
        for (int id : dkite_products) {
            if ((use.getId() == id || usedWith.getId() == id) && (use.getId() == DRAGON_SQ_SHIELD || usedWith.getId() == DRAGON_SQ_SHIELD)) {
                createDragonKiteshield(player);
                return true;
            }
        }

        List<Integer> dplate_products = Arrays.asList(DRAGON_METAL_SHARD, DRAGON_METAL_LUMP);
        for (int id : dplate_products) {
            if ((use.getId() == id || usedWith.getId() == id) && (use.getId() == DRAGON_CHAINBODY || usedWith.getId() == DRAGON_CHAINBODY)) {
                createDragonPlatebody(player);
                return true;
            }
        }
        return false;
    }

    private void combineTwistedAncestral(Player player) {
        if(!player.inventory().containsAll(new Item(ANCESTRAL_HAT), new Item(ANCESTRAL_ROBE_TOP), new Item(ANCESTRAL_ROBE_BOTTOM))) {
            player.message("You need all three ancestral pieces to combine the kit.");
            return;
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Create Twisted Ancestral?", "Yes, sacrifice the kit.", "No, not right now.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        if(!player.inventory().containsAll(new Item(ANCESTRAL_HAT), new Item(ANCESTRAL_ROBE_TOP), new Item(ANCESTRAL_ROBE_BOTTOM))) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(TWISTED_ANCESTRAL_COLOUR_KIT),true);
                        player.inventory().remove(new Item(ANCESTRAL_HAT),true);
                        player.inventory().remove(new Item(ANCESTRAL_ROBE_TOP),true);
                        player.inventory().remove(new Item(ANCESTRAL_ROBE_BOTTOM),true);
                        player.inventory().add(new Item(TWISTED_ANCESTRAL_HAT),true);
                        player.inventory().add(new Item(TWISTED_ANCESTRAL_ROBE_TOP),true);
                        player.inventory().add(new Item(TWISTED_ANCESTRAL_ROBE_BOTTOM),true);
                        player.message("You carefully attach the kit to the ancestral piece, to change its colours.");
                        stop();
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private void combineAssembler(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Create Ava's Assembler?", "Yes, sacrifice Vorkath's head.", "No, not right now.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        if(!player.inventory().containsAll(VORKATHS_HEAD_21907, AVAS_ACCUMULATOR)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(VORKATHS_HEAD_21907), true);
                        player.inventory().remove(new Item(AVAS_ACCUMULATOR), true);
                        player.inventory().add(new Item(AVAS_ASSEMBLER), true);
                        player.message("You carefully attach the Vorkath's head to the device and create the assembler.");
                        stop();
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private void combineTwistedSkot(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Recolor the Twisted slayer helmet (i)?", "Yes, sacrifice Dark claw.", "No, not right now.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        if(!player.inventory().containsAll(DARK_CLAW, TWISTED_SLAYER_HELMET_I)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(DARK_CLAW), true);
                        player.inventory().remove(new Item(TWISTED_SLAYER_HELMET_I), true);
                        player.inventory().add(new Item(HYDRA_SLAYER_HELMET_I), true);
                        player.message("You carefully attach Dark claw to the helmet to give it an outstanding look.");
                        stop();
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private void combineTwistedHydra(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Recolor the Twisted slayer helmet (i)?", "Yes, sacrifice hydra's head.", "No, not right now.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        if(!player.inventory().containsAll(ALCHEMICAL_HYDRA_HEADS, TWISTED_SLAYER_HELMET_I)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(ALCHEMICAL_HYDRA_HEADS), true);
                        player.inventory().remove(new Item(TWISTED_SLAYER_HELMET_I), true);
                        player.inventory().add(new Item(HYDRA_SLAYER_HELMET_I), true);
                        player.message("You carefully attach hydra's head to the helmet to give it an outstanding look.");
                        stop();
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private void createDragonPlatebody(Player player) {
        if (!player.inventory().containsAny(DRAGON_METAL_SHARD, DRAGON_METAL_LUMP, DRAGON_CHAINBODY)) {
            player.message("You're missing some of the items to create the dragon platebody.");
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Create the Dragon Platebody?", "Yes", "No");
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
                        if(!player.inventory().containsAll(DRAGON_CHAINBODY, DRAGON_METAL_LUMP, DRAGON_METAL_SHARD)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(DRAGON_CHAINBODY), true);
                        player.inventory().remove(new Item(DRAGON_METAL_LUMP), true);
                        player.inventory().remove(new Item(DRAGON_METAL_SHARD), true);
                        int slot = player.getAttribOr(AttributeKey.ALT_ITEM_SLOT, -1);
                        player.inventory().add(new Item(21892), slot, true);
                        send(DialogueType.ITEM_STATEMENT, 21892, "", "You combine the shard, lump and chainbody to create a Dragon Platebody.");
                        setPhase(1);
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private void createDragonKiteshield(Player player) {
        if (!player.inventory().containsAny(DRAGON_METAL_SHARD, DRAGON_METAL_SLICE, DRAGON_SQ_SHIELD)) {
            player.message("You're missing some of the items to create the dragon kiteshield.");
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Create the Dragon Kiteshield?", "Yes", "No");
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
                        if(!player.inventory().containsAll(DRAGON_SQ_SHIELD, DRAGON_METAL_SLICE, DRAGON_METAL_SHARD)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(DRAGON_SQ_SHIELD), true);
                        player.inventory().remove(new Item(DRAGON_METAL_SLICE), true);
                        player.inventory().remove(new Item(DRAGON_METAL_SHARD), true);
                        int slot = player.getAttribOr(AttributeKey.ALT_ITEM_SLOT, -1);
                        player.inventory().add(new Item(21895), slot, true);
                        send(DialogueType.ITEM_STATEMENT, 21895, "", "You combine the shard, slice and square shield to create a Dragon Kiteshield.");
                        setPhase(1);
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    private void combineGuardianBoots(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Create Guardian Boots?", "Yes, I'd like to merge the core usedWith the boots.", "No, not right now.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        if(!player.inventory().containsAll(BANDOS_BOOTS, BLACK_TOURMALINE_CORE)) {
                            stop();
                            return;
                        }
                        player.inventory().remove(new Item(BANDOS_BOOTS), true);
                        player.inventory().remove(new Item(BLACK_TOURMALINE_CORE), true);
                        player.inventory().add(new Item(GUARDIAN_BOOTS), true);
                        player.message("You merge the black tourmaline core usedWith the boots to create a pair of guardian boots.");
                        stop();
                    } else if(option == 2) {
                        stop();
                    }
                }
            }
        });
    }
}
