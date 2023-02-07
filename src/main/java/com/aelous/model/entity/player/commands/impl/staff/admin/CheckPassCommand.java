package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class CheckPassCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        //TODO: maybe fix this so it's thread-safe.
        player.message("Coming soon, possibly...");
        //This command is a good example of how BCrypt hashed bank pins can be checked.
        //String[] pieces = command.split(" ");
        //String pass = pieces[1];
        //player.message("Checking pass " + pass);
        //Submit the task to the low-priority thread-pool so we don't lock the game thread. This isn't thread safe, oops.
        /*
        GameEngine.getInstance().submitLowPriority(() -> {
            if (BCrypt.checkpw(pass,player.getPassword())) {
                player.message("Password matches!");
            } else {
                player.message("Password does not match!");
            }
        });*/
        //This will lock the game thread, don't do this.
        /*
        if (BCrypt.checkpw(pieces[1],player.getPassword())) {
            player.message("Password matches!");
        } else {
            player.message("Password does not match!");
        }
        */
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}




