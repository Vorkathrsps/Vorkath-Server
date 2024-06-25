package com.cryptic.model.entity.combat.damagehandler.impl.ammo;

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
            if (Utils.percentageChance(boltSpecialChance(player, alwaysSpec))) {
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
