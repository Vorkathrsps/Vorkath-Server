package com.aelous.model.entity.npc;

import com.aelous.model.content.areas.wilderness.wildernesskeys.WildernessKeys;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.corruptedhunleff.CorruptedHunleff;
import com.aelous.model.entity.combat.method.impl.npcs.karuulm.Wyrm;
import com.aelous.utility.Debugs;
import com.google.common.base.Stopwatch;
import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.content.areas.wilderness.content.boss_event.BossEvent;
import com.aelous.model.content.skill.impl.hunter.trap.impl.Chinchompas;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.NodeType;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.demonicgorillas.DemonicGorilla;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah.Zulrah;
import com.aelous.model.entity.combat.method.impl.npcs.fightcaves.TzTokJad;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl.KreeArra;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.bandos.Graardor;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.saradomin.Zilyana;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.zamorak.Kril;
import com.aelous.model.entity.combat.method.impl.npcs.karuulm.Drake;
import com.aelous.model.entity.combat.method.impl.npcs.karuulm.Hydra;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.npc.bots.NPCBotHandler;
import com.aelous.model.entity.npc.impl.MaxHitDummyNpc;
import com.aelous.model.entity.npc.impl.UndeadMaxHitDummy;
import com.aelous.model.entity.npc.pets.PetDefinitions;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.model.map.route.routes.TargetRoute;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.NpcPerformance;
import com.aelous.utility.SecondsTimer;
import com.aelous.utility.Utils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKOTIZO;
import static com.aelous.utility.CustomNpcIdentifiers.BRUTAL_LAVA_DRAGON;
import static com.aelous.utility.CustomNpcIdentifiers.CORRUPTED_NECHRYARCH;
import static com.aelous.utility.ItemIdentifiers.BRACELET_OF_ETHEREUM;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.GIANT_MOLE;
import static org.apache.logging.log4j.util.Unbox.box;

/**
 * Represents a non-playable mob, which players can interact with.
 *
 * @author Professor Oak
 */
public class NPC extends Entity {

    private static final Logger logger = LogManager.getLogger(NPC.class);
    public boolean ignoreOccupiedTiles;

    private boolean lockMovementCompletely;

    public boolean completelyLockedFromMoving() {
        return lockMovementCompletely;
    }

    public void completelyLockedFromMoving(boolean lockMovementCompletely) {
        this.lockMovementCompletely = lockMovementCompletely;
    }

    public WildernessKeys wildernessKeys = new WildernessKeys(null, this);
    public WildernessKeys getWildernessKeys() {
        return wildernessKeys;
    }

    private int capDamage = -1;

    public int capDamage() {
        return capDamage;
    }

    public void capDamage(int capDamage) {
        this.capDamage = capDamage;
    }

    private boolean cantInteract;

    public boolean cantInteract() {
        return cantInteract;
    }

    public void cantInteract(boolean cantInteract) {
        this.cantInteract = cantInteract;
    }

    public boolean isRandomWalkAllowed() {
        return walkRadius > 0 &&
            spawnArea != null
            && !hidden()
            && getMovement().isAtDestination()
                && !locked()
                && !isMovementBlocked(false, false);
    }

    public boolean isWorldBoss() {
        return (Arrays.stream(BossEvent.values()).anyMatch(boss -> id == boss.npc)) || id == BRUTAL_LAVA_DRAGON || id == SKOTIZO || id == CORRUPTED_NECHRYARCH;
    }

    public boolean isPet() {
        return (Arrays.stream(PetDefinitions.values()).anyMatch(pet -> id == pet.npc));
    }

    //Target switching may be computationally expensive since it's in sequence (core processing).
    public static boolean TARG_SWITCH_ON = true;

    public String spawnStack = "";

    private int id;
    private Tile spawnTile;
    private boolean ancientSpawn;
    private int walkRadius;
    private int spawnDirection;
    private int lastDirection;
    // If a player can see this npc. if not, what's the point in processing it?
    private boolean inViewport = true;
    private NpcDefinition def;
    private int hp;
    private NPCCombatInfo combatInfo;
    private boolean hidden;
    private boolean respawns = true;
    private boolean venomImmune;
    private boolean poisonImmune;
    @Getter
    private Area spawnArea;
    private int transmog = -1;

