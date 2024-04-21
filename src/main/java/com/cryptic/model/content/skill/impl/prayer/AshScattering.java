package com.cryptic.model.content.skill.impl.prayer;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.timers.TimerKey;

public class AshScattering extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        int id = item.getId();
        Ashes ash = Ashes.get(id);
        if (option == 1) {
            if (ash != null) {
                if (item.getId() == ash.id) {
                    if (player.getTimers().has(TimerKey.BONE_BURYING)) return true;
                    scatter(player, ash);
                    return true;
                }
            }
        }
        return false;
    }

    public final void scatter(final Player player, final Ashes ashes) {
        player.animate(2295);
        player.sendPrivateSound(2295);
        player.getInventory().remove(ashes.id);
        player.getSkills().addXp(Skills.PRAYER, ashes.experience);
        player.getTimers().extendOrRegister(TimerKey.BONE_BURYING, 3);
    }
}
