package com.aelous.model.content.areas.riskzone;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

public class RiskFightArea extends PacketInteraction {
    public static final Area NH_AREA = new Area(3079, 3507, 3090, 3515);
    public static final Area ONE_V_ONE_1 = new Area(3079, 3507, 3090, 3515);
    /*public static final Area ONE_V_ONE_2 = new Area(3103, 3513, 3110, 3517);
    public static final Area ONE_V_ONE_3 = new Area(3111, 3513, 3116, 3517);*/

    private void walk(Player player, GameObject obj) {
        player.runFn(1, () -> {
            int x = player.getAbsX();
            int y = player.getAbsY();

            switch (obj.getRotation()) {
                case 1:
                case 3:
                    if(y < obj.tile().y) {
                        System.out.println("we here 3");
                        y += 1;
                    } else {
                        System.out.println("we here 4");
                        y -= 1;
                    }
                default: break;
            }
            player.lock();
            player.stepAbs(x, y, MovementQueue.StepType.FORCED_WALK);
            final int finalY = y;
            player.waitForTile(new Tile(x, finalY), player::unlock);
        });
    }

    private void walkX(Player player, boolean xUp) {
        Tile targTile = player.tile().transform(xUp ? +1 : -1, 0, 0);
        player.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
    }

    private void walkY(Player player, boolean yUp) {
        Tile targTile = player.tile().transform(0, yUp ? +1 : -1, 0);
        player.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            var risk = player.<Long>getAttribOr(AttributeKey.RISKED_WEALTH, 0L);
            if (obj.getId() == BELL_21394) {
                if (player.getTimers().has(TimerKey.RISK_FIGHT_BELL)) {
                    player.message(Color.RED.wrap("You can ring the bell again in " + player.getTimers().asMinutesAndSecondsLeft(TimerKey.RISK_FIGHT_BELL) + "."));
                } else {
                    player.getTimers().register(TimerKey.RISK_FIGHT_BELL, 300);
                    World.getWorld().sendWorldMessage("<img=2011>" + Color.BLUE.wrap(player.getUsername()) + Color.RAID_PURPLE.wrap(" has just rung the bell at the risk zone and is looking for a fight!"));
                }
                return true;
            }
            if (obj.getId() == MAGICAL_BARRIER_31808) {
                walk(player, obj);
                return true;
            }

            if (obj.getId() == ENERGY_BARRIER_4470) {
                if (obj.tile().equals(3043, 3789, 0) || obj.tile().equals(3044, 3789, 0)) {
                    if (player.tile().y == 3789) {
                        player.message(Color.RED.wrap("You sense that the barrier has a magical force that won't let you back out."));
                        walkY(player, true);
                    }
                    return true;
                }
            }

            if (obj.getId() == ENERGY_BARRIER_4470) {
                //500k risk area
                if (obj.tile().equals(3086, 3506, 0) || obj.tile().equals(3085, 3506, 0) || obj.tile().equals(3084, 3506, 0) || obj.tile().equals(3083, 3506, 0)) {
                    player.getRisk().update(); // make sure our wealth is up to date.
                    if (risk <= 0) {
                        player.message(Color.RED.wrap("Prepare to lose bank!"));
                        return true;
                    }
                    if (player.tile().y == 3506) {
                        if (!player.tile().equals(obj.tile().transform(0, 0, 0))) {
                            player.getMovementQueue().walkTo(obj.tile().transform(0, 0, 0));
                        }
                        Chain.bound(player).name("RiskFightArenaTask").waitForTile(obj.tile().transform(0, 0, 0), () -> {
                            Skulling.assignSkullState(player, SkullType.RED_SKULL);
                            player.forceChat("I am currently risking " + Utils.formatNumber(risk) + " BM!");
                            Tile targTile = player.tile().transform(0, +1, 0);
                            player.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
                            Chain.bound(player).name("RiskFightArenaTask2").waitForTile(targTile, player::unlock);
                        });
                    } else if (player.tile().y == 3507) {
                        Skulling.unskull(player);
                        walkY(player, false);
                    }
                    return true;
                }
                //250k risk area
                if (obj.tile().equals(3110, 3508, 0) || obj.tile().equals(3110, 3507, 0)) {
                    player.getRisk().update(); // make sure our wealth is up to date.
                    if (risk <= 250_000) {
                        player.message(Color.RED.wrap("You need to risk at least 250K blood money to enter this risk zone."));
                        return true;
                    }
                    if (player.tile().x == 3110 && (player.tile().y == 3507 || player.tile().y == 3508)) {
                        Skulling.assignSkullState(player, SkullType.RED_SKULL);
                        player.forceChat("I am currently risking " + Utils.formatNumber(risk) + " BM!");
                        walkX(player, true);
                    } else if (player.tile().x == 3111 && (player.tile().y == 3507 || player.tile().y == 3508)) {
                        Skulling.unskull(player);
                        walkX(player, false);
                    }
                    return true;
                }
                //1M risk area
                if (obj.tile().equals(3112, 3512, 0) || obj.tile().equals(3113, 3512, 0)) {
                    player.getRisk().update(); // make sure our wealth is up to date.
                    if (risk <= 1_000_000) {
                        player.message(Color.RED.wrap("You need to risk at least 1M blood money to enter this risk zone."));
                        return true;
                    }
                    if (player.tile().y == 3512 && (player.tile().x == 3112 || player.tile().x == 3113)) {
                        Skulling.assignSkullState(player, SkullType.RED_SKULL);
                        player.forceChat("I am currently risking " + Utils.formatNumber(risk) + " BM!");
                        walkY(player, true);
                    } else if (player.tile().y == 3513 && (player.tile().x == 3112 || player.tile().x == 3113)) {
                        Skulling.unskull(player);
                        walkY(player, false);
                    }
                    return true;
                }
                //No restrictions here
                if (obj.tile().equals(3085, 3506, 0) || obj.tile().equals(3083, 3506, 0) || obj.tile().equals(3084, 3506, 0) || obj.tile().equals(3086, 3506, 0)) {
                    if (player.tile().y == 3506) {
                        Skulling.assignSkullState(player, SkullType.RED_SKULL);
                        player.forceChat("I am currently risking " + Utils.formatNumber(risk) + " BM!");
                        walkY(player, false);
                    } else if (player.tile().y == 3507) {
                        if (player.tile().y <= 3507) {
                            if (!player.tile().equals(obj.tile().transform(0, 1, 0))) {
                                player.getMovementQueue().walkTo(obj.tile().transform(0, 0, 0));
                            }
                            Chain.bound(player).name("RiskFightArenaTask").waitForTile(obj.tile().transform(0, 0, 0), () -> {
                                Skulling.unskull(player);
                                Tile targTile = player.tile().transform(0, -1, 0);
                                player.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
                                Chain.bound(player).name("RiskFightArenaTask2").waitForTile(targTile, player::unlock);
                            });
                        }
                    }
                    return true;
                }
                if (obj.tile().equals(3079, 3499, 0) || obj.tile().equals(3078, 3499, 0)) {
                    if (player.tile().y == 3498) {
                        Skulling.assignSkullState(player, SkullType.RED_SKULL);
                        player.forceChat("I am currently risking " + Utils.formatNumber(risk) + " BM!");
                        walkY(player, true);
                    } else if (player.tile().y == 3499) {
                        if (player.tile().y <= 3499) {
                            if (!player.tile().equals(obj.tile().transform(0, 0, 0))) {
                                player.getMovementQueue().walkTo(obj.tile().transform(0, 0, 0));
                            }
                            Chain.bound(player).name("RiskFightArenaTask").waitForTile(obj.tile().transform(0, 0, 0), () -> {
                                Skulling.unskull(player);
                                Tile targTile = player.tile().transform(0, -1, 0);
                                player.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
                                Chain.bound(player).name("RiskFightArenaTask2").waitForTile(targTile, player::unlock);
                            });
                        }
                    }
                    return true;
                }
                if (obj.tile().equals(3076, 3473, 0) || obj.tile().equals(3075, 3473, 0)) {
                    if (player.tile().y == 3474) {
                        Skulling.assignSkullState(player, SkullType.RED_SKULL);
                        player.forceChat("I am currently risking " + Utils.formatNumber(risk) + " BM!");
                        walkY(player, false);
                    } else if (player.tile().y == 3473) {
                        if (player.tile().y <= 3473) {
                            if (!player.tile().equals(obj.tile().transform(0, 0, 0))) {
                                player.getMovementQueue().walkTo(obj.tile().transform(0, 0, 0));
                            }
                            Chain.bound(player).name("RiskFightArenaTask").waitForTile(obj.tile().transform(0, 0, 0), () -> {
                                Skulling.unskull(player);
                                Tile targTile = player.tile().transform(0, 1, 0);
                                player.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
                                Chain.bound(player).name("RiskFightArenaTask2").waitForTile(targTile, player::unlock);
                            });
                        }
                    }
                    return true;
                }
                return true;
            }
        }
        return false;
    }
}
