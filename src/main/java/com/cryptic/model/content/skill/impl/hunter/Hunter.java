package com.cryptic.model.content.skill.impl.hunter;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.hunter.trap.Trap;
import com.cryptic.model.content.skill.impl.hunter.trap.Trap.TrapState;
import com.cryptic.model.content.skill.impl.hunter.trap.Trap.TrapType;
import com.cryptic.model.content.skill.impl.hunter.trap.TrapProcessor;
import com.cryptic.model.content.skill.impl.hunter.trap.TrapTask;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static com.cryptic.model.entity.attributes.AttributeKey.MOVEMENT_PACKET_STEPS;

/**
 * The class which holds static functionality for the hunter skill.
 *
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Hunter {

    //Barehand impling anim id 7171

    /**
     * The mappings which contain each trap by player on the world.
     */
    public static final Map<Player, TrapProcessor> GLOBAL_TRAPS = new HashMap<>();

    /**
     * Retrieves the maximum amount of traps a player can lay.
     *
     * @param player the player to lay a trap down for.
     * @return a numerical value determining the amount a player can lay.
     */
    private static int getMaximumTraps(Player player) {
        int level = player.getSkills().level(Skills.HUNTER);
        return level / 20 + 1;
    }

    /**
     * Attempts to abandon the specified {@code trap} for the player.
     *
     * @param trap   the trap that was abandoned.
     * @param logout if the abandon was due to the player logging out.
     */
    public static void abandon(Player player, Trap trap, boolean logout) {
        if (GLOBAL_TRAPS.get(player) == null) {
            return;
        }

        if (logout) {
            GLOBAL_TRAPS.get(player).getTraps().forEach(t -> {
                t.setAbandoned(true);
                ObjectManager.removeObj(t.getObject());
                GroundItemHandler.createGroundItem(new GroundItem(new Item(t.getType().getItemId()), t.getObject().tile(), player));
            });
            GLOBAL_TRAPS.get(player).getTraps().clear();
        } else {
            GLOBAL_TRAPS.get(player).getTraps().remove(trap);
            trap.setAbandoned(true);
            ObjectManager.removeObj(trap.getObject());
            GroundItemHandler.createGroundItem(new GroundItem(new Item(trap.getType().getItemId()), trap.getObject().tile(), player));
            player.message("You have abandoned your trap...");
        }

        if (GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
            GLOBAL_TRAPS.get(player).setTask(Optional.empty());
            GLOBAL_TRAPS.remove(player);
        }
    }

    /**
     * Attempts to lay down the specified {@code trap} for the specified {@code player}.
     *
     * @param player the player to lay the trap for.
     * @param trap   the trap to lay down for the player.
     * @return {@code true} if the trap was laid, {@code false} otherwise.
     */
    public static boolean lay(Player player, Trap trap) {
        if (!player.last_trap_layed.elapsed(1200)) {
            return false;
        }

        player.last_trap_layed.reset();

        int[] areas = {9271, 9272, 10285, 10029, 10810, 12346, 12347, 12602, 12603, 12090, 13113};

        boolean isHunterArea = player.getPlayerRights().isCommunityManager(player) || Arrays.stream(areas).anyMatch(area -> area == player.tile().region());

        if (!isHunterArea) {
            player.message("This is not a suitable spot to place a trap.");
            return false;
        }

        GLOBAL_TRAPS.putIfAbsent(player, new TrapProcessor());

        TrapProcessor trapProcessor = GLOBAL_TRAPS.get(player);

        if (trapProcessor.getTraps().size() >= getMaximumTraps(player) && !player.getPlayerRights().isCommunityManager(player)) {
            player.message("You cannot lay more then " + getMaximumTraps(player) + " with your hunter level.");
            return false;
        }

        if (ObjectManager.exists(new Tile(player.getX(), player.getY(), player.getZ()))) {
            player.message("You can't lay down your trap here.");
            return false;
        }

        player.animate(Animation.DEFAULT_RESET_ANIMATION);
        player.animate(5208);
        MutableObject<GroundItem> groundItem = new MutableObject<GroundItem>();
        groundItem.setValue(new GroundItem(new Item(trap.getType().getItemId()), player.tile(), player));

        int[] ticks = new int[]{0};
        int[] attempts = new int[]{0};
        Chain.bound(player).name("trap_placement_task").runFn(1, () -> {
            player.inventory().remove(trap.getType().getItemId());
            groundItem.getValue().setState(GroundItem.State.SEEN_BY_OWNER);
            groundItem.getValue().spawn();
        }).repeatingTask(1, task -> {
            ticks[0]++;
            if (task.isStopped()) {
                task.stop();
                return;
            }
            if (ticks[0] >= 3) {
                ticks[0] = 0;
                attempts[0]++;
                player.animate(5208);
            }
            if (World.getWorld().random(0, 4) == 1 || attempts[0] >= 1) {
                trapProcessor.getTraps().add(trap);
                if (trapProcessor.getTask().isEmpty()) {
                    trapProcessor.setTask(new TrapTask(player));
                    TaskManager.submit(trapProcessor.getTask().get());
                }
                trap.submit();
                player.animate(Animation.DEFAULT_RESET_ANIMATION);
                GroundItemHandler.sendRemoveGroundItem(groundItem.getValue());
                ObjectManager.addObj(trap.getObject());
                MovementQueue.clippedStep(player, 1);
                task.stop();
            }
        });
        return true;
    }

    /**
     * Attempts to pick up the trap for the specified {@code player}.
     *
     * @param player the player to pick this trap up for.
     * @param object the object id that was clicked.
     * @return {@code true} if the trap was picked up, {@code false} otherwise.
     */
    public static boolean pickup(Player player, GameObject object) {
        Optional<TrapType> type = TrapType.getTrapByObjectId(object.getId());

        if (type.isEmpty()) {
            return false;
        }

        Trap trap = getTrap(player, object).orElse(null);

        if (trap == null) {
            return false;
        }

        if (trap.getPlayer() == null) {
            player.message("You can't pickup someone elses trap...");
            return false;
        }

        if (trap.getState().equals(TrapState.CAUGHT)) {
            return false;
        }

        trap.onPickUp();
        player.getMovementQueue().clear();
        player.stepAbs(trap.getObject().tile().transform(0,0), MovementQueue.StepType.REGULAR);
        if (trap.getObject().definition().name.equalsIgnoreCase("Box Trap") || trap.getObject().definition().name.equalsIgnoreCase("Shaking Box")) {
            player.animate(5212);
        } else if (trap.getObject().definition().name.equalsIgnoreCase("Bird Snare")) {
            player.animate(5207);
        }
        Chain.noCtx().runFn(2, () -> {
            ObjectManager.removeObj(trap.getObject());
            player.inventory().addOrDrop(new Item(trap.getType().getItemId(), 1));
            GLOBAL_TRAPS.get(player).getTraps().remove(trap);
            if (GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
                GLOBAL_TRAPS.get(player).setTask(Optional.empty());
                GLOBAL_TRAPS.remove(player);
            }
        });
        return true;
    }

    /**
     * Attempts to claim the rewards of this trap.
     *
     * @param player the player attempting to claim the items.
     * @param object the object being interacted with.
     * @return {@code true} if the trap was claimed, {@code false} otherwise.
     */
    public static boolean claim(Player player, GameObject object) {
        Trap trap = getTrap(player, object).orElse(null);

        if (trap == null) {
            player.message("You can't pickup someone elses trap...");
            return false;
        }

        if (!trap.canClaim(object)) {
            return false;
        }

        if (trap.getPlayer() == null) {
            player.message("You can't claim the rewards of someone elses trap...");
            return false;
        }

        if (!trap.getState().equals(TrapState.CAUGHT)) {
            return false;
        }


        player.getMovementQueue().clear();
        player.stepAbs(trap.getObject().tile().transform(0,0), MovementQueue.StepType.REGULAR);
        if (trap.getObject().definition().name.equalsIgnoreCase("Shaking Box")) {
            player.animate(5212);
        } else if (trap.getObject().definition().name.equalsIgnoreCase("Bird Snare")) {
            player.animate(5207);
        }
        BooleanSupplier waitUntil = () -> player.tile().equals(trap.getObject().tile().transform(0,0));
        player.waitUntil(waitUntil, () -> Chain.noCtx().runFn(2, () -> {
            player.inventory().addOrDrop(new Item(trap.getType().getItemId(), 1));
            player.getSkills().addXp(Skills.HUNTER, (int) trap.experience());
            trap.reward();
            if (Utils.rollDie(20, 1)) {
                player.inventory().addOrDrop(new Item(7956, 1));
                player.message("You collect your prey from the trap and found a casket!");
            }
            ObjectManager.removeObj(trap.getObject());
            GLOBAL_TRAPS.get(player).getTraps().remove(trap);

            if (GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
                GLOBAL_TRAPS.get(player).setTask(Optional.empty());
                GLOBAL_TRAPS.remove(player);
            }
        }));
        return true;
    }


    /**
     * Gets a trap for the specified global object given.
     *
     * @param player the player to return a trap for.
     * @param object the object to compare.
     * @return a trap wrapped in an optional, {@link Optional#empty()} otherwise.
     */
    public static Optional<Trap> getTrap(Player player, GameObject object) {
        return !GLOBAL_TRAPS.containsKey(player) ? Optional.empty() : GLOBAL_TRAPS.get(player).getTraps().stream().filter(trap -> trap.getObject().getId() == object.getId() && trap.getObject().getX() == object.getX() && trap.getObject().getY() == object.getY() && trap.getObject().getHeight() == object.getHeight()).findAny();
    }
}
