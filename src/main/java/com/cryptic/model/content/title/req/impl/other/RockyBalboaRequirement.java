package com.cryptic.model.content.title.req.impl.other;

import com.cryptic.model.content.title.req.TitleRequirement;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

/**
 * @author Patrick van Elderen | February, 15, 2021, 18:02
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RockyBalboaRequirement extends TitleRequirement {

    public RockyBalboaRequirement() {
        super("Must have completed<br>Punching bags III");
    }

    @Override
    public boolean satisfies(Player player) {
        return player.<Boolean>getAttribOr(AttributeKey.ROCKY_BALBOA_TITLE_UNLOCKED, false);
    }

}
