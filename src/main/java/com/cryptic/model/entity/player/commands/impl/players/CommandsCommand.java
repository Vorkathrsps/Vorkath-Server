package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameConstants;
import com.cryptic.GameServer;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CommandsCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        player.message("Opening commands list...");

        List<String> commands = new ArrayList<>();
        commands.add("<br><col=" + Color.MITHRIL.getColorValue() + ">Command [arguments] (optional arguments) - description</col>");
        commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">PVP Commands:</col></shad>");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::skull</col> - Gives you a regular skull.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::redskull</col> - Gives you a red skull.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::kdr</col> - Display your KDR in chat.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::chins</col> - Teleports you to the black chins in wilderness.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::mb</col> - Teleports you to the mage bank.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::50s</col> - Teleports you to level 50 wilderness.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::44s</col> - Teleports you to level 44 wilderness.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::graves</col> - Teleports you to the graves in wilderness.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::wests</col> - Teleports you to wests in wilderness.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::easts</col> - Teleports you to easts in wilderness.");

        commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Regular Commands:</col></shad>");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::event</col> - Teleports you to the current event area.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::duel</col> - Teleports you to the Duel Arena.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::gamble</col> - Teleports you to the gambling area.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::changepassword</col> - Change your password.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::vote</col> - Opens the store on the " + GameServer.getServerType().getName() + " website.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::donate</col> - Opens the store on the " + GameServer.getServerType().getName() + " website.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::discord</col> - Opens the link to our Discord server.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::rules</col> - Opens the link to our rules.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::yell</col> - Allows you to use the yell channel.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::claimpet</col> - Picks up your pet.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::home</col> - Teleport home.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::exit</col> - Leave the tournament.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::shops</col> - Teleport to the shops area.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::staff</col> - Shows you all staff members online.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::creationdate</col> - Display your creation date in chat.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::players</col> - Displays the number of players.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::empty</col> - Empties your inventory.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::clearbank</col> - Empties your bank.");
        commands.add("<br><col=" + Color.RED.getColorValue() + ">::togglevialsmash</col> - Smashes vials into pieces.");

        if (player.getMemberRights().isRegularMemberOrGreater(player)) {
            commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Donor Commands:</col></shad>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::dzone</col> - Teleports you to the donor zone.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::unskull</col> - Removes your skull.");
        }

        if (player.getMemberRights().isSuperMemberOrGreater(player)) {
            commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Super Donor Commands:</col></shad>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::pickyellcolour</col> - Picks yell colour.");
        }

        if (player.getMemberRights().isExtremeMemberOrGreater(player)) {
            commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Extreme Donor Commands:</col></shad>");
        }

        if (player.getPlayerRights().isSupport(player)) {
            commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Support Commands:</col></shad>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::teleto [username]</col> - Teleports to the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::mute </col> - Mutes the player");
        }

        if (player.getPlayerRights().isModerator(player)) {
            commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Moderator Commands:</col></shad>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::unmute </col> - Unmutes the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::unipmute </col> - Unmutes the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::unban </col> - Unbans the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::teletome [username]</col> - Teleports the specified player to you.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::modzone </col> - Teleports to the mod zone.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::vanish (on or off)</col> - Makes you invisible.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::unvanish (on or off)</col> - Makes you visible.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkbank </col> - Check the bank of an online or offline player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkinv </col> - Check the inventory of an online player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::modzone </col> - Teleport to the Moderator only zone.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::sz </col> - Teleport to the Staff Zone.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::simulate </col> - Simulate the drop table of any NPC.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::kick [username]</col> - disconnects the player.");
        }

        if (player.getPlayerRights().isAdministrator(player)) {
            commands.add("<br><br><shad=0><col=" + Color.GREEN.getColorValue() + ">Administrator Commands:</col></shad>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::ban [username]</col>");
            commands.add("example: <br><col=" + Color.RED.getColorValue() + ">::ban player</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::ipban [username]</col>");
            commands.add("example: <br><col=" + Color.RED.getColorValue() + ">::ipban player</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::kick [username]</col> - Disconnects (kicks) the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::exit [username]</col> - Closes the client of the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::resetslayertask [username]</col> - Resets a player's slayer task.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::setslayerstreak [username] [amount]</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::giveslayerpoints [username] [amount]</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::spawnkey</col> - Spawns the wilderness key without timer.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::tele [x] [y] (z)</col> - Teleports to the specified coordinates.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::getip [player]</col> - Displays the IP of the specified player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::kill (player)</col> - Kills the specified or current player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::broadcast [text]</col> - Displays a yellow message.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::osrsbroadcast</col> - Displays a yellow OSRS message.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::dismissosrsbroadcast</col> - Dismisses the last OSRS message.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::deletepin [username]</col> - Deletes the bank pin for the player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::copypass [online username] [offline username]</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::changepassother [username]</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::dropparty [radius] [percentage] [itemId]</col>");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::setmember [username] [rights]</col> - rights number 0 to 8.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::promote [username] [rights]</col> - rights number 0 to 5.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::alert [text]</col> - Sends a client-side alert with text.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::globalmsg [text]</col> - Sends a server-side message of text.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkbank [username]</col> - Check a players bank.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkplayerwealth [username]</col> Check wealth of a Player.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkinv [username]</col> - Check a players inventory.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkequip [username]</col> - Check a players equipment.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::giveitem [username] [id] [amount]</col> - Gives a player an item.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::updatepass [username]</col> - Sets the users pass from the site.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::checkip [IP]</col> - Finds alts based on IP.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::up</col> - Teleports you one height level up.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::down</col> - Teleports you one height level down.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::setlevelo</col> - Sets a skill level for other players.");
            commands.add("<br><col=" + Color.RED.getColorValue() + ">::opentorn</col> - Starts a tournament immediately.");
        }

        player.sendScroll("<col=" + Color.MAROON.getColorValue() + ">Commands List</col>", Collections.singletonList(commands).toString().replaceAll(Pattern.quote("["), "").replaceAll(Pattern.quote("]"), "").replaceAll(",", ""));
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
