package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.EntityCombatBuilder;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.model.phase.Phase;
import com.cryptic.model.phase.PhaseStage;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public class Vetion extends CommonCombatMethod {
    boolean hasAttacked = false;
    Set<Tile> usedTiles = new HashSet<>();
    public static List<Player> playersInArea = new ArrayList<>();
    @Getter
    @Setter
    public Phase phase = new Phase(PhaseStage.ONE);
    public static final Area vetionArea = new Area(1879, 11553, 1895, 11559, 1); //block this when tb'd
    Set<Tile> tiles = new HashSet<>(12);
    private final List<String> VETION_QUOTES = Arrays.asList("Dodge this!",
        "Sit still you rat!",
        "Die, rodent!", "I will end you!",
        "You can't escape!",
        "Filthy whelps!", "Time to die, mortal!", "You call that a weapon?!", "Now i've got you!");

    @Override
    public void init(NPC npc) {
        if (phase.getStage() == PhaseStage.ONE && entity.npc().id() == 6611 && entity.npc().hp() == entity.npc().maxHp()) {
            Chain.noCtx().runFn(1, () -> {
                entity.npc().canAttack(false);
                entity.lockNoDamage();
                entity.animate(9977);
            }).then(2, () -> {
                entity.unlock();
                phase.setStage(PhaseStage.TWO);
                entity.npc().canAttack(true);
                entity.getCombat().setTarget(target);
            });
        }
    }

    @Override
    public void preDefend(Hit hit) {
        Player player = (Player) target;
        NPC vetion = (NPC) entity;
        if (player != null) {
            if (VetionMinion.houndCount.size() == 0) {
                vetion.message("My hounds! I'll make you pay for that!");
            }
            if (!hasAttacked) {
                vetion.getCombat().setTarget(player);
            }
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.face(null);

        if (!withinDistance(8)) {
            return false;
        }

        if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
            spawnHellhounds((NPC) entity, target);
        }

        if (Utils.percentageChance(50)) {
            magicSwordRaise();
        } else if (Utils.percentageChance(50)) {
            magicSwordSlash();
        } else {
            doShieldBash();
        }

        return true;
    }

    @Override
    public EntityCombatBuilder combatBuilder(Entity entity) {
        return new EntityCombatBuilder(entity, target);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public void doFollowLogic() {
        NPC vetion = (NPC) entity;
        vetion.face(null);
        if (hasAttacked) {
            vetion.stepAbs(target.getX(), target.getY(), MovementQueue.StepType.REGULAR);
        }
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }

    void magicSwordRaise() {
        NPC vetion = (NPC) entity;
        Player player = (Player) target;

        //create list for tiles to store
        List<Tile> tileList = new ArrayList<>();
        var targetTile = player.tile();

        //add the players tile
        tileList.add(targetTile);

        //add the player tile to the used tile
        usedTiles.add(targetTile);

        for (int tileIndex = 0; tileIndex < 4; tileIndex++) {
            //create field to find tiles around
            var tilesAround = World.getWorld().randomTileAround(targetTile, 4);

            //add used tiles to not create duplicates
            usedTiles.add(tilesAround);
            tileList.add(tilesAround);
        }

        //handle the list
        tileList
            .stream()
            .filter(tile -> World.getWorld().clipAt(tile.x, tile.y, tile.level) == 0)
            .filter(tile -> !tile.equals(vetion.tile()))
            .forEach(tile ->
                Chain.noCtx()
                    .runFn(1, () -> {
                        vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                        vetion.lockMoveDamageOk();
                        vetion.getMovementQueue().clear();
                    })
                    .then(1, () -> {
                        vetion.animate(9969);
                        vetion.graphic(vetion.id() == 6612 ? 2345 : 2344, GraphicHeight.MIDDLE, 0);
                        World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, tile, 0, 0);
                    })
                    .then(3, () -> {
                        if (!player.tile().equals(tile) && !player.tile().inSqRadius(tile, 1)) {
                            return;
                        }
                        if (!player.dead() && player.isRegistered() && !vetion.dead() && player.tile().equals(tile)) {
                            player.hit(vetion, Utils.random(15, 30), 0);
                        } else if (player.tile().inSqRadius(tile, 1)) {
                            player.hit(vetion, Utils.random(15, 30) / 2, 0);
                        }
                    })
                    .then(2, () -> {
                        vetion.unlock();
                        vetion.getCombat().setTarget(player);
                        vetion.face(null);
                        hasAttacked = true;
                        usedTiles.clear();
                        tiles.clear();
                    }));

    }

    void magicSwordSlash() {
        NPC vetion = (NPC) entity;
        Player player = (Player) target;

        //create list for tiles to store
        List<Tile> tileList = new ArrayList<>();
        var targetTile = player.tile();

        //add the players tile
        tileList.add(targetTile);

        //add the player tile to the used tile
        usedTiles.add(targetTile);

        for (int tileIndex = 0; tileIndex < 4; tileIndex++) {
            //create field to find tiles around
            var tilesAround = World.getWorld().randomTileAround(targetTile, 4);

            //add used tiles to not create duplicates
            usedTiles.add(tilesAround);
            tileList.add(tilesAround);
        }

        //handle the list
        tileList
            .stream()
            .filter(tile -> World.getWorld().clipAt(tile.x, tile.y, tile.level) == 0)
            .filter(tile -> !tile.equals(vetion.tile()))
            .forEach(tile ->
                Chain.noCtx().runFn(1, () -> {
                        vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                        vetion.lockMoveDamageOk();
                        vetion.getMovementQueue().clear();
                    })
                    .then(1, () -> {
                        vetion.animate(9972);
                        vetion.graphic(2348);
                        World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, tile, 0, 0);
                    })
                    .then(3, () -> {
                        if (!player.tile().equals(tile) && !player.tile().inSqRadius(tile, 1)) {
                            return;
                        }
                        if (!player.dead() && player.isRegistered() && !vetion.dead() && player.tile().equals(tile)) {
                            player.hit(vetion, Utils.random(15, 30), 0);
                        } else if (player.tile().inSqRadius(tile, 1)) {
                            player.hit(vetion, Utils.random(15, 30) / 2, 0);
                        }
                    })
                    .then(2, () -> {
                        vetion.unlock();
                        vetion.getCombat().setTarget(player);
                        vetion.face(null);
                        hasAttacked = true;
                        usedTiles.clear();
                        tiles.clear();
                    }));
    }

    private void doShieldBash() {
        NPC vetion = (NPC) entity;
        Player player = (Player) target;
        Chain.noCtx().runFn(1, () -> {
            vetion.forceChat(Utils.randomElement(VETION_QUOTES));
            vetion.setPositionToFace(player.tile());
            vetion.lockMoveDamageOk();
            vetion.getMovementQueue().clear();
            var dir = Direction.resolveForLargeNpc(player.tile(), vetion.npc());
            spawnShieldInDir(this, vetion.tile(), dir);
        }).then(2, () -> {
            vetion.animate(9974);
        }).then(1, () -> {
            for (var t : tiles) {
                if (t.equals(player.tile())) {
                    if (!player.dead() && player.isRegistered() && !vetion.dead()) {
                        player.hit(vetion, Utils.random(15, 30), 0);
                    }
                }
            }
        }).then(2, () -> {
            vetion.unlock();
            vetion.getCombat().setTarget(player);
            vetion.face(null);
            hasAttacked = true;
            tiles.clear();
        });
    }

    private void spawnHellhounds(NPC vetion, Entity target) {
        List<NPC> minions = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            VetionMinion minion = new VetionMinion(vetion, target);
            minions.add(minion);
            World.getWorld().registerNpc(minion);
            minion.face(target);
            minion.getCombat().setTarget(target);
        }

        vetion.animate(9976);
        vetion.forceChat(vetion.id() == 6611 ? "Gah! Hounds! get them!" : "HOUNDS! DISPOSE OF THESE TRESSPASSERS!");

        vetion.putAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED, true);
        vetion.putAttrib(AttributeKey.MINION_LIST, minions);
    }

    public boolean transform(Entity entity) {
        NPC purpleVetion = (NPC) entity;
        if (purpleVetion.npc().id() == 6611) {
            if (purpleVetion.hp() == 0 || purpleVetion.dead() && !purpleVetion.<Boolean>getAttribOr(AttributeKey.VETION_REBORN_ACTIVE, false)) {
                Chain.noCtx().runFn(2, () -> {
                    purpleVetion.lockNoDamage();
                    purpleVetion.canAttack(false);
                    purpleVetion.message("Now.. DO IT AGAIN!!!");
                    purpleVetion.transmog(6612);
                    purpleVetion.animate(9979);
                    purpleVetion.npc().def(World.getWorld().definitions().get(NpcDefinition.class, 6612));
                    purpleVetion.heal(purpleVetion.maxHp());
                    purpleVetion.getTimers().register(TimerKey.VETION_REBORN_TIMER, 500);
                    purpleVetion.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, true);
                }).then(1, () -> {
                    purpleVetion.unlock();
                    purpleVetion.canAttack(true);
                });
                return true;
            }
        } else if (purpleVetion.npc().id() == 6612) {
            purpleVetion.animate(9980);
            purpleVetion.getTimers().cancel(TimerKey.VETION_REBORN_TIMER);
            purpleVetion.clearAttrib(AttributeKey.VETION_REBORN_ACTIVE);
            Chain.noCtx().runFn(5, () -> {
                purpleVetion.animate(-1);
                purpleVetion.remove();
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        if (hit.getTarget().isNpc()) {
            return transform(hit.getTarget());
        }
        return true;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

    @Override
    public ArrayList<Entity> getPossibleTargets(Entity mob) {
        return Arrays.stream(mob.closePlayers(64)).collect(Collectors.toCollection(ArrayList::new));
    }


    public static void spawnShieldInDir(Vetion vetion, Tile origin, Direction dir) {
        var PATTERNS = new int[][][]{ // player is [NESW] of vetion
            // NESW
            new int[][]{
                new int[]{-2, 4}, new int[]{-1, 4}, new int[]{0, 4}, new int[]{1, 4}, new int[]{2, 4}, new int[]{3, 4}, new int[]{4, 4}
                , new int[]{-1, 3}, new int[]{0, 3}, new int[]{1, 3}, new int[]{2, 3}, new int[]{3, 3}
            },
            new int[][]{
                new int[]{4, 4}, new int[]{4, 3}, new int[]{4, 2}, new int[]{4, 1}, new int[]{4, 0}, new int[]{4, -1}, new int[]{4, -2}
                , new int[]{3, 3}, new int[]{3, 2}, new int[]{3, 1}, new int[]{3, 0}, new int[]{3, -1}
            },
            new int[][]{
                new int[]{-2, -2}, new int[]{-1, -2}, new int[]{0, -2}, new int[]{1, -2}, new int[]{2, -2}, new int[]{3, -2}, new int[]{4, -2}
                , new int[]{-1, -1}, new int[]{0, -1}, new int[]{1, -1}, new int[]{2, -1}, new int[]{3, -1}
            },
            new int[][]{
                new int[]{-2, -2}, new int[]{-2, -1}, new int[]{-2, 0}, new int[]{-2, 1}, new int[]{-2, 2}, new int[]{-2, 3}, new int[]{-2, 4}
                , new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1}, new int[]{-1, 2}, new int[]{-1, 3}
            }
        };
        if (dir.ordinal() <= 3) {
            int[][] pattern = PATTERNS[dir.ordinal()];
            if (pattern.length == 0)
                return;
            for (int[] offset : pattern) {
                if (offset == null || offset.length == 0)
                    break;
                var pos = origin.transform(offset[0], offset[1]);
                vetion.tiles.add(pos); // you were missing side ones
                World.getWorld().tileGraphic(2236, pos, 0, 0);
                World.getWorld().tileGraphic(2184, pos, 0, 90);
                //World.getWorld().tileGraphic(2349, pos, 0, 30);
            }
        } else if (dir.ordinal() >= 4 && dir.ordinal() <= 7) {
            Area[][] PATTERNS2 = new Area[][]{
                new Area[]{new Area(-2, 1, -1, 4), new Area(-2, 3, 1, 4),},
                new Area[]{new Area(3, 1, 4, 4), new Area(1, 3, 4, 4),},
                new Area[]{new Area(1, -2, 4, -1), new Area(3, -2, 4, 1),},
                new Area[]{new Area(-2, -2, -1, 1), new Area(-2, -2, 1, -1),},
            };
            Area[] pattern = PATTERNS2[dir.ordinal() - 4];
            if (pattern.length == 0)
                return;
            for (Area area : pattern) {
                area.forEachPos(t -> {
                    var pos = origin.transform(t.x, t.y);
                    vetion.tiles.add(pos);
                });
            }
            for (Tile tile : vetion.tiles) {
                World.getWorld().tileGraphic(2236, tile, 0, 0);
                World.getWorld().tileGraphic(2184, tile, 0, 90);
            }
        }
    }
}
