package com.aelous.model.content.areas.alkharid;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.BORDER_GUARD;

/**
 * @author Patrick van Elderen | April, 14, 2021, 18:16
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class AlKharidGate extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int opt) {
        if (obj.getId() == 2882 || obj.getId() == 2883) {
            if (opt == 1) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Can I come through this gate?");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            send(DialogueType.NPC_STATEMENT, BORDER_GUARD, Expression.HAPPY, "You must pay a toll of 10 gold coins to pass.");
                            setPhase(1);
                        } else if (isPhase(1)) {
                            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "No thank you, I'll walk around.", "Who does my money go to?", "Yes, ok.");
                            setPhase(2);
                        } else if (isPhase(3)) {
                            send(DialogueType.NPC_STATEMENT, BORDER_GUARD, Expression.HAPPY, "Ok suit yourself.");
                            setPhase(4);
                        } else if (isPhase(4)) {
                            stop();
                        } else if (isPhase(5)) {
                            send(DialogueType.NPC_STATEMENT, BORDER_GUARD, Expression.HAPPY, "The money goes to the city of Al-Kharid.");
                            setPhase(4);
                        } else if (isPhase(6)) {
                            if (player.inventory().contains(new Item(995, 10))) {
                                passThrough(player);
                                stop();
                            } else {
                                send(DialogueType.PLAYER_STATEMENT, Expression.SLIGHTLY_SAD, "Oh dear I don't actually seem to have enough money.");
                                setPhase(4);
                            }
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(2)) {
                            if (option == 1) {
                                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "No thank you, I'll walk around.");
                                setPhase(3);
                            } else if (option == 2) {
                                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Who does my money go to?");
                                setPhase(5);
                            } else if (option == 3) {
                                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Yes, ok.");
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
                            send(DialogueType.PLAYER_STATEMENT, Expression.SLIGHTLY_SAD, "Oh dear I don't actually seem to have enough money.");
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
