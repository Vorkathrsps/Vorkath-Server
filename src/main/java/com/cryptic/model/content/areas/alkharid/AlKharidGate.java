package com.cryptic.model.content.areas.alkharid;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.BORDER_GUARD;

/**
 * @author Origin | April, 14, 2021, 18:16
 * 
 */
public class AlKharidGate extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int opt) {
        if (obj.getId() == 2882 || obj.getId() == 2883) {
            if (opt == 1) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendPlayerChat(Expression.HAPPY, "Can I come through this gate?");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            sendNpcChat(BORDER_GUARD, Expression.HAPPY, "You must pay a toll of 10 gold coins to pass.");
                            setPhase(1);
                        } else if (isPhase(1)) {
                            sendOption(DEFAULT_OPTION_TITLE, "No thank you, I'll walk around.", "Who does my money go to?", "Yes, ok.");
                            setPhase(2);
                        } else if (isPhase(3)) {
                            sendNpcChat(BORDER_GUARD, Expression.HAPPY, "Ok suit yourself.");
                            setPhase(4);
                        } else if (isPhase(4)) {
                            stop();
                        } else if (isPhase(5)) {
                            sendNpcChat(BORDER_GUARD, Expression.HAPPY, "The money goes to the city of Al-Kharid.");
                            setPhase(4);
                        } else if (isPhase(6)) {
                            if (player.inventory().contains(new Item(995, 10))) {
                                passThrough(player);
                                stop();
                            } else {
                                sendPlayerChat(Expression.SLIGHTLY_SAD, "Oh dear I don't actually seem to have enough money.");
                                setPhase(4);
                            }
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(2)) {
                            if (option == 1) {
                                sendPlayerChat(Expression.HAPPY, "No thank you, I'll walk around.");
                                setPhase(3);
                            } else if (option == 2) {
                                sendPlayerChat(Expression.HAPPY, "Who does my money go to?");
                                setPhase(5);
                            } else if (option == 3) {
                                sendPlayerChat(Expression.HAPPY, "Yes, ok.");
                                setPhase(6);
                            }
                        }
                    }
                });
            } else if (opt == 4) {
                if (player.inventory().contains(new Item(995, 10))) {
                    passThrough(player);
                } else {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            sendPlayerChat(Expression.SLIGHTLY_SAD, "Oh dear I don't actually seem to have enough money.");
                            setPhase(0);
                        }

                        @Override
                        protected void next() {
                            if(isPhase(0)) {
                                stop();
                            }
                        }
                    });
                }
            }
            return true;
        }
        return false;
    }

    private void passThrough(Player player) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);

        player.lock();

        player.message("You pay the guard.");
        player.inventory().remove(new Item(995, 10), true);

        ObjectManager.removeObj(new GameObject(2882, new Tile(3268, 3227), 0, 0));
        ObjectManager.removeObj(new GameObject(2883, new Tile(3268, 3228), 0, 0));
        ObjectManager.addObj(new GameObject(7174, new Tile(3267, 3228), 0, 1));
        ObjectManager.addObj(new GameObject(7173, new Tile(3267, 3227), 0, 3));

        if (player.tile().x < 3268) {
            player.getMovementQueue().step(3268, obj.tile().y, MovementQueue.StepType.FORCED_WALK);
        } else {
            player.getMovementQueue().step(3267, obj.tile().y, MovementQueue.StepType.FORCED_WALK);
        }

        Chain.bound(player).name("AlKharidGateTask").runFn(1, () -> {
            ObjectManager.removeObj(new GameObject(7174, new Tile(3267, 3228), 0, 1));
            ObjectManager.removeObj(new GameObject(7173, new Tile(3267, 3227), 0, 3));
            ObjectManager.addObj(new GameObject(2882, new Tile(3268, 3227), 0, 0));
            ObjectManager.addObj(new GameObject(2883, new Tile(3268, 3228), 0, 0));
        }).then(1, player::unlock);
    }
}
