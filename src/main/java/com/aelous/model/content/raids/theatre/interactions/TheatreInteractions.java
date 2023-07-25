package com.aelous.model.content.raids.theatre.interactions;

import com.aelous.model.content.instance.InstanceConfiguration;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.stage.RoomState;
import com.aelous.model.content.raids.theatre.stage.TheatreStage;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

public class TheatreInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        Theatre theatre = new Theatre(player, new TheatreArea(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, Theatre.rooms()));
        TheatreInterface theatreInterface = new TheatreInterface(player);

        if (obj.getId() == 32655) {
            theatreInterface.open(player);
            return true;
        }

        if (player.getTheatreParty() != null) {

            if (obj.getId() == 32653) {
                theatre.startRaid();
                return true;
            }

            if (obj.getId() == 32996) {
                theatre.dispose();
                return true;
            }

            if (obj.getId() == 32755) {
                if (player.getClickDelay().elapsed(3000)) {
                    if (player.tile().x < obj.tile().x) {
                        player.lock();
                        player.stepAbs(obj.getX() + 1, obj.getY(), MovementQueue.StepType.FORCED_WALK);
                        Chain.noCtx().runFn(2, () -> {
                            player.getClickDelay().reset();
                            player.unlock();
                        });
                    } else {
                        player.lock();
                        player.stepAbs(obj.getX() - 1, obj.getY(), MovementQueue.StepType.FORCED_WALK);
                        Chain.noCtx().runFn(2, () -> {
                            player.getClickDelay().reset();
                            player.unlock();
                        });
                    }
                    return true;
                }
            }
            if (obj.getId() == 33113) {
                if (player.tile().region() == 12613 && Theatre.getTheatrePhase().getStage() == TheatreStage.TWO) {
                    player.teleport(3269, 4447, player.getInstancedArea().getzLevel());
                    player.setRoomState(RoomState.INCOMPLETE);
                    player.getClickDelay().reset();
                    return true;
                } else if (player.tile().region() == 12613 && !Theatre.theatrePhase.getStage().equals(TheatreStage.TWO)) {
                    player.message(Color.RED.wrap("You must complete this room before progressing further into this raid."));
                    player.message(Theatre.getTheatrePhase().getStage().toString());
                    player.getClickDelay().reset();
                    return false;
                }

                if (player.tile().region() == 13125 && Theatre.getTheatrePhase().getStage() == TheatreStage.THREE) {
                    player.teleport(3295, 4283, player.getInstancedArea().getzLevel());
                    player.setRoomState(RoomState.INCOMPLETE);
                    player.getClickDelay().reset();
                    return true;
                } else if (player.tile().region() == 12613 && !Theatre.theatrePhase.getStage().equals(TheatreStage.THREE)) {
                    player.message(Color.RED.wrap("You must complete this room before progressing further into this raid."));
                    player.message(Theatre.getTheatrePhase().getStage().toString());
                    player.getClickDelay().reset();
                    return false;
                }

                if (player.tile().region() == 13122 && Theatre.getTheatrePhase().getStage() == TheatreStage.FOUR) {
                    player.teleport(3280, 4294, player.getInstancedArea().getzLevel());
                    player.setRoomState(RoomState.INCOMPLETE);
                    player.getClickDelay().reset();
                    return true;
                } else if (player.tile().region() == 12613 && !Theatre.theatrePhase.getStage().equals(TheatreStage.FOUR)) {
                    player.message(Color.RED.wrap("You must complete this room before progressing further into this raid."));
                    player.message(Theatre.getTheatrePhase().getStage().toString());
                    player.getClickDelay().reset();
                    return false;
                }

                if (player.tile().region() == 13123 && Theatre.getTheatrePhase().getStage() == TheatreStage.FIVE) {
                    player.teleport(3170, 4377, player.getInstancedArea().getzLevel() + 1);
                    player.setRoomState(RoomState.INCOMPLETE);
                    player.getClickDelay().reset();
                    return true;
                } else if (player.tile().region() == 13123 && !Theatre.theatrePhase.getStage().equals(TheatreStage.FIVE)) {
                    player.message(Color.RED.wrap("You must complete this room before progressing further into this raid."));
                    player.getClickDelay().reset();
                    return false;
                }
            }
        } else {
            player.message("You must have an active party to start this raid.");
            return false;
        }
        return false;
    }
}
