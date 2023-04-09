package com.aelous.model.entity.combat.method.impl.npcs.bosses.cerberus;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.TickDelay;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * @author Patrick van Elderen | March, 10, 2021, 09:44
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Cerberus extends CommonCombatMethod {

    private final TickDelay comboAttackCooldown = new TickDelay();
    private final TickDelay spreadLavaCooldown = new TickDelay();
    private final TickDelay spawnSoulCooldown = new TickDelay();

    private static final Area area = new Area(1358, 1257, 1378, 1257);
    private boolean combatAttack = false;

    private void rangedAttack() {
        //mob.forceChat("RANGE");
        if(target.dead() || target.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
            return;
        }
        var tileDist = entity.tile().distance(target.tile());
        var delay = (1D + (Math.floor(1 + tileDist) / 6D));
        //int duration = entity.executeProjectile(new Projectile(entity, target, 1245, 50, tileDist, 43, 31, 0, 5));
        int duration = 0;
        entity.animate(4492);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), duration + 1, CombatType.RANGED).checkAccuracy().submit();
        target.performGraphic(new Graphic(1244, GraphicHeight.HIGH, duration + 75));
    }

    private void magicAttack() {
        //mob.forceChat("MAGIC");
        if(target.dead() || target.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
            return;
        }
        int tileDist = entity.tile().distance(target.tile());
        int delay = (int) entity.getCombat().magicSpellDelay(target);
        //int duration = entity.executeProjectile(new Projectile(entity, target, 1242, 50, tileDist, 43, 31, 0, 5));
        int duration = 0;
        entity.animate(4492);

        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);

        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), duration + 1, CombatType.MAGIC).checkAccuracy().submit();

        if (hit > 0) {
            target.graphic(1243, GraphicHeight.HIGH, duration + 75);
        }
    }

    private void meleeAttack(boolean animate) {
        if (animate)
            entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
    }

    private void comboAttack() {
        if(target.dead() || target.getSkills().xpLevel(Skills.HITPOINTS) <= 0) {
            return;
        }
        entity.animate(4490); // triple attack
        combatAttack = true;
        //mob.forceChat("MAGIC COMBO");
        entity.runFn(0, this::magicAttack).then(1, () -> {
            if (entity == null || target.dead()) {
                return;
            }
            rangedAttack();
        }).then(2, () -> {
            combatAttack = true;
            meleeAttack(true);
        });
        comboAttackCooldown.delay(66); // ~40 seconds cooldown
    }

    ArrayList<NPC> souls = new ArrayList<>();

    private void spawnSouls(Entity target) {

        if (target == null) {
            return;
        }

        if (target.dead()) {
            return;
        }

        int tileDist = entity.tile().distance(target.tile());

        NPC melee = new NPC(5869, new Tile(1239, 1256, 0));
        NPC archer = new NPC(5867, new Tile(1240, 1256, 0));
        NPC magician = new NPC(5868, new Tile(1241, 1256, 0));

        if (!target.tile().inArea(area)) {
            World.getWorld().unregisterNpc(melee);
            World.getWorld().unregisterNpc(archer);
            World.getWorld().unregisterNpc(magician);
            souls.clear();
            return;
        }

        melee.respawns(false);
        archer.respawns(false);
        magician.respawns(false);
        melee.getCombat().setTarget(target);
        archer.getCombat().setTarget(target);
        magician.getCombat().setTarget(target);
        World.getWorld().registerNpc(melee);
        World.getWorld().registerNpc(archer);
        World.getWorld().registerNpc(magician);
        souls.add(melee);
        souls.add(archer);
        souls.add(magician);

        melee.startEvent(1, () -> {
            melee.stopActions(true);
            melee.setPositionToFace(target.tile());
            melee.animate(1); //TODO
        },2, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                //melee.executeProjectile(new Projectile(melee, target, 1248, 50, tileDist, 43, 31, 0, target.getSize()));
                if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MELEE)) {
                    p.hit(melee, 30);
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
            archer.setPositionToFace(target.tile());
            archer.animate(1); //TODO
        },2, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                //archer.executeProjectile(new Projectile(archer, target, 1248, 50, tileDist, 43, 31, 0, target.getSize()));
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

    private void spreadLava() { //TODO lava doesnt spread across 3 tiles instead all on mob.tile()
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
            //entity.executeProjectile(new Projectile(entity, target, 1247,25,86,65,0,0,entity.getSize()));
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
    public boolean prepareAttack(Entity entity, Entity target) {

        SecureRandom secureRandom = new SecureRandom();

        if (!entity.dead()) {

            if (!comboAttackCooldown.isDelayed()) {
                comboAttack();
            }
            if (souls.isEmpty()) {
                if (secureRandom.nextDouble() < 0.10) {
                    entity.forceChat("Arrrrroooooooooo!");
                    spawnSouls(target);
                }
            }
            if (entity.hp() <= 200 && !spreadLavaCooldown.isDelayed()) {
                spreadLava();
            }
            if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollPercent(25)) {
                meleeAttack(true);
            } else if (Utils.rollPercent(50)) {
                magicAttack();
            } else {
                rangedAttack();
            }
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return combatAttack ? 12 : entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
