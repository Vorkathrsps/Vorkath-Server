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
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.container.equipment.Equipment;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import static com.aelous.model.entity.combat.ranged.RangedData.zaryteCrossBowEvoke;

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
