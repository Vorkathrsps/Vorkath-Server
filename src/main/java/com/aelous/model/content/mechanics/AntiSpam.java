package com.aelous.model.content.mechanics;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.PlayerPunishment;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since November 01, 2021
 */
public class AntiSpam {

    /**
     * Most commonly used domain name endings.
     */
    private final static String[] flaggedDomainEndings =
        {".com", ".net", ".co.uk", ".io", ".org"};

    /**
     * Websites an advertiser may use to tell players to go on them to type in a specific search term.
     */
    private final static String[] flaggedSearchWebsites =
        {"googl", "youtube", "yt"};

    /**
     * Alterinative synonyms to tell someone to type in something on a website.
     */
    private final static String[] flaggedSearchWords =
        {"search", "type", "look"};

    /**
     * Ip mute a new player for advertising.
     */
    public static boolean isNewPlayerSpamming(Player player, String textSent) {//TODO get rid of this class
        if (player.<Long>getAttribOr(AttributeKey.GAME_TIME,0) < 600L) {
            int flagPoint = 0;
            for (int index = 0; index < player.newPlayerChat.size(); index++) {
                String oldText = player.newPlayerChat.get(index).toLowerCase();
                boolean flaggedThisText = false;

                // Give the player +1 flag point player for advertising a website, for example "moparscape.org"
                for (String flaggedDomainText : flaggedDomainEndings) {
                    String flaggedDomainTextWithDot = flaggedDomainText.replace(".", "dot");
                    flaggedDomainTextWithDot = flaggedDomainTextWithDot.replace(".", "dot");

                    String flaggedDomainTextWithDotAndSpace = flaggedDomainText.replace(".", "dot ");
                    if (oldText.contains(flaggedDomainText) || oldText.contains(flaggedDomainTextWithDot) || oldText.contains(flaggedDomainTextWithDotAndSpace)) {
                        flagPoint++;
                        flaggedThisText = true;
                        break;
                    }
                }
                if (flaggedThisText) {
                    continue;
                }

                boolean oldTextHasSearchWebsite = false;
                boolean oldTextHasSearchWordText = false;
                // Give the player +1 flag point player for advertising in a search manor such as "search for autoswitcher on google"
                for (String flaggedSearchWebsiteText : flaggedSearchWebsites) {
                    if (oldText.contains(flaggedSearchWebsiteText)) {
                        oldTextHasSearchWebsite = true;
                        break;
                    }
                }
                for (String flaggedSearchWordText : flaggedSearchWords) {
                    if (oldText.contains(flaggedSearchWordText)) {
                        oldTextHasSearchWordText = true;
                        break;
                    }
                }
                if (oldTextHasSearchWebsite && oldTextHasSearchWordText) {
                    flagPoint++;
                }

            }
            if (flagPoint >= 2) {
                player.putAttrib(AttributeKey.MUTED, true);
                if (!PlayerPunishment.IPmuted(player.getHostAddress())) {
                    PlayerPunishment.ipmute(player.getHostAddress());
                }
                return true;
            }
            player.newPlayerChat.add(textSent.toLowerCase());
            if (player.newPlayerChat.size() > 5) {
                player.newPlayerChat.remove(0);
            }
        }
        return false;
    }
}
