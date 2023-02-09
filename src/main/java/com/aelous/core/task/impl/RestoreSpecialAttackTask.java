package com.aelous.core.task.impl;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.Combat;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.Equipment;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.ItemIdentifiers;

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
