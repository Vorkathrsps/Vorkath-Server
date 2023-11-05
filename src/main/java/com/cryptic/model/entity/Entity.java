package com.cryptic.model.entity;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.core.TimesCycle;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.TickAndStop;
import com.cryptic.core.task.impl.TickableTask;
import com.cryptic.model.World;
import com.cryptic.model.action.ActionManager;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.*;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.AttackNpcListener;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken.KrakenInstance;
import com.cryptic.model.entity.masks.*;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.masks.impl.tinting.Tinting;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.InfectionType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Boundary;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.model.map.region.Region;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.RouteFinder;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.utility.Color;
import com.cryptic.utility.Debugs;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;
import com.cryptic.utility.timers.TimerRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.cryptic.model.entity.attributes.AttributeKey.NO_MOVEMENT_NIGHTMARE;
import static com.cryptic.model.entity.attributes.AttributeKey.VENOMED_BY;

@Slf4j
public abstract class Entity {

    /**
     * runite, if entity's process() has been called. used to determine which entity e1 or e2
     * <br> was handled first in 1 cycle
     */
    public boolean processed;
    public boolean occupyingTiles;

    public Entity() {
    }

    /**
     * The Entity constructor.
     *
     * @param tile The position the entity is currently in.
     */
    public Entity(NodeType type, Tile tile) {
        this.tile = tile;
        this.type = type;
        this.lastKnownRegion = tile;
    }

    /**
     * The entity's type.
     */
    private NodeType type;

    /**
     * The entity's unique index.
     */
    private int index;

    /**
     * The entity's tile size.
     */
    @Setter private int size;

    protected Tile tile;

    /**
     * The entity's first position in current map region.
     */
    private Tile lastKnownRegion;

    /**
     * Gets this type.
     *
     * @return
     */
    public NodeType getNodeType() {
        return type;
    }

    public boolean finished() {
        return index < 1;
    }

    /**
     * Gets the entity's unique index.
     *
     * @return The entity's index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the entity's index.
     *
     * @param index The value the entity's index will contain.
     * @return The Entity instance.
     */
    public Entity setIndex(int index) {
        this.index = index;
        return this;
    }

    /**
     * Gets this entity's first position upon entering their
     * current map region.
     *
     * @return The lastKnownRegion instance.
     */
    public Tile getLastKnownRegion() {
        return lastKnownRegion;
    }

    /**
     * Sets the entity's current region's position.
     *
     * @param lastKnownRegion The position in which the player first entered the current region.
     * @return The Entity instance.
     */
    public Entity setLastKnownRegion(Tile lastKnownRegion) {
        this.lastKnownRegion = lastKnownRegion;
        return this;
    }

    public abstract Entity setHitpoints(int hitpoints);

    protected abstract void die();

    public abstract int getBaseAttackSpeed();

    public abstract int attackAnimation();

    public abstract int getBlockAnim();

    public abstract void hp(int hp, int exceed);

    public abstract int hp();

    public abstract int maxHp();

    /**
     * Gets the width of the entity.
     *
     * @return The width of the entity.
     */
    public abstract int xLength();

    /**
     * Gets the width of the entity.
     *
     * @return The width of the entity.
     */
    public abstract int yLength();

    /**
     * Gets the centre position of the entity.
     *
     * @return The centre Position of the entity.
     */
    public abstract Tile getCentrePosition();

    /**
     * Gets the projectile lockon index of this mob.
     *
     * @return The projectile lockon index of this mob.
     */
    public abstract int getProjectileLockonIndex();

    /**
     * An abstract method used for handling actions
     * once this entity has been added to the world.
     */
    public abstract void onAdd();

    /**
     * An abstract method used for handling actions
     * once this entity has been removed from the world.
     */
    public abstract void onRemove();

    public abstract Hit manipulateHit(Hit hit);

    public boolean isAt(Tile pos) {
        return isAt(pos.getX(), pos.getY());
    }

    public boolean isAt(int x, int y) {
        return tile.getX() == x && tile.getY() == y;
    }

    public int getX() {
        return tile().getX();
    }

    public int getY() {
        return tile().getY();
    }

    public int getZ() {
        return tile().getLevel();
    }

    /**
     * Sets the entity position
     *
     * @param tile the world position
     */
    public Entity setTile(Tile tile) {
        if (tile == null)
            throw new RuntimeException("wtf");
        this.tile = tile;
        return this;
    }

    /**
     * Gets the entity position.
     *
     * @return the entity's world position
     */
    public Tile tile() {
        return tile;
    }

    /**
     * gets the entity's tile size.
     *
     * @return The size the entity occupies in the world.
     */
    public int getSize() {
        return size;
    }

    public boolean isNpc() {
        return this instanceof NPC;
    }

    public boolean isPlayer() {
        return this instanceof Player;
    }

    public Player getAsPlayer() {
        return ((Player) this);
    }

    public Player player() {
        return ((Player) this);
    }

    public NPC getAsNpc() {
        return ((NPC) this);
    }

    public NPC npc() {
        return ((NPC) this);
    }

    /**
     * a task that runs every 1 game tick. Aka repeatingTask
     * <br> IMPORTANT: not a chain, this is uninterruptable, unless stop is hardcoded
     */
    public TickableTask repeatingTask(Consumer<TickableTask> r) {
        TickableTask task = new TickableTask(true, 1) {
            @Override
            protected void tick() {
                r.accept(this);
            }
        };
        task.bind(this);
        TaskManager.submit(task);
        return task;
    }

    public Chain<Entity> repeatingTask(int tickBetweenLoop, Consumer<Task> work) {
        return Chain.bound(this).repeatingTask(tickBetweenLoop, work);
    }

    public Chain<Entity> conditionalRepeatingTask(String name, BooleanSupplier condition, int tickBetweenLoop, Consumer<Task> work) {
        return Chain.<Entity>noCtx().repeatingTask(tickBetweenLoop, work).name(name).cancelWhen(condition);
    }

    @Getter
    @Setter
    private KrakenInstance krakenInstance;

    @Getter
    public Hit hits = new Hit(this, this);

    /**
     * a task that runs Once after {@code delay} ticks.
     */
    public TickAndStop runOnceTask(int delay, Consumer<TickAndStop> r) {
        TickAndStop task = new TickAndStop(delay) {
            @Override
            public void executeAndStop() {
                r.accept(this);
            }
        };
        task.bind(this);
        TaskManager.submit(task);
        return task;
    }

    public Player[] closePlayers() {
        return closePlayers(254, 14);
    }

    /**
     * Max player capacity 254 since the client limit is 255 including yourself.
     */
    public Player[] closePlayers(int span) {
        return closePlayers(254, span);
    }

    public Player[] closePlayers(int maxCapacity, int span) {
        Player[] targs = new Player[maxCapacity];
        int caret = 0;
        for (int idx = 0; idx < 2048; idx++) {
            Player p = World.getWorld().getPlayers().get(idx);
            if (p == null || p == this || p.tile() == null || p.tile().level != tile().level || p.looks().hidden() || p.finished()) {
                continue;
            }
            if (tile().distance(p.tile()) > span)
                continue;
            if (p.tile().inSqRadius(tile, span)) {
                targs[caret++] = p;
            }
            if (caret >= targs.length) {
                break;
            }
        }
        Player[] set = new Player[caret];
        System.arraycopy(targs, 0, set, 0, caret);
        return set;
    }

    public NPC[] closeNpcs(int span) {
        return closeNpcs(254, span);
    }

    public NPC[] closeNpcs(int maxCapacity, int span) {
        NPC[] targs = new NPC[maxCapacity];

        int caret = 0;
        for (int idx = 0; idx < World.getWorld().getNpcs().capacity(); idx++) {
            NPC npc = World.getWorld().getNpcs().get(idx);
            if (npc == null || npc == this || npc.tile() == null || npc.tile().level != tile().level || npc.finished()) {
                continue;
            }
            if (tile().distance(npc.tile()) > span)
                continue;
            if (npc.tile().inSqRadius(tile, span)) {
                targs[caret++] = npc;
            }
            if (caret >= targs.length) {
                break;
            }
        }
        NPC[] set = new NPC[caret];
        System.arraycopy(targs, 0, set, 0, caret);
        return set;
    }

