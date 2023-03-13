package com.aelous.model.content.areas.lumbridge.dialogue;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;

/**
 * @author PVE
 * @Since augustus 28, 2020
 */
public class Hans extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.STATEMENT,"Your current play time is: "+getTime());
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            stop();
        }
    }

    private String getTime() {
        long gameTime = player.getAttribOr(AttributeKey.GAME_TIME, 0L);
        int time = (int) (gameTime * 0.6);
        int days = (time / 86400);
        int hours = (time / 3600) - (days * 24);
        int minutes = (time / 60) - (days * 1440) - (hours * 60);
        String minute = minutes > 1 ? "Minutes" : "Minute";
        String hour = hours > 1 ? "Hours" : "Hour";
        String day = days > 1 ? "Days" : "Day";
        return ""+days+" "+day+" "+hours+" "+hour+" "+minutes+" "+minute+".";
    }

    public static String getTimeDHS(Player player) {
        long gameTime = player.getAttribOr(AttributeKey.GAME_TIME, 0L);
        int time = (int) (gameTime * 0.6);
        int days = (time / 86400);
        int hours = (time / 3600) - (days * 24);
        int minutes = (time / 60) - (days * 1440) - (hours * 60);
        String m = "";
        if (days > 0)
            m += ""+days+"d";
        if (hours > 0)
            m += ""+hours+"h";
        if (days < 0 && hours < 0)
            m += ""+minutes+"m";
        return m;
    }
}
