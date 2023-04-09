package com.aelous.model.entity.combat.method.impl.npcs.godwars.zamorak;

import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.Area;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kril extends CommonCombatMethod {

    public static boolean isMinion(NPC n) {
        return n.id() >= 3129 && n.id() <= 3132;
    }

    private static final Area ENCAMPMENT = new Area(2918, 5318, 2936, 5331);

    public static Area getENCAMPMENT() {
        return ENCAMPMENT;
    }

    private static Entity lastBossDamager = null;

    public static Entity getLastBossDamager() {
        return lastBossDamager;
    }

    public static void setLastBossDamager(Entity lastBossDamager) {
        Kril.lastBossDamager = lastBossDamager;
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
        if (Utils.rollDie(3, 1)) {
            entity.forceChat(Utils.randomElement(QUOTES));
        }

        boolean melee_dist = entity.tile().distance(target.tile()) <= 1;

        // Attack the player
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollDie(2, 1)) {
            entity.animate(6948);
            // If we're in melee distance it's actually classed as if the target hit us -- has an effect on auto-retal in gwd!
            if (GwdLogic.isBoss(entity.getAsNpc().id())) {
                Map<Entity, Long> last_attacked_map = entity.getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<Entity, Long>());
                last_attacked_map.put(target, System.currentTimeMillis());
                entity.putAttrib(AttributeKey.LAST_ATTACKED_MAP, last_attacked_map);
            }

            if (Utils.rollDie(5, 1)) {
                int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
                entity.forceChat("YARRRRRRR!"); // Overrides previous quote
                target.hit(entity, hit, CombatType.MELEE).submit();
                target.message("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
                target.getSkills().alterSkill(Skills.PRAYER,-20);
            } else {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
            }
        } else {
            entity.animate(6950);
            var tileDist = entity.tile().distance(target.tile());
            int durationMagic = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(entity, target, 1227, 51, durationMagic, 1, 5, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
        }

        // Slight chance of poison
        if (Utils.rollDie(10, 1)) {
            target.poison(16);
        }
        return true;
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
