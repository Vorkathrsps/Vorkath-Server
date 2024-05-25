package com.cryptic.model.content.members;

import com.cryptic.GameServer;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.inter.clan.Clan;
import com.cryptic.model.inter.clan.ClanRepository;
import com.cryptic.model.content.instance.InstancedAreaManager;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.World;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

import java.util.Arrays;

import static com.cryptic.utility.CustomNpcIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin
 * mei 19, 2020
 */
public class MemberZone extends PacketInteraction {

    public static boolean canAttack(Entity attacker, Entity target) {
        if (attacker.isPlayer() && target.isNpc()) {
            Player player = (Player) attacker;
            NPC npc = (NPC) target;

            var elite_member = player.getMemberRights().isEliteMemberOrGreater(player);
            var extreme_member = player.getMemberRights().isExtremeMemberOrGreater(player);

            //Make sure we're in the member zone
            if (player.tile().memberCave()) {
                var isRevenant = npc.def().name.toLowerCase().contains("revenant");
                var isMember = player.getMemberRights().isRegularMemberOrGreater(player);
                if(isRevenant && !isMember) {
                    player.getCombat().reset();
                    player.message(Color.RED.wrap("You need to be at least a Ruby member to attack revenants."));
                    return false;
                }

                if((npc.id() == ANCIENT_REVENANT_DARK_BEAST || npc.id() == ANCIENT_REVENANT_ORK || npc.id() == ANCIENT_REVENANT_CYCLOPS || npc.id() == ANCIENT_REVENANT_DRAGON || npc.id() == ANCIENT_REVENANT_KNIGHT) && !elite_member) {
                    player.getCombat().reset();
                    player.message(Color.RED.wrap("You need to be at least a Emerald member to attack ancient revenants."));
                    return false;
                }

                if((npc.id() == ANCIENT_BARRELCHEST || npc.id() == ANCIENT_CHAOS_ELEMENTAL || npc.id() == ANCIENT_KING_BLACK_DRAGON) && !extreme_member) {
                    player.getCombat().reset();
                    player.message(Color.RED.wrap("You need to be at least a Diamond member to attack ancient bosses."));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == CAVE_ENTRANCE_31606 || obj.getId() == PORTAL_OF_LEGENDS) {
                if (!player.getMemberRights().isRegularMemberOrGreater(player)) {
                    player.message(Color.RED.wrap("You need to be at least an Ruby Member to enter the member zone."));
                    return true;
                }

                Tile tile = new Tile(2335, 9795);

                if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
                    return true;
                }

                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendStatement("This teleport will send you to a dangerous area.", "Do you wish to continue?");
                        setPhase(1);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(1)) {
                            sendOption(DEFAULT_OPTION_TITLE, "Yes.", "No.");
                            setPhase(2);
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (option == 1) {
                            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                stop();
                                return;
                            }
                            Teleports.basicTeleport(player, tile);
                        } else if (option == 2) {
                            stop();
                        }
                    }
                });
                return true;
            }
            if (obj.getId() == EXIT_30844) {
                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                if(player.getMemberRights().isRegularMemberOrGreater(player)) {
                    Teleports.basicTeleport(player, new Tile(2457, 2858));
                } else {
                    Teleports.basicTeleport(player, GameServer.settings().getHomeTile());
                }
                return true;
            }
            if (obj.getId() == PORTAL_OF_HEROES) {
                Tile tile = new Tile(3299, 3918);

                if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
                    return true;
                }

                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendStatement("This teleport will send you to a dangerous area.", "Do you wish to continue?");
                        setPhase(1);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(1)) {
                            sendOption(DEFAULT_OPTION_TITLE, "Yes.", "No.");
                            setPhase(2);
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (option == 1) {
                            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                stop();
                                return;
                            }
                            Teleports.basicTeleport(player, tile);
                            player.message("You have been teleported to level 50 wilderness.");
                        } else if (option == 2) {
                            stop();
                        }
                    }
                });
                return true;
            }
            if (obj.getId() == PORTAL_OF_CHAMPIONS) {
                Tile tile = new Tile(3287, 3884);

                if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
                    return true;
                }

                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendStatement("This teleport will send you to a dangerous area.", "Do you wish to continue?");
                        setPhase(1);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(1)) {
                            sendOption(DEFAULT_OPTION_TITLE, "Yes.", "No.");
                            setPhase(2);
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (option == 1) {
                            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                stop();
                                return;
                            }
                            Teleports.basicTeleport(player, tile);
                            player.message("You have been teleported to level 50 wilderness.");
                        } else if (option == 2) {
                            stop();
                        }
                    }
                });
                return true;
            }
            if (obj.getId() == PORTAL_34752) {
                if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                    Teleports.basicTeleport(player, GameServer.settings().getHomeTile());
                }
                return true;
            }
            if (obj.getId() == STAIRS_31627) {
                if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                    if (player.getClanChat() != null) {
                        Clan clan = ClanRepository.get(player.getClanChat());

                        if (clan != null) {
                            if (clan.meetingRoom == null) {
                                clan.meetingRoom = InstancedAreaManager.getSingleton().createInstancedArea(new Area(1, 2, 3, 4));
                                NPC pvpDummy = new NPC(NpcIdentifiers.UNDEAD_COMBAT_DUMMY, new Tile(2454, 2846, 2 + clan.meetingRoom.getzLevel()));
                                pvpDummy.spawnDirection(1);
                                NPC slayerDummy = new NPC(NpcIdentifiers.COMBAT_DUMMY, new Tile(2454, 2848, 2 + clan.meetingRoom.getzLevel()));
                                slayerDummy.spawnDirection(6);
                                clan.dummys = Arrays.asList(pvpDummy, slayerDummy);
                                World.getWorld().registerNpc(clan.dummys.get(0));
                                World.getWorld().registerNpc(clan.dummys.get(1));
                            }

                            Teleports.basicTeleport(player, new Tile(2452, 2847, 2 + clan.meetingRoom.getzLevel()));
                            player.message("You teleport to the " + player.getClanChat() + " clan outpost.");
                        }
                    }
                }
                return true;
            }
            if (obj.getId() == STAIRS_31610) {
                if (!player.getMemberRights().isRegularMemberOrGreater(player)) {
                    player.message(Color.RED.wrap("You need to be at least an Member to enter the member zone."));
                    return true;
                }
                if (Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                    Teleports.basicTeleport(player, new Tile(2457, 2858));
                }
                return true;
            }
            if (obj.getId() == PILLAR_31561) {
                player.smartPathTo(obj.tile());
                // lazy wait until we stop moving
                player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> {
                    if (obj.tile().equals(2356, 9841)) {
                        if (player.getSkills().level(Skills.AGILITY) < 91) {
                            player.message("You need an agility level of at least 91 to jump this pillar.");
                        } else {
                            if (player.tile().equals(2356, 9839)) {
                                Chain.bound(null).runFn(1, () -> {
                                    player.animate(741, 15);
                                }).then(2, () -> {
                                    player.teleport(new Tile(2356, 9841));
                                }).then(2, () -> {
                                    player.animate(741, 15);
                                }).then(2, () -> {
                                    player.teleport(new Tile(2356, 9843));
                                });
                            } else {
                                Chain.bound(null).runFn(1, () -> {
                                    player.animate(741, 15);
                                }).then(2, () -> {
                                    player.teleport(new Tile(2356, 9841));
                                }).then(2, () -> {
                                    player.animate(741, 15);
                                }).then(2, () -> {
                                    player.teleport(new Tile(2356, 9839));
                                });
                            }
                        }
                    }
                });
                return true;
            }

            if(obj.getId() == ROW_BOAT) {
                if(!player.getMemberRights().isSuperMemberOrGreater(player)) {
                    player.message(Color.RED.wrap("You need to be at least a Sapphire Member to travel with this boat."));
                    return true;
                }
                player.teleport(new Tile(2312, 9904));
                return true;
            }
        }
        return false;
    }
}