    // A list of npc-ids such as Bosses that are immune to venom.
    public static final int[] venom_immunes = new int[]{NpcIdentifiers.COMBAT_DUMMY, NpcIdentifiers.UNDEAD_COMBAT_DUMMY, 3127, 494, 2265, 2266, 2267, 7144, 7145, 7146, 7147, 7148, 7149, 6611, 6612, 2042, 2043, 2044, 9035, 9036, 9037};
    public static final int[] poison_immunes = new int[]{NpcIdentifiers.COMBAT_DUMMY, NpcIdentifiers.UNDEAD_COMBAT_DUMMY, 9035, 9036, 9037};

    public NPC spawn(boolean respawns) {
        World.getWorld().registerNpc(this);
        respawns(respawns);
        return this;
    }

    public NPC spawn(Tile location) {
        this.spawnTile = location;
        World.getWorld().registerNpc(this);
        return this;
    }

    public NPC spawn() {
        World.getWorld().registerNpc(this);
        return this;
    }

    public NPC(int id, Tile tile) {
        super(NodeType.NPC, tile);
        this.id = id;
        spawnTile = tile;
        def = World.getWorld().definitions().get(NpcDefinition.class, id);
        combatInfo = World.getWorld().combatInfo(id);
        hp = combatInfo == null ? 50 : combatInfo.stats.hitpoints;
        spawnArea = new Area(spawnTile, walkRadius);
        getCombat().setAutoRetaliate(true);
        ignoreOccupiedTiles = def.ignoreOccupiedTiles;

        for (int types : venom_immunes) {
            if (id == types) {
                setVenomImmune(true);
            }
        }
        for (int types : poison_immunes) {
            if (id == types) {
                setPoisonImmune(true);
            }
        }

        try {
            NPCBotHandler.assignBotHandler(this);
        } catch (Exception e) {
            logger.error("sadge", e);
            logger.error("NPC {} might not have an NPC definition entry.", box(id));
        }

        if (getCombatInfo() != null && getCombatInfo().scripts != null && getCombatInfo().scripts.combat_ != null) {
            if (id == NpcIdentifiers.ZULRAH || id == NpcIdentifiers.ZULRAH_2043 || id == NpcIdentifiers.ZULRAH_2044) {
                setCombatMethod(Zulrah.EmptyCombatMethod.make());
            }
            setCombatMethod(getCombatInfo().scripts.newCombatInstance());
        }

        if (getMobName().toLowerCase().contains("clerk") || getMobName().toLowerCase().contains("banker") || getMobName().toLowerCase().contains("aubury") || getMobName().toLowerCase().contains("wise old man") || getMobName().toLowerCase().contains("mac") || getMobName().toLowerCase().contains("shop keeper")) {
            skipReachCheck = t -> {
                Direction current = Direction.fromDeltas(getX() - t.getX(), getY() - t.getY());
                return current.isDiagonal || t.distance(tile()) == 1;
            };
        }
        if (tile().equals(3109, 3517))
            walkTo = tile.transform(1, 0);
        if (this.getMobName().toLowerCase().contains("rock crab"))
            ignoreOccupiedTiles = true;
    }

