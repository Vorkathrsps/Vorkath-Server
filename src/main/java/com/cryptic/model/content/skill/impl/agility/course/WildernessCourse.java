package com.cryptic.model.content.skill.impl.agility.course;

import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.packet_actions.interactions.objects.Ladders;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.model.entity.attributes.AttributeKey.WILDY_COURSE_STATE;

/**
 * Created by Jak on 13/06/2016.
 */
public class WildernessCourse extends PacketInteraction {

    public final static int LOWER_GATE = 23555;
    public final static int UPPERGATE_EAST = 23552;
    public final static int UPPERGATE_WEST = 23554;
    private final static int PIPE = 23137;
    private final static int ROPESWING = 23132;
    private final static int LADDERDOWN = 14758;
    // ladder up is same as barb course
    private final static int STEPPINGSTONE = 23556;
    private final static int LOGBALANCE = 23542;
    private final static int ROCKS = 23640;

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == LOWER_GATE) {
                if (player.getSkills().level(Skills.AGILITY) < 52) {
                    player.message("You do not have the required level to enter this course.");
                    return true;
                }
                lowergate(player, obj);
                return true;
            }
            if (obj.getId() == UPPERGATE_EAST) {
                uppergate(player, obj);
                return true;
            }

            if (obj.getId() == UPPERGATE_WEST) {
                uppergate(player, obj);
                return true;
            }

            if (obj.getId() == PIPE) {
                player.smartPathTo(obj.tile());
                Chain.bound(player).name("WildernessCoursePipeTask").waitForTile(new Tile(3004, 3937), () -> {
                    player.lockDelayDamage();
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 6), 30, 60, 749, 0);
                    player.setForceMovement(forceMovement);
                }).then(3, () -> player.getMovementQueue().interpolate(player.tile().x, player.tile().y + 1)).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 7), 30, 60, 748, 0);
                    player.setForceMovement(forceMovement);
                }).then(3, () -> {
                    var stage = player.<Integer>getAttribOr(WILDY_COURSE_STATE, 0) + 1;
                    player.putAttrib(WILDY_COURSE_STATE, stage);
                    player.getSkills().addXp(Skills.AGILITY, 12.5);
                    player.unlock();
                });
                return true;
            }

            if (obj.getId() == ROPESWING) {
                player.smartPathTo(new Tile(3005, 3951));
                Chain.bound(player).waitForTile(new Tile(3005, 3951, player.getZ()), () -> {
                    player.getMovementQueue().step(3005, 3953, MovementQueue.StepType.FORCED_WALK);
                }).then(2, () -> {
                    player.waitForTile(new Tile(3005, 3953, player.getZ()), () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 5), 30, 60, 751, 0);
                        player.setForceMovement(forceMovement);
                        player.getPacketSender().sendObjectAnimation(obj, 54);
                        Chain.bound(player).runFn(1, () -> {
                            var stage = player.<Integer>getAttribOr(WILDY_COURSE_STATE, 0) + 1;
                            player.putAttrib(WILDY_COURSE_STATE, stage);
                            player.getSkills().addXp(Skills.AGILITY, 20.0);
                            player.getPacketSender().sendObjectAnimation(obj, 55);
                        }).then(player::unlock);
                    });
                });
                return true;
            }

            if (obj.getId() == LADDERDOWN) {
                Ladders.ladderDown(player, player.tile().transform(0, 6400), true);
                return true;
            }

            if (obj.getId() == STEPPINGSTONE) {
                player.smartPathTo(new Tile(3002, 3960));
                player.waitUntil(() -> player.tile().equals(3002, 3960), () -> {
                    player.getMovementQueue().step(3002, 3960, MovementQueue.StepType.FORCED_WALK);
                    player.lockDelayDamage();
                }).then(2, () -> {
                    Chain.bound(player).name("WildernessCourseRockJumping").runFn(1, () -> {
                        player.lockDelayDamage();
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 0), 0, 30, 741, 3);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 0), 0, 30, 741, 3);
                        player.setForceMovement(forceMovement);
                    }).then(3, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 0), 0, 30, 741, 3);
                        player.setForceMovement(forceMovement);
                    }).then(3, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 0), 0, 30, 741, 3);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 0), 0, 30, 741, 3);
                        player.setForceMovement(forceMovement);
                    }).then(2, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(-1, 0), 0, 30, 741, 3);
                        player.setForceMovement(forceMovement);
                        player.looks().resetRender();
                        var stage = player.<Integer>getAttribOr(WILDY_COURSE_STATE, 0) + 1;
                        player.putAttrib(WILDY_COURSE_STATE, stage);
                        player.getSkills().addXp(Skills.AGILITY, 20.0);
                        player.unlock();
                    });
                });
                return true;
            }

            if (obj.getId() == LOGBALANCE) {
                player.smartPathTo(new Tile(3002, 3945));
                player.waitForTile(new Tile(3002, 3945), () -> {
                    player.lockDelayDamage();
                    player.message("You walk carefully across the slippery log...");
                    player.agilityWalk(false);
                    player.getMovementQueue().clear();
                    if (!player.tile().equals(3002, 3945)) { // Get in position
                        player.getMovementQueue().step(3002, 3945, MovementQueue.StepType.FORCED_WALK);
                    }
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                }).then(1, () -> {
                    player.getMovementQueue().step(2994, 3945, MovementQueue.StepType.FORCED_WALK);
                    player.agilityWalk(true);
                    var stage = player.<Integer>getAttribOr(WILDY_COURSE_STATE, 0) + 1;
                    player.putAttrib(WILDY_COURSE_STATE, stage);
                }).then(8, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 20.0);
                    player.message("...You make it safely to the other side.");
                    player.looks().resetRender();
                    player.unlock();
                });
                return true;
            }

            if (obj.getId() == ROCKS) {
                player.waitUntil(1, () -> player.tile().y == 3937, () -> {
                    if (!player.tile().equals(2995, 3937)) { // Get in position
                        player.getMovementQueue().step(2995, 3937, MovementQueue.StepType.FORCED_WALK);
                        player.getMovementQueue().clear();
                    }
                    Chain.bound(player).name("WildernessCourseRockWallClimbing").runFn(1, () -> {
                        player.lockDelayDamage();
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 740, 2);
                        player.setForceMovement(forceMovement);
                    }).then(1, () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -2), 0, 30, 737, 2);
                        player.setForceMovement(forceMovement);
                    }).then(1, () -> {
                        var stage = player.<Integer>getAttribOr(WILDY_COURSE_STATE, 0) + 1;
                        player.putAttrib(WILDY_COURSE_STATE, stage);
                        player.getSkills().addXp(Skills.AGILITY, 498.9);
                        if (stage == 5) {
                            player.getTaskMasterManager().increase(Tasks.WILDERNESS_COURSE);
                            DailyTaskManager.increase(DailyTasks.WILDY_AGLITY, player);
                            if (WildernessArea.isInWilderness(player)) {
                                if (World.getWorld().rollDie(5, 1)) {
                                    if (player.inventory().add(new Item(13307, 5), true)) {
                                        player.message("You find 5 blood money coin on the ground.");
                                    }
                                }
                            }
                        }
                        player.putAttrib(WILDY_COURSE_STATE, 0);
                        player.unlock();
                    });
                });
                return true;
            }
        }
        return false;
    }

    // TODO make formula include exponential success growth for levels over requirement ending at 70
    public static boolean successful(Player player) {
        int req = 35;
        int endeffectiveness = 70;
        int gap = player.getSkills().xpLevel(Skills.AGILITY) - req;
        double failrate = 0.30;
        return Math.random() >= (failrate - ((failrate / (endeffectiveness - req)) * gap));
    }

    private static void uppergate(Player player, GameObject obj) {
        if (!player.tile().equals(obj.tile())) {
            player.getMovementQueue().walkTo(obj.tile());
        }

        if (obj.interactAble() && obj.tile().equals(2998, 3931)) {
            if (!player.tile().equals(2998, 3931)) { // Get in position
                player.getMovementQueue().step(2998, 3931, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().clear();
            }
            Chain.bound(player).waitForTile(new Tile(2998, 3931, 0), () -> {
                Chain.bound(player).runFn(1, () -> {
                    player.lockDelayDamage();
                    openDoubleDoors();
                }).then(1, () -> {
                    player.getMovementQueue().clear();
                    player.getMovementQueue().step(2998, 3916, MovementQueue.StepType.FORCED_WALK);
                    player.agilityWalk(false);
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                }).waitForTile(new Tile(2998, 3916, 0), () -> {
                    openEntranceGate(player, obj);
                }).then(1, () -> {
                    player.looks().resetRender();
                    player.agilityWalk(true);
                    player.unlock();
                });
            });
        }
    }

    private static void lowergate(Player player, GameObject obj) {
        if (player.getSkills().xpLevel(Skills.AGILITY) < 52) {
            player.message("You need a Agility level of 52 to pass this gate.");
            return;
        }

        if (obj.interactAble() && obj.tile().equals(2998, 3917)) {
            if (!player.tile().equals(2995, 3917)) { // Get in position
                player.getMovementQueue().step(2998, 3916, MovementQueue.StepType.FORCED_WALK);
                player.getMovementQueue().clear();
            }
            Chain.bound(player).waitForTile(new Tile(2998, 3916, 0), () -> {
                Chain.bound(player).runFn(1, () -> {
                    player.lockDelayDamage();
                    openEntranceGate(player, obj);
                }).then(1, () -> {
                    player.getMovementQueue().clear();
                    player.getMovementQueue().step(2998, 3931, MovementQueue.StepType.FORCED_WALK);
                    player.agilityWalk(false);
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                }).waitForTile(new Tile(2998, 3931, 0), WildernessCourse::openDoubleDoors).then(1, () -> {
                    player.looks().resetRender();
                    player.agilityWalk(true);
                    player.unlock();
                });
            });
        }
    }

    private static void openEntranceGate(Player player, GameObject obj) {
        obj.interactAble(false);
        ObjectManager.removeObj(obj);
        var newobj = new GameObject(1548, new Tile(2997, 3917), obj.getType(), 2).interactAble(false);
        ObjectManager.addObj(newobj);
        Chain.bound(player).name("openEntranceGateTask").runFn(2, () -> {
            // Put the old door back
            ObjectManager.removeObj(newobj);
            ObjectManager.addObj(obj);
            obj.interactAble(true);
        });
    }

    private static void openDoubleDoors() {
        GameObject openDoor1 = new GameObject(23552, new Tile(2998, 3931, 0), 0, 3);
        GameObject rotateDoor1 = new GameObject(23552, new Tile(2998, 3931, 0), 0, 2);
        ObjectManager.replace(openDoor1, rotateDoor1, 3);

        GameObject openDoor2 = new GameObject(23554, new Tile(2997, 3931, 0), 0, 3);
        GameObject rotateDoor2 = new GameObject(23554, new Tile(2997, 3931, 0), 0, 4);
        ObjectManager.replace(openDoor2, rotateDoor2, 3);
    }
}
