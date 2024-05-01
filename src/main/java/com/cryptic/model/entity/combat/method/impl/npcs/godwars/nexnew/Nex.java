package com.cryptic.model.entity.combat.method.impl.npcs.godwars.nexnew;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.masks.impl.tinting.Tinting;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.BLOOD_REAVER;
import static com.cryptic.model.content.collection_logs.LogType.BOSSES;
import static com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.NexCombat.BLOOD_SACRIFICE_ATTACK_MAX;
import static com.cryptic.utility.ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD;

@SuppressWarnings("ALL")
public class Nex extends NPC {
    private NexStage stage;
    private boolean unlock;
    Map<Player, Integer> damageMap;
    private final List<NPC> bloodReavers;
    private final List<GameObject> stalagmites;
    public long lastSiphon;
    private boolean soulsplit;
    private boolean turmoil;
    private boolean siphon;
    private boolean introduction = false;
    private boolean initiated = false;
    private int intervalCount = 0;
    private int attackInterval = 5;
    MutableObject<NPC> fumus = new MutableObject<>();
    MutableObject<NPC> umbra = new MutableObject<>();
    MutableObject<NPC> cruor = new MutableObject<>();
    MutableObject<NPC> glacies = new MutableObject<>();
    public static final Area NEX_AREA = new Area(2910, 5189, 2939, 5217);

    public Nex(int id, Tile tile, NexStage stage) {
        super(id, tile);
        this.stage = stage;
        this.useSmartPath = true;
        this.bloodReavers = new ArrayList<>();
        this.stalagmites = new ArrayList<>();
        this.damageMap = new HashMap<>();
        this.ignoreOccupiedTiles = true;
        this.setCombatMethod(null);
        this.spawnDirection(Direction.WEST.toInteger());
    }

    private void choke() {
        AtomicInteger count = new AtomicInteger(0);
        var possibleTargets = getPossibleTargets(this);
        int furthestDistance = getFurthestDistanceToPoint(possibleTargets);
        possibleTargets.stream().filter(p -> !p.hasAttrib(AttributeKey.CHOKED)).filter(p -> !p.getAsPlayer().getEquipment().wearingSlayerHelm()).filter(f -> furthestDistance != 0).filter(p -> p.tile().distanceTo(this.tile) == furthestDistance).forEach(p -> {
            this.forceChat("Let the virus flow through you!");
            p.putAttrib(AttributeKey.CHOKED, true);
            tickChoke(count, p);
        });
    }

    private void tickChoke(AtomicInteger count, Entity p) {
        Chain.noCtx().repeatingTask(5, choke -> {
            if (count.get() >= 10 || this.stage.equals(NexStage.TWO) || p.dead() || !inNexArea(p.tile())) {
                p.clearAttrib(AttributeKey.CHOKED);
                choke.stop();
                return;
            }
            var c = count.getAndIncrement();
            getPossibleTargets(p).stream().filter(f -> p.tile().nextTo(f.tile())).forEach(f -> tickChoke(new AtomicInteger(0), f));
            p.forceChat("*cough*");
            p.hit(this, 5, 1, null).setAccurate(true).submit();
        });
    }

    private int getFurthestDistanceToPoint(ArrayList<Entity> possibleTargets) {
        int furthestDistance = possibleTargets.stream().mapToInt(p -> this.tile.distance(p.tile())).max().orElse(0);
        possibleTargets.sort(Comparator.comparingInt(p -> p.getBonuses().totalBonuses(p, World.getWorld().equipmentInfo()).magedef));
        return furthestDistance;
    }

    private void zarosMagic() {
        this.animate(9189);
        for (Entity t : getPossibleTargets(this)) {
            if (!inNexArea(t.tile())) continue;
            var tileDist = this.tile().distance(t.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(this, t, 2007, 51, duration, 41, 25, 8, 15, 10);
//            p.sendProjectile((Player) target);
            final int delay = (int) (p.getSpeed() / 30D);
            new Hit(this, t, World.getWorld().random(0, 32), delay, CombatType.MAGIC).checkAccuracy(true).submit().postDamage(h -> {
                if (h.isAccurate() && !Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MAGIC)) {
                    int drain = t.getAsPlayer().getEquipment().hasAt(EquipSlot.SHIELD, SPECTRAL_SPIRIT_SHIELD) ? h.getDamage() / 3 : h.getDamage() / 2;
                    h.getTarget().skills().alterSkill(Skills.PRAYER, -drain);
                }
            });
            t.graphic(2008, GraphicHeight.HIGH, p.getSpeed());
        }
    }

