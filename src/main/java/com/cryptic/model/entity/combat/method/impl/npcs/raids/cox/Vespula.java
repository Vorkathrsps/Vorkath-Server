package com.cryptic.model.entity.combat.method.impl.npcs.raids.cox;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.Direction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.VESPULA;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.VESPULA_7532;

/**
 * @Author Origin
 * @Since October 30, 2021
 */
public class Vespula extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.isNpc()) {
            NPC npc = entity.getAsNpc();
            if (npc.id() != VESPULA || !withinDistance(1) || World.getWorld().random(5) > 3)
                rangeAttack(npc, target);
            else
                meleeAttack(npc, target);
        }
        return true;
    }

    private void rangeAttack(NPC npc, Entity entity) {
        if (target.isPlayer()) {
            Player player = target.getAsPlayer();
            npc.animate(entity.attackAnimation());
            var tileDist = npc.tile().transform(1, 1, 0).distance(target.tile());
            var delay = Math.max(1, (50 + (tileDist * 12)) / 30);

           // npc.setPositionToFace(null); // Stop facing the target
            //Target all raids party members
            if (player.raidsParty != null) {
                for (Player p : player.raidsParty.getMembers()) {
                    if (p != null && p.getRaids() != null && p.getRaids().raiding(p) && p.tile().inArea(new Area(3298, 5287, 3325, 5309, p.raidsParty.getHeight()))) {
                        if (npc.id() == VESPULA) {
                            new Projectile(npc, target, 1486, 20, 12 * tileDist, 70, 43, 0).sendProjectile();
                        } else {
                            new Projectile(npc, target, 1486, 20, 12 * tileDist, 40, 43, 0).sendProjectile();
                        }
                        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).postDamage(this::handleAfterHit).submit();

                        //echo projectile
                        Direction echoDir = World.getWorld().random(Direction.values());
                        Tile echoTile = target.tile().copy().transform(echoDir.deltaX, echoDir.deltaY, target.tile().level);

                        if (npc.id() == VESPULA)
                            new Projectile(entity.tile().transform(1, 1, 0), echoTile, 1, 1486, 100, 30, 70, 0, 0).sendProjectile();
                        else
                            new Projectile(entity.tile().transform(1, 1, 0), echoTile, 1, 1486, 100, 30, 40, 0, 0).sendProjectile();

                        Chain.bound(null).runFn(4, () -> {
                            if (p.isAt(echoTile)) {
                                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).postDamage(this::handleAfterHit).submit();
                            }
                        });
                    }
                }
            }
        }
    }

    private void meleeAttack(NPC npc, Entity entity) {
        npc.animate(7454);
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
    }

    public static void onHit(NPC npc, int damage) {
        if (npc.hp() > npc.maxHp() / 5 && (npc.hp() - damage) <= npc.maxHp() / 5) {
            // land
            npc.transmog(VESPULA_7532, true);
            npc.heal(npc.maxHp());
            npc.setCombatInfo(World.getWorld().combatInfo(VESPULA_7532));
            npc.setCombatMethod(World.getWorld().combatInfo(VESPULA_7532).scripts.newCombatInstance());
            npc.animate(7457);
            Chain.bound(null).runFn(50, () -> {
                if (!npc.dead()) {
                    npc.transmog(VESPULA, false);
                    npc.setCombatInfo(World.getWorld().combatInfo(VESPULA_7532));
                    npc.setCombatMethod(World.getWorld().combatInfo(VESPULA_7532).scripts.newCombatInstance());
                    npc.animate(7452);
                    npc.heal(npc.maxHp());
                }
            });
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

    public void handleAfterHit(Hit hit) {
        Entity attacker = hit.getAttacker();
        Entity entity = hit.getTarget();
        if (World.getWorld().rollDie(5,1)) {
            if (!Equipment.venomHelm(target)) { // Serp helm stops poison.
                target.hit(attacker, 20, HitMark.POISON);
            }
        }
    }
}
