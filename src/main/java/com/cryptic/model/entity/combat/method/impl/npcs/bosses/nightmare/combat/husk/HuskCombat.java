package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.husk;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.npc.NPC;

public class HuskCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) {
            return false;
        }

        if (entity == null || target == null) {
            return false;
        }

        attack((NPC) entity, target);

        return true;
    }

    void attack(NPC husk, Entity target) {
        if (husk == null || target == null) {
            return;
        }

        husk.animate(8564);

        Hit hit = target.hit(husk, CombatFactory.calcDamageFromType(husk, target, CombatType.MAGIC), 1, CombatType.MAGIC);

        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
            var damage = hit.getDamage();
            hit.setDamage((int) (damage * 1.50));
            hit.submit();
        } else {
            hit.submit();
        }
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
