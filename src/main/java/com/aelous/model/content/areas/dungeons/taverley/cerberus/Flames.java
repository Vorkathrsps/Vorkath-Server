package com.aelous.model.content.areas.dungeons.taverley.cerberus;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.cerberus.CerberusRegion;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.Optional;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.FLAMES;

public class Flames extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == FLAMES) {
                //Crossing the flames (Both interaction options)
                int interactionOption = player.getAttrib(AttributeKey.INTERACTION_OPTION);
                if (interactionOption == 1) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, "Do you wish to pass through the flames?", "Yes - I know I'll get hurt.", "No way!");
                            setPhase(0);
                        }
                        @Override
                        public void select(int option) {
                            if (getPhase() == 0) {
                                if(option == 1) {
                                    moveThroughFlames(player, obj);
                                } else {
                                    stop();
                                }
                            }
                        }
                    });
                } else {
                    moveThroughFlames(player, obj);
                }
                return true;
            }
        }
        return false;
    }

    private void moveThroughFlames(Player player, GameObject obj) {
        int x = player.tile().x;
        int y = player.tile().y;
        int region = player.tile().region();
       Optional<CerberusRegion> cerberusRegion = CerberusRegion.valueOfRegion(region);

        if (y < obj.tile().y) {
            player.setPositionToFace(null);
            player.getMovementQueue().clear();
            player.getMovementQueue().interpolate(x, y + 2);
            player.hit(player,5);
        } else {
            player.setPositionToFace(null);
            player.getMovementQueue().clear();
            player.getMovementQueue().interpolate(x, y - 2);
            player.hit(player,5);
        }
    }
}
