package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.cache.definitions.ObjectDefinition;
import com.aelous.model.World;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.RegionManager;
import com.aelous.utility.Color;
import com.aelous.utility.TickDelay;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.GREAT_OLM_LEFT_CLAW_7555;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.GREAT_OLM_RIGHT_CLAW_7553;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;
import static com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm.Facing.*;
import static com.aelous.model.entity.attributes.AttributeKey.OLM_BURN_EFFECT;
import static com.aelous.model.entity.attributes.AttributeKey.VENOMED_BY;

/**
 * @author Sharky
 * @Since December 24, 2022
 */
public class GreatOlm extends CommonCombatMethod {

    private static final Projectile CRYSTAL_DROP_PROJECTILE = new Projectile(1357, 150, 0, 0, 135, 0, 0, 0);
    private static final Projectile CRYSTAL_BOMB_PROJECTILE = new Projectile(1357, 100, 0, 30, 100, 0, 16, 0);
    private static final Projectile CRYSTAL_SPIKE_PROJECTILE = new Projectile(1352, 200, 0, 0, 30, 0, 0, 0);

    private static final Projectile ACID_POOL_PROJECTILE = new Projectile(1354, 100, 0, 30, 100, 0, 16, 0);
    private static final Projectile ACID_DRIP_PROJECTILE = new Projectile(1354, 100, 43, 30, 25, 6, 16, 0);

    private static final Projectile BURN_PROJECTILE = new Projectile(1350, 100, 43, 35, 20, 6, 16, 192);
    private static final Projectile FLAME_WALL_PROJECTILE_1 = new Projectile(1347, 100, 0, 15, 55, 0, 16, 127);
    private static final Projectile FLAME_WALL_PROJECTILE_2 = new Projectile(1348, 0, 0, 0, 30, 0, 16, 0);

    private static final Projectile SIPHON_PROJECTILE = new Projectile(1355, 100, 0, 30, 100, 0, 16, 0);
    private static final Projectile MAGIC_SPHERE = new Projectile(1341, 80, 43, 30, 150, 0, 16, 192);
    private static final Projectile RANGED_SPHERE = new Projectile(1343, 80, 43, 30, 150, 0, 16, 192);
    private static final Projectile MELEE_SPHERE = new Projectile(1345, 80, 43, 30, 150, 0, 16, 192);

    AtomicBoolean turning = new AtomicBoolean();

    /**
     * Converts coordinates
     */
    public Tile getTile(int localX, int localY) {
        return entity.tile().regionCorner().transform(0, 0, entity.getZ()).relative(localX, localY);
    }

    public NPC npc, rightClaw, leftClaw;

    public Party party;

