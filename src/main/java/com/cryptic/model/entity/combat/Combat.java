package com.cryptic.model.entity.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.mechanics.MultiwayCombat;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.LockType;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.formula.maxhit.*;
import com.cryptic.model.entity.combat.hit.HitDamageCache;
import com.cryptic.model.entity.combat.hit.HitQueue;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.magic.data.ModernSpells;
import com.cryptic.model.entity.combat.magic.spells.CombatSpells;
import com.cryptic.model.entity.combat.method.CombatMethod;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.specials.melee.GraniteMaul;
import com.cryptic.model.entity.combat.ranged.RangedData.RangedWeapon;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.combat.weapon.FightType;
import com.cryptic.model.entity.combat.weapon.WeaponType;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.model.map.route.RouteMisc;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.utility.Debugs;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.timers.TimerKey;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.UNDEAD_COMBAT_DUMMY;
import static com.cryptic.model.content.daily_tasks.DailyTaskUtility.DAILY_TASK_MANAGER_INTERFACE;

/**
 * My entity-based combat system. The main class of the system.
 *
 * @author Swiffy
 */

public class Combat {

    private static final Logger logger = LogManager.getLogger(Combat.class);

    public CombatSpell[] AUTOCAST_SPELLS = {
        CombatSpells.TRIDENT_OF_THE_SEAS.getSpell(),
        CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell(),
        CombatSpells.SANGUINESTI_STAFF.getSpell(),
        CombatSpells.TUMEKENS_SHADOW.getSpell(),
        CombatSpells.DAWNBRINGER.getSpell(),
        CombatSpells.ACCURSED_SCEPTRE.getSpell(),
        CombatSpells.THAMMARON_SCEPTRE.getSpell(),
        CombatSpells.STARTER_STAFF.getSpell()
    };

    // The user's damage map
    private Map<Entity, HitDamageCache> damageMap;

    public Map<Entity, HitDamageCache> getDamageMap() {
        if (damageMap == null) damageMap = new HashMap<>(); // only create when code needs it!
        return damageMap;
    }

    public void setDamageMap(Map<Entity, HitDamageCache> damageMap) {
        this.damageMap = damageMap;
    }

    public void clearDamagers() {
        if (damageMap == null) return;
        damageMap.clear();
    }

    // Ranged data
    public RangedWeapon rangedWeapon;
    // The user's HitQueue
    private final HitQueue hitQueue;
    // The mob
    private final Entity mob;
    // The mob's current target
    private Entity target;
    // The last combat method used
    private CombatMethod method;
    // Fight type
    private FightType fightType = FightType.UNARMED_KICK;
    // WeaponInterface
    private WeaponType weapon;
    // Autoretaliate
    private boolean autoRetaliate;
    // Magic data
    private CombatSpell castSpell;
    private ModernSpells spellID;
    private CombatSpell autoCastSpell;
    @Getter
    @Setter
    private CombatSpell poweredStaffSpell;

    public Combat(Entity mob) {
        this.mob = mob;
        this.hitQueue = new HitQueue();
    }

    public CombatType getCombatType() {
        CombatType combatType = null;
        if (method instanceof CommonCombatMethod commonCombatMethod) {
            combatType = commonCombatMethod.styleOf();
        }
        return combatType;
    }

    public void setMethod(CombatMethod method) {
        this.method = method;
    }

