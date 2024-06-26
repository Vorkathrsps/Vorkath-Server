package com.cryptic.model.content.title.req.impl.other;

import com.cryptic.model.content.title.req.TitleRequirement;
import com.cryptic.model.entity.player.Player;

/**
 * @author Origin | January, 20, 2021, 13:04
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
