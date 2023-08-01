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

public class OpalBolts implements AmmunitionDamageEffectListener {

    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        if (damage <= 0 || combatType != CombatType.RANGED || !(entity instanceof Player player)) {
            return 0;
        }

        boolean alwaysSpec = zaryteCrossBowEvoke(player);

        if (target instanceof NPC npc && npc.isCombatDummy()) {
            alwaysSpec = true;
        }
        if (player.getEquipment().containsAny(ItemIdentifiers.OPAL_DRAGON_BOLTS_E, ItemIdentifiers.OPAL_BOLTS_E)) {
            if (Utils.percentageChance(boltSpecialChance(alwaysSpec))) {
                int current_range_level = player.getSkills().level(Skills.RANGED);
                target.performGraphic(new Graphic(749, GraphicHeight.LOW, 55 + 5));
                double boltSpecialMultiplier = (current_range_level * 0.10); // Can max deal 25% extra damage.
                damage += boltSpecialMultiplier;
                return damage;
            }
        }
        return 0;
    }
}
