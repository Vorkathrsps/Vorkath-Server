package com.aelous.model.content.raids.theatre.boss.nylocas.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.nylocas.VasiliasHandler;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasProcess extends NPC {
    VasiliasHandler vasiliasHandler;
    AtomicInteger vasiliasLifeLength = new AtomicInteger(50);

    int[] npcs = new int[]{NYLOCAS_ISCHYROS_8342, NYLOCAS_TOXOBOLOS_8343, NYLOCAS_HAGIOS};
    @Getter @Setter int timer = 3;
    @Getter @Setter int transmogIdx;
    @Getter @Setter boolean pathingToTile;
    @Nonnull Player player;
    public VasiliasProcess(int id, Tile tile, VasiliasHandler vasiliasHandler, @Nonnull Player player) { //yes
        super(id, tile);
        this.vasiliasHandler = vasiliasHandler;
        this.player = player;
        this.setIgnoreOccupiedTiles(true);
        vasiliasHandler.vasiliasNpc.add(this);
        putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
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
                int randomIndex = vasiliasHandler.getRandomNPC();
                while (randomIndex == transmogIdx) {
                    randomIndex = vasiliasHandler.getRandomNPC();
                }
                setTransmogIdx(randomIndex);
            }
            this.transmog(vasiliasHandler.getRandomNPC());
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
        for (int i = 0; i < VasiliasHandler.getFromTile().length; i++) {
            selectedTile = VasiliasHandler.getFromTile()[i].transform(0, 0, 0);
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
        if (!isPathingToTile() && vasiliasHandler.pillarNpc.isEmpty() && getCombat().getTarget() == null) {
            this.getCombat().setTarget(player);
        }
    }

    private void attackClosestAlivePillar() {
        List<NPC> availablePillars = vasiliasHandler.pillarNpc.stream().filter(p -> !p.dead() && p.isRegistered()).toList();
        if (!availablePillars.isEmpty()) {
            List<NPC> closestPillars = new ArrayList<>(availablePillars);
            closestPillars.sort(Comparator.comparingDouble(pillar -> this.tile().distance(pillar.tile())));

            Random random = new Random();
            int randomIndex = random.nextInt(Math.min(closestPillars.size(), 2));
            NPC randomPillar = closestPillars.get(randomIndex);
            this.getCombat().setTarget(randomPillar);
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

}
