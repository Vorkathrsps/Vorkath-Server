package com.aelous.model.content.areas.varrock;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.object.doors.Door;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.map.object.doors.Doors.CACHE;

/**
 * @author Patrick van Elderen | April, 14, 2021, 19:20
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class CookingGuild extends PacketInteraction {

    private final static int CHEF = 2658;
    private final static int CHEFS_HAT = 1949;
    private final static int GOLDEN_CHEFS_HAT = 20205;
    public final static int GUILD_DOOR = 24958;
    private final static int FIRST_FLOOR_STAIRS = 2608;
    private final static int SECOND_FLOOR_STAIRS = 2609;
    private final static int THIRD_FLOOR_STAIRS = 2610;

    private static void door(Player player, GameObject door) {

        //Requirement checks to enter the cooking guild.
        if (player.tile().y <= 3443) {
            if (player.getSkills().level(Skills.COOKING) < 32) {
                DialogueManager.npcChat(player, Expression.DULL, CHEF, "Sorry. Only the finest chefs are allowed in here. Get", "your cooking level up to 32 and come back wearing a", "chef's hat.");
            } else if (!player.getEquipment().containsAny(CHEFS_HAT, GOLDEN_CHEFS_HAT)) {
                DialogueManager.npcChat(player, Expression.ANXIOUS, CHEF, "You can't come in here unless you're wearing a chef's", "hat, or something like that.");
            } else {
                if (!player.tile().equals(door.tile().transform(0, 0, 0))) {
                    player.getMovementQueue().interpolate(door.tile().transform(0, 0, 0));
                }

                //cached
                GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
                GameObject spawned = new GameObject(24959, new Tile(3143, 3444), door.getType(), 2);

                Chain.bound(player).name("CookingGuildDoor1Task").waitForTile(door.tile().transform(0, 0, 0), () -> {
                    player.lock();
                    ObjectManager.removeObj(old);
                    ObjectManager.addObj(spawned);
                    player.getMovementQueue().interpolate(3143, 3444, MovementQueue.StepType.FORCED_WALK);
                }).then(2, () -> {
                    ObjectManager.removeObj(spawned);
                    ObjectManager.addObj(old);
                }).waitForTile(new Tile(3143, 3444), player::unlock);
            }
        } else {
            if (!player.tile().equals(door.tile().transform(0, 1, 0))) {
                player.getMovementQueue().interpolate(door.tile().transform(0, 1, 0));
            }

            //cached
            GameObject old = new GameObject(door.getId(), door.tile(), door.getType(), door.getRotation());
            GameObject spawned = new GameObject(24959, new Tile(3143, 3444), door.getType(), 2);

            Chain.bound(player).name("CookingGuildDoor2Task").waitForTile(door.tile().transform(0, 1, 0), () -> {
                player.lock();
                ObjectManager.removeObj(old);
                ObjectManager.addObj(spawned);
                player.getMovementQueue().interpolate(3143, 3443, MovementQueue.StepType.FORCED_WALK);
            }).then(2, () -> {
                ObjectManager.removeObj(spawned);
                ObjectManager.addObj(old);
            }).waitForTile(new Tile(3143, 3443), player::unlock);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int interaction) {
        if (interaction == 1) {
            // This door ID is used elsewhere too!
            if (obj.getId() == GUILD_DOOR) {
                if (obj.tile().equals(3143, 3443)) {
                    CookingGuild.door(player, obj);
                } else {
                    Door door = CACHE.stream().filter(d -> d.id() == obj.getId()).findAny().orElse(null);
                    if (door == null)
                        return true;
                    door.open(obj, player,true);
                }
                return true;
            }
            //Staircase inside the cooking guild
            if (obj.getId() == FIRST_FLOOR_STAIRS) {
                if (obj.tile().equals(3144, 3447, 0)) {
                    Ladders.ladderUp(player, new Tile(player.tile().x, player.tile().y, player.tile().level + 1), false);
                }
                return true;
            }

            if (obj.getId() == SECOND_FLOOR_STAIRS) {
                Ladders.ladderUp(player, new Tile(3144, 3446, player.tile().level + 1), false);
                return true;
            }

            if (obj.getId() == THIRD_FLOOR_STAIRS) {
                if (obj.tile().equals(3144, 3447, 2)) {
                    Ladders.ladderDown(player, new Tile(3144, 3449, player.tile().level - 1), false);
                }
                return true;
            }
        } else if (interaction == 2) {
            if (obj.getId() == SECOND_FLOOR_STAIRS) {
                Ladders.ladderUp(player, new Tile(3144, 3446, player.tile().level + 1), false);
                return true;
            }
        } else if (interaction == 3) {
            if (obj.getId() == SECOND_FLOOR_STAIRS) {
                Ladders.ladderDown(player, new Tile(3144, 3449, player.tile().level - 1), false);
                return true;
            }
        }
        return false;
    }
}
