package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;

/**
 * @author PVE
 * @Since augustus 05, 2020
 */
public class DesertLizardsCombat extends CommonCombatMethod {

    public static void iceCooler(Player player, NPC npc, boolean manual) {
        if (npc.getCombat().getTarget() != player) {
            player.message("That desert lizard is not fighting you.");
            return;
        }

        if (manual && npc.hp() > 4) {
            player.message("The desert lizard is not weak enough to be cooled!");
            return;
        }

        if(!player.inventory().contains(ItemIdentifiers.ICE_COOLER)) {
            player.message("You need at least one ice cooler.");
            return;
        }

        player.animate(2779);
        npc.graphic(85);
        player.inventory().remove(new Item(ItemIdentifiers.ICE_COOLER, 1));
        npc.hp(0, 0);
        npc.die();
    }

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
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
