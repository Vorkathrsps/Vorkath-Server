package com.aelous.model.content.items;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.CommandManager;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Debugs;
import com.aelous.utility.chainedwork.Chain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.aelous.utility.ItemIdentifiers.ROTTEN_POTATO;

/**
 * item 5733
 * <br>
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 17, 2020
 */
public class RottenPotato extends PacketInteraction {
    private static final Logger logger = LogManager.getLogger(RottenPotato.class);

    public static boolean onItemOnMob(Player player, Entity target) {
        // Give you the name and distance to a target
        if (player.getPlayerRights().isDeveloper(player)) {
            //Debugs.CMB.toggle();
            if (target.isPlayer()) {
                player.debugMessage(String.format("Distance to %s (%d) : %d. ", (target.getAsPlayer()).getUsername(), target.getIndex(), player.tile().distance(target.tile())));
            } else {
                player.getPacketSender().sendPositionalHint(target.tile(), 2);
                Debugs.CMB.debug(player, String.format("on %s %s", target, target.tile()), target, true);
                //System.out.println(String.format("on %s %s", target, target.tile()));
                player.getMovementQueue().clear();
                //System.out.println("mob pid "+target.getIndex());
                NPC npc = (NPC) target;
                player.debugMessage(String.format("Distance to %s (%d) : %d size %d. ", npc.def().name, target.getIndex(), player.tile().distance(target.tile()), npc.getSize()));
                potatoOnMob(player, npc);
            }
            return true;
        }
        return false;
    }

