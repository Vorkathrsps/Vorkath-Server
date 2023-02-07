package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.save.PlayerSave;

public class ChangePasswordCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {

        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }

       player.setNameScript("Enter a new password:", new InputScript() {

           @Override
           public boolean handle(Object value) {
               String input = (String) value;
               if (input.length() > 2 && input.length() < 20) {
                   if (PlayerSave.playerExists(player.getUsername())) {
                       player.setNewPassword(input);
                   } else {
                       player.setPassword(input);
                   }
                   player.message("You have successfully changed your password.");
               } else {
                   player.message("Invalid password input.");
               }
               return false;
           }
       });


    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
