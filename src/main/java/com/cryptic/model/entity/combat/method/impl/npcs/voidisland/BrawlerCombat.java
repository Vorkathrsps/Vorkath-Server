package com.cryptic.model.entity.combat.method.impl.npcs.voidisland;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;

public class BrawlerCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!isReachable()) return false;
        entity.animate(3897);
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
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
            var increment = 1;
            switch (npc.id()) {
                case 1738 -> increment += 2;
                case 1736 -> increment += 1;
            }
            var current = killer.<Integer>getAttribOr(AttributeKey.VOID_ISLAND_POINTS, 0);
            current += increment;
            killer.putAttrib(AttributeKey.VOID_ISLAND_POINTS, current);
            killer.message("<img=2009>" + Color.COOL_BLUE.wrap("<shad=0>You have received " + increment + " Void Island point.</shad>"));
            killer.message("<img=2009>" + Color.PURPLE.wrap("<shad=0>You now have a Total of " + current + " Void Island points.</shad>"));
        }
        npc.animate(3894);
    }
}
