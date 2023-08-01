package com.aelous.model.content.raids.theatre.boss.maiden.handler;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.maiden.blood.BloodSpawn;
import com.aelous.model.content.raids.theatre.boss.maiden.nylos.MaidenNylo;
import com.aelous.model.content.raids.theatre.stage.RoomState;
import com.aelous.model.content.raids.theatre.stage.TheatreStage;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.aelous.model.content.raids.theatre.boss.maiden.utils.MaidenUtils.*;

/**
 * @Author: Origin
 * @Date: 7/21/2023
 */
public class MaidenProcess extends NPC {
    private final Player player;
    BloodSpawn orb = null;
    MaidenNylo nylo = null;
    private final List<Player> players = new ArrayList<>();
    @Getter @Setter private int randomBlood = 0;
    private int intervalCount = 0;
    private int attackInterval = 10;
    private boolean nyloSpawned70to50;
    private boolean nyloSpawned50to30;
    private boolean nyloSpawned30to0;
    private final TheatreArea theatreArea;
    private final Theatre theatre;

    public MaidenProcess(int id, Tile tile, Player player, Theatre theatre, TheatreArea theatreArea) {
        super(id, tile);
        this.player = player;
        this.theatre = theatre;
        this.theatreArea = theatreArea;
        this.setCombatMethod(null);
        this.spawnDirection(Direction.EAST.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.getMovementQueue().setBlockMovement(true);
    }

    public void sequenceTornadoAndBlood() {
        if (Utils.sequenceRandomInterval(randomBlood, 7, 14)) {
            throwBlood();
            this.setRandomBlood(0);
            return;
        }
        randomBlood++;
        this.face(player);
        Chain.noCtx().runFn(1, () -> this.face(null));
        this.animate(8092);
        int tileDist = this.tile().distance(player.tile());
        int duration = (80 + -5 + (8 * tileDist));
        Projectile p = new Projectile(this, player, 1577, 80, duration, 0, 0, 0, 6, 8);
        final int delay = this.executeProjectile(p);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
    }

    public void throwBlood() {
        var tile = player.tile().copy();
        this.face(player);
        this.animate(8091);
        Chain.noCtx().runFn(1, () -> this.face(null));
        var tileDist = this.tile().distance(tile);
        int duration = (68 + 25 + (10 * tileDist));
        Projectile p = new Projectile(this, tile, 2002, 68, duration, 95, 0, 20, 5, 10);
        p.send(this, tile);
        World.getWorld().tileGraphic(1579, tile, 0, p.getSpeed());
        this.orb = new BloodSpawn(10821, new Tile(p.getEnd().getX(), p.getEnd().getY()).transform(0, 0, theatreArea.getzLevel()), player, this, theatreArea);
        Chain.noCtx().runFn(16, () -> {
            this.orb.spawn(false);
        });
    }

    public void spawnNylocasMatomenos(int partySize) {
        int zLevel = theatreArea.getzLevel();
        Set<Tile> selectedTiles = new HashSet<>();
        List<Tile> availableTiles = new ArrayList<>(Arrays.asList(MaidenNylo.spawn_tiles));
        Collections.shuffle(availableTiles);

        if (partySize < 5) {
            int numNpcsToSpawn = partySize * 2;

            for (int i = 0; i < Math.min(numNpcsToSpawn, availableTiles.size()); i++) {
                Tile tile = availableTiles.get(i);
                if (!selectedTiles.contains(tile)) {
                    nylo = (MaidenNylo) new MaidenNylo(NpcIdentifiers.NYLOCAS_MATOMENOS, new Tile(tile.getX(), tile.getY()).transform(0, 0, zLevel), this).spawn(false);
                    selectedTiles.add(tile);
                }
            }
        } else {
            for (int i = 0; i < MaidenNylo.spawn_tiles.length; i++) {
                Tile tile = MaidenNylo.spawn_tiles[i];
                if (!selectedTiles.contains(tile)) {
                    nylo = (MaidenNylo) new MaidenNylo(NpcIdentifiers.NYLOCAS_MATOMENOS, new Tile(tile.getX(), tile.getY()).transform(0, 0, zLevel), this).spawn(false);
                    selectedTiles.add(tile);
                }
            }
        }
        selectedTiles.clear();
    }

    @Override
    public void postSequence() {
        if (this.dead()) {
            return;
        }

        if (insideBounds()) {

            double healthAmount = hp() * 1.0 / (maxHp() * 1.0);

            if (healthAmount <= 0.70 && !nyloSpawned70to50) {
                this.transmog(10815);
                spawnNylocasMatomenos(this.partySize());
                nyloSpawned70to50 = true;
                return;
            }
            if (healthAmount <= 0.50 && !nyloSpawned50to30) {
                this.transmog(10816);
                spawnNylocasMatomenos(this.partySize());
                nyloSpawned50to30 = true;
                return;
            }
            if (healthAmount <= 0.30 && !nyloSpawned30to0) {
                this.transmog(10817);
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
        Theatre.theatrePhase.setStage(TheatreStage.TWO);
        player.setRoomState(RoomState.COMPLETE);
        player.getTheatreParty().onRoomStateChanged(player.getRoomState());
        if (nylo != null) {
            nylo.die();
        }
        players.clear();
        Chain.noCtx().runFn(1, () -> {
            this.animate(8094);
        }).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        });
    }

    public int partySize() {
        return player.getTheatreParty().getParty().size();
    }

    protected boolean insideBounds() {
        if (IGNORED.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()) || (!MAIDEN_AREA.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()) && IGNORED.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()))) {
            return false;
        }

        if (MAIDEN_AREA.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()) && !IGNORED.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile())) {
            if (!players.contains(player)) {
                players.add(player);
                return true;
            }
        } else {
            players.remove(player);
            return false;
        }
        return true;
    }

}
