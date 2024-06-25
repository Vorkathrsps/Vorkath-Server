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

public class JadeBolt implements AmmunitionDamageEffectListener {

    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        if (damage <= 0 || combatType != CombatType.RANGED || !(entity instanceof Player player)) {
            return 0;
        }

        boolean alwaysSpec = zaryteCrossBowEvoke(player);

        if (target instanceof NPC npc && npc.isCombatDummy()) {
            alwaysSpec = true;
        }

        if (player.getEquipment().containsAny(ItemIdentifiers.JADE_DRAGON_BOLTS_E, ItemIdentifiers.JADE_BOLTS_E)) {
            if (Utils.percentageChance(boltSpecialChance(player, alwaysSpec))) {
                double boltSpecialMultiplier = 1.18;
                damage *= boltSpecialMultiplier;
                target.performGraphic(new Graphic(755, GraphicHeight.LOW, 55 + 5));
                if (target instanceof NPC npc) {
                    if (!npc.isCombatDummy())
                        target.stun(10);
                }
                return damage;
            }
        }
        return 0;
    }
}