    private void smokeRush() {
        this.animate(9189);
        for (Entity t : getPossibleTargets(this)) {
            if (!inNexArea(t.tile())) continue;
            var tileDist = this.tile().distance(t.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(this, t, 384, 51, duration, 41, 25, 8, 15, 10);
            final int delay = this.executeProjectile(p);
            new Hit(this, t, World.getWorld().random(0, 3), delay, CombatType.MAGIC).checkAccuracy(true).submit().postDamage(h -> {
                if (h.isAccurate()) {
                    if (World.getWorld().rollDie(100, 25)) {
                        h.getTarget().hit(this, 2, HitMark.POISON);
                        h.getTarget().poison(2);
                    }
                }
            });
            t.graphic(1998, GraphicHeight.MIDDLE, p.getSpeed());
        }
    }

    public void killBloodReavers() {
        if (bloodReavers == null) return;
        for (NPC npc : bloodReavers) {
            if (npc == null) {
                continue;
            }
            if (!npc.dead()) {
                npc.hit(this, npc.hp());
                if (!this.dead()) {
                    this.healHit(this, npc.hp());
                }
            }
        }
    }

    public void bloodSiphon() {
        if (siphon) return;
        if (this.lastSiphon < Utils.currentTimeMillis()) {
            this.siphon = true;
            this.lastSiphon = Utils.currentTimeMillis() + 30000;
            this.lockMoveDamageOk();
            this.getMovement().reset();
            this.graphic(2015, GraphicHeight.LOW, 30);
            this.killBloodReavers();
            this.forceChat("A siphon will solve this!");
            this.animate(9183);
            int maxMinions = Math.min(9 - this.bloodReavers.size(), Arrays.stream(this.closePlayers()).filter(p -> NEX_AREA.contains(p)).toArray().length); // one per player
            if (maxMinions > 8) maxMinions = 8;
            if (maxMinions != 0) {
                for (int i = 0; i < maxMinions; i++) {
                    List<Tile> tiles = NEX_AREA.randomTile().area(2, pos -> pos != null && World.getWorld().clipAt(pos) == 0 && !pos.equals(this.tile().expandedBounds(2)) && MovementQueue.dumbReachable(pos.x, pos.y, this.tile));
                    tiles.removeIf(t -> !inNexArea(t));
                    Tile destination = Utils.randomElement(tiles);
                    NPC bloodReaver = new NPC(BLOOD_REAVER, destination);
                    if (this.bloodReavers != null) this.bloodReavers.add(bloodReaver);
                    Chain.noCtx().runFn(bloodReaver.getCombatInfo().deathlen, () -> {
                        bloodReaver.spawn(false);
                    });
                    bloodReaver.graphic(2017, GraphicHeight.LOW, 30);
                }
            }
            Chain.bound(null).runFn(8, () -> {
                this.siphon = false;
                this.unlock();
            });
        }
    }

    private void bloodSacrifice(Entity target) {
        if (siphon) return;
        short delay = 0;
        short duration = 240;
        byte hue = 0;
        byte sat = 6;
        byte lum = 28;
        byte opac = 108;
        this.forceChat("I demand a blood sacrifice!");
        final Player player = (Player) target;
        player.message("Nex has marked you as a sacrifice, RUN!");

        for (final Entity selectedTarget : getPossibleTargets(this)) selectedTarget.setTinting(new Tinting(delay, duration, hue, sat, lum, opac));

        Chain.bound(null).name("bloodsacrifice").cancelWhen(() -> {
            return !this.tile().isWithinDistance(target.tile(), 5) || target.dead() || this.dead(); // cancels as expected
        }).runFn(8, () -> {
            int damage = World.getWorld().random(BLOOD_SACRIFICE_ATTACK_MAX);
            player.performGraphic(new Graphic(2003, GraphicHeight.HIGH, 1));
            player.hit(this, damage);
            this.heal(damage);
            int currentLevel = player.skills().level(Skills.PRAYER);
            int drain = currentLevel / 3;
            player.skills().alterSkill(Skills.PRAYER, -drain);
            player.message("You didn't make it far enough in time - Nex fires a punishing attack!");

            for (final Entity t : getPossibleTargets(this)) {
                if (t.tile().isWithinDistance(player.tile(), 7)) {
                    damage = World.getWorld().random(1, 12);
                    t.hit(this, damage);
                    this.heal(damage);
                    t.graphic(2003);
                }
            }
        });
    }

    private void iceBarrage() {
        this.animate(9189);
        for (Entity t : getPossibleTargets(this)) {
            var tileDist = this.tile().distance(t.tile());
            int duration = (51 + -5 + (10 * tileDist));
            if (!inNexArea(t.tile())) continue;
            Projectile p = new Projectile(this, t, 362, 51, duration, 43, 31, 16, 15, 10);
            final int delay = this.executeProjectile(p);
            t.graphic(2005, GraphicHeight.LOW, p.getSpeed());
            new Hit(this, t, World.getWorld().random(0, 32), delay, CombatType.MAGIC).checkAccuracy(true).submit().postDamage(h -> {
                if (h.isAccurate() && !Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MAGIC)) {
                    int drain = t.getAsPlayer().getEquipment().hasAt(EquipSlot.SHIELD, SPECTRAL_SPIRIT_SHIELD) ? h.getDamage() / 3 : h.getDamage() / 2;
                    h.getTarget().skills().alterSkill(Skills.PRAYER, -drain);
                }
            });
        }
    }

