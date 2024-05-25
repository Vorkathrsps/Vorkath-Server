package com.cryptic.model.content.raids.theatreofblood.boss.maiden;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatreofblood.TheatreInstance;
import com.cryptic.model.content.raids.theatreofblood.boss.maiden.blood.BloodSpawn;
import com.cryptic.model.content.raids.theatreofblood.boss.maiden.nylos.MaidenNylo;
import com.cryptic.model.content.raids.theatreofblood.stage.RoomState;
import com.cryptic.model.content.raids.theatreofblood.stage.TheatreStage;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.cryptic.model.content.raids.theatreofblood.boss.maiden.utils.MaidenUtils.*;

/**
 * @Author: Origin
 * @Date: 7/21/2023
 */
public class Maiden extends NPC {
    BloodSpawn orb = null;
    MaidenNylo nylo = null;
    private final List<Player> players = new ArrayList<>();
    @Getter
    @Setter
    private int randomBlood = 0;
    private int intervalCount = 0;
    private int attackInterval = 10;
    private boolean nyloSpawned70to50;
    private boolean nyloSpawned50to30;
    private boolean nyloSpawned30to0;
    private final TheatreInstance theatreInstance;

    public Maiden(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.setCombatMethod(null);
        this.spawnDirection(Direction.EAST.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.getMovementQueue().setBlockMovement(true);
        this.setHealthBar(2);
    }

    public void sequenceTornadoAndBlood() {
        if (Utils.sequenceRandomInterval(randomBlood, 7, 14)) {
            throwBlood();
            this.setRandomBlood(0);
            return;
        }
        randomBlood++;
        for (Player target : this.theatreInstance.getPlayers()) {
            if (!players.contains(target)) continue;
            if (target == null) continue;
            this.face(target);
            Chain.noCtx().runFn(1, () -> this.face(null));
            this.animate(8092);
            int tileDist = this.tile().distance(target.tile());
            int duration = (80 + -5 + (8 * tileDist));
            Projectile p = new Projectile(this, target, 1577, 80, duration, 0, 0, 0, 6, 8);
            this.executeProjectile(p);
            Hit hit = Hit.builder(this, target, CombatFactory.calcDamageFromType(this, target, CombatType.MAGIC), p.getSpeed() / 30, CombatType.MAGIC).checkAccuracy(true);
            hit.submit();
        }
    }

    public void throwBlood() {
        var target = this.theatreInstance.getRandomTarget();
        var tile = target.tile().copy();
        this.face(target);
        this.animate(8091);
        Chain.noCtx().runFn(1, () -> this.face(null));
        var tileDist = this.tile().distance(tile);
        int duration = (68 + 25 + (10 * tileDist));
        Projectile p = new Projectile(this, tile, 2002, 68, duration, 95, 0, 20, 5, 10);
        p.send(this, tile);
        World.getWorld().sendClippedTileGraphic(1579, tile, 0, p.getSpeed());
        this.orb = new BloodSpawn(10821, new Tile(p.getEnd().getX(), p.getEnd().getY()).transform(0, 0, theatreInstance.getzLevel()), target, this, theatreInstance);
        Chain.noCtx().runFn(16, () -> {
            this.orb.setInstancedArea(theatreInstance);
            this.orb.spawn(false);
        });
    }

    public void spawnNylocasMatomenos(int partySize) {
        int zLevel = theatreInstance.getzLevel();
        Set<Tile> selectedTiles = new HashSet<>();
        List<Tile> availableTiles = new ArrayList<>(Arrays.asList(MaidenNylo.spawn_tiles));

        if (partySize < 5) {
            int numNpcsToSpawn = partySize * 2;

            for (int i = 0; i < Math.min(numNpcsToSpawn, availableTiles.size()); i++) {
                Collections.shuffle(availableTiles);
                Tile tile = availableTiles.get(i);
                var finalTile = Utils.randomElement(availableTiles);
                if (!selectedTiles.contains(tile)) {
                    nylo = new MaidenNylo(NpcIdentifiers.NYLOCAS_MATOMENOS, finalTile.transform(0, 0, zLevel), this);
                    nylo.setInstancedArea(theatreInstance);
                    nylo.spawn(false);
                    selectedTiles.add(tile);
                }
            }
        } else {
            for (int i = 0; i < MaidenNylo.spawn_tiles.length; i++) {
                Collections.shuffle(availableTiles);
                Tile tile = MaidenNylo.spawn_tiles[i];
                var finalTile = Utils.randomElement(availableTiles);
                if (!selectedTiles.contains(tile)) {
                    nylo = new MaidenNylo(NpcIdentifiers.NYLOCAS_MATOMENOS, finalTile.transform(0, 0, zLevel), this);
                    nylo.setInstancedArea(theatreInstance);
                    nylo.spawn(false);
                    selectedTiles.add(tile);
                }
            }
        }
        selectedTiles.clear();
    }

    @Override
    public void combatSequence() {
        if (this.dead()) {
            return;
        }

        if (insideBounds()) {

            double healthAmount = hp() * 1.0 / (maxHp() * 1.0);

            if (healthAmount <= 0.70 && !nyloSpawned70to50) {
                this.transmog(10815, false);
                spawnNylocasMatomenos(this.partySize());
                nyloSpawned70to50 = true;
                return;
            }
            if (healthAmount <= 0.50 && !nyloSpawned50to30) {
                this.transmog(10816, false);
                spawnNylocasMatomenos(this.partySize());
                nyloSpawned50to30 = true;
                return;
            }
            if (healthAmount <= 0.30 && !nyloSpawned30to0) {
                this.transmog(10817, false);
                spawnNylocasMatomenos(this.partySize());
                nyloSpawned30to0 = true;
                return;
            }

            intervalCount++;
            attackInterval--;
            if (intervalCount >= 10 && attackInterval <= 0 && !this.dead()) {
                sequenceTornadoAndBlood();
                intervalCount = 0;
                attackInterval = 10;
            }
        }
    }

    @Override
    public void die() {
        for (Player player : this.theatreInstance.getPlayers()) {
            theatreInstance.getTheatrePhase().setStage(TheatreStage.TWO);
            player.setRoomState(RoomState.COMPLETE);
        }
        if (nylo != null) nylo.die();
        players.clear();
        for (var o : this.theatreInstance.bloodObjectList) {
            if (o == null) continue;
            o.remove();
        }
        this.theatreInstance.bloodObjectList.clear();
        for (var n : this.theatreInstance.orbList) {
            if (n == null) continue;
            n.remove();
        }
        this.theatreInstance.orbList.clear();
        Chain.noCtx().runFn(1, () -> this.animate(8094)).then(3, () -> World.getWorld().unregisterNpc(this));
    }

    public int partySize() {
        var player = theatreInstance.getOwner();
        return player.getTheatreInstance().getPlayers().size();
    }

    protected boolean insideBounds() {
        for (var p2 : theatreInstance.getPlayers()) {
            if (IGNORED.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()).contains(p2.tile()) || (!MAIDEN_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()).contains(p2.tile()) && IGNORED.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()).contains(p2.tile()))) {
                continue;
            }

            if (MAIDEN_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()).contains(p2.tile()) && !IGNORED.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()).contains(p2.tile())) {
                if (!players.contains(p2)) {
                    players.add(p2);
                }
            } else {
                players.remove(p2);
            }
        }
        return !players.isEmpty();
    }

}