    public void delayAttack(int ticks) {
        mob.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, ticks);
    }

    MagicMaxHitFormula magicMaxHitFormula = new MagicMaxHitFormula();

    public int getMaximumMagicDamage() { //TODO add support for monster magic strength
        if (mob instanceof NPC npc) {
            return npc.getCombatInfo() != null ? npc.getCombatInfo().maxhit : 0;
        }
        if (target instanceof NPC npc && mob instanceof Player player && npc.id() == UNDEAD_COMBAT_DUMMY) {
            return magicMaxHitFormula.calculateMaxMagicHit(player);
        }
        return magicMaxHitFormula.calculateMaxMagicHit(mob.getAsPlayer());
    }


    public int getMaximumMeleeDamage() {
        if (mob.isNpc()) {
            return mob.getAsNpc().getCombatInfo() == null ? 0 : mob.getAsNpc().getCombatInfo().maxhit;
        }

        if (mob.isPlayer() && target != null && target.isNpc() && target.getAsNpc().id() == UNDEAD_COMBAT_DUMMY) {
            return MeleeMaxHitFormula.maxHit(mob.getAsPlayer());
        }

        return MeleeMaxHitFormula.maxHit(mob.getAsPlayer());
    }

    /**
     * The maximum range hit
     *
     * @return The max hit
     */

    RangeMaxHitFormula maxHitFormula = new RangeMaxHitFormula();

    public int getMaximumRangedDamage() {

        if (mob instanceof NPC npc) {
            return npc.getCombatInfo() != null ? npc.getCombatInfo().maxhit : 0;
        }

        if (target instanceof NPC npc && mob instanceof Player player && npc.id() == UNDEAD_COMBAT_DUMMY) {
            return maxHitFormula.calculateMaximumHit(player, player.isSpecialActivated());
        }

        return maxHitFormula.calculateMaximumHit(mob.getAsPlayer(), mob.getAsPlayer().isSpecialActivated());
    }

    private void applyTeleBlockImmunity() {
        if (mob.getAsPlayer().getTimers().left(TimerKey.TELEBLOCK) <= 0) {
            mob.getAsPlayer().message("<col=4f006f>The teleblock spell cast on you fades away.");
            mob.getAsPlayer().getTimers().cancel(TimerKey.TELEBLOCK);
            mob.getAsPlayer().getTimers().extendOrRegister(TimerKey.TELEBLOCK_IMMUNITY, 100);
        }
    }

    public void preAttack() {
        method = CombatFactory.getMethod(mob);
        checkLastTarget();
        checkGraniteMaul();
        if (target != null) {
            if (!CombatFactory.canAttack(mob, method, target)) reset();
            else if (target != null && (mob.isPlayer() || (mob.isNpc() && mob.getAsNpc().useSmartPath)))
                TargetRoute.set(mob, target, method.moveCloseToTargetTileRange(mob));
        }
    }

    public boolean multiCheck(Player player) {
        boolean targetInMulti = target.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1;
        if (!targetInMulti) {
            return true;
        }
        return true;
    }


    /**
     * Attacks an entity by updating our current target.
     *
     * @param target The target to attack.
     */
    public void attack(Entity target) {
        //When certain conditions are met you can no longer attack.
        if (mob.dead() || target.dead() || mob.locked()) {
            return;
        }

        if (mob.isPlayer()) {
            Player player = mob.getAsPlayer();

            if (player.locked()) {
                return;
            }

            player.action.reset();
            player.getInterfaceManager().closeDialogue();
            player.getRunePouch().close();
            player.action.clearNonWalkableActions();
            if (!player.getInterfaceManager().isMainClear()) {
                boolean ignore = player.getInterfaceManager().isInterfaceOpen(DAILY_TASK_MANAGER_INTERFACE) || player.getInterfaceManager().isInterfaceOpen(29050) || player.getInterfaceManager().isInterfaceOpen(55140);
                if (!ignore) {
                    player.getInterfaceManager().close(false);
                }
            }
        }

        //Set new target
        setTarget(target);

        // Set facing
        if (mob.getInteractingEntity() != target) {
            mob.setEntityInteraction(target);
        }

        if (mob.isPlayer()) {
            mob.getMovementQueue().clear();
        }

        Debugs.CMB.debug(mob, "Attack", target, true);
    }

    /**
     * Processes combat.
     */
    public void process() {
        hitQueue.process(mob);
        performNewAttack();
        if (mob instanceof NPC npc) {
            npc.combatSequence();
        }

        if (mob.isPlayer() && target == null) {
            //No target found reset fight time
            if (fightTimer.isRunning()) {
                fightTimer.reset();
            }
        }
    }

    public static final int[] ignored_interaction = new int[]{12167, 12166, 11774, 11762, 6611, 6612, 11776, 13013, 13017};

    public boolean beforePerformAttack() {

        if (mob.isPlayer() && mob.getRouteFinder() != null && mob.getRouteFinder().targetRoute != null && !mob.getRouteFinder().targetRoute.withinDistance) {
            return false;
        }

        if (mob instanceof NPC npc && npc.beforeAttack()) return false;

        if (!CombatFactory.validTarget(mob, target)) {
            reset();
            return false;
        }

        if (!CombatFactory.canAttack(mob, method, target)) {
            reset();
            return false;
        }

        if (mob.isPlayer()) {
            if (method instanceof CommonCombatMethod commonCombatMethod) {
                if (!commonCombatMethod.canAttackStyle(mob, target, commonCombatMethod.styleOf())) {
                    return false;
                }
            }
        }

        if (mob.getInteractingEntity() != target && !mob.isNpc(ignored_interaction)) mob.setEntityInteraction(target);
        return true;
    }

    /**
     * Attempts to attack the target.
     */
    public void performNewAttack() {
        try {
            performNewAttack0();
        } catch (Exception e) {
            logger.error("performNewAttack ex on " + mob.getMobName());
            logger.error("perfNewAttack", e);
            String sb = "combat state: " +
                this;
            logger.error(sb);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * the real method, without try-catch wrapped around it
     */
    private void performNewAttack0() {
        if (target == null) {
            return;
        }

        /**
         * Set our common combat method
         */
        method = CombatFactory.getMethod(mob);

        if (method instanceof CommonCombatMethod) {
            ((CommonCombatMethod) method).set(mob, target);
        }

        if (!beforePerformAttack()) {
            return;
        }

        updateLastTarget(target);

        final int attackSpeed = method.getAttackSpeed(mob);

        boolean graniteMaulSpecial = (method instanceof GraniteMaul);
        if (graniteMaulSpecial && specialGraniteMaul()) {
            return;
        }

        final Entity target = this.target;

        int combatAttackTicksRemaining = mob.getTimers().left(TimerKey.COMBAT_ATTACK);

        if (combatAttackTicksRemaining <= 0) {
            if (!method.prepareAttack(mob, target)) return;
            if (mob instanceof Player player && target instanceof Player) {
                if (WildernessArea.isInWilderness(player)) {
                    Skulling.skull(player, target, SkullType.WHITE_SKULL);
                }
            }
            target.putAttrib(AttributeKey.LAST_DAMAGER, mob);
            target.putAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME, System.currentTimeMillis());
            mob.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);
            mob.putAttrib(AttributeKey.LAST_ATTACK_TIME, System.currentTimeMillis());
            mob.putAttrib(AttributeKey.LAST_TARGET, target); // my thinking was maybe thats set before canAttack but nah its fine
            mob.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);
            if (target.isPlayer()) {
                Player player = target.getAsPlayer();
                if (!player.getInterfaceManager().isMainClear()) {
                    boolean ignore = player.getInterfaceManager().isInterfaceOpen(DAILY_TASK_MANAGER_INTERFACE) || player.getInterfaceManager().isInterfaceOpen(29050) || player.getInterfaceManager().isInterfaceOpen(55140);
                    if (!ignore) {
                        player.getInterfaceManager().close(false);
                    }
                }
            }
            if (!graniteMaulSpecial) mob.getTimers().register(TimerKey.COMBAT_ATTACK, attackSpeed);
            if (mob.isPlayer() && method == CombatFactory.MAGIC_COMBAT) {
                if (method instanceof CommonCombatMethod o) o.postAttack();
            }
        }
    }

    static final List<Integer> gmauls = new ArrayList<>(List.of(ItemIdentifiers.GRANITE_MAUL, ItemIdentifiers.GRANITE_MAUL_12848, ItemIdentifiers.GRANITE_MAUL_24225));

    private boolean specialGraniteMaul() {
        var graniteMaulSpecials = mob.<Integer>getAttribOr(AttributeKey.GRANITE_MAUL_SPECIALS, 0);
        if (graniteMaulSpecials == 0)
            return false;

        if (mob.isPlayer()) {
            Player player = mob.getAsPlayer();
            boolean isGmaul = gmauls.stream().anyMatch(granite_maul -> player.getEquipment().hasAt(EquipSlot.WEAPON, granite_maul));
            if (!isGmaul)
                return false;
        }

        mob.putAttrib(AttributeKey.GRANITE_MAUL_SPECIALS, 0);

        if (graniteMaulSpecials > 2)
            graniteMaulSpecials = 2;

        for (int i = 0; i < graniteMaulSpecials; i++) {
            mob.getCombat().method.prepareAttack(mob, target);
        }

        // any gmaul spec pushes the next weapon attack to the next tick
        mob.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 1);
        return true;
    }

    public double magicSpellDelay(Entity target) {
        int delay = (int) (1D + Math.floor(1 + target.tile().getChevDistance(target.tile()) / 3D));
        delay = (int) Math.min(Math.max(1.0, delay), 5.0);
        return delay;
    }

    /**
     * Resets combat for the {@link Entity}.
     */
    public void reset() {
        updateLastTarget(target);
        target = null;
        lastTarget = null;
        mob.clearAttrib(AttributeKey.TARGET);
        if (mob.isPlayer()) mob.getAsPlayer().getMovementQueue().resetFollowing();
        mob.setEntityInteraction(null);
        TargetRoute.reset(mob);
    }

    /**
     * Adds damage to the damage map, as long as the argued amount of damage is
     * above 0 and the argued entity is a player.
     *
     * @param entity the entity to add damage for.
     * @param amount the amount of damage to add for the argued entity.
     */
    HitDamageCache hitDamageCache = null;

    public void addDamage(Entity entity, int amount) {
        if (amount <= 0 || isNonCombatNpc(this.mob)) {
            // Damage on non-combat NPCs is not tracked
            return;
        }

        Map<Entity, HitDamageCache> damageMap = getDamageMap();

        hitDamageCache = damageMap.computeIfAbsent(entity, key -> new HitDamageCache(0));
        hitDamageCache.incrementDamage(amount);
    }


    private boolean isNonCombatNpc(Entity entity) {
        if (!entity.isNpc()) return false;
        return entity.isNpc() && entity.getAsNpc().getCombatInfo() != null && entity.getAsNpc().getCombatInfo().unattackable;
    }

    /**
     * Performs a search on the <code>damageMap</code> to find which {@link Player}
     * dealt the most damage on this controller.
     *
     * @return the player who killed this entity, or <code>null</code> if an npc or
     * something else killed this entity.
     */
    public Optional<Player> getKiller() {

        // Return null if no players killed this entity.
        if (damageMap == null || damageMap.size() == 0) {
            return Optional.empty();
        }

        // The damage and killer placeholders.
        int damage = 0;
        Optional<Player> killer = Optional.empty();

        for (Entry<Entity, HitDamageCache> entry : damageMap.entrySet()) {

            // Check if this entry is valid.
            if (entry == null) {
                continue;
            }

            // Check if the cached time is valid.
            long timeout = entry.getValue().getStopwatch().elapsed();
            if (timeout > CombatConstants.DAMAGE_CACHE_TIMEOUT) {
                continue;
            }

            // Check if the key for this entry has logged out.
            if (entry.getKey().isPlayer()) {
                Player player = (Player) entry.getKey();
                if (!player.isRegistered()) {
                    continue;
                }

                // If their damage is above the placeholder value, they become the
                // new 'placeholder'.
                if (entry.getValue().getDamage() > damage) {
                    damage = entry.getValue().getDamage();
                    killer = Optional.of((Player) entry.getKey());
                }
            }
        }

        // Return the killer placeholder.
        return killer;
    }

    public Entity getTargetRef() {
        var ref = mob.<WeakReference<Entity>>getAttribOr(AttributeKey.TARGET, new WeakReference<Entity>(null));
        if (ref == null) return null;
        var target = ref.get();

        if (target != null)
            mob.setEntityInteraction(target);

        // If these conditions fail, we can't attack
        if (target != null && !target.dead() && !mob.dead() && !target.finished()) {
            return target;
        }

        return null;
    }

    public Entity refreshTarget() {
        var target = getTarget();
        var npc = mob.getAsNpc();

        // If these conditions fail, we can't attack
        if (target != null && !target.dead() && !npc.dead() && !npc.finished() && !target.finished()) {
            return target;
        }

        return null;
    }

    public boolean damageMapContains(Player player) {
        return damageMap.containsKey(player);
    }

    public boolean damageMapContainsEntity(Entity entity) {
        return damageMap.containsKey(entity);
    }

    /**
     * Getters and setters
     **/

    public Entity getMob() {
        return mob;
    }

    /**
     * Return the player's combat target. This is not the bounty target.
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Set the player's combat target. This is not the bounty target.
     */
    public void setTarget(Entity target) {
        updateLastTarget(target);
        this.target = target;
        mob.putAttrib(AttributeKey.TARGET, new WeakReference<>(target));
        if (mob.isPlayer()) {
            System.out.printf("");
        }
    }

    public Entity lastTarget;

    private int lastTargetTimeoutTicks;

    private void updateLastTarget(Entity target) {
        if (target == null) // dont cancel this field
            return;
        lastTarget = target;
        lastTargetTimeoutTicks = 5;
    }

    private void checkLastTarget() {
        //System.out.println("lastTargetTimeoutTicks "+lastTargetTimeoutTicks+" lastTargetTimeoutTicks "+lastTargetTimeoutTicks);
        if (lastTargetTimeoutTicks > 0 && --lastTargetTimeoutTicks == 0) {
            lastTarget = null;
        }
    }

    public HitQueue getHitQueue() {
        return hitQueue;
    }

    public CombatSpell getCastSpell() {
        return castSpell;
    }

    public void setCastSpell(CombatSpell castSpell) {
        this.castSpell = castSpell;
    }

    public CombatSpell getAutoCastSpell() {
        return autoCastSpell;
    }

    public void setAutoCastSpell(CombatSpell autoCastSpell) {
        this.autoCastSpell = autoCastSpell;
    }

    public RangedWeapon getRangedWeapon() {
        return rangedWeapon;
    }

    public void setRangedWeapon(RangedWeapon rangedWeapon) {
        //System.out.printf("%s wep updated %s%n", mob, rangedWeapon);
        this.rangedWeapon = rangedWeapon;
    }

    public AttackType getAttackType() {
        return fightType.getAttackType();
    }

    public WeaponType getWeaponType() {
        return weapon;
    }

    public void setWeapon(WeaponType weapon) {
        this.weapon = weapon;
    }

    public FightType getFightType() {
        return fightType;
    }

    public void setFightType(FightType fightType) {
        this.fightType = fightType;
    }

    public boolean hasAutoReliateToggled() {
        return autoRetaliate;
    }

    public void setAutoRetaliate(boolean autoRetaliate) {
        this.autoRetaliate = autoRetaliate;
        if (mob instanceof Player) {
            mob.getAsPlayer().getPacketSender().sendConfig(172, autoRetaliate ? 1 : 0);
        }
    }

    private void checkGraniteMaul() {
        var graniteMaulTimeoutTicks = mob.<Integer>getAttribOr(AttributeKey.GRANITE_MAUL_TIMEOUT_TICKS, 0);
        if (graniteMaulTimeoutTicks > 0) {
            mob.putAttrib(AttributeKey.GRANITE_MAUL_TIMEOUT_TICKS, graniteMaulTimeoutTicks - 1);
            if (mob.<Integer>getAttribOr(AttributeKey.GRANITE_MAUL_TIMEOUT_TICKS, 0) == 0) {
                mob.putAttrib(AttributeKey.GRANITE_MAUL_SPECIALS, 0);
            } else if (graniteMaulTimeoutTicks == 4)
                //1 tick less than 5 because it was subtracted
                autoAttackGraniteMaul();
        }
    }

    /**
     * when in range of 1x1 target, re-focus the previous target
     */
    private void autoAttackGraniteMaul() {
        // Define our target as last entity we attacked
        if (target != null || lastTarget == null)
            return;
        if (mob.getZ() != lastTarget.getZ())
            return;
        int x = mob.getAbsX();
        int y = mob.getAbsY();
        if (lastTarget.getSize() == 1) {
            int targetX = lastTarget.getAbsX();
            int targetY = lastTarget.getAbsY();
            int diffX = Math.abs(x - targetX);
            int diffY = Math.abs(y - targetY);
            if ((diffX + diffY) != 1)
                return;
        } else {
            Tile closestPos = RouteMisc.getClosestPosition(mob, lastTarget);
            int targetX = closestPos.getX();
            int targetY = closestPos.getY();
            int diffX = Math.abs(x - targetX);
            int diffY = Math.abs(y - targetY);
            if (diffX > 1 || diffY > 1)
                return;
        }
        mob.setEntityInteraction(lastTarget);
        setTarget(lastTarget);
    }

    /**
     * aka NPCCombat.follow0/follow in Runite
     */
    public void followTarget() {

        method = CombatFactory.getMethod(mob);

        mob.npc().findAgroTarget();

        if (target != null && method instanceof CommonCombatMethod ccm) {
            ccm.set(mob, target);
            ccm.doFollowLogic();
            ccm.process(mob, target);
        } else if (target != null) {
            DumbRoute.step(mob, target, method.moveCloseToTargetTileRange(mob));
        }
        checkRetreat();
    }

    /**
     * while {@link com.cryptic.model.entity.npc.NPCCombatInfo#aggroradius} is based on the NPCs current position, they could be
     * at a corner of their Walkable Area and attack. For NPCs with attack distance 1 (melee) you don't want players to safe-spot them
     * from outside their Attack/Walkable Area. to Solve this, 07 npcs walk away from the player, causing the player to move into the Attackable Box:
     * Acceptable:
     * |========================|
     * |---------------NPC------|
     * |------------------------|
     * |------------------------|
     * |------------------------|
     * |------------------------|
     * |------------------------|
     * |---PLAYER---------------|
     * |========================|
     * <p>
     * vs
     * Safe spotable:
     * |========================|
     * |------------------------|
     * |------------------------|
     * |------------------------|
     * |------------------------|
     * |------------------------|
     * |----NPC-----------------|
     * |------------------------|
     * |========================|
     * .........................
     * .........................
     * ..PLAYER-................
     * .........................
     * .........................
     */
    private void checkRetreat() {
        if (target == null && !CombatFactory.wasRecentlyAttacked(mob)) return;
        var hasAgroDistanceOverride = mob.hasAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE);
        var expand = hasAgroDistanceOverride ? mob.<Integer>getAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE) : 16;
        var fightArea = mob.npc().spawnTile().area(expand);
        BooleanSupplier cancelIf = () -> mob.npc().getCombat().inCombat() || mob.npc().getCombat().getTarget() != null;
        BooleanSupplier waitUntil = () -> mob.npc().tile().distanceTo(mob.npc().spawnTile()) == 0;
        AttributeKey attribute = AttributeKey.RETREATING;
        if (target != null) {
            boolean singleWayCombat = lastTargetTimeoutTicks == 0 && target instanceof Player player && player.getCombat().getTarget() != mob.npc() && !MultiwayCombat.includes(player.tile());
            boolean clippedTile = World.getWorld().clipAt(target.tile()) != 0 && !ProjectileRoute.hasLineOfSight(mob.npc(), target);
            boolean inBounds = mob.npc().tile().inArea(fightArea) && !target.tile().inArea(fightArea);
            if ((singleWayCombat || clippedTile || inBounds)) {
                if (!mob.npc().hasAttrib(attribute)) mob.npc().putAttrib(attribute, true);
                mob.npc().ignoreOccupiedTiles = true;
                mob.npc().getCombat().reset();
                if (method instanceof CommonCombatMethod ccm) ccm.onRetreat(mob.npc(), waitUntil, cancelIf, attribute);
                DumbRoute.route(mob, mob.npc().spawnTile().getX(), mob.npc().spawnTile().getY());
                Debugs.CMB.debug(mob, "retreating against non reachable target", target);
            }
        }
    }

    @Override
    public String toString() {
        return "Combat{" +
            "damageMap=" + (damageMap == null ? "?" : damageMap.size()) +
            ", rangedWeapon=" + (rangedWeapon == null ? "None" : rangedWeapon) +
            ", hitQueue=" + (hitQueue == null ? "?" : hitQueue.size()) +
            ", mob=" + (mob == null ? "null" : mob) +
            ", target=" + (target == null ? "null" : target) +
            ", method=" + (method == null ? "null" : method) +
            ", fightType=" + (fightType == null ? "null" : fightType) +
            ", weapon=" + (weapon == null ? "?" : weapon.name()) +
            ", autoRetaliate=" + autoRetaliate +
            ", castSpell=" + (castSpell == null ? "none" : castSpell.name()) +
            ", weps: " + (mob != null && mob.isPlayer() ? ("wepid: " + mob.getAsPlayer().getEquipment().getId(EquipSlot.WEAPON) + " ammo=" +
            mob.getAsPlayer().getEquipment().getId(EquipSlot.AMMO)) : " useless:" + mob) +
            '}';
    }

    private final Stopwatch fightTimer = Stopwatch.createUnstarted();

    public Stopwatch getFightTimer() {
        return fightTimer;
    }

    public boolean inCombat() {
        return CombatFactory.inCombat(mob);
    }

}
