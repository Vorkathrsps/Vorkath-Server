package com.cryptic.model.content.raids.theatre.boss.verzik;

import com.cryptic.core.task.Task;
import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.verzik.nylocas.NylocasAthanatos;
import com.cryptic.model.content.raids.theatre.boss.verzik.nylocas.NylocasMatomenos;
import com.cryptic.model.content.raids.theatre.boss.verzik.phase.VerzikPhase;
import com.cryptic.model.content.raids.theatre.boss.verzik.tornado.Tornado;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.ClipUtils;
import com.cryptic.model.map.route.RouteMisc;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.VERZIKS_THRONE_32737;

public class Verzik extends NPC {
    @Getter
    TheatreInstance theatreInstance;
    @Getter
    @Setter
    private
    VerzikPhase phase;
    @Getter
    @Setter
    int walkCount = 0;
    @Getter
    @Setter
    int intervalCount = 0;
    @Getter
    @Setter
    int intervals = 0;
    @Getter
    @Setter
    int sequenceRandomIntervalTick = 0;
    int value = this.phase == VerzikPhase.ONE ? 12 : this.phase == VerzikPhase.TWO ? 4 : this.phase == VerzikPhase.THREE ? 7 : 0;
    @Getter
    @Setter
    int attackCount = 0;
    final int direction = Direction.SOUTH.toInteger();
    @Getter
    Tile destination = new Tile(3166, 4311);
    @Getter
    @Setter
    boolean pathing = false;
    @Getter
    @Setter
    boolean spawningMatomenos = false;
    @Getter
    @Setter
    boolean spawnedInitialNylo = false;
    @Getter
    @Setter
    boolean processedNylocasInitialSpawn = false;
    int meleeAttackCount = 0;
    @Getter
    @Setter
    boolean sendingChargedShot = false;
    @Getter
    @Setter
    boolean hasSentChargedShot = false;
    @Getter
    @Setter
    boolean adjustAttackSpeed = false;
    boolean webshot = false;
    boolean spewingWebs = false;
    Set<Tile> webTileSet = new HashSet<>();
    Set<Tile> activeTiles = new HashSet<>();

    public Verzik(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.spawnDirection(direction);
        this.setCombatMethod(null);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setPhase(VerzikPhase.ONE);
    }

    public void sendAthanatos() {
        if (!this.phase.equals(VerzikPhase.TWO)) return;
        Tile randomTile = World.getWorld().randomTileAround(this.tile, 8);
        if (randomTile == null) return;
        int tileDist = (int) this.tile.distanceTo(randomTile);
        int duration = 21 + 159 + tileDist;
        Tile verzikTile = this.tile.center(this.getSize());
        Projectile projectile = new Projectile(verzikTile, randomTile, 1586, 21, duration, 70, 1, 12, this.getSize(), 64, 0);
        int projectileDelay = projectile.send(this, randomTile);
        final MutableObject<NylocasAthanatos> nylocasMutableObject = new MutableObject<>();
        processNylocasAthanatos(randomTile, projectileDelay, nylocasMutableObject);
    }

    private void processNylocasAthanatos(Tile randomTile, int projectileDelay, MutableObject<NylocasAthanatos> nylocasMutableObject) {
        Chain
            .noCtx()
            .runFn(projectileDelay, () -> handleNylocasSpawn(randomTile, nylocasMutableObject))
            .then(5, () -> sendInitialProjectile(nylocasMutableObject))
            .repeatingTask(6, heal -> cycleHealTask(nylocasMutableObject, heal));
    }

    private void handleNylocasSpawn(@NotNull Tile randomTile, MutableObject<NylocasAthanatos> nylocasMutableObject) {
        Tile finalTile = new Tile(randomTile.getX(), randomTile.getY()).transform(0, 0, this.theatreInstance.getzLevel());

        for (Player player : this.theatreInstance.getPlayers()) {
            if (player.tile().equals(finalTile)) {
                player.hit(this, Utils.random(78));
            }
        }

        NylocasAthanatos nylocas = new NylocasAthanatos(8384, finalTile, this.theatreInstance);
        nylocasMutableObject.setValue(nylocas);
        setNpcInstance(nylocas);
        nylocas.noRetaliation(true);
        spawnNylocasNpc(nylocas);
        nylocas.face(this);
        nylocas.animate(8079);
        nylocas.graphic(1590);
        addToNylocasList(nylocas);
    }

    private void sendInitialNylocas(double healthPercentage) {
        if (healthPercentage <= 0.35 && !this.isSpawnedInitialNylo() && !this.isProcessedNylocasInitialSpawn()) {
            this.setProcessedNylocasInitialSpawn(true);
            this.setSpawnedInitialNylo(true);
        }
    }

