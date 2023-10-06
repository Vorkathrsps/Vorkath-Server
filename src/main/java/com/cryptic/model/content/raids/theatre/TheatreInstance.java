package com.cryptic.model.content.raids.theatre;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.bloat.Bloat;
import com.cryptic.model.content.raids.theatre.boss.maiden.Maiden;
import com.cryptic.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.Sotetseg;
import com.cryptic.model.content.raids.theatre.boss.xarpus.Xarpus;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreController;
import com.cryptic.model.content.raids.theatre.stage.*;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cryptic.model.content.mechanics.DeathProcess.*;
import static com.cryptic.model.content.mechanics.DeathProcess.SOTETSEG_AREA;

/**
 * @Author: Origin
 * @Date: 10/5/2023
 */
public class TheatreInstance extends TheatreArea {

    @Getter public Player owner;
    @Getter List<TheatreHandler> npcList = new ArrayList<>();
    @Getter public TheatreController theatreController = new TheatreController(npcList);
    @Getter public List<Player> occupiedCageSpawnPointsList = new ArrayList<>();
    @Getter public List<Player> players;
    @Getter public static TheatrePhase theatrePhase = new TheatrePhase(TheatreStage.ONE);
    Tile entrance = new Tile(3219, 4454);

    public static Area[] rooms() {
        int[] regions = {12613, 12869, 13125, 12612, 12611, 12687, 13123, 13122};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public TheatreInstance(Player owner, List<Player> players) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, rooms());
        this.owner = owner;
        this.players = players;
    }

    public TheatreInstance buildParty() {
        owner.setInstance(this);
        owner.teleport(entrance.transform(0, 0, this.getzLevel()));
        owner.setTheatreState(TheatreState.ACTIVE);
        owner.setRaidDeathState(RaidDeathState.ALIVE);
        owner.setRoomState(RoomState.INCOMPLETE);
        for (var p : players) {
            if (p != owner) {
                if (p != null) {
                    p.setInstance(owner.getTheatreInstance());
                    p.teleport(entrance.transform(0, 0, owner.getTheatreInstance().getzLevel()));
                    p.setTheatreState(TheatreState.ACTIVE);
                    p.setRaidDeathState(RaidDeathState.ALIVE);
                    p.setRoomState(RoomState.INCOMPLETE);
                }
            }
        }
        return this;
    }

    public void startRaid() {
        npcList.add(new Maiden());
        npcList.add(new Xarpus());
        npcList.add(new Bloat());
        npcList.add(new Vasilias());
        npcList.add(new Sotetseg());
        theatreController.build(this.owner, this);
    }

    public void onRoomStateChanged(RoomState roomState) {
        if (roomState == RoomState.COMPLETE) {
            players.forEach(p -> {
                if (p.tile().inArea(VERZIK_AREA)) {
                    p.teleport(3168, 4315, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(BLOAT_AREA)) {
                    p.teleport(3282, 4447, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(MAIDEN_AREA)) {
                    p.teleport(3180, 4447, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(XARPUS_AREA)) {
                    p.teleport(3170, 4389, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(VASILIAS_AREA)) {
                    p.teleport(3295, 4248, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(SOTETSEG_AREA)) {
                    p.teleport(3279, 4316, p.getTheatreInstance().getzLevel());
                }
            });
        }
    }

    public void clear() {
        for (var member : players) {
            Arrays.stream(rooms())//fail safe
                .findFirst()
                .filter(p -> p.contains(member.tile()))
                .ifPresent(p -> member.teleport(new Tile(3670, 3219, 0)));
            member.setTheatreParty(null);
        }
        this.getNpcList().clear();
        this.getPlayers().clear();
    }

    @Override
    public void dispose() {
        super.dispose();
        clear();
    }

}
