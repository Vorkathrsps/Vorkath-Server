package com.cryptic.core.task.impl;

import com.cryptic.core.task.Task;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

/**
 * A {@link Task} implementation which handles
 * the regeneration of special attack.
 * 
 * @author Professor Oak
 */
public class RestoreSpecialAttackTask extends Task {

    private final Entity entity;

    public RestoreSpecialAttackTask(Entity entity) {
        super("RestoreSpecialAttackTask",  50, entity, false);
        this.entity = entity;
        entity.setRecoveringSpecialAttack(true);

    }

    @Override
    public void execute() {

        if (entity == null || !entity.isRegistered() || entity.getSpecialAttackPercentage() >= 100 || !entity.isRecoveringSpecialAttack()) {
            if (entity != null) {
                entity.setRecoveringSpecialAttack(false);
            }

            stop();
            return;
        }

        boolean wearingLightBearer = entity.getAsPlayer().getEquipment().contains(ItemIdentifiers.LIGHTBEARER);

        int healSpecPercentageBy = 10;
        int specPercentageRegenLightBearer = 20;
        int amount = wearingLightBearer ? entity.getSpecialAttackPercentage() + specPercentageRegenLightBearer : entity.getSpecialAttackPercentage() + healSpecPercentageBy;

        if (amount >= 100) {
            amount = 100;
            entity.setRecoveringSpecialAttack(false);
            stop();
        }

        entity.setSpecialAttackPercentage(amount);

        if (entity.isPlayer()) {
            Player player = entity.getAsPlayer();
            CombatSpecial.updateBar(player);
            if (amount == 50 || amount == 100) {
                player.message("Your special attack energy is now " + player.getSpecialAttackPercentage() + "%.");
            }
        }

        CombatSpecial.updateBar((Player) entity);
    }
}