    private void sendMatomenos() {
        if (!this.theatreInstance.getVerzikNylocasList().isEmpty()) return;
        Tile t = this.tile;
        Tile transformedTile = new Tile(t.getX(), t.getY(), theatreInstance.getzLevel());
        Tile leftTile = transformedTile.transform(5, 0);
        Tile rightTile = transformedTile.transform(-5, 0);
        NylocasMatomenos[] matomenosArray = new NylocasMatomenos[]{new NylocasMatomenos(8385, leftTile, theatreInstance), new NylocasMatomenos(8385, rightTile, theatreInstance)};
        handleNylocasMatomenosSpawn(matomenosArray);
        Chain
            .noCtx()
            .runFn(10, () -> this.setSpawningMatomenos(false))
            .then(35, this::handleNylocasMatomenosHeal);
    }

    private void handleNylocasMatomenosHeal() {
        List<NPC> list = this.theatreInstance.getVerzikNylocasList();
        if (list.isEmpty()) return;
        for (var n : list) {
            if (n == null) return;
            if (n.dead()) return;
            this.healHit(n, n.hp());
            n.die();
        }
        list.clear();
    }

    private void handleNylocasMatomenosSpawn(NPC[] matomenosArray) {
        this.setSpawningMatomenos(true);
        this.animate(-1);
        this.animate(8117);
        Chain
            .noCtx()
            .runFn(2, () -> {
                for (NPC nylocas : matomenosArray) {
                    if (this.theatreInstance.getPlayers().size() <= 2) {
                        addToNylocasList(nylocas);
                        setNpcInstance(nylocas);
                        spawnNylocasNpc(nylocas);
                        nylocas.animate(8098);
                        nylocas.noRetaliation(true);
                        break;
                    }
                    addToNylocasList(nylocas);
                    setNpcInstance(nylocas);
                    spawnNylocasNpc(nylocas);
                    nylocas.animate(8098);
                    nylocas.noRetaliation(true);
                }
            });
    }

    private boolean isInitialSpawn() {
        if (this.isSpawnedInitialNylo()) {
            this.setSpawnedInitialNylo(false);
            sendMatomenos();
            return true;
        }
        return false;
    }

    private boolean isAthanatosOrMatomenos(double healthPercentage) {
        if (healthPercentage <= 0.35 || this.isProcessedNylocasInitialSpawn()) {
            sendMatomenos();
            return true;
        }
        sendAthanatos();
        return true;
    }

    private boolean sendSpawnNylocas() {
        if (!this.phase.equals(VerzikPhase.TWO)) return false;
        if (isInitialSpawn()) return true;
        if (this.getSequenceRandomIntervalTick() >= 6) {
            this.setSequenceRandomIntervalTick(0);
            if (this.theatreInstance.getVerzikNylocasList().isEmpty()) {
                double healthPercentage = (double) hp() / maxHp();
                return isAthanatosOrMatomenos(healthPercentage);
            }
        }
        return false;
    }

    private void sendPhaseTwoAttacks() {
        if (!this.phase.equals(VerzikPhase.TWO)) return;
        for (Player player : this.theatreInstance.getPlayers()) {
            if (player == null) continue;
            if (isInMeleeRange(player)) break;
            if (this.getAttackCount() <= 4) {
                sendToxicBlast(player);
                return;
            }
            sendElectricShock();
        }
    }

    public void sendToxicBlast(Player player) {
        boolean lineOfSight = ProjectileRoute.hasLineOfSight(this, player.tile());
        if (!lineOfSight) return;
        if (!this.phase.equals(VerzikPhase.TWO)) return;
        int tileDist = this.tile.distance(player.getCentrePosition());
        int duration = 21 + 39 + (tileDist);
        Tile verzikTile = this.tile.center(this.getSize());
        Tile playerTile = player.getCentrePosition();
        Projectile projectile = new Projectile(verzikTile, playerTile, 1583, 21, duration, 70, 0, 12, this.getSize(), 128, 0);
        int delay = (int) (projectile.getSpeed() / 30D);
        projectile.send(this, playerTile);
        runToxicBlastTask(player, projectile, delay);
        World.getWorld().tileGraphic(1584, player.tile(), 0, projectile.getSpeed());
    }

    public void sendKnockBack(Player p) {
        if (!this.phase.equals(VerzikPhase.TWO)) return;
        int vecX = (p.getAbsX() - Utils.getClosestX(this, p.tile()));
        if (vecX != 0)
            vecX /= Math.abs(vecX);
        int vecY = (p.getAbsY() - Utils.getClosestY(this, p.tile()));
        if (vecY != 0)
            vecY /= Math.abs(vecY);
        int endX = p.getAbsX();
        int endY = p.getAbsY();
        for (int i = 0; i < 4; i++) {
            if (DumbRoute.getDirection(endX, endY, this.theatreInstance.getzLevel(), p.getSize(), endX + vecX, endY + vecY) == null) {
                break;
            }
            endX += vecX;
            endY += vecY;
        }
        Direction direction = getDirection(vecX, vecY);
        if (endX != p.getAbsX() || endY != p.getAbsY()) {
            sendForceMovement(p, endX, endY, direction);
        }
    }


