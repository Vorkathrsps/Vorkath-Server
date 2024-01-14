package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.route.routes.ProjectileRoute;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Origin | January, 08, 2021, 08:48
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class SpiritualMage extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) return false;
        if (ProjectileRoute.hasLineOfSight(entity, target)) {
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1193, 51, duration, 43, 31, 0, entity.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true);

            // Does NOT splash when miss!
            if (target instanceof Player playerTarget) {
                NPC npc = (NPC) entity;
                if (hit.getDamage() > 0) {
                    playerTarget.performGraphic(get_graphic(npc.id())); // Cannot protect from this.
                } else {
                    playerTarget.performGraphic(new Graphic(85, GraphicHeight.LOW, p.getSpeed())); // Cannot protect from this.
                }
            }
        }
        return true;
    }

    private Graphic get_graphic(int npc) {
        return switch (npc) {
            case SPIRITUAL_MAGE_3161, BATTLE_MAGE -> new Graphic(166, GraphicHeight.LOW, 0);
            case SPIRITUAL_MAGE, SARADOMIN_PRIEST, BATTLE_MAGE_1611 -> new Graphic(78, GraphicHeight.HIGH, 0);
            default -> new Graphic(77, GraphicHeight.HIGH, 0);
        };
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 7;
    }
}
