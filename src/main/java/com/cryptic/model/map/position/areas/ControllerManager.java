package com.cryptic.model.map.position.areas;

import com.cryptic.model.content.raids.theatre.area.TheatreAreaController;
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
        CONTROLLERS.add(new TheatreAreaController());
    }

    /**
     * Processes areas for the given mob.
     */
    public static void process(Player player) {
        Tile tile = player.tile();
        Controller controller = player.getController();

        if (controller != null) {
            //We only want to check using the abstract area or using the area manager, not both,
            //since wilderness does not have the correct coordinates in the constructor, and
            //wilderness also uses custom code for determining wilderness "level".
            if ((!controller.useInsideCheck() && !inside(tile, controller)) || (controller.useInsideCheck() && !controller.inside(player))) {
                //System.out.println(mob.getMobName() + " leaving " + controller + " located at " + mob.tile());
                controller.leave(player);
                controller = null;
            }
        }

        if (controller == null) {
            controller = get(tile);
            if (controller == null) {//fallback
                for (Controller area : CONTROLLERS) {
                    if (area.useInsideCheck() && area.inside(player)) {
                        controller = area;
                        break;
                    }
                }
            }
            if (controller != null) {
                //System.out.println(mob.getMobName() + " entering " + controller + " located at " + mob.tile());
                controller.enter(player);
            }
        }

        // Handle processing..
        if (controller != null) {
            controller.process(player);
        }
        // Update area..
        player.setController(controller);
    }

    /**
     * Checks if a {@link Entity} can attack another one.
     */
    public static boolean canAttack(Player attacker, Entity target) {
        if (attacker.getController() != null) {
            return attacker.getController().canAttack(attacker, target);
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
