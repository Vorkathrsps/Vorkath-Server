package com.cryptic.clientscripts.impl.skills;

import com.cryptic.clientscripts.impl.skills.information.SkillEnum;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class SkillTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SKILL_TAB;
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        player.clearAttrib(AttributeKey.SKILL_INFORMATION);

        SkillEnum skill = SkillEnum.BUTTON_TO_SKILL_MAP.get(button);
        if (skill != null) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, skill.ordinal() + 1);
        }

        GameInterface.SKILL_INFORMATION.open(player);
    }
}
