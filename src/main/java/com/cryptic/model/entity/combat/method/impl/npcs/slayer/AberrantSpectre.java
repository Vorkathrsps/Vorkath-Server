package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.ItemIdentifiers;

public class AberrantSpectre extends CommonCombatMethod {
    private static final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC};
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }
        entity.animate(entity.attackAnimation());
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 336, 51, duration, 43, 31, 0, entity.getSize(), 10);
        final int delay = p.send(entity.tile(), target.tile());

        Player player = (Player) target;

        if(!player.getEquipment().contains(ItemIdentifiers.NOSE_PEG) && !player.getEquipment().wearingSlayerHelm()) {
            player.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC) + 3, CombatType.MAGIC).submit();
            for (int skill : DRAIN) {
                player.getSkills().alterSkill(skill, -6);
            }
            player.message("<col=ff0000>The aberrant spectre's stench disorients you!");
            player.message("<col=ff0000>A nose peg can protect you from this attack.");
        } else {
            player.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}
