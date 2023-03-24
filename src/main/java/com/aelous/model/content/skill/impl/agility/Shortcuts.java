package com.aelous.model.content.skill.impl.agility;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date march 07, 2020 16:42
 */
public class Shortcuts extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == STEPPING_STONE_14917 && obj.tile().equals(3092, 3879)) {
            if (player.getSkills().level(Skills.AGILITY) < 82) {
                player.message("You need a Agility level of 82 to use this shortcut.");
                return true;
            }

            if (player.tile().equals(3091, 3882)) {
                player.lockDelayDamage();

                Chain.bound(player).name("Shortcut3Task").runFn(2, () -> {
                    player.animate(741);
                    TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(+1, -2), 5, 35, 4, 741)));
                }).then(2, () -> {
                    player.animate(741);
                    TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(+1, -1), 5, 35, 4, 741)));
                }).waitForTile(new Tile(3093, 3879), player::unlock);
            } else if (player.tile().equals(3093, 3879)) {
                if (player.getSkills().level(Skills.AGILITY) < 82) {
                    player.message("You need a Agility level of 82 to use this shortcut.");
                    return true;
                }
                player.lockDelayDamage();
                Chain.bound(player).name("Shortcut4Task").runFn(2, () -> {
                    player.animate(741);
                    TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-1, +1), 5, 35, 4, 741)));
                }).then(2, () -> {
                    player.animate(741);
                    TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-1, +2), 5, 35, 4, 741)));
                }).waitForTile(new Tile(3091, 3882), player::unlock);
            }
            return true;
        }

        if (obj.getId() == PIPE_21727) {
            // South side
            if (obj.tile().equals(2698, 9493)) {
                if (player.tile().equals(2698, 9492)) {
                    player.teleport(new Tile(2698, 9500));
                }
            }

            // North side
            if (obj.tile().equals(2698, 9498)) {
                if (player.tile().equals(2698, 9500)) {
                    player.teleport(new Tile(2698, 9492));
                }
            }
            return true;
        }

        // Al Kharid mine shortcut exit
        if (obj.getId() == ROCKS_16550) {
            if (player.getSkills().level(Skills.AGILITY) < 38) {
                player.message("You need an agility level of 38 to negotiate these rocks.");
            } else {
                TaskManager.submit(new TickAndStop(1) {
                    @Override
                    public void executeAndStop() {
                        player.looks().render(738, 737, 737, 737, 737, 737, -1);
                        player.getMovementQueue().clear();
                        player.getMovementQueue().interpolate(3305, 3315);
                    }
                });

                TaskManager.submit(new TickAndStop(2) {
                    @Override
                    public void executeAndStop() {

                    }
                });

                TaskManager.submit(new TickAndStop(3) {
                    @Override
                    public void executeAndStop() {

                    }
                });

                TaskManager.submit(new TickAndStop(5) {
                    @Override
                    public void executeAndStop() {
                        player.looks().resetRender();
                        player.getMovementQueue().clear();
                        player.getMovementQueue().interpolate(3306, 3315);
                    }
                });
            }
            return true;
        }

        // Fence between lumb and varrock
        if (obj.getId() == FENCE_16518) {
            if (player.tile().equals(3240, 3334)) {
                player.teleport(player.tile().transform(0, 1, 0));
            } else if (player.tile().equals(3240, 3335)) {
                player.teleport(player.tile().transform(0, -1, 0));
            }
            return true;
        }

        // Fally crumbled wall
        if (obj.getId() == CRUMBLING_WALL_24222) {
            if (player.tile().equals(2936, 3355)) {
                player.teleport(player.tile().transform(-1, 0, 0));
            } else if (player.tile().equals(2935, 3355)) {
                player.teleport(player.tile().transform(1, 0, 0));
            }
            return true;
        }

        // Rope swing orges outside yanille west
        if (obj.getId() == ROPESWING_23570) {
            if (player.tile().equals(2511, 3096)) {
                player.teleport(new Tile(2511, 3089));
            } else if (player.tile().equals(2511, 3089)) {
                player.teleport(new Tile(2511, 3096));
            }
            return true;
        }

        // Fally wall climb, inside to up
        if (obj.getId() == WALL_17049) {
            if (player.tile().equals(3033, 3389)) {
                player.teleport(new Tile(3032, 3389, 1));
            }
            // Fally wall climb, down to up outside
            else if (player.tile().y == 3390) {
                player.teleport(new Tile(3033, 3389, 1));
            }
            return true;
        }

        // Fally wall climb, up to down inside
        if (obj.getId() == WALL_17052) {
            if (player.tile().equals(3032, 3389, 1)) {
                player.teleport(new Tile(3033, 3389, 0));
            }
            return true;
        }

        // Fally wall climb, up to down outside
        if (obj.getId() == WALL_17051) {
            if (player.tile().equals(3033, 3389, 1)) {
                player.teleport(new Tile(3033, 3390, 0));
            }
            return true;
        }

        // Fally wall climb south side to up
        if (obj.getId() == WALL_17050) {
            if (player.tile().equals(3032, 3388, 0)) {
                player.teleport(new Tile(3032, 3389, 1));
            }
            return true;
        }

        // Fred farmer Snew Position jump lumbridge
        if (obj.getId() == STILE_12982) {
            if (player.tile().equals(3197, 3276)) { // Verify it's the right object.
                if (player.tile().y == 3276 || player.tile().y == 3275) {
                    // Verify loc.
                    player.teleport(new Tile(3197, 3278));
                } else if (player.tile().equals(3197, 3278)) {
                    player.teleport(new Tile(3197, 3275));
                }
            }
            return true;
        }

        // Edge dungeon bars
        if (obj.getId() == MONKEYBARS_23566) {
            edge_dungeon_monkeybars(player);
            return true;
        }

        // Watchtower hole under wall
        if (obj.getId() == HOLE_16520) {
            if (player.tile().equals(2575, 3112)) {
                player.teleport(player.tile().transform(0, -5, 0));
            } else if (player.tile().equals(2575, 3107)) {
                player.teleport(player.tile().transform(0, 5, 0));
            }
            return true;
        }

        // Watchtower wall climb
        if (obj.getId() == TRELLIS_20056) {
            if (player.tile().equals(2548, 3119)) {
                player.teleport(2548, 3118, 1);
            }
            return true;
        }

        // Log balance west of seers coal trucks
        if (obj.getId() == LOG_BALANCE_23274) {
            if (obj.tile().equals(2602, 3477)) { //; Verify it's the right object haha.
                if (player.tile().x >= 2602) {
                    player.teleport(2598, 3477);
                } else if (player.tile().x <= 2599) {
                    player.teleport(2603, 3477);
                }
            }
            return true;
        }

        // GE under wall tunnel
        if (obj.getId() == UNDERWALL_TUNNEL_16529) {
            player.getMovementQueue().walkTo(obj.tile().transform(-1, 0, 0));
            player.waitForTile(obj.tile().transform(-1, 0, 0), () -> {
                player.lockDelayDamage();
                player.animate(2589, 0);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(1, 0), 0, 50, 2, 2589)));
            }).then(2, () -> {
                player.animate(2590, 0);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(3, -3), 0, 100, 2, 2590)));
            }).then(5, () -> {
                player.animate(2591, 0);
                TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(1, 0), 15, 33, 2, 2591)));
                player.unlock();
            });
            return true;
        }

        // GE under wall
        if (obj.getId() == UNDERWALL_TUNNEL_16530) {
            player.getMovementQueue().walkTo(obj.tile().transform(1, 0, 0));
            player.waitForTile(obj.tile().transform(1, 0, 0), () -> {
                player.lockDelayDamage();
                player.animate(2589, 0);
               // TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-1, 0), 0, 50, FaceDirection.WEST)));
            }).then(2, () -> {
                player.animate(2590, 0);
               // TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-3, 3), 0, 100, FaceDirection.WEST)));
            }).then(5, () -> {
                player.animate(2591, 0);
              //  TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(-1, 0), 15, 33, FaceDirection.WEST)));
                player.unlock();
            });
            return true;
        }

        // Eagles peak rock climb
        if (obj.getId() == ROCKS_19849) {
            if (obj.tile().equals(2322, 3501)) {
                if (player.tile().equals(2322, 3502)) {
                    player.teleport(2324, 3497);
                } else if (player.tile().equals(2324, 3497)) {
                    player.teleport(2322, 3502);
                }
            }
            return true;
        }

        // Falador hole under wall
        if (obj.getId() == UNDERWALL_TUNNEL_16528) {
            if (player.tile().equals(2948, 3313)) {
                player.teleport(player.tile().transform(0, -4, 0));
            } else if (player.tile().equals(2948, 3309)) {
                player.teleport(player.tile().transform(0, 4, 0));
            }
            return true;
        }

        // Stepping stones east side of draynor manor
        if (obj.getId() == STEPPING_STONE_16533) {
            if (player.tile().equals(3154, 3363)) {
                player.teleport(player.tile().transform(-5, 0, 0));
            } else if (player.tile().equals(3149, 3363)) {
                player.teleport(player.tile().transform(5, 0, 0));
            }
            return true;
        }

        // White wolf mountain grapple to catherby
        if (obj.getId() == ROCKS_17042) {
            if (player.tile().equals(2869, 3430)) {
                player.teleport(2866, 3429);
            }
            return true;
        }

        // Cairn isle rock slide climb
        if (obj.getId() == ROCKS_2231) {
            if (player.tile().x == 2795) {
                player.teleport(player.tile().transform(-4, 0, 0));
            }
            return true;
        }

        // East ardy log balance
        if (obj.getId() == LOG_BALANCE_16548) {
            if (obj.tile().equals(2601, 3336)) {
                if (player.tile().equals(2602, 3336)) {
                    player.teleport(player.tile().transform(-4, 0, 0));
                } else if (player.tile().equals(2598, 3336)) {
                    player.teleport(player.tile().transform(4, 0, 0));
                }
            }
            return true;
        }

        // Grand tree climb shortcut north side from waterfall
        if (obj.getId() == ROCKS_16535) {
            if (obj.tile().equals(2489, 3520)) {
                if (player.tile().equals(2489, 3521)) {
                    player.teleport(2486, 3515);
                }
            }
            return true;
        }

        // Grand tree climb shortcut north side from waterfall
        if (obj.getId() == ROCKS_16534) {
            if (obj.tile().equals(2487, 3515)) {
                if (player.tile().equals(2486, 3515)) {
                    player.teleport(2489, 3521);
                }
            }
            return true;
        }

        // Rock climb al kharid mining pit
        if (obj.getId() == ROCKS_16549) {
            if (obj.tile().equals(3305, 3315)) {
                if (player.tile().distance(obj.tile()) < 2) {
                    player.teleport(3302, 3315);
                }
            }
            return true;
        }

        // Yanille south wall climb, outside up
        if (obj.getId() == WALL_17047) {
            if (player.tile().equals(2556, 3072)) {
                player.teleport(new Tile(2556, 3073, 1));
            }
            if (obj.tile().equals(2556, 3075)) {
                player.teleport(2556, 3074, 1);
            }
            return true;
        }

        // Yanille south wall climb, up to outside
        if (obj.getId() == WALL_17048) {
            if (obj.tile().equals(2556, 3072)) {
                if (player.tile().equals(2556, 3073, 1)) {
                    player.teleport(new Tile(2556, 3072));
                }
            }
            if (obj.tile().equals(2556, 3075)) {
                if (player.tile().equals(2556, 3074, 1)) {
                    player.teleport(new Tile(2556, 3075));
                }
            }
            return true;
        }

        // Crevice shortcut falador mines
        if (obj.getId() == CREVICE_16543) {
            // East side
            if (obj.tile().equals(3034, 9806)) {
                if (player.tile().equals(3035, 9806)) {
                    player.teleport(new Tile(3028, 9806));
                }
            }
            // West side
            if (obj.tile().equals(3029, 9806)) {
                if (player.tile().equals(3028, 9806)) {
                    player.teleport(new Tile(3035, 9806));
                }
            }
            return true;
        }

        // draynor under wall
        if (obj.getId() == UNDERWALL_TUNNEL_19036) {
            // East side
            if (obj.tile().equals(3069, 3260)) {
                player.teleport(new Tile(3065, 3260));
            }
            return true;
        }

        // draynor under wall
        if (obj.getId() == UNDERWALL_TUNNEL_19032) {
            // West side
            if (obj.tile().equals(3066, 3260)) {
                player.teleport(new Tile(3070, 3260));
            }
            return true;
        }

        // zanaris wall
        if (obj.getId() == JUTTING_WALL_17002) {
            if (obj.tile().equals(2400, 4404)) {
                player.teleport(player.tile().transform(0, -2, 0));
            }
            if (obj.tile().equals(2400, 4402)) {
                player.teleport(player.tile().transform(0, 2, 0));
            }

            if (obj.tile().equals(2415, 4403)) {
                player.teleport(player.tile().transform(0, -2, 0));
            }
            if (obj.tile().equals(2415, 4401)) {
                player.teleport(player.tile().transform(0, 2, 0));
            }

            if (obj.tile().equals(2408, 4396)) {
                player.teleport(player.tile().transform(0, -2, 0));
            }
            if (obj.tile().equals(2408, 4394)) {
                player.teleport(player.tile().transform(0, 2, 0));
            }
            return true;
        }

        // yanille dungeon monkey bars
        if (obj.getId() == MONKEYBARS_23567) {
            if (obj.tile().equals(2598, 9489) || obj.tile().equals(2597, 9489)) {
                player.teleport(player.tile().transform(0, 7, 0));
            }
            if (obj.tile().equals(2598, 9494) || obj.tile().equals(2597, 9494)) {
                player.teleport(player.tile().transform(0, -7, 0));
            }
            return true;
        }

        // yanille dungeon pipe
        if (obj.getId() == OBSTACLE_PIPE_23140) {
            if (obj.tile().equals(2576, 9506)) {
                player.teleport(2572, 9506);
            }
            if (obj.tile().equals(2573, 9506)) {
                player.teleport(2578, 9506);
            }
            return true;
        }

        // yanille balance log
        if (obj.getId() == BALANCING_LEDGE_23548) {
            if (obj.tile().equals(2580, 9519)) {
                player.teleport(2580, 9512);
            }
            if (obj.tile().equals(2580, 9512)) {
                player.teleport(2580, 9520);
            }
            return true;
        }

        // edge dungeon pipe
        if (obj.getId() == OBSTACLE_PIPE_16511) {
            if (obj.tile().equals(3150, 9906)) {
                player.teleport(3149, 9906);
            }
            if (obj.tile().equals(3149, 9906)) {
                player.teleport(3155, 9906);
            }
            return true;
        }

        // Trollhiem - GWD boulder enterance
        if (obj.getId() == BOULDER_26415) {

            if (obj.tile().equals(2898, 3716)) {
                if (player.tile().y == 3716) {
                    player.teleport(player.tile().transform(0, 3, 0));
                }
                if (player.tile().y == 3719) {
                    player.teleport(player.tile().transform(0, -3, 0));
                }
            }
            return true;
        }

        // Trollheim - GWD crevice entrance
        if (obj.getId() == LITTLE_CRACK) {
            if (obj.tile().equals(2900, 3713)) {
                player.teleport(2904, 3720);
            }
            if (obj.tile().equals(2904, 3719)) {
                player.teleport(2899, 3713);
            }
            return true;
        }

        // Fremennick slayer dungeon
        if (obj.getId() == STRANGE_FLOOR_16544) {
            if (obj.tile().equals(2774, 10003)) {
                if (player.tile().x == 2775) {
                    player.teleport(player.tile().transform(-2, 0, 0));
                } else if (player.tile().x == 2773) {
                    player.teleport(player.tile().transform(2, 0, 0));
                }
            }

            if (obj.tile().equals(2769, 10002)) {
                if (player.tile().x == 2770) {
                    player.teleport(player.tile().transform(-2, 0, 0));
                } else if (player.tile().x == 2768) {
                    player.teleport(player.tile().transform(2, 0, 0));
                }
            }
            return true;
        }

        // Fremennick slayer dungeon
        if (obj.getId() == CREVICE_16539) {
            if (obj.tile().equals(2734, 10008)) {
                if (player.tile().x == 2735) {
                    player.teleport(player.tile().transform(-5, 0, 0));
                } else if (player.tile().x == 2730) {
                    player.teleport(player.tile().transform(5, 0, 0));
                }
            }
            return true;
        }

        // Temple east side of varrock shortcut
        if (obj.getId() == ORNATE_RAILING) {
            if (obj.tile().equals(3424, 2476)) {
                if (player.tile().x == 3423) {
                    player.teleport(player.tile().transform(1, 0, 0));
                } else if (player.tile().x == 3424) {
                    player.teleport(player.tile().transform(-1, 0, 0));
                }
            }
            return true;
        }

        // Temple east side of varrock shortcut
        if (obj.getId() == ROCKS_16999) {
            if (obj.tile().equals(3426, 3478)) {
                player.teleport(3424, 3476);
            }
            return true;
        }

        // Temple east side of varrock shortcut
        if (obj.getId() == ROCKS_16998) {
            if (obj.tile().equals(3425, 3476)) {
                player.teleport(3426, 3478);
            }
            return true;
        }
        return false;
    }

    private static void edge_dungeon_monkeybars(Player player) {
        if (player.tile().y == 9963) {
            player.teleport(player.tile().transform(0, 7, 0));
        } else if (player.tile().y == 9970) {
            player.teleport(player.tile().transform(0, -7, 0));
        }
    }
}
