package com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.FaceDirection;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.masks.impl.tinting.Tinting;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import com.cryptic.model.entity.npc.droptables.ItemDrops;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.model.phase.PhaseStage;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.BLOOD_REAVER;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.NEX_11282;
import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars.nex;
import static com.cryptic.utility.ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD;

public class NexCombat extends CommonCombatMethod {
    private int attackCount;
    private static final int TURMOIL_GFX = 2016;
    private static final int MELEE_ATTACK_ANIM = 9180;
    private static final int MELEE_ATTACK_ZAROS_PHASE = 9181;
    private static final int VIRUS_ATTACK_ANIM = 9189;
    private static final int MAGIC_ATTACK_ANIM = 9189;//Shared animation
    private static final int BLOOD_SIPHON_ANIM = 9183;
    private static final int EMBRACE_DARKNESS_ATTACK_ANIM = 9182;
    private static final int SHADOW_SMASH_ATTACK_ANIM = 9186;
    private static final int SMOKE_BULLET_ATTACK_ANIM = 9178;
    private static final int FLYING_PLAYER_ANIM = 1157;
    private static final int MAGIC_ATTACK_MAX = 32;
    private static final int RANGED_ATTACK_MAX = 60;
    private static final int MELEE_ATTACK_MAX = 29;
    private static final int DRAG_ATTACK_MAX = 30;
    private static final int SMOKE_BULLET_ATTACK_MAX = 50;
    private static final int SHADOW_SMASH_ATTACK_MAX = 50;
    public static final int BLOOD_SACRIFICE_ATTACK_MAX = 50;
    private static final int CONTAINMENT_SPECIAL_ATTACK_MAX = 60;
    private static final int ICE_PRISON_SPECIAL_ATTACK_MAX = 75;

    private static final int[] DRAIN = {Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE};

    public static final Area NEX_AREA = new Area(2910, 5189, 2939, 5217);

    public static Tile[] NO_ESCAPE_TELEPORTS = {new Tile(2925, 5212, 0), // north
        new Tile(2934, 5203, 0), // east,
        new Tile(2924, 5194, 0), // south
        new Tile(2916, 5203, 0),}; // west

