package com.aelous.model.content.title.req.impl.other;

import com.aelous.model.content.title.req.TitleRequirement;
import com.aelous.model.entity.player.Player;

/**
 * @author Patrick van Elderen | January, 20, 2021, 13:04
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class CustomRequirement extends TitleRequirement {

    public CustomRequirement() {
        super("Must be at least an<br>elite member");
    }

    @Override
    public boolean satisfies(Player player) {
        return player.getMemberRights().isEliteMemberOrGreater(player);
    }

}
