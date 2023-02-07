package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.rights.MemberRights;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

public class YellCommand implements Command {

    private static int getYellDelay(Player player) {
        int yellTimer = 60;
        MemberRights memberRights = player.getMemberRights();

        switch (memberRights) {
            case RUBY_MEMBER -> yellTimer = 30;
            case SAPPHIRE_MEMBER -> yellTimer = 20;
            case EMERALD_MEMBER -> yellTimer = 10;
            case DIAMOND_MEMBER -> yellTimer = 5;
            case DRAGONSTONE_MEMBER, ONYX_MEMBER, ZENYTE_MEMBER -> yellTimer = 0;
        }
        return player.getPlayerRights().isStaffMember(player) ? 0 : yellTimer;
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (player.muted()) {
            player.message("You are muted and cannot yell. Please try again later.");
            return;
        }
        if(player.jailed()) {
            player.message("You are jailed and cannot yell. Please try again later.");
            return;
        }
        int kc = player.getAttribOr(AttributeKey.PLAYER_KILLS, 0);
        if (player.getMemberRights().getRightValue() < MemberRights.RUBY_MEMBER.getRightValue() && player.getPlayerRights() == PlayerRights.PLAYER && kc < 50) {
            player.message("Only Members and players with over 50 kills in the wilderness can yell.");
            return;
        }
        if (player.getYellDelay().active()) {
            int secondsRemaining = player.getYellDelay().secondsRemaining();
            player.message("Please wait " + secondsRemaining + " more seconds before using this yell again...");
            player.message("<col=ca0d0d>Note:<col=0> Abusing yell results in a <col=ca0d0d>permanent<col=0> mute.");
            player.message("<col=255>Note:<col=0> Different types of Membership allow you to yell more often.");
            return;
        }

        String yellMessage = command.substring(5);
        if (yellMessage.length() > 80) {
            yellMessage = yellMessage.substring(0, 79);
        }
        if (Utils.blockedWord(yellMessage)) {
            player.message("<col=ca0d0d>Please refrain from using foul language in the yell chat! Thanks.");
            return;
        }

        //#Text colour
        String yellColour = player.getAttribOr(AttributeKey.YELL_COLOUR, "006601");

        //System.out.println("[Global] <img=" + (player.getPlayerRights().getRight())
        //    + "</img> " + player.getUsername() + ":<col=" + colour + "> " + Misc.ucFirst(yellMessage));

        //#Name colour is based on member rights not player rights
        String nameColour = player.getMemberRights().yellNameColour();

        boolean ignoreStaffColour = true;

        //#Staff colours can be different
        if(ignoreStaffColour) {
            switch (player.getPlayerRights()) {
                case MODERATOR -> nameColour = Color.WHITE.tag();
                case ADMINISTRATOR, OWNER -> nameColour = Color.RED.tag();
                case SUPPORT -> nameColour = Color.CYAN.tag();
            }
        }

        //# This was made by Ken to ensure the client size it needs [Global]
        //String mainChannel = "[Global]";

        //# The player icon
        String playerIcon = player.getPlayerRights().getSpriteId() != -1 ? "<img=" + player.getPlayerRights().getSpriteId() + ">" : "";
        String memberIcon = player.getMemberRights().getSpriteId() != -1 ? "<img=" + player.getMemberRights().getSpriteId() + ">" : "";

        //# The username...
        String username = player.getUsername();

        //# The message but formatted proper usages of capitals and such.
        String formatYellMessage = Utils.ucFirst(yellMessage);

        //# Constructs a world message
        //World.getWorld().sendWorldMessage(mainChannel + " " + icon + "</img> "+nameColour+"" + username + ":</col><col=" + yellColour + "> " + formatYellMessage);

        //System.out.println(yellColour);
        World.getWorld().sendWorldMessage("<shad=1>"+nameColour+"["+playerIcon+"</img>"+memberIcon+"</img>"+username+"]</col>: <col="+yellColour+">"+formatYellMessage+"</shad>");
        int yellDelay = getYellDelay(player);
        if (yellDelay > 0) {
            player.getYellDelay().start(yellDelay);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