    public NPC(int id, Tile tile, boolean spawn) {
        super(NodeType.NPC, tile);
        this.id = id;
        spawnTile = tile;
        def = World.getWorld().definitions().get(NpcDefinition.class, id);
        combatInfo = World.getWorld().combatInfo(id);
        hp = combatInfo == null ? 50 : combatInfo.stats.hitpoints;
        spawnArea = new Area(spawnTile, walkRadius);
        getCombat().setAutoRetaliate(true);

        for (int types : venom_immunes) {
            if (id == types) {
                setVenomImmune(true);
            }
        }
        for (int types : poison_immunes) {
            if (id == types) {
                setPoisonImmune(true);
            }
        }

        try {
            NPCBotHandler.assignBotHandler(this);
        } catch (Exception e) {
            logger.error("sadge", e);
            logger.error("NPC {} might not have an NPC definition entry.", box(id));
        }

        if (getCombatInfo() != null && getCombatInfo().scripts != null && getCombatInfo().scripts.combat_ != null) {
            if (id == NpcIdentifiers.ZULRAH || id == NpcIdentifiers.ZULRAH_2043 || id == NpcIdentifiers.ZULRAH_2044) {
                setCombatMethod(Zulrah.EmptyCombatMethod.make());
            }
            setCombatMethod(getCombatInfo().scripts.newCombatInstance());
        }

        if (getMobName().toLowerCase().contains("clerk") || getMobName().toLowerCase().contains("banker")) {
            skipReachCheck = t -> {
                Direction current = Direction.fromDeltas(getX() - t.getX(), getY() - t.getY());
                return current.isDiagonal || t.distance(tile()) == 1;
            };
        }
        if (tile().equals(3109, 3517))
            walkTo = tile.transform(1, 0);

        if (spawn) {
            World.getWorld().registerNpc(this);
        }
    }

    /**
     * Returns a new instance of the npc with its respective extension.
     *
     * @param id
     * @param tile
     * @return the NPC
     */
    public static NPC of(int id, Tile tile) {
        return switch (id) {
            case NpcIdentifiers.COMBAT_DUMMY -> new MaxHitDummyNpc(id, tile);
            case NpcIdentifiers.UNDEAD_COMBAT_DUMMY -> new UndeadMaxHitDummy(id, tile);
            case Wyrm.IDLE, Wyrm.ACTIVE -> new Wyrm(id, tile);
            case 8609 -> new Hydra(id, tile);
            case 8612, 8613 -> new Drake(id, tile);
            case NpcIdentifiers.TZTOKJAD -> new TzTokJad(id, tile);
            case NpcIdentifiers.DEMONIC_GORILLA,
                NpcIdentifiers.DEMONIC_GORILLA_7145,
                NpcIdentifiers.DEMONIC_GORILLA_7146 -> new DemonicGorilla(id, tile);
            case NpcIdentifiers.CORRUPTED_HUNLLEF,
                NpcIdentifiers.CORRUPTED_HUNLLEF_9036,
                NpcIdentifiers.CORRUPTED_HUNLLEF_9037 -> new CorruptedHunleff(id, tile);
            default -> new NPC(id, tile);
        };
    }

    public int transmog() {
        this.getUpdateFlag().flag(Flag.TRANSFORM);
        return transmog;
    }

    public void transmog(int id) {
        this.transmog = id;
        this.id = id;
        this.def(World.getWorld().definitions().get(NpcDefinition.class, id));
        NpcDefinition def = def();
        setSize(def.size);
        this.getUpdateFlag().flag(Flag.TRANSFORM);
    }

    public void inViewport(boolean b) {
        inViewport = b;
    }

    public boolean inViewport() {
        return inViewport;
    }

    public NPC walkRadius(int r) {
        if (walkRadius != r) {
            spawnArea = new Area(spawnTile, r);
        }
        walkRadius = r;
        return this;
    }

    public int walkRadius() {
        return walkRadius;
    }

    public boolean ancientSpawn() {
        return ancientSpawn;
    }

    public void ancientSpawn(boolean ancient) {
        ancientSpawn = ancient;
    }

    public NPC spawnDirection(int d) {
        spawnDirection = d;
        return this;
    }

    public int spawnDirection() {
        return spawnDirection;
    }

    public NPC lastDirection(int d) {
        lastDirection = d;
        return this;
    }

    public int lastDirection() {
        return lastDirection;
    }

    public Tile spawnTile() {
        return spawnTile;
    }

    public int id() {
        if (transmog != -1) {
            return transmog();
        }
        return id;
    }

    public int getId() {
        if (transmog != -1) {
            return transmog();
        }
        return id;
    }

    public NpcDefinition def() {
        return def;
    }

    public void def(NpcDefinition d) {
        this.def = d;
    }

    public NPCCombatInfo getCombatInfo() {
        return combatInfo;
    }

    public void setCombatInfo(NPCCombatInfo info) {
        combatInfo = info;
    }

