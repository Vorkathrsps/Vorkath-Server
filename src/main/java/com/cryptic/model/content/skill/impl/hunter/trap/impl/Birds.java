package com.cryptic.model.content.skill.impl.hunter.trap.impl;

import com.cryptic.model.content.skill.impl.hunter.Hunter;
import com.cryptic.model.content.skill.impl.hunter.trap.Trap;
import com.cryptic.model.content.skill.impl.hunter.trap.TrapProcessor;
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
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
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
 * The bird snare implementation of the {@link Trap} class which represents a single bird snare.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Birds extends Trap {

    /**
     * Constructs a new {@link Birds}.
     * @param player    {@link #getPlayer()}.
     */
    final Player player;
    public Birds(Player player) {
        super(player, TrapType.BIRD_SNARE);
        this.player = player;
    }
    
    /**
     * The npc trapped inside this box.
     */
    private Optional<NPC> trapped = Optional.empty();

    /**
     * The object identification for a dismantled failed snare.
     */
    private static final int FAILED_ID = 9344;

    /**
     * The distance the npc has to have from the snare before it gets triggered.
     */
    private static final int DISTANCE_PORT = 3;

    /**
     * A collection of all the npcs that can be caught with a bird snare.
     */
    private static final int[] NPC_IDS = new int[]{BirdData.CRIMSON_SWIFT.npcId, BirdData.GOLDEN_WARBLER.npcId,
        BirdData.COPPER_LONGTAIL.npcId, BirdData.CERULEAN_TWITCH.npcId, BirdData.TROPICAL_WAGTAIL.npcId};
    /**
     * Kills the specified {@code npc}.
     * @param npc   the npc to kill.
     */
    private void kill(NPC npc) {
        World.getWorld().unregisterNpc(npc);
        trapped = Optional.of(npc);
    }
    
    @Override
    public boolean canCatch(NPC npc) {
        Optional<BirdData> data = BirdData.getBirdDataByNpcId(npc.id());
        
        if (!data.isPresent()) {
            throw new IllegalStateException("Invalid bird id.");
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
        player.message("You pick up your bird snare.");
        this.setPickup(true);
    }

    @Override
    public void onSetup() {
        player.message("You set-up your bird snare.");
    }

    @Setter
    private boolean pickup = false;
    @Override
    public void onCatch(NPC npc) {
        if (!ObjectManager.exists(new Tile(getObject().getX(), getObject().getY(), getObject().getHeight()))) {
            return;
        }

        Optional<BirdData> data = BirdData.getBirdDataByNpcId(npc.id());

        if (data.isEmpty()) {
            throw new IllegalStateException("Invalid bird id.");
        }

        final Trap birdSnare = this;
        BirdData bird = data.get();

        BooleanSupplier pickup = () -> this.pickup;
        Chain.bound(null).name("catch_box_trap_task").cancelWhen(() -> {
            if (pickup.getAsBoolean()) {
                npc.stopActions(true);
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
            if (npc.tile().equals(getObject().getX(), getObject().getY())) {
                if (Utils.rollDie(50, 1)) {
                    setState(TrapState.FALLEN);
                    task.stop();
                    return;
                }
                kill(npc);
                npc.hidden(true);
                npc.teleport(npc.spawnTile());
                npc.setPositionToFace(npc.tile().transform(0, 0));
                npc.hp(npc.maxHp(), 0);
                npc.animate(-1);
                npc.getCombat().getKiller();
                npc.getCombat().clearDamagers();
                npc.getMovementQueue().clear();
                Chain.bound(null).runFn(8, () -> {
                    npc.hidden(false);
                    npc.unlock();
                    World.getWorld().registerNpc(npc);
                });
                ObjectManager.removeObj(getObject());
                birdSnare.setObject(bird.caughtId);
                ObjectManager.addObj(getObject());
                setState(TrapState.CAUGHT);
                task.stop();
            }
        });
    }

    @Override
    public void onSequence() {
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
        Optional<BirdData> data = BirdData.getBirdDataByObjectId(getObject().getId());
        
        if (data.isEmpty()) {
            throw new IllegalStateException("Invalid object id.");
        }

        player.inventory().addOrDrop(new Item(BONES, 1));
        player.inventory().addOrDrop(new Item(RAW_BIRD_MEAT, 1));
        player.inventory().addOrDrop(new Item(data.get().reward, 1));
    }
    
    @Override
    public double experience() {
        if (trapped.isEmpty()) {
            throw new IllegalStateException("No npc is trapped.");
        }
        Optional<BirdData> data = BirdData.getBirdDataByObjectId(getObject().getId());
        
        if (data.isEmpty()) {
            throw new IllegalStateException("Invalid object id.");
        }
        
        return data.get().experience;
    }
    
    @Override
    public boolean canClaim(GameObject object) {
        if (trapped.isEmpty()) {
            return false;
        }
        BirdData data = BirdData.getBirdDataByObjectId(object.getId()).orElse(null);

        return data != null;
    }
    
    @Override
    public void setState(TrapState state) {
        if (state.equals(TrapState.PENDING)) {
            throw new IllegalArgumentException("Cannot set trap state back to pending.");
        }
        if (state.equals(TrapState.FALLEN)) {
            ObjectManager.removeObj(getObject());
            this.setObject(FAILED_ID);
            ObjectManager.addObj(getObject());
        }
        player.message("Your trap has been triggered by something...");
        super.setState(state);
    }

    /**
     * The enumerated type whose elements represent a set of constants
     * used for bird snaring.
     * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
     */
    public enum BirdData {
        CRIMSON_SWIFT(NpcIdentifiers.CRIMSON_SWIFT,9373, 1, 34, new Item(RED_FEATHER, 5)),
        GOLDEN_WARBLER(NpcIdentifiers.GOLDEN_WARBLER,9377, 5, 47, new Item(YELLOW_FEATHER, 5)),
        COPPER_LONGTAIL(NpcIdentifiers.COPPER_LONGTAIL,9379, 9, 61, new Item(ORANGE_FEATHER, 5)),
        CERULEAN_TWITCH(NpcIdentifiers.CERULEAN_TWITCH,9375, 11, 64.5, new Item(BLUE_FEATHER, 5)),
        TROPICAL_WAGTAIL(NpcIdentifiers.TROPICAL_WAGTAIL,9348, 19, 95, new Item(STRIPY_FEATHER, 5));

        /**
         * Caches our enum values.
         */
        private static final ImmutableSet<BirdData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(BirdData.class));
        
        /**
         * The npc id for this bird.
         */
        private final int npcId;

        /**
         * The object id for the catched bird.
         */
        private final int caughtId;

        /**
         * The requirement for this bird.
         */
        private final int requirement;

        /**
         * The experience gained for this bird.
         */
        private final double experience;

        /**
         * The reward obtained for this bird.
         */
        private final Item reward;

        /**
         * Constructs a new {@link BirdData}.
         * @param npcId         {@link #npcId}.
         * @param objectId      {@link #caughtId}
         * @param requirement   {@link #requirement}.
         * @param experience    {@link #experience}.
         * @param reward        {@link #reward}.
         */
        BirdData(int npcId, int objectId, int requirement, double experience, Item reward) {
            this.npcId = npcId;
            this.caughtId = objectId;
            this.requirement = requirement;
            this.experience = experience;
            this.reward = reward;
        }
        
        /**
         * @return the npc id.
         */
        public int getNpcId() {
            return npcId;
        }

        /**
         * Retrieves a {@link BirdData} enumerator dependant on the specified {@code id}.
         * @param id    the npc id to return an enumerator from.
         * @return a {@link BirdData} enumerator wrapped inside an optional, {@link Optional#empty()} otherwise.
         */
        public static Optional<BirdData> getBirdDataByNpcId(int id) {
            return VALUES.stream().filter(bird -> bird.npcId == id).findAny();
        }
        
        /**
         * Retrieves a {@link BirdData} enumerator dependant on the specified {@code id}.
         * @param id    the object id to return an enumerator from.
         * @return a {@link BirdData} enumerator wrapped inside an optional, {@link Optional#empty()} otherwise.
         */
        public static Optional<BirdData> getBirdDataByObjectId(int id) {
            return VALUES.stream().filter(bird -> bird.caughtId == id).findAny();
        }

    }

}
