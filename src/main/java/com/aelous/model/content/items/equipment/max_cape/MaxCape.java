package com.aelous.model.content.items.equipment.max_cape;

import com.aelous.model.content.areas.edgevile.Mac;
import com.aelous.model.content.items.equipment.max_cape.dialogue.CombineDialogue;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

public class MaxCape extends PacketInteraction {

    public final static int FROST_IMBUED_MAX_CAPE = 30023;
    public final static int FROST_IMBUED_MAX_HOOD = 30025;
    public final static int INFERNAL_MAX_CAPE = 21285;
    public final static int INFERNAL_MAX_HOOD = 21282;
    public final static int IMBUED_SARADOMIN_MAX_CAPE = 21776;
    public final static int IMBUED_SARADOMIN_MAX_HOOD = 21778;
    public final static int IMBUED_ZAMORAK_MAX_CAPE = 21780;
    public final static int IMBUED_ZAMORAK_MAX_HOOD = 21782;
    public final static int IMBUED_GUTHIX_MAX_CAPE = 21784;
    public final static int IMBUED_GUTHIX_MAX_HOOD = 21786;
    public final static int ASSEMBLER_MAX_CAPE = 21898;
    public final static int ASSEMBLER_MAX_CAPE_HOOD = 21900;
    public final static int NORMAL_MAXCAPE = 13280;
    public final static int HOOD = 13281;
    public final static int FIRECAPE = 6570;
    public final static int AVAS = 10499;
    public final static int GUTHIX = 2413;
    public final static int SARA = 2412;
    public final static int ZAMMY = 2414;
    public final static int ARDY_CAPE_4 = 13124;
    public final static int IMBUED_SARADOMIN_CAPE = 21791;
    public final static int IMBUED_GUTHIX_CAPE = 21793;
    public final static int IMBUED_ZAMORAK_CAPE = 21795;
    public final static int INFERNAL = 21295;
    public final static int ASSEMBLER = 22109;
    public static final int FROST = 30015;

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        // Fusion
        for (int src : new int[]{FIRECAPE, AVAS, SARA, GUTHIX, ZAMMY, ARDY_CAPE_4, INFERNAL, IMBUED_SARADOMIN_CAPE, IMBUED_ZAMORAK_CAPE, IMBUED_GUTHIX_CAPE, ASSEMBLER, FROST}) {
            if ((use.getId() == NORMAL_MAXCAPE && usedWith.getId() == src) || (use.getId() == src && usedWith.getId() == NORMAL_MAXCAPE)) {
                combine(player, src);
                return true;
            }
            if ((use.getId() == HOOD && usedWith.getId() == src) || (use.getId() == src && usedWith.getId() == HOOD)) {
                combine(player, src);
                return true;
            }
        }
        return false;
    }

    private void combine(Player player, int src) {
        boolean hasItems = player.inventory().containsAll(NORMAL_MAXCAPE, HOOD, src);

        if (!hasItems) {
            DialogueManager.sendStatement(player, "You need a Max Cape and the Max Hood to infuse these together.");
            return;
        }

        player.getDialogueManager().start(new CombineDialogue());
    }

    public static boolean hasTotalLevel(Player player) {
        return Mac.totalLevel(player) >= Mac.TOTAL_LEVEL_FOR_MAXED;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 3) {
            if(item.name().contains("Max cape") || item.name().contains("max cape")) {
                player.getDialogueManager().start(new MaxCapeDialogue());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemContainerActionInteraction(Player player, Item item, int slot, int interfaceId, int type) {
        if(type == 2) {
            if(item.name().contains("Max cape") || item.name().contains("max cape")) {
                if(interfaceId == 1688 && slot == 1) {
                    player.getDialogueManager().start(new MaxCapeDialogue());
                }
                return true;
            }
        }
        return false;
    }

    private static class MaxCapeDialogue extends Dialogue {

        @Override
        protected void start(Object... parameters) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Teleport to slayer task.", "Nevermind.");
            setPhase(0);
        }

        @Override
        protected void select(int option) {
            if(isPhase(0)) {
                if(option == 1) {
                    stop();
                    SlayerCreature.teleport(player);
                } else if(option == 2) {
                    stop();
                }
            }
        }
    }
}
