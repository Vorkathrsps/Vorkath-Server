package com.cryptic.model.entity.combat.damagehandler.impl.ammo;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import static com.cryptic.model.entity.combat.ranged.RangedData.boltSpecialChance;
import static com.cryptic.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

public class OnyxBolts implements AmmunitionDamageEffectListener {

    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        if (damage <= 0 || combatType != CombatType.RANGED || !(entity instanceof Player player)) {
            return 0;
        }

        boolean alwaysSpec = zaryteCrossBowEvoke(player);

        if (target instanceof NPC npc && npc.isCombatDummy()) {
            alwaysSpec = true;
        }

        if (player.getEquipment().containsAny(ItemIdentifiers.ONYX_DRAGON_BOLTS_E, ItemIdentifiers.ONYX_BOLTS_E)) {
            if (Utils.percentageChance(boltSpecialChance(player, alwaysSpec))) {
                target.performGraphic(new Graphic(753, GraphicHeight.LOW, 55 + 5));
                double boltSpecialMultiplier = 1.20; //20% extra damage
                damage *= boltSpecialMultiplier;
                int heal = (int) (damage * 0.25);

                if (player.hp() < 99) {
                    player.setHitpoints(player.hp() + heal);
                }
                return damage;
            }
        }
        return 0;
    }
}
