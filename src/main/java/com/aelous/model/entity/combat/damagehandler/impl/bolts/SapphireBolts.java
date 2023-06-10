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

public class SapphireBolts implements AmmunitionDamageEffectListener {
    @Override
    public int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage) {
        if (damage <= 0 || combatType != CombatType.RANGED || !(entity instanceof Player player)) {
            return 0;
        }

        boolean alwaysSpec = zaryteCrossBowEvoke(player);

        if (target instanceof NPC npc && npc.isCombatDummy()) {
            alwaysSpec = true;
        }

        if (player.getEquipment().containsAny(ItemIdentifiers.SAPPHIRE_DRAGON_BOLTS_E, ItemIdentifiers.SAPPHIRE_BOLTS_E)) {
            if (Utils.percentageChance(boltSpecialChance(alwaysSpec))) {
                Player t = target.getAsPlayer();
                t.performGraphic(new Graphic(751, GraphicHeight.LOW, 55 + 5));
                t.getSkills().alterSkill(Skills.PRAYER, -20);
                t.getPacketSender().sendMessage("Your Prayer level has been leeched.");

                player.getSkills().alterSkill(Skills.PRAYER, +20);
                player.getPacketSender().sendMessage("Your enchanted bolts leech some Prayer points from your opponent..");
                return damage;
            }
        }
        return 0;
    }
}
