package com.aelous.model.content.skill.impl.fletching.impl;

import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author PVE
 * @Since augustus 01, 2020
 */
public class Ballistae extends PacketInteraction {

    private static final int MONKEY_TAIL = ItemIdentifiers.MONKEY_TAIL;
    private static final int SPRING = ItemIdentifiers.BALLISTA_SPRING;
    private static final int LIMBS = ItemIdentifiers.BALLISTA_LIMBS;

    private static final int LIGHT_FRAME = ItemIdentifiers.LIGHT_FRAME;
    private static final int HEAVY_FRAME = ItemIdentifiers.HEAVY_FRAME;

    private static final int INCOMPLETE_LIGHT = ItemIdentifiers.INCOMPLETE_LIGHT_BALLISTA;
    private static final int INCOMPLETE_HEAVY = ItemIdentifiers.INCOMPLETE_HEAVY_BALLISTA;

    private static final int UNSTRUNG_LIGHT = ItemIdentifiers.UNSTRUNG_LIGHT_BALLISTA;
    private static final int UNSTRUNG_HEAVY = ItemIdentifiers.UNSTRUNG_HEAVY_BALLISTA;

    private static final int LIGHT_BALLISTA = ItemIdentifiers.LIGHT_BALLISTA;
    private static final int HEAVY_BALLISTA = ItemIdentifiers.HEAVY_BALLISTA;

    private void craft(Player player, double exp, int levelReq, Item itemOne, Item itemTwo, int result) {
        if (player.getSkills().level(Skills.FLETCHING) < levelReq) {
            DialogueManager.sendStatement(player, "<col=369>You need a Fletching level of " + levelReq + " to do that.");
            return;
        }

        Chain.bound(player).runFn(1, () -> {
            player.inventory().remove(itemOne);
            player.inventory().remove(itemTwo);
            player.inventory().add(new Item(result, 1));
            player.animate(7172);
            player.getSkills().addXp(Skills.FLETCHING, exp);
        }).then(4, player::resetAnimation);
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == MONKEY_TAIL || usedWith.getId() == MONKEY_TAIL) && (use.getId() == SPRING || usedWith.getId() == SPRING)) {
            player.message("The frame must first be combined with a pair of ballista limbs.");
            return true;
        }

        if ((use.getId() == MONKEY_TAIL || usedWith.getId() == MONKEY_TAIL) && (use.getId() == LIGHT_FRAME || usedWith.getId() == LIGHT_FRAME)) {
            player.message("The frame must first be combined with a pair of ballista limbs.");
            return true;
        }

        if ((use.getId() == MONKEY_TAIL || usedWith.getId() == MONKEY_TAIL) && (use.getId() == LIMBS || usedWith.getId() == LIMBS)) {
            player.message("The limbs must first be combined with a ballista frame.");
            return true;
        }

        if ((use.getId() == SPRING || usedWith.getId() == SPRING) && (use.getId() == LIMBS || usedWith.getId() == LIMBS)) {
            player.message("The limbs must first be combined with a ballista frame.");
            return true;
        }

        if ((use.getId() == MONKEY_TAIL || usedWith.getId() == MONKEY_TAIL) && (use.getId() == INCOMPLETE_LIGHT || usedWith.getId() == INCOMPLETE_LIGHT)) {
            player.message("You should add a ballista spring before attaching the tail.");
            return true;
        }

        if ((use.getId() == LIMBS || usedWith.getId() == LIMBS) && (use.getId() == LIGHT_FRAME || usedWith.getId() == LIGHT_FRAME)) {
            craft(player, 15.0, 30, use, usedWith, INCOMPLETE_LIGHT);
            return true;
        }

        if ((use.getId() == LIMBS || usedWith.getId() == LIMBS) && (use.getId() == HEAVY_FRAME || usedWith.getId() == HEAVY_FRAME)) {
            craft(player, 30.0, 72, use, usedWith, INCOMPLETE_HEAVY);
            return true;
        }

        if ((use.getId() == SPRING || usedWith.getId() == SPRING) && (use.getId() == INCOMPLETE_LIGHT || usedWith.getId() == INCOMPLETE_LIGHT)) {
            craft(player, 15.0, 30, use, usedWith, UNSTRUNG_LIGHT);
            return true;
        }

        if ((use.getId() == SPRING || usedWith.getId() == SPRING) && (use.getId() == INCOMPLETE_HEAVY || usedWith.getId() == INCOMPLETE_HEAVY)) {
            craft(player, 30.0, 72, use, usedWith, UNSTRUNG_HEAVY);
            return true;
        }

        if ((use.getId() == MONKEY_TAIL || usedWith.getId() == MONKEY_TAIL) && (use.getId() == UNSTRUNG_LIGHT || usedWith.getId() == UNSTRUNG_LIGHT)) {
            craft(player, 300.0, 30, use, usedWith, LIGHT_BALLISTA);
            return true;
        }

        if ((use.getId() == MONKEY_TAIL || usedWith.getId() == MONKEY_TAIL) && (use.getId() == UNSTRUNG_HEAVY || usedWith.getId() == UNSTRUNG_HEAVY)) {
            craft(player, 600.0, 72, use, usedWith, HEAVY_BALLISTA);
            return true;
        }

        return false;
    }

}
