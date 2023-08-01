package com.cryptic.model.content.raids.theatre.boss.nylocas.handler;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.boss.nylocas.VasiliasBoss;
import com.cryptic.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.cryptic.model.content.raids.theatre.boss.nylocas.state.VasiliasState;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasProcess extends NPC {
    Vasilias vasilias;
    AtomicInteger vasiliasLifeLength = new AtomicInteger(50);
    @Getter
    private static final Tile[] fromTile = new Tile[]{
        new Tile(3282, 4249),
        new Tile(3295, 4235),
        new Tile(3309, 4248),
        new Tile(3282, 4248),
        new Tile(3296, 4235),
        new Tile(3309, 4249)
    };
    int[] npcs = new int[]{NYLOCAS_ISCHYROS_8342, NYLOCAS_TOXOBOLOS_8343, NYLOCAS_HAGIOS};
    @Getter @Setter int timer = 3;
    @Getter @Setter int transmogIdx;
    @Getter @Setter boolean pathingToTile;
    public List<NPC> pillarNpc = new ArrayList<>();
    public List<NPC> vasiliasNpc = new ArrayList<>();
    public List<GameObject> pillarObject = new ArrayList<>();
    @Nonnull Player player;
    AtomicInteger wave = new AtomicInteger();
    @Getter int finalInterpolatedTransmog;
    public VasiliasProcess(int id, Tile tile, @Nonnull Player player) {
        super(id, tile);
        this.player = player;
        this.setIgnoreOccupiedTiles(true);
        vasilias.vasiliasNpc.add(this);
        putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
    }

    public Tile getRandomTile() {
        Tile[] tileArray = fromTile;
        if (tileArray.length == 0) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(tileArray.length);
        return tileArray[randomIndex].transform(0, 0, 0);
    }

    public int getRandomNPC() {
        Random random = new Random();
        finalInterpolatedTransmog = random.nextInt(npcs.length);
        return npcs[finalInterpolatedTransmog];
    }

    public void startSpiderSpawnTask() {
        VasiliasBoss boss = new VasiliasBoss(8355, new Tile(3294, 4247, 0), player, VasiliasState.ALIVE);
        Chain.noCtxRepeat().repeatingTask(5, t -> {
            this.spawn(false);
            if (this.wave.get() == 50) {
                boss.spawn(false);
                t.stop();
                return;
            }
            this.wave.getAndIncrement();
        });
    }

    @Override
    public void postSequence() {
        super.postSequence();

        if (dead() || locked() || frozen()) {
            return;
        }

        if (getTimer() > 0) {
            timer--;
            if (transmogIdx == npcs.length - 1) {
                int randomIndex = getRandomNPC();
                while (randomIndex == transmogIdx) {
                    randomIndex = getRandomNPC();
                }
                setTransmogIdx(randomIndex);
            }
            this.transmog(getRandomNPC());
            transmogIdx = (transmogIdx + 1) % npcs.length;
            setTimer(3);
        }

        vasiliasLifeLength.getAndDecrement();

        if (vasiliasLifeLength.get() == 0) {
            this.die();
            vasiliasLifeLength.getAndSet(50);
        }

        Tile[] toTile = new Tile[]{
            new Tile(3290, 4249, 0),
            new Tile(3295, 4243, 0),
            new Tile(3301, 4248, 0),
            new Tile(3290, 4248, 0),
            new Tile(3296, 4243, 0),
            new Tile(3301, 4249, 0)
        };

        Tile selectedTile;
        int matchingIndex = -1;
        for (int i = 0; i < getFromTile().length; i++) {
            selectedTile = getFromTile()[i].transform(0, 0, 0);
            if (selectedTile.equals(this.getX(), this.getY())) {
                matchingIndex = i;
                break;
            }
        }

        if (matchingIndex != -1) {
            this.getMovementQueue().reset();
            setPathingToTile(true);
            Tile destinationTile = toTile[matchingIndex];
            this.setPositionToFace(destinationTile);
            this.stepAbs(destinationTile.getX(), destinationTile.getY(), MovementQueue.StepType.FORCED_WALK);
            this.waitForTile(destinationTile, () -> {
                setPathingToTile(false);
                timer = -1;
                attackClosestAlivePillar();
            });
        }
        if (!isPathingToTile() && getCombat().getTarget() == null) {
            attackClosestAlivePillar();
        }
        if (!isPathingToTile() && vasilias.pillarNpc.isEmpty() && getCombat().getTarget() == null) {
            this.getCombat().setTarget(player);
        }
    }

    @Override
    public void die() {
        var target = this.getCombat().getTarget();
        Chain.noCtx().delay(1, () -> {
            animate(7991);
            if (tile() != null && getCombat().getTarget() != null && getCombat().getTarget().tile() != null) {
                if (this.tile().nextTo(target.tile())) {
                    target.hit(this, Utils.random(getCombatInfo().maxhit));
                }
            }
        }).then(3, () -> World.getWorld().unregisterNpc(this));
    }

    private void attackClosestAlivePillar() {
        List<NPC> availablePillars = vasilias.pillarNpc.stream().filter(p -> !p.dead() && p.isRegistered()).toList();
        if (!availablePillars.isEmpty()) {
            List<NPC> closestPillars = new ArrayList<>(availablePillars);
            closestPillars.sort(Comparator.comparingDouble(pillar -> this.tile().distance(pillar.tile())));

            Random random = new Random();
            int randomIndex = random.nextInt(Math.min(closestPillars.size(), 2));
            NPC randomPillar = closestPillars.get(randomIndex);
            this.getCombat().setTarget(randomPillar);
        }
    }

}
