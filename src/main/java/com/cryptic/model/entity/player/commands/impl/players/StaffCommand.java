package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.relations.PlayerRelations;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.utility.Color;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Origin | July, 05, 2021, 22:10
 * 
 */
public class StaffCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        List<Player> staff = World.getWorld().getPlayers().stream().filter(px -> px != null && px.getPlayerRights().isStaffMember(px)).toList();
        int admins = 0, mods = 0, helpers = 0, cm = 0, owner = 0;

        StringBuilder staffOnline = new StringBuilder("<lsprite=2175></img> <col=800000>Owner:");

        for (Player staffmember : staff) {
            if (staffmember.getPlayerRights() == PlayerRights.OWNER) {
                if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                    continue;

                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                owner++;
            }
        }

        if (owner == 0) {
            staffOnline.append(" - Nobody");
        }

        staffOnline.append("<br><br><br><lsprite=2174></img>  <col=800000>Community Managers:");

        for (Player staffmember : staff) {
            if (staffmember.getPlayerRights() == PlayerRights.COMMUNITY_MANAGER) {
                if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                    continue;

                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                cm++;
            }
        }

        if (cm == 0) {
            staffOnline.append("<br><br>- Nobody");
        }

        staffOnline.append("<br><br><br><lsprite=2172></img>  <col=800000>Administrators:");

        for (Player staffmember : staff) {
            if (staffmember.getPlayerRights() == PlayerRights.ADMINISTRATOR) {
                if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                    continue;

                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                admins++;
            }
        }

        if (admins == 0) {
            staffOnline.append("<br><br>- Nobody");
        }

        staffOnline.append("<br><br><br><lsprite=2171></img> <col=800000>Moderators:");
        for (Player staffmember : staff) {
            if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) continue;
            if (staffmember.getPlayerRights() == PlayerRights.MODERATOR) {
                staffOnline.append("<br><br> - ").append(staffmember.getUsername());
                mods++;
            }
        }

        if (mods == 0) {
            staffOnline.append("<br><br>- Nobody");
        }

        staffOnline.append("<br><br><br><lsprite=2170></img> <col=800000>Support:");
        for (Player staffmember : staff) {
            if (staffmember.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.OFF) // Pm OFF
                continue;
            if (staffmember.getPlayerRights() == PlayerRights.SUPPORT) {
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
