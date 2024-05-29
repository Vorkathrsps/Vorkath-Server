package com.cryptic.model.content.areas.varrock;

import com.cryptic.model.content.packet_actions.interactions.objects.Ladders;
import com.cryptic.model.cs2.impl.dialogue.DialogueManager;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

/**
 * @author Origin | April, 14, 2021, 19:20
 * 
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
                    return true;
                }
                return false;
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
