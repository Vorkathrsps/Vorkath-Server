package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;

import static com.cryptic.utility.ItemIdentifiers.GRANITE_MAUL_12848;

/**
 * Granite maul
 *
 * @author Gabriel Hannason
 */
public class GraniteMaul extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        double vigour = 0;

        int specPercentage = (int) (entity.getSpecialAttackPercentage() + vigour);

        //Make sure the player has enough special attack
        if (specPercentage < entity.getAsPlayer().getCombatSpecial().getDrainAmount()) {
            entity.message("You do not have enough special attack energy left!");
            entity.setSpecialActivated(false);
            CombatSpecial.updateBar(entity.getAsPlayer());
            return false;
        }
        entity.animate(1667);
        entity.graphic(340, GraphicHeight.HIGH, 0);
        //TODO mob.world().spawnSound(mob.tile(), 2715, 0, 10)

        int delay = 0;
        if (entity.isPlayer() && target.isPlayer()) {
            int renderIndexOf = World.getWorld().getPlayers().getRenderOrderInternal().indexOf(entity.getIndex());
            int renderIndexOf2 = World.getWorld().getPlayers().getRenderOrderInternal().indexOf(target.getIndex());
            delay = renderIndexOf > renderIndexOf2 ? 1 : 0;
        }

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), delay, CombatType.MELEE).checkAccuracy();

        if (hit.getDamage() > 49) {
            hit.setDamage(entity.getAsPlayer().getEquipment().hasAt(EquipSlot.WEAPON, GRANITE_MAUL_12848) ? 50 : 49);
        }

        hit.submit();
        CombatSpecial.drain(entity, entity.getAsPlayer().getCombatSpecial().getDrainAmount());
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
