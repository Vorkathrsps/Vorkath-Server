package com.cryptic.model.content.raids.theatreofblood.interactions;

import com.cryptic.model.content.raids.theatreofblood.interactions.dialogue.TheatreDialogue;
import com.cryptic.model.content.raids.theatreofblood.stage.RoomState;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;

public class TheatreInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        var id = obj.getId();
        if (id == 32655) {
            if (player.getTheatreInterface() == null) {
                player.setTheatreInterface(new TheatreInterface(player, new ArrayList<>()).open(player));
            } else {
                player.getTheatreInterface().open(player);
            }
            return true;
        }
        if (id == 32653) {
            if (player.getRaidParty() != null) {
                player.getDialogueManager().start(new TheatreDialogue());
            }
            return true;
        }
        if (player.getTheatreInstance() == null) return false;
        var height = player.getTheatreInstance().getzLevel();
        if (id == 32751) {
            var party = player.getTheatreInstance().getPlayers();
            for (var p : party) {
                if (p == player) {
                    p.lock();
                    p.teleport(new Tile(3168, 4298, player.getTheatreInstance().getzLevel()));
                    p.setRoomState(RoomState.INCOMPLETE);
                    p.waitForTile(new Tile(3168, 4298, player.getTheatreInstance().getzLevel()), () -> {
                        p.smartPathTo(new Tile(3167, 4302, player.getTheatreInstance().getzLevel()));
                    }).waitForTile(new Tile(3168, 4298, player.getTheatreInstance().getzLevel()), () -> {

                    }).then(1, () -> {
                        p.getMovementQueue().clear();
                        p.stepAbs(3168, 4303, MovementQueue.StepType.FORCED_WALK);
                    }).waitForTile(new Tile(3168, 4303, player.getTheatreInstance().getzLevel()), p::unlock);
                    break;
                }
            }
            return true;
        } else if (id == 32755) {
            if (player.tile().region() == 12613) {
                if (player.tile().getX() < 3186) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(2, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(-2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            } else if (player.tile().region() == 13125) {
                if (player.tile().getX() < 3288) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(2, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else if (player.tile().getX() < 3303 && player.tile().getX() > 3286) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(-2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else if (player.tile().getX() < 3305 && player.tile().getX() > 3302) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(2, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(-2, 0);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            } else if (player.tile().region() == 13123) {
                if (player.tile().getY() >= 4308) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, -2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, 2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            } else if (player.tile().region() == 12612) {
                if (player.tile().getY() == 4394) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, 2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else if (player.tile().getY() == 4396) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, -2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else if (player.tile().getY() == 4378) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, 2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else if (player.tile().getY() == 4380) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, -2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            } else if (player.tile().region() == 13122) {
                if (player.tile().getY() >= 4256) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, -2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                } else if (player.tile().getY() <= 4254) {
                    Chain.noCtx().runFn(1, () -> {
                        player.agilityWalk(false);
                        player.lock();
                        var t = new Tile(player.tile().getX(), player.tile().getY()).transform(0, 2);
                        player.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.FORCED_WALK);
                    }).then(3, () -> {
                        player.unlock();
                        player.agilityWalk(true);
                    });
                }
                return true;
            }
        } else if (id == 33113) {
            var party = player.getTheatreInstance().getPlayers();
            if (player.getRoomState().equals(RoomState.INCOMPLETE)) {
                player.message(Color.RED.wrap("You must complete this room to advance to the next fight."));
                return true;
            }
            if (player.tile().region() == 12613) {
                for (var p : party) {
                    if (p == player) {
                        p.teleport(new Tile(3271, 4448, height));
                        p.setRoomState(RoomState.INCOMPLETE);
                        break;
                    }
                }
            } else if (player.tile().region() == 13125) {
                for (var p : party) {
                    if (p == player) {
                        p.teleport(new Tile(3300, 4276, height));
                        p.setRoomState(RoomState.INCOMPLETE);
                        break;
                    }
                }
            } else if (player.tile().region() == 13122) {
                for (var p : party) {
                    if (p == player) {
                        p.teleport(new Tile(3279, 4294, height));
                        p.setRoomState(RoomState.INCOMPLETE);
                        break;
                    }
                }
            } else if (player.tile().region() == 13123) {
                for (var p : party) {
                    if (p == player) {
                        p.teleport(new Tile(3170, 4376, height + 1));
                        p.setRoomState(RoomState.INCOMPLETE);
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
