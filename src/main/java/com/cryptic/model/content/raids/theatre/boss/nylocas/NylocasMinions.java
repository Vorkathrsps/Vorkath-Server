package com.cryptic.model.content.raids.theatre.boss.nylocas;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class NylocasMinions extends NPC {
    AtomicInteger vasiliasLifeLength = new AtomicInteger(30);
    private final Tile[] fromTile = new Tile[]{
        new Tile(3282, 4249),
        new Tile(3295, 4235),
        new Tile(3309, 4248),
        new Tile(3282, 4248),
        new Tile(3296, 4235),
        new Tile(3309, 4249)
    };
    private final Tile[] toTile = new Tile[]{
        new Tile(3290, 4249),
        new Tile(3295, 4243),
        new Tile(3301, 4248),
        new Tile(3290, 4248),
        new Tile(3296, 4243),
        new Tile(3301, 4249)
    };
    int[] npcs = new int[]{NYLOCAS_ISCHYROS_8342, NYLOCAS_TOXOBOLOS_8343, NYLOCAS_HAGIOS};
    @Getter @Setter int timer = 3;
    @Getter @Setter int transmogIdx;
    @Getter @Setter boolean pathingToTile;
    @Getter int finalInterpolatedTransmog;
    TheatreInstance theatreInstance;
    public List<NPC> vasiliasNpc = new ArrayList<>();
    public NylocasMinions(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.setIgnoreOccupiedTiles(true);
        this.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
        vasiliasNpc.add(this);
    }

    public int getRandomNPC() {
        Random random = new Random();
        finalInterpolatedTransmog = random.nextInt(npcs.length);
        return npcs[finalInterpolatedTransmog];
    }

    @Override
    public void postSequence() {
        if (getTimer() > 0) {
            timer--;

            // Check if transmogrification is needed
            if (transmogIdx == npcs.length - 1) {
                // Generate a random index different from transmogIdx
                int randomIndex = getRandomNPC();
                while (randomIndex == transmogIdx) {
                    randomIndex = getRandomNPC();
                }
                // Update the transmog index
                setTransmogIdx(randomIndex);
            }

            // Transmogrify the NPC with a new random NPC
            this.transmog(getRandomNPC());

            // Update the transmog index for the next iteration
            transmogIdx = (transmogIdx + 1) % npcs.length;

            // Spawn the NPC (assuming this handles the spawning logic correctly)
            this.spawn(false);

            // Reset the timer for the next iteration
            setTimer(3);
        }

        vasiliasLifeLength.getAndDecrement();

        if (vasiliasLifeLength.get() == 0) {
            this.die();
            vasiliasLifeLength.getAndSet(30);
        }

        Tile selectedTile;
        int matchingIndex = -1;
        for (int i = 0; i < fromTile.length; i++) {
            selectedTile = fromTile[i].transform(0, 0, 0);
            if (selectedTile.equals(this.getX(), this.getY())) {
                matchingIndex = i;
                break;
            }
        }

        if (matchingIndex != -1) {
            this.getMovementQueue().reset();
            setPathingToTile(true);
            Tile destinationTile = toTile[matchingIndex].transform(0, 0, theatreInstance.getzLevel());
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
        if (!isPathingToTile() && theatreInstance.pillarList.isEmpty() && getCombat().getTarget() == null) {
            this.getCombat().setTarget(theatreInstance.getOwner()); //TODO change to pick random target
        }
    }

    @Override
    public void die() {
        var target = this.getCombat().getTarget();
        Chain.noCtx().delay(1, () -> {
            animate(7991);
            if (this.tile() != null && this.getCombat().getTarget() != null && this.getCombat().getTarget().tile() != null) {
                if (this.tile().nextTo(target.tile())) {
                    target.hit(this, Utils.random(getCombatInfo().maxhit));
                }
            }
        }).then(3, () -> World.getWorld().unregisterNpc(this));
    }

    private void attackClosestAlivePillar() {
        List<NPC> availablePillars = theatreInstance.pillarList.stream().filter(p -> !p.dead() && p.isRegistered()).toList();
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