    public boolean dead() {
        return hp() < 1;
    }

    public void message(String format, Object... params) {
        // Stub to ease player-specific messaging
    }

    /**
     * The player's head icon hint.
     */
    private int headHint = -1;


    /**
     * Gets the player's current head hint index.
     *
     * @return The player's head hint.
     */
    public int getHeadHint() {
        return headHint;
    }

    /**
     * Sets the player's head icon hint.
     *
     * @param headHint The hint index to use.
     * @return The Appearance instance.
     */
    public Entity setHeadHint(int headHint) {
        this.headHint = headHint;
        getUpdateFlag().flag(Flag.APPEARANCE);
        return this;
    }

    /**
     * Is this entity registered.
     */
    private boolean registered;

    public Graphic graphic() {
        return graphic;
    }

    public Tinting tinting() {
        return tinting;
    }

    public Animation getAnimation() {
        return animation;
    }

    @Setter
    private Tile faceTile;

    /**
     * Gets the face tile.
     *
     * @return The face tile, or <code>null</code> if the entity is not facing.
     */
    public @Nullable Tile getFaceTile() {
        return faceTile;
    }

    @Getter
    public Tile lastTileFaced;

    public Entity setPositionToFace(Tile tile) {
        this.getUpdateFlag().flag(Flag.FACE_TILE);
        this.faceTile = tile;
        this.lastTileFaced = tile;
        return this;
    }

    /**
     * @return GAME TICKS UNTIL PROJECTILE REACHES END TILE
     */
    public int executeProjectile(Projectile projectile) {
        if (projectile == null) {
            return 0;
        }

        Tile source = projectile.getStart();
        Tile target = projectile.getTarget();

        if (target == null) {
            return 0;
        }

        int creatorSize = projectile.getCreatorSize() == -1 ? getSize() : projectile.getCreatorSize();

        Tile distance = source.getDistanceTo(target);

        if (distance.getX() <= 64 && distance.getY() <= 64) {
            for (Player player : World.getWorld().getPlayers()) {
                if (player == null) {
                    continue;
                }

                if (source.isViewableFrom(player.getCentrePosition())) {
                    player.getPacketSender()
                        .sendProjectile(projectile.getStart(), projectile.getOffset(), projectile.getAngle(), projectile.getSpeed(), projectile.getProjectileID(), projectile.getStartHeight(), projectile.getEndHeight(), projectile.getLockon(), projectile.getDelay(), projectile.getSlope(), creatorSize, projectile.getStartDistanceOffset());
                }
            }
        }

        return projectile.getTime(projectile.getStart(), projectile.getEnd());
    }

    public Hit getHits(Entity attacker, Entity target) {
        return new Hit(attacker, target);
    }

    public UpdateFlag getUpdateFlag() {
        return updateFlag;
    }

    public abstract MovementQueue getMovementQueue();

    public Combat getCombat() {
        return combat;
    }

    public Entity getInteractingEntity() {
        return interactingEntity;
    }

    public String getForcedChat() {
        return forcedChat;
    }

    public Entity setForcedChat(String forcedChat) {
        this.forcedChat = forcedChat;
        return this;
    }

    public boolean[] getPrayerActive() {
        return prayerActive;
    }

    public Entity setPrayerActive(int id, boolean prayerActive) {
        this.prayerActive[id] = prayerActive;
        return this;
    }

    public void decrementHealth(Hit hit) {
        if (dead())
            return;

        if (hit.getHitMark() == HitMark.HEALED) {
            heal(hit.getDamage());
            return;
        }

        int outcome = hp() - hit.getDamage();
        if (outcome < 0) {
            outcome = 0;
            putAttrib(AttributeKey.KILLING_BLOW_HIT, hit);
        }

        setHitpoints(outcome);

        if (isNpc() && hp() <= 0) {
            if (getAsNpc().getCombatMethod() != null && getAsNpc().getCombatMethod().customOnDeath(hit)) {
                return;
            }
        }

        if (hp() < 1 && !locked()) {
            die();
        }
    }

    public Boundary getBoundary() {
        int x = tile().getX();
        int y = tile().getY();
        int size = (getSize() - 1);
        return new Boundary(x, x + size, y, y + size);
    }

    public Boundary boundaryBounds() {
        return new Boundary(tile(), getSize());
    }

    public Boundary boundaryBounds(int enlarge) {
        return new Boundary(tile().getX() - enlarge, tile().getY() - enlarge, (tile().getX() + getSize() - 1) + enlarge,
            (tile().getY() + getSize() - 1) + enlarge, tile().getLevel());
    }

    public Boundary exactBoundsEnlarged(int enlarge) {
        return new Boundary(tile().getX() - enlarge, tile().getY() - enlarge, (tile().getX() + getSize()) + enlarge,
            (tile().getY() + getSize()) + enlarge, tile().getLevel());
    }

    public Boundary getBounds() {
        return new Boundary(tile().getX(), tile().getY(), tile().getX() + getSize() - 1, tile().getY() + getSize() - 1, tile().getLevel());
    }

    public Area bounds(int enlargedBy) {
        return new Area(tile.x - enlargedBy, tile.y - enlargedBy, (tile.x + getSize() - 1) + enlargedBy, (tile.y + getSize() - 1) + enlargedBy);
    }

    public Area bounds() {
        return new Area(tile.x, tile.y, tile.x + getSize() - 1, tile.y + getSize() - 1, tile.getZ());
    }

    public List<Hit> nextHits = new ArrayList<>(4);

    public void setWalkingDirection(Direction walkDirection) {
        this.walkingDirection = walkDirection;
    }

    public void setRunningDirection(Direction runDirection) {
        this.runningDirection = runDirection;
    }

    public Direction getWalkingDirection() {
        return walkingDirection;
    }

    public Direction getRunningDirection() {
        return runningDirection;
    }

    /**
     * Determines if this mob needs to reset their movement queue.
     *
     * @return {@code true} if this mob needs to reset their movement
     * queue, {@code false} otherwise.
     */
    public final boolean isResetMovementQueue() {
        return resetMovementQueue;
    }

    /**
     * Gets if this entity is registered.
     *
     * @return the unregistered.
     */
    public boolean isRegistered() {
        return registered;
    }

    /**
     * Sets if this entity is registered,
     *
     * @param registered the registered to set.
     */
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    /**
     * Sets the value for resetMovementQueue.
     *
     * @param resetMovementQueue the new value to set.
     */
    public final void setResetMovementQueue(boolean resetMovementQueue) {
        this.resetMovementQueue = resetMovementQueue;
    }

    @Getter
    @Setter
    public int tinted = 0;
    @Getter
    List<NPC> activeThrall = new ArrayList<>();

    public void setNeedsPlacement(boolean needsPlacement) {
        this.needsPlacement = needsPlacement;
    }

    public boolean isNeedsPlacement() {
        return needsPlacement;
    }

    public boolean isSpecialActivated() {
        return specialActivated;
    }

    public void setSpecialActivated(boolean specialActivated) {
        this.specialActivated = specialActivated;
    }

    public int getSpecialAttackPercentage() {
        return specialAttackPercentage;
    }

    public void setSpecialAttackPercentage(int specialAttackPercentage) {
        this.specialAttackPercentage = specialAttackPercentage;
    }

    public void desecreaseSpecialAttack(int drainAmount) {
        this.specialAttackPercentage -= drainAmount;

        if (specialAttackPercentage < 0) {
            specialAttackPercentage = 0;
        }
    }

    public void restoreSpecialAttack(int percentage) {
        if (specialAttackPercentage >= 100)
            return;
        specialAttackPercentage += specialAttackPercentage > (100 - percentage) ? 100 - specialAttackPercentage : percentage;
        CombatSpecial.updateBar(((Player) this));
    }

    public boolean isRecoveringSpecialAttack() {
        return recoveringSpecialAttack;
    }

