package com.cryptic.model.inter.skillinglamp;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

public class SkillingLamp extends PacketInteraction {
    Skill[] skills = Skill.values();
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (player.isPerformingAction()) return false;
        if (item.getId() == 28800) {
            if (option == 1) {
                player.setPerformingAction(true);
                Chain.noCtx().runFn(1, () -> {
                    final Skill randomSkill = Utils.randomElement(skills);
                    final int id = randomSkill.getId();
                    player.getInventory().remove(item.getId());
                    player.getSkills().addXp(id, Utils.random(500, 1000));
                }).then(1, player::clearPerformingAction);
                return true;
            }
        }
        return false;
    }
}
