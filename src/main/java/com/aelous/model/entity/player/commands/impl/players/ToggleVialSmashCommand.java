package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 21, 2020
 */
public class ToggleVialSmashCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        boolean smashVials = player.getAttribOr(AttributeKey.GIVE_EMPTY_POTION_VIALS, false);
        if(!smashVials) {
            player.putAttrib(AttributeKey.GIVE_EMPTY_POTION_VIALS, true);
            player.message("When drinking your last dose of your potions, vials now get removed.");
        } else {
            player.putAttrib(AttributeKey.GIVE_EMPTY_POTION_VIALS, false);
            player.message("When drinking your last dose of your potions, vials now get added again.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
