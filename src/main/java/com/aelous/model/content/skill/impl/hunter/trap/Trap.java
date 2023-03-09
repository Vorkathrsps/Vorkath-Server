package com.aelous.model.content.skill.impl.hunter.trap;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.RandomGen;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Represents a single trap on the world.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public abstract class Trap {

    public final RandomGen random = new RandomGen();
    
    /**
     * The owner of this trap.
     */
    protected final Player player;
    
    /**
     * The type of this trap.
     */
    private final TrapType type;

    /**
     * The state of this trap.
     */
    private TrapState state;
    
    /**
     * The global object spawned on the world.
     */
    private GameObject object;
    
    /**
     * Determines if this trap is abandoned.
     */
    private boolean abandoned = false;
    
    /**
     * Constructs a new {@link Trap}.
     * @param player    {@link #player}.
     * @param type      {@link #type}.
     */
    public Trap(Player player, TrapType type) {
        this.player = player;
        this.type = type;
        this.state = TrapState.PENDING;
        this.object = new GameObject(type.objectId, player.tile());
    }
    
    /**
     * Submits the trap task for this trap.
     */
    public void submit() {
        this.onSetup();
    }
    
    /**
     * Attempts to trap the specified {@code npc} by checking the prerequisites and initiating the 
     * abstract {@link #onCatch} method.
     * @param npc   the npc to trap.
     */
    public void trap(NPC npc) {
        if (!this.getState().equals(TrapState.PENDING) || !canCatch(npc) || this.isAbandoned()) {
            return;
        }
        onCatch(npc);
    }
    
    /**
     * The array containing every larupia item set.
     */
    private static final int[] LARUPIA_SET = new int[]{LARUPIA_LEGS, LARUPIA_TOP, LARUPIA_HAT};
    
    /**
     * Determines fi the player has equiped any set that boosts the success formula.
     * @return the amount of items the player is wearing.
     */
    public boolean hasLarupiaSetEquipped() {
        return IntStream.range(0, LARUPIA_SET.length).allMatch(id -> player.getEquipment().contains(id));
    }
    
    /**
     * Calculates the chance for the bird to be lured <b>or</b> trapped.
     * @param npc       the npc being caught.
     * @return the double value which defines the chance.
     */
    public int successFormula(NPC npc) {
        Player player = this.getPlayer();
        if (player == null) {
            return 0;
        }
        int chance = 70;
        if (this.hasLarupiaSetEquipped()) {
            chance = chance + 10;
        }
        chance = chance + (int) (player.getSkills().level(Skills.HUNTER) / 1.5) + 10;

        if (player.getSkills().level(Skills.HUNTER) < 65) {
            chance = (int) (chance * 1.05) + 3;
        } else if (player.getSkills().level(Skills.HUNTER) < 60) {
            chance = (int) (chance * 1.1);
        } else if (player.getSkills().level(Skills.HUNTER) < 55) {
            chance = (int) (chance * 1.2);
        } else if (player.getSkills().level(Skills.HUNTER) < 50) {
            chance = (int) (chance * 1.3) + 1;
        } else if (player.getSkills().level(Skills.HUNTER) < 40) {
            chance = (int) (chance * 1.4) + 3;
        } else if (player.getSkills().level(Skills.HUNTER) < 25) {
            chance = (int) (chance * 1.5) + 8;      
        }
        return chance;
    }
    
    /**
     * Determines if the trap can catch.
     * @param npc       the npc to check.
     * @return {@code true} if the player can, {@code false} otherwise.
     */
    public abstract boolean canCatch(NPC npc);
    
    /**
     * The functionality that should be handled when the trap is picked up.
     */
    public abstract void onPickUp();
    
    /**
     * The functionality that should be handled when the trap is being set-up.
     */
    public abstract void onSetup();
    
    /**
     * The functionality that should be handled when the trap has catched.
     * @param npc   the npc that was catched.
     */
    public abstract void onCatch(NPC npc);
    
    /**
     * The functionality that should be handled every 600ms.
     */
    public abstract void onSequence();
    
    /**
     * The reward for this player.
     * return an array of items defining the reward.
     */
    public abstract void reward();
    
    /**
     * The experience gained for catching this npc.
     * @return a numerical value defining the amount of experience gained.
     */
    public abstract double experience();
    
    /**
     * Determines if the trap can be claimed.
     * @param object        the object that was interacted with.
     * @return {@code true} if the trap can, {@code false} otherwise.
     */
    public abstract boolean canClaim(GameObject object);
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the type
     */
    public TrapType getType() {
        return type;
    }

    /**
     * @return the state
     */
    public TrapState getState() {
        return state;
    }

    /**
     * @param state the state to set.
     */
    public void setState(TrapState state) {
        this.state = state;
    }
    
    /**
     * @return the object
     */
    public GameObject getObject() {
        return object;
    }
    
    /**
     * Sets the object id.
     * @param id    the id to set.
     */
    public void setObject(int id) {
        this.object = new GameObject(id, this.getObject().tile());
    }

    /**
     * @return the abandoned
     */
    public boolean isAbandoned() {
        return abandoned;
    }

    /**
     * @param abandoned the abandoned to set
     */
    public void setAbandoned(boolean abandoned) {
        this.abandoned = abandoned;
    }

    /**
     * The enumerated type whose elements represent a set of constants
     * used to define the type of a trap.
     * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
     */
    public enum TrapType {
        BOX_TRAP(9380, ItemIdentifiers.BOX_TRAP),
        DISMANTLED_BOX_TRAP(9385, ItemIdentifiers.BOX_TRAP),
        DISMANTLED_BIRD_SNARE(9344, ItemIdentifiers.BIRD_SNARE),
        BIRD_SNARE(9345, ItemIdentifiers.BIRD_SNARE);
        
        /**
         * Caches our enum values.
         */
        private static final ImmutableSet<TrapType> VALUES = Sets.immutableEnumSet(EnumSet.allOf(TrapType.class));
        
        /**
         * The object id for this trap.
         */
        private final int objectId;
        
        /**
         * The item id for this trap.
         */
        private final int itemId;
        
        /**
         * Constructs a new {@link TrapType}.
         * @param objectId  {@link #objectId}.
         * @param itemId    {@link #itemId}.
         */
        private TrapType(int objectId, int itemId) {
            this.objectId = objectId;
            this.itemId = itemId;
        }
        
        /**
         * @return the object id
         */
        public int getObjectId() {
            return objectId;
        }
        
        /**
         * @return the item id
         */
        public int getItemId() {
            return itemId;
        }
        
        /**
         * Gets a trap dependent of the specified {@code objectId}.
         * @param objectId  the id to get the trap type enumerator from.
         * @return a {@link TrapType} wrapped in an optional, {@link Optional#empty} otherwise.
         */
        public static Optional<TrapType> getTrapByObjectId(int objectId) {
            return VALUES.stream().filter(trap -> trap.objectId == objectId).findAny();
        }
        
        /**
         * Gets a trap dependent of the specified {@code itemId}.
         * @param itemId    the id to get the trap type enumerator from.
         * @return a {@link TrapType} wrapped in an optional, {@link Optional#empty} otherwise.
         */
        public static Optional<TrapType> getTrapByItemId(int itemId) {
            return VALUES.stream().filter(trap -> trap.itemId == itemId).findAny();
        }
    }
    
    /**
     * The enumerated type whose elements represent a set of constants
     * used to define the state of a trap.
     * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
     */
    public enum TrapState {
        PENDING,
        CAUGHT,
        FALLEN;
    }
    
}
