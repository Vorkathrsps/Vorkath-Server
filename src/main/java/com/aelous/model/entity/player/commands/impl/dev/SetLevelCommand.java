package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.items.Item;

/**
 * @author PVE
 * @Since augustus 28, 2020
 */
public class SetLevelCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 3) {
            player.message("Usage: ::lvl skill level. Example: ::lvl 1 99");
        } else {
            int skill = Integer.parseInt(parts[1]);
            int lvl = Integer.parseInt(parts[2]);
            if (!(skill >= 0 && skill < Skills.SKILL_COUNT)) {
                player.message("Invalid skill id: " + skill);
                return;
            }
            // Any equip?
            if (!player.getPlayerRights().isDeveloper(player)) {
                if (skill == Skills.HITPOINTS && lvl < 10) {
                    player.message("Hitpoints cannot go under <col=FF0000>10</col>.");
                    lvl = 10;
                }
                for (Item item : player.getEquipment().getItems()) {
                    if (item != null) {
                        player.message("You cannot change your levels while having <col=FF0000>equipment</col> on.");
                        return;
                    }
                }
            }

            // Turn off prayers
            Prayers.closeAllPrayers(player);

            player.getSkills().setXp(skill, Skills.levelToXp(Math.min(99, lvl)));
            player.getSkills().update();
            player.getSkills().recalculateCombat();
            player.message("Skill <col=FF0000>" + Skills.SKILL_NAMES[skill] + "</col> set to <col=FF0000>" + player.getSkills().levels()[skill] + "</col>.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isDeveloper(player);
    }
}
