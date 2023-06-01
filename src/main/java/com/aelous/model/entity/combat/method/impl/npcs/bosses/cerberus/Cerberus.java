package com.aelous.model.entity.combat.method.impl.npcs.bosses.cerberus;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.TickDelay;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @Author: Origin
 * @SubAuthor: Patrick
 * Date: 6/1/2023
 */
public class Cerberus extends CommonCombatMethod {

    private final TickDelay comboAttackCooldown = new TickDelay();
    private final TickDelay spreadLavaCooldown = new TickDelay();
    private final TickDelay spawnSoulCooldown = new TickDelay();

    private static final Area area = new Area(1358, 1257, 1378, 1257);
    private boolean combatAttack = false;

    private void rangedAttack(Entity entity, Entity target) {
        if (entity instanceof NPC npc) {
            if (target instanceof Player player) {
                if (npc.dead()) {
                    return;
                }
                if (player.dead() || player.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
                    return;
                }
                npc.animate(4492);
                int tileDist = npc.tile().distance(player.tile());
                int duration = (51 + 11 + (20 * tileDist));
                var tile = npc.tile().translateAndCenterLargeNpc(npc, player);
                Projectile p = new Projectile(tile, player, 1245, 51, duration, 65, 31, 0, npc.getSize(), 20);
                final int delay = npc.executeProjectile(p);
                Hit hit = player.hit(npc, World.getWorld().random(30), CombatType.RANGED).clientDelay(delay).checkAccuracy();
                hit.submit();
                if (hit.getDamage() > 0) {
                    target.graphic(1244, GraphicHeight.HIGH, p.getSpeed());
                }
            }
        }
    }

