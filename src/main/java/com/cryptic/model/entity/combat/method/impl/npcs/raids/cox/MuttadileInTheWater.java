package com.cryptic.model.entity.combat.method.impl.npcs.raids.cox;

import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;

/**
 * @Author Origin
 * @Since October 31, 2021
 */
public class MuttadileInTheWater extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        magicAttack((NPC) entity, (Player) target);
        return true;
    }

    private void magicAttack(NPC npc, Player target) {
        Party party = target.raidsParty;
        if (party == null) {
            return;
        }

        for (Player member : party.getMembers()) {
            if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && member.tile().inArea(new Area(3300, 5319, 3324, 5338, member.raidsParty.getHeight()))) {
                var tileDist = npc.tile().transform(1, 1, 0).distance(target.tile());
                var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
                new Projectile(npc, target, 393, 20, 12 * tileDist, 0, 30, 0).sendProjectile();
                target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 15;
    }
}