    private static void potatoOnMob(Player player, NPC npc) {
        npc.getAsNpc().cantInteract(false);
        npc.unlock();
        System.out.println(npc.getSize());
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Options for NPC "+ npc.def().name, "Kill NPC.", "Despawn NPC.", "Teleport to me.", "Transmog.", "Replace.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(option == 1) {
                    if(npc.dead()) {
                        player.message("" + npc.def().name + " is dead.");
                    }
                    if (npc.id() == 6611)
                        npc.clearAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED);
                    var h = npc.hit(player, npc.hp(), (CombatType) null);
                    h.submit();
                    if (npc.getCombatMethod() != null && npc.getCombatMethod().customOnDeath(h)) {
                        // empty on purpose just to trigger above method
                    }
                    Chain.noCtx().delay(2, () -> {
                        if (!npc.locked()) // for some reason death code didnt run? kill manually
                            npc.die();
                    });
                } else if(option == 2) {
                    World.getWorld().unregisterNpc(npc);
                } else if(option == 3) {
                    npc.teleport(player.tile());
                } else if(option == 4) {
                    player.setAmountScript("Enter NPC ID (or 0 to reset)", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            int id = (Integer) value;
                            npc.transmog(id == 0 ? -1 : id);
                            return true;
                        }
                    });
                } else if(option == 5) {
                    player.setAmountScript("Enter NPC ID (or 0 to cancel)", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            int id = (Integer) value;
                            if (id > 0) {
                                World.getWorld().unregisterNpc(npc);
                                NPC newNpc = new NPC(id, npc.tile());
                                World.getWorld().registerNpc(newNpc);
                            }
                            return true;
                        }
                    });
                }
                stop();
            }
        });
    }

    public static void onItemOption1(Player player) {
        if (player.getPlayerRights().isDeveloper(player)) {
            CommandManager.attempt(player, "infhp");
            if (GameServer.properties().production) {
                potatoChat(player);
            } else {
                //CommandManager.attempt(player, "clipat");
                //CommandManager.attempt(player, "teleto testbot1");
                CommandManager.attempt(player, "hydra");
            }
        }
    }

    private static void potatoChat(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Op1", "Set all stats.", "Wipe inventory.", "Setup POH", "Teleport to player", "Spawn aggressive NPC.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    stop();

                    player.setAmountScript("Set to what level?", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            int lvl = (Integer) value;
                            lvl = Math.max(1, Math.min(99, lvl));
                            for (int i = 0; i < Skills.SKILL_COUNT; i++) {
                                player.getSkills().setXp(i, Skills.levelToXp(lvl));
                                player.getSkills().update();
                                player.getSkills().recalculateCombat();
                            }
                            return true;
                        }
                    });
                } else if (option == 2) {
                    stop();
                    player.inventory().clear();
                    player.inventory().add(new Item(ROTTEN_POTATO));
                } else if (option == 3) {
                    stop();
                    player.message("We don't have the Construction skill. This option isn't available.");
                } else if (option == 4) {
                    stop();

                    player.setNameScript("Teleport to:", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            String input = (String) value;
                            Optional<Player> teleportTo = World.getWorld().getPlayerByName(input);

                            if(teleportTo.isPresent()) {
                                player.teleport(teleportTo.get().tile());
                                player.message("You have teleported to "+teleportTo.get().getUsername()+".");
                            } else {
                                player.message(input+" is not online right now.");
                            }
                            return true;
                        }
                    });
                } else if (option == 5) {
                    stop();

                    player.setAmountScript("Enter the npc ID", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            int id = (Integer) value;
                            NPC npc = new NPC(id, new Tile(player.tile().x - 2, player.tile().y));
                            World.getWorld().registerNpc(npc);
                            if (npc.getCombatInfo() != null)
                                npc.getCombatInfo().aggressive = true;
                            return true;
                        }
                    });
                }
            }
        });
    }

    public static void onItemOption3(Player player) {
        if (player.getPlayerRights().isDeveloper(player)) {
            //potatoOp3(player);
            if (!GameServer.properties().production) {
                //CommandManager.attempt(player, "addbotsvorkath 400");
            }
        }
    }

    private static void potatoOp3(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Op3", "Bank menu", "AMEs for all!", "Teleport to RARE!", "Spawn RARE!");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, "Op3", "Open bank.", "Set PIN to 2468.", "Wipe bank.");
                            setPhase(0);
                        }

                        @Override
                        protected void select(int option) {
                            if (option == 1) {
                                stop();
                                player.getBank().open();
                            } else if (option == 2) {
                                /*player.getBankPin().setPinLength(4);
                                player.getBankPin().setHashedPin("2468");
                                player.message("Your bank pin is now 2468.");*/
                                stop();
                            } else if (option == 3) {
                                player.getBank().clear();
                            }
                        }
                    });
                } else if (option == 2) {
                    //TODO
                } else if (option == 3) {
                    //TODO
                } else if (option == 4) {
                    //TODO
                }
            }
        });
    }

    @Override
    public boolean handleEquipment(Player player, Item item) {
        if(item.getId() == ROTTEN_POTATO) {
            if (!GameServer.properties().production) {
                //CommandManager.attempt(player, "scm");
                CommandManager.attempt(player, "infhp");
            }
            return true;
        }
        return false;
    }

    public static void onItemOption2(Player player) {
        if (player.getPlayerRights().isDeveloper(player)) {
            potatoOp2(player);
        }
    }

    private static void potatoOp2(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Op2", "Keep me logged in.", "Kick me out.", "Kill me.", "Transmogrify me...");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(option == 1) {
                    //TODO
                } else if(option == 2) {
                    player.requestLogout();
                } else if(option == 3) {
                    //player.typeLessHit(player.hitpoints());
                } else if(option == 4) {
                    player.setAmountScript("Transmogrify me...", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            int id = (Integer) value;
                            player.looks().transmog(id);
                            return true;
                        }
                    });
                }
            }
        });
    }

    public static void used_on_object(Player player) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
        String name = obj.definition().name;
        //System.out.println(obj.definition().toStringBig());
        //System.out.println(obj.definition(World.getWorld()).toStringBig());
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Delete obj "+name, "Obj on tile count", "Clear object's attributes", "Nevermind");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if(option == 1) {
                    ObjectManager.removeObj(obj);
                    player.message("removed object "+name+" at "+obj.tile()+".");
                    stop();
                } else if(option == 2) {
                    //TODO
                } else if(option == 3) {
                    //TODO
                } else if(option == 4) {
                    stop();
                }
            }
        });
    }
}
