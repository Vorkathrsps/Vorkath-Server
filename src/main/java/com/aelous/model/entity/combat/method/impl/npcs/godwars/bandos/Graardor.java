package com.aelous.model.entity.combat.method.impl.npcs.godwars.bandos;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graardor extends CommonCombatMethod {

    private static final Area BANDOS_AREA = new Area(2863, 5350, 2877, 5370);

    public static Area getBandosArea() {
        return BANDOS_AREA;
    }

    public static boolean isMinion(NPC n) {
        return n.id() >= 2216 && n.id() <= 2218;
    }

    private static Entity lastBossDamager = null;

    public static Entity getLastBossDamager() {
        return lastBossDamager;
    }

    public static void setLastBossDamager(Entity lastBossDamager) {
        Graardor.lastBossDamager = lastBossDamager;
    }

    private final List<String> QUOTES = Arrays.asList("Death to our enemies!",
        "Brargh!",
        "Break their bones!",
        "For the glory of Bandos!",
        "Split their skulls!",
        "We feast on the bones of our enemies tonight!",
        "CHAAARGE!",
        "Crush them underfoot!",
        "All glory to Bandos!",
        "GRRRAAAAAR!",
        "FOR THE GLORY OF THE BIG HIGH WAR GOD!");

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        if (!withinDistance(8))
            return false;

        if (Utils.rollDie(6, 1))
            entity.forceChat(Utils.randomElement(QUOTES));

        if (withinDistance(1) && Utils.rollPercent(65))
            meleeAttack();
        else
            rangedAttack();
        return true;

    }

    private void rangedAttack() {
        entity.animate(7021);
        if (target != null) {
            if (entity.getLocalPlayers().stream().anyMatch(p -> p.tile().distance(entity.tile()) < 10)) {
                if (ProjectileRoute.allow(entity.getAsNpc(), target.getAsPlayer())) {
                    new Projectile(entity, target, 1202, 90, 5, 1, 5, 0).sendProjectile();
                    target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
                }
            }
        }
    }

    private void meleeAttack() {
        entity.animate(7018);
        if (target != null) {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
            if (GwdLogic.isBoss(entity.getAsNpc().id())) {
                Map<Entity, Long> last_attacked_map = entity.getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<Entity, Long>());
                last_attacked_map.put(target, System.currentTimeMillis());
                entity.putAttrib(AttributeKey.LAST_ATTACKED_MAP, last_attacked_map);
            }
        }
    }


    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
