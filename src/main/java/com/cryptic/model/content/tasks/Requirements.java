package com.cryptic.model.content.tasks;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

/**
 * A utility class for all the task requirements.
 * @author Origin | April, 08, 2021, 21:52
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Requirements {

    public static long bmRisk(Player player) {
        return player.getAttribOr(AttributeKey.RISKED_WEALTH, 0L);
    }

}
