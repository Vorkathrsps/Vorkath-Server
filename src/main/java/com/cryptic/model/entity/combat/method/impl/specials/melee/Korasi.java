package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.Prayer;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

public class Korasi extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(32764);
        entity.graphic(32767);
        target.performGraphic(new Graphic(32766, GraphicHeight.LOW, 30));
        boolean isDummy = target.isNpc() && target.getAsNpc().isCombatDummy();
        double maxHit = entity.getCombat().getMaximumMeleeDamage();
        double minHit = maxHit * 0.5;
        double random = Utils.THREAD_LOCAL_RANDOM.get().nextInt((int) (maxHit * 1.5 + 1 - minHit));
        double hitDamage = minHit + random;
        entity.sendPrivateSound(6182, 30);
        if (isDummy) hitDamage = maxHit * 1.5;
        int finalDamage = (int) Math.floor(hitDamage);
        new Hit(entity, target, 1, CombatType.MAGIC)
            .setAccurate(true)
            .setDamage(finalDamage)
            .setHitMark(HitMark.HIT)
            .submit()
            .postDamage(hit -> {
                hit.setAccurate(true);
                hit.setDamage(finalDamage);
                if (target instanceof Player player)
                    if (player.getPrayer().isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) hit.setDamage(hit.getDamage() / 2);
            });
        CombatSpecial.drain(entity, CombatSpecial.KORASI_SWORD.getDrainAmount());
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
