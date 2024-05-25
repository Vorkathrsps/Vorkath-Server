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
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import static com.cryptic.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

public class DragonStoneBolts implements AmmunitionDamageEffectListener {
    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        if (damage <= 0 || combatType != CombatType.RANGED || !(entity instanceof Player player)) {
            return 0;
        }

        boolean alwaysSpec = zaryteCrossBowEvoke(player);
        boolean canPerformDragonsBreath = true;

        if (target instanceof NPC npc && npc.isCombatDummy()) {
            alwaysSpec = true;
        }

        if (target instanceof Player targ && targ.isPlayer()) {
            boolean potionEffect = (int) targ.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0) > 0;
            canPerformDragonsBreath = !(potionEffect || Equipment.hasDragonProtectionGear(targ));
        }

        if (player.getEquipment().containsAny(ItemIdentifiers.DRAGONSTONE_DRAGON_BOLTS_E, ItemIdentifiers.DRAGONSTONE_BOLTS_E)) {
            if (Utils.percentageChance(RangedData.boltSpecialChance(alwaysSpec)) && canPerformDragonsBreath) {
                target.performGraphic(new Graphic(756, GraphicHeight.HIGH, 60));
                int currentRangeLevel = player.getSkills().level(Skills.RANGED);
                double boltSpecialMultiplier = currentRangeLevel * 0.20;
                damage += boltSpecialMultiplier;
            }
            return damage;
        }
        return 0;
    }
}
