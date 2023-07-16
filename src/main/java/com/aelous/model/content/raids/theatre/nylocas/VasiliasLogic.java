package com.aelous.model.content.raids.theatre.nylocas;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.nylocas.pillars.PillarSpawn;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;

import java.util.*;

public class VasiliasLogic extends NPC {

    @Getter
    public List<NPC> spawns = new ArrayList<>();
    List<NPC> pillars = PillarSpawn.getNpcs();
    List<NPC> availablePillars = pillars.stream().filter(p -> !p.dead() && p.isRegistered()).toList();
    int lifeLength = 30;

    public VasiliasLogic(int id, Tile tile) {
        super(id, tile);
        spawns.add(this);
    }

    @Override
    public void postSequence() {
        super.postSequence();

        if (pillars.isEmpty()) {
            for (var s : spawns) {
                s.die();
                spawns.clear();
            }
        }

        if (dead() || locked() || frozen()) {
            return;
        }

        lifeLength--;

        if (lifeLength == 0) {
            this.die();
            lifeLength = 120;
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
        for (int i = 0; i < VasiliasProcess.getToSpawn().length; i++) {
            selectedTile = VasiliasProcess.getToSpawn()[i].transform(0, 0, 0);
            if (selectedTile.equals(this.getX(), this.getY())) {
                matchingIndex = i;
                break;
            }
        }

        if (matchingIndex != -1) {
            Tile destinationTile = finalTiles[matchingIndex];
            this.setPositionToFace(destinationTile);
            this.getMovementQueue().reset();
            this.stepAbs(destinationTile.getX(), destinationTile.getY(), MovementQueue.StepType.FORCED_WALK);
            this.waitForTile(destinationTile, () -> {
                this.getMovementQueue().reset();

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
            if (tile() != null && getCombat().getTarget() != null) {
                if (this.tile().nextTo(target.tile())) {
                    target.hit(this, Utils.random(getCombatInfo().maxhit));
                }
            }
        }).then(2, () -> {
            World.getWorld().unregisterNpc(this);
        });
    }

}
