package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.totems;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.Ashihama;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.instance.NightmareInstance;
import com.cryptic.model.entity.npc.NPC;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SouthWestTotem extends CommonCombatMethod {
    @Getter @Setter int damageCount = 0;
    @Override
    public void init(NPC npc) {
        npc.noRetaliation(true);
        npc.getCombat().setAutoRetaliate(false);
    }
    @Override
    public void preDefend(Hit hit) {
        hit.setHitMark(HitMark.YELLOW_ARROW_UP);
    }
    @Override
    public void postDamage(Hit hit) {
        var player = hit.getAttacker().getAsPlayer();
        damageCount += hit.getDamage();
        player.getPacketSender().sendProgressBar(75006, damageCount);
    }

    @Override
    public ArrayList<Entity> getPossibleTargets(Entity mob) {
        return Arrays.stream(mob.closePlayers(64)).filter(p -> NightmareInstance.room().contains(p)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        return false;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        var player = hit.getAttacker().getAsPlayer();
        var totem = (NPC) this.entity;

        if (player == null || totem == null) {
            return false;
        }

        var totems = player.getNightmareInstance().getTotems();

        totems.remove(totem);

        setDamageCount(0);
        totem.transmog(9436);
        totem.setCombatInfo(World.getWorld().combatInfo(9436));
        totem.setHitpoints(totem.maxHp());
        totem.noRetaliation(true);
        totem.getCombat().setAutoRetaliate(false);
        if (player.getInstancedArea() != null) {
            player.getInstancedArea().addNpc(totem);
        }
        player.getPacketSender().sendProgressBar(75006, 100);
        return true;
    }
}
