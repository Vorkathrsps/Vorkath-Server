package com.aelous.model.entity.player.commands.impl.dev;



import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import org.mindrot.jbcrypt.BCrypt;

public class CreateServerLagCommand implements Command {
    /*
     * This command is absolutely great for testing our lag monitoring in log4j2 and in Discord.
     * This command is super useful.
     */
    @Override
    public void execute(Player player, String command, String[] parts) {
        //Method one of creating server lag. Running BCrypt hashing on the game thread.
        int rounds = 15;
        if (parts.length == 2) {
            rounds = Integer.parseInt(parts[1]);
        }
        player.message("Creating " + rounds + " salt rounds BCrypt hash.");
        BCrypt.hashpw("hello", BCrypt.gensalt(rounds));

        //Method two of creating server lag. Depositing over 300 items with refreshing the container after each deposit.
        /*for (int i = 0; i < GameConstants.PVP_ALLOWED_SPAWNS.length; i++ ) {
            player.getBank(Bank.getTabForItem(player, GameConstants.PVP_ALLOWED_SPAWNS[i])).add(new Item(GameConstants.PVP_ALLOWED_SPAWNS[i], 10000));
        }*/

    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }
}
