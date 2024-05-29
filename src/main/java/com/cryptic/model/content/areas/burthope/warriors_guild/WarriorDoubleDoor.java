package com.cryptic.model.content.areas.burthope.warriors_guild;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.ItemIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_24306;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DOOR_24309;

/**
 * @author Origin | March, 26, 2021, 09:54
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WarriorDoubleDoor extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (option == 1) {
            if (object.getId() == DOOR_24309 || object.getId() == DOOR_24306) {
                door(player);
                return true;
            }
        }
        return false;
    }

    private static void door(Player player) {
        //Main floor
        if (player.tile().level == 0) {
            GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);

            player.lock();

            if (!player.tile().equals(obj.tile().transform(0, -1, 0)) && player.tile().x >= 3546) {
                player.getMovementQueue().walkTo(obj.tile());
            } else if (!player.tile().equals(obj.tile().transform(0, 1, 0)) && player.tile().y <= 3545) {
                player.getMovementQueue().walkTo(obj.tile().transform(0, -1, 0));
            }

            GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
            GameObject spawned = new GameObject(obj.getId() + 2, new Tile(obj.tile().x, 3545), obj.getType(), obj.getId() == 24309 ? 2 : 0);
            ObjectManager.removeObj(old);
            ObjectManager.addObj(spawned);
            Chain.bound(player).name("WarriorGuildMainFloorDoorTask").runFn(2, () -> {
                ObjectManager.removeObj(spawned);
                ObjectManager.addObj(old);
            });

            if (player.tile().y >= 3546) {
                player.getMovementQueue().interpolate(obj.tile().x, 3545, MovementQueue.StepType.FORCED_WALK);
            } else if (player.tile().y <= 3545) {
                player.getMovementQueue().interpolate(obj.tile().x, 3546, MovementQueue.StepType.FORCED_WALK);
            }

            player.unlock();
        } else if (player.tile().level == 2) {

            //Second floor
            GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);

            player.lock();

            if (!player.tile().equals(obj.tile().transform(-1, 0, 0)) && player.tile().x <= 2846) {
                player.getMovementQueue().walkTo(obj.tile().transform(-1, 0, 0));
            } else if (!player.tile().equals(obj.tile().transform(0, 0, 0)) && player.tile().x >= 2847) {
                player.getMovementQueue().walkTo(obj.tile().transform(0, 0, 0));
            }

            //Does our player have enough tokens or are they wearing the attack cape? //TODO cape
            if (/*(*/!player.inventory().contains(new Item(WARRIOR_GUILD_TOKEN, 100)) /*&& !CapeOfCompletion.ATTACK.operating(player))*/ && player.tile().x == 2846) {
                player.unlock();
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendItemStatement(new Item(WARRIOR_GUILD_TOKEN, 100), "", "You don't have enough Warrior Guild Tokens to enter the", "cyclopes enclosure yet, collect at least 100 then come back.");
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

           /* GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
            GameObject spawned = new GameObject(obj.getId() + 2, new Tile(obj.tile().x - 1, obj.tile().y, obj.tile().level), obj.getType(), obj.getId() == 24309 ? 1 : 3);
            ObjectManager.removeObj(old);
            ObjectManager.addObj(spawned);
            Chain.bound(player).name("WarriorGuildSecondFloorDoorTask").runFn(2, () -> {
                ObjectManager.removeObj(spawned);
                ObjectManager.addObj(old);
            });*/

            if (player.tile().x == 2846) {
                player.teleport(obj.tile().x, player.tile().y, player.getZ());
                //player.getMovementQueue().interpolate(obj.tile().x, player.tile().y, MovementQueue.StepType.FORCED_WALK);
            } else if (player.tile().x == 2847) {
                player.teleport(obj.tile().x - 1, player.tile().y, player.getZ());
                //player.getMovementQueue().interpolate(obj.tile().x - 1, player.tile().y, MovementQueue.StepType.FORCED_WALK);
            }

            if (player.tile().x == 2847) {
                if (player.inventory().contains(new Item(RUNE_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, RUNE_DEFENDER))
                    set_item(player, RUNE_DEFENDER);
                else if (player.inventory().contains(new Item(ADAMANT_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, ADAMANT_DEFENDER))
                    set_item(player, RUNE_DEFENDER);
                else if (player.inventory().contains(new Item(MITHRIL_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, MITHRIL_DEFENDER))
                    set_item(player, ADAMANT_DEFENDER);
                else if (player.inventory().contains(new Item(BLACK_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, BLACK_DEFENDER))
                    set_item(player, MITHRIL_DEFENDER);
                else if (player.inventory().contains(new Item(STEEL_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, STEEL_DEFENDER))
                    set_item(player, BLACK_DEFENDER);
                else if (player.inventory().contains(new Item(IRON_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, IRON_DEFENDER))
                    set_item(player, STEEL_DEFENDER);
                else if (player.inventory().contains(new Item(BRONZE_DEFENDER)) || player.getEquipment().hasAt(EquipSlot.SHIELD, BRONZE_DEFENDER))
                    set_item(player, IRON_DEFENDER);
                else
                    set_item(player, BRONZE_DEFENDER);

                int defid = player.getAttribOr(AttributeKey.WARRIORS_GUILD_CYCLOPS_ROOM_DEFENDER, -1);
                ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, defid);

                player.message("<col=804080>Cyclops' are currently dropping " + def.name.toLowerCase() + "s.");
                CyclopsRoom.handle_time_spent(player, false);
            }
        }
        player.unlock();
    }

    private static void set_item(Player player, int item) {
        player.putAttrib(AttributeKey.WARRIORS_GUILD_CYCLOPS_ROOM_DEFENDER, item);
    }
}