    @Override
    public void init(NPC npc1) {
        World.getWorld().definitions().get(ObjectDefinition.class, FIRE_32297).clipType = 1; // force flame wall fire to clip tiles

        npc = entity.npc();
        npc.putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN, 40);
        northTargetBounds = new Area(getTile(RIGHT.swX, RIGHT.swY), getTile(RIGHT.neX, RIGHT.neY), npc.getZ()); // no debug
        centerTargetBounds = new Area(getTile(CENTER.swX, CENTER.swY), getTile(CENTER.neX, CENTER.neY), npc.getZ()); // somehow this is null
        southTargetBounds = new Area(getTile(LEFT.swX, LEFT.swY), getTile(LEFT.neX, LEFT.neY), npc.getZ());
        arenaBounds = new Area(getTile(28, 35), getTile(37, 51), npc.getZ());
        lastPhase = 2; //0,1,2  = 3 phases default
        Chain.noCtx().repeatingTask(5, t -> {
            List<Player> allTargets = getAllTargets(); // wait until a player is available
            if (allTargets.size() == 0) {
                return;
            }
            party = npc1.getAttrib(AttributeKey.RAID_PARTY);
            rightClaw = party.monsters.stream().filter(e -> e.id() == GREAT_OLM_RIGHT_CLAW_7553).findFirst().get();
            leftClaw = party.monsters.stream().filter(e -> e.id() == GREAT_OLM_LEFT_CLAW_7555).findFirst().get();
            rightClaw.putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN, 40);
            leftClaw.putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN, 40);
            rightClaw.setCombatMethod(new CommonCombatMethod() {

                @Override
                public boolean prepareAttack(Entity entity, Entity target) {
                    return true;
                }

                @Override
                public int getAttackSpeed(Entity entity) {
                    return 0;
                }

                @Override
                public int getAttackDistance(Entity entity) {
                    return 0;
                }

                @Override
                public void onDeath(Player killer, NPC npc) {
                    clawDeathStart(npc);
                    Chain.bound(null).runFn(npc.getCombatInfo().deathlen, () -> {
                        clawDeathEnd(npc);
                    });
                }

                @Override
                public void preDefend(Hit hit) {
                    preRightClawDefend(hit);
                }

                @Override
                public void postDamage(Hit hit) {
                    // none
                }
            });
            leftClaw.setCombatMethod(new CommonCombatMethod() {

                @Override
                public boolean prepareAttack(Entity entity, Entity target) {
                    return true;
                }

                @Override
                public int getAttackSpeed(Entity entity) {
                    return 0;
                }

                @Override
                public int getAttackDistance(Entity entity) {
                    return 0;
                }

                @Override
                public void onDeath(Player killer, NPC npc) {
                    clawDeathStart(npc);
                    Chain.bound(null).runFn(npc.getCombatInfo().deathlen, () -> {
                        clawDeathEnd(npc);
                    });
                }

                @Override
                public void preDefend(Hit hit) {
                    preLeftClawDefend(hit);
                }

                @Override
                public void postDamage(Hit hit) {
                    postLeftClawDamage(hit);
                }
            });
            rise();
            t.stop();
        });
        startAcidPoolEvent(npc);
    }

    @Override
    public void postDamage(Hit hit) {
        if (hit.getDamage() == 0 || hit.splatType == SplatType.NPC_HEALING_HITSPLAT || entity.dead())
            return;
        if (currentPhase != lastPhase || !leftClaw.dead() || !rightClaw.dead()) {
            entity.healHit(entity, hit.getDamage(), 3);
        }
    }

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        if (getObject(npc).getId() != 29881) // hasnt risen yet
            return false;
        if (turning.get()) {
            return false;
        }
        attackCounter++;
        if ((attackCounter % 10) == 1) {
            if (++specialCounter == (isEmpowered() ? 10 : 9))
                specialCounter = 1;
        }

        Party party = target.player().raidsParty;
        List<Player> targets = getFacingTargets();
        //System.out.println("targets size : " + targets.size());
        // if everyone runs between both sides, olm spends all the time turning and no time attacking.
        if (justTurned && targets.size() == 0)
            targets = Arrays.stream(npc.closePlayers(32))
                .collect(Collectors.toList());
        var headRunnerNotInDirection = false;
        if (isOnEastSide()) {
            headRunnerNotInDirection = (facing == RIGHT && target.tile().y < 5741) || (facing == LEFT && target.tile().y > 5739) || (facing == CENTER && target.tile().y != 5740);
        } else {
            headRunnerNotInDirection = (facing == LEFT && target.tile().y < 5741) || (facing == RIGHT && target.tile().y > 5739) || (facing == CENTER && target.tile().y != 5740);
        }
        if (targets.size() == 0 || (!justTurned && headRunnerNotInDirection)) {
            turn();
            justTurned = true;
            return false;
        }
        justTurned = false;

        int attackType = (attackCounter - 1) % 4;
        if (attackType == 0 || attackType == 2) {
            if (finalStand && !siphonDelay.isDelayed() && Utils.rollPercent(15)) {
                siphonAttack(npc);
                siphonDelay.delay(20);
            } else {
                PhasePower power = isEmpowered() ? World.getWorld().get(PhasePower.values()) : phasePower;
                if (power == PhasePower.ACID && Utils.rollPercent(15)) {
                    if (Utils.rollPercent(30) && Utils.rollPercent(50))
                        acidPoolsAttack(npc, party);
                    else
                        acidDrip(npc);
                } else if (power == PhasePower.FLAME && Utils.rollPercent(20)) {
                    if (Utils.rollPercent(30) && Utils.rollPercent(50))
                        flameWall(npc);
                    else
                        burnAttack(npc);
                } else if (power == PhasePower.CRYSTAL && Utils.rollPercent(15)) {
                    if (Utils.rollPercent(30) && Utils.rollPercent(50))
                        crystalBomb(npc, party);
                    else
                        crystalMark(npc);
                } else {
                    if (World.getWorld().get() < 0.93)
                        basicAttack(npc, targets);
                    else
                        sphereAttack(npc, targets);
                }
            }
        } else if (attackType == 1 && !clenched) {
            int specialType = (specialCounter - 1) % (isEmpowered() ? 9 : 8);
            if (specialType == 4)
                crystalBurst(npc);
            else if (specialType == 5)
                lightningAttack(npc);
            else if (specialType == 6)
                swapAttack(npc);
            else if (currentPhase > 1 && (specialType == 2 || specialType == 8))
                clawHealing();
            else
                basicAttack(npc, targets);
        }
        if (attackType == 2) {
            attackCounter = 0;
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity mob) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 64;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

    @Override
    public void doFollowLogic() {

    }

    @Override
    public void onDeath(Player killer, NPC npc) {
        Party party = killer.raidsParty;
        olmDeathStart();
        Chain.bound(null).runFn(npc.getCombatInfo().deathlen, () -> {
            olmDeathEnd(party);
        });
    }

    private CombatType lastBasicAttackStyle = Utils.rollPercent(50) ? CombatType.MAGIC : CombatType.RANGED;
    private PhasePower phasePower = null;
    private boolean finalStand = false;
    private final TickDelay siphonDelay = new TickDelay();
    private int clenchDamageCounter = 0;
    private boolean clenched;
    private boolean clawHealing;
    private int attackCounter = 0;
    private int specialCounter = 0;
    private int currentPhase = 0;
    private int lastPhase;

    public boolean justTurned;

    private boolean isEmpowered() {
        return currentPhase == lastPhase;
    }

    public void acidDrip(NPC npc) {
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);
        List<Player> potentialTargets = getFacingTargets();
        if (potentialTargets.size() == 0)
            return;
        Player target = World.getWorld().get(potentialTargets);
        ACID_DRIP_PROJECTILE.send(npc, target);
        target.message(Color.RED.wrap("The Great Olm has smothered you in acid. It starts to drip off slowly."));

        Chain.noCtx().delay(3, () -> {
            AtomicInteger maxLoops = new AtomicInteger();
            AtomicInteger sleepFor = new AtomicInteger();
            Chain.noCtx().repeatingTask(1, t -> {
                if (npc.dead() || !npc.isRegistered()) t.stop();
                if (sleepFor.getAndDecrement() > 0)
                    return; // replacement for event.delay inside a loop
                if (maxLoops.getAndIncrement() < 20) {
                    if (Tile.getObject(30032, target.getAbsX(), target.getAbsY(), target.getZ(), 10, -1) != null) {
                        sleepFor.incrementAndGet();
                        return;
                    }
                    Tile tile = target.getPreviousTile().copy();
                    if (tile.distance(target.tile()) > 16) {
                        //teleported or something
                        return;
                    }
                    if (tile.equals(target.tile())) {
                        if (Tile.getObject(30032, tile.getX(), tile.getY(), tile.getZ(), 10, -1) == null) {
                            spawnAcidPool(npc, tile);
                        }
                        sleepFor.incrementAndGet();
                        return;
                    }

                    for (int i = 0; i < 2; i++) {
                        if (Tile.getObject(30032, tile.getX(), tile.getY(), tile.getZ(), 10, -1) == null) {
                            spawnAcidPool(npc, tile);
                        }
                        if (tile.equals(target.tile())) {
                            return;
                        }
                        Direction dir = Direction.getDirection(tile, target.tile());
                        tile.transform(dir.x, dir.y, 0);
                    }
                }
            });
        });
    }

    private static final int[] BURN_STAT_DRAIN = {Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC};

    public void burnPlayer(NPC npc, Player player, boolean wasSpread) {
        if (!player.<Boolean>getAttribOr(AttributeKey.OLM_BURN_EFFECT, false))
            return;
        if (wasSpread)
            player.forceChat("I will burn with you!");

        player.putAttrib(OLM_BURN_EFFECT, true);
        for (int i = 0; i < 5; i++) {
            if (!player.tile().inBounds(arenaBounds))
                return;
            if (!wasSpread || i > 0)
                player.forceChat("Burn with me!");
            player.hit(npc, 5);
            for (int type : BURN_STAT_DRAIN)
                player.skills().alterSkill(type, -2);

            for (Player p : npc.closePlayers(32)) {
                if (p.tile().isWithinDistance(player.tile(), 1))
                    burnPlayer(npc, p, true);
            }
            Chain.noCtx().delay(i * 8, () -> {
                player.clearAttrib(OLM_BURN_EFFECT);
            });
        }
    }

    public void burnAttack(NPC npc) {
        List<Player> potentialTargets = getFacingTargets();
        if (potentialTargets.size() == 0)
            return;
        Player target = World.getWorld().get(potentialTargets);
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);
        BURN_PROJECTILE.send(npc, target);

        Chain.noCtx().delay(2, () -> {
            burnPlayer(npc, target, false);
        });

    }


    public void basicAttack(NPC npc, List<Player> targets) {
        Chain.bound(npc).runFn(1, () -> {
            animate(npc, facing.getAttackAnim(isEmpowered()));
            lastBasicAttackStyle = Utils.rollPercent(75) ? lastBasicAttackStyle : (lastBasicAttackStyle == CombatType.RANGED ? CombatType.MAGIC : CombatType.RANGED);
            targets.forEach(p -> {
                var tileDist = entity.tile().distance(target.tile());
                int duration = lastBasicAttackStyle == CombatType.RANGED ? (41 + 11 + (5 * tileDist)) : (51 + -5 + (10 * tileDist));
                Projectile projectile = new Projectile(npc, p, lastBasicAttackStyle == CombatType.RANGED ? 1340 : 1339, lastBasicAttackStyle == CombatType.RANGED ? 41 : 51, duration, 80, 31, 0, 1, lastBasicAttackStyle == CombatType.RANGED ? 5 : 10);
                final int delay = entity.executeProjectile(projectile);
                int maxDamage = npc.getCombatInfo().maxhit;
                if (Prayers.usingPrayer(p, lastBasicAttackStyle == CombatType.RANGED ? Prayers.PROTECT_FROM_MISSILES : Prayers.PROTECT_FROM_MAGIC))
                    maxDamage /= 4;
                Hit hit = p.hit(npc, maxDamage, lastBasicAttackStyle).clientDelay(delay).checkAccuracy();
                hit.submit();
            });
        }).then(1, () -> delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1));
    }

    public void sphereAttack(NPC npc, List<Player> targets) {
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);
        for (int i = 0; i < 3 && targets.size() > 0; i++) {
            Player target = targets.remove(World.getWorld().get(targets.size() - 1));
            CombatType style = World.getWorld().get() < 1d / 3 ? CombatType.MAGIC : (World.getWorld().get() < 1d / 2 ? CombatType.RANGED : CombatType.MELEE);
            String message;
            Projectile projectile;
            int prayer;
            int hitGfx;
            switch (style) {
                case MAGIC -> {
                    message = Color.PURPLE.wrap("The Great Olm fires a sphere of magical power your way.");
                    projectile = MAGIC_SPHERE;
                    hitGfx = 1342;
                    prayer = Prayers.PROTECT_FROM_MAGIC;
                }
                case RANGED -> {
                    message = Color.DARK_GREEN.wrap("The Great Olm fires a sphere of accuracy and dexterity your way.");
                    projectile = RANGED_SPHERE;
                    hitGfx = 1344;
                    prayer = Prayers.PROTECT_FROM_MISSILES;
                }
                case MELEE -> {
                    message = Color.RED.wrap("The Great Olm fires a sphere of aggression your way.");
                    projectile = MELEE_SPHERE;
                    hitGfx = 1346;
                    prayer = Prayers.PROTECT_FROM_MELEE;
                }
                default -> {
                    return;
                }
            }
            if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MISSILES) || Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC) || Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MELEE)) {
                target.skills().alterSkill(Skills.PRAYER, target.skills().level(Skills.PRAYER) / 2);
                Prayers.closeAllPrayers(target);
                message += " Your prayers have been sapped.";
            }
            int delay = projectile.send(npc, target);
            target.graphic(hitGfx, GraphicHeight.HIGH, projectile.getSpeed());
            target.message(message);

            Chain.noCtx().delay(4, () -> {
                if (!Prayers.usingPrayer(target, prayer)) {
                    target.hit(npc, World.getWorld().random((Math.min(200, target.hp() / 2))), delay);
                }
            });

        }
    }

    public void crystalBurst(NPC npc) {
        if (leftClaw.dead() || clenched)
            return;
        Chain.bound(npc).runFn(1, () -> {
            animate(leftClaw, 7356);
        }).then(1, () -> {
            delayedAnimation(leftClaw, 7355, 2);
        });
        getAllTargets().forEach(p -> {
            Tile pos = p.tile().copy();
            GameObject crystal = GameObject.spawn(30033, pos, 10, 0);
            Chain.noCtx().runFn(3, () -> {
                crystal.setId(LARGE_CRYSTALS);
                if (p.tile().equals(pos))
                    p.hit(npc, World.getWorld().random(npc.getCombatInfo().maxhit));
            }).delay(1, crystal::remove);
        });
    }

    public void lightningAttack(NPC npc) {
        if (leftClaw.dead() || clenched)
            return;
        Chain.bound(npc).runFn(1, () -> {
            animate(leftClaw, 7358);
        }).then(1, () -> {
            delayedAnimation(leftClaw, 7355, 2);
        });
        for (int x = 30; x <= 36; x++) {
            if (!Utils.rollPercent(45))
                continue;
            int yStep = Utils.rollPercent(50) ? -1 : 1;
            final Tile[] lightningPos = {getTile(x, yStep == -1 ? 52 : 35)};
            Chain.noCtx().repeatingTask(1, t -> {
                if (npc.dead() || !npc.isRegistered()) t.stop();
                if (t.getRunDuration() == 17)
                    t.stop();
                World.getWorld().tileGraphic(1356, lightningPos[0], 0, 0);
                forAllTargets(player -> {
                    if (player.isAt(lightningPos[0])) {
                        player.hit(npc, World.getWorld().random(npc.getCombatInfo().maxhit / 2));
                        player.stun(2, true);
                        CombatFactory.disableProtectionPrayers(player, true, true);
                        player.message(Color.RED.wrap("You've been electrocuted to the spot!"));
                    }
                });
                lightningPos[0] = lightningPos[0].transform(0, yStep, 0);

            });
        }
    }

    public void swapAttack(NPC npc) {
        if (leftClaw.dead() || clenched)
            return;
        Chain.bound(npc).runFn(1, () -> {
            animate(leftClaw, 7359);
        }).then(1, () -> {
            delayedAnimation(leftClaw, 7355, 2);
        });
        LinkedList<Player> targets = new LinkedList<>(getAllTargets());
        for (int i = 0; i < 4; i++) {
            if (targets.isEmpty())
                return;
            Player player = targets.pop();
            Player other = null;
            Tile tile = null;
            if (!targets.isEmpty())
                other = targets.pop();
            if (other != null) {
                player.message("The Great Olm has paired you with " + Color.RED.wrap(other.getDisplayName()) + "! The magical power will enact soon...");
                other.message("The Great Olm has paired you with " + Color.RED.wrap(player.getDisplayName()) + "! The magical power will enact soon...");
            } else {
                player.message("The Great Olm had no one to pair you with! The magical power will enact soon...");
                tile = centerTargetBounds.randomTile();
            }
            int gfxId = 1359 + i;
            Player finalOther = other;
            Tile finalTile = tile;

            AtomicInteger ticks = new AtomicInteger();
            Chain.noCtx().repeatingTask(2, t -> {
                if (npc.dead() || !npc.isRegistered()) t.stop();
                if (ticks.getAndIncrement() < 4) {
                    player.graphic(gfxId, GraphicHeight.LOW, 0);
                    if (finalOther != null)
                        finalOther.graphic(gfxId, GraphicHeight.LOW, 0);
                    else
                        World.getWorld().tileGraphic(gfxId, finalTile, 0, 0);
                } else {
                    t.stop();
                }
            }).then(() -> {
                int distance = player.tile().distance(finalOther != null ? finalOther.tile() : finalTile);
                if (distance > 20)
                    return;
                if (distance == 0) {
                    player.message("The teleport attack has no effect!");
                    if (finalOther != null)
                        finalOther.message("The teleport attack has no effect!");
                } else {
                    player.teleport(finalOther != null ? finalOther.tile() : finalTile);
                    player.hit(npc, World.getWorld().random(distance * 4));
                    if (finalOther != null) {
                        finalOther.teleport(player.tile());
                        finalOther.hit(npc, World.getWorld().random(distance * 4));
                    }
                }
            });


        }
    }

    public void clawHealing() {
        if (leftClaw.dead() || clenched || clawHealing)
            return;
        animate(leftClaw, 7357);
        clawHealing = true;

        Chain.noCtx().delay(15, () -> {
            clawHealing = false;
            animate(leftClaw, 7355);
        });

    }

    //phase specific attacks
    public void crystalBomb(NPC npc, Party party) {
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);
        int bombCount = 1;
        for (int i = 0; i < bombCount; i++) {

            Tile bombPos = arenaBounds.randomTile();
            CRYSTAL_BOMB_PROJECTILE.send(npc, bombPos);
            Chain.noCtx().delay(5, () -> {
                GameObject bomb = GameObject.spawn(29766, bombPos, 10, 0);
                Chain.noCtx().delay(8, () -> {
                    bomb.remove();
                    World.getWorld().tileGraphic(40, bombPos, 0, CRYSTAL_BOMB_PROJECTILE.getSpeed());
                    forAllTargets(p -> {
                        int distance = p.tile().distance(bombPos);
                        if (distance > 3)
                            return;
                        p.hit(npc, World.getWorld().random(60 - (distance * 10)));
                    });
                });
            });

        }
    }

    public void crystalMark(NPC npc) {
        forAllTargets(p -> p.message("The Great Olm sounds a cry..."));
        List<Player> potentialTargets = getFacingTargets();
        if (potentialTargets.size() == 0)
            return;
        Player target = World.getWorld().get(potentialTargets);
        target.message(Color.RED.wrap("The Great Olm has chosen you as its target - watch out!"));
        target.graphic(246);

        AtomicInteger crystals = new AtomicInteger();
        AtomicInteger sleepFor = new AtomicInteger();
        Chain.noCtx().repeatingTask(1, t -> {
            if (npc.dead() || !npc.isRegistered()) t.stop();
            if (sleepFor.getAndDecrement() > 0)
                return; // replacement for event.delay inside a loop
            if (crystals.getAndIncrement() > 10) {
                t.stop();
                return;
            }
            target.graphic(246);
            Tile pos = target.tile().copy();
            sleepFor.addAndGet(6);
            Chain.noCtx().delay(1, () -> {
                target.graphic(246);
                int delay = CRYSTAL_SPIKE_PROJECTILE.send(pos.relative(0, 1), pos);
                getAllTargets().forEach(p -> {
                    if (p.tile().equals(pos))
                        p.hit(npc, World.getWorld().random(10, 15), delay);
                });
                World.getWorld().tileGraphic(1353, pos, 0, CRYSTAL_SPIKE_PROJECTILE.getSpeed());
            }).delay(3, () -> {
                target.graphic(246);
                getAllTargets().forEach(p -> {
                    if (p.tile().equals(pos))
                        p.hit(npc, World.getWorld().random(5, 10));
                });
            });
        });

    }

    private final List<GameObject> fires = new ArrayList<>(20);

    public void flameWall(NPC npc) {
        List<Player> potentialTargets = getFacingTargets();
        if (potentialTargets.size() == 0)
            return;
        Player target = World.getWorld().get(potentialTargets);
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);
        int targetY = target.tile().getY();
        int localY = targetY & 63;
        if (localY <= 36 || localY >= 51) // fail
            return;
        int projectileX = isOnEastSide() ? npc.spawnTile().getX() - 10 : npc.spawnTile().getX() - 1;
        Tile src1 = new Tile(projectileX, targetY + 1, npc.getZ());
        FLAME_WALL_PROJECTILE_1.send(npc, src1);
        Tile src2 = new Tile(projectileX, targetY - 1, npc.getZ());
        FLAME_WALL_PROJECTILE_1.send(npc, src2);

        Chain.noCtx().delay(1, () -> {
            if (isOnEastSide()) {
                for (int x = projectileX; x < projectileX + 10; x++) {
                    FLAME_WALL_PROJECTILE_2.send(src1.getX(), src1.getY(), x, targetY + 1, src1.getZ());
                    FLAME_WALL_PROJECTILE_2.send(src2.getX(), src2.getY(), x, targetY - 1, src2.getZ());
                }
            } else {
                for (int x = projectileX; x > projectileX - 10; x--) {
                    FLAME_WALL_PROJECTILE_2.send(src1.getX(), src1.getY(), x, targetY + 1, src1.getZ());
                    FLAME_WALL_PROJECTILE_2.send(src2.getX(), src2.getY(), x, targetY - 1, src2.getZ());
                }
            }
        }).delay(1, () -> {
            if (isOnEastSide()) {
                for (int x = projectileX; x < projectileX + 10; x++) {
                    fires.add(GameObject.spawn(FIRE_32297, x, targetY + 2, npc.getZ(), 10, 0));
                    fires.add(GameObject.spawn(FIRE_32297, x, targetY - 2, npc.getZ(), 10, 0));
                }
            } else {
                for (int x = projectileX; x > projectileX - 10; x--) {
                    fires.add(GameObject.spawn(FIRE_32297, x, targetY + 2, npc.getZ(), 10, 0));
                    fires.add(GameObject.spawn(FIRE_32297, x, targetY - 2, npc.getZ(), 10, 0));
                }
            }
        }).delay(10, () -> {
            getAllTargets().forEach(p -> {
                if (p.getAbsY() == targetY)
                    p.hit(npc, World.getWorld().random(35, 60));
            });
        }).delay(1, () -> {
            fires.forEach(GameObject::remove);
        });

    }

    public void startAcidPoolEvent(NPC npc) {

        Chain.noCtx().repeatingTask(1, t -> {
            if (npc.dead() || !npc.isRegistered()) t.stop();
            forAllTargets(p -> {
                if (Tile.getObject(30032, p.getAbsX(), p.getAbsY(), p.getZ(), 10, -1) != null) {
                    p.hit(npc, World.getWorld().random(3, 6), SplatType.POISON_HITSPLAT);
                    p.poison(4);
                }
            });
        });

    }

    public void spawnAcidPool(NPC npc, Tile tile) {
        GameObject pool = GameObject.spawn(30032, tile, 10, 0);
        Chain.noCtx().delay(10, pool::remove);
    }

    public void acidPoolsAttack(NPC npc, Party party) {
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);
        int poisonPools = 6;
        for (int i = 0; i < poisonPools; i++) {
            Tile pos = arenaBounds.randomTile();
            ACID_POOL_PROJECTILE.send(npc, pos);

            Chain.noCtx().delay(1, () -> {
                GameObject pool = GameObject.spawn(30032, pos, 10, 0);
                Chain.noCtx().delay(6, pool::remove);
            });

        }
    }

    public void siphonAttack(NPC npc) {
        animate(npc, facing.getAttackAnim(isEmpowered()));
        delayedAnimation(npc, facing.getIdleAnim(isEmpowered()), 1);

        Tile[] siphons = new Tile[2];
        for (int i = 0; i < siphons.length; i++) {
            siphons[i] = centerTargetBounds.randomTile();
            SIPHON_PROJECTILE.send(npc, siphons[i]);
        }
        Chain.noCtx().delay(4, () -> {
            for (Tile siphon : siphons) {
                World.getWorld().tileGraphic(1363, siphon, 0, 0);
            }
        }).delay(5, () -> {
            int damageDealt = 0;
            for (Player player : getAllTargets()) {
                boolean safe = false;
                for (Tile siphon : siphons) {
                    if (player.tile().equals(siphon)) {
                        safe = true;
                        break;
                    }
                }
                if (!safe) {
                    damageDealt += World.getWorld().random(5, 10);
                    player.hit(npc, damageDealt);
                }
            }
            npc.hit(npc, damageDealt * 3, SplatType.NPC_HEALING_HITSPLAT);
        });

    }

    public void preLeftClawDefend(Hit hit) {
        if (clenched)
            hit.block();
        else if (clawHealing)
            hit.setSplatType(SplatType.NPC_HEALING_HITSPLAT);
        else if (hit.getCombatType() != null && !hit.getCombatType().isMelee()) {
            if (hit.getSource() != null && hit.getSource().isPlayer())
                hit.getSource().message("The claw resists your non-melee attack!");
            hit.block();
        }
    }

    public void preRightClawDefend(Hit hit) {
        if (hit.getCombatType() != null && !hit.getCombatType().isMagic()) {
            if (hit.getSource() != null && hit.getSource().isPlayer())
                hit.getSource().message("The claw resists your non-magic attack!");
            hit.block();
        }
    }

    public void postLeftClawDamage(Hit hit) {
        if (hit.getSource().isPlayer()) {
            if (rightClaw.dead() || leftClaw.dead() || clenched || currentPhase == lastPhase)
                return;
            clenchDamageCounter += hit.getDamage();
            if (clenchDamageCounter >= leftClaw.maxHp() / 5) {
                forAllTargets(p -> p.message("The Great Olm's left claw clenches to protect itself temporarily."));
                clenchDamageCounter = 0;
                clenched = true;

                Chain.bound(npc).runFn(1, () -> {
                    animate(leftClaw, 7360);
                }).then(1, () -> {
                    delayedAnimation(leftClaw, 7361, 2);
                });

                Chain.noCtx().delay(20, () -> {
                    forAllTargets(p -> p.message("The Great Olm regains control of its left claw!"));
                    clenched = false;
                    Chain.bound(npc).runFn(1, () -> {
                        animate(leftClaw, 7362);
                    }).then(1, () -> {
                        delayedAnimation(leftClaw, 7355, 2);
                    });
                });

            }
        }
    }

    public void olmDeathStart() {
        animate(npc, 7348);
        if (!fires.isEmpty()) {
            fires.forEach(GameObject::remove);
        }
    }

    public void olmDeathEnd(Party party) {
        forAllTargets(p -> p.getPacketSender().sendCameraNeutrality());
        getObject(npc).setId(LARGE_HOLE_29882);
        party.greatOlmRewardCrystal.setId(ANCIENT_CHEST); // reward chest
        party.greatOlmCrystal.animate(7506);
        if (party.getLeader().getRaids() != null) {
            party.getLeader().getRaids().complete(party);
        }
        Chain.noCtx().delay(2, () -> {
            party.greatOlmCrystal.remove();
        });

        npc.remove();
        leftClaw.remove();
        rightClaw.remove();
        getObject(npc).remove();
        getObject(leftClaw).remove();
        getObject(rightClaw).remove();
    }

    public void clawDeathStart(NPC claw) {
        // make hand object do dying (falling underground) anim
        animate(claw, claw == leftClaw ? 7370 : 7352);
        Chain.noCtx().delay(2, () -> {
            // set object ID to empty hole
            getObject(claw).setId(claw == leftClaw ? LARGE_ROCK_29885 : CRYSTAL_STRUCTURE);
        });
    }

    public void clawDeathEnd(NPC claw) {
        claw.hidden(true);
        if (leftClaw.dead() && rightClaw.dead()) {
            nextPhase();
        } else if (currentPhase == lastPhase) {
            startClawReviveTimer(claw);
        }
    }

    public void startClawReviveTimer(NPC claw) {
        NPC otherClaw = claw == leftClaw ? rightClaw : leftClaw;
        claw.lock();

        Chain.noCtx().delay(2, () -> {
            claw.hidden(false);
            AtomicInteger progress = new AtomicInteger(1);
            Chain.noCtx().repeatingTask(1, t -> {
                if (npc.dead() || !npc.isRegistered()) t.stop();
                if (progress.getAndIncrement() <= 25 && !otherClaw.dead()) {
                    claw.getUpdateFlag().flag(Flag.FIRST_SPLAT);
                }
                t.stop();
            }).then(() -> {
                if (!otherClaw.dead() && progress.get() >= 25) { // failed, revive claw
                    restore(claw);
                    getObject(claw).setId(claw == leftClaw ? LARGE_ROCK_29883 : CRYSTALLINE_STRUCTURE);
                    Chain.noCtx().delay(1, () -> {
                        animate(claw, claw == leftClaw ? 7354 : 7350);
                    }).delay(5, () -> {
                        getObject(claw).setId(claw == leftClaw ? LARGE_ROCK_29884 : CRYSTALLINE_STRUCTURE_29887);
                    });
                } else {
                    animate(npc, facing.getIdleAnim(isEmpowered()));
                    claw.hidden(true);
                }
            });
            claw.unlock();
        });

    }

    public void ceilingCrystals(NPC npc, int delay, int duration) {

        forAllTargets(p -> p.getPacketSender().shakeCamera(0, 6, 0, 6));

        Chain.noCtx().delay(delay, () -> {
            AtomicInteger ticks = new AtomicInteger();
            Chain.noCtx().repeatingTask(2, t -> {
                if (npc.dead() || !npc.isRegistered()) t.stop();
                if (ticks.get() < duration) {
                    for (int i = 0; i < 2; i++) {
                        Tile tile;
                        if (i == 0 && ticks.get() % 4 == 0 && target != null)
                            tile = target.tile().copy();
                        else
                            tile = arenaBounds.randomTile();
                        Tile src = World.getWorld().get() < 0.5 ? tile.relative(1, 0) : tile.relative(0, 1);
                        Projectile p1 = new Projectile(entity, tile, 1357, 0, 120, 150, 0, 0, 1, 10);
                        final int projDelay = p1.send(src, tile);
                        World.getWorld().tileGraphic(1447, tile, 0, 30);
                        World.getWorld().tileGraphic(1358, tile, 0, p1.getSpeed());
                        Chain.bound(null).runFn(projDelay, () -> forAllTargets(p -> {
                            int distance = p.tile().distance(tile);
                            if (p.tile().isWithinDistance(tile, 1)) {
                                p.hit(npc, World.getWorld().random(distance == 0 ? 30 : 15));
                            }
                        }));
                    }
                    ticks.addAndGet(2);
                } else {
                    forAllTargets(p -> p.getPacketSender().sendCameraNeutrality());
                    t.stop();
                }
            }).then(() -> forAllTargets(p -> p.getPacketSender().sendCameraNeutrality()));
        });
    }

    public void nextPhase() {
        if (npc.locked())
            return;
        if (currentPhase >= lastPhase) {
            if (!finalStand) {
                finalStand = true;
                ceilingCrystals(npc, 0, 2500); // Was 10k
                forAllTargets(p -> p.message("The Great Olm is giving its all. This is its final stand."));
            }
            return;
        }
        npc.lock();
        leftClaw.lock();
        rightClaw.lock();
        ceilingCrystals(npc, 1, 15);// Was 30
        facing = CENTER;

        //go down
        animate(npc, 7348);
        animate(leftClaw, 7370);
        animate(rightClaw, 7352);
        Chain.noCtx().delay(2, () -> {
            if (npc.finished())
                return;
            getObject(npc).setId(LARGE_HOLE_29882);
            getObject(leftClaw).setId(LARGE_ROCK_29885);
            getObject(rightClaw).setId(CRYSTAL_STRUCTURE);
            if (isOnEastSide()) { // to west
                npc.teleport(getTile(23, 42));
                leftClaw.teleport(getTile(23, 47));
                rightClaw.teleport(getTile(23, 37));
            } else {
                npc.teleport(npc.spawnTile());
                leftClaw.teleport(leftClaw.spawnTile());
                rightClaw.teleport(rightClaw.spawnTile());
            }
        }).delay(30, () -> {
            if (currentPhase + 1 != lastPhase) {
                phasePower = World.getWorld().get(PhasePower.values());
                forAllTargets(p -> p.message("The Great Olm rises with the power of " + phasePower.name + "."));
            } else {
                phasePower = null;
            }
            rise();
            restore(leftClaw);
            restore(rightClaw);
            currentPhase++;
        });

    }

    public void restore(NPC claw) {
        claw.heal(claw.maxHp());
        claw.putAttrib(AttributeKey.POISON_TICKS, 0);
        claw.putAttrib(AttributeKey.VENOM_TICKS, 0);
        claw.putAttrib(AttributeKey.POISON_TICKS, 0);
        claw.clearAttrib(VENOMED_BY);
        claw.resetFreeze();
        claw.hidden(false);
    }

    public void rise() {
        npc.lock();
        rightClaw.lock();
        leftClaw.lock();
        getObject(rightClaw).setId(CRYSTALLINE_STRUCTURE);
        getObject(npc).setId(LARGE_HOLE);
        getObject(leftClaw).setId(LARGE_ROCK_29883);
        Chain.bound(null).runFn(1, () -> {
            animate(rightClaw, 7350);
            animate(npc, isEmpowered() ? 7383 : 7335);
            animate(leftClaw, 7354);
        }).then(5, () -> {
            getObject(rightClaw).setId(CRYSTALLINE_STRUCTURE_29887);
            getObject(npc).setId(LARGE_HOLE_29881);
            getObject(leftClaw).setId(LARGE_ROCK_29884);
            npc.unlock();
            leftClaw.unlock();
            rightClaw.unlock();
            target = getAllTargets().stream().findFirst().orElse(null);
        });
    }

    public void turn() {
        Facing dest = getTurnDestination();
        if (dest == null || dest == facing) {
            return;
        }
        Chain.noCtx().runFn(1, () -> {
            turning.getAndSet(true);
            if ((facing == LEFT && dest == RIGHT) || (facing == RIGHT && dest == LEFT) || (facing == LEFT && dest == CENTER)) { // 'far' transition
                delayedAnimation(npc, dest.getFarTransitionAnim(isEmpowered()), 1);
            } else {
                delayedAnimation(npc, dest.getCloseTransitionAnim(isEmpowered()), 1);
            }
        }).then(2, () -> {
            delayedAnimation(npc, dest.getIdleAnim(isEmpowered()), 1);
            facing = dest;
        }).then(2, () -> turning.getAndSet(false));
    }

    public Facing getTurnDestination() {
        if (target == null)
            target = World.getWorld().get(getAllTargets());//bet this is null cuz it needs a target to find bounds
        //if (target.tile().inBounds(centerTargetBounds)) // it should npe on this line then not in bounds
        //    return CENTER;
        // fuck the center, these bounds are huge, wont match 07 logic
        if (target.tile().y >= 5741)
            return isOnEastSide() ? RIGHT : LEFT;
        else if (target.tile().y <= 5739)
            return isOnEastSide() ? LEFT : RIGHT;
        else
            return CENTER;
    }

    public void animate(NPC npc, int animationId) {
        GameObject obj = getObject(npc);
        if (obj != null) {
            obj.animate(animationId);
            // System.out.println("anim "+npc.getMobName()+" "+animationId+" on "+obj.definition().name);
        } else {
            System.err.println("no obj for " + npc.getMobName());
        }
    }

    public void delayedAnimation(NPC npc, int animationId, int delay) {
        npc.runFn(delay, () -> animate(npc, animationId));
    }

    private final GameObject DUMMY = new GameObject(0, new Tile(0, 0, 0));

    private GameObject getObject(NPC npc) {
        var t = new Tile(
            isOnEastSide() ? npc.getAbsX() : npc.getAbsX() - 3,
            npc.getAbsY(),
            npc.getZ());
        GameObject obj = t.getObject(-1, 10, -1);
        if (obj == null) {
            return DUMMY;
        }
        return obj;
    }

    private boolean isOnEastSide() {
        return npc.tile().equals(npc.npc().spawnTile());
    }

    private Facing facing = CENTER;
    private Area northTargetBounds, centerTargetBounds, southTargetBounds, arenaBounds;

    enum Facing {
        RIGHT(7337, 7376, 7346, 7373,
            7339, 7343, 7381, 7379,
            28, 44,
            37, 52),
        CENTER(7336, 7374, 7345, 7371,
            7340, 7342, 7382, 7378,
            28, 39,
            37, 49),
        LEFT(7338, 7375, 7347, 7372,
            7341, 7344, 7377, 7380,
            28, 34,
            37, 45),
        ;

        Facing(int idleAnim, int empoweredIdleAnim, int attackAnim, int empoweredAttackAnim, int closeTransitionAnim, int farTransitionAnim, int empoweredCloseTransitionAnim, int empoweredFarTransitionAnim, int swX, int swY, int neX, int neY) {
            this.idleAnim = idleAnim;
            this.empoweredIdleAnim = empoweredIdleAnim;
            this.attackAnim = attackAnim;
            this.empoweredAttackAnim = empoweredAttackAnim;
            this.closeTransitionAnim = closeTransitionAnim;
            this.farTransitionAnim = farTransitionAnim;
            this.empoweredCloseTransitionAnim = empoweredCloseTransitionAnim;
            this.empoweredFarTransitionAnim = empoweredFarTransitionAnim;
            this.swX = swX;
            this.swY = swY;
            this.neX = neX;
            this.neY = neY;
        }

        final int idleAnim;
        final int empoweredIdleAnim;
        final int attackAnim;
        final int empoweredAttackAnim;
        final int closeTransitionAnim;
        final int farTransitionAnim;
        final int empoweredCloseTransitionAnim;
        final int empoweredFarTransitionAnim;

        int getIdleAnim(boolean empowered) {
            return empowered ? empoweredIdleAnim : idleAnim;
        }

        int getAttackAnim(boolean empowered) {
            return empowered ? empoweredAttackAnim : attackAnim;
        }

        int getCloseTransitionAnim(boolean empowered) {
            return empowered ? empoweredCloseTransitionAnim : closeTransitionAnim;
        }

        int getFarTransitionAnim(boolean empowered) {
            return empowered ? empoweredFarTransitionAnim : farTransitionAnim;
        }

        final int swX;
        final int swY;
        final int neX;
        final int neY; // local targeting area, assuming olm is on the east side. must be flipped if on west

    }

    enum PhasePower {
        ACID(Color.DARK_GREEN.wrap("acid")),
        FLAME(Color.RED.wrap("flame")),
        CRYSTAL(Color.RAID_PURPLE.wrap("crystal")),
        ;

        PhasePower(String name) {
            this.name = name;
        }

        final String name;
    }

    public void forAllTargets(Consumer<Player> action) {
        Arrays.stream(npc.closePlayers(32))
            .filter(p -> (p.tile().getY() & 63) >= 34) // on olm floor, past the barrier
            .forEach(action);
    }

    private List<Player> getAllTargets() {
        return Arrays.stream(npc.closePlayers(32))
            .filter(p -> (p.tile().getY() & 63) >= 34) // on olm floor, past the barrier
            .collect(Collectors.toList());
    }

    private List<Player> getFacingTargets() {
        Area bounds;
        if (facing == CENTER)
            bounds = centerTargetBounds;
        else if (facing == RIGHT)
            bounds = isOnEastSide() ? northTargetBounds : southTargetBounds;
        else
            bounds = isOnEastSide() ? southTargetBounds : northTargetBounds;

        // System.out.println("bounds " + bounds);
        // bounds.corners().forEach(() -> new GroundItem(new Item(ItemID.MITHRIL_ARROWTIPS), c, null).setInstance(npc.getInstance()).spawn().setTimer(2));
        return Arrays.stream(npc.closePlayers(32))
            .filter(p -> p.tile().inBounds(bounds))
            .collect(Collectors.toList());
    }
}
