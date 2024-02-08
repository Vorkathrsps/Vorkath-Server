package com.cryptic.model.content.raids.theatre.boss.nylocas;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.CombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.model.map.route.routes.DumbRoute.withinDistance;

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
    public NylocasMinions(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.setIgnoreOccupiedTiles(true);
        this.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
        this.setCombatMethod(new CombatMethod() {
            @Override
            public boolean prepareAttack(Entity entity, Entity target) {
                if (!withinDistance(entity, target, 1)) return false;
                entity.animate(entity.attackAnimation());
                entity.submitAccurateHit(target, 0, Utils.random(1, entity.getAsNpc().getCombatInfo().maxhit), this);
                return true;
            }

            @Override
            public int getAttackSpeed(Entity entity) {
                return 3;
            }

            @Override
            public int moveCloseToTargetTileRange(Entity entity) {
                return 1;
            }
        });
    }
    public int getRandomNPC() {
        Random random = new Random();
        finalInterpolatedTransmog = random.nextInt(npcs.length);
        return npcs[finalInterpolatedTransmog];
    }
    @Override
    public void postCombatProcess() {
        if (getTimer() > 0) {
            timer--;

            int randomIndex;
            if (transmogIdx == npcs.length - 1) {
                randomIndex = getRandomNPC();

                while (randomIndex == transmogIdx) {
                    randomIndex = getRandomNPC();
                }

                setTransmogIdx(randomIndex);
            }
            transmogIdx = (transmogIdx + 1) % npcs.length;
            this.transmog(npcs[transmogIdx], false);
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
        if (!isPathingToTile() && theatreInstance.getPillarList().isEmpty() && getCombat().getTarget() == null) {
            this.getCombat().setTarget(this.theatreInstance.getRandomTarget());
        }
    }

    @Override
    public void die() {
        var target = this.getCombat().getTarget();
        Chain.noCtx().delay(1, () -> {
            animate(7991);
            if (this.tile() != null && this.getCombat().getTarget() != null && this.getCombat().getTarget().tile() != null) {
                if (target == null) return;
                if (this.tile().nextTo(target.tile())) {
                    if (this.getCombatInfo() != null) {
                        this.submitAccurateHit(target, 0, Utils.random(1, this.getCombatInfo().maxhit), null);
                    }
                }
            }
        }).then(3, () -> {
            theatreInstance.removeNpc(this);
            World.getWorld().unregisterNpc(this);
        });
    }

    private void attackClosestAlivePillar() {
        List<NPC> availablePillars = theatreInstance.getPillarList().stream().filter(npc -> !npc.dead() && npc.isRegistered()).toList();
        System.out.println("pillar list: "  + availablePillars.size());
        if (!availablePillars.isEmpty()) {
            List<NPC> closestPillars = new ArrayList<>(availablePillars);
            System.out.println("closest found: " + closestPillars);
            closestPillars.sort(Comparator.comparingDouble(pillar -> this.tile().distanceToPoint(pillar.tile().getX(), pillar.tile().getY())));
            int randomIndex = World.getWorld().random().nextInt(Math.min(closestPillars.size(), 2));
            System.out.println("random index: " + randomIndex);
            NPC randomPillar = closestPillars.get(randomIndex);
            System.out.println("random pillar: " + randomPillar);
            this.getCombat().setTarget(randomPillar);
        }
    }

}
