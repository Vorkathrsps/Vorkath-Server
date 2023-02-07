package com.aelous.model.content.minigames.impl.fight_caves;

import com.aelous.model.content.bank_pin.dialogue.BankTellerDialogue;
import com.aelous.model.content.minigames.MinigameManager;
import com.aelous.model.content.minigames.impl.fight_caves.dialogue.TzHaarMejJalDialogue;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * Created by Kaleem on 19/08/2017.
 */
public class TzHaarCityPlugin extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        int npcId = npc.id();

        if (npcId == NpcIdentifiers.TZHAARMEJJAL) {
            if (option == 1 || option == 2) {
                handleMejJal(player);
            }
            return true;
        }

        if (npcId == NpcIdentifiers.TZHAARKETZUH) {
            if(option == 1) {
                player.getDialogueManager().start(new BankTellerDialogue(), npc);
            } else if(option == 2) {
                player.getBank().open();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        if (object.getId() == 11833) { //Fight caves entrance
            if(!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.TZTOK_JAD) && !player.getPlayerRights().isDeveloper(player)) {
                player.message("You need the Tztok Jad Slayer Reward unlocked before you can enter this cave.");
                return true;
            }
            MinigameManager.playMinigame(player, new FightCavesMinigame(63));
            return true;
        } else if (object.getId() == 11834) { //Fight caves leaving
            if(player.getMinigame() != null) {
                player.getMinigame().end(player);
            } else {
                player.teleport(FightCavesMinigame.OUTSIDE);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        return super.handleItemOnObject(player, item, object);
    }

    private void handleMejJal(Player player) {
        player.getDialogueManager().start(new TzHaarMejJalDialogue());
    }
}
