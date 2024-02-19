package com.cryptic.model.entity.combat.method.impl.npcs.godwars.zamorak;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KrilCombat extends CommonCombatMethod {
    private static final Area ENCAMPMENT = new Area(2918, 5318, 2936, 5331);

    public static Area getENCAMPMENT() {
        return ENCAMPMENT;
    }

    private final List<String> QUOTES = Arrays.asList("Attack them, you dogs!",
        "Forward!",
        "Death to Saradomin's dogs!",
        "Kill them, you cowards!",
        "The Dark One will have their souls!",
        "Zamorak curse them!",
        "Rend them limb from limb!",
        "No retreat!");

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (GwdLogic.isBoss(entity.getAsNpc().id())) {
            Map<Entity, Long> last_attacked_map = entity.getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<Entity, Long>());
            last_attacked_map.put(target, System.currentTimeMillis());
            entity.putAttrib(AttributeKey.LAST_ATTACKED_MAP, last_attacked_map);
        }

        if (Utils.rollDie(3, 1)) entity.forceChat(Utils.randomElement(QUOTES));

        if (Utils.rollDice(10)) target.poison(16);

        if (Utils.rollDice(50)) {
            if (!withinDistance(8)) return false;
            magic();
        } else {
            if (!withinDistance(1)) return false;
            melee();
        }
        return true;
    }

    public void melee() {
        entity.animate(6948);
        if (Utils.rollDie(5, 1)) {
            int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
            entity.forceChat("YARRRRRRR!");
            target.hit(entity, hit, CombatType.MELEE).submit();
            target.message("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
            target.getSkills().alterSkill(Skills.PRAYER, -20);
        } else {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy(true).submit();
        }
    }

    public void magic() {
        if (!ProjectileRoute.hasLineOfSight(entity, target)) return;
        entity.animate(6950);
        var tileDist = entity.tile().distance(target.tile());
        int durationMagic = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1227, 51, durationMagic, 60, 30, 6, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
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
