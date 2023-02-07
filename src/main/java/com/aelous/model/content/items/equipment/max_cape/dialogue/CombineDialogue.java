package com.aelous.model.content.items.equipment.max_cape.dialogue;

import com.aelous.model.content.items.equipment.max_cape.MaxCape;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;

import static com.aelous.model.content.items.equipment.max_cape.MaxCape.HOOD;
import static com.aelous.model.content.items.equipment.max_cape.MaxCape.NORMAL_MAXCAPE;

public class CombineDialogue extends Dialogue {

    // Cape : hood
    private int[] comboOf(int id) {
        if (id == MaxCape.FIRECAPE) {
            return new int[]{13329, 13330};
        } else if (id == MaxCape.AVAS) {
            return new int[]{13337, 13338};
        } else if (id == MaxCape.SARA) {
            return new int[]{13331, 13332};
        } else if (id == MaxCape.GUTHIX) {
            return new int[]{13335, 13336};
        } else if (id == MaxCape.ZAMMY) {
            return new int[]{13333, 13334};
        } else if (id == MaxCape.ARDY_CAPE_4) {
            return new int[]{20760, 20764};
        } else if (id == MaxCape.INFERNAL) {
            return new int[]{MaxCape.INFERNAL_MAX_CAPE, MaxCape.INFERNAL_MAX_HOOD};
        } else if (id == MaxCape.IMBUED_SARADOMIN_CAPE) {
            return new int[]{MaxCape.IMBUED_SARADOMIN_MAX_CAPE, MaxCape.IMBUED_SARADOMIN_MAX_HOOD};
        } else if (id == MaxCape.IMBUED_GUTHIX_CAPE) {
            return new int[]{MaxCape.IMBUED_GUTHIX_MAX_CAPE, MaxCape.IMBUED_GUTHIX_MAX_HOOD};
        } else if (id == MaxCape.IMBUED_ZAMORAK_CAPE) {
            return new int[]{MaxCape.IMBUED_ZAMORAK_MAX_CAPE, MaxCape.IMBUED_ZAMORAK_MAX_HOOD};
        } else if (id == MaxCape.ASSEMBLER) {
            return new int[]{MaxCape.ASSEMBLER_MAX_CAPE, MaxCape.ASSEMBLER_MAX_CAPE_HOOD};
        } else if (id == MaxCape.FROST) {
            return new int[]{MaxCape.FROST_IMBUED_MAX_CAPE, MaxCape.FROST_IMBUED_MAX_HOOD};
        }

        //Return empty
        return new int[]{};
    }

    @Override
    protected void start(Object... parameters) {
        int src = player.getAttribOr(AttributeKey.ITEM_ID,-1);
        String name = new Item(src).name();

        send(DialogueType.ITEM_STATEMENT, new Item(NORMAL_MAXCAPE), "", "Are you sure you want to combine the Max Cape and a", "" + name + "? This combines the stats together and", "cannot be undone. You <col=FF0000>will lose</col> your " + name + ".");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Proceed with infusion.", "Never mind.");
            setPhase(1);
        }
    }

    @Override
    protected void select(int option) {
        if (getPhase() == 1) {
            if (option == 1) {
                int src = player.getAttribOr(AttributeKey.ITEM_ID,-1);
                int[] combo = comboOf(src);
                if (combo.length == 2) { // Safety
                    if(!player.inventory().containsAll(src, NORMAL_MAXCAPE, HOOD)) {
                        stop();
                        return;
                    }
                    player.inventory().remove(new Item(src), true);
                    player.inventory().remove(new Item(NORMAL_MAXCAPE), true);
                    player.inventory().remove(new Item(HOOD), true);
                    player.inventory().add(new Item(combo[0]), true);
                    player.inventory().add(new Item(combo[1]), true);
                    String extra = "and Hood";
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.ITEM_STATEMENT, new Item(combo[0]), "", "You fuse the items together to produce a new Max Cape" + extra + ".");
                            setPhase(0);
                        }

                        @Override
                        protected void next() {
                            if (getPhase() == 0) {
                                stop();
                            }
                        }
                    });
                }
            }
            stop();
        } else if (option == 2) {
            stop();
        }
    }
}
