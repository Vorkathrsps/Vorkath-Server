package com.cryptic.model.content.areas.dungeons.karuulm_slayer_dungeon;

import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.ForceMovementTask;
import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstancedAreaManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.method.impl.npcs.hydra.AlchemicalHydra;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.Direction;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin | March, 04, 2021, 20:12
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class KaruulmSlayerDungeon extends PacketInteraction {

    private static final Area[] SLAYER_ONLY_AREA = {
        new Area(1251, 10147, 1279, 10170, 0), // Wyrms
        new Area(1300, 10255, 1336, 10277, 0), // Hydras
        new Area(1337, 10223, 1366, 10255, 1), // Drakes
    };

    private void handleBarrier(Player player, GameObject wall) {
        if (CombatFactory.inCombat(player)) {
            player.message("You can't leave this barrier while in combat.");
            return;
        }
        Chain.bound(null).runFn(1, () -> {
            player.lockDelayDamage();
            boolean east = wall.tile().x > player.getAbsX();
            TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(east ? 1 : -1, 0), 0, 50, east ? Direction.EAST.faceValue : Direction.WEST.faceValue)));
        }).then(2, player::unlock);
    }

    private static boolean isInSlayerOnlyArea(Player player) {
        for (Area area : SLAYER_ONLY_AREA) {
            if (player.tile().inArea(area)) {
                return true;
            }
        }
        return false;
    }

    private void climbWall(Player player) {
        if (player.isPerformingAction()) return;
        player.setPerformingAction(true);
        var x = player.tile().getX();
        var y = player.tile().getY();
        int transformX = x == 1303 ? -2 : x == 1301 ? 2 : 0;
        int transformY = y == 10214 ? 2 : y == 10216 ? -2 : 0;
        int face = transformX == -2 ? 3 : transformX == 2 ? 1 : transformY == 2 ? 0 : 4;
        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(transformX, transformY), 30, 45, 839, face);
        player.lock();
        player.agilityWalk(false);
        Chain.bound(player).runFn(1, () -> {
            player.lockDelayDamage();
            player.setForceMovement(forceMovement);
        }).then(2, () -> {
            player.unlock();
            player.agilityWalk(true);
            player.clearPerformingAction();
        });
    }

    private void jumpGap(Player player, GameObject gap) {
        Direction dir = player.getAbsY() > gap.tile().y ? Direction.SOUTH : Direction.NORTH;
        Chain.bound(null).runFn(1, () -> {
            player.lockDelayDamage();
            player.animate(3067);
            TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(dir.deltaX * 5, dir.deltaY * 5), 25, 65, dir.faceValue)));
        }).then(3, player::unlock);
    }

    private void crawlThroughTunnel(Player player, GameObject gap) {
        Direction dir = player.getAbsX() > gap.tile().x ? Direction.WEST : Direction.EAST;
        Chain.bound(null).runFn(1, () -> {
            player.lockDelayDamage();
            player.animate(2796);
            TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(dir.deltaX * 7, dir.deltaY * 7), 15, 85, dir.faceValue)));
        }).then(3, player::unlock);
    }

    /**
     * The doors to access the alchemical hydra.
     */
    private final List<Integer> alchemicalDoors = Arrays.asList(ALCHEMICAL_DOOR, ALCHEMICAL_DOOR_34554);

    public static final Area ALCHEMICAL_HYDRA_AREA = new Area(1356, 10257, 1377, 10278);
    public static final Tile ENTRANCE_POINT = new Tile(1356, 10258);
    public static final Tile HYDRA_SPAWN_TILE = new Tile(1364, 10265);

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == ELEVATOR) {
            //player.animate(798);
            player.teleport(1311, 10188);
            return true;
        }
        if (obj.getId() == MYSTERIOUS_PIPE) {
            if (player.getSkills().level(Skills.AGILITY) < 88) {
                player.message("You need a Agility level of 88 to enter this pipe.");
                return true;
            }
            player.animate(833);
            Chain.bound(null).runFn(2, () -> {
                if (obj.tile().equals(1346, 10231)) {
                    player.teleport(1315, 10214);
                } else {
                    player.teleport(1346, 10232);
                }
            });
            return true;
        }
        for (int alchemicalDoor : alchemicalDoors) {
            if (obj.getId() == alchemicalDoor) {
                boolean inside = player.getAbsX() >= 1356;
                player.lockMovement();
                Chain.bound(null).runFn(1, () -> { // and running a tick later bad .. if delaying tick have to lock
                    player.unlock();
                    if (!inside) {
                        player.getCombat().clearDamagers();
                        var instance = InstancedAreaManager.getSingleton().createInstancedArea(ALCHEMICAL_HYDRA_AREA);
                        player.setInstancedArea(instance);
                        player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));
                        var hydra = new AlchemicalHydra(HYDRA_SPAWN_TILE.transform(0, 0, instance.getzLevel()), player);
                        hydra.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 25);
                        Arrays.stream(hydra.closePlayers()).forEach(p -> {
                            HealthHud.open(p, HealthHud.Type.REGULAR, "Alchemical Hydra", 1100);
                        });
                        World.getWorld().registerNpc(hydra);
                        instance.addNpc(hydra);
                    } else {
                        player.teleport(1355, 10259);
                    }
                });
                return true;
            }
        }
        if (obj.getId() == STEPS_34530 && obj.tile().equals(1314, 10188, 1)) {
            player.teleport(1318, 10188, 2);
            return true;
        }
        if (obj.getId() == STEPS_34530 && obj.tile().equals(1330, 10205, 0)) {
            player.teleport(1334, 10205, 1);
            return true;
        }
        if (obj.getId() == STEPS_34530 && obj.tile().equals(1318, 10188, 2)) {
            player.teleport(1334, 10205, 1);
            return true;
        }
        if (obj.getId() == STEPS_34531 && obj.tile().equals(1330, 10205, 1)) {
            player.teleport(1329, 10205, 0);
            return true;
        }
        if (obj.getId() == STEPS_34531 && obj.tile().equals(1314, 10188, 2)) {
            player.teleport(1313, 10188, 1);
            return true;
        }
        if (obj.getId() == ENERGY_BARRIER && obj.tile().equals(1357, 10206, 1)) {
            handleBarrier(player, obj);
            return true;
        }
        if (obj.getId() == ENERGY_BARRIER && obj.tile().equals(1357, 10207, 1)) {
            handleBarrier(player, obj);
            return true;
        }
        if (obj.getId() == LAVA_GAP) {
            jumpGap(player, obj);
            return true;
        }
        if (obj.getId() == TUNNEL_34516) {
            crawlThroughTunnel(player, obj);
            return true;
        }
        if (obj.getId() == ROCKS_34544 || obj.getId() == ROCKS_34548) {
            climbWall(player);
            return true;
        }
        return false;
    }
}
