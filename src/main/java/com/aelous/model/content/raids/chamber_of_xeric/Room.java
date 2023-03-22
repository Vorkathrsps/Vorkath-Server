package com.aelous.model.content.raids.chamber_of_xeric;

import com.aelous.model.content.raids.chamber_of_xeric.reward.ChamberOfXericReward;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.impl.ObjectInteractionHandler;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;
import static com.aelous.model.inter.dialogue.Dialogue.send;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 29, 2021
 */
public class Room extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (option == 1) {
            Party party = player.raidsParty;
            if (party == null) {
                return false;
            }

            if (object.getId() == STEPS_29778) {
                if (player.getRaids() != null) {
                    var alive = player.raidsParty.monsters.stream().anyMatch(n -> n.id() == GREAT_OLM_7554 && !n.dead());
                    if (alive) {
                        player.message("the raid isn't over!");
                        // until DynamicMap support is added, or custom Z clipping is supported, you cant have unique clipping at z>3 which is all instances
                        // this means players can noclip through the crystal to the stairs, so reset movement when they try this when boss (and crystal techniclly) is still active
                        player.getMovementQueue().reset();
                        return true;
                    }
                    // re-route the player, when complete, trigger exit raid. dont trigger exit raid until path complete (path was skipped earlier via isRemoteObjectSkipPath())
                    player.getRouteFinder().routeObject(object, () -> player.getRaids().exit(player), false);
                }
                return true;
            }

            if (object.getId() == PASSAGE_29789) {
                //First stage
                if (object.tile().equals(3307, 5205)) {
                    Tile tektonRoom = new Tile(3310, 5277, player.tile().level + 1);
                    player.teleport(tektonRoom);
                    //Second stage
                } else if (object.tile().equals(3310, 5306, 1)) {
                    if (party.getRaidStage() >= 2) {
                        Tile vasaRoom = new Tile(3311, 5279, player.tile().level - 1);
                        player.teleport(vasaRoom);
                    } else {
                        player.message(Color.RED.wrap("Please defeat all the monsters before going to the next room."));
                    }
                    //Third stage
                } else if (object.tile().equals(3311, 5308)) {
                    if (party.getRaidStage() >= 3) {
                        Tile vangaurdsRoom = new Tile(3311, 5311, player.tile().level);
                        player.teleport(vangaurdsRoom);
                    } else {
                        player.message(Color.RED.wrap("Please defeat all the monsters before going to the next room."));
                    }
                } else if (object.tile().equals(3311, 5341)) {
                    if (party.getRaidStage() >= 4) {
                        Tile muttadileRoom = new Tile(3311, 5309, player.tile().level + 1);
                        player.teleport(muttadileRoom);
                    } else {
                        player.message(Color.RED.wrap("Please defeat all the monsters before going to the next room."));
                    }
                } else if (object.tile().equals(3308, 5337)) {
                    if (party.getRaidStage() >= 5) {
                        Tile vespulaRoom = new Tile(3311, 5277, player.tile().level + 1);
                        player.teleport(vespulaRoom);
                    } else {
                        player.message(Color.RED.wrap("Please defeat all the monsters before going to the next room."));
                    }
                } else if (object.tile().equals(3311, 5309)) {
                    if (party.getRaidStage() >= 6) {
                        Tile olmWaitingRoom = new Tile(3232, 5721, player.tile().level - 2);
                        player.teleport(olmWaitingRoom);
                    } else {
                        player.message(Color.RED.wrap("Please defeat all the monsters before going to the next room."));
                    }
                }
                return true;
            }

            if (object.getId() == MYSTICAL_BARRIER) {
                Tile bossRoomTile = new Tile(3232, 5730, player.tile().level);
                player.teleport(bossRoomTile);
                return true;
            }

            if (object.getId() == ANCIENT_CHEST) {
                if (player.getRaidRewards().isEmpty()) {
                    player.message(Color.RED.wrap("You have already looted the chest, or your points are below 10,000."));
                    return true;
                }
                ChamberOfXericReward.displayRewards(player);
                ChamberOfXericReward.withdrawReward(player);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == VERZIK_VITUR_8369) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.NPC_STATEMENT, VERZIK_VITUR_8369, Expression.CALM_TALK, "Now that was quite the show! I haven't been that", "entertained in a long time.");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if(isPhase(0)) {
                            npc.transmog(VERZIK_VITUR_8370);
                            stop();
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }
}
