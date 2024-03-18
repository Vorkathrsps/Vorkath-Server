package com.cryptic.model.entity.combat.method.impl.npcs.godwars.bandos;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraardorCombat extends CommonCombatMethod {

    private static final Area BANDOS_AREA = new Area(2863, 5350, 2877, 5370);

    public static Area getBandosArea() {
        return BANDOS_AREA;
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
    public void init(NPC npc) {
        npc.ignoreOccupiedTiles = true;
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);

    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) return false;

        if (Utils.rollDie(6, 1)) entity.forceChat(Utils.randomElement(QUOTES));

        if (withinDistance(1) && Utils.rollPercent(65))
            meleeAttack();
        else
            rangedAttack();
        return true;
    }

    private void rangedAttack() {
        if (!withinDistance(8)) return;
        entity.animate(7021);
        if (target == null) return;
        if (!ProjectileRoute.hasLineOfSight(entity, target)) return;
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1202, 41, duration, 43, 31, 8, entity.getSize(), 5);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
    }

    private void meleeAttack() {
        if (!withinDistance(1)) return;
        entity.animate(7018);
        if (target == null) return;
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
        if (GwdLogic.isBoss(entity.getAsNpc().id())) {
            Map<Entity, Long> last_attacked_map = entity.getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<Entity, Long>());
            last_attacked_map.put(target, System.currentTimeMillis());
            entity.putAttrib(AttributeKey.LAST_ATTACKED_MAP, last_attacked_map);
        }
    }


    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }
}
