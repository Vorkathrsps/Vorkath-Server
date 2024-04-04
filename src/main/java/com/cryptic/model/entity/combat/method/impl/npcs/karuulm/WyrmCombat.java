package com.cryptic.model.entity.combat.method.impl.npcs.karuulm;

import com.cryptic.model.World;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;

import java.util.function.BooleanSupplier;

/**
 * The combat script for the wyrm.
 *
 * @author Origin | December, 22, 2020, 14:16
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WyrmCombat extends CommonCombatMethod {

    boolean transformed;
    final int[] ids = new int[]{8610, 8611};

    @Override
    public void onRespawn(NPC npc) {
        transformed = false;
        if (npc.getId() == 8611) {
            npc.transmog(8610, true);
        }
    }

    @Override
    public void preDefend(Hit hit) {
        NPC wyrm = (NPC) entity;
        if (!transformed) {
            wyrm.transmog(8611, true);
            wyrm.animate(8268);
            wyrm.lockDamageOk();
            Chain.noCtx().runFn(2, () -> {
                transformed = true;
                wyrm.unlock();
            });
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC wyrm = (NPC) entity;
        if (wyrm.getId() == 8610) return false;
        var inMeleeDistance = withinDistance(1);
        if (inMeleeDistance && World.getWorld().rollDie(2, 1)) basicAttack(wyrm, target);
        else magicAttack(wyrm, target);
        return true;
    }

    private void basicAttack(Entity entity, Entity target) {
        entity.animate(8270);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void magicAttack(Entity entity, Entity target) {
        entity.animate(8271);
        int tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 335, 51, duration, 60, 31, 12, entity.getSize(), 10);
        var delay = entity.executeProjectile(p);
        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        new Hit(entity, target, hit, delay - 1, CombatType.RANGED).checkAccuracy(true).submit();
        target.performGraphic(new Graphic(1635, GraphicHeight.LOW, p.getSpeed()));
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }

    @Override
    public void onRetreat(Entity entity, BooleanSupplier waitUntil, BooleanSupplier cancel, AttributeKey key) {
        var npc = (NPC) entity;
        final int[] ticks = {4};
        if (ArrayUtils.contains(ids, npc.id())) {
            int offset = npc.id() - 1;
            npc.setEntityInteraction(null);
            npc.waitUntil(waitUntil, () ->
                Chain.noCtx().repeatingTask(1, tick -> {
                    ticks[0]--;
                    if (ticks[0] <= 0) {
                        npc.transmog(offset, true);
                        npc.clearAttribs();
                        tick.stop();
                    }
                }).cancelWhen(() -> {
                    npc.clearAttrib(key);
                    return cancel.getAsBoolean();
                })).cancelWhen(() -> {
                npc.clearAttrib(key);
                return cancel.getAsBoolean();
            });
        }
    }
}
