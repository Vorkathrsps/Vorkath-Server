package com.aelous.model.content.raids.theatre.nylocas.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.nylocas.VasiliasListener;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasNpcHandler extends NPC {

    VasiliasListener vasiliasListener;
    AtomicInteger vasiliasLifeLength = new AtomicInteger(50);

    public VasiliasNpcHandler(int id, Tile tile, VasiliasListener vasiliasListener) {
        super(id, tile);
        this.vasiliasListener = vasiliasListener;
        vasiliasListener.vasiliasNpc.add(this);
        putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
        System.out.println("instantiated vasilias npcs successfully");
    }

    @Override
    public void postSequence() {
        super.postSequence();

        if (dead() || locked() || frozen()) {
            return;
        }

        vasiliasLifeLength.getAndDecrement();

        if (vasiliasLifeLength.get() == 0) {
            this.die();
            vasiliasLifeLength.getAndSet(50);
        }

        Tile[] finalTiles = new Tile[]{
            new Tile(3290, 4249, 0),
            new Tile(3295, 4243, 0),
            new Tile(3301, 4248, 0),
            new Tile(3290, 4248, 0),
            new Tile(3296, 4243, 0),
            new Tile(3301, 4249, 0)
        };

        Tile selectedTile;
        int matchingIndex = -1;
        for (int i = 0; i < VasiliasListener.getToSpawn().length; i++) {
            selectedTile = VasiliasListener.getToSpawn()[i].transform(0, 0, 0);
            if (selectedTile.equals(this.getX(), this.getY())) {
                matchingIndex = i;
                break;
            }
        }

        if (matchingIndex != -1) {
            this.getMovementQueue().reset();
            Tile destinationTile = finalTiles[matchingIndex];
            this.setPositionToFace(destinationTile);
            this.stepAbs(destinationTile.getX(), destinationTile.getY(), MovementQueue.StepType.FORCED_WALK);
            this.waitForTile(destinationTile, () -> {
                List<NPC> availablePillars = vasiliasListener.pillarNpc.stream().filter(p -> !p.dead() && p.isRegistered()).toList();
                if (!availablePillars.isEmpty()) {
                    List<NPC> closestPillars = new ArrayList<>(availablePillars);
                    closestPillars.sort(Comparator.comparingDouble(pillar -> this.tile().distance(pillar.tile())));

                    Random random = new Random();
                    int randomIndex = random.nextInt(Math.min(closestPillars.size(), 2));
                    NPC randomPillar = closestPillars.get(randomIndex);
                    this.getCombat().setTarget(randomPillar);
                }
            });
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
