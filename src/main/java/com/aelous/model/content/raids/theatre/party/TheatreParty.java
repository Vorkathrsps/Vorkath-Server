package com.aelous.model.content.raids.theatre.party;

import com.aelous.model.content.raids.theatre.stage.RoomState;
import com.aelous.model.entity.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.aelous.model.content.mechanics.Death.*;

public class TheatreParty {

    @Getter @Setter public Player leader;
    @Getter public List<Player> party = new ArrayList<>();

    public TheatreParty(Player leader) {
        this.leader = leader;
    }

    public List<Player> occupiedCageSpawnPointsList = new ArrayList<>();

    public void createParty() {
        if (leader != null && leader.getTheatreParty() == null) {
            this.party.add(leader);
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

    public void onRoomStateChanged(RoomState roomState) {
        if (roomState == RoomState.COMPLETE) {
            for (Player player : party) {
                if (player.tile().inArea(VERZIK_AREA)) {
                    player.teleport(3168, 4315, player.getTheatre().theatreArea.getzLevel());
                } else if (player.tile().inArea(BLOAT_AREA)) {
                    player.teleport(3282, 4447, player.getTheatre().theatreArea.getzLevel());
                } else if (player.tile().inArea(MAIDEN_AREA)) {
                    player.teleport(3180, 4447, player.getTheatre().theatreArea.getzLevel());
                } else if (player.tile().inArea(XARPUS_AREA)) {
                    player.teleport(3170, 4389, player.getTheatre().theatreArea.getzLevel());
                } else if (player.tile().inArea(VASILIAS_AREA)) {
                    player.teleport(3295, 4248, player.getTheatre().theatreArea.getzLevel());
                } else if (player.tile().inArea(SOTETSEG_AREA)) {
                    player.teleport(3279, 4316, player.getTheatre().theatreArea.getzLevel());
                }
            }

        }
    }
}
