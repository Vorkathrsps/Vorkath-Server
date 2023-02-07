package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.masks.Direction;

/**
 * @author Patrick van Elderen | May, 16, 2021, 12:41
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class OlmAnimations {

    public static void resetAnimation(Party party) {
        if (party.getCurrentPhase() == 3) {
            if (party.getGreatOlmNpc().tile().getX() >= 3238) {
                if (party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7374));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7376));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7375));
            } else {
                if (party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7374));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7375));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7376));
            }
        } else {
            if (party.getGreatOlmNpc().tile().getX() >= 3238) {
                if (party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7336));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7337));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7338));
            } else {
                if (party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7336));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7338));
                else if (party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger())
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7337));
            }
        }
        party.setOlmAttacking(false);
    }

}
