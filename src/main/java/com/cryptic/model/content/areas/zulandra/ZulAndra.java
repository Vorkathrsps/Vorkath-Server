package com.cryptic.model.content.areas.zulandra;

import com.cryptic.model.World;
import com.cryptic.model.content.areas.zulandra.dialogue.*;
import com.cryptic.model.content.instance.InstancedAreaManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.zulrah.Zulrah;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.SACRIFICIAL_BOAT;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.ZULANDRA_TELEPORT;
import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;

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
                        sendOption("Return to Zulrah's shrine?", "Yes.", "No.");
                        setPhase(0);
                    }

                    @Override
                    protected void select(int option) {
                        if (getPhase() == 0) {
                            if (option == 1) {
                                stop();
                                player.getPacketSender().sendScreenFade("", 1, 5);
                                player.getDialogueManager().sendStatement( "The priestess rows you to Zulrah's shrine,", "then hurriedly paddles away.");
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
                        sendOption("Return to Zulrah's shrine?", "Yes.", "No.");
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
                                var instance = InstancedAreaManager.getSingleton().createInstancedArea(ZULRAH_AREA);
                                NPC zulrah = new NPC(NpcIdentifiers.ZULRAH, ZULRAH_PLAYER_START_TILE.transform(-2, 3, instance.getzLevel()));
                                Chain.noCtx().runFn(3, () -> {
                                    player.animate(-1);
                                    player.getInterfaceManager().close();
                                    player.getMovementQueue().clear();
                                    player.setInstancedArea(instance);
                                    instance.addNpc(zulrah);
                                    instance.addPlayer(player);
                                    zulrah.setInstancedArea(instance);
                                    zulrah.respawns(false);
                                    Long uid = player.<Long>getAttribOr(PLAYER_UID, 0L);
                                    zulrah.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(uid, player));
                                    zulrah.setPositionToFace(null);
                                    zulrah.noRetaliation(true);
                                    zulrah.getCombatInfo().aggressive = false;
                                    player.teleport(ZULRAH_PLAYER_START_TILE.x, ZULRAH_PLAYER_START_TILE.y, instance.getzLevel());
                                }).then(1, () -> {
                                    player.unlock();
                                }).then(2, () -> {
                                    World.getWorld().registerNpc(zulrah);
                                    zulrah.setPositionToFace(zulrah.tile().transform(0, -10, 0));
                                    zulrah.animate(5073);
                                    Zulrah.startZulrahBattle(zulrah, player);
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

    public static void enterInstance(Player player) {
        player.lock();
        Chain.bound(null).name("ZulAndraBoatTask").runFn(9, () -> {
            var instance = InstancedAreaManager.getSingleton().createInstancedArea(ZULRAH_AREA);
            NPC zulrah = new NPC(NpcIdentifiers.ZULRAH, ZULRAH_PLAYER_START_TILE.transform(-2, 3, instance.getzLevel()));
            player.setInstancedArea(instance);
            instance.addNpc(zulrah);
            instance.addPlayer(player);
            player.getMovementQueue().clear();
            player.teleport(ZULRAH_PLAYER_START_TILE.x, ZULRAH_PLAYER_START_TILE.y, instance.getzLevel());
            player.unlock();

            zulrah.setInstancedArea(instance);
            zulrah.respawns(false);
            Long uid = player.<Long>getAttribOr(PLAYER_UID, 0L);
            zulrah.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(uid, player));
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
                        sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "I'm afraid I don't have anything for you to collect. If I", "had any of your items, but you died before collecting", "them from me, I'd lose them.");
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