    public void handlePhaseTwoMagicAttack() {
        Player target = this.theatreInstance.getRandomTarget();
        int tileDist = this.tile.distance(target.tile());
        int duration = (20 + (10 * tileDist));
        Projectile projectile = new Projectile(this, target, 1591, 21, duration, 70, 24, 8, this.getSize(), 128, 0);
        int delay = projectile.send(this, target);
        Hit hit = Hit.builder(this, target, CombatFactory.calcDamageFromType(this, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
        int damage = hit.getDamage();
        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
            hit.setDamage(0);
        }
        if (target.getEquipment().hasAt(EquipSlot.FEET, ItemIdentifiers.INSULATED_BOOTS)) {
            hit.setDamage((int) (damage * 0.5));
        }
        hit.submit();
    }

    private void sendSphere(@NotNull Player player, int duration) {
        Entity target = player;
        boolean lineOfSight = ProjectileRoute.hasLineOfSight(this, player.tile());
        target = getEntity(player, target, lineOfSight);
        Projectile projectile = new Projectile(this, target, 1580, 20, duration, 100, 25, 20, 0, 5, 10);
        int delay = this.executeProjectile(projectile);
        boolean isUsingPrayer = Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC);
        int damage = Utils.random(10, 137);
        Hit hit = Hit.builder(this, target, isUsingPrayer ? (int) (damage * .50) : damage, delay, CombatType.MAGIC).setAccurate(true);
        hit.submit();
        target.graphic(1582, GraphicHeight.LOW, projectile.getSpeed());
    }

    public void handleElectricShock() {
        if (!this.phase.equals(VerzikPhase.TWO)) return;
        this.setAttackCount(0);
        Player target = this.theatreInstance.getRandomTarget();
        Tile tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        int tileDist = this.tile.distance(target.tile());
        int duration = (21 + 39 + (5 * tileDist));
        Projectile projectile = new Projectile(this, target, 1585, 21, duration, 70, 24, 12, this.getSize(), 128, 5);
        int delay = (int) (projectile.getSpeed() / 30D);
        projectile.send(this, target);
        Hit hit = Hit.builder(this, target, CombatFactory.calcDamageFromType(this, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
        int damage = hit.getDamage();
        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
            hit.setDamage(0);
        }
        if (target.getEquipment().hasAt(EquipSlot.FEET, ItemIdentifiers.INSULATED_BOOTS)) {
            hit.setDamage((int) (damage * 0.5));
        }
        hit.submit();
    }

    private void runToxicBlastTask(Player player, Projectile projectile, int delay) {
        Chain.bound(this).name("VerzikToxicBlastTask").runFn(delay, () -> {
            if (player.tile().equals(projectile.getEnd())) {
                int damage = Utils.random(1, 47);
                if (Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MISSILES)) {
                    player.hit(this, (int) (damage * 0.5));
                    return;
                }
                player.hit(this, damage);
            }
        });
    }

    public void sendPhaseOne() {
        if (!this.phase.equals(VerzikPhase.ONE)) return;
        this.face(null);
        this.animate(8109);
        List<Player> players = this.theatreInstance.getPlayers();
        for (var player : players) {
            if (player == null) continue;
            var position = player.tile();
            var tileDist = this.tile.distance(position);
            int duration = (20 + (10 * tileDist));
            Chain.noCtx().delay(2, () -> sendSphere(player, duration));
        }
    }

    public void sendPhaseTwo() {
        if (!this.phase.equals(VerzikPhase.TWO)) return;
        this.attackCount++;
        Player target = this.theatreInstance.getRandomTarget();
        Tile tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        sendPhaseTwoAttacks();
    }

    private void sendMagicPhaseThree() {
        this.animate(8124);
        Player target = this.theatreInstance.getRandomTarget();
        this.getCombat().setTarget(target);
        for (var p : this.theatreInstance.getPlayers()) {
            if (p == null) continue;
            var tileDist = this.tile.distance(p.tile());
            int duration = (53 + 45 + (7 * tileDist));
            Projectile projectile = new Projectile(this, p, 1594, 53, duration, 100, 25, 20, this.getSize(), 100, 7);
            int delay = (int) (projectile.getSpeed() / 30D);
            projectile.send(this, p);
            Hit hit = Hit.builder(this, target, Utils.random(0, 33), delay, CombatType.MAGIC).checkAccuracy(true);
            hit.submit();
            if (Prayers.usingPrayer(p, Prayers.PROTECT_FROM_MAGIC)) {
                hit.block();
            }
            p.graphic(1581, GraphicHeight.LOW, projectile.getSpeed());
        }
    }

