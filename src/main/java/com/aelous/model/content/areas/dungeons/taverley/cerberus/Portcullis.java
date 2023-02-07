package com.aelous.model.content.areas.dungeons.taverley.cerberus;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.PORTCULLIS_21772;

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
