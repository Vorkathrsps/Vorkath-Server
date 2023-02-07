package com.aelous.utility.timers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public enum TimerKey {

    VASA_TELEPORT_ATTACK,

    /**
     * Since we don't use an attack timer for their special
     */
    THROWING_AXE_DELAY,

    /**
     * Player counting 3,2,1,go for a stake.
     */
    STAKE_COUNTDOWN,

    /**
     * Timer set by the dragon scimitar special attack, blocking the overhead prayers.
     */
    OVERHEADS_BLOCKED,

    /**
     * Like a teleblock. W2 protection mechanic. Stops instant teleporting after using your special attack, you prick.
     */
    BLOCK_SPEC_AND_TELE,

    /**
     * Timer used to indicate when our last non-generic damage was dealt. Used to see if we can log out. 10 s (17 ticks)
     */
    COMBAT_LOGOUT,

    // How many ticks left that we've forfeit the risk protection mechanic. Ticks offline.
    RISK_PROTECTION(20, true),

    /**
     * Key used when burying bones in the ground.
     */
    BONE_BURYING,

    /**
     * Key used to have the 1.8 seconds of fire-chaining in firemaking
     */
    FIRE_CATCHING,

    /**
     * Timer used to signify when a reanimated monster will despawn.
     */
    REANIMATED_MONSTER_DESPAWN,

    // You can only chomp of these cakes once per tick.
    EAT_ROCKCAKE,

    /**
     * Key used to deduct micro-prayer points and process overhead logic
     */
    PRAYER_TICK,

    /**
     * Timer used for refilling the players special attack from Surgeon General Tafani
     */
    RECHARGE_SPECIAL_ATTACK(17, false),

    /**
     * Timer used for sending world messages for a risk fight
     */
    RISK_FIGHT_BELL(51, false),

    /**
     * Timer used to indicate when the next Wilderness boss event happens.
     */
    WILDERNESS_BOSS_TIMER,

    /**
     * Timer used to indicate when the wilderness boss event (forcefully) ends.
     */
    WILDERNESS_BOSS_TIMEOUT,

    /**
     * 7 minute cooldown timer on the Imbued heart until you can re-use it.
     */
    IMBUED_HEART_COOLDOWN(24, false),

    /**
     * 2 minute NPC timer that will despawn a superior boss monster if not under attack at that point.
     */
    SUPERIOR_BOSS_DESPAWN,

    HAMSTRUNG,

    /**
     * Vetion reborn is active for 5 mins until he transmogs back to regular vetion (if not killed).
     */
    VETION_REBORN_TIMER,

    GODWARS_ALTAR_LOCK(2),

    /**
     * Timer used to determine how long is left in the Charge spell.
     */
    CHARGE_SPELL,

    /**
     * The key used when a player has commenced a successful dragonfire special.
     */
    DRAGONFIRE_SPECIAL,

    /*
     * Key used to indicate that the vengeance spell is on cooldown
     */
    VENGEANCE_COOLDOWN,

    CLICK_DELAY,
    FOOD,
    KARAMBWAN,
    POTION,
    COMBAT_ATTACK,
    /**
     * Key used to indicate an entity is currently frozen and blocked from moving.
     */
    FROZEN,

    /**
     * Key used to tell if they've had enough time in between freezes. Aka immunity.
     */
    REFREEZE,

    /**
     * Key used to indicate that the entity is currently stunned.
     */
    STUNNED,

    /**
     * The amount of immunity time the player has
     */
    STUN_IMMUNITY,

    ATTACK_IMMUNITY,
    TOURNAMENT_FIGHT_IMMUNE,
    SOTD_DAMAGE_REDUCTION,
    ZILY_SPEC_COOLDOWN,
    ELDER_CHAOS_DRUID_TELEPORT,

    /**
     * Timer ticking down before spear effect wears off. On zero, if you're stacked with pending spears, you'll be moved gain.
     */
    SPEAR,

    /**
     * Key used to manage the teleblock timer
     */
    TELEBLOCK(1, false),

    /*
     * All keys below until the mark are for farming.
     * Naming: FM_region_patch
     */
    // (key 1 & 2 already in use)
    FM_CAMELOT_ALLOT_NORTH(3),
    FM_CAMELOT_ALLOT_SOUTH(4
    ),
    FM_ARDY_ALLOT_NORTH(5),
    FM_ARDY_ALLOT_SOUTH(6),
    FM_FALLY_ALLOT_NORTH(7),
    FM_FALLY_ALLOT_SOUTH(8),
    FM_CANIFIS_ALLOT_NORTH(9),
    FM_CANIFIS_ALLOT_SOUTH(10),
    FM_ZEAH_ALLOT_NORTH(11),
    FM_ZEAH_ALLOT_SOUTH(12),
    FM_CAMELOT_HERBS(13),
    FM_ARDY_HERBS(14),
    FM_FALLY_HERBS(15),
    FM_ZEAH_HERBS(16),
    // (key 17 already in use)
    FM_CANIFIS_HERBS(18),
    COMPOST_BIN(19),
    FM_FALLY_FLOWER(27),
    FM_CAMELOT_FLOWER(29),
    FM_ARDY_FLOWER(30),
    FM_CANIFIS_FLOWER(31),
    FM_ZEAH_FLOWER(32),
    FM_PRIF_ALLOTMENT_SOUTH(43),
    FM_PRIF_FLOWER(44),
    PRIF_ALLOTMENT_NORTH(45),
    PRIF_CRYSTAL_TREE(46),
    FARMING_GUILD_ALLOT(47),
    FARMING_GUILD_HERBS(48),

    /**
     * Key used to manage the special teleblock timer that isn't removed on logout
     */
    SPECIAL_TELEBLOCK(49, false),

    /**
     * Key used to manage the teleblock immunity timer
     */
    TELEBLOCK_IMMUNITY(50, false);

    public static final TimerKey[] cachedValues = values();
    private static final Logger logger = LogManager.getLogger(TimerKey.class);
    private boolean persistent;
    private int persistencyKey;
    private boolean ticksOffline;

    private TimerKey() {
        // Empty default constructor (non-persistent)
    }

    private TimerKey(int persistencyKey) {
        persistent = true;
        this.persistencyKey = persistencyKey;
        ticksOffline = true;
    }

    private TimerKey(int persistencyKey, boolean ticksOffline) {
        persistent = true;
        this.persistencyKey = persistencyKey;
        this.ticksOffline = ticksOffline;
    }

    public boolean persistent() {
        return persistent;
    }

    public int persistencyKey() {
        return persistencyKey;
    }

    public boolean ticksOffline() {
        return ticksOffline;
    }

    public static Optional<TimerKey> forPersistencyKey(int key) {
        for (TimerKey tk : cachedValues) {
            if (tk.persistencyKey == key)
                return Optional.of(tk);
        }

        return Optional.empty();
    }

    public static void verifyIntegrity() {
        // Critical check for overlapping timer keys - that'd be disastrous.
        Set<Integer> uniques = new HashSet<>();
        for (TimerKey key : values()) {
            if (key.persistent()) {
                if (uniques.contains(key.persistencyKey)) {
                    System.err.println("=================================== WARNING ===================================");
                    logger.error("WARNING: DUPLICATE TIMER KEY " + key.persistencyKey + " ON " + key.name() + "!");
                    logger.error("REJECTING SERVER BOOT FOR OBVIOUS REASONS.");
                    System.err.println("=================================== WARNING ===================================");
                    System.exit(1);
                } else {
                    uniques.add(key.persistencyKey);
                }
            }
        }
    }
}
