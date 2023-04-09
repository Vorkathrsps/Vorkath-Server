package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author PVE
 * @Since augustus 05, 2020
 */
public class Gargoyle extends CommonCombatMethod {

    public static int getNormalId() {
        return 412;
    }

    public static int getCrumblingId() {
        return 412;
    }

    public static void onDeath(NPC npc) {
        npc.transmog(getNormalId());
    }

    public static void smash(Player player, NPC npc, boolean manual) {
        if (npc.getCombat().getTarget() != player) {
            player.message("That gargoyle is not fighting you.");
            return;
        }
        if (manual && npc.hp() > 9) {
            player.message("The gargoyle is not weak enough to be smashed!");
            return;
        }

        player.animate(401);
        String plural = player.getEquipment().containsAny(ItemIdentifiers.GRANITE_MAUL, ItemIdentifiers.GRANITE_MAUL_12848, ItemIdentifiers.GRANITE_MAUL_24225) ? "granite maul" : "rock hammer";
        player.message("You smash the Gargoyle with the "+plural+".");
        npc.hp(0, 0);
        npc.die();
        npc.transmog(getCrumblingId());
        npc.animate(1520);
    }

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
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
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
