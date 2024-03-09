package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;
import com.cryptic.model.items.container.equipment.EquipmentInfo;

public class FormidableFighter extends AbstractSigil {
    @Override
    protected void onRemove(Player player) {

    }

    @Override
    protected void processMisc(Player player) {

    }

    @Override
    protected void processCombat(Player player, Entity target) {

    }

    @Override
    protected void damageModification(Player player, Hit hit) {
        if (!attuned(player)) return;
        if (hit.isAccurate()) {
            if (World.getWorld().rollDie(20, 1)) {
                int damage = hit.getDamage();
                damage += 5;
                hit.setDamage(damage);
            }
        }
    }

    @Override
    protected void skillModification(Player player) {

    }

    @Override
    protected void resistanceModification(Entity attacker, Entity target, Hit entity) {

    }

    @Override
    protected void accuracyModification(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {
        if (!attuned(player)) return;
        EquipmentBonuses attackerBonus = player.getBonuses().totalBonuses(player, World.getWorld().equipmentInfo());
        attackerBonus.stab += 30;
        attackerBonus.slash += 30;
        attackerBonus.crush += 30;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.FORMIDABLE_FIGHTER);
    }

    @Override
    protected boolean activate(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.MELEE);
    }
}