    @Getter
    @Setter
    public static AtomicBoolean darknessTickBoolean = new AtomicBoolean(false);
    Map<Player, Integer> damageMap = new HashMap<>();
    @Override
    public void init(NPC npc) {
        npc.clearAttrib(SMOKE_PHASE_INITIATED);
        npc.clearAttrib(SHADOW_PHASE_INITIATED);
        npc.clearAttrib(BLOOD_PHASE_INITIATED);
        npc.clearAttrib(AttributeKey.ICE_PHASE_INITIATED);
        npc.useSmartPath = true; // wont get stuck on corners
        npc.def().ignoreOccupiedTiles = true; // walk through minions
        npc.lockMoveDamageOk();
        npc.getMovement().reset();
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 50);
        Chain.noCtx().repeatingTask(1, t -> {
            if (npc.dead()) {
                t.stop();
                return;
            }
            if (npc.locked()) { // still in setup phase. wait till all minions spawned
                return;
            }
            if (World.getWorld().getPlayers().stream().filter(Objects::nonNull).noneMatch(NEX_AREA::contains)) {
                ZarosGodwars.clear();
                t.stop();
            }
        });
        npc.getCombatInfo().scripts.agro_ = (_, p) -> NEX_AREA.contains(p);
    }

    public int lastAttack;

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        if (nex == null) {
            return false;
        }

        if (nex.doingSiphon) {
            return false;
        }

        if (nex.progressingPhase.get()) {
            return false;
        }

        nex.faceEntity(target);
        attackCount += 1;
        lastAttack = 0;


        switch (nex.getPhase().getStage()) {
            case ONE -> {
                if (withinDistance(1) && World.getWorld().rollDie(2)) {
                    basicAttack(nex, target);
                    return true;
                }
                if (attackCount % 5 == 0) {
                    if (World.getWorld().rollDie(10)) {
                        //smokeDash();
                    } else {
                        choke();
                    }
                } else {
                    smokeRush();
                }
                // drag(nex);
            }
            case TWO -> {
                if (nex.darkenScreen.get()) {
                    embraceDarkness();
                }
                if (attackCount % 5 == 0) {
                    if (World.getWorld().rollDie(4)) {
                        shadowSmash();
                    }
                } else {
                    shadowShots();
                }
            }
            case THREE -> {
                if (attackCount % 5 == 0) {
                    if (World.getWorld().rollDie(4)) {
                        bloodSacrifice(target);
                    } else {
                        bloodSiphon();
                    }
                } else {
                    bloodBarrage();
                }
            }
            case FOUR -> {
                if (attackCount % 5 == 0) {
                    if (World.getWorld().rollDie(6)) {
                        icePrison(target);
                    } else {
                        containment();
                    }
                } else {
                    iceBarrage();
                }
            }
            case FIVE -> {
                if (World.getWorld().rollDie(15, 1)) {
                    if (!nex.turmoil) {
                        nex.getCombat().delayAttack(2);
                        nex.animate(9179);
                        nex.graphic(TURMOIL_GFX);
                        nex.turmoil = true;
                    }
                }
                if (withinDistance(1)) {
                    if (World.getWorld().rollDie(3, 1)) {
                        magic();
                    } else {
                        zarosMelee(target);
                    }
                    return true;
                }

                if (World.getWorld().rollDie(5, 1)) {
                    leech(target);
                } else {
                    magic();
                }
            }
        }
        return true;
    }

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    private void zarosMelee(Entity target) {
        nex.animate(MELEE_ATTACK_ZAROS_PHASE);
        int damage = nex.getCombatInfo().maxhit;
        if (nex.turmoil) damage *= 1.50;

        if (World.getWorld().rollDie(10, 2) && nex.turmoil) {
            target.hit(nex, World.getWorld().random(1, 8), 1, CombatType.MELEE).checkAccuracy(true).submit();
            return;
        }

        target.hit(nex, World.getWorld().random(damage), 1, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void magic() {
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            if (!inNexArea(t.tile())) {
                continue;
            }
            var tileDist = entity.tile().distance(target.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(nex, target, 2007, 51, duration, 43, 31, 0, nex.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            Hit hit = t.hit(nex, World.getWorld().random(MAGIC_ATTACK_MAX), delay, CombatType.MAGIC).checkAccuracy(true).postDamage(h -> {
                if (h.isAccurate()) {
                    h.getTarget().graphic(2008, GraphicHeight.MIDDLE, p.getSpeed());
                    h.getTarget().skills().alterSkill(Skills.PRAYER, -5);
                }
            });
            hit.submit();
        }
    }

    private void leech(Entity target) {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(nex, target, 2010, 51, duration, 43, 31, 0, nex.getSize(), 10);
        entity.executeProjectile(p);
        for (int skill : DRAIN) {
            int take = 5;
            target.graphic(2011, GraphicHeight.LOW, p.getSpeed());
            target.player().skills().alterSkill(skill, -take);
            nex.getCombatInfo().stats.attack += take;
            nex.getCombatInfo().stats.strength += take;
            nex.getCombatInfo().stats.defence += take;
        }
    }

    private void bloodSacrifice(Entity target) {
        short delay = 0;
        short duration = 240;
        byte hue = 0;
        byte sat = 6;
        byte lum = 28;
        byte opac = 108;
        nex.forceChat("I demand a blood sacrifice!");
        final Player player = (Player) target;
        player.message("Nex has marked you as a sacrifice, RUN!");

        for (final Entity selectedTarget : getPossibleTargets(nex)) {
            selectedTarget.setTinting(new Tinting(delay, duration, hue, sat, lum, opac));
        }

        Chain.bound(null).name("bloodsacrifice").cancelWhen(() -> {
            return !entity.tile().isWithinDistance(target.tile(), 5) || target.dead() || entity.dead(); // cancels as expected
        }).runFn(8, () -> {
            int damage = World.getWorld().random(BLOOD_SACRIFICE_ATTACK_MAX);
            player.performGraphic(new Graphic(2003, GraphicHeight.HIGH, 1));
            player.hit(nex, damage);
            nex.heal(damage);
            int currentLevel = player.skills().level(Skills.PRAYER);
            int drain = currentLevel / 3;
            player.skills().alterSkill(Skills.PRAYER, -drain);
            player.message("You didn't make it far enough in time - Nex fires a punishing attack!");

            for (final Entity t : getPossibleTargets(nex)) {
                if (t.tile().isWithinDistance(player.tile(), 7)) {
                    damage = World.getWorld().random(1, 12);
                    t.hit(nex, damage);
                    nex.heal(damage);
                    t.graphic(2003);
                }
            }
        });
    }

    public void bloodSiphon() {
        if (nex.lastSiphon < Utils.currentTimeMillis()) {
            nex.lastSiphon = Utils.currentTimeMillis() + 30000;
            nex.lockMoveDamageOk();
            nex.getMovement().reset();
            nex.graphic(2015, GraphicHeight.LOW, 30);
            nex.killBloodReavers();
            nex.forceChat("A siphon will solve this!");
            nex.animate(BLOOD_SIPHON_ANIM);
            nex.doingSiphon = true;
            int maxMinions = Math.min(9 - nex.bloodReavers.size(), Arrays.stream(nex.closePlayers()).filter(NEX_AREA::contains).toArray().length); // one per player
            if (maxMinions > 8) {
                maxMinions = 8;
            }
            if (maxMinions != 0) {
                for (int i = 0; i < maxMinions; i++) {
                    List<Tile> tiles = nex.tile().area(7, pos -> World.getWorld().clipAt(pos) == 0 && !pos.equals(entity.tile()) && !ProjectileRoute.hasLineOfSight(entity, pos));
                    tiles.removeIf(t -> t.x <= 2909);
                    Tile destination = Utils.randomElement(tiles);
                    NPC bloodReaver = new NPC(BLOOD_REAVER, destination);
                    if (nex.bloodReavers != null)
                        nex.bloodReavers.add(bloodReaver);
                    bloodReaver.spawn(false);
                    bloodReaver.graphic(2017, GraphicHeight.LOW, 30);
                }
            }
            Chain.bound(null).runFn(8, () -> {
                nex.doingSiphon = false;
                nex.unlock();
            });
        }
    }

    private void bloodBarrage() {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 2002, 51, duration, 43, 0, 0, nex.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        nex.animate(MAGIC_ATTACK_ANIM);
        nex.graphic(2001, GraphicHeight.HIGH, 0);
        for (Entity t : getPossibleTargets(nex)) {
            if (!inNexArea(t.tile())) {
                continue;
            }
            t.hit(nex, World.getWorld().random(MAGIC_ATTACK_MAX), delay, CombatType.MAGIC).checkAccuracy(true).postDamage(h -> {
                if (h.isAccurate()) {
                    int heal = h.getDamage() / 4;
                    if (heal <= 0) {
                        heal = 1;
                    }
                    nex.healHit(nex, heal);
                }
            }).submit();
            target.graphic(2001, GraphicHeight.HIGH, p.getSpeed());
        }
    }

    public static boolean inNexArea(Tile tile) {
        return tile.inArea(NEX_AREA);
    }

    public void embraceDarkness() {
        nex.forceChat("Embrace darkness!");
        nex.animate(EMBRACE_DARKNESS_ATTACK_ANIM, 30);
        nex.darkenScreen.getAndSet(false);

        for (Entity t : getPossibleTargets(nex)) {
            if (!inNexArea(t.tile())) continue;
            if (t instanceof Player player) {
                if (!inNexArea(player.tile())) continue;

                AtomicInteger tick = new AtomicInteger();
                Chain.bound(null).repeatingTask(1, chain -> {
                    synchronized (tick) {
                        tick.getAndIncrement();
                        if (tick.get() == 33) {
                            player.getPacketSender().darkenScreen(0);
                            chain.stop();
                            nex.darkenScreen.getAndSet(false);
                            return;
                        }

                        int distance = Utils.getDistance(player.getX(), player.getY(), nex.getX(), nex.getY());
                        int opacity = 200 - (distance * 17);
                        if (opacity <= 30) opacity = 30;
                        player.getPacketSender().darkenScreen(opacity);
                        if (distance <= 3) player.hit(nex, 5);
                    }
                });
            }
        }

        Chain.bound(null).runFn(33, () -> {
            for (Entity targets : getPossibleTargets(nex)) {
                if (targets instanceof Player player) {
                    player.getPacketSender().darkenScreen(0);
                    nex.darkenScreen.getAndSet(false);
                }
            }
        });
    }

    private void shadowSmash() {
        nex.forceChat("Fear the Shadow!");
        nex.animate(SHADOW_SMASH_ATTACK_ANIM);
        ArrayList<Entity> possibleTargets = getPossibleTargets(nex);
        final HashMap<String, int[]> tiles = new HashMap<>();
        for (Entity t : possibleTargets) {
            if (!inNexArea(t.tile())) {
                continue;
            }
            String key = t.getX() + "_" + t.getY();
            if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
                tiles.put(key, new int[]{t.getX(), t.getY()});
            }
        }
        List<GameObject> shadows = new ArrayList<>();
        Chain.bound(null).runFn(1, () -> {
            for (int[] tile : tiles.values()) {
                shadows.add(GameObject.spawn(42942, tile[0], tile[1], 0, 10, 0));
            }
        }).then(3, () -> {
            for (GameObject obj : shadows) {
                obj.remove();
            }
            shadows.clear();
            for (int[] tile : tiles.values()) {
                World.getWorld().sendClippedTileGraphic(383, new Tile(tile[0], tile[1], 0), 0, 0);
                for (Entity t : possibleTargets) {
                    if (t.getX() == tile[0] && t.getY() == tile[1]) {
                        t.hit(nex, World.getWorld().random(1, SHADOW_SMASH_ATTACK_MAX));
                    }
                }
            }
        });
    }

    private void shadowShots() {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            if (!inNexArea(t.tile())) {
                continue;
            }
            Projectile p = new Projectile(nex, t, 378, 51, duration, 43, 35, 0, t.getSize(), 10);
            final int delay = nex.executeProjectile(p);
            int damage = 0;
            if (t.tile().distance(nex.tile()) <= 2) {
                damage = RANGED_ATTACK_MAX;
            } else if (t.tile().distance(nex.tile()) <= 4) {
                damage = RANGED_ATTACK_MAX - 20;
            } else if (t.tile().distance(nex.tile()) > 6) {
                damage = RANGED_ATTACK_MAX - 30;
            }
            if (Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES)) {
                damage = damage / 2;
            }
            t.hit(nex, World.getWorld().random(damage), delay, CombatType.RANGED).ignorePrayer().checkAccuracy(true).postDamage(h -> {
                // Successful hits can drain prayer points slightly, which can be reduced by the spectral spirit shield.
                if (h.isAccurate()) {
                    t.skills().alterSkill(Skills.PRAYER, t.player().getEquipment().hasAt(EquipSlot.SHIELD, SPECTRAL_SPIRIT_SHIELD) ? -2 : -3);
                }
            }).submit();
            t.graphic(379, GraphicHeight.MIDDLE, p.getSpeed());
        }
    }

    public void smokeDash() {
        if (nex.phase.getStage() == PhaseStage.ONE) {
            return;
        }
        lastAttack = 1;
        nex.lastNoEscape = Utils.currentTimeMillis() + 30000;
        nex.forceChat("There is...");
        //nex.cantInteract(true); // rsps friendly
        nex.getCombat().reset();
        final int idx = Utils.random(NO_ESCAPE_TELEPORTS.length);
        // yes, teleport (not very visually user-friendly)
        // to the outskirt of the room, then dash inwards.
        final Tile selectedTile = NO_ESCAPE_TELEPORTS[idx];
        final Tile center = new Tile(2924, 5202, 0);
        nex.lockMoveDamageOk();
        nex.getMovement().reset();
        nex.animate(SMOKE_BULLET_ATTACK_ANIM);
        nex.setPositionToFace(selectedTile);
        nex.faceEntity(null);
        var start = nex.tile();

        Chain.bound(null).runFn(3, () -> {
            var face = FaceDirection.forTargetTile(start, selectedTile);
            //final ForceMovement fm = new ForceMovement(selectedTile, null, 15, 0, face);
            // nex.forceChat(face+" 1");
            //nex.setForceMovement(fm);
            nex.lock();
            nex.getMovementQueue().clear();
            nex.teleport(selectedTile);
        }).then(1, () -> {
            //nex.face(Direction.getDirection(selectedTile, center));
            nex.setPositionToFace(center);
            nex.forceChat("NO ESCAPE!");
        }).then(1, () -> { // lands
            var face = FaceDirection.forTargetTile(selectedTile, center);
            //final ForceMovement fm = new ForceMovement(center, null, 60, 0, face);
            //  nex.forceChat(face+" 2");
            //nex.setForceMovement(fm);
            nex.stepAbs(center.getX(), center.getY(), MovementQueue.StepType.FORCED_RUN);

            //Look for potential victims
            for (Entity p : nex.calculatePossibleTargets(center, selectedTile, idx == 0 || idx == 2)) {
                if (p instanceof Player) {
                    Chain.bound(null).runFn(1, () -> {
                        p.lock();
                        p.animate(FLYING_PLAYER_ANIM);
                        p.graphic(245, GraphicHeight.HIGH_5, 124);
                        p.hit(p, World.getWorld().random(SMOKE_BULLET_ATTACK_MAX));
                        p.stun(2, true);
                        int diffX = center.x - p.getAbsX();
                        int diffY = center.y - p.getAbsY();
                        //TaskManager.submit(new ForceMovementTask(p.player(), 3, new ForceMovement(p.tile().clone(), new Tile(diffX, diffY), 10, 60, idx == 3 ? FaceDirection.WEST : idx == 2 ? FaceDirection.NORTH_EAST : idx == 1 ? FaceDirection.NORTH : FaceDirection.NORTH_WEST)));
                        p.unlock();
                    });
                }
            }
        }).then(7, () -> {
            nex.teleport(center);
            nex.animate(nex.def().standingAnimation);
            nex.unlock();
            nex.getCombat().setTarget(Utils.randomElement(getPossibleTargets(nex)));
            nex.cantInteract(false);
        });
    }

    /**
     * Nex will drag a random player towards her, stunning them and deactivate their protection prayer.
     * Nex will only drag players if they are within 6 tiles away.
     * There is a 1/4 chance for Nex to drag a player if they do not have Protect from Magic on and a 1/8 chance if they do.
     */
    private void drag(Nex nex) {
        Entity target = Utils.randomElement(getPossibleTargets(nex));
        if (target == null) {
            return; // No targets found
        }
        if (target.tile().distanceTo(nex.tile()) < 6) {
            return;
        }
        var drag = World.getWorld().rollDie(Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC) ? 4 : 8);
        if (target.isPlayer() && drag) {
            int vecX = (nex.getAbsX() - Utils.getClosestX(nex, target.tile()));
            int vecY = (nex.getAbsY() - Utils.getClosestY(nex, target.tile()));
            FaceDirection dir;
            if (vecX == -1) dir = FaceDirection.EAST;
            else if (vecX == 1) dir = FaceDirection.WEST;
            else if (vecY == -1) dir = FaceDirection.NORTH;
            else dir = FaceDirection.SOUTH;

            Tile tile = Utils.randomElement(nex.getCentrePosition().area(7, t -> World.getWorld().meleeClip(t.x, t.y, t.level) == 0));
            // Cancel when player is not in the Nex region
            Chain.bound(null).cancelWhen(() -> target.tile().region() != 11601).runFn(1, () -> {
                final Player p = target.getAsPlayer();
                p.lock();
                p.getMovement().reset();
                p.animate(FLYING_PLAYER_ANIM);
                p.graphic(1998, GraphicHeight.HIGH_5, 124);
                p.hit(nex, World.getWorld().random(DRAG_ATTACK_MAX));
                p.stun(2, true);
                CombatFactory.disableProtectionPrayers(p);
                int diffX = tile.x - p.getAbsX();
                int diffY = tile.y - p.getAbsY();
                //ForceMovement forceMovement = new ForceMovement(p.tile().clone(), new Tile(diffX, diffY), 10, 60, dir);
                //TaskManager.submit(new ForceMovementTask(p, 1, forceMovement));
                p.unlock();
            });
        }
    }

    private void choke() {
        AtomicInteger count = new AtomicInteger(0);
        var possibleTargets = getPossibleTargets(nex);
        int furthestDistance = getFurthestDistanceToPoint(possibleTargets);
        possibleTargets
            .stream()
            .filter(p -> !p.hasAttrib(AttributeKey.CHOKED))
            .filter(p -> !p.getAsPlayer().getEquipment().wearingSlayerHelm())
            .filter(f -> furthestDistance != 0).filter(p -> p.tile().distanceTo(nex.tile()) == furthestDistance).forEach(p -> {
                nex.forceChat("Let the virus flow through you!");
                p.putAttrib(AttributeKey.CHOKED, true);
                tickChoke(count, p);
            });
    }

    private void tickChoke(AtomicInteger count, Entity p) {
        Chain.noCtx().repeatingTask(5, choke -> {
            if (count.get() >= 10 || p.dead() || !inNexArea(p.tile())) {
                p.clearAttrib(AttributeKey.CHOKED);
                choke.stop();
                return;
            }
            count.getAndIncrement();
            getPossibleTargets(p).stream().filter(f -> p.tile().nextTo(f.tile())).forEach(f -> tickChoke(new AtomicInteger(0), f));
            p.forceChat("*Cough*");
            p.hit(nex, 5, 1, null).setAccurate(true).submit();
        });
    }

    private int getFurthestDistanceToPoint(ArrayList<Entity> possibleTargets) {
        int furthestDistance = possibleTargets.stream().mapToInt(p -> nex.tile().distance(p.tile())).max().orElse(0);
        possibleTargets.sort(Comparator.comparingInt(p -> p.getBonuses().totalBonuses(p, World.getWorld().equipmentInfo()).magedef));
        return furthestDistance;
    }

    private void smokeRush() {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            if (!inNexArea(t.tile())) {
                continue;
            }
            Projectile p = new Projectile(nex, t, 384, 51, duration, 43, 35, 0, t.getSize(), 10);
            final int delay = nex.executeProjectile(p);
            t.hit(nex, World.getWorld().random(MAGIC_ATTACK_MAX), delay, CombatType.MAGIC).checkAccuracy(true).postDamage(h -> {
                if (h.isAccurate()) {
                    if (World.getWorld().rollDie(100, 25)) {
                        h.getTarget().hit(nex, 2, HitMark.POISON);
                        h.getTarget().poison(2);
                    }
                    h.getTarget().graphic(1998, GraphicHeight.MIDDLE, p.getSpeed());
                }
            }).submit();
        }
    }

    //which will freeze them if they are not praying Protect from Magic and lowers their prayer points by half of the damage she deals, reduced to one third by
    // the spectral spirit shield
    private void iceBarrage() {
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            if (!inNexArea(t.tile())) {
                continue;
            }
            Projectile p = new Projectile(nex, t, 362, 51, duration, 43, 31, 0, t.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            t.hit(nex, World.getWorld().random(MAGIC_ATTACK_MAX), delay, CombatType.MAGIC).checkAccuracy(true).postDamage(h -> {
                if (h.isAccurate() && !Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MAGIC)) {
                    h.getTarget().graphic(2005, GraphicHeight.MIDDLE, p.getSpeed());
                    h.getTarget().freeze(33, nex, true);
                    int drain = t.player().getEquipment().hasAt(EquipSlot.SHIELD, SPECTRAL_SPIRIT_SHIELD) ? h.getDamage() / 3 : h.getDamage() / 2;
                    h.getTarget().skills().alterSkill(Skills.PRAYER, -drain);
                }
            }).submit();
        }
    }

    private void containment() {
        nex.lockMoveDamageOk();
        nex.forceChat("Contain this!");
        nex.animate(SHADOW_SMASH_ATTACK_ANIM);
        var copy = nex.getCentrePosition().copy();
        var area = copy.area(2);
        Set<Tile> tiles = new HashSet<>();
        var middle = area.middleTile();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if ((Math.abs(dx) == 2 || Math.abs(dy) == 2) && (dx != 2 || dy != 2) && (dx != 2 || dy != -2) && (dx != -2 || dy != 2) && (dx != -2 || dy != -2)) {
                    tiles.add(middle.transform(dx, dy));
                }
            }
        }

        Chain.noCtx().runFn(4, () -> {
            for (Tile tile : tiles) {
                if (RegionManager.zarosBlock(tile)) continue;
                nex.stalagmite.add(GameObject.spawn(42944, tile.getX(), tile.getY(), tile.getZ(), 10, 0));

            }
        }).then(7, () -> {
            for (Tile tile : tiles) {
                getPossibleTargets(nex).forEach(t -> {
                    if (tile.isWithinDistance(t.tile(), 5) && !nex.stalagmiteDestroyed) {
                        CombatFactory.disableProtectionPrayers((Player) t);
                        int damage = Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES) ? CONTAINMENT_SPECIAL_ATTACK_MAX / 2 : CONTAINMENT_SPECIAL_ATTACK_MAX;
                        Hit hit = t.hit(nex, World.getWorld().random(damage), CombatType.MAGIC);
                        hit.submit();
                    }
                });
                break;
            }
            for (GameObject obj : nex.stalagmite) {
                obj.remove();
            }
            nex.stalagmite.clear();
            nex.stalagmiteDestroyed = false;
            nex.unlock();
        });
    }

    private void icePrison(Entity target) {

        nex.forceChat("Die now, in a prison of ice!");
        nex.animate(SHADOW_SMASH_ATTACK_ANIM);

        target.freeze(5, nex, true);

        var playerTile = target.getCentrePosition();
        var area = playerTile.area(1);
        Set<Tile> tiles = new HashSet<>();
        area.forEachPos(tile -> {
            if (tile.equals(playerTile)) return;
            tiles.add(tile);
        });
        Chain.bound(null).runFn(4, () -> {
            for (Tile tile : tiles) {
                if (RegionManager.zarosBlock(tile)) continue;
                nex.stalagmite.add(GameObject.spawn(42944, tile.getX(), tile.getY(), tile.getZ(), 10, 0));
            }
        }).then(7, () -> {
            for (Tile tile : tiles) {

                getPossibleTargets(nex).forEach(t -> {
                    if (tile.isWithinDistance(t.tile(), 3) && !nex.stalagmiteDestroyed) {
                        CombatFactory.disableProtectionPrayers((Player) t);
                        int damage = Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES) ? ICE_PRISON_SPECIAL_ATTACK_MAX / 2 : ICE_PRISON_SPECIAL_ATTACK_MAX;
                        Hit hit = t.hit(nex, World.getWorld().random(damage), CombatType.MAGIC);
                        hit.submit();
                    }
                });
                break;
            }
            for (GameObject obj : nex.stalagmite) {
                obj.remove();
            }
            nex.stalagmite.clear();
            nex.stalagmiteDestroyed = false;
        });
    }

    @Override
    public int getAttackSpeed(Entity mob) {
        return lastAttack == 1 ? 12 : mob.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 30;
    }

    @Override
    public void doFollowLogic() {
        if (nex.progressingPhase.get()) {
            return;
        }
        TargetRoute.set(entity, target, 1);
    }

    @Override
    public void postDamage(Hit hit) {
        if (hit.getTarget().isNpc() && hit.getSource().isPlayer()) {
            if (nex.id() == NpcIdentifiers.NEX_11280 && hit.getCombatType() == CombatType.MELEE) {
                Entity source = hit.getSource();
                if (source != null) {
                    int deflectedDamage = (int) (hit.getDamage() * 0.1);
                    if (deflectedDamage > 0)
                        source.hit(hit.getTarget(), deflectedDamage, 1, null).setIsReflected().submit();
                }
            }
        }
        if (Objects.requireNonNull(hit.getSource()).isNpc()) {
            if (nex.soulsplit) {
                nex.healHit(nex, hit.getDamage());
            }
        }

        incrementDamageMap(hit, hit.getSource(), target);
    }

    @Override
    public void preDefend(Hit hit) {
        if (hit.getTarget().isNpc() && hit.getSource().isPlayer()) {
            if (nex.phase.getStage() == PhaseStage.ONE && nex.hp() <= 2720 && !nex.<Boolean>getAttribOr(SMOKE_PHASE_INITIATED, false)) {
                nex.forceChat("Fumus, don't fail me!");
                nex.getCombat().delayAttack(1);
                nex.bodyguardPhase = BodyguardPhase.FUMUS;
                ZarosGodwars.fumus.putAttrib(AttributeKey.BARRIER_BROKEN, true);
                nex.putAttrib(SMOKE_PHASE_INITIATED, true);
            } else if (nex.phase.getStage() == PhaseStage.TWO && nex.hp() <= 2040 && !nex.<Boolean>getAttribOr(SHADOW_PHASE_INITIATED, false)) {
                nex.forceChat("Umbra, don't fail me!");
                nex.getCombat().delayAttack(1);
                nex.bodyguardPhase = BodyguardPhase.UMBRA;
                ZarosGodwars.umbra.putAttrib(AttributeKey.BARRIER_BROKEN, true);
                nex.putAttrib(SHADOW_PHASE_INITIATED, true);
            } else if (nex.phase.getStage() == PhaseStage.THREE && nex.hp() <= 1360 && !nex.<Boolean>getAttribOr(BLOOD_PHASE_INITIATED, false)) {
                nex.forceChat("Cruor, don't fail me!");
                nex.getCombat().delayAttack(1);
                nex.bodyguardPhase = BodyguardPhase.CRUOR;
                ZarosGodwars.cruor.putAttrib(AttributeKey.BARRIER_BROKEN, true);
                nex.putAttrib(BLOOD_PHASE_INITIATED, true);
            } else if (nex.phase.getStage() == PhaseStage.FOUR && nex.hp() <= 680 && !nex.<Boolean>getAttribOr(ICE_PHASE_INITIATED, false)) {
                nex.forceChat("Glacies, don't fail me!");
                nex.getCombat().delayAttack(1);
                nex.bodyguardPhase = BodyguardPhase.GLACIES;
                ZarosGodwars.glacies.putAttrib(AttributeKey.BARRIER_BROKEN, true);
                nex.putAttrib(AttributeKey.ICE_PHASE_INITIATED, true);
            }
        }

        if (nex.bodyguardPhase != null) {
            hit.block();
        }

        if (nex.phase.getStage() == PhaseStage.THREE) {
            if (nex.doingSiphon && hit.getSource().isPlayer()) {
                nex.healHit(nex, hit.getDamage());
            }
        }
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        var nex = (Nex) hit.getTarget();
        if (hit.getTarget().isNpc()) {
            NPC npc = hit.getTarget().npc();
            Player player = hit.getSource().player();
            npc.transmog(NEX_11282, false);
            final NPCCombatInfo combatInfo = npc.getCombatInfo();
            npc.animate(combatInfo.animations.death);
            Chain.bound(null).runFn(combatInfo.deathlen, () -> {
                npc.graphic(2013, GraphicHeight.LOW, 30);
                var list = Lists.newArrayList(
                    new Tile(npc.getX() + 1, npc.getY() - 2, npc.getZ()),
                    new Tile(npc.getX() - 1, npc.getY() - 1, npc.getZ()),
                    new Tile(npc.getX() + 3, npc.getY() - 1, npc.getZ()),
                    new Tile(npc.getX() + 3, npc.getY() - 1, npc.getZ()),
                    new Tile(npc.getX() - 1, npc.getY() + 3, npc.getZ()),
                    new Tile(npc.getX() - 1, npc.getY() + 3, npc.getZ()),
                    new Tile(npc.getX() + 1, npc.getY() + 4, npc.getZ())
                );
                list.removeIf(t -> !MovementQueue.dumbReachable(t.getX(), t.getY(), nex.tile()));

                for (Player close : npc.closePlayers(10)) {
                    if (!NEX_AREA.contains(close)) continue;
                    for (Tile tile : list) {
                        var projectile = new Projectile(npc.getCentrePosition(), tile, 1, 2012, 100, 40, tile.getZ(), 0, 0);
                        nex.executeProjectile(projectile);
                        World.getWorld().sendClippedTileGraphic(2014, tile, 0, 85);
                    }
                    close.hit(npc, World.getWorld().random(40));
                }
            }).then(3, () -> {
                for (NPC re : nex.bloodReavers) {
                    if (re == null) {
                        continue;
                    }
                    re.remove();
                }

                ZarosGodwars.clear();

                Arrays.stream(npc.closePlayers()).forEach(close -> {
                    HealthHud.close(close);
                    close.getPacketSender().sendEffectTimer(12, EffectTimer.MONSTER_RESPAWN);
                });
                new GameObject(42967, new Tile(2909, 5202, 0), 10, 1).spawn(); // spawn purple
                drop();

            }).then(20, ZarosGodwars::startEvent);
        }
        return true;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    private void drop() {
        var nex = this.entity.getAsNpc();
        var amountOfPlayersToGetDrop = 5;
        var list = nex.getCombat().getDamageMap().entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().getDamage())).collect(Collectors.collectingAndThen(Collectors.toList(), l -> {
            Collections.reverse(l);
            return l;
        }));
        list.stream().limit(amountOfPlayersToGetDrop).forEach(e -> {
            var key = e.getKey();
            Player player = (Player) key;
            AchievementsManager.handleNexAchievement(player);
            if (nex.tile().isWithinDistance(player.tile(), 12)) {
                ItemDrops drop = new ItemDrops();
                drop.rollTheDropTable(player, nex);
            }
        });
    }

    final void incrementDamageMap(Hit hit, Entity source, Entity target) {
        if (source instanceof Player player && target instanceof NPC) {
            if (!damageMap.containsKey(player)) damageMap.put(player, hit.getDamage());
            else damageMap.computeIfPresent(player, (_, v) -> v + hit.getDamage());
        }
    }

    final void computeDropTable(NPC npc, ItemDrops drops) {
        for (var entry : damageMap.entrySet()) {
            var player = entry.getKey();
            var damage = entry.getValue();
            if (player == null || damage < 100) continue;
            drops.rollTheDropTable(player, npc);
        }
    }

    @Override
    public ArrayList<Entity> getPossibleTargets(Entity mob) {
        if (inNexArea(mob.tile())) {
            return Arrays.stream(mob.closePlayers(64)).filter(NEX_AREA::contains).collect(Collectors.toCollection(ArrayList::new));
        }
        return null;
    }

}
