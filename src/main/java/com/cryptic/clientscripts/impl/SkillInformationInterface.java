package com.cryptic.clientscripts.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class SkillInformationInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.SKILL_INFORMATION;
    }

    @Override
    public void beforeOpen(Player player) {
        System.out.println(gameInterface().getId() << 16);
//        setEvents(new EventNode(ComponentID.));
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
