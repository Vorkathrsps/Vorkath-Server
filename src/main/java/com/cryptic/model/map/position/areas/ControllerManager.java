package com.cryptic.model.map.position.areas;

import com.cryptic.model.content.raids.theatre.area.NylocasAreaController;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.*;

import java.util.ArrayList;
import java.util.List;

public class ControllerManager {

    private static final List<Controller> CONTROLLERS = new ArrayList<>();


    static {
        CONTROLLERS.add(new DuelArenaArea());
        CONTROLLERS.add(new WildernessArea());
        CONTROLLERS.add(new FightCaveArea());
        CONTROLLERS.add(new COXArea());
        CONTROLLERS.add(new TournamentArea());
        CONTROLLERS.add(new NylocasAreaController());
        CONTROLLERS.add(new KingBlackDragonLair());
        CONTROLLERS.add(new KrakenArea());
        CONTROLLERS.add(new NexArea());
        CONTROLLERS.add(new VenenatisArea());
        CONTROLLERS.add(new CallistoArea());
        CONTROLLERS.add(new VetionArea());
    }

    /**
     * Processes areas for the given mob.
     */
    public static void process(Player player) {
        Tile tile = player.tile();
        List<Controller> activeControllers = player.getControllers();
        List<Controller> newActiveControllers = new ArrayList<>();

        if (activeControllers != null) {
            for (Controller controller : CONTROLLERS) {
                boolean insideController = ((controller.useInsideCheck() && inside(tile, controller)) || (!controller.useInsideCheck() && inside(tile, controller)) || (controller.useInsideCheck() && controller.inside(player)));

                if (activeControllers.contains(controller)) {
                    if (!insideController) {
                        controller.leave(player);
                    } else {
                        newActiveControllers.add(controller);
                        controller.process(player);
                    }
                } else {
                    if (insideController) {
                        controller.enter(player);
                        newActiveControllers.add(controller);
                        controller.process(player);
                    }
                }
            }

            for (Controller controller : activeControllers) {
                if (!newActiveControllers.contains(controller)) {
                    controller.leave(player);
                }
            }
        }

        player.setControllers(newActiveControllers);
    }


    /**
     * Checks if a {@link Entity} can attack another one.
     */
    public static boolean canAttack(Player attacker, Entity target) {
        if (!attacker.getControllers().isEmpty()) {
            for (Controller controller : attacker.getControllers()) {
                return controller.canAttack(attacker, target);
            }
        }
        return true;
    }

    /**
     * Checks if a position is inside of an area's boundaries.
     */
    // there are always more reasons that just position to check if a mob is in a area, so i'd have another check that takes the mob as an argument
    public static boolean inside(Tile tile, Controller controller) {
        for (Area area : controller.getAreas()) {
            if (area.contains(tile)) {
                return true;
            }
        }
        return false;
    }

    public static Controller get(Tile tile) {
        for (Controller controller : CONTROLLERS) {
            if (inside(tile, controller)) {
                return controller;
            }
        }
        return null;
    }
}