    private void magicAttack(Entity entity, Entity target) {
        if (entity instanceof NPC npc) {
            if (target instanceof Player player) {
                if (player.dead() || player.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
                    return;
                }
               npc.animate(4492);
                int tileDist = npc.tile().distance(player.tile());
                int duration = (51 + 11 + (20 * tileDist));
                Projectile p = new Projectile(npc.tile().translateAndCenterLargeNpc(npc, player), player, 1242, 55, duration, 65, 31, 0, npc.getSize(), 20);
                final int delay = npc.executeProjectile(p);
                Hit hit = Hit.builder(npc, player, CombatFactory.calcDamageFromType(npc, player, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
                hit.submit();
                if (hit.getDamage() > 0) {
                    target.graphic(1243, GraphicHeight.HIGH, p.getSpeed());
                }
            }
        }
    }

    private void meleeAttack() {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
    }

    private void comboAttack(Entity entity, Entity target) {
        if (entity instanceof NPC npc) {
            if (target instanceof Player player) {
                if (player.dead() || player.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
                    return;
                }
                npc.animate(4490); // triple attack
                combatAttack = true;
                //mob.forceChat("MAGIC COMBO");
                npc.runFn(1, () -> {
                    if (player.dead()) {
                        return;
                    }
                    rangedAttack(npc, player);
                }).then(1, () -> {
                    magicAttack(npc, player);
                }).then(1, () -> {
                    combatAttack = true;
                    meleeAttack();
                });
                comboAttackCooldown.delay(66); // ~40 seconds cooldown
            }
        }
    }

    ArrayList<NPC> souls = new ArrayList<>();

    private void spawnSouls(Entity target) {

        if (target == null) {
            return;
        }

        if (target.dead()) {
            return;
        }

        NPC melee = new NPC(5869, new Tile(1239, 1256, 0));
        NPC archer = new NPC(5867, new Tile(1240, 1256, 0));
        NPC magician = new NPC(5868, new Tile(1241, 1256, 0));

        souls.add(melee);
        souls.add(archer);
        souls.add(magician);

        melee.respawns(false);
        archer.respawns(false);
        magician.respawns(false);
        World.getWorld().definitions().get(NpcDefinition.class, melee.getId());
        World.getWorld().definitions().get(NpcDefinition.class, archer.getId());
        World.getWorld().definitions().get(NpcDefinition.class, magician.getId());
        melee.face(entity);
        archer.face(entity);
        magician.face(entity);
        World.getWorld().registerNpc(melee);
        World.getWorld().registerNpc(archer);
        World.getWorld().registerNpc(magician);


        int tileDist = melee.tile().getChevDistance(target.tile());
        var tile = melee.tile().transform(1, 1, 0);
        int duration = (41 + 11 + (5 * tileDist));
        final Projectile[] projectile = {null};

        melee.startEvent(1, () -> {
            melee.stopActions(true);
            melee.lock();
            melee.setPositionToFace(target.tile());
            melee.animate(1); //TODO
        },2, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                projectile[0] = new Projectile(tile, target, 1248, 41, duration, 43, 0, 0, target.getSize(), 5);
                if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MELEE)) {
                    final int delay = melee.executeProjectile(projectile[0]);
                    p.hit(melee, 30, delay);
                } else {
                    melee.executeProjectile(projectile[0]);
                    p.hits.block();
                }
                if (!target.getAsPlayer().getEquipment().contains(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD)) {
                    p.getSkills().setLevel(Skills.PRAYER, Math.max(0, p.getSkills().level(Skills.PRAYER) - 30));
                } else {
                    p.getSkills().setLevel(Skills.PRAYER, Math.max(0, p.getSkills().level(Skills.PRAYER) - 15));
                }
            });
        });
        archer.startEvent(3, () -> {
            archer.stopActions(true);
            archer.lock();
            archer.setPositionToFace(target.tile());
            archer.animate(1); //TODO
        },2, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MISSILES)) {
                    p.hit(archer, 30);
                }
                if (!target.getAsPlayer().getEquipment().contains(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD)) {
                    p.getSkills().setLevel(Skills.PRAYER, Math.max(0, p.getSkills().level(Skills.PRAYER) - 30));
                } else {
                    p.getSkills().setLevel(Skills.PRAYER, Math.max(0, p.getSkills().level(Skills.PRAYER) - 15));
                }
            });
        });

        magician.startEvent(5, () -> {
            magician.stopActions(true);
            magician.lock();
            magician.setPositionToFace(target.tile());
            magician.animate(1); //TODO
        },2, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
                    p.hit(magician, 30);
                }
                if (!target.getAsPlayer().getEquipment().contains(ItemIdentifiers.SPECTRAL_SPIRIT_SHIELD)) {
                    p.getSkills().setLevel(Skills.PRAYER, Math.max(0, p.getSkills().level(Skills.PRAYER) - 30));
                } else {
                    p.getSkills().setLevel(Skills.PRAYER, Math.max(0, p.getSkills().level(Skills.PRAYER) - 15));
                }
            });
        });

        Chain.runGlobal(10, () -> {
            World.getWorld().unregisterNpc(melee);
            World.getWorld().unregisterNpc(archer);
            World.getWorld().unregisterNpc(magician);
            souls.clear();
        });

    }

    private void spreadLava() {
        if(target.dead() || target.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
            return;
        }
        spreadLavaCooldown.delay(30);
        entity.animate(4493);
        entity.forceChat("Grrrrrrrrrr");
        Tile[] positions = {target.tile().copy(),
            new Tile(entity.getAbsX() + Utils.random(-1, entity.getSize() - 2), entity.getAbsY() + Utils.random(-4, entity.getSize() + 4), entity.getZ()),
            new Tile(entity.getAbsX() + Utils.random(-1, entity.getSize() - 2), entity.getAbsY() + Utils.random(-4, entity.getSize() + 4), entity.getZ())};
        for (Tile pos : positions) {
            entity.runFn(1, () -> {
                World.getWorld().tileGraphic(1246, new Tile(pos.getX(), pos.getY(), pos.getZ()), 0, 0);
                World.getWorld().tileGraphic(1246, new Tile(pos.getX()-1, pos.getY() - 1, pos.getZ()), 0, 0);
            }).then(2, () -> {
                if (target == null)
                    return;
                if (target.tile().equals(pos)) {
                    target.hit(entity,World.getWorld().random(10, 15));
                } else if (Utils.getDistance(target.tile(), pos) == 1) {
                    target.hit(entity,7);
                }
            }).then(2, () -> {
                World.getWorld().tileGraphic(1247, new Tile(pos.getX(), pos.getY(), pos.getZ()), 0, 0);
            }).then(1, () -> {
                if (target == null)
                    return;
                if (target.tile().equals(pos)) {
                    target.hit(entity,World.getWorld().random(10, 18));
                } else if (Utils.getDistance(target.tile(), pos) == 1) {
                    target.hit(entity,10);
                }
            });
        }
    }

    @Override
    public void onDeath() {
        comboAttackCooldown.reset();
        spreadLavaCooldown.reset();
        spawnSoulCooldown.reset();
        souls.clear();
    }

    @Override
    public boolean prepareAttack(@NotNull Entity entity, Entity target) {
        if (!comboAttackCooldown.isDelayed()) {
            comboAttack(entity, target);
            return true;
        }

        if (entity.hp() <= 200 && !spreadLavaCooldown.isDelayed()) {
            spreadLava();
            return true;
        }

        if (Utils.percentageChance(50)) {
            magicAttack(entity, target);
        } else {
            rangedAttack(entity, target);
            return true;
        }

        if (souls.isEmpty()) {
            if (Utils.percentageChance(10)) {
                entity.forceChat("Arrrrroooooooooo!");
                spawnSouls(target);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return this.entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
