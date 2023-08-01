package com.cryptic.model.entity.combat.damagehandler.impl.bolts;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import static com.cryptic.model.entity.combat.ranged.RangedData.boltSpecialChance;
import static com.cryptic.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

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