    public void hidden(boolean b) {
        hidden = b;
        Tile.occupy(this);
    }

    public boolean hidden() {
        return hidden;
    }

    public NPC respawns(boolean b) {
        respawns = b;
        return this;
    }

    public boolean respawns() {
        return respawns;
    }

    public boolean isBot() {
        return id >= 13000 && id <= 13009;
    }

    public boolean isVenomImmune() {
        return venomImmune;
    }

    public void setVenomImmune(boolean venomImmune) {
        this.venomImmune = venomImmune;
    }

    public boolean isPoisonImmune() {
        return poisonImmune;
    }

    public void setPoisonImmune(boolean poisonImmune) {
        this.poisonImmune = poisonImmune;
    }


    /**
     * The npc's combat method, used
     * for attacking.
     */
    private CombatMethod combatMethod;

    /**
     * The {@link SecondsTimer} where this npc is
     * immune to attacks.
     */
    private final SecondsTimer immunity = new SecondsTimer();

    public boolean canSeeTarget(Entity attacker, Entity target) {
        return attacker.tile().isWithinDistance(target.tile());
    }

    public boolean isCombatDummy() {
        return this.id == NpcIdentifiers.COMBAT_DUMMY || this.id == NpcIdentifiers.UNDEAD_COMBAT_DUMMY;
    }

    public boolean isPvPCombatDummy() {
        return this.id == NpcIdentifiers.UNDEAD_COMBAT_DUMMY;
    }

    public NpcPerformance performance = new NpcPerformance();

    /**
     * Processes this npc. Previously called onTick.
     */
    public final void sequence() {
       /* if (NpcPerformance.PERF_CHECK_MODE_ENABLED) {
            sequencePerformanceMode();
        } else {*/
            sequenceNormal();
       // }
        postSequence();
    }

    /**
     * override me
     */
    public void postSequence() {

    }

    public boolean useSmartPath;

    private void sequenceNormal() {
        action.sequence();
        TaskManager.sequenceForMob(this);
        getTimers().cycle(this);
        getCombat().followTarget();
        if (useSmartPath)
            TargetRoute.beforeMovement(this);
        getMovementQueue().process();
        if (useSmartPath)
            TargetRoute.afterMovement(this);
        //Process the bot handler!
        if (getBotHandler() != null) {
            getBotHandler().process();
        }
        getCombat().process();
    }

    /*private void sequencePerformanceMode() {
        performance.reset();

        // accumulateRuntimeTo(() -> {
        performance.actionSequence = Stopwatch.createStarted();
        action.sequence();
        performance.actionSequence.stop();
        if (performance.action == null && action.getCurrentAction() != null) {
            performance.action = action.getCurrentAction().keyOrOrigin();
        }

        TaskManager.sequenceForMob(this); // performance part F = tasks

        accumulateRuntimeTo(() -> {
            // Timers
            getTimers().cycle(this);
        }, d -> NpcPerformance.G += d.toNanos());

        //}, d -> NpcPerformance.cumeNpcE += d.toNanos());


        //Only process the npc if they have properly been added
        //to the game with a definition.
        if (def != null) {
            try {
                accumulateRuntimeTo(() -> {
                    //Handles random walk and retreating from fights
                    getCombat().followTarget();
                }, to -> NpcPerformance.npcA += to.toNanos());

                accumulateRuntimeTo(() -> {
                    if (useSmartPath)
                        TargetRoute.beforeMovement(this);
                    getMovementQueue().process();
                    if (useSmartPath)
                        TargetRoute.afterMovement(this);
                }, d -> NpcPerformance.cumeNpcB += d.toNanos());

                //Handle combat
                accumulateRuntimeTo(() -> {
                    //Process the bot handler!
                    if (getBotHandler() != null) {
                        getBotHandler().process();
                    }

                    getCombat().process();
                    // Process areas..
                }, d -> NpcPerformance.cumeNpcD += d.toNanos());

            } catch (Exception e) {
                logger.error("There was an error sequencing an NPC. Check the npc spawns and other json files.", e);
                GameEngine.getInstance().addSyncTask(() -> {
                    remove();
                });
            }
        }
        performance.assess(this);
    }*/

