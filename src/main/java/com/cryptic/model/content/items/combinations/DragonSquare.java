package com.cryptic.model.content.items.combine;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.ItemIdentifiers.SHIELD_LEFT_HALF;
import static com.cryptic.utility.ItemIdentifiers.SHIELD_RIGHT_HALF;

/**
 * @author Origin | March, 16, 2021, 14:24
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DragonSquare extends PacketInteraction {

    private static final int LEFT = 2366;
    private static final int RIGHT = 2368;
    private static final int FULL = 1187;
    
    private boolean onAnvil(Player player) {
        if (player.inventory().containsAll(LEFT, RIGHT)) {
            player.message("You start to hammer the metal...");

            player.animate(898);
            Chain.bound(player).name("DragonSquareAnvilTask").runFn(6, () -> {
                if (player.inventory().containsAll(LEFT, RIGHT)) {
                    player.inventory().remove(new Item(LEFT), true);
                    player.inventory().remove(new Item(RIGHT), true);
                    player.inventory().add(new Item(FULL), true);
                }
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendItemStatement(new Item(FULL), "", "You forge the shield halves together to complete it.");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            stop();
                        }
                    }
                });
            });
        } else {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    sendItemStatement(new Item(LEFT), new Item(RIGHT), "You need both the left and right shield halves to forge a square shield.", "");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        if (object.definition().name.equalsIgnoreCase("anvil")) {
            if (player.inventory().containsAll(SHIELD_LEFT_HALF, SHIELD_RIGHT_HALF)) {
                return onAnvil(player);
            }
        }
        return false;
    }
}