    public void setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
        this.recoveringSpecialAttack = recoveringSpecialAttack;
    }

    public boolean inDungeon() {
        return false;
    }

    public int distanceToPoint(int pointX, int pointY) {
        return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public boolean isFixingDiagonal() {
        return fixingDiagonal;
    }

    public void setFixingDiagonal(boolean fixingDiagonal) {
        this.fixingDiagonal = fixingDiagonal;
    }

    public boolean isRepositioning() {
        return repositioning;
    }

    public void setRepositioning(boolean repositioning) {
        this.repositioning = repositioning;
    }

    protected Map<AttributeKey, Object> attribs;

    public boolean hasAttrib(AttributeKey key) {
        return attribs != null && attribs.containsKey(key);
    }

    /**
     * Gets an attribute without a default value.
     * Make sure to be careful using this, to avoid
     * NullPointerExceptions because of no default value.
     *
     * @param key
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttrib(AttributeKey key) {
        return attribs == null ? null : (T) attribs.get(key);
    }

    /**
     * Gets an attribute with a default value.
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribOr(AttributeKey key, Object defaultValue) {
        return attribs == null ? (T) defaultValue : (T) attribs.getOrDefault(key, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrT(AttributeKey key, T defaultValue) {
        return attribs == null ? (T) defaultValue : (T) attribs.getOrDefault(key, defaultValue);
    }

    public void clearAttrib(AttributeKey key) {
        if (attribs != null)
            attribs.remove(key);
    }

    public void clearAttribs() {
        attribs.clear();
    }

    public Object putAttrib(AttributeKey key, Object v) {
        if (attribs == null)
            attribs = new EnumMap<>(AttributeKey.class);
        return attribs.put(key, v);
    }

    /**
     * Modifies the current numerical value of an attribute.
     *
     * @param key          the key of the attribute to be changed.
     * @param modifier     the value that will be modifying the current value.
     * @param defaultValue the default value to be inserted if none exists.
     * @param <T>          the type of number being modified.
     * @throws IllegalArgumentException thrown when the current value returned from the key is not parsable to numerical value
     *                                  or if the modifier and defaultValue are not the same class.
     */
    public <T extends Number> void modifyNumericalAttribute(AttributeKey key, T modifier, T defaultValue) throws
        IllegalArgumentException {
        Preconditions.checkArgument(modifier.getClass() == defaultValue.getClass(),
            "Modifier and defaultValue must have same class.");

        Number current = getAttribOr(key, defaultValue);

        if (current.getClass() == Byte.class) {
            putAttrib(key, current.byteValue() + modifier.byteValue());
        } else if (current.getClass() == Short.class) {
            putAttrib(key, current.shortValue() + modifier.shortValue());
        } else if (current.getClass() == Integer.class) {
            putAttrib(key, current.intValue() + modifier.intValue());
        } else if (current.getClass() == Long.class) {
            putAttrib(key, current.longValue() + modifier.longValue());
        } else if (current.getClass() == Float.class) {
            putAttrib(key, current.floatValue() + modifier.floatValue());
        } else if (current.getClass() == Double.class) {
            putAttrib(key, current.doubleValue() + modifier.doubleValue());
        } else {
            throw new IllegalArgumentException("current value isn't a parsable number.");
        }
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static int getAttribIntOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Integer.class, () -> 0);
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static long getAttribLongOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Long.class, () -> 0L);
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static boolean getAttribBooleanOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Boolean.class, () -> false);
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static String getAttribStringOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, String.class, () -> "");
    }

    /**
     * If there isn't a method for your type, it's probably not a simple primitive. Use {@link Player#getAttribTypeOr(Entity, AttributeKey, Object, Class, Supplier)} instead.
     */
    public static double getAttribDoubleOr(Entity player, AttributeKey key, @Nullable Object defaultValue) {
        return getAttribTypeOr(player, key, defaultValue, Double.class, () -> 0d);
    }

    static final Class<?>[] DISALLOWED = new Class[]{int.class, float.class, byte.class, double.class, long.class, short.class};

    /**
     * To avoid class cast exceptions when dealing with {@link AttributeKey}, since OSS' attributeMap doesn't enforce types,
     * and specifically for mission-critical player serialization (we don't care about runtime/gameplay errors.. yet)
     * do some hardcoded type checks so serialization never breaks and we don't lose players progress. Supplier is the concrete return type, will always be 100% correct matching the expected type of an attrib's values. defaultValue is an object, has no type checking, and can be totally wrong. The point of this method is to only use default if the type won't produce a CCE.
     *
     * @param defaultValue The offending generic -- no way to get runtime types from the <T> returned generic type..
     *                     so no way to compare this class to T
     * @param type         Enforces the type check to avoid serialization exceptions
     * @param supplier     The correct return type.
     * @author Shadowrs/Jak
     * @since 06/06/2020
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAttribTypeOr(@NotNull Entity player, @NotNull AttributeKey key, @Nullable Object defaultValue, @NotNull Class<T> type, @NotNull Supplier<T> supplier) {
        Preconditions.checkArgument(Arrays.stream(DISALLOWED).noneMatch(p -> type == p), "You cannot use type %s", type.getName());
        if (!player.hasAttrib(key)) {
            if (defaultValue == null && !type.isPrimitive() || defaultValue != null && defaultValue.getClass() == type) {
                return (T) defaultValue;
            } else {
                String msg = String.format("CRITICAL ERROR: wrong fallback Type associated with AttributeKey %s (expected: %s, but got: %s) when saving Player: %s. Using fallback value %s",
                    key, type, defaultValue == null ? "null" : defaultValue.getClass(), player.getMobName(), supplier.get());
                //logger.error(msg);
                if (!GameServer.properties().production) {
                    throw new RuntimeException("IncorrectFallbackTypeException");
                }
                return supplier.get();
            }
        }
        @Nullable Object stored = player.getAttrib(key);
        if (stored == null && !type.isPrimitive() || stored != null && stored.getClass() == type) {
            return (T) stored;
        } else {
            String msg = String.format("CRITICAL ERROR: wrong stored Type associated with AttributeKey %s (expected: %s, but got: %s) when saving Player: %s. Data loss possible. Replacing value '%s' with %s",
                key, type, stored == null ? "null" : stored.getClass(), player.getMobName(),
                stored, supplier.get());
            // logger.error(msg);
            if (!GameServer.properties().production) {
                throw new RuntimeException("IncorrectStoredTypeException");
            }
            return supplier.get();
        }
    }

    /**
     * doesnt return {@code Hit} instance because its immidiately submitted() so you cant change properties after.
     */
    public void hit(Entity attacker, int damage) {
        hit(attacker, damage, HitMark.DEFAULT);
    }

    /**
     * doesnt return {@code Hit} instance because its immidiately submitted() so you cant change properties after.
     */
    public void hit(Entity attacker, int damage, int delay) {
        hit(attacker, damage, HitMark.DEFAULT);
    }

    /**
     * doesnt return {@code Hit} instance because its immidiately submitted() so you cant change properties after.
     */
    public void hit(Entity attacker, int damage, HitMark type) {
        hit(attacker, damage, 0, null, type);
    }

    /**
     * doesnt return {@code Hit} instance because its immidiately submitted() so you cant change properties after.
     */
    public void hit(Entity attacker, int damage, CombatType combatType, HitMark type) {
        hit(attacker, damage, 0, combatType, type);
    }

    /**
     * Use a builder pattern, allowing you to call methods to change properties of Hit before calling {@code CombatFactory.addPendingHit(hit);}
     */
    public Hit hit(Entity attacker, int damage, int delay, CombatType type) {
        Hit hit = Hit.builder(attacker, this, damage, delay, type);
        return hit;
    }

    /**
     * Use a builder pattern, allowing you to call methods to change properties of Hit before calling {@code CombatFactory.addPendingHit(hit);}
     */
    public Hit hit(Entity attacker, int damage, CombatType type) {
        Hit hit = Hit.builder(attacker, this, damage, 0, type);
        return hit;
    }

    /**
     * doesn't return {@code Hit} instance because It's immediately submitted() so you can't change properties after.
     */
    public void healHit(Entity attacker, int heal) {
        hit(attacker, heal, null, HitMark.HEALED);
    }

    /**
     * doesn't return {@code Hit} instance because It's immediately submitted() so you can't change properties after.
     */
    public void healHit(Entity attacker, int heal, int delay) {
        hit(attacker, heal, delay, null, HitMark.HEALED);
    }

    /**
     * doesn't return {@code Hit} instance because It's immediately submitted() so you can't change properties after.
     */
    public void hit(Entity attacker, int damage, int delay, CombatType combatType, HitMark type) {
        hit(attacker, damage, delay, combatType).setIsReflected().setHitMark(type).submit();
    }

    protected boolean noRetaliation = false;

    public void noRetaliation(boolean b) {
        noRetaliation = b;
    }

    public void autoRetaliate(Entity attacker) {
        boolean sameAttacker = attacker == this;

        if (dead() || hp() < 1 || (isPlayer() && !getCombat().hasAutoReliateToggled()) || noRetaliation || locked() || stunned() || sameAttacker) {
            Debugs.CMB.debug(attacker, "auto ret: dead: " + dead() + " hp: " + hp() + " autoret: " + getCombat().hasAutoReliateToggled() + " noRetaliation: " + noRetaliation + " locked: " + locked() + " stunned: " + stunned() + " sameAttacker: " + sameAttacker, this, true);
            return;
        }

        // As soon as the hit on us appears, we'll turn around and face the attacker.
        setEntityInteraction(attacker);

        startEvent(2, () -> {
            if (attacker.dead() || dead())
                return;
            // Override logic
            getCombat().setTarget(attacker);
            // this mob needs to hit the attacker, not vice versa
            this.getCombat().attack(attacker);
        });
        //System.out.println(this.getMobName()+" retal to "+attacker.getMobName());
    }

    /**
     * Sounds that happen when the hit appears.
     * Two distinct: a sound if damage>0 .. and a shield block sound.
     * Note: these sounds are special because they are _personal_ 'effect' sounds - not AREA sounds broadcast to closeby players.
     */
    public void takehitSound(Hit hit) {
        if (hit == null)
            return;
        // block sounds depends entirely on entity type

        if (hit.getAttacker() != null && hit.getAttacker() instanceof Player) {
            if (hit.getDamage() > 0) {

            }
            //TODO
        }
    }

    @Getter
    public long lockTime;

    public LockType getLock() {
        return lock;
    }

    private LockType lock = LockType.NONE;

    public boolean locked() {
        return lock != null && lock != LockType.NONE;
    }

    public String lockState() {
        return lock == null ? "NULL" : lock.name();
    }

    public boolean isLogoutOkLocked() {
        return lock == LockType.FULL_LOGOUT_OK;
    }

    public boolean isMoveLocked() {
        return lock == LockType.MOVEMENT || lock == LockType.MOVEMENT_DAMAGE_OK;
    }

    public boolean isMoveLockedDamageOk() {
        return lock == LockType.MOVEMENT_DAMAGE_OK;
    }


    public void lock() {
        lock = LockType.FULL;
        lockTime = System.currentTimeMillis();
    }

    public void logoutLock() {
        this.lock = LockType.FULL_LOGOUT_OK;
        lockTime = System.currentTimeMillis();
    }

    public void lockNoDamage() {
        lock = LockType.NULLIFY_DAMAGE;
        lockTime = System.currentTimeMillis();
    }

    public void lockDelayDamage() {
        lock = LockType.DELAY_DAMAGE;
        lockTime = System.currentTimeMillis();
    }

    public void lockDamageOk() {
        lock = LockType.FULL_WITHDMG;
        lockTime = System.currentTimeMillis();
    }

    public void lockMoveDamageOk() {
        lock = LockType.MOVEMENT_DAMAGE_OK;
        lockTime = System.currentTimeMillis();
    }

    public boolean isNullifyDamageLock() {
        return lock == LockType.NULLIFY_DAMAGE;
    }

    public boolean isDelayDamageLocked() {
        lockTime = System.currentTimeMillis();
        return lock == LockType.DELAY_DAMAGE;
    }

    /**
     * Locked, unable to attack. Cerberus.
     */
    public boolean isNoAttackLocked() {
        return lock == LockType.NO_ATTACK;
    }

    public boolean isDamageOkLocked() {
        return lock == LockType.FULL_WITHDMG;
    }

    public void lockMovement() {
        lock = LockType.MOVEMENT;
        lockTime = System.currentTimeMillis();
    }

    public void lockNoAttack() {
        lock = LockType.NO_ATTACK;
        lockTime = System.currentTimeMillis();
    }

    public void unlock() {
        lock = LockType.NONE;
    }

    public boolean canWalkNPC(int toX, int toY) {
        return canWalkNPC(toX, toY, false);
    }

    public boolean canWalkNPC(int toX, int toY, boolean checkUnder) {
        if (this.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) != 1 /*|| (!checkUnder && !canWalkNPC(getX(), getY(), true))*/)
            return true;
        int size = getSize();
       /* for(int regionId : getMapRegionsIds()) {
            List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
            if(npcIndexes != null) {
                for(int npcIndex : npcIndexes) {
                    NPC target = World.getWorld().getNpcs().get(npcIndex);
                    if(target == null || target == this || target.isDead() || !target.isRegistered() || target.getZ() != getZ() || !AreaManager.inMulti(target))
                        continue;
                    int targetSize = target.getSize();
                    if(!checkUnder && target.getNextWalkDirection() == -1) { //means the walk hasnt been processed yet
                        int previewDir = getPreviewNextWalkStep();
                        if(previewDir != -1) {
                            Position tile = target.getPosition().transform(Directions.DIRECTION_DELTA_X[previewDir],
                                Directions.DIRECTION_DELTA_Y[previewDir], 0);
                            if(colides(tile.getX(), tile.getY(), targetSize, getX(), getY(), size))
                                continue;

                            if(colides(tile.getX(), tile.getY(), targetSize, toX, toY, size))
                                return false;
                        }
                    }
                    if(colides(target.getX(), target.getY(), targetSize, getX(), getY(), size))
                        continue;
                    if(colides(target.getX(), target.getY(), targetSize, toX, toY, size))
                        return false;
                }
            }
        }*/
        return true;
    }

    private static boolean colides(int x1, int y1, int size1, int x2, int y2, int size2) {
        for (int checkX1 = x1; checkX1 < x1 + size1; checkX1++) {
            for (int checkY1 = y1; checkY1 < y1 + size1; checkY1++) {
                for (int checkX2 = x2; checkX2 < x2 + size2; checkX2++) {
                    for (int checkY2 = y2; checkY2 < y2 + size2; checkY2++) {
                        if (checkX1 == checkX2 && checkY1 == checkY2)
                            return true;
                    }

                }
            }
        }
        return false;
    }

    public void resetWalkSteps() {
        getMovementQueue().clear();
    }

    public Tile getPreviousTile() {
        if (previousTile == null) {
            previousTile = RouteFinder.findWalkable(tile());
        }
        return previousTile;
    }

    public void setPreviousTile(Tile previousTile) {
        this.previousTile = previousTile;
    }

    private Tile previousTile;

    private Skills skills;

    public Skills getSkills() {
        return skills;
    }

    public Skills skills() {
        return skills;
    }

    public boolean poison(int damage) {
        return poison(damage, true);
    }

    private InfectionType infectionType;

    public InfectionType infection() {
        return infectionType;
    }

    public void setInfection(InfectionType infectionType) {
        this.infectionType = infectionType;
        if (this.isPlayer()) {
            this.getAsPlayer().getPacketSender().sendInfection(infectionType);
            this.getUpdateFlag().flag(Flag.APPEARANCE);
        }
    }

    public boolean poison(int dmg, boolean sendmsg) {
        int venomState = getAttribOr(AttributeKey.VENOM_TICKS, 0);
        // Can't inflict poison if venomed, it takes priority.
        if (venomState > 0)
            return false;

        if (isPlayer()) {
            Player p = (Player) this;
            if (Equipment.venomHelm(p)) { // Serp helm stops poison.
                return false;
            }
            if ((int) getAttribOr(AttributeKey.POISON_TICKS, 0) != 0) {
                // Immune or already poisoned.
                return false;
            }
            if (sendmsg)
                message("You have been poisoned!");
            p.setInfection(InfectionType.POISON_INFECTION);
            p.putAttrib(AttributeKey.POISON_TICKS, Poison.ticksForDamage(dmg));
        } else if (isNpc()) {
            NPC me = (NPC) this;
            if (me.isPoisonImmune())
                return false;
            if ((int) getAttribOr(AttributeKey.POISON_TICKS, 0) != 0) {
                // Immune / already poisoned
                return false;
            }
            putAttrib(AttributeKey.POISON_TICKS, Poison.ticksForDamage(dmg));
        }
        return true;
    }

    private static final int[] NPCS_IMMUNE_TO_VENOM = new int[]
        {
            NpcIdentifiers.WHIRLPOOL,
            NpcIdentifiers.WHIRLPOOL_496,
            NpcIdentifiers.WHIRLPOOL_5534,
            NpcIdentifiers.ENORMOUS_TENTACLE,
            NpcIdentifiers.ENORMOUS_TENTACLE_10708,
            NpcIdentifiers.ENORMOUS_TENTACLE_10709,
            NpcIdentifiers.CORPOREAL_BEAST,
            NpcIdentifiers.THE_NIGHTMARE_9425,
            NpcIdentifiers.THE_NIGHTMARE_9430,
            NpcIdentifiers.VERZIK_VITUR_8373,
            NpcIdentifiers.VERZIK_VITUR,
            NpcIdentifiers.VERZIK_VITUR_8371,
            NpcIdentifiers.VERZIK_VITUR_8370,
            NpcIdentifiers.VERZIK_VITUR_8372,
            NpcIdentifiers.VERZIK_VITUR_8374,
            NpcIdentifiers.VERZIK_VITUR_8375,
            NpcIdentifiers.VERZIK_VITUR_8373,
            NpcIdentifiers.TOTEM,
            NpcIdentifiers.TOTEM_9435,
            NpcIdentifiers.TOTEM_9436,
            NpcIdentifiers.TOTEM_9437,
            NpcIdentifiers.TOTEM_9438,
            NpcIdentifiers.TOTEM_9439,
            NpcIdentifiers.TOTEM_9440,
            NpcIdentifiers.TOTEM_9441,
            NpcIdentifiers.TOTEM_9442,
            NpcIdentifiers.TOTEM_9443,
            NpcIdentifiers.TOTEM_9444,
            NpcIdentifiers.TOTEM_9445,
            NpcIdentifiers.CHAOS_ELEMENTAL,
            NpcIdentifiers.DAGANNOTH_PRIME,
            NpcIdentifiers.DAGANNOTH_REX,
            NpcIdentifiers.DAGANNOTH_SUPREME,
            NpcIdentifiers.DEMONIC_GORILLA,
            NpcIdentifiers.DEMONIC_GORILLA_7145,
            NpcIdentifiers.DEMONIC_GORILLA_7146,
            NpcIdentifiers.DEMONIC_GORILLA_7147,
            NpcIdentifiers.DEMONIC_GORILLA_7148,
            NpcIdentifiers.DEMONIC_GORILLA_7149,
            NpcIdentifiers.DEMONIC_GORILLA_7152,
            NpcIdentifiers.GIANT_MOLE,
            NpcIdentifiers.KALPHITE_QUEEN,
            NpcIdentifiers.KALPHITE_QUEEN_6501,
            NpcIdentifiers.KALPHITE_QUEEN_6500,
            NpcIdentifiers.KALPHITE_QUEEN_963,
            NpcIdentifiers.KALPHITE_QUEEN_965,
            NpcIdentifiers.KALPHITE_QUEEN_4303,
            NpcIdentifiers.KALPHITE_QUEEN_4304,
            NpcIdentifiers.KRAKEN,
            NpcIdentifiers.KRAKEN_6640,
            NpcIdentifiers.KRAKEN_6656,
            NpcIdentifiers.OBOR,
            NpcIdentifiers.TZTOKJAD,
            NpcIdentifiers.TZTOKJAD_6506,
            NpcIdentifiers.VETION,
            NpcIdentifiers.VETION_REBORN,
            NpcIdentifiers.ABYSSAL_SIRE,
            NpcIdentifiers.ABYSSAL_SIRE_5887,
            NpcIdentifiers.ABYSSAL_SIRE_5888,
            NpcIdentifiers.ABYSSAL_SIRE_5889,
            NpcIdentifiers.ABYSSAL_SIRE_5890,
            NpcIdentifiers.ABYSSAL_SIRE_5891,
            NpcIdentifiers.ABYSSAL_SIRE_11961,
            NpcIdentifiers.COMMANDER_ZILYANA,
            NpcIdentifiers.COMMANDER_ZILYANA_6493,
            NpcIdentifiers.CALLISTO,
            NpcIdentifiers.CALLISTO_6609,
            NpcIdentifiers.VENENATIS,
            NpcIdentifiers.VENENATIS_6610,
            NpcIdentifiers.COW,
            NpcIdentifiers.DAWN,
            NpcIdentifiers.DAWN_7852,
            NpcIdentifiers.DAWN_7853,
            NpcIdentifiers.DAWN_7884,
            NpcIdentifiers.DAWN_7885,
            NpcIdentifiers.GENERAL_GRAARDOR,
            NpcIdentifiers.GENERAL_GRAARDOR_6494,
            NpcIdentifiers.KREEARRA,
            NpcIdentifiers.KREEARRA_6492,
            NpcIdentifiers.KRIL_TSUTSAROTH,
            NpcIdentifiers.KRIL_TSUTSAROTH_6495,
            NpcIdentifiers.LIZARDMAN,
            NpcIdentifiers.MITHRIL_DRAGON,
            NpcIdentifiers.MITHRIL_DRAGON_8088,
            NpcIdentifiers.MITHRIL_DRAGON_8089,
            NpcIdentifiers.GREAT_OLM,
            NpcIdentifiers.GREAT_OLM_7554,
            NpcIdentifiers.GREAT_OLM_LEFT_CLAW,
            NpcIdentifiers.GREAT_OLM_LEFT_CLAW_7555,
            NpcIdentifiers.GREAT_OLM_RIGHT_CLAW,
            NpcIdentifiers.GREAT_OLM_RIGHT_CLAW_7553,
            NpcIdentifiers.LIZARDMAN_SHAMAN,
            NpcIdentifiers.LIZARDMAN_SHAMAN_6767,
            NpcIdentifiers.LIZARDMAN_SHAMAN_7573,
            NpcIdentifiers.LIZARDMAN_SHAMAN_7574,
            NpcIdentifiers.LIZARDMAN_SHAMAN_7744,
            NpcIdentifiers.LIZARDMAN_SHAMAN_8565,
            NpcIdentifiers.ZULRAH,
            NpcIdentifiers.ZULRAH_2044,
            NpcIdentifiers.ZULRAH_2043,
            NpcIdentifiers.THERMONUCLEAR_SMOKE_DEVIL,
            NpcIdentifiers.TZKALZUK,
            NpcIdentifiers.VORKATH,
            NpcIdentifiers.VORKATH_8061,
            NpcIdentifiers.VORKATH_8059,
            NpcIdentifiers.VORKATH_8058,
            NpcIdentifiers.VORKATH_8060,
            NpcIdentifiers.VORKATH_11959,
            NpcIdentifiers.WYRM,
            NpcIdentifiers.WYRM_8611,
            NpcIdentifiers.SNAKELING,
            NpcIdentifiers.SNAKELING_2127,
            NpcIdentifiers.SNAKELING_2128,
            NpcIdentifiers.SNAKELING_2129,
            NpcIdentifiers.SNAKELING_2130,
            NpcIdentifiers.SNAKELING_2131,
            NpcIdentifiers.LIZARDMAN_BRUTE,
            NpcIdentifiers.LIZARDMAN_BRUTE_6919,
            NpcIdentifiers.LIZARDMAN_BRUTE_8564,
            NpcIdentifiers.LIZARDMAN_BRUTE_10947,
            NpcIdentifiers.DRAKE,
            NpcIdentifiers.DRAKE_8612,
            NpcIdentifiers.DRAKE_8613,
            NpcIdentifiers.DUSK,
            NpcIdentifiers.DUSK_7851,
            NpcIdentifiers.DUSK_7854,
            NpcIdentifiers.DUSK_7855,
            NpcIdentifiers.DUSK_7882,
            NpcIdentifiers.DUSK_7883,
            NpcIdentifiers.DUSK_7887,
            NpcIdentifiers.DUSK_7888,
            NpcIdentifiers.DUSK_7886,
            NpcIdentifiers.DUSK_7889,
            NpcIdentifiers.BASILISK_KNIGHT,
            NpcIdentifiers.RUNE_DRAGON,
            NpcIdentifiers.ADAMANT_DRAGON,
            NpcIdentifiers.CERBERUS,
            NpcIdentifiers.CERBERUS_5863,
            NpcIdentifiers.CERBERUS_5866,
            NpcIdentifiers.BRUTAL_BLACK_DRAGON,
            NpcIdentifiers.BRUTAL_GREEN_DRAGON,
            NpcIdentifiers.BRUTAL_RED_DRAGON,
            NpcIdentifiers.BRUTAL_BLACK_DRAGON_8092,
            NpcIdentifiers.BRUTAL_BLACK_DRAGON_8093,
            NpcIdentifiers.BRUTAL_GREEN_DRAGON,
            NpcIdentifiers.BRUTAL_GREEN_DRAGON_8081,
            NpcIdentifiers.BRUTAL_RED_DRAGON_8087,
            NpcIdentifiers.COMBAT_DUMMY
        };

    public void venom(Entity source) {
        if (source == null || source == this)
            return;

        if (source instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (ArrayUtils.contains(NPCS_IMMUNE_TO_VENOM, npc.id())) {
                    return;
                }
            }
        }

        if (Venom.venomed(source))
            return;

        if (isPlayer()) {
            Player p = (Player) this;

            if (Equipment.venomHelm(p)) { // Serp helm stops venom.
                return;
            }
            boolean admin_bypass = false;
            if (source.isPlayer()) {
                assert source instanceof Player;
                boolean fromAdmin = ((Player) source).getPlayerRights().isAdministrator(p);
                if (fromAdmin && GameServer.properties().venomFromAdminsOn) {
                    admin_bypass = true;
                }
            }
            // Source was a normal player
            if (!GameServer.properties().venomVsPlayersOn && !admin_bypass)
                return;
        } else if (isNpc()) {
            NPC me = (NPC) this;
            if (me.isVenomImmune()) {
                return;
            }
        }

        int tick = getAttribOr(AttributeKey.VENOM_TICKS, 0);
        if (tick == 0) {
            if (isPlayer()) {
                Player me = (Player) this;
                me.setInfection(InfectionType.VENOM_INFECTION); // Now venomed
                me.message("<col=145A32>You've been infected with venom!");
            }
            putAttrib(AttributeKey.VENOM_TICKS, 8); // default start -- venom newly applied. 8 cycles
            putAttrib(VENOMED_BY, source);
            Venom.setTimer(this);
        }
    }

    private final List<Player> localPlayers = new LinkedList<>();
    private final List<NPC> localNpcs = new LinkedList<>();

    public List<Player> getLocalPlayers() {
        return localPlayers;
    }

    public List<NPC> getLocalNpcs() {
        return localNpcs;
    }

    /**
     * shortcut for {@link Chain#bound(Object)}.{@link Chain#runFn(int, Runnable)}
     */
    public Chain<Entity> runFn(int startAfterTicks, Runnable r) {
        return Chain.bound(this).runFn(startAfterTicks, r);
    }

    /**
     * shortcut for {@link Chain#bound(Object)}.{@link Chain#runFn(int, Runnable)}
     * <br>
     * not bound to anything
     */
    public Chain runUninterruptable(int startAfterTicks, Runnable r) {
        return Chain.runGlobal(startAfterTicks, r);
    }

    /**
     * shortcut to {@link Chain#waitForTile(Tile, Runnable)}
     */
    public Chain<Entity> waitForTile(Tile tile, Runnable work) {
        return Chain.bound(this).waitForTile(tile, work);
    }

    public Chain<Entity> waitForArea(Area area, Runnable work) {
        return Chain.bound(this).waitForArea(area, work);
    }

    public Chain<Entity> walkAndWait(Tile tile, Runnable work) {
        //smartPathTo(tile, 1);
        smartPathTo(tile);
        return Chain.bound(this).waitForTile(tile, work);
    }

    /**
     * shortcut to {@link Chain#waitUntil(int, BooleanSupplier, Runnable)}
     */
    public Chain<Entity> waitUntil(int tickBetweenLoop, BooleanSupplier condition, Runnable work) {
        return Chain.bound(this).waitUntil(tickBetweenLoop, condition, work);
    }

    public Chain<Entity> waitUntil(BooleanSupplier condition, Runnable work) {
        return Chain.bound(this).waitUntil(1, condition, work);
    }

    public void onHit(Hit hit) {

    }

    public void takeHit() {

    }

    public Tinting tint(Tinting tint) {
        getAsPlayer().getUpdateFlag().flag(Flag.LUMINANCE);
        return tinting;
    }

    /**
     * When handling objects (doing custom walkto logic) if {@code
     * player.smartPathTo(startPos, obj.getSize());} doesn't work or walk exactly where you expect it too, its probably beacuse its a 1999 pathfinder.
     * <br> use {@code player.doPath(new DefaultPathFinder(), tile)} instead
     *
     * @return
     */
    public void smartPathTo(Tile targetPos) {
        getRouteFinder().routeAbsolute(targetPos.x, targetPos.y);
    }

    public final List<Task> activeTasks = new LinkedList<>();

    private RouteFinder routeFinder;

    public RouteFinder getRouteFinder() {
        if (routeFinder == null)
            routeFinder = new RouteFinder(this);
        return routeFinder;
    }

    public void step(int diffX, int diffY, MovementQueue.StepType stepType) {
        stepAbs(getAbsX() + diffX, getAbsY() + diffY, stepType);
    }

    public void stepAbs(int absX, int absY, MovementQueue.StepType stepType) {
        if (absX < 64 || absY < 64) {
            log.warn("attempted to step to coords {} {} -- maybe you meant to tile.translate({},{}) instead?", absX, absY, absX, absY);
        }
        /* forces a step without route finding */
        MovementQueue movement = getMovement();
        movement.readOffset = 0;
        movement.getStepsX()[0] = absX;
        movement.getStepsY()[0] = absY;
        movement.writeOffset = 1;
        movement.stepType = stepType;
    }

    public void stepAbs(Tile tile, MovementQueue.StepType stepType) {
        var absX = tile.getX();
        var absY = tile.getY();
        if (absX < 64 || absY < 64) {
            log.warn("attempted to step to coords {} {} -- maybe you meant to tile.translate({},{}) instead?", absX, absY, absX, absY);
        }
        /* forces a step without route finding */
        MovementQueue movement = getMovement();
        movement.readOffset = 0;
        movement.getStepsX()[0] = absX;
        movement.getStepsY()[0] = absY;
        movement.writeOffset = 1;
        movement.stepType = stepType;
    }

    public void resetSteps() {
        MovementQueue movement = getMovement();
        movement.readOffset = 0;
        movement.writeOffset = 0;
        movement.stepType = MovementQueue.StepType.REGULAR;
    }

    public boolean addStep(int absX, int absY) {
        MovementQueue movement = getMovement();
        if (movement.writeOffset < 50) {
            movement.getStepsX()[movement.writeOffset] = absX;
            movement.getStepsY()[movement.writeOffset] = absY;
            movement.writeOffset++;
            return true;
        }
        return false;
    }

    public boolean isMovementBlocked(boolean message, boolean ignoreFreeze) {
        return !getMovementQueue().canMove(message);
    }

    public int getAbsX() {
        return tile.getX();
    }

    public int getAbsY() {
        return tile.getY();
    }

    public static void accumulateRuntimeTo(Runnable r, Consumer<Duration> to) {
        if (!TimesCycle.BENCHMARKING_ENABLED) { // skip timer
            r.run();
            return;
        }
        long startTime = System.nanoTime();
        r.run();
        long endTime = System.nanoTime();
        Duration duration = Duration.ofNanos(endTime - startTime);
        to.accept(duration);
    }

    public static void generalTimed(Runnable r, Consumer<Duration> to) {
        com.google.common.base.Stopwatch stopwatch = Stopwatch.createStarted();
        r.run();
        stopwatch.stop();
        to.accept(stopwatch.elapsed());
    }

    public static void time(Consumer<Duration> consumer, Runnable task) {
        if (!TimesCycle.BENCHMARKING_ENABLED) {
            try {
                task.run();
            } catch (Exception e) {
                log.error("kys", e);
            }
            return;
        }

        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        Duration elapsedDuration = Duration.ofNanos(endTime - startTime);
        consumer.accept(elapsedDuration);
    }


    private int graphicSwap;

    public int getGraphicSwap() {
        return graphicSwap;
    }

    public void setGraphicSwap(int graphicSwap) {
        this.graphicSwap = graphicSwap;
    }

    private ForceMovement forceMovement;

    public ForceMovement getForceMovement() {
        return forceMovement;
    }

    public Entity setForceMovement(ForceMovement forceMovement) {
        getUpdateFlag().flag(Flag.FORCED_MOVEMENT);
        this.forceMovement = forceMovement;
        Chain.noCtx().delay(1 + (forceMovement.getSpeed() / 30), () -> {
            if (forceMovement.getEnd() != null) {
                teleport(tile().transform(forceMovement.getEnd()));
            }
            getMovementQueue().reset();
        });
        return this;
    }

    @Getter
    @Setter
    public boolean teleportJump;

    public void setTeleportJump(boolean teleportJump) {
        this.teleportJump = teleportJump;
    }

    public void queueTeleportJump(Tile newTile) {
        this.setTeleportJump(true);
        setTile(newTile);
        Tile.occupy(this);
        this.faceTile = newTile;
        this.tile = newTile;
    }

    public ActionManager action = new ActionManager();

    public void heal(int amount) {
        heal(amount, 0);
    }

    public void heal(int amount, int exceed) {
        hp(hp() + amount, exceed);
    }

    public void teleblock(int time) {
        this.teleblock(time, false);
    }

    /**
     * The effects of the teleblock are lifted when the affected player logs out, leaves the Wilderness by any means (jumping over the wilderness ditch, dying, etc.)
     * or when the teleblock timer expires. In addition, it is lifted if players kill the opponent that cast the spell on them, though this does not give a
     * temporary immunity to another Tele Block.
     *
     * @param time The tele block time in ticks
     */
    public void teleblock(int time, boolean triggerOnLogin) {
        if (triggerOnLogin) {
            timers.extendOrRegister(TimerKey.SPECIAL_TELEBLOCK, time);

            String end = "approximately 2 minutes";
            if (time > 400) {
                end = "5 minutes";
            }

            if (isPlayer()) {
                Player player = (Player) this;
                player.getPacketSender().sendEffectTimer((int) (time * 0.6), EffectTimer.TELEBLOCK);
                player.message("<col=804080>A teleblock spell has been cast on you. It will expire in " + end + ".");
            }
            return;
        }

        if (timers.has(TimerKey.SPECIAL_TELEBLOCK)) {
            return;
        }

        if (timers.has(TimerKey.TELEBLOCK)) {
            return;
        }

        if (timers.has(TimerKey.TELEBLOCK_IMMUNITY)) {
            return;
        }

        if (!timers.has(TimerKey.TELEBLOCK)) {

            if (tile.region() == 6992 || tile.region() == 6993) {
                time = 100;
            }

            timers.extendOrRegister(TimerKey.TELEBLOCK, time);
            timers.extendOrRegister(TimerKey.TELEBLOCK_IMMUNITY, time + 55);
            String end = "approximately 2 minutes";
            if (time > 400) {
                end = "5 minutes";
            }

            if (isPlayer()) {
                Player player = (Player) this;
                player.graphic(345);
                player.getPacketSender().sendEffectTimer((int) (time * 0.6), EffectTimer.TELEBLOCK);
                player.message("<col=804080>A teleblock spell has been cast on you. It will expire in " + end + ".");
            }
        }
    }

    public void stun(int time) {
        stun(time, true);
    }

    public void stun(int time, boolean message) {
        stun(time, message, true, false);
    }

    public void stun(int time, boolean message, boolean gfx, boolean npcStun) {
        if (timers.has(TimerKey.STUN_IMMUNITY)) {
            return;
        }

        //Stun the player.
        timers.extendOrRegister(TimerKey.STUNNED, time);

        //In addition to this, players are given a (3.0 seconds) period of immunity after a stun wears off in which they cannot be stunned again.
        if (!npcStun) {
            timers.extendOrRegister(TimerKey.STUN_IMMUNITY, time + 5);
        }

        if (message) {
            message("You have been stunned!");
        }

        if (gfx) {
            graphic(245, GraphicHeight.HIGH, 0);
        }

        stopActions(false);

        //Despite this, it is popular in player killing as the brief stun causes all incoming damage to be ignored until it dissipates, after which all damage taken is applied at once.
        if (!npcStun) {
            lockDelayDamage();
        }

        if (isPlayer()) {
            clearAttrib(AttributeKey.TARGET); // Does actually stop us interacting
        }

        Chain.bound(this).runFn(time, this::unlock);
    }

    public boolean frozen() {
        return timers.has(TimerKey.FROZEN);
    }

    public void removeFreeze() {
        timers.cancel(TimerKey.FROZEN);
    }

    public boolean stunned() {
        return timers.has(TimerKey.STUNNED);
    }

    int[] npcs_immune_to_freeze = new int[]
        {

        };

    public void freeze(int time, @NonNull Entity attacker, boolean ignoreImmunity) {
        if (attacker instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof Player enemy) {
                if (!ignoreImmunity) {
                    enemy.getTimers().extendOrRegister(TimerKey.FREEZE_IMMUNITY, time + 5);
                    if (enemy.getTimers().has(TimerKey.FREEZE_IMMUNITY) || enemy.getTimers().has(TimerKey.FROZEN)) {
                        return;
                    }
                }
                player.stopActions(true);
                enemy.putAttrib(AttributeKey.FROZEN_BY, player);
                enemy.getTimers().extendOrRegister(TimerKey.FROZEN, time);
                enemy.getPacketSender().sendEffectTimer((int) Math.round(time * 0.6), EffectTimer.FREEZE).sendMessage(Color.RED.wrap("You have been frozen!"));
            } else if (target instanceof NPC npc) {
                if (ArrayUtils.contains(npcs_immune_to_freeze, npc.id())) {
                    return;
                }
                if (npc.getTimers().has(TimerKey.FREEZE_IMMUNITY) || npc.getTimers().has(TimerKey.FROZEN)) {
                    return;
                }
                if (!npc.locked()) {
                    npc.getMovementQueue().clear();
                }
                npc.putAttrib(AttributeKey.FROZEN_BY, player);
                npc.getTimers().extendOrRegister(TimerKey.FROZEN, time);
                npc.getTimers().extendOrRegister(TimerKey.FREEZE_IMMUNITY, time + 5);
            }
        }

    }

    public void stopActions(boolean cancelMoving) {
        if (locked()) {
            return;
        }

        this.setEntityInteraction(null);
        if (isPlayer())
            getAsPlayer().getMovementQueue().resetFollowing();
        // Graphics and animations are not reset when you walk.
        if (cancelMoving)
            getMovementQueue().clear();
        action.clearNonWalkableActions();
        interruptChains();
        TargetRoute.reset(this);
    }

    public void interruptChains() {
        chains.forEach(c -> { // interrupt chains
            c.nextNode = null;
            c.interrupted = true;
            if (c.task != null)
                c.task.stop();
        });
        chains.removeIf(c -> c.interrupted || (c.task != null && c.task.isStopped()));
    }

    public final ArrayList<Chain<?>> chains = new ArrayList<>();

    public String getMobName() {
        if (isNpc()) {
            return (getAsNpc().def().name != null ? getAsNpc().def().name : "N/A");
        } else {
            return getAsPlayer().getUsername();
        }
    }

    private boolean[] prayerActive = new boolean[30];

    /**
     * Teleports the mob to a target location
     */
    public void teleport(Tile teleportTarget) {

        if (isPlayer() && !getAsPlayer().getInterfaceManager().isClear()) {
            if (getAsPlayer().getInterfaceManager().getWalkable() != 196) {
                getAsPlayer().getInterfaceManager().close(true);
            }
            getAsPlayer().getInterfaceManager().close(false);
        }

        if (isPlayer()) {
            if (player().getNightmareInstance() != null) {
                player().getNightmareInstance().getPlayers().remove(player());
            }

            if (player().hasAttrib(NO_MOVEMENT_NIGHTMARE)) {
                player().clearAttrib(NO_MOVEMENT_NIGHTMARE);
            }

            if (player().getHits() != null) {
                player().getHits().invalidate();
            }

        }

        if (isPlayer() && player() != null) {
            var party = player().getTheatreInstance();

            if (party != null) {
                var playerRegion = player().tile().region();

            }
        }


        setTile(teleportTarget);
        Tile.occupy(this);
        setPreviousTile(teleportTarget);
        setNeedsPlacement(true);
        setResetMovementQueue(true);
        setEntityInteraction(null);
        //getAsPlayer().isNullifyDamageLock(); //we nullfiy the damage taken when teleporting

        if (isPlayer()) {
            getAsPlayer().getAsPlayer().getMovementQueue().handleRegionChange();
        }

        getMovementQueue().clear();

        getMovementQueue().lastFollowX = getMovement().followX = -1;
        getMovementQueue().lastFollowY = getMovement().followY = -1;

        if (getInstancedArea() != null) { // make sure this is after tile set
            if (!getInstancedArea().inInstanceArea(this)) {
                // mob has left the instance
                if (isNpc()) {
                    // players can TP out .. but npcs? if they're tping out thats probably a bug!
                    LogManager.getLogger("Entity").error("Npc is teleporting out of instance. removing " + getMobName() + " from " + getInstancedArea(), new RuntimeException("tp out of instance"));
                } else {
                    LogManager.getLogger("Entity").info("Player left instance. If not ok, double check area coords removing " + getMobName() + " from " + getInstancedArea());
                }
                if (isNpc()) {
                    getInstancedArea().removeNpc(getAsNpc());
                } else if (isPlayer()) {
                    getInstancedArea().removePlayer(getAsPlayer());
                }
            }
        }
    }

    public MovementQueue getMovement() {
        return getMovementQueue();
    }

    public void teleport(int x, int y) {
        teleport(new Tile(x, y));
    }

    public void teleport(int x, int y, int z) {
        teleport(new Tile(x, y, z));
    }

    /**
     * Resets all flags related to updating.
     */
    public void resetUpdating() {
        getUpdateFlag().reset();
        this.setTeleportJump(false);
        walkingDirection = Direction.NONE;
        runningDirection = Direction.NONE;
        needsPlacement = false;
        resetMovementQueue = false;
        forcedChat = null;
        interactingEntity = null;
        animation = null;
        graphic = null;
        nextHits.clear();
    }

    public Entity forceChat(String message) {
        getUpdateFlag().flag(Flag.FORCED_CHAT);
        setForcedChat(message);
        return this;
    }

    public Entity setEntityInteraction(Entity entity) {
        getUpdateFlag().flag(Flag.ENTITY_INTERACTION);
        this.interactingEntity = entity;
        return this;
    }

    public void animate(int animation) {
        animate(new Animation(animation));
    }

    public void animate(int animation, int delay) {
        animate(new Animation(animation, delay));
    }

    public void resetAnimation() {
        animate(-1, 0);
    }

    public void graphic(int id) {
        performGraphic(new Graphic(id, GraphicHeight.LOW, 0));
    }

    public void graphic(int id, GraphicHeight height, int delay) {
        performGraphic(new Graphic(id, height, delay));
    }

    public void animate(Animation animation) {
        if (this.animation != null && animation != null) {
            if (this.animation.getPriority().ordinal() > animation.getPriority().ordinal()) {
                return;
            }
        }

        this.animation = animation;
        this.recentAnim = animation;
        getUpdateFlag().flag(Flag.ANIMATION);
    }

    public void setTinting(Tinting tinting) {
        this.tinting = tinting;
        getUpdateFlag().flag(Flag.LUMINANCE);
    }

    public void performGraphic(Graphic graphic) {
        this.graphic = graphic;
        getUpdateFlag().flag(Flag.GRAPHIC);
    }

    /**
     * The {@link TimerRepository} which manages all of the
     * timers/delays for this {@link Entity}.
     */
    private final TimerRepository timers = new TimerRepository();

    public TimerRepository getTimers() {
        return timers;
    }

    /*
     * Fields
     */
    private final Combat combat = new Combat(this);
    private String forcedChat;
    private boolean fixingDiagonal = false;
    private boolean repositioning = false;
    private Direction walkingDirection = Direction.NONE, runningDirection = Direction.NONE;
    private final UpdateFlag updateFlag = new UpdateFlag();
    private Animation animation;
    public Animation recentAnim;
    private Graphic graphic;
    private Tinting tinting;
    private Entity interactingEntity;
    private boolean resetMovementQueue;
    private boolean needsPlacement;
    private int specialAttackPercentage = 100;
    private boolean specialActivated;
    private boolean recoveringSpecialAttack;
    protected Controller controller;

    /**
     * Listeners
     */

    public AttackNpcListener attackNpcListener;

    public int projectileSpeed(Entity target) {
        int clientSpeed;
        if (this.tile().isWithinDistance(this, target, 1)) {
            clientSpeed = 56;
        } else if (this.tile().isWithinDistance(this, target, 5)) {
            clientSpeed = 61;
        } else if (this.tile().isWithinDistance(this, target, 8)) {
            clientSpeed = 71;
        } else {
            clientSpeed = 81;
        }
        return clientSpeed;
    }

    public void startEvent(int delayTicks, Runnable run) {
        Chain.bound(null).runFn(delayTicks, run);
    }

    public void startEvent(int delayTicks, Runnable run, int tickDelay, Runnable afterDelay) {
        Chain.bound(null).runFn(delayTicks, run).then(tickDelay, afterDelay);
    }


    public int getProjectileHitDelay(Entity target) {
        int gfxDelay;
        if (this.tile().isWithinDistance(this, target, 1)) {
            gfxDelay = 80;
        } else if (this.tile().isWithinDistance(this, target, 5)) {
            gfxDelay = 100;
        } else if (this.tile().isWithinDistance(this, target, 8)) {
            gfxDelay = 120;
        } else {
            gfxDelay = 140;
        }

        return (gfxDelay / 20) - 2;
    }

    public int pidOrderIndex;

    public void faceEntity(Entity target) {
        setEntityInteraction(target);
    }

    public void face(Entity mob) {
        setEntityInteraction(mob);
    }

    public void resetFreeze() {
        getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
    }

    @Getter
    private InstancedArea instancedArea;

    public void setInstancedArea(InstancedArea instancedArea) {
        var prev = this.instancedArea;
        this.instancedArea = instancedArea;
        if (prev == instancedArea)
            return;
        if (prev != null && instancedArea == null) { // setting null probably removing
            if (isPlayer())
                prev.removePlayer(getAsPlayer());
            else
                prev.removeNpc(npc());
        } else if (instancedArea != null) { // add
            if (isPlayer())
                instancedArea.addPlayer(getAsPlayer());
            else
                instancedArea.addNpc(npc());
        }
    }

    public boolean isNpc(int i) {
        return isNpc() && getAsNpc().id() == i;
    }

    public Region[] surrounding;

    /**
     * @author Shadowrs
     */
    public Region[] getSurroundingRegions() {
        final var region = tile.region();
        if (surrounding != null && surrounding[0] != null && surrounding[0].getRegionId() == region)
            return surrounding;
        surrounding = new Region[9];
        surrounding[0] = RegionManager.getRegion(region); // cant tell if its chunk or abs X we'll see
        surrounding[1] = RegionManager.getRegion(region - 1);
        surrounding[2] = RegionManager.getRegion(region + 1);
        surrounding[3] = RegionManager.getRegion(region - 256);
        surrounding[4] = RegionManager.getRegion(region + 256);
        surrounding[5] = RegionManager.getRegion(region + 257);
        surrounding[6] = RegionManager.getRegion(region - 257);
        surrounding[7] = RegionManager.getRegion(region - 255);
        surrounding[8] = RegionManager.getRegion(region + 255);
        // System.out.println("regions "+ Arrays.toString(Arrays.stream(surrounding).map(r -> r.getRegionId()).toArray()));
        return surrounding;
    }

    public void debug(int i, String s) {
        if (isNpc() && isNpc(i)) {
            if (s.length() > 0) {
                forceChat(s);
                log.debug(s);
            }
        }
    }
}
