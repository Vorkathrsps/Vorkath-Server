package com.aelous.model.content.areas.zulandra;

import com.aelous.model.World;
import com.aelous.model.content.areas.zulandra.dialogue.*;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah.Zulrah;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Tuple;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.SACRIFICIAL_BOAT;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.ZULANDRA_TELEPORT;

/**
 * no need to use custom region packet, the instance system simply changes the heightlevel and uses the real world-map coords for
 * areas.
 */
public class ZulAndra extends PacketInteraction {

    private static final Tile ZULRAH_PLAYER_START_TILE = new Tile(2268, 3069);
    private static final Area ZULRAH_AREA = new Area(2251, 3058, 2281, 3088);
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == SACRIFICIAL_BOAT) {

                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.OPTION, "Return to Zulrah's shrine?", "Yes.", "No.");
                        setPhase(0);
                    }

                    @Override
                    protected void select(int option) {
                        if (getPhase() == 0) {
                            if (option == 1) {
                                stop();
                                player.getPacketSender().sendScreenFade("", 1, 5);
                                DialogueManager.sendStatement(player, "The priestess rows you to Zulrah's shrine,", "then hurriedly paddles away.");
                                enterInstance(player);
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    }

                });
                return true;
            }
            if (obj.getId() == ZULANDRA_TELEPORT) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.OPTION, "Return to Zulrah's shrine?", "Yes.", "No.");
                        setPhase(0);
                    }

                    @Override
                    protected void select(int option) {
                        if (getPhase() == 0) {
                            if (option == 1) {
                                player.lock();
                                player.animate(3864);
                                player.graphic(1039, GraphicHeight.HIGH, 0);
                                stop();
                                Chain.bound(null).name("ZulrahReturnTask").runFn(4, () -> {
                                    player.animate(-1);
                                    player.getInterfaceManager().close();
                                    enterInstance(player);
                                });
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }

    private void enterInstance(Player player) {
        player.lock();
        Chain.bound(null).name("ZulAndraBoatTask").runFn(9, () -> {
            var instance = InstancedAreaManager.getSingleton().createInstancedArea(ZULRAH_AREA);
            NPC zulrah = new NPC(NpcIdentifiers.ZULRAH, ZULRAH_PLAYER_START_TILE.transform(-2, 3, instance.getzLevel()));
            player.setInstance(instance);
            instance.addNpc(zulrah);
            instance.addPlayer(player);
            player.getMovementQueue().clear();
            player.teleport(ZULRAH_PLAYER_START_TILE.x, ZULRAH_PLAYER_START_TILE.y, instance.getzLevel());
            player.unlock();

            zulrah.setInstance(instance);
            zulrah.respawns(false);
            zulrah.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
            zulrah.setPositionToFace(null);
            zulrah.noRetaliation(true);
            zulrah.getCombatInfo().aggressive = false;
        }).then(1, () -> player.message("Welcome to Zulrah's shrine.")).then(1, () -> {
            var zulrah = player.getInstancedArea().getNpcs().get(0);
            World.getWorld().registerNpc(zulrah);
            zulrah.setPositionToFace(zulrah.tile().transform(0, -10, 0));
            zulrah.animate(5073);
            Zulrah.startZulrahBattle(zulrah, player);
        });
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033) {
            if (option == 1) {
                player.getDialogueManager().start(new PriestessZulGwenwynig());
            } else if (option == 2) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "I'm afraid I don't have anything for you to collect. If I", "had any of your items, but you died before collecting", "them from me, I'd lose them.");
                    }
                });
            }
            return true;
        }

        if (npc.id() == NpcIdentifiers.SACRIFICE) {
            player.getDialogueManager().start(new Sacrafise());
            return true;
        }

        if (npc.id() == NpcIdentifiers.ZULURGISH) {
            player.getDialogueManager().start(new ZulUrgish());
            return true;
        }

        if (npc.id() == NpcIdentifiers.ZULARETH) {
            player.getDialogueManager().start(new ZulAreth());
            return true;
        }

        if (npc.id() == NpcIdentifiers.ZULANIEL) {
            player.getDialogueManager().start(new ZulAniel());
            return true;
        }

        if (npc.id() == NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA) {
            player.getDialogueManager().start(new HighPriestessZulHarcinqa());
            return true;
        }

        if (npc.id() == NpcIdentifiers.ZULONAN) {
            player.getDialogueManager().start(new ZulOnan());
            return true;
        }

        if (npc.id() == NpcIdentifiers.ZULGUTUSOLLY) {
            player.getDialogueManager().start(new ZulGutusolly());
            return true;
        }

        if (npc.id() == NpcIdentifiers.ZULCHERAY) {
            player.getDialogueManager().start(new ZulCheray());
            return true;
        }
        return false;
    }
}
