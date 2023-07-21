package com.aelous.model.content.raids.theatre.party;

import com.aelous.model.entity.player.Player;
import lombok.Getter;

import java.util.*;

public class TheatreParty {

    public Player leader;
    public Player member;
    @Getter public List<Player> party = new ArrayList<>();

    public TheatreParty(Player leader, Player member) {
        this.leader = leader;
        this.member = member;
    }

    public void create() {
        party.add(leader);
        leader.message("Your party has been successfully created.");
    }

    public void invite() {
        if (member != null && !party.contains(member)) {
            party.add(member);
            member.message(member.getDisplayName() + " has been invited to the party.");
        } else {
            leader.message(leader.getDisplayName() + " is already in the party.");
        }
    }

    public void join() {
        if (member != null && party.contains(member)) {
            party.add(member);
            member.message("You've joined the party.");
            for (var m : party) {
                if (m.equals(member)) {
                    m.message(member.getDisplayName() + " has joined the party.");
                }
            }
        } else {
            if (member != null && party.contains(member)) {
                leader.message(member.getDisplayName() + " is already in the party.");
            }
        }
    }

    public void leave() {
        if (member != null && party.contains(member)) {
            party.remove(member);
            for (var m : party) {
                m.message(member.getDisplayName() + " has left the party.");
            }
        }
    }

    public void disband() {
        if (leader != null && member != null) {
            leader.message("You've disbanded the party.");
            member.message("The party was disbanded.");
            clear();
        }
    }

    public void clear() {
        party.clear();
    }

}
