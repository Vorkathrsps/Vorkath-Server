package com.aelous.model.entity.combat.damagehandler.impl.bolts;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import static com.aelous.model.entity.combat.ranged.RangedData.boltSpecialChance;
import static com.aelous.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

public class RubyBolts implements AmmunitionDamageEffectListener {
    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        if (damage <= 0 || combatType != CombatType.RANGED || !(entity instanceof Player player)) {
            return 0;
        }

        boolean alwaysSpec = zaryteCrossBowEvoke(player);

        if (target instanceof NPC npc && npc.isCombatDummy()) {
            alwaysSpec = true;
        }

        if (player.getEquipment().containsAny(ItemIdentifiers.RUBY_DRAGON_BOLTS_E, ItemIdentifiers.RUBY_BOLTS_E)) {
            if (Utils.percentageChance(boltSpecialChance(alwaysSpec))) {
                int cap = 100;

                target.performGraphic(new Graphic(754, GraphicHeight.LOW, 55 + 5));

                int selfDamage = (int) (player.getSkills().level(Skills.HITPOINTS) * 0.1);
                if (selfDamage < player.getSkills().level(Skills.HITPOINTS)) {
                    int targetHP = target.hp();
                    damage += targetHP * 0.2;
                    if (damage > cap)
                        damage = cap;

                    if (!target.getAsNpc().isCombatDummy()) {
                        player.hit(player, selfDamage, 0, null).setIsReflected().submit();
                    }
                    return damage;
                }
            }
        }
        return 0;
    }
}
