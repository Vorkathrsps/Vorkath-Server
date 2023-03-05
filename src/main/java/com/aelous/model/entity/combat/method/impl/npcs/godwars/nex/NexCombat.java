package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCCombatInfo;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.BLOOD_REAVER;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.NEX_11282;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 12, 2022
 */
public class NexCombat extends CommonCombatMethod {

    private int attackCount;
    private static final int TURMOIL_GFX = 2016;
    private static final int MELEE_ATTACK_ANIM = 9180;
    private static final int MELEE_ATTACK_ZAROS_PHASE = 9181;
    private static final int VIRUS_ATTACK_ANIM = 9189;
    private static final int MAGIC_ATTACK_ANIM = 9189;//Shared animation
    private static final int BLOOD_SIPHON_ANIM = 9183;
    private static final int EMBRACE_DARKNESS_ATTACK_ANIM = 9182;
    private static final int SHADOW_SMASH_ATTACK_ANIM = 9186;
    private static final int SMOKE_BULLET_ATTACK_ANIM = 9178;
    private static final int FLYING_PLAYER_ANIM = 1157;
    private static final int MAGIC_ATTACK_MAX = 32;
    private static final int RANGED_ATTACK_MAX = 60;
    private static final int MELEE_ATTACK_MAX = 29;
    private static final int DRAG_ATTACK_MAX = 30;
    private static final int SMOKE_BULLET_ATTACK_MAX = 50;
    private static final int SHADOW_SMASH_ATTACK_MAX = 50;
    private static final int BLOOD_SACRIFICE_ATTACK_MAX = 80;
    private static final int CONTAINMENT_SPECIAL_ATTACK_MAX = 60;
    private static final int ICE_PRISON_SPECIAL_ATTACK_MAX = 75;

