package com.cryptic.model.content.areas.burthope.rogues_den;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.content.BankObjects;
import com.cryptic.model.content.areas.burthope.rogues_den.dialogue.BrianORichard;
import com.cryptic.model.content.areas.burthope.rogues_den.dialogue.EmeraldBenedict;
import com.cryptic.model.content.areas.burthope.rogues_den.dialogue.Grace;
import com.cryptic.model.content.areas.burthope.rogues_den.dialogue.MartinThwait;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.cs2.impl.dialogue.DialogueManager;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin | March, 26, 2021, 09:38
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RoguesDen extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if (npc.id() == GRACE) {
                player.getDialogueManager().start(new Grace());
                return true;
            }
            if (npc.id() == EMERALD_BENEDICT) {
                player.getDialogueManager().start(new EmeraldBenedict());
                return true;
            }
            if(npc.id() == MARTIN_THWAIT) {
                player.getDialogueManager().start(new MartinThwait());
                return true;
            }
            if(npc.id() == BRIAN_ORICHARD) {
                player.getDialogueManager().start(new BrianORichard());
                return true;
            }
        }

        if(option == 2) {
            if (npc.id() == GRACE) {
                World.getWorld().shop(21).open(player);
                return true;
            }
            if (npc.id() == EMERALD_BENEDICT) {
                player.getBank().open();
                return true;
            }
        }

        if(option == 3) {
            if (npc.id() == EMERALD_BENEDICT) {
                World.getWorld().shop(48).open(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnNpc(Player player, Item item, NPC npc) {
        if (npc.id() == EMERALD_BENEDICT) {
            int itemId = player.getAttribOr(AttributeKey.ITEM_ID, -1);
            int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, -1);
            ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, itemId);
            if (def == null) return false;

            BankObjects.noteLogic(player, itemId, slot, def);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            if(object.getId() == TRAPDOOR_7257) {
                player.teleport(3061, 4985, 1);
                return true;
            }
            if(object.getId() == PASSAGEWAY_7258) {
                player.teleport(2906, 3537);
                return true;
            }
            if(object.getId() == DOORWAY_7256) {
                int inventory_space = player.inventory().getFreeSlots();
                int equipment_space = player.getEquipment().getFreeSlots();

                //Does our player have the gem?
                if (player.inventory().contains(new Item(BrianORichard.MYSTIC_JEWEL))) {

                    //Does our player have any items in their inventory/equipment?
                    if (inventory_space == 27 && equipment_space == 14) {
                        GameObject door = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);

                        if (player.tile().y <= 4991) {
                            if (!player.tile().equals(door.tile().transform(0, 0, 0))) {
                                player.getMovementQueue().walkTo(door.tile().transform(0, 0, 0));
                            }
                            Chain.bound(player).name("MazeEntranceDoorwayTask").waitForTile(door.tile().transform(0, 0, 0), () -> {
                                player.lock();

                                GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
                                GameObject spawned = new GameObject(7254, new Tile(3056, 4991, door.tile().level), door.getType(), 1);

                                ObjectManager.removeObj(old);
                                ObjectManager.addObj(spawned);

                                player.getMovementQueue().interpolate(3056, 4992, MovementQueue.StepType.FORCED_WALK);
                                Chain.bound(player).name("MazeEntranceDoorway1Task").waitForTile(new Tile(3056, 4992), player::unlock);

                                Chain.bound(player).name("MazeEntranceDoorway2Task").runFn(2, () -> {
                                    ObjectManager.removeObj(spawned);
                                    ObjectManager.addObj(old);
                                });
                            });
                        } else {
                            player.message("The door won't open!");
                        }
                    } else {
                        DialogueManager.npcChat(player, Expression.H, BRIAN_ORICHARD,"Tut tut tut, now you know you're not allowed to take", "anything except that jewel in with you.");
                    }
                } else {
                    DialogueManager.npcChat(player, Expression.H1, BRIAN_ORICHARD,"And where do you think you're going? A little too eager", "I think. Come and talk to me before you go wandering", "around in there.");
                }
                return true;
            }
        }
        return false;
    }
}
