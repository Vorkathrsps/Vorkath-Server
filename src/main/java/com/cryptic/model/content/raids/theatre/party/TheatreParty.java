package com.cryptic.model.content.raids.theatre.party;

import com.cryptic.model.entity.player.Player;
import lombok.Getter;

import java.util.List;

public class TheatreParty {
    @Getter Player owner;
    @Getter public List<Player> players;

    public TheatreParty(Player owner, List<Player> players) {
        this.owner = owner;
        this.players = players;
    }

    public void addOwner() {
        if (!players.contains(owner))
            players.add(owner);
    }

    public void clear() {
        players.clear();
    }

}