    public void findAgroTargetTimed() {
        accumulateRuntimeTo(() -> {
            findAgroTarget();
        }, d -> NpcPerformance.H += d.toNanos());
    }

    public void findAgroTarget() {

        Stopwatch stopwatch1 = Stopwatch.createStarted();
        boolean wilderness = (WildernessArea.wildernessLevel(tile()) >= 1) && !WildernessArea.inside_rouges_castle(tile()) && !Chinchompas.hunterNpc(id);
        if (dead() || !inViewport || locked() || combatInfo == null || !(combatInfo.aggressive || (wilderness && getBotHandler() == null)))
            return;

        //NPCs should only aggro if you can attack them.
        final int ceil = def.combatlevel * 2;
        final boolean override = combatInfo != null && combatInfo.scripts != null && combatInfo.scripts.agro_ != null;
        var bounds = boundaryBounds(combatInfo != null ? combatInfo.aggroradius : 1);

        //Highly optimized code
        Stream<Player> playerStream = World.getWorld().getPlayers()
            .stream()
            .filter(Objects::nonNull)
            .filter(p -> !p.looks().hidden())
            .filter(p -> {
                var v = bounds.inside(p.tile());
                //bounds.forEachPos(t -> t.showTempItem(995));
                return v;
            });
        // apply overrides
        if (override) {
            playerStream = playerStream.filter(p -> combatInfo.scripts.agro_.shouldAgro(this, p));
        } else {
            if (!wilderness) {
                // only check combatLevel if no custom script is present which will override it
                playerStream = playerStream.filter(p -> p.getSkills().combatLevel() <= ceil)
                    .filter(p -> CombatFactory.bothInFixedRoom(this, p));
            } else {
                playerStream = playerStream.filter(p -> p.getEquipment().getId(EquipSlot.HANDS) != BRACELET_OF_ETHEREUM && (def != null && !def.name.contains("revenant")));
            }
        }
        // execute stream filters and use.
        final List<Player> collect = playerStream.toList();
        for (Player p : collect) {
            long lastTime = System.currentTimeMillis() - (long) p.getAttribOr(AttributeKey.LAST_WAS_ATTACKED_TIME, 0L);
            Entity lastAttacker = p.getAttrib(AttributeKey.LAST_DAMAGER);
            if (lastTime > 5000L || lastAttacker == this ||
                (lastAttacker != null && (lastAttacker.dead() || lastAttacker.finished()))
                || p.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1) {
                if (CombatFactory.canAttack(this, combatMethod, p)) {
                    getCombat().attack(p);
                    //String ss = this.def.getName()+" v "+p.getUsername()+" : "+ CombatFactory.canAttack(this, method, p);
                    //System.out.println(ss);
                    //this.forceChat(ss);
                    break;
                }

            }
        }
        stopwatch1.stop();
        performance.aggression = stopwatch1;
    }

    /**
     * Sets the interacting entity.
     *
     * @param mob The new entity to interact with.
     */
    public void faceEntity(Entity mob) {
        this.setEntityInteraction(mob);
        this.getUpdateFlag().flag(Flag.ENTITY_INTERACTION);
    }


    /**
     * The npc's head icon.
     */
    private int PKBotHeadIcon = -1;

    public int getPKBotHeadIcon() {
        return PKBotHeadIcon;
    }

    public void setPKBotHeadIcon(int PKBotHeadIcon) {
        this.PKBotHeadIcon = PKBotHeadIcon;
        //We used to flag APPEARANCE, now we flag TRANSFORM.
        getUpdateFlag().flag(Flag.TRANSFORM);
    }

    /**
     * The npc bot handler.
     */
    private NPCBotHandler botHandler;

    public NPCBotHandler getBotHandler() {
        return botHandler;
    }

    public void setBotHandler(NPCBotHandler botHandler) {
        this.botHandler = botHandler;
    }

    public CombatMethod getCombatMethod() {
        return combatMethod;
    }

    public void setCombatMethod(CombatMethod combatMethod) {
        this.combatMethod = combatMethod;
        if (combatMethod instanceof CommonCombatMethod ccm) {
            ccm.set(this, null);
            ccm.init(this);
        }
    }

