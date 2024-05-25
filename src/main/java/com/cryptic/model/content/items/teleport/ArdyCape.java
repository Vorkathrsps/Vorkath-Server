package com.cryptic.model.content.items.teleport;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;

/**
 * Created by Jak on 31/10/2016.
 */
public class ArdyCape {

    public static final int ARDY_MAXCAPE = 20760;
    public static final int ARDY_CLOAK_4 = 13124;
    private static final Tile MONASTERY = new Tile(2606, 3221);
    private static final Tile ARDY_FARM = new Tile(2664, 3375);

    public static void onEquipmentOption(Player player, Item item, int slot) {
        if(item.getId() == ARDY_CLOAK_4 && slot == 1) {
            ardyCapeTeleport(player);
        }
    }

    public static void onItemOption3(Player player, Item item) {
        if(item.getId() == ARDY_CLOAK_4) {
            if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                Teleports.basicTeleport(player, MONASTERY,3872, new Graphic(1237, GraphicHeight.HIGH));
            }
        }
    }

    public static void onItemOption2(Player player, Item item) {
        if(item.getId() == ARDY_CLOAK_4) {
            if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                Teleports.basicTeleport(player, ARDY_FARM, 3872, new Graphic(1237, GraphicHeight.HIGH));
            }
        }
    }

    private static void ardyCapeTeleport(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Kandarin Monastery", "Ardougne Farm");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(isPhase(0)) {
                    if(option == 1) {
                        if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            Teleports.basicTeleport(player, MONASTERY, 3872, new Graphic(1237, GraphicHeight.HIGH));
                        }
                    } else if(option == 2) {
                        if (Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                            Teleports.basicTeleport(player, ARDY_FARM, 3872, new Graphic(1237, GraphicHeight.HIGH));
                        }
                    }
                }
            }
        });
    }
}
