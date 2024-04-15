package com.cryptic.model.entity.combat.damagehandler.impl.typeless;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;

import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

public class PrayerDamage implements DamageModifyingListener {
    private static final String[] ignoredNegatedDamage = new String[]{"corporeal beast", "dragon", "king black", "graardor", "kree", "zilyana", "vorkath", "maiden", "verzik", "xarpus", "bloat", "sotetseg", "enormous tentacle", "kraken", "tsutsaroth", "olm", "alchemical hydra", "nex", "venenatis"};
    private final int[] ignoreFullNegatedDamage = new int[]{CORPOREAL_BEAST, KING_BLACK_DRAGON, KING_BLACK_DRAGON_6502, KING_BLACK_DRAGON_2642, GENERAL_GRAARDOR, GENERAL_GRAARDOR_6494, KREEARRA, KREEARRA_6492, COMMANDER_ZILYANA, COMMANDER_ZILYANA_6493, KRIL_TSUTSAROTH, KRIL_TSUTSAROTH_6495, ENORMOUS_TENTACLE, VORKATH, VORKATH_8058, VORKATH_8059, VORKATH_8060, VORKATH_8061, VORKATH_11959, VERZIK_VITUR, VERZIK_VITUR_8369, VERZIK_VITUR_8370, VERZIK_VITUR_8371, VERZIK_VITUR_8372, VERZIK_VITUR_8373, VERZIK_VITUR_8374, VERZIK_VITUR_8375, VERZIK_VITUR_10830, VERZIK_VITUR_10831, VERZIK_VITUR_10832, VERZIK_VITUR_10833, VERZIK_VITUR_10834, VERZIK_VITUR_10835, VERZIK_VITUR_10836, VERZIK_VITUR_10847, VERZIK_VITUR_10848, VERZIK_VITUR_10849, VERZIK_VITUR_10850, VERZIK_VITUR_10851, VERZIK_VITUR_10852, VERZIK_VITUR_10853};

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var target = hit.getTarget();
        if (target instanceof Player player) {
            var meleePrayer = (Prayers.usingPrayer(player, PROTECT_FROM_MELEE) && CombatType.MELEE.equals(hit.getCombatType()));
            var rangedPrayer = (Prayers.usingPrayer(player, PROTECT_FROM_MISSILES) && CombatType.RANGED.equals(hit.getCombatType()));
            var magicPrayer = (Prayers.usingPrayer(player, PROTECT_FROM_MAGIC) && CombatType.MAGIC.equals(hit.getCombatType()));
            if (hit.isAccurate()) {
                if (hit.prayerIgnored) return false;
                var damage = hit.getDamage();
                if (damage <= 0) return false;
                if (meleePrayer || rangedPrayer || magicPrayer) {
                    if (entity instanceof NPC npc) {
                        if (ArrayUtils.contains(ignoreFullNegatedDamage, npc.getId())) {
                            damage = (int) (1 + (damage * 0.66D));
                            hit.setDamage(damage);
                            if (damage <= 0) hit.block();
                            return true;
                        }
                        hit.block();
                        return true;
                    }
                    damage = (int) (1 + (damage * 0.4));
                    hit.setDamage(damage);
                    return true;
                }
            }
        }
        return false;
    }
}
