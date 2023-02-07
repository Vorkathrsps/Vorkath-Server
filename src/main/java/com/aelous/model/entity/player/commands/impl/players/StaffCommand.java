package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.relations.PlayerRelations;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.utility.Color;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick van Elderen | July, 05, 2021, 22:10
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class StaffCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        //Generate a list of staff members online
        List<Player> staff = World.getWorld().getPlayers().stream().filter(px -> px != null && px.getPlayerRights().isStaffMember(px)).collect(Collectors.toList());
        int admins = 0, mods = 0, helpers = 0;

        StringBuilder staffOnline = new StringBuilder("<col=800000>Administrators:");

        for (Player staffmember : staff) {
            if (staffmember.getPlayerRights() == PlayerRights.ADMINISTRATOR || staffmember.getPlayerRights() == PlayerRights.DEVELOPER) {
                if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                    continue;

                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                admins++;
            }
        }

        if (admins == 0) {
            staffOnline.append(" - Nobody");
        }

        staffOnline.append("<br><br><br><col=800000>Moderators:");
        for (Player staffmember : staff) {
            if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                continue;
            if (staffmember.getPlayerRights() == PlayerRights.MODERATOR) {
                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                mods++;
            }
        }

        if (mods == 0) {
            staffOnline.append("<br><br>- Nobody");
        }

        staffOnline.append("<br><br><br><col=800000>Helpers:");
        for (Player staffmember : staff) {
            if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                continue;
            if (staffmember.getPlayerRights().isSupport(player)) {
                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                helpers++;
            }
        }

        if (helpers == 0) {
            staffOnline.append("<br><br>- Nobody");
        }
        String admin_s = admins != 1 ? "s" : "";
        String mod_s = mods != 1 ? "s" : "";
        String hepl_s = helpers != 1 ? "s" : "";
        int total = admins + helpers + mods;
        String breakdown = total == 0 ? "None" : total + " (" + admins + " admin" + admin_s + ", " + mods + " mod" + mod_s + ", " + helpers + " helper" + hepl_s + ")";
        player.sendScroll(Color.MAROON.wrap("Staff Online: " + breakdown), staffOnline.toString());
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
