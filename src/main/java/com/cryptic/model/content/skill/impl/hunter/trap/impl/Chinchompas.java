package com.cryptic.model.content.skill.impl.hunter.trap.impl;

import com.cryptic.model.content.skill.impl.hunter.Hunter;
import com.cryptic.model.content.skill.impl.hunter.trap.Trap;
import com.cryptic.model.content.skill.impl.hunter.trap.TrapProcessor;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.npc.NPC;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.Setter;
import org.apache.commons.lang.ArrayUtils;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * The box trap implementation of the {@link Trap} class which represents a single box trap.
 *
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Chinchompas extends Trap {

    /**
     * Constructs a new {@link Chinchompas}.
     *
     * @param player {@link #getPlayer()}.
     */
    final Player player;

    public Chinchompas(Player player) {
        super(player, TrapType.BOX_TRAP);
        this.player = player;
    }

    /**
     * The npc trapped inside this box.
     */
    private Optional<NPC> trapped = Optional.empty();

    /**
     * The object identification for a dismantled failed box trap.
     */
    private static final int FAILED_ID = 9385;

    /**
     * The object identification for a caught box trap.
     */
    private static final int CAUGHT_ID = 9382;

    /**
     * The distance the npc has to have from the box trap before it gets triggered.
     */
    private static final int DISTANCE_PORT = 3;

    /**
     * A collection of all the npcs that can be caught with a box trap.
     */
    private static final int[] NPC_IDS = new int[]{BoxTrapData.GREY_CHINCHOMPA.npcId,
        BoxTrapData.RED_CHINCHOMPA.npcId, BoxTrapData.BLACK_CHINCHOMPA.npcId};

    public static boolean hunterNpc(int id) {
        //Fastest to check the IDs with conditional operators, otherwise use an int array with lang3 ArrayUtils.contains
        return id >= 2910 && id <= 2912;
    }

    /**
     * Kills the specified {@code npc}.
     *
     * @param npc the npc to kill.
     */
    private void kill(NPC npc) {
        World.getWorld().unregisterNpc(npc);
        npc.setHitpoints(0);
        trapped = Optional.of(npc);
    }

    @Override
    public boolean canCatch(NPC npc) {
        Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByNpcId(npc.id());

        if (data.isEmpty()) {
            throw new IllegalStateException("Invalid box trap id.");
        }

        if (player.getSkills().level(Skills.HUNTER) < data.get().requirement) {
            player.message("You do not have the required level to catch these.");
            setState(TrapState.FALLEN);
            return false;
        }
        return true;
    }

    @Override
    public void onPickUp() {
        player.message("You pick up your box trap.");
        this.setPickup(true);
    }

    @Override
    public void onSetup() {
        player.message("You set-up your box trap.");
    }

    @Setter
    private boolean pickup = false;

    @Override
    public void onCatch(NPC npc) {
        if (!ObjectManager.exists(new Tile(getObject().getX(), getObject().getY(), getObject().getHeight()))) {
            return;
        }
        final Trap boxtrap = this;

        BooleanSupplier pickup = () -> this.pickup;

        Chain.bound(null).name("catch_box_trap_task").cancelWhen(() -> {
            if (pickup.getAsBoolean()) {
                npc.stopActions(true);
                npc.unlock();
                return true;
            }
            return false;
        }).repeatingTask(1, task -> {
            if (isAbandoned()) {
                task.stop();
                return;
            }
            npc.getMovementQueue().clear();
            npc.stepAbs(this.getObject().tile().transform(0, 0), MovementQueue.StepType.FORCED_WALK);
            TrapProcessor trapProcessor = Hunter.GLOBAL_TRAPS.get(player);
            if (trapProcessor == null) {
                task.stop();
                return;
            }
            if (this.getState().equals(TrapState.CAUGHT)) {
                npc.stopActions(true);
                task.stop();
                return;
            }
            if (npc.tile().equals(getObject().tile().getX(), getObject().tile().getY())) {
                if (Utils.rollDie(50, 1)) {
                    setState(TrapState.FALLEN);
                    task.stop();
                    return;
                }
                npc.lockNoDamage();
                npc.setPositionToFace(npc.tile().transform(0, 0));
                npc.animate(-1);
                npc.animate(5184);
                ObjectManager.removeObj(getObject());
                boxtrap.setObject(9381);
                ObjectManager.addObj(getObject());
                Chain.bound(null).runFn(1, () -> {
                    kill(npc);
                    npc.hidden(true);
                    npc.teleport(npc.spawnTile());
                    npc.hp(npc.maxHp(), 0);
                    npc.getCombat().getKiller();
                    npc.getCombat().clearDamagers();
                    npc.getMovementQueue().clear();
                }).then(2, () -> {
                    ObjectManager.removeObj(getObject());
                    boxtrap.setObject(CAUGHT_ID);
                    ObjectManager.addObj(getObject());
                }).then(4, () -> {
                    npc.hidden(false);
                    npc.animate(-1);
                    npc.unlock();
                    World.getWorld().registerNpc(npc);
                });
                setState(TrapState.CAUGHT);
                task.stop();
            }
        });
    }

    @Override
    public void onSequence() { //TODO add region check for safety
        var map = Hunter.GLOBAL_TRAPS.get(player);
        for (var trap : map.getTraps()) {
            var player = trap.getPlayer();
            if (this.player != player) continue;
            for (var region : player.getRegions()) {
                for (var npc : region.getNpcs()) {
                    if (npc == null || npc.dead()) continue;
                    if (!ArrayUtils.contains(NPC_IDS, npc.id())) continue;
                    if (this.getObject().getHeight() == npc.getZ() && Math.abs(this.getObject().getX() - npc.getX()) <= DISTANCE_PORT && Math.abs(this.getObject().getY() - npc.getY()) <= DISTANCE_PORT) {
                        if (this.isAbandoned()) {
                            return;
                        }
                        if (Utils.rollDie(2, 1)) {
                            trap(npc);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reward() {
        if (trapped.isEmpty()) {
            throw new IllegalStateException("No npc is trapped.");
        }

        Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByNpcId(trapped.get().id());

        if (data.isEmpty()) {
            throw new IllegalStateException("Invalid object id.");
        }

        int amount;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER, SAPPHIRE_MEMBER, EMERALD_MEMBER -> amount = 2;
            case DIAMOND_MEMBER, DRAGONSTONE_MEMBER -> amount = 3;
            case ONYX_MEMBER, ZENYTE_MEMBER -> amount = 4;
            default -> amount = 1;
        }
        Item reward = switch (data.get()) {
            case GREY_CHINCHOMPA -> new Item(CHINCHOMPA_10033, amount);
            case RED_CHINCHOMPA -> new Item(RED_CHINCHOMPA_10034, amount);
            case BLACK_CHINCHOMPA -> new Item(BLACK_CHINCHOMPA, amount);
        };

        if (data.get() == BoxTrapData.BLACK_CHINCHOMPA) {
            player.getTaskMasterManager().increase(Tasks.BLACK_CHINCHOMPAS);
        }

        player.inventory().addOrDrop(reward);
    }

    @Override
    public double experience() {
        if (trapped.isEmpty()) {
            throw new IllegalStateException("No npc is trapped.");
        }

        Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByNpcId(trapped.get().id());

        if (data.isEmpty()) {
            throw new IllegalStateException("Invalid object id.");
        }

        return data.get().experience;
    }

    @Override
    public boolean canClaim(GameObject object) {
        return trapped.isPresent();
    }

    @Override
    public void setState(TrapState state) {
        if (state.equals(TrapState.PENDING)) {
            throw new IllegalArgumentException("Cannot set trap state back to pending.");
        }
        if (state.equals(TrapState.FALLEN)) {
            ObjectManager.removeObj(getObject());
            this.setObject(FAILED_ID);
            ObjectManager.addObj(this.getObject());
        }
        player.message("Your trap has been triggered by something...");
        super.setState(state);
    }

    /**
     * The enumerated type whose elements represent a set of constants
     * used for box trapping.
     *
     * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
     */
    private enum BoxTrapData {
        GREY_CHINCHOMPA(2910, 53, 198.25),
        RED_CHINCHOMPA(2911, 63, 265),
        BLACK_CHINCHOMPA(2912, 73, 315);

        /**
         * Caches our enum values.
         */
        private static final ImmutableSet<BoxTrapData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(BoxTrapData.class));

        /**
         * The npc id for this box trap.
         */
        private final int npcId;

        /**
         * The requirement for this box trap.
         */
        private final int requirement;

        /**
         * The experience gained for this box trap.
         */
        private final double experience;

        /**
         * Constructs a new {@link BoxTrapData}.
         *
         * @param npcId       {@link #npcId}.
         * @param requirement {@link #requirement}.
         * @param experience  {@link #experience}.
         */
        BoxTrapData(int npcId, int requirement, double experience) {
            this.npcId = npcId;
            this.requirement = requirement;
            this.experience = experience;
        }

        /**
         * Retrieves a {@link BoxTrapData} enumerator dependant on the specified {@code id}.
         *
         * @param id the npc id to return an enumerator from.
         * @return a {@link BoxTrapData} enumerator wrapped inside an optional, {@link Optional#empty()} otherwise.
         */
        public static Optional<BoxTrapData> getBoxTrapDataByNpcId(int id) {
            return VALUES.stream().filter(box -> box.npcId == id).findAny();
        }
    }
}
