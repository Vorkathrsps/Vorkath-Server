package com.cryptic.model.entity.combat.method.impl.npcs.aggression;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.npc.AggressionCheck;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class RevenantAggro implements AggressionCheck {
    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        NPC npc = (NPC) entity;
        Player player = (Player) victim;
        if (ArrayUtils.contains(FormulaUtils.isRevenant(), npc.getId())) {
            return !player.getEquipment().contains(ItemIdentifiers.BRACELET_OF_ETHEREUM);
        }
        return true;
    }
}
