package com.cryptic.clientscripts.impl.skills.information;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class SkillInformationInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SKILL_INFORMATION;
    }

    @Override
    public void beforeOpen(Player player) {
        int viewedSkill = player.<Integer>getAttribOr(AttributeKey.SKILL_INFORMATION, -1);
        setEvents(new EventNode(8, 0, 2000));
        setEvents(new EventNode(7, 0, 200));
        player.getPacketSender().runClientScriptNew(1902, viewedSkill, 0);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.SKILL_INFORMATION_CLOSE) {
            player.clearAttrib(AttributeKey.SKILL_INFORMATION);
            this.gameInterface().close(player);
        }
    }
}