    private void sendMeleePhaseThree(Player target) {
        this.animate(8123);
        Hit hit = Hit.builder(this, target, Utils.random(0, 63), 3, CombatType.MELEE).checkAccuracy(true);
        hit.submit();
        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MELEE)) {
            hit.block();
        }
    }

    private void sendRangePhaseThree() {
        this.animate(8125);
        Player target = this.theatreInstance.getRandomTarget();
        this.getCombat().setTarget(target);
        for (var p : this.theatreInstance.getPlayers()) {
            if (p == null) continue;
            var tileDist = this.tile.distance(p.tile());
            int duration = (62 + 45 + (7 * tileDist));
            Projectile projectile = new Projectile(this, p, 1593, 62, duration, 100, 25, 20, this.getSize(), 100, 7);
            int delay = (int) (projectile.getSpeed() / 30D);
            projectile.send(this, p);
            Hit hit = Hit.builder(this, p, Utils.random(0, 33), delay, CombatType.RANGED).checkAccuracy(true);
            hit.submit();
            if (Prayers.usingPrayer(p, Prayers.PROTECT_FROM_MISSILES)) {
                hit.block();
            }
        }
    }

    private void sendChargedShot() {
        this.animate(8126);
        this.lockMovement();
        MutableObject<Tile> randomTile = new MutableObject<>();
        findValidTile(randomTile);
        for (Player p : this.theatreInstance.getPlayers()) {
            if (p == null) continue;
            var tileDist = this.tile.distance(p.tile());
            int duration = (261 + 144 + (tileDist));
            Projectile projectile = new Projectile(this, p, 1596, 261, duration, 250, 30, 50, this.getSize(), 0, 0);
            int delay = this.executeProjectile(projectile);
            Chain.noCtx().runFn((int) (projectile.getSpeed() / 30D + 1), () -> {
                if (!p.tile().equals(getTileValue(randomTile))) {
                    int damage = Utils.random(1, 80);
                    Hit hit = Hit.builder(this, p, damage, delay, CombatType.MAGIC).setAccurate(true);
                    hit.submit();
                } else {
                    p.graphic(1597, GraphicHeight.LOW, 0);
                }
                this.setSendingChargedShot(false);
            }).then(1, () -> {
                this.unlock();
                sequenceTornado();
            });
        }
    }

    private void findValidTile(MutableObject<Tile> randomTile) {
        for (int index = 0; index < this.theatreInstance.getPlayers().size(); index++) {
            Tile randomTileAround = World.getWorld().randomTileAround(this.tile, 10);
            randomTile.setValue(randomTileAround);
            if (RegionManager.blocked(getTileValue(randomTile))) continue;
            World.getWorld().tileGraphic(1595, getTileValue(randomTile), 0, 0);
        }
    }

    private void sequenceTornado() {
        for (var p : this.theatreInstance.getPlayers()) {
            if (p == null) continue;
            Tornado tornado = new Tornado(8386, p.tile().transform(-1, 0), this.theatreInstance);
            tornado.setInstancedArea(this.theatreInstance);
            tornado.spawn(false);
            tornado.getCombat().setTarget(p);
            this.theatreInstance.getTornadoList().add(tornado);
        }
    }

    private void sendSequences() {
        if (this.getIntervalCount() >= attackSpeed() && this.getIntervals() <= 0 && !this.dead()) {
            this.setIntervalCount(0);
            this.setIntervals(value);
            switch (this.phase) {
                case ONE -> sendPhaseOne();
                case TWO -> {
                    if (isSpawningMatomenos()) return;
                    sendPhaseTwo();
                }
                case THREE -> {
                    if (this.isSendingChargedShot()) return;
                    if (this.spewingWebs) return;
                    double healthPercentage = (double) hp() / maxHp();
                    if (World.getWorld().rollDie(30, 1) && healthPercentage >= 30.0D) this.sendWebShot();
                    sequenceThirdPhase();
                }
            }
        }
    }

    private int attackSpeed() {
        return this.phase == VerzikPhase.ONE ? 12 : this.phase == VerzikPhase.TWO ? 4 : this.phase == VerzikPhase.THREE && !this.isAdjustAttackSpeed() ? 7 : 5;
    }

    private void sequenceThirdPhase() {
        meleeAttackCount++;
        if (meleeAttackCount >= 4) {
            meleeAttackCount = 0;
            Player target = validate();
            if (target == null) return;
            sendMeleePhaseThree(target);
            return;
        }
        var healthPercentage = (double) hp() / maxHp();
        var random = World.getWorld().random(1, 2);
       /* if (healthPercentage <= 0.20 && !this.isHasSentChargedShot()) {
            this.forceChat("I'm not finished with you just yet!");
            this.sendChargedShot();
            this.setAdjustAttackSpeed(true);
            this.setSendingChargedShot(true);
            this.setHasSentChargedShot(true);
            return;
        } */

        if (random == 1) {
            sendMagicPhaseThree();
            return;
        }
        if (random == 2) {
            sendRangePhaseThree();
        }
    }

    @Nullable
    private Player validate() {
        Player target = this.theatreInstance.getRandomTarget();
        this.getCombat().setTarget(target);
        if (!DumbRoute.withinDistance(this, target, 1)) {
            return null;
        }
        return target;
    }

    Set<Tile> clippedTiles = new HashSet<>();
    List<GameObject> webObjects = new ArrayList<>();

    public void setClippedTiles() {
        this.getCentrePosition().area(3).forEachPos(t -> {
            ClipUtils.addClipping(t.getX(), t.getY(), t.getZ(), 1, 1, true, false);
            this.clippedTiles.add(t);
        });
        for (Player player : this.theatreInstance.getPlayers()) {
            if (player == null) continue;
            for (Tile tile : this.clippedTiles) {
                if (tile == null) continue;
                if (player.tile().equals(tile)) {
                    player.lock();
                    Direction direction = Direction.SOUTH;
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x, direction.y - 5), 30, 60, 1114, 0);
                    player.setForceMovement(forceMovement);
                    Chain.noCtx().runFn(2, player::unlock);
                }
            }
        }
    }

    public void removeClippedTiles() {
        for (Tile tile : this.clippedTiles) {
            if (tile == null) continue;
            ClipUtils.removeClipping(tile.getX(), tile.getY(), tile.getZ(), 1, 1, true, false);
        }
        this.clippedTiles.clear();
    }

    public void addWebs(GameObject object) {
        if (object == null) return;
        webObjects.add(object);
    }

    public void removeWebs() {
        for (GameObject web : this.webObjects) {
            if (web == null) continue;
            web.remove();
        }
        this.webObjects.clear();
    }

    private void sendWebShot() {
        this.animateAndTransmog(-1, 8373);
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.webshot = true;
        this.waitForTile(destination.transform(-1, -2), () -> Chain.noCtx().runFn(2, () -> {
            this.setPhase(VerzikPhase.THREE);
            this.animateAndTransmog(-1, 8374);
            this.setClippedTiles();
            this.animate(8127);
            this.lockMoveDamageOk();
            this.spewingWebs = true;
            Tile centerPosition = this.tile.center(7);
            AtomicInteger count = new AtomicInteger(14);
            MutableObject<Player> playerObject = new MutableObject<>();
            Player randomTarget = this.theatreInstance.getRandomTarget();
            Chain.noCtx().repeatingTask(1, targeting -> {
                playerObject.setValue(randomTarget);
                Player target = getTargetValue(playerObject);
                Direction direction = Direction.diagonal(this.getCentrePosition().getDeltaX(target.getCentrePosition()), this.getCentrePosition().getDeltaY(target.getCentrePosition()));
                if (direction == null) return;
                this.setPositionToFace(target.tile().transform(direction.x, direction.y));
                if (count.get() <= 0 || this.dead() || !this.isRegistered()) {
                    targeting.stop();
                }
            });
            Chain.noCtx().repeatingTask(2, projectiles -> {
                if (count.get() <= 0 || this.dead() || !this.isRegistered()) {
                    for (Player player : this.theatreInstance.getPlayers()) {
                        if (player == null) continue;
                        player.clearAttrib(AttributeKey.STUCK_ON_WEB);
                    }
                    count.getAndSet(0);
                    this.webshot = false;
                    this.spewingWebs = false;
                    this.removeClippedTiles();
                    this.removeWebs();
                    this.unlock();
                    this.animate(Animation.DEFAULT_RESET_ANIMATION);
                    this.webTileSet.clear();
                    projectiles.stop();
                    return;
                }
                this.webTileSet.clear();
                this.face(null);
                Player target = getTargetValue(playerObject);
                if (target == null) return;
                Tile copy = target.tile().copy();
                double distance = centerPosition.distanceTo(copy);
                int duration = (int) (8 + (82 + distance));
                List<GameObject> temp = new ArrayList<>(this.webObjects);
                this.webTileSet.add(copy);
                for (int index = 0; index < World.getWorld().random(1, 2); index++) {
                    Tile randomTile = World.getWorld().randomTileAround(copy, 3);
                    if (randomTile == null) continue;
                    this.webTileSet.add(randomTile);
                }
                Projectile projectile = new Projectile(centerPosition, copy, 1601, 8, duration, 87, 0, 15, this.getSize(), 0, 0);
                for (Tile t : this.webTileSet) {
                    if (t == null) continue;
                    if (t.getZ() != this.theatreInstance.getzLevel()) continue;
                    projectile.send(centerPosition, t);
                    GameObject web = new GameObject(32734, t);
                    if (!web.tile().allowObjectPlacement()) continue;
                    this.addWebs(web);
                }
                Chain.noCtx().cancelWhen(projectiles::isStopped).runFn(projectile.getSpeed() / 30 - 2, () -> {
                    for (GameObject o : temp) {
                        if (o == null) continue;
                        if (o.getZ() != this.theatreInstance.getzLevel()) continue;
                        if (!o.tile().allowObjectPlacement()) continue;
                        o.spawn();
                    }
                    for (GameObject o : this.webObjects) {
                        if (o == null) continue;
                        if (o.getZ() != this.theatreInstance.getzLevel()) continue;
                        if (!o.tile().allowObjectPlacement()) continue;
                        this.activeTiles.add(o.tile());
                    }
                    temp.clear();
                });
                this.webTileSet.clear();
                count.getAndDecrement();
            });
            Chain.noCtx().repeatingTask(2, tileTask -> {
                if (count.get() <= 0 || this.dead() || !this.isRegistered()) {
                    this.activeTiles.clear();
                    tileTask.stop();
                    return;
                }
                for (Player player : this.theatreInstance.getPlayers()) { //TODO
                    if (player == null) continue;
                    for (Tile tile : this.activeTiles) {
                        if (tile == null) continue;
                        /*if (player.tile().equals(tile) && !player.hasAttrib(AttributeKey.STUCK_ON_WEB)) {
                            player.putAttrib(AttributeKey.STUCK_ON_WEB, true);
                            this.submitAccurateHit(player, 0, Utils.random(10, 20), null);
                            break;
                        }*/
                    }
                }
            });
        }));
    }

    private static Tile getTileValue(MutableObject<Tile> clonedTile) {
        return clonedTile.getValue();
    }

    private static Player getTargetValue(MutableObject<Player> target) {
        return target.getValue();
    }

    private void updateFaceTile(Tile face) {
        if (this.getFaceTile() != null) {
            Tile faceTile = new Tile(face.getX(), face.getY());
            this.setFaceTile(faceTile);

            if (this.getSize() > 1) {
                faceTile = this.tile().transform(this.getSize() / 2, this.getSize() / 2, 0);
            }

            this.setPositionToFace(faceTile);
        }
    }

    public void interpolateSteps() {
        this.face(null);
        if (!this.isPathing()) this.setPathing(true);
        for (int index = 0; index < 2; index++) {
            Tile currentTile = this.tile;
            Tile dst = destination.transform(-1, -2);
            int deltaX = dst.getX() - currentTile.getX();
            int deltaY = dst.getY() - currentTile.getY();
            int nextStepDeltaX = Integer.compare(deltaX, 0);
            int nextStepDeltaY = Integer.compare(deltaY, 0);
            int nextX = currentTile.getX() + nextStepDeltaX;
            int nextY = currentTile.getY() + nextStepDeltaY;
            this.queueTeleportJump(new Tile(nextX, nextY, this.theatreInstance.getzLevel()));
            if (nextStepDeltaX == 0 && nextStepDeltaY == 0) {
                this.setPathing(false);
                Tile faceTile = this.tile.center(7).transform(Direction.SOUTH.x, Direction.SOUTH.y);
                this.updateFaceTile(faceTile);
            }
        }
    }


    @Override
    public boolean beforeAttack() {
        if (this.transitionWebShot() || this.spewingWebs) {
            this.intervalCount = 0;
            this.intervals = value;
            return true;
        }
        if (this.phase.equals(VerzikPhase.TWO)) {
            double healthPercentage = (double) hp() / maxHp();
            sendInitialNylocas(healthPercentage);
        }
        if (this.sendSpawnNylocas()) return true;
        return false;
    }

    @Override
    public void combatSequence() {
        if (this.id() == VERZIK_VITUR_8369) return;
        if (transitionBetweenPhase()) return;
        if (this.getTimers().left(TimerKey.COMBAT_ATTACK) != 0) {
            this.getCombat().setTarget(null);
            this.setFaceTile(null);
            return;
        }
        this.intervalCount++;
        this.intervals--;
        sendSequences();
    }

    @Override
    public void die() {
        switch (this.phase) {
            case ONE -> transitionPhaseOne();
            case TWO -> transitionPhaseTwo();
            case THREE -> transitionPhaseThree();
        }
    }

    public void transitionPhaseOne() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.animate(8111);
        List<GameObject> pillarObjects = this.theatreInstance.getVerzikPillarObjects();
        List<NPC> pillarNpcs = this.theatreInstance.getVerzikPillarNpcs();
        replaceObjects();
        clearLists(pillarObjects, pillarNpcs);
        Direction direction = Direction.SOUTH;
        Chain
            .noCtx()
            .delay(4, () -> transitionAndReplace(direction))
            .then(2, this::animateAndSetPath);
    }

    public void transitionPhaseTwo() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.canAttack(false);
        this.animate(8118);
        clearAthanatosList();
        Chain
            .noCtx()
            .runFn(2, () -> animateAndTransmog(8119, 8373))
            .then(4, this::finalizePhaseTwo);
    }

    private void clearAthanatosList() {
        if (!this.theatreInstance.getVerzikNylocasList().isEmpty()) {
            Iterator<NPC> iterator = this.theatreInstance.getVerzikNylocasList().iterator();
            while (iterator.hasNext()) {
                NPC npc = iterator.next();
                if (npc != null) {
                    npc.die();
                    iterator.remove();
                }
            }
        }
    }

    public void transitionPhaseThree() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.animate(8128);
        this.setAdjustAttackSpeed(false);
        for (var npc : this.theatreInstance.getTornadoList()) {
            if (npc == null) continue;
            npc.remove();
        }
        this.theatreInstance.getTornadoList().clear();
        Chain
            .noCtx()
            .delay(2, () -> animateAndTransmog(-1, 8375))
            .then(6, this::animateThrone)
            .then(4, this::openTreasureRoom);
    }

    private void setNpcInstance(NPC nylocas) {
        nylocas.setInstancedArea(theatreInstance);
    }

    private void spawnNylocasNpc(NPC nylocas) {
        nylocas.spawn(false);
    }

    private void addToNylocasList(NPC nylocas) {
        this.theatreInstance.getVerzikNylocasList().add(nylocas);
    }

    private void animateThrone() {
        GameObject throne = this.theatreInstance.throne.getValue();
        throne.animate(8108);
        this.remove();
    }

    private void openTreasureRoom() {
        GameObject throne = this.theatreInstance.throne.getValue();
        GameObject throne_two = new GameObject(TREASURE_ROOM, new Tile(throne.getX(), throne.getY(), this.theatreInstance.getzLevel()), 10, 0);
        throne.setId(throne_two.getId());
        this.setPhase(VerzikPhase.DEAD);
        this.theatreInstance.spawnTreasure();
    }

    private void animateAndTransmog(int animation, int id) {
        this.animate(animation);
        this.transmog(id, true);
        this.setInstancedArea(this.theatreInstance);
    }

    private void checkForceMovement(GameObject o) {
        for (Player player : this.theatreInstance.getPlayers()) {
            if (player == null) continue;
            if (player.tile().isWithinDistance(this.tile, 1) || o.tile().isWithinDistance(player.tile(), 1)) {
                this.forceMove(player);
            }
        }
    }

    private void forceMove(@NotNull Player player) {
        player.hit(this, 10);
        Direction direction = Direction.SOUTH;
        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x(), direction.y()), 30, 60, 1114, 0);
        player.setForceMovement(forceMovement);
    }

    public void interpolatePhaseTwoTransition() {
        for (int index = 0; index < 2; index++) {
            Tile currentTile = this.tile;
            Tile dst = destination;
            int deltaX = dst.getX() - currentTile.getX();
            int deltaY = dst.getY() - currentTile.getY();
            int nextStepDeltaX = Integer.compare(deltaX, 0);
            int nextStepDeltaY = Integer.compare(deltaY, 0);
            int nextX = currentTile.getX() + nextStepDeltaX;
            int nextY = currentTile.getY() + nextStepDeltaY;
            this.queueTeleportJump(new Tile(nextX, nextY, this.theatreInstance.getzLevel()));
            if (nextStepDeltaX == 0 && nextStepDeltaY == 0) {
                Chain.noCtx().runFn(3, this::finalizeTransmog);
            }
        }
    }

    private void finalizeTransmog() {
        this.queueTeleportJump(this.getDestination().transform(1, 1, this.theatreInstance.getzLevel()));
        this.transmog(8372, true);
        this.heal(this.maxHp());
        this.setPathing(false);
        this.getCombat().delayAttack(1);
        this.setPhase(VerzikPhase.TWO);
    }

    @NotNull
    private Direction getDirection(int vecX, int vecY) {
        Direction direction;
        if (vecX == -1) {
            direction = Direction.EAST;
        } else if (vecX == 1) {
            direction = Direction.WEST;
        } else if (vecY == -1) {
            direction = Direction.NORTH;
        } else {
            direction = Direction.SOUTH;
        }
        return direction;
    }

    private void sendForceMovement(@NotNull Player player, int endX, int endY, Direction direction) {
        int diffX = endX - player.getAbsX();
        int diffY = endY - player.getAbsY();
        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(diffX, diffY), 30, 60, 1157, direction);
        player.setForceMovement(forceMovement);
        if (!player.tile().equals(this.getCentrePosition())) {
            player.hit(this, Utils.random(0, 60));
        } else {
            player.hit(this, 70);
        }
        player.stun(8);
    }

    private void cycleHealTask(@NotNull MutableObject<NylocasAthanatos> nylocasMutableObject, Task heal) {
        NylocasAthanatos nylocas = nylocasMutableObject.getValue();
        int distance = (int) nylocas.tile().distanceTo(this.tile);
        int duration = 3 + 21 + distance;
        if (stopHealingTask(nylocasMutableObject, heal, duration)) return;
        Projectile nyloProjectile = new Projectile(nylocas, this, 1587, 3, duration, 5, 70, 2, this.getSize(), 0, 0);
        int nyloDelay = nylocas.executeProjectile(nyloProjectile);
        this.healHit(nylocas, Utils.random(9, 10), nyloDelay);
    }

    private boolean stopHealingTask(MutableObject<NylocasAthanatos> nylocasMutableObject, Task heal, int dur) {
        NylocasAthanatos nylocas = nylocasMutableObject.getValue();
        if (this.theatreInstance == null) {
            nylocas.remove();
            heal.stop();
            return true;
        }

        if (this.id() != 8371) {
            nylocas.die();
            heal.stop();
            return true;
        }

        if (nylocas.dead()) {
            Projectile nyloProjectile = new Projectile(nylocas, this, 1588, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
            int nyloDelay = nylocas.executeProjectile(nyloProjectile);
            this.hit(nylocas, 70, nyloDelay);
            heal.stop();
            return true;
        }

        return false;
    }

    private void sendInitialProjectile(@NotNull MutableObject<NylocasAthanatos> nylocasMutableObject) {
        NylocasAthanatos nylocas = nylocasMutableObject.getValue();
        int dist = (int) nylocas.tile().distanceTo(this.tile);
        int dur = 3 + 21 + dist;

        if (nylocas.dead()) {
            Projectile nyloProjectile = new Projectile(nylocas, this, 1588, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
            int nyloDelay = nylocas.executeProjectile(nyloProjectile);
            this.hit(nylocas, 75, nyloDelay);
            return;
        }

        Projectile nyloProjectile = new Projectile(nylocas, this, 1587, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
        int nyloDelay = nylocas.executeProjectile(nyloProjectile);
        this.healHit(nylocas, 50, nyloDelay);
    }

    private Entity getEntity(Player player, Entity target, boolean lineOfSight) {
        for (var npc : this.theatreInstance.getVerzikPillarNpcs()) {
            Tile playerSwTile = player.getCentrePosition().getSouthwestTile(npc);
            Tile pillarSwTile = npc.getCentrePosition().getSouthwestTile(player);
            if (player.tile().getX() <= 3168) {
                if (playerSwTile.isWithinDistance(pillarSwTile, 2) && !lineOfSight) {
                    if (pillarSwTile.isWithinDistance(playerSwTile, 1)) {
                        target = npc;
                        break;
                    }
                }
            } else {
                Tile playerSeTile = player.getCentrePosition().getSouthEastTile(npc);
                Tile pillarSeTile = npc.getCentrePosition().getSouthEastTile(player);
                if (playerSeTile.isWithinDistance(pillarSeTile, 2) && !lineOfSight) {
                    if (pillarSwTile.isWithinDistance(playerSeTile, 1)) {
                        target = npc;
                        break;
                    }
                }
            }
        }
        return target;
    }

    private void finalizePhaseTwo() {
        this.animateAndTransmog(-1, 8374);
        this.heal(this.maxHp());
        this.setIgnoreOccupiedTiles(true);
        this.forceChat("Behold my true nature!");
        this.queueTeleportJump(this.getDestination().transform(-1, -1, this.theatreInstance.getzLevel()));
        this.canAttack(true);
        this.setPhase(VerzikPhase.THREE);
    }

    private void animateAndSetPath() {
        this.animate(-1);
        this.setPathing(true);
    }

    private void transitionAndReplace(Direction direction) {
        this.theatreInstance.throne.setValue(new GameObject(VERZIKS_THRONE_32737, new Tile(3167, 4324, this.theatreInstance.getzLevel()), 10, 0));
        GameObject throne = this.theatreInstance.throne.getValue();
        throne.spawn();
        this.theatreInstance.getTreasureSpawns().add(throne);
        animateAndTransmog(8112, 8371);
        this.setPositionToFace(this.getDestination().center(5).tileToDir(direction));
    }

    private void clearLists(@NotNull List<GameObject> pillarObjects, @NotNull List<NPC> pillarNpcs) {
        pillarObjects.clear();
        pillarNpcs.forEach(NPC::remove);
        pillarNpcs.clear();
    }

    private void replaceObjects() {
        for (NPC npc : this.theatreInstance.getVerzikPillarNpcs()) {
            if (npc == null) continue;
            MapObjects.get(32687, npc.tile())
                .ifPresent(pillar -> {
                    pillar.setId(32688);
                    Chain.noCtx().delay(2, () -> {
                        pillar.setId(32689);
                        checkForceMovement(pillar);
                    }).then(1, () -> pillar.animate(8104)).then(2, pillar::remove);
                });
        }
    }

    private void sendElectricShock() {
        this.sequenceRandomIntervalTick++;
        this.setAttackCount(0);
        if (this.isProcessedNylocasInitialSpawn()) {
            handlePhaseTwoMagicAttack();
            return;
        }
        handleElectricShock();
    }

    private boolean isInMeleeRange(Player player) {
        if (RouteMisc.getEffectiveDistance(player, this) <= 1) {
            sendKnockBack(player);
            return true;
        }
        return false;
    }

    private boolean transitionWebShot() {
        if (this.id() == VERZIK_VITUR_8373 && this.webshot && this.phase.equals(VerzikPhase.TRANSITIONING)) {
            this.walkCount++;
            if (this.getWalkCount() >= 1) {
                this.setWalkCount(0);
                interpolateSteps();
            }
            return true;
        }
        return false;
    }

    private boolean transitionBetweenPhase() {
        if (this.id() == VERZIK_VITUR_8371 && this.isPathing()) {
            this.walkCount++;
            if (this.getWalkCount() >= 1) {
                this.setWalkCount(0);
                interpolatePhaseTwoTransition();
            }
            return true;
        }
        return this.phase.equals(VerzikPhase.TRANSITIONING);
    }
}