    private void shadowSmash() {
        this.forceChat("Fear the Shadow!");
        this.animate(9186);
        final HashMap<String, int[]> tiles = new HashMap<>();
        for (Entity t : getPossibleTargets(this)) {
            if (!inNexArea(t.tile())) continue;
            String key = t.getX() + "_" + t.getY();
            if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
                tiles.put(key, new int[]{t.getX(), t.getY()});
            }
        }
        List<GameObject> shadows = new ArrayList<>();
        Chain.noCtx().runFn(1, () -> {
            for (int[] tile : tiles.values()) {
                shadows.add(new GameObject(42942, new Tile(tile[0], tile[1], 0), 0, 10, 0).spawn());
            }
        }).then(3, () -> {
            for (GameObject obj : shadows) {
                obj.remove();
            }
            shadows.clear();
            for (int[] tile : tiles.values()) {
                World.getWorld().sendClippedTileGraphic(383, new Tile(tile[0], tile[1], 0), 0, 0);
                for (Entity t : getPossibleTargets(this)) {
                    if (t.getX() == tile[0] && t.getY() == tile[1]) {
                        t.hit(this, World.getWorld().random(1, 50));
                    }
                }
            }
        });
    }

    private void shadowShots() {
        this.animate(9189);
        for (Entity t : getPossibleTargets(this)) {
            if (!inNexArea(t.tile())) continue;
            var tileDist = this.tile().distance(t.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(this, t, 378, 51, duration, 43, 35, 0, t.getSize(), 10);
           //// p.sendProjectile();


            final int delay = this.executeProjectile(p);
            int damage = 0;
            damage = getShadowDamage(t, damage);
            new Hit(this, t, World.getWorld().random(damage),delay, CombatType.RANGED).checkAccuracy(true).submit().postDamage(h -> {
                if (h.isAccurate())
                    t.skills().alterSkill(Skills.PRAYER, t.getAsPlayer().getEquipment().hasAt(EquipSlot.SHIELD, SPECTRAL_SPIRIT_SHIELD) ? -2 : -3);
            });
            t.graphic(379, GraphicHeight.LOW, p.getSpeed());
        }
    }

    private int getShadowDamage(Entity t, int damage) {
        if (t.tile().distance(this.tile()) <= 2) damage = 60;
        else if (t.tile().distance(this.tile()) <= 4) damage = 60 - 20;
        else if (t.tile().distance(this.tile()) > 6) damage = 60 - 30;
        if (Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES)) damage = damage / 2;
        return damage;
    }

    private void bloodBarrage() {
        for (var t : getPossibleTargets(this)) {
            var tileDist = this.tile().distance(t.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(this, t, 2002, 51, duration, 43, 0, 0, t.getSize(), 10);
            final int delay = (int) (p.getSpeed() / 30D);
            this.animate(9189);
            this.graphic(2001, GraphicHeight.HIGH, 0);
            if (!inNexArea(t.tile())) continue;
            new Hit(this, t, World.getWorld().random(0,32), delay, CombatType.MAGIC).checkAccuracy(true).submit();
            t.graphic(2001, GraphicHeight.MIDDLE, p.getSpeed());
        }
    }

    private void icePrison(Entity target) {

        this.forceChat("Die now, in a prison of ice!");
        this.animate(9186);

        target.freeze(5, this, true);

        Set<Tile> tiles = target.tile().expandedBounds(1);
        Chain.noCtx().runFn(4, () -> {
            for (Tile tile : tiles) {
                if (!tile.allowObjectPlacement()) {
                    continue;
                }
                if (MovementQueue.dumbReachable(tile.getX(), tile.getY(), this.tile())) {
                    this.stalagmites.add(new GameObject(42944, new Tile(tile.getX(), tile.getY(), tile.getZ()), 10, 0).spawn());
                }
            }

        }).then(7, () -> {
            for (Tile tile : tiles) {
                getPossibleTargets(this).forEach(t -> {
                    if (tile.isWithinDistance(t.tile(), 3)) {
                        CombatFactory.disableProtectionPrayers((Player) t);
                        //int damage = Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES) ? ICE_PRISON_SPECIAL_ATTACK_MAX / 2 : ICE_PRISON_SPECIAL_ATTACK_MAX;
                        Hit hit = t.hit(this, World.getWorld().random(0, 60), CombatType.MAGIC).submit();
                    }
                });
                break;
            }
            for (GameObject obj : this.stalagmites) {
                obj.remove();
            }
            this.stalagmites.clear();
        });
    }

    @Override
    public void combatSequence() {
        Entity target = Utils.randomElement(this.getPossibleTargets(this));
        if (target == null) return;
        if (!inNexArea(target.tile())) return;
        if (!introduction && !initiated) {
            entranceAnimation.run();
            return;
        }
        if (!introduction) return;
        double healthAmount = hp() * 1.0 / (maxHp() * 1.0);
        getStage(healthAmount);
        incrementCycle();
        if (intervalCount >= 5 && attackInterval <= 0 && !this.dead()) {
            resetCycle();
            sequenceAttacks(target);
        }
    }

    @Override
    public void die() {
        this.animate(9184);
        this.graphic(2013, GraphicHeight.LOW, 30);
        MutableObject<List<Tile>> list = new MutableObject<>();
        Chain.noCtx().runFn(1, () -> {
            list.setValue(Lists.newArrayList(
                new Tile(this.getX() + 1, this.getY() - 2, this.getZ()),
                new Tile(this.getX() - 1, this.getY() - 1, this.getZ()),
                new Tile(this.getX() + 3, this.getY() - 1, this.getZ()),
                new Tile(this.getX() + 3, this.getY() - 1, this.getZ()),
                new Tile(this.getX() - 1, this.getY() + 3, this.getZ()),
                new Tile(this.getX() - 1, this.getY() + 3, this.getZ()),
                new Tile(this.getX() + 1, this.getY() + 4, this.getZ())
            ));
            list.getValue().removeIf(t -> !ProjectileRoute.hasLineOfSight(this, t));
            list.getValue().removeIf(t -> !MovementQueue.dumbReachable(t.getX(), t.getY(), this.tile()));
            for (Tile tile : list.getValue()) {
                var projectile = new Projectile(this.getCentrePosition(), tile, 1, 2012, 100, 40, tile.getZ(), 0, 0);
                // projectile.sendProjectile

                World.getWorld().sendClippedTileGraphic(2014, tile, 0, projectile.getSpeed());
            }
        }).then(3, () -> {
            for (Player close : this.closePlayers(10)) {
                close.hit(this, World.getWorld().random(40));
            }
        }).then(4, () -> {
            ScalarLootTable table = ScalarLootTable.forNPC(11278);
            if (table != null) {
                Item reward = table.randomItem(World.getWorld().random());
                if (reward != null) {

                    for (var p : this.closePlayers(10)) {
                        BOSSES.log(p, 11278, reward);
                        GroundItemHandler.createGroundItem(new GroundItem(reward, this.tile, p));
                    }
                }
            }
            clear();
            this.remove();
        });
    }

    private void sequenceAttacks(Entity target) {
        this.setPositionToFace(target.tile());
        switch (this.stage) {
            case ONE -> {
                if (World.getWorld().rollDie(50, 1)) {
                    if (target.hasAttrib(AttributeKey.CHOKED)) {
                        smokeRush();
                        return;
                    }
                    choke();
                } else smokeRush();
            }
            case TWO -> {
                if (World.getWorld().rollDie(50, 1)) shadowSmash();
                else shadowShots();
            }
            case THREE -> {
                if (World.getWorld().rollDie(50, 1)) bloodSacrifice(target);
                if (World.getWorld().rollDie(75, 1)) bloodSiphon();
                else bloodBarrage();
            }
            case FOUR -> {
                if (World.getWorld().rollDie(35, 1)) icePrison(target);
                else iceBarrage();
            }
            case FIVE -> {
                if (World.getWorld().rollDie(35, 1)) icePrison(target);
                else iceBarrage();
            }
            case ZAROS -> zarosMagic();
        }
    }

    private void resetCycle() {
        intervalCount = 0;
        attackInterval = 5;
    }

    private void incrementCycle() {
        intervalCount++;
        attackInterval--;
    }

    private void clear() {
        this.stage = NexStage.NULL;
        this.intervalCount = 0;
        this.attackInterval = 5;
        this.initiated = false;
        this.introduction = false;
        this.soulsplit = false;
        this.turmoil = false;
        this.siphon = false;
        this.unlock = false;
        this.clearAttrib(AttributeKey.INVULNERABLE);
        if (fumus.getValue() != null) {
            fumus.getValue().remove();
        }
        if (umbra.getValue() != null) {
            umbra.getValue().remove();
        }
        if (cruor.getValue() != null) {
            cruor.getValue().remove();
        }
        if (glacies.getValue() != null) {
            glacies.getValue().remove();
        }
        if (!this.stalagmites.isEmpty()) {
            for (var o : stalagmites) {
                o.remove();
            }
            stalagmites.clear();
        }
        if (!this.bloodReavers.isEmpty()) {
            for (var n : bloodReavers) {
                n.die();
            }
            this.bloodReavers.clear();
        }
        new GameObject(42967, new Tile(2909, 5202, 0), 10, 1).spawn();
    }

    private void getStage(double healthAmount) {
        if (healthAmount <= 0.80 && !unlock && this.stage.equals(NexStage.ONE)) {
            this.unlock = true;
            this.forceChat("Fumus, don't fail me!");
            fumus.getValue().unlock();
            this.putAttrib(AttributeKey.INVULNERABLE, true);
        }
        if (fumus.getValue().dead() && this.stage.equals(NexStage.ONE)) {
            this.unlock = false;
            this.clearAttrib(AttributeKey.INVULNERABLE);
            this.forceChat("Darken my shadow!");
            this.stage = NexStage.TWO;
        }
        if (healthAmount <= 0.50 && !unlock && this.stage.equals(NexStage.TWO)) {
            this.unlock = true;
            this.putAttrib(AttributeKey.INVULNERABLE, true);
            this.forceChat("Umbra, don't fail me!");
            umbra.getValue().unlock();
        }
        if (umbra.getValue().dead() && this.stage.equals(NexStage.TWO)) {
            this.unlock = false;
            this.clearAttrib(AttributeKey.INVULNERABLE);
            this.stage = NexStage.THREE;
        }
        if (healthAmount <= 0.40 && !unlock && this.stage.equals(NexStage.THREE)) {
            this.unlock = true;
            this.putAttrib(AttributeKey.INVULNERABLE, true);
            this.forceChat("Cruor, don't fail me!");
            cruor.getValue().unlock();
        }
        if (cruor.getValue().dead() && this.stage.equals(NexStage.THREE)) {
            this.unlock = false;
            this.clearAttrib(AttributeKey.INVULNERABLE);
            this.forceChat("Flood my lungs with blood!");
            this.stage = NexStage.FOUR;
        }
        if (healthAmount <= 0.20 && !unlock && this.stage.equals(NexStage.FOUR)) {
            this.unlock = true;
            this.putAttrib(AttributeKey.INVULNERABLE, true);
            this.forceChat("Glacies, don't fail me!");
            glacies.getValue().unlock();
        }
        if (glacies.getValue().dead() && this.stage.equals(NexStage.FOUR)) {
            this.unlock = false;
            this.clearAttrib(AttributeKey.INVULNERABLE);
            this.forceChat("Infuse me with the power of ice!");
            this.stage = NexStage.FIVE;
        }
        if (healthAmount <= 0.10 && !unlock && this.stage.equals(NexStage.FIVE)) {
            this.unlock = true;
            this.heal(500);
            this.forceChat("NOW THE POWER OF ZAROS!");
            this.stage = NexStage.ZAROS;
        }
    }

    public ArrayList<Entity> getPossibleTargets(Entity mob) {
        if (inNexArea(mob.tile())) {
            return Arrays.stream(mob.closePlayers(64)).filter(NEX_AREA::contains).collect(Collectors.toCollection(ArrayList::new));
        }
        return null;
    }

    boolean inNexArea(Tile tile) {
        return tile.inArea(NEX_AREA);
    }

    Runnable entranceAnimation = () -> {
        this.initiated = true;
        this.forceChat("AT LAST!");
        this.animate(9182);
        Chain.noCtx().runFn(4, () -> {
            fumus.setValue(new NPC(11283, new Tile(2913, 5215, 0)).spawn(false));
            fumus.getValue().setPositionToFace(this.tile());
            fumus.getValue().lockNoDamage();
            this.setPositionToFace(fumus.getValue().tile());
            this.forceChat("Fumus!");
            this.animate(9189);
        }).then(1, () -> {
            int tileDist = fumus.getValue().tile().transform(3, 3).distance(this.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile projectile = new Projectile(fumus.getValue(), this, 2010, 30, duration, 18, 18, 0, 1, 5);
            // projectile.sendProjectile

        }).then(3, () -> {
            umbra.setValue(new NPC(11284, new Tile(2937, 5215, 0)).spawn(false));
            umbra.getValue().setPositionToFace(this.tile());
            umbra.getValue().lockNoDamage();
            this.setPositionToFace(umbra.getValue().tile());
            this.forceChat("Umbra!");
            this.animate(9189);
        }).then(1, () -> {
            int tileDist = umbra.getValue().tile().transform(3, 3).distance(this.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile projectile = new Projectile(umbra.getValue(), this, 2010, 30, duration, 18, 18, 0, 1, 5);
            // projectile.sendProjectile

        }).then(3, () -> {
            cruor.setValue(new NPC(11285, new Tile(2937, 5191, 0)).spawn(false));
            cruor.getValue().setPositionToFace(this.tile());
            cruor.getValue().lockNoDamage();
            this.setPositionToFace(cruor.getValue().tile());
            this.forceChat("Cruor!");
            this.animate(9189);
        }).then(1, () -> {
            int tileDist = cruor.getValue().tile().transform(3, 3).distance(this.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile projectile = new Projectile(cruor.getValue(), this, 2010, 30, duration, 18, 18, 0, 1, 5);
            // projectile.sendProjectile

        }).then(3, () -> {
            glacies.setValue(new NPC(11286, new Tile(2913, 5191, 0)).spawn(false));
            glacies.getValue().setPositionToFace(this.tile());
            glacies.getValue().lockNoDamage();
            this.setPositionToFace(glacies.getValue().tile());
            this.forceChat("Glacies!");
            this.animate(9189);
        }).then(1, () -> {
            int tileDist = glacies.getValue().tile().transform(3, 3).distance(this.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile projectile = new Projectile(glacies.getValue(), this, 2010, 30, duration, 18, 18, 0, 1, 5);
            // projectile.sendProjectile

        }).then(3, () -> {
            this.forceChat("Fill my soul with smoke!");
        }).then(1, () -> {
            this.introduction = true;
            this.transmog(11278, true);
            new GameObject(42941, new Tile(2909, 5202, 0), 10, 1).spawn(); // spawn red
        });
    };
}
