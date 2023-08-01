package com.cryptic.model.content.areas.dungeons.taverley.cerberus;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.PORTCULLIS_21772;

public class Portcullis extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(obj.getId() == PORTCULLIS_21772) {
            Tile destination = obj.tile().equals(new Tile(1239, 1225)) ? new Tile(1291, 1253) : //West
                obj.tile().equals(new Tile(1367, 1225)) ? new Tile(1328, 1253) : //East
                    obj.tile().equals(new Tile(1303, 1289)) ? new Tile(1309, 1269) : //North
                        new Tile(0, 0);

            if (option == 1) {
                teleportPlayer(player, destination);
            }
            return true;
        }
        return false;
    }

    private void teleportPlayer(Player player, Tile tile) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, "Do you wish to leave?", "Yes, I'm scared.", "Nah, I'll stay.");
                setPhase(0);
            }
            @Override
            public void select(int option) {
                if (getPhase() == 0) {
                    if(option == 1) {
                        player.teleport(tile);
                    }
                    stop();
                }
            }
        });
    }
}
