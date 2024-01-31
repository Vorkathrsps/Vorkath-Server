package com.cryptic.model.content.sigils.io;

import com.cryptic.model.content.sigils.AbstractSigilHandler;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public class DeftStrikes extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {

    }

    @Override
    protected void damageModification(Player player, Hit hit) {

    }

    @Override
    protected void accuracyModification(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {
        if (!attuned(player)) return;
        var boost = 1.20;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> boost = 1.21;
            case SAPPHIRE_MEMBER -> boost = 1.22;
            case EMERALD_MEMBER -> boost = 1.23;
            case DIAMOND_MEMBER -> boost = 1.24;
            case DRAGONSTONE_MEMBER -> boost = 1.25;
            case ONYX_MEMBER -> boost = 1.26;
            case ZENYTE_MEMBER -> boost = 1.27;
        }
        if (player.getCombat().getCombatType().isRanged()) rangeAccuracy.modifier += boost;
        else if (player.getCombat().getCombatType().isMagic()) magicAccuracy.modifier += boost;
        else if (player.getCombat().getCombatType().isMelee()) meleeAccuracy.modifier += boost;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.DEFT_STRIKES);
    }

    @Override
    protected boolean activated(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.RANGED) || player.getCombat().getCombatType().equals(CombatType.MELEE) || player.getCombat().getCombatType().equals(CombatType.MAGIC);
    }
}
