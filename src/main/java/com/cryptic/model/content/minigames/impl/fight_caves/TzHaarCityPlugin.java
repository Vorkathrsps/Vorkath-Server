package com.cryptic.model.content.minigames.impl.fight_caves;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.bank_pin.dialogue.BankTellerDialogue;
import com.cryptic.model.content.minigames.MinigameManager;
import com.cryptic.model.content.minigames.impl.fight_caves.dialogue.TzHaarMejJalDialogue;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

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
