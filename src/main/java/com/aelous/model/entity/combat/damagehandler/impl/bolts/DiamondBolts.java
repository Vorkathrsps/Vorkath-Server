package com.aelous.model.entity.combat.damagehandler.impl.bolts;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;
import com.aelous.model.entity.combat.ranged.RangedData;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import static com.aelous.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

public class DiamondBolts implements AmmunitionDamageEffectListener {

    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        var always_spec = false;

        if (damage <= 0 || combatType != CombatType.RANGED) {
            return 0;
        }

        if (target instanceof NPC npc) {
            if (npc.isCombatDummy()) {
                always_spec = true;
            }
        }

        if (entity instanceof Player player) {

            if (zaryteCrossBowEvoke(player)) {
                always_spec = true;
            }
            if (player.getEquipment().containsAny(ItemIdentifiers.DIAMOND_DRAGON_BOLTS_E, ItemIdentifiers.DIAMOND_BOLTS_E)) {
                if (Utils.percentageChance(RangedData.boltSpecialChance(always_spec))) {
                    player.putAttrib(AttributeKey.ARMOUR_PIERCING, true);
                    target.performGraphic(new Graphic(758, GraphicHeight.HIGH));
                    damage *= 1.15;
                }
                return damage;
            }
        }
        return 0;
    }
}
