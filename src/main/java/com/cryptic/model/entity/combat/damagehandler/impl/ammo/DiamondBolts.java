package com.cryptic.model.entity.combat.damagehandler.impl.ammo;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;
import com.cryptic.model.entity.combat.ranged.RangedData;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import static com.cryptic.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

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
                if (Utils.percentageChance(RangedData.boltSpecialChance(player, always_spec))) {
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
