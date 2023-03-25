package com.aelous.model.content.skill.impl.agility.course;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

/**
 * Created by Jak on 13/06/2016.
 */
public class Barbarian extends PacketInteraction {

    private static final int ENTRANCE_PIPE = 20210;
    private static final int ROPE_SWING = 23131;
    private static final int LOG_BALANCE = 23144;
    private static final int NET = 20211;
    private static final int LEDGE = 23547;
    private static final int LEDGE_LADDER = 42487;
    private static final int WALL = 1948;
    private static final int DUNGEONLADDER_DOWN = 16680;

    private void putStage(Player player, int stageBit) {
        int stage = player.getAttribOr(AttributeKey.BARBARIAN_COURSE_STATE, 0);
        stage = stage | stageBit;
        player.putAttrib(AttributeKey.BARBARIAN_COURSE_STATE, stage);
    }

    // TODO make formula include exponential success growth for levels over requirement ending at 70
    public boolean successful(Player player) {
        int req = 35;
        int endeffectiveness = 70;
        int gap = player.getSkills().xpLevel(Skills.AGILITY) - req;
        double failrate = 0.30;
        return Math.random() >= (failrate - ((failrate / (endeffectiveness - req)) * gap));
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == ENTRANCE_PIPE) {
                player.smartPathTo(new Tile(2552, 3561));
                if (player.getSkills().level(Skills.AGILITY) >= 35) {
                    player.waitForTile(new Tile(2552, 3561), () -> {
                        player.lockDelayDamage();
                        player.getMovementQueue().clear();
                        player.agilityWalk(false);
                        player.looks().render(749, -1);
                        Chain.bound(player).runFn(0, () -> {
                            player.agilityWalk(true);
                            ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -3), 30, 60, 749, 2);
                            player.setForceMovement(forceMovement);
                            player.looks().resetRender();
                        });
                    }).then(2, player::unlock);
                } else {
                    player.message("You need a agility level of 35 to enter this course.");
                }
                return true;
            }

            if (obj.getId() == ROPE_SWING) {
                if (!player.tile().equals(2551, 3554)) {
                    player.getMovementQueue().clear();
                    player.smartPathTo(new Tile(2551, 3554));
                }
                player.waitForTile(new Tile(2551, 3554, player.getZ()), () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, -5), 30, 60, 751, 2);
                    player.setForceMovement(forceMovement);
                    player.getPacketSender().sendObjectAnimation(obj, 54);
                    Chain.bound(player).runFn(1, () -> {
                        player.getSkills().addXp(Skills.AGILITY, 22.0);
                        putStage(player, 1);
                        player.getPacketSender().sendObjectAnimation(obj, 55);
                    }).then(player::unlock);
                });


                return true;
            }

            if (obj.getId() == LOG_BALANCE) {
                player.smartPathTo(new Tile(2551, 3546));
                player.waitForTile(new Tile(2551, 3546), () -> {
                    player.lockDelayDamage();
                    player.message("You walk carefully across the slippery log...");
                    player.agilityWalk(false);
                    player.getMovementQueue().clear();
                    player.getMovementQueue().step(2541, 3546, MovementQueue.StepType.FORCED_WALK);
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                }).then(1, () -> {
                    player.agilityWalk(true);
                    putStage(player, 2);
                }).then(10, () -> {
                    player.getSkills().addXp(Skills.AGILITY, 13.7);
                    player.message("...You make it safely to the other side.");
                    player.looks().resetRender();
                    player.unlock();
                });
                return true;
            }

            if (obj.getId() == NET) {
                if (player.tile().x < 2539) {
                    player.message("You can't climb the net from here.");
                    return true;
                }
                Ladders.ladderUp(player, player.tile().transform(-1, 0, 1), true);
                putStage(player, 4);
                player.getSkills().addXp(Skills.AGILITY, 8.2);
                return true;
            }

            if (obj.getId() == LEDGE) {
                player.smartPathTo(new Tile(2536, 3547, 1));
                player.waitForTile(new Tile(2536, 3547, 1), () -> {
                    player.lockDelayDamage();
                    player.animate(753);
                    player.agilityWalk(false);
                    player.getMovementQueue().clear();
                    player.getMovementQueue().step(2532, 3547, MovementQueue.StepType.FORCED_WALK);
                    player.looks().render(756, 756, 756, 756, 756, 756, -1);
                }).then(1, () -> {
                    player.agilityWalk(true);
                    putStage(player, 8);
                }).then(4, () -> {
                    player.getMovementQueue().step(2532, 3546, MovementQueue.StepType.FORCED_WALK);
                    player.getSkills().addXp(Skills.AGILITY, 22.0);
                    player.message("...You make it safely to the other side.");
                    player.looks().resetRender();
                    player.unlock();
                });
                return true;
            }

            if (obj.getId() == LEDGE_LADDER) {
                if (player.tile().getLevel() == 1) {
                    Ladders.ladderDown(player, player.tile().transform(0, 0, -1), true);
                }
            }

            if (obj.getId() == WALL) {
                if (obj.tile().equals(new Tile(2536, 3553))) {
                    player.smartPathTo(new Tile(2535, 3553));
                }
                if (obj.tile().equals(new Tile(2536, 3553)) || obj.tile().equals(new Tile(2539, 3553)) || obj.tile().equals(new Tile(2542, 3553))) {
                    player.waitForTile(obj.tile().transform(-1, 0, 0), () -> {
                        var end = obj.tile().equals(2542, 3553);
                        if (!player.tile().equals(obj.tile().transform(-1, 0, 0))) {
                            player.getMovementQueue().walkTo(obj.tile().transform(-1, 0, 0));
                        }
                        player.lockDelayDamage();
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(2, 0), 30, 60, 839, 1);
                        player.setForceMovement(forceMovement);
                        Chain.bound(player).name("BarbarianWallTask").runFn(1, () -> {
                            player.getSkills().addXp(Skills.AGILITY, 13.7);
                            player.unlock();
                            int stage = player.getAttribOr(AttributeKey.BARBARIAN_COURSE_STATE, 0);
                            if (end && stage == 15) {
                                player.putAttrib(AttributeKey.BARBARIAN_COURSE_STATE, 0);
                                player.getSkills().addXp(Skills.AGILITY, 46.2);
                            }
                        });
                    });
                }
                return true;
            }

            if (obj.getId() == DUNGEONLADDER_DOWN) {
                if (obj.tile().equals(new Tile(2884, 3397))) { // Taverly ldader
                    Ladders.ladderDown(player, new Tile(2884, 9798), true);
                } else if (obj.tile().equals(2547, 3551)) { // Barb ladder
                    Ladders.ladderDown(player, new Tile(2548, 9951), true);
                } else if (obj.tile().equals(3088, 3571)) { // air obelisk
                    Ladders.ladderDown(player, new Tile(player.tile().x, player.tile().y + 6400), true);
                } else {
                    player.message("This ladder does not seem to lead anywhere... Odd!");
                }
                return true;
            }
        }
        return false;
    }


}