    private static final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE};

    static GameObject redBarrier;// = new GameObject(42941, ancientBarrierPurple.get().tile(), ancientBarrierPurple.get().getType(), ancientBarrierPurple.get().getRotation());

    public static final Area NEX_AREA = new Area(2910, 5189, 2939, 5217);

    public static Tile[] NO_ESCAPE_TELEPORTS = {new Tile(2925, 5212, 0), // north
        new Tile(2934, 5203, 0), // east,
        new Tile(2924, 5194, 0), // south
        new Tile(2916, 5203, 0),}; // west

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        if (entity.isNpc()) {
            NPC npc = entity.getAsNpc();
            final Nex nex = (Nex) npc;
            attackCount += 1;
            nex.useSmartPath = true;
            if (nex.getAttacksStage() == 0) {
                if (nex.getLastVirus() < Utils.currentTimeMillis() && attackCount == 1) {
                    virusAttack(nex, target);
                } else if (attackCount == 4) {
                    dragAttack(nex);
                } else if (nex.getLastNoEscape() < Utils.currentTimeMillis() && attackCount == 6) {
                    attackCount = 0;
                    smokeBulletAttack(nex);
                } else {
                    if (CombatFactory.canReach(nex, CombatFactory.MELEE_COMBAT, target)) {
                        if(World.getWorld().rollDie(2,1)) {
                            meleeAttack(nex, target,false);
                        } else {
                            smokeRushAttack(nex);
                        }
                    } else {
                        smokeRushAttack(nex);
                    }
                }
            } else if (nex.getAttacksStage() == 1) {
                if (attackCount == 1) {
                    shadowShotsAttack(nex);
                } else if (attackCount == 3) {
                    shadowSmashAttack(nex);
                    attackCount = 0;
                /*} else if (attackCount == 6) {
                    embraceDarknessAttack(nex);
                    attackCount = 0; // reset attack count
                    */
                } else {
                    if (CombatFactory.canReach(nex, CombatFactory.MELEE_COMBAT, target)) {
                        meleeAttack(nex, target,false);
                    } else {
                        shadowShotsAttack(nex);
                    }
                }
            } else if (nex.getAttacksStage() == 2) {
                if (attackCount == 1) {
                    bloodBarrage(nex, target);
                } else if (attackCount == 3) {
                    bloodSiphon(nex);
                } else if (attackCount == 6) {
                    bloodSacrifice(nex, target);
                    attackCount = 0; // reset attack count
                } else {
                    if (CombatFactory.canReach(nex, CombatFactory.MELEE_COMBAT, target)) {
                        if(World.getWorld().rollDie(2,1)) {
                            meleeAttack(nex, target,false);
                        } else {
                            smokeRushAttack(nex);
                        }
                    } else {
                        bloodBarrage(nex, target);
                    }
                }
            } else if (nex.getAttacksStage() == 3) {
                if (CombatFactory.canReach(nex, CombatFactory.MELEE_COMBAT, target)) {
                    if(World.getWorld().rollDie(2,1)) {
                        meleeAttack(nex, target,false);
                    } else {
                        smokeRushAttack(nex);
                    }
                } else {
                    iceBarrageAttack(nex);
                }
            } else if (nex.getAttacksStage() == 4) {
                if (attackCount == 0) {
                    if (!nex.isTurmoilAttackUsed()) {
                        turmoil(nex);
                    }
                } else {
                    //25% chance to drain
                    if (World.getWorld().rollDie(100, 25)) {
                        drainAttack(nex);
                    }

                    if (CombatFactory.canReach(nex, CombatFactory.MELEE_COMBAT, target)) {
                        if(World.getWorld().rollDie(2,1)) {
                            meleeAttack(nex, target,true);
                        } else {
                            smokeRushAttack(nex);
                        }
                    } else {
                        magicAttack(nex);
                    }
                }
            }
        }
    }

    //Lasting until the end of the fight, Nex's magic and melee attacks are now far more accurate and powerful, hitting massive damage even through prayer.
    private void turmoil(Nex nex) {
        nex.graphic(TURMOIL_GFX);
        nex.combatInfo().stats.attack += 100;
        nex.combatInfo().stats.strength += 100;
        nex.combatInfo().stats.magic += 100;
        nex.putAttrib(AttributeKey.TURMOIL_ACTIVE, true);
        nex.setTurmoilAttackUsed(true);
    }

    private void drainAttack(Nex nex) {
        for (Entity t : getPossibleTargets(nex)) {
            Projectile projectile = new Projectile(nex, t, 2010, 0, 100, 43, 31, 0);
            projectile.sendProjectile();

            for (int skill : DRAIN) {
                int take = 5;
                t.skills().alterSkill(skill, -take);
                nex.combatInfo().stats.attack += take;
                nex.combatInfo().stats.strength += take;
                nex.combatInfo().stats.defence += take;
            }
        }
    }

    private void magicAttack(Nex nex) {
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, MAGIC_ATTACK_MAX);
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            Projectile projectile = new Projectile(nex, t, 2007, 0, 100, 43, 31, 0);
            projectile.sendProjectile();
            Hit hit = t.hit(nex, CombatFactory.calcDamageFromType(nex, t, CombatType.MAGIC), 3, CombatType.MAGIC);
            hit.checkAccuracy().submit();
            if(hit.isAccurate()) {
                t.graphic(2008);
                t.skills().alterSkill(Skills.PRAYER, -5);
            }
        }
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, -1);
    }

    private void bloodSacrifice(Nex nex, Entity target) {
        nex.forceChat("I demand a blood sacrifice!");
        final Player player = (Player) target;
        //player.getAppearence().setGlowRed(true);
        player.message("Nex has marked you as a sacrifice, RUN!");
        final int x = player.getX();
        final int y = player.getY();

        //marked player has about four seconds to move at least seven tiles away from Nex, otherwise they will be dealt up to 40 damage through
        // Protect from Magic which heals her and drains the player's current prayer points by 1/3rd.
        Chain.bound(null).runFn(7, () -> {
            //player.getAppearence().setGlowRed(false);
            if (x == player.getX() && y == player.getY()) {
                int damage = Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC) ? 40 : BLOOD_SACRIFICE_ATTACK_MAX;
                player.hit(nex, World.getWorld().random(1,damage));
                int currentLevel = player.skills().level(Skills.PRAYER);
                int drain = currentLevel / 3;
                player.skills().alterSkill(Skills.PRAYER, -drain);
                player.message("You didn't make it far enough in time - Nex fires a punishing attack!");

                //Random players will also take up to 12 damage through Protect from Magic, and their prayer is drained by 1/3rd as well.
                for (final Entity t : getPossibleTargets(nex)) {
                    Projectile projectile = new Projectile(nex, t, 374, 35, 56, 41, 16, 0);
                    projectile.sendProjectile();

                    damage = World.getWorld().random(1, 12);
                    t.hit(nex, damage, 1);
                    t.graphic(376);
                    nex.hit(nex, damage / 4, SplatType.NPC_HEALING_HITSPLAT);
                }
            }
        });
    }

    private void bloodSiphon(Nex nex) {
        if(nex.getLastSiphon() < Utils.currentTimeMillis()) {
            nex.setLastSiphon(Utils.currentTimeMillis() + 30000);
            nex.killBloodReavers();
            nex.forceChat("A siphon will solve this!");
            nex.animate(BLOOD_SIPHON_ANIM);
            nex.setDoingSiphon(true);
            int bloodReaverSize = World.getWorld().definitions().get(NpcDefinition.class, BLOOD_REAVER).size;
            int respawnedBloodReaverCount = 0;
            int maxMinions = Utils.getRandom(3);
            if (maxMinions != 0) {
                int[][] dirs = Utils.getCoordOffsetsNear(bloodReaverSize);
                for (int dir = 0; dir < dirs[0].length; dir++) {
                    final Tile tile = new Tile(target.getX() + dirs[0][dir], target.getY() + dirs[1][dir], target.getZ());
                    if (World.getWorld().canMoveNPC(tile.getZ(), tile.getX(), tile.getY(), bloodReaverSize)) {
                        nex.getBloodReavers()[respawnedBloodReaverCount++] = new NPC(BLOOD_REAVER, tile).spawn(false);
                        if (respawnedBloodReaverCount == maxMinions)
                            break;
                    }
                }
            }
            Chain.bound(null).runFn(8, () -> nex.setDoingSiphon(false));
        }
    }

    private void bloodBarrage(Nex nex, Entity target) {
        nex.animate(MAGIC_ATTACK_ANIM);
        nex.graphic(2001);
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, MAGIC_ATTACK_MAX);
        Hit hit = target.hit(nex, CombatFactory.calcDamageFromType(nex, target, CombatType.MAGIC), 3, CombatType.MAGIC).graphic(new Graphic(379));
        hit.checkAccuracy().submit();
        if (hit.isAccurate()) {
            nex.hit(nex, hit.getDamage() / 4, SplatType.NPC_HEALING_HITSPLAT);
            target.graphic(2002);
        }
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, -1);
    }

    private void embraceDarknessAttack(Nex nex) {
        nex.forceChat("Embrace darkness!");
        nex.animate(EMBRACE_DARKNESS_ATTACK_ANIM);

        Chain.bound(null).runFn(1, () -> {
            if (nex.getAttacksStage() != 1 || nex.finished()) {
                for (Entity m : getPossibleTargets(nex)) {
                    if (m instanceof Player) {
                        Player player = (Player) m;
                        //TODO a varbit to turn screen darker
                    }
                }
                return;
            }

            if (Utils.getRandom(2) == 0) {
                for (Entity t : getPossibleTargets(nex)) {
                    if (t instanceof Player) {
                        Player player = (Player) t;
                        int distance = Utils.getDistance(player.getX(), player.getY(), nex.getX(), nex.getY());
                        if (distance > 30)
                            distance = 30;
                        //TODO a varbit to turn screen darker
                    }
                }
            }
        });
    }

    private void shadowSmashAttack(Nex nex) {
        nex.forceChat("Fear the Shadow!");
        nex.animate( SHADOW_SMASH_ATTACK_ANIM);
        List<Entity> possibleTargets = getPossibleTargets(nex);
        final HashMap<String, int[]> tiles = new HashMap<>();
        for (Entity t : possibleTargets) {
            String key = t.getX() + "_" + t.getY();
            if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
                tiles.put(key, new int[]{t.getX(), t.getY()});
            }
        }

        //Set up the tile graphics
        Chain.bound(null).runFn(1, () -> {
            for (int[] tile : tiles.values()) {
                World.getWorld().tileGraphic(383, new Tile(tile[0], tile[1], 0), 0, 0);
            }
            //Players have 3 ticks to move
        }).then(3, () -> {
            for (int[] tile : tiles.values()) {
                for (Entity t : possibleTargets) {
                    if (t.getX() == tile[0] && t.getY() == tile[1]) {
                        t.hit(nex, World.getWorld().random(1, SHADOW_SMASH_ATTACK_MAX));
                    }
                }
            }
        });
    }

    private void shadowShotsAttack(Nex nex) {
        //When using prot range, damage cut in half
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            Projectile projectile = new Projectile(nex, t, 378, 0, 100, 43, 31, 0);
            projectile.sendProjectile();

            //Players will take more damage from the shadow shots the closer to Nex they are, up to 30.
            int damage = 0;
            if (t.tile().distance(nex.tile()) <= 2) {
                damage = RANGED_ATTACK_MAX;
            } else if (t.tile().distance(nex.tile()) <= 4) {
                damage = RANGED_ATTACK_MAX - 20;
            } else if (t.tile().distance(nex.tile()) > 6) {
                damage = RANGED_ATTACK_MAX - 30;
            }

            if(Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES)) {
                damage = damage / 2;
            }

            nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, damage);
            t.hit(nex, World.getWorld().random(1, damage), 3);
            Chain.bound(null).runFn(3, () -> {
                t.graphic(379);
                t.skills().alterSkill(Skills.PRAYER, -5);
            });
        }
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, -1);
    }

    private void smokeBulletAttack(Nex nex) {
        nex.setLastNoEscape(Utils.currentTimeMillis() + 30000);
        nex.forceChat("There is...");
        nex.cantInteract(true);
        nex.getCombat().reset();
        final int idx = Utils.random(NO_ESCAPE_TELEPORTS.length);
        final Tile selectedTile = NO_ESCAPE_TELEPORTS[idx];
        final Tile center = new Tile(2924, 5202, 0);

        Chain.bound(null).runFn(4, () -> {
            nex.animate(SMOKE_BULLET_ATTACK_ANIM);
            nex.setPositionToFace(selectedTile);
        }).then(6, () -> {
            nex.teleport(selectedTile);
            nex.faceEntity(null);
        }).then(1, () -> { // lands
            nex.forceChat("NO ESCAPE!");
            //Look for potential victims
            for (Entity p : nex.calculatePossibleTargets(center, selectedTile, idx == 0 || idx == 2)) {
                if (p instanceof Player) {
                    Chain.bound(null).runFn(1, () -> {
                        p.lock();
                        p.animate(FLYING_PLAYER_ANIM);
                        p.graphic(245, GraphicHeight.LOW, 124);
                        p.hit(p, World.getWorld().random(SMOKE_BULLET_ATTACK_MAX));
                        p.stun(2, true);
                        int diffX = center.x - p.getAbsX();
                        int diffY = center.y - p.getAbsY();
                        //TaskManager.submit(new ForceMovement(p.getAsPlayer(), 3, new ForceMovement(p.tile().clone(), new Tile(diffX, diffY), 10, 60, idx == 3 ? 3 : idx == 2 ? 2 : idx == 1 ? 1 : 0)));
                        final ForceMovement fm = new ForceMovement(selectedTile, new Tile(diffX, diffY), 10, 60, idx == 3 ? 3 : idx == 2 ? 2 : idx == 1 ? 1 : 0, 0);
                        p.setForceMovement(fm);
                        p.unlock();
                    });
                }
            }
            nex.animate(SMOKE_BULLET_ATTACK_ANIM);
            Tile dif = new Tile(center.x - selectedTile.x, center.y - selectedTile.y);
            final ForceMovement fm = new ForceMovement(selectedTile, dif, 50, 80, 3);
            entity.setForceMovement(fm);
            Chain.bound(null).then(3, () -> nex.teleport(center)); //Update nex on map
        }).then(8, () -> {
            nex.getCombat().setTarget(Utils.randomElement(getPossibleTargets(nex)));
            nex.cantInteract(false);
        });
    }

    private void dragAttack(Nex nex) {
        Entity target = Utils.randomElement(getPossibleTargets(nex));
        if(target == null) {
            return; // No targets found
        }
        if (target.isPlayer()) {
            int vecX = (nex.getAbsX() - Utils.getClosestX(nex, target.tile()));
            int vecY = (nex.getAbsY() - Utils.getClosestY(nex, target.tile()));
            int endX = nex.getAbsX();
            int endY = nex.getAbsY();
            for (int i = 0; i < 4; i++) {
                if (DumbRoute.getDirection(endX, endY, nex.getZ(), target.getSize(), endX + vecX, endY + vecY) != null) { // we can take this step!
                    endX += vecX;
                    endY += vecY;
                } else
                    break; // cant take the step, stop here
            }
            Direction dir;
            if (vecX == -1)
                dir = Direction.EAST;
            else if (vecX == 1)
                dir = Direction.WEST;
            else if (vecY == -1)
                dir = Direction.NORTH;
            else
                dir = Direction.SOUTH;

            int finalEndX = endX;
            int finalEndY = endY;
            Chain.bound(null).runFn(1, () -> {
                final Player p = target.getAsPlayer();
                p.lock();
                p.animate(FLYING_PLAYER_ANIM);
                p.graphic(1998, GraphicHeight.LOW, 124);
                p.hit(entity, World.getWorld().random(DRAG_ATTACK_MAX));
                p.stun(2, true);
                int diffX = finalEndX - p.getAbsX();
                int diffY = finalEndY - p.getAbsY();
                //TaskManager.submit(new ForceMovementTask(p, 3, new ForceMovement(p.tile().clone(), new Tile(diffX, diffY), 10, 60, dir.toInteger())));
                p.unlock();
            });
        }
    }

    private void virusAttack(Nex nex, Entity target) {
        nex.animate(VIRUS_ATTACK_ANIM);
        nex.setLastVirus(Utils.currentTimeMillis() + 60000);
        nex.forceChat("Let the virus flow through you.");
        sendVirusAttack(nex, new ArrayList<>(), getPossibleTargets(nex), target);
    }

    public void sendVirusAttack(Nex nex, ArrayList<Entity> hit, List<Entity> possibleTargets, Entity infected) {
        for (Entity t : possibleTargets) {
            if (hit.contains(t))
                continue;
            if (Utils.getDistance(t.getX(), t.getY(), infected.getX(), infected.getY()) <= 1) {
                t.forceChat("*Cough*");
                t.hit(nex, World.getWorld().random(10));
                t.skills().alterSkill(Skills.PRAYER, -2);
                hit.add(t);
                sendVirusAttack(nex, hit, possibleTargets, infected);
            }
        }
    }

    private void smokeRushAttack(Nex nex) {
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, MAGIC_ATTACK_MAX);
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            Projectile projectile = new Projectile(nex, t, 384, 0, 100, 43, 31, 0);
            projectile.sendProjectile();
            if (World.getWorld().rollDie(100, 25)) {
                t.hit(nex, 2, SplatType.POISON_HITSPLAT);
            }
            t.hit(nex, CombatFactory.calcDamageFromType(nex, t, CombatType.MAGIC), 3, CombatType.MAGIC).graphic(new Graphic(385)).checkAccuracy().submit();
        }
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, -1);
    }

    private void meleeAttack(Nex nex, Entity target, boolean zarosPhase) {
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, MELEE_ATTACK_MAX);
        nex.animate(zarosPhase ? MELEE_ATTACK_ZAROS_PHASE : MELEE_ATTACK_ANIM);
        target.hit(nex, CombatFactory.calcDamageFromType(nex, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, -1);
    }

    private void iceBarrageAttack(Nex nex) {
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, MAGIC_ATTACK_MAX);
        nex.animate(MAGIC_ATTACK_ANIM);
        for (Entity t : getPossibleTargets(nex)) {
            Projectile projectile = new Projectile(nex, t, 362, 0, 100, 43, 31, 0);
            projectile.sendProjectile();
            Hit hit = t.hit(nex, CombatFactory.calcDamageFromType(nex, t, CombatType.MAGIC), 3, CombatType.MAGIC);
            hit.checkAccuracy().submit();
            if(hit.isAccurate()) {
                if (World.getWorld().rollDie(100, 25)) {
                    t.graphic(369);
                    t.freeze(10, nex);
                }
            }
        }
        nex.putAttrib(AttributeKey.MAXHIT_OVERRIDE, -1);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        if (hit.getTarget().isNpc()) {
            NPC npc = hit.getTarget().getAsNpc();
            npc.clearAttrib(AttributeKey.TURMOIL_ACTIVE);
            npc.transmog(NEX_11282);
            npc.forceChat("Taste my wrath!");
            final NPCCombatInfo combatInfo = npc.combatInfo();
            npc.animate(combatInfo.animations.death);
            Chain.bound(null).runFn(combatInfo.deathlen, () -> {
                npc.graphic(2013);
                List<Entity> possibleTargets = getPossibleTargets(npc);
                if (possibleTargets != null) {
                    for (Entity t : possibleTargets) {
                        if (t == null || t.dead() || !t.isRegistered() || !t.tile().isWithinDistance(npc.tile(), 5))
                            continue;
                        Projectile projectile = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() + 1, npc.getY() - 2, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() + 1, npc.getY() - 2, npc.getZ()), 0, 85);
                        Projectile projectile2 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() - 1, npc.getY() - 1, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile2.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() - 1, npc.getY() - 1, npc.getZ()), 0, 85);
                        Projectile projectile3 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() + 3, npc.getY() - 1, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile3.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() + 3, npc.getY() - 1, npc.getZ()), 0, 85);
                        Projectile projectile4 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() + 3, npc.getY() - 1, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile4.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() + 3, npc.getY() - 1, npc.getZ()), 0, 85);
                        Projectile projectile5 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() + 4, npc.getY() + 1, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile5.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() + 4, npc.getY() + 1, npc.getZ()), 0, 85);
                        Projectile projectile6 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() - 1, npc.getY() + 3, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile6.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() - 1, npc.getY() + 3, npc.getZ()), 0, 85);
                        Projectile projectile7 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() + 3, npc.getY() + 3, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile7.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() + 3, npc.getY() + 3, npc.getZ()), 0, 85);
                        Projectile projectile8 = new Projectile(npc.getCentrePosition(), new Tile(npc.getX() + 1, npc.getY() + 4, npc.getZ()), 1, 2012, 100, 40, npc.getZ(), 0, 0);
                        projectile8.sendProjectile();
                        World.getWorld().tileGraphic(2014, new Tile(npc.getX() + 1, npc.getY() + 4, npc.getZ()), 0, 85);
                        t.hit(npc, World.getWorld().random(40));
                    }
                }
            }).then(2, () -> {

                dropLoot(npc);

                npc.remove();

                if (redBarrierPurple != null && ancientBarrierPurple.isPresent()) {
                    ObjectManager.replaceWith(redBarrierPurple, ancientBarrierPurple.get());
                }

                ZarosGodwars.clear();

                //Respawn nex
                //seems to be bugging out here on respawn

                if (getPlayersCount() >= 1) {
                    respawnNexFunction();
                } else {
                    ZarosGodwars.cancel();
                }

            });
        }
        return true;
    }

    public void respawnNexFunction() {
        ZarosGodwars.startEvent();
    }

    public void dropLoot(NPC npc) {
        ZarosGodwars.drop(npc);
    }

}
