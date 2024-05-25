package com.cryptic.model.entity.combat.method.impl.npcs.voidisland;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.chainedwork.Chain;

public class TorcherCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance()) return false;
        int tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 647, 41, duration, 50, 30, 16, entity.getSize(), 5);
        entity.animate(3882);
        final int delay = entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public void doFollowLogic() {
        follow((int) this.calculateMaxAllowedDistance());
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return (int) this.calculateMaxAllowedDistance();
    }


    @Override
    public void onDeath(Player killer, NPC npc) {
        if (killer != null) {
            var current = killer.<Integer>getAttribOr(AttributeKey.VOID_ISLAND_POINTS, 0);
            current += 1;
            killer.putAttrib(AttributeKey.VOID_ISLAND_POINTS, current);
            killer.message("<img=2009>" + Color.COOL_BLUE.wrap("<shad=0>You have received 1 Void Island point.</shad>"));
            killer.message("<img=2009>" + Color.PURPLE.wrap("<shad=0>You now have a Total of " + current + " Void Island points.</shad>"));
        }
        npc.animate(3881);
    }
}
