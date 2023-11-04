package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class CaveHorror extends CommonCombatMethod {

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        Player player = (Player) target;
        if (player.getEquipment().getId(EquipSlot.AMULET) != 8923) {
            entity.animate(4237);
            player.hit(entity, Utils.random(target.maxHp() / 10), CombatType.MELEE).submit();
            player.message("<col=ff0000>The cave horror's scream rips through you!");
            player.message("<col=ff0000>A witchwood icon can protect you from this attack.");
        } else
            basicAttack(entity, target);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
