package com.cryptic.model.content.raids.theatre.boss.verzik;

import com.cryptic.core.task.Task;
import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.verzik.nylocas.NylocasAthanatos;
import com.cryptic.model.content.raids.theatre.boss.verzik.nylocas.NylocasMatomenos;
import com.cryptic.model.content.raids.theatre.boss.verzik.phase.VerzikPhase;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.RouteMisc;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.VERZIK_VITUR_8369;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.VERZIK_VITUR_8371;
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
    int value = this.getPhase() == VerzikPhase.ONE ? 12 : this.getPhase() == VerzikPhase.TWO ? 4 : this.getPhase() == VerzikPhase.THREE ? 7 : 0;
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
    GameObject throne;
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
    boolean adjustAttackSpeed = false;
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
        if (!this.getPhase().equals(VerzikPhase.TWO)) return;
        Tile randomTile = World.getWorld().randomTileAround(this.tile, 8);
        if (randomTile == null) return;
        int tileDist = (int) this.tile().distanceTo(randomTile);
        int duration = 21 + 159 + tileDist;
        Tile verzikTile = this.tile().center(this.getSize());
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
        Tile finalTile = new Tile(randomTile.getX(), randomTile.getY()).transform(0, 0, this.getTheatreInstance().getzLevel());

        for (Player player : this.getTheatreInstance().getPlayers()) {
            if (player.tile().equals(finalTile)) {
                player.hit(this, Utils.random(78));
            }
        }

        NylocasAthanatos nylocas = new NylocasAthanatos(8384, finalTile, this.getTheatreInstance());
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
        if (!this.getTheatreInstance().getVerzikNylocasList().isEmpty()) {
            return;
        }
        Tile t = this.tile();
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
        if (this.theatreInstance.getVerzikNylocasList().isEmpty()) return;
        for (var n : theatreInstance.getVerzikNylocasList()) {
            if (n == null) return;
            if (n.dead()) return;
            this.healHit(n, n.hp());
            n.die();
        }
        if (theatreInstance.getVerzikNylocasList() != null) {
            theatreInstance.getVerzikNylocasList().clear();
        }
    }
    private void handleNylocasMatomenosSpawn(NPC[] matomenosArray) {
        this.setSpawningMatomenos(true);
        this.animate(-1);
        this.animate(8117);
        Chain
            .noCtx()
            .runFn(2, () -> {
                for (var nylocas : matomenosArray) {
                    if (this.getTheatreInstance().getPlayers().size() <= 2) {
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
        if (!this.getPhase().equals(VerzikPhase.TWO)) return false;
        if (isInitialSpawn()) return true;
        if (this.getSequenceRandomIntervalTick() >= 6) {
            this.setSequenceRandomIntervalTick(0);
            if (this.getTheatreInstance().getVerzikNylocasList().isEmpty()) {
                double healthPercentage = (double) hp() / maxHp();
                return isAthanatosOrMatomenos(healthPercentage);
            }
        }
        return false;
    }
    private void sendPhaseTwoAttacks() {
        if (!this.getPhase().equals(VerzikPhase.TWO)) return;
        var players = this.getTheatreInstance().getPlayers();
        for (var player : players) {
            if (player == null) continue;
            if (isInMeleeRange(player)) break;
            double healthPercentage = (double) hp() / maxHp();
            sendInitialNylocas(healthPercentage);
            if (sendSpawnNylocas()) return;
            if (this.getAttackCount() <= 4) {
                sendToxicBlast(player);
                return;
            }
            sendElectricShock();
        }
    }
    public void sendToxicBlast(Player player) {
        if (!this.getPhase().equals(VerzikPhase.TWO)) return;
        var tileDist = this.tile().distance(player.getCentrePosition());
        int duration = 21 + 39 + (tileDist);
        var verzikTile = this.tile().center(this.getSize());
        var playerTile = player.getCentrePosition();
        Projectile projectile = new Projectile(verzikTile, playerTile, 1583, 21, duration, 70, 0, 12, this.getSize(), 128, 0);
        int delay = projectile.send(this, playerTile);
        runToxicBlastTask(player, projectile, delay);
        World.getWorld().tileGraphic(1584, player.tile(), 0, projectile.getSpeed());
    }
    public void sendKnockBack(Player p) {
        if (!this.getPhase().equals(VerzikPhase.TWO)) return;
        int vecX = (p.getAbsX() - Utils.getClosestX(this, p.tile()));
        if (vecX != 0)
            vecX /= Math.abs(vecX);
        int vecY = (p.getAbsY() - Utils.getClosestY(this, p.tile()));
        if (vecY != 0)
            vecY /= Math.abs(vecY);
        int endX = p.getAbsX();
        int endY = p.getAbsY();
        for (int i = 0; i < 4; i++) {
            if (DumbRoute.getDirection(endX, endY, this.getTheatreInstance().getzLevel(), p.getSize(), endX + vecX, endY + vecY) == null) {
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
        var target = Utils.randomElement(this.getTheatreInstance().getPlayers());
        var tileDist = this.tile().distance(target.tile());
        int duration = (20 + (10 * tileDist));
        Projectile projectile = new Projectile(this, target, 1591, 21, duration, 70, 24, 8, this.getSize(), 128, 0);
        int delay = projectile.send(this, target);
        Hit hit = Hit.builder(this, target, CombatFactory.calcDamageFromType(this, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        var damage = hit.getDamage();
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
        var damage = Utils.random(10, 137);
        Hit hit = Hit.builder(this, target, isUsingPrayer ? (int) (damage * .50) : damage, delay, CombatType.MAGIC).setAccurate(true);
        hit.submit();
        target.graphic(1582, GraphicHeight.LOW, projectile.getSpeed());
    }
    public void handleElectricShock() {
        if (!this.getPhase().equals(VerzikPhase.TWO)) return;
        this.setAttackCount(0);
        var target = Utils.randomElement(this.getTheatreInstance().getPlayers());
        if (target == null) return;
        var tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        var tileDist = this.tile().distance(target.tile());
        int duration = (21 + 39 + (5 * tileDist));
        Projectile projectile = new Projectile(this, target, 1585, 21, duration, 70, 24, 12, this.getSize(), 128, 5);
        int delay = projectile.send(this, target);
        Hit hit = Hit.builder(this, target, CombatFactory.calcDamageFromType(this, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        var damage = hit.getDamage();
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
                var damage = Utils.random(1, 47);
                if (Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MISSILES)) {
                    player.hit(this, (int) (damage * 0.5));
                    return;
                }
                player.hit(this, damage);
            }
        });
    }
    public void sendPhaseOne() {
        if (!this.getPhase().equals(VerzikPhase.ONE)) return;
        this.face(null);
        this.animate(8109);
        var players = this.getTheatreInstance().getPlayers();
        for (var player : players) {
            if (player == null) continue;
            var position = player.tile();
            var tileDist = this.tile().distance(position);
            int duration = (20 + (10 * tileDist));
            Chain.noCtx().delay(2, () -> sendSphere(player, duration));
        }
    }
    public void sendPhaseTwo() {
        if (!this.getPhase().equals(VerzikPhase.TWO)) return;
        this.attackCount++;
        var target = Utils.randomElement(this.getTheatreInstance().getPlayers());
        var tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        sendPhaseTwoAttacks();
    }
    private void sendMagicPhaseThree() {
        this.animate(8124);
        var target = Utils.randomElement(theatreInstance.getPlayers());
        this.getCombat().setTarget(target);
        for (var p : theatreInstance.getPlayers()) {
            var tileDist = this.tile().distance(p.tile());
            int duration = (53 + 45 + (7 * tileDist));
            Projectile projectile = new Projectile(this, p, 1594, 53, duration, 100, 25, 20, this.getSize(), 100, 7);
            int delay = projectile.send(this, p);
            Hit hit = Hit.builder(this, target, Utils.random(0, 33), delay, CombatType.MAGIC).checkAccuracy();
            hit.submit();
            if (Prayers.usingPrayer(p, Prayers.PROTECT_FROM_MAGIC)) {
                hit.block();
            }
            p.graphic(1581, GraphicHeight.LOW, projectile.getSpeed());
        }
    }
    private void sendMeleePhaseThree(Player target) {
        this.animate(8123);
        Hit hit = Hit.builder(this, target, Utils.random(0, 63), 3, CombatType.MELEE).checkAccuracy();
        hit.submit();
        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MELEE)) {
            hit.block();
        }
    }
    private void sendRangePhaseThree() {
        this.animate(8125);
        var target = Utils.randomElement(theatreInstance.getPlayers());
        this.getCombat().setTarget(target);
        for (var p : theatreInstance.getPlayers()) {
            var tileDist = this.tile().distance(p.tile());
            int duration = (62 + 45 + (7 * tileDist));
            Projectile projectile = new Projectile(this, p, 1593, 62, duration, 100, 25, 20, this.getSize(), 100, 7);
            int delay = projectile.send(this, p);
            Hit hit = Hit.builder(this, target, Utils.random(0, 33), delay, CombatType.RANGED).checkAccuracy();
            hit.submit();
            if (Prayers.usingPrayer(p, Prayers.PROTECT_FROM_MISSILES)) {
                hit.block();
            }
        }
    }
    private void sendWebs() { //projectile 1601
        if (!this.tile().equals(destination.getX(), destination.getY())) {
            this.setPhase(VerzikPhase.TRANSITIONING);
            this.setPathing(true);
        }
        this.animate(8127);
    }
    private void sendChargedShot() {
        this.animate(8126);
        MutableObject<Tile> randomTile = new MutableObject<>();
        for (int index = 0; index < this.getTheatreInstance().getPlayers().size(); index++) {
            var randomTileAround = World.getWorld().randomTileAround(this.tile, 10);
            randomTile.setValue(randomTileAround);
            if (RegionManager.blocked(randomTile.getValue())) continue;
            World.getWorld().tileGraphic(1595, randomTile.getValue(), 0, 0);
        }
        for (var p : theatreInstance.getPlayers()) {
            var tileDist = this.tile().distance(p.tile());
            int duration = (261 + 144 + (tileDist));
            Projectile projectile = new Projectile(this, p, 1596, 261, duration, 250, 30, 50, this.getSize(), 0, 0);
            int delay = this.executeProjectile(projectile);
            Chain.noCtx().runFn(delay, () -> {
                if (!p.tile().equals(randomTile.getValue())) {
                    var damage = Utils.random(1, 80);
                    Hit hit = Hit.builder(this, p, damage, delay, CombatType.MAGIC).setAccurate(true);
                    hit.submit();
                } else {
                    p.graphic(1597, GraphicHeight.LOW, 0);
                }
                this.setSendingChargedShot(false);
            }).then(2, this::sequenceTornado);
        }
    }
    private void sequenceTornado() { //TODO

    }
    private void sendSequences() {
        if (this.getIntervalCount() >= (this.getPhase() == VerzikPhase.ONE ? 12 : this.getPhase() == VerzikPhase.TWO ? 4 : this.getPhase() == VerzikPhase.THREE && !this.isAdjustAttackSpeed() ? 7 : 5) && this.getIntervals() <= 0 && !this.dead()) {
            this.setIntervalCount(0);
            this.setIntervals(value);
            switch (this.getPhase()) {
                case ONE -> sendPhaseOne();
                case TWO -> {
                    if (isSpawningMatomenos()) return;
                    sendPhaseTwo();
                }
                case THREE -> {
                    if (this.isSendingChargedShot()) return;
                    sequenceThirdPhase();
                }
            }
        }
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
        if (healthPercentage <= 0.20) {
            this.forceChat("I'm not finished with you just yet!");
            this.sendChargedShot();
            this.setSendingChargedShot(true);
            this.setAdjustAttackSpeed(true);
            return;
        }
        if (random == 1) {
            sendMagicPhaseThree();
            return;
        }
        if (random == 2) {
            sendRangePhaseThree();
            return;
        }
    }
    @Nullable
    private Player validate() {
        var target = Utils.randomElement(theatreInstance.getPlayers());
        this.getCombat().setTarget(target);
        if (!DumbRoute.withinDistance(this, target, 1)) {
            return null;
        }
        return target;
    }
    @Override
    public void postSequence() {
        if (this.id() == VERZIK_VITUR_8369) return;
        if (transitionBetweenPhase()) return;
        this.intervalCount++;
        this.intervals--;
        sendSequences();
    }
    @Override
    public void die() {
        switch (this.getPhase()) {
            case ONE -> transitionPhaseOne();
            case TWO -> transitionPhaseTwo();
            case THREE -> transitionPhaseThree();
        }
    }
    public void transitionPhaseOne() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.animate(8111);
        var pillarObjects = this.getTheatreInstance().getVerzikPillarObjects();
        var pillarNpcs = this.getTheatreInstance().getVerzikPillarNpcs();
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
        for (var n : this.getTheatreInstance().getVerzikNylocasList()) {
            if (this.getTheatreInstance().getVerzikNylocasList().isEmpty()) return;
            n.die();
        }
        this.getTheatreInstance().getVerzikNylocasList().clear();
        Chain
            .noCtx()
            .runFn(2, () -> animateAndTransmog(8119, 8373))
            .then(4, this::finalizePhaseTwo);
    }
    public void transitionPhaseThree() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.animate(8128);
        this.getTheatreInstance().spawnTreasure(true);
        this.setAdjustAttackSpeed(false);
        Chain
            .noCtx()
            .delay(2, () -> animateAndTransmog(-1, 8375))
            .then(6, this::animateThrone)
            .then(4, this::openTreasureRoom);
    }
    private void setNpcInstance(NPC nylocas) {
        nylocas.setInstance(theatreInstance);
    }
    private void spawnNylocasNpc(NPC nylocas) {
        nylocas.spawn(false);
    }
    private void addToNylocasList(NPC nylocas) {
        this.getTheatreInstance().getVerzikNylocasList().add(nylocas);
    }
    private void animateThrone() {
        throne.animate(8108);
        this.remove();
    }
    private void openTreasureRoom() {
        GameObject throne_two = new GameObject(TREASURE_ROOM, new Tile(throne.getX(), throne.getY(), this.getTheatreInstance().getzLevel()), 10, 0);
        throne.replaceWith(throne_two, false);
        this.setPhase(VerzikPhase.DEAD);
    }
    private void animateAndTransmog(int animation, int id) {
        this.animate(animation);
        this.transmog(id);
        this.setInstance(this.getTheatreInstance());
    }
    private void checkForceMovement(GameObject o) {
        theatreInstance
            .getPlayers()
            .stream()
            .filter(Objects::nonNull)
            .filter(player -> player.tile().isWithinDistance(this.tile(), 1) || o.tile().isWithinDistance(player.tile(), 1))
            .forEach(this::forceMove);
    }
    private void forceMove(@NotNull Player player) {
        player.hit(this, 10);
        Direction direction = Direction.SOUTH;
        ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x(), direction.y()), 30, 60, 1114, 0);
        player.setForceMovement(forceMovement);
    }
    public void interpolatePhaseTwoTransition() {
        for (int index = 0; index < 2; index++) {
            Tile currentTile = this.tile();
            var dst = destination;
            int deltaX = dst.getX() - currentTile.getX();
            int deltaY = dst.getY() - currentTile.getY();
            int nextStepDeltaX = Integer.compare(deltaX, 0);
            int nextStepDeltaY = Integer.compare(deltaY, 0);
            int nextX = currentTile.getX() + nextStepDeltaX;
            int nextY = currentTile.getY() + nextStepDeltaY;
            if (nextStepDeltaX == 0 && nextStepDeltaY == 0) {
                Chain.noCtx().runFn(1, this::finalizeTransmog);
                return;
            }
            this.queueTeleportJump(new Tile(nextX, nextY, this.getTheatreInstance().getzLevel()));
        }
    }
    private void finalizeTransmog() {
        this.queueTeleportJump(this.getDestination().transform(1, 1, this.getTheatreInstance().getzLevel()));
        this.transmog(8372);
        this.heal(this.maxHp());
        this.setPathing(false);
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
        int distance = (int) nylocasMutableObject.getValue().tile().distanceTo(this.tile);
        int duration = 3 + 21 + distance;

        if (stopHealingTask(nylocasMutableObject, heal, duration)) return;

        Projectile nyloProjectile = new Projectile(nylocasMutableObject.getValue(), this, 1587, 3, duration, 5, 70, 2, this.getSize(), 0, 0);
        int nyloDelay = nylocasMutableObject.getValue().executeProjectile(nyloProjectile);
        this.healHit(nylocasMutableObject.getValue(), Utils.random(9, 10), nyloDelay);
    }
    private boolean stopHealingTask(MutableObject<NylocasAthanatos> nylocasMutableObject, Task heal, int dur) {
        if (this.getTheatreInstance() == null) {
            nylocasMutableObject.getValue().remove();
            heal.stop();
            return true;
        }

        if (nylocasMutableObject.getValue().dead()) {
            Projectile nyloProjectile = new Projectile(nylocasMutableObject.getValue(), this, 1588, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
            int nyloDelay = nylocasMutableObject.getValue().executeProjectile(nyloProjectile);
            this.hit(nylocasMutableObject.getValue(), 70, nyloDelay);
            heal.stop();
            return true;
        }
        return false;
    }
    private void sendInitialProjectile(@NotNull MutableObject<NylocasAthanatos> nylocasMutableObject) {
        int dist = (int) nylocasMutableObject.getValue().tile().distanceTo(this.tile);
        int dur = 3 + 21 + dist;

        if (nylocasMutableObject.getValue().dead()) {
            Projectile nyloProjectile = new Projectile(nylocasMutableObject.getValue(), this, 1588, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
            int nyloDelay = nylocasMutableObject.getValue().executeProjectile(nyloProjectile);
            this.hit(nylocasMutableObject.getValue(), 75, nyloDelay);
            return;
        }

        Projectile nyloProjectile = new Projectile(nylocasMutableObject.getValue(), this, 1587, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
        int nyloDelay = nylocasMutableObject.getValue().executeProjectile(nyloProjectile);
        this.healHit(nylocasMutableObject.getValue(), 50, nyloDelay);
    }
    private Entity getEntity(Player player, Entity target, boolean lineOfSight) {
        for (var npc : this.getTheatreInstance().getVerzikPillarNpcs()) {
            var playerSwTile = player.getCentrePosition().getSouthwestTile(npc);
            var pillarSwTile = npc.getCentrePosition().getSouthwestTile(player);
            if (player.tile().getX() <= 3168) {
                if (playerSwTile.isWithinDistance(pillarSwTile, 2) && !lineOfSight) {
                    if (pillarSwTile.isWithinDistance(playerSwTile, 1)) {
                        target = npc;
                        break;
                    }
                }
            } else {
                var playerSeTile = player.getCentrePosition().getSouthEastTile(npc);
                var pillarSeTile = npc.getCentrePosition().getSouthEastTile(player);
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
        this.canAttack(true);
        animateAndTransmog(-1, 8374);
        this.heal(this.maxHp());
        this.setIgnoreOccupiedTiles(true);
        this.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 40);
        this.forceChat("Behold my true nature!");
        this.queueTeleportJump(this.getDestination().transform(-1, -1, this.getTheatreInstance().getzLevel()));
        this.setPhase(VerzikPhase.THREE);
    }
    private void animateAndSetPath() {
        this.animate(-1);
        this.setPathing(true);
    }
    private void transitionAndReplace(Direction direction) {
        throne = new GameObject(VERZIKS_THRONE_32737, new Tile(3167, 4324, this.getTheatreInstance().getzLevel()), 10, 0);
        throne.spawn();
        animateAndTransmog(8112, 8371);
        this.setPositionToFace(this.getDestination().center(5).tileToDir(direction));
    }
    private void clearLists(@NotNull List<GameObject> pillarObjects, @NotNull List<NPC> pillarNpcs) {
        pillarObjects.clear();
        pillarNpcs.forEach(NPC::remove);
        pillarNpcs.clear();
    }
    private void replaceObjects() {
        this
            .getTheatreInstance()
            .getVerzikPillarNpcs()
            .forEach(n ->
                MapObjects
                    .get(32687, n.tile())
                    .ifPresent(pillar -> {
                        pillar.setId(32688);
                        Chain.noCtx().delay(2, () -> {
                            pillar.setId(32689);
                            checkForceMovement(pillar);
                        }).then(1, () -> pillar.animate(8104)).then(2, pillar::remove);
                    }));
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
    private boolean transitionBetweenPhase() {
        if (this.id() == VERZIK_VITUR_8371 && this.isPathing()) {
            this.walkCount++;
            if (this.getWalkCount() >= 2) {
                this.setWalkCount(0);
                interpolatePhaseTwoTransition();
            }
            return true;
        }
        return this.getPhase().equals(VerzikPhase.TRANSITIONING);
    }
}
