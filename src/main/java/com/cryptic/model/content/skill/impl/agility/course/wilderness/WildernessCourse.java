package com.cryptic.model.content.skill.impl.agility.course.wilderness;

import com.cryptic.model.content.packet_actions.interactions.objects.Ladders;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.List;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.AGILITY_DISPENSER;

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

    final List<Item> itemList = List.of(
        new Item(ItemIdentifiers.BLIGHTED_ANGLERFISH + 1, 5),
        new Item(ItemIdentifiers.BLIGHTED_MANTA_RAY + 1, 5),
        new Item(ItemIdentifiers.BLIGHTED_KARAMBWAN + 1, 5),
        new Item(ItemIdentifiers.BLIGHTED_SUPER_RESTORE4 + 1, 5),
        new Item(ItemIdentifiers.CLUE_SCROLL_MEDIUM, 2),
        new Item(ItemIdentifiers.RUNE_MED_HELM + 1, 2),
        new Item(ItemIdentifiers.RUNE_PLATELEGS + 1, 2),
        new Item(ItemIdentifiers.RUNE_KITESHIELD + 1, 2),
        new Item(ItemIdentifiers.RUNE_PLATEBODY + 1, 2),
        new Item(ItemIdentifiers.RUNE_CHAINBODY + 1, 2),
        new Item(ItemIdentifiers.DRAGON_SCIMITAR + 1, 2),
        new Item(ItemIdentifiers.COINS_995, 50_000),
        new Item(ItemIdentifiers.WILDERNESS_AGILITY_TICKET, 1)
    );

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 3) {
            if (obj.getId() == AGILITY_DISPENSER) {
                if (!player.getInventory().contains(ItemIdentifiers.WILDERNESS_AGILITY_TICKET)) {
                    player.sendInformationMessage("You do not have any Wilderness Agility Tickets to dispense.");
                    return true;
                }
                if (player.isPerformingAction()) return true;
                if (player.getInventory().contains(ItemIdentifiers.WILDERNESS_AGILITY_TICKET)) {
                    player.setPerformingAction(true);
                    final int experience = 200;
                    final int count = player.getInventory().count(ItemIdentifiers.WILDERNESS_AGILITY_TICKET);
                    double total = count * experience;
                    total = getKaramjaGloveBoost(player, total);
                    player.getInventory().remove(ItemIdentifiers.WILDERNESS_AGILITY_TICKET, count);
                    player.getSkills().addXp(Skills.AGILITY, total);
                    Chain.noCtx().runFn(1, player::clearPerformingAction);
                    return true;
                }
            }
        }
        if (option == 1) {
            if (obj.getId() == AGILITY_DISPENSER) {
                if (player.getWildernessAgilityLoot().isEmpty()) {
                    player.sendInformationMessage("You currently have no loot to claim from the Agility Dispenser.");
                    return true;
                }
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().addOrDrop(player.getWildernessAgilityLoot());
                player.getWildernessAgilityLoot().clear();
                Chain.noCtx().runFn(1, player::clearPerformingAction);
                return true;
            }
            if (obj.getId() == PIPE) {
                player.smartPathTo(obj.tile());
                Chain.bound(player).name("WildernessCoursePipeTask").waitForTile(new Tile(3004, 3937), () -> {
                    player.lockDelayDamage();
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 6), 30, 60, 749, 0);
                    player.setForceMovement(forceMovement);
                }).then(1, () -> {
                    player.looks().hideLooks(true);
                }).then(1, () -> player.getMovementQueue().interpolate(player.tile().x, player.tile().y + 1)).then(1, () -> {
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 7), 30, 60, 748, 0);
                    player.setForceMovement(forceMovement);
                }).then(2, () -> {
                    player.looks().hideLooks(false);
                    player.animate(748);
                }).then(2, () -> {
                    int stage = getStage(player, 0);
                    player.putAttrib(AttributeKey.WILDERNESS_COURSE_STAGE, stage);
                    player.getSkills().addXp(Skills.AGILITY, 12.5);
                    player.unlock();
                });
                return true;
            }

            if (obj.getId() == ROPESWING) {
                player.smartPathTo(new Tile(3005, 3951));
                Chain.bound(player).waitForTile(new Tile(3005, 3951, player.getZ()), () -> {
                    player.lockDelayDamage();
                    player.getMovementQueue().step(3005, 3953, MovementQueue.StepType.FORCED_WALK);
                }).then(2, () -> {
                    player.waitForTile(new Tile(3005, 3953, player.getZ()), () -> {
                        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(0, 5), 30, 60, 751, 0);
                        player.setForceMovement(forceMovement);
                        player.getPacketSender().sendObjectAnimation(obj, 54);
                        Chain.bound(player).runFn(1, () -> {
                            int stage = getStage(player, 1);
                            player.putAttrib(AttributeKey.WILDERNESS_COURSE_STAGE, stage);
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
                    player.lockDelayDamage();
                    player.getMovementQueue().step(3002, 3960, MovementQueue.StepType.FORCED_WALK);
                }).then(1, () -> {
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
                        player.getSkills().addXp(Skills.AGILITY, 20.0);
                        player.unlock();
                        int stage = getStage(player, 2);
                        player.putAttrib(AttributeKey.WILDERNESS_COURSE_STAGE, stage);
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
                }).then(8, () -> {
                    int stage = getStage(player, 3);
                    player.putAttrib(AttributeKey.WILDERNESS_COURSE_STAGE, stage);
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
                        int stage = getStage(player, 4);
                        player.putAttrib(AttributeKey.WILDERNESS_COURSE_STAGE, stage);
                        player.getSkills().addXp(Skills.AGILITY, 498.9);
                        if (stage == 5) {
                            player.getTaskMasterManager().increase(Tasks.WILDERNESS_COURSE);
                            if (WildernessArea.isInWilderness(player)) {
                                addRandomItemToDispenser(player);
                                if (World.getWorld().rollDie(5, 1)) {
                                    if (player.inventory().add(new Item(13307, 5), true)) {
                                        player.message("You find 5 blood money coin on the ground.");
                                    }
                                }
                            }
                        }
                        player.putAttrib(AttributeKey.WILDERNESS_COURSE_STAGE, 0);
                        player.unlock();
                    });
                });
                return true;
            }
        }
        return false;
    }

    double getKaramjaGloveBoost(Player player, double total) {
        if (player.getEquipment().containsAny(ItemIdentifiers.KARAMJA_GLOVES_1, ItemIdentifiers.KARAMJA_GLOVES_2,ItemIdentifiers.KARAMJA_GLOVES_3,ItemIdentifiers.KARAMJA_GLOVES_4) || player.getInventory().containsAny(ItemIdentifiers.KARAMJA_GLOVES_1, ItemIdentifiers.KARAMJA_GLOVES_2,ItemIdentifiers.KARAMJA_GLOVES_3,ItemIdentifiers.KARAMJA_GLOVES_4)) {
            total *= 1.10D;
        }
        return total;
    }

    private int getStage(Player player, int step) {
        int stage = player.<Integer>getAttribOr(AttributeKey.WILDERNESS_COURSE_STAGE, 0);
        if (stage == step) {
            stage = player.<Integer>getAttribOr(AttributeKey.WILDERNESS_COURSE_STAGE, 0) + 1;
        }
        return stage;
    }

    private void addRandomItemToDispenser(Player player) {
        var randomItem = Utils.randomElement(itemList);
        player.getWildernessAgilityLoot().add(randomItem);
    }

    public static boolean successful(Player player) {
        int req = 35;
        int endeffectiveness = 70;
        int gap = player.getSkills().xpLevel(Skills.AGILITY) - req;
        double failrate = 0.30;
        return Math.random() >= (failrate - ((failrate / (endeffectiveness - req)) * gap));
    }
}
