package com.aelous.model.entity.npc.droptables.impl;

import com.aelous.model.content.areas.burthope.warriors_guild.MagicalAnimator;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.WARRIOR_GUILD_TOKEN;

/**
 * @author Patrick van Elderen | March, 26, 2021, 18:55
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class AnimatedArmourDrops implements Droptable {

    @Override
    public void reward(NPC npc, Player player) {
        if (npc.id() == MagicalAnimator.ArmourSets.BRONZE.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.BRONZE.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.BRONZE.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.BRONZE.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 5));
        } else if (npc.id() == MagicalAnimator.ArmourSets.IRON.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.IRON.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.IRON.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.IRON.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 10));
        } else if (npc.id() == MagicalAnimator.ArmourSets.STEEL.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.STEEL.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.STEEL.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.STEEL.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 15));
        } else if (npc.id() == MagicalAnimator.ArmourSets.BLACK.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.BLACK.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.BLACK.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.BLACK.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 20));
        } else if (npc.id() == MagicalAnimator.ArmourSets.MITHRIL.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.MITHRIL.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.MITHRIL.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.MITHRIL.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 25));
        } else if (npc.id() == MagicalAnimator.ArmourSets.ADAMANT.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.ADAMANT.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.ADAMANT.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.ADAMANT.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 30));
        } else if (npc.id() == MagicalAnimator.ArmourSets.RUNE.npc) {
            drop(npc, player, MagicalAnimator.ArmourSets.RUNE.helm);
            drop(npc, player, MagicalAnimator.ArmourSets.RUNE.legs);
            drop(npc, player, MagicalAnimator.ArmourSets.RUNE.body);
            drop(npc, player, new Item(WARRIOR_GUILD_TOKEN, 40));
        }
    }


}