    public SecondsTimer getImmunity() {
        return immunity;
    }

    public void graphic(int graphic) {
        this.performGraphic(new Graphic(graphic));
    }

    private boolean target_fleeing(Area room, Entity attacker) {

        Entity target = getCombat().getTarget();
        if (target != null && room != null) {
            Map<Entity, Long> last_attacked_map = getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<>());
            List<Entity> invalid = new ArrayList<>();

            // Identify when our current focused target attacked us.
            long[] last_time = new long[1];

            // Identify invalid entries and our current targets last attack time
            if (last_attacked_map.size() > 0) {
                last_attacked_map.forEach((p, t) -> {
                    if (target == p) // Our current target hasn't attacked for 10s. Fuck that guy, change!
                        last_time[0] = t;
                    if (!room.contains(p)) {
                        invalid.add(p);
                    }
                    //System.out.println(p.index()+" vs "+target.index());
                });
            }
            // Remove invalid entries
            invalid.forEach(last_attacked_map::remove);
            invalid.clear();

            // 0L = never attacked in the first place. otherwise 10s check
            if (last_time[0] == 0L || System.currentTimeMillis() - last_time[0] >= 8000) {
                if (last_attacked_map.size() > 0) {
                    // Retaliate to a random person who has recently attacked us in this room.
                    super.autoRetaliate(last_attacked_map.keySet().toArray(new Entity[0])[Utils.random(last_attacked_map.size() - 1)]);
                } else {
                    // Fall back to whoever actually hit us
                    super.autoRetaliate(attacker);
                }
                return true;
            }
        }
        return false;
    }

    public void cloneDamage(NPC npc) {
        this.getCombat().setDamageMap(npc.getCombat().getDamageMap());
    }


    static final int[] PERMANENT_MOVEMENT_BLOCKED = {
        NpcIdentifiers.VORKATH_8061, NpcIdentifiers.PORTAL_1747, NpcIdentifiers.PORTAL_1748, NpcIdentifiers.PORTAL_1749, NpcIdentifiers.PORTAL_1750, NpcIdentifiers.VOID_KNIGHT_2950, NpcIdentifiers.VOID_KNIGHT_2951, NpcIdentifiers.VOID_KNIGHT_2952
    };

    public boolean permaBlockedMovement() {
        return Arrays.stream(PERMANENT_MOVEMENT_BLOCKED).anyMatch(n -> this.id == n);
    }

    public NPC[] closeNpcs(int span) {
        return closeNpcs(254, span);
    }

    public NPC[] closeNpcs(int maxCapacity, int span) {
        NPC[] targs = new NPC[maxCapacity];

        int caret = 0;
        for (int idx = 0; idx < World.getWorld().getNpcs().size(); idx++) {
            NPC npc = World.getWorld().getNpcs().get(idx);
            if (npc == null || npc == this || tile().distance(npc.tile()) > 14 || npc.tile().level != tile().level || npc.finished()) {
                continue;
            }
            if (npc.tile().inSqRadius(tile(), span)) {
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

    @Override
    public String toString() {
        return "NPC{" + getMobName()+
            ", id=" + id +
            ", hp=" + hp +
            ", tile=" + tile +
            ", lock=" + lockState() +
            ", walkRadius=" + walkRadius +
            ", spawnDirection=" + spawnDirection +
            "lockMovementCompletely=" + lockMovementCompletely +
            ", capDamage=" + capDamage +
            ", cantInteract=" + cantInteract +
            ", spawnTile=" + spawnTile +
            ", ancientSpawn=" + ancientSpawn +
            ", lastDirection=" + lastDirection +
            ", inViewport=" + inViewport +
            ", def=" + (def==null?"?":"def") +
            ", combatInfo=" + (combatInfo == null? "?":combatInfo) +
            ", hidden=" + hidden +
            ", respawns=" + respawns +
            ", venomImmune=" + venomImmune +
            ", poisonImmune=" + poisonImmune +
            ", spawnArea=" + spawnArea +
            ", transmog=" + transmog +
            ", combatMethod=" + combatMethod +
            ", immunity=" + immunity +
            ", useSmartPath=" + useSmartPath +
            ", walkTo=" + walkTo +
            ", skipReachCheck=" + skipReachCheck +
            ", canAttack=" + canAttack +
            ", spawnStack='" + spawnStack + '\'' +
            ", idx: "+getIndex()+
            '}';
    }

    @Override
    public int yLength() {
        return def().size;
    }

    @Override
    public int xLength() {
        return def().size;
    }

    @Override
    public Tile getCentrePosition() {
        Tile base = this.tile();

        if (this.getSize() > 1) {
            base = this.tile().transform(this.getSize() / 2, this.getSize() / 2, 0);
        }
        return base;
    }

    @Override
    public int getProjectileLockonIndex() {
        return getIndex() + 1;
    }

    @Override
    public void onAdd() {
    }

    @Override
    public void onRemove() {
        TaskManager.cancelTasks(this);
    }

    @Override
    public Hit manipulateHit(Hit hit) {
        return hit;
    }

    @Override
    public void die() {
        try {
            NPCDeath.execute(this);
        } catch (Exception e1) {e1.printStackTrace(); }
    }

    @Override
    public int hp() {
        return hp;
    }

    @Override
    public void hp(int hp, int exceed) {
        this.hp = Math.min(maxHp() + exceed, hp);
    }

    @Override
    public int maxHp() {
        return combatInfo == null ? 50 : combatInfo.stats.hitpoints;
    }

    @Override
    public NPC setHitpoints(int hitpoints) {
        if (isCombatDummy()) {
            if (combatInfo.stats.hitpoints > hitpoints) {
                return this;
            }
        }
        this.hp = hitpoints;
        return this;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isNpc() {
        return true;
    }

    @Override
    public boolean dead() {
        return hp == 0;
    }

    private final NpcMovement movementQueue = new NpcMovement(this);
    @Override
    public NpcMovement getMovementQueue() {
        return movementQueue;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NPC && ((NPC) other).getIndex() == getIndex() && ((NPC) other).id() == id();
    }

    @Override
    public int getSize() {
        return def == null ? 1 :
            Math.max(1, def.size);
    }

    @Override
    public int getBaseAttackSpeed() {
        return combatInfo != null ? combatInfo.attackspeed : 4;
    }

    @Override
    public int attackAnimation() {
        if (combatInfo != null && combatInfo.animations != null) {
            return combatInfo.animations.attack;
        }

        return 422;
    }

    @Override
    public int getBlockAnim() {
        if (combatInfo != null && combatInfo.animations != null) {
            return combatInfo.animations.block;
        }

        return -1;//TODO default
    }

    @Override
    public void autoRetaliate(Entity attacker) {

        // If the bosses' current target has not attacked us back for at least 10, we change target to whoever attacked us last.
        if ((id == 2215 && target_fleeing(Graardor.getBandosArea(), attacker))
            || (id == 3162 && target_fleeing(KreeArra.getENCAMPMENT(), attacker))
            || (id == 2205 && target_fleeing(Zilyana.getENCAMPMENT(), attacker))
            || (id == 3129 && target_fleeing(Kril.getENCAMPMENT(), attacker))
            || (id == 7709)
            || (id == 7710)
            || (id == 7707) || Zulrah.is(this)) {
            return;
        }
        if (def != null && combatInfo != null && !combatInfo.retaliates) {
            Debugs.CMB.debug(attacker, "mob cant retal");
            //System.out.println("STOP AUTORETALIATE");
            return;
        }
        super.autoRetaliate(attacker);
    }

    public PetDefinitions petType() {
        return this.getAttribOr(AttributeKey.PET_TYPE, null);
    }

    public Tile walkTo;
    public Predicate<Tile> skipReachCheck;

    public void remove() {
        //Only remove if this npc is registered
        if (isRegistered())
            World.getWorld().unregisterNpc(this);
    }

    private boolean canAttack = true;

    public boolean canAttack() {
        return canAttack;
    }

    public NPC canAttack(boolean canAttack) {
        this.canAttack = canAttack;
        return this;
    }

}
