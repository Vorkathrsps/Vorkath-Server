package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.masks.Direction;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | May, 16, 2021, 13:25
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DirectionSwitching {

    public static void switchDirectionsWest(Party party) {
        if(party.getGreatOlmNpc().lastDirection() == Direction.NORTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7343));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.NORTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7342));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.SOUTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7344));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.SOUTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7340));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.NONE.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7341));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.NONE.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7339));
        }
        Chain.bound(null).runFn(2, () -> {
            OlmAnimations.resetAnimation(party);
        });
    }

    public static void switchDirectionsEast(Party party) {
        if(party.getGreatOlmNpc().lastDirection() == Direction.NORTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7344));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.NORTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7340));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.SOUTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7343));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.SOUTH.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NONE.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7342));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.NONE.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.NORTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7339));
        }
        if(party.getGreatOlmNpc().lastDirection() == Direction.NONE.toInteger() && party.getGreatOlmNpc().spawnDirection() == Direction.SOUTH.toInteger()) {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7341));
        }
        Chain.bound(null).runFn(2, () -> {
            OlmAnimations.resetAnimation(party);
        });
    }
}
