package com.aelous.model.content.raids.theatre.party;

import com.aelous.model.entity.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class TheatreParty {

    @Getter @Setter public Player leader;
    @Getter public List<Player> party = new ArrayList<>();

    public TheatreParty(Player leader) {
        this.leader = leader;
    }

    public void createParty() {
        if (leader != null && leader.getTheatreParty() == null) {
            TheatreParty party = new TheatreParty(leader);
            this.party.add(leader);
            leader.setTheatreParty(party);
            leader.message("Your party has been successfully created.");
        }
    }

    public void join() {

    }

    public void leave() {

    }

    public void disband() {

    }

    public void clear() {
        party.clear();
    }

}
