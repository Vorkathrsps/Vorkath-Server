package com.cryptic.model.content.items.equipment.wildswords;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class WildernessSword extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 3) {
            if (item.getId() == ItemIdentifiers.WILDERNESS_SWORD_3) {
                if (Teleports.canTeleport(player, true, TeleportType.ABOVE_20_WILD)) {
                    handleTeleport(player, "50", 2998, 3913);
                }
                return true;
            }
            if (item.getId() == ItemIdentifiers.WILDERNESS_SWORD_4) {
                if (Teleports.canTeleport(player, true, TeleportType.ABOVE_20_WILD)) {
                    handleTeleport(player, "55",3018, 3958);
                }
                return true;
            }
        }
        return false;
    }

    private static void handleTeleport(Player player, String wildernessLevel, int x, int y) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendStatement("This Teleport will take you into Level " + wildernessLevel + " Deep Wilderness.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    sendOption("Would you like to continue?", "Yes", "No");
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        player.lockMovement();
                        player.animate(3872);
                        player.graphic(283);
                        Chain.noCtx().runFn(4, () -> {
                            player.teleport(new Tile(x, y));
                            player.animate(Animation.DEFAULT_RESET_ANIMATION);
                            player.unlock();
                        });
                        stop();
                    } else {
                        stop();
                    }
                }
            }
        });
    }
}
