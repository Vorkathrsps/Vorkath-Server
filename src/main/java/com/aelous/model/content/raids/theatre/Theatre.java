package com.aelous.model.content.raids.theatre;

import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.bloat.Bloat;
import com.aelous.model.content.raids.theatre.boss.maiden.Maiden;
import com.aelous.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.aelous.model.content.raids.theatre.boss.sotetseg.Sotetseg;
import com.aelous.model.content.raids.theatre.boss.xarpus.Xarpus;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.content.raids.theatre.controller.TheatreController;
import com.aelous.model.content.raids.theatre.stage.*;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class Theatre {
    List<TheatreRaid> boss = new ArrayList<>();
    Tile entrance = new Tile(3219, 4454);
    public TheatreController theatreController = new TheatreController(boss);

    public TheatreArea theatreArea;
    public Player player;
    @Getter public static TheatrePhase theatrePhase = new TheatrePhase(TheatreStage.ONE);
    public static Area[] rooms() {
        int[] regions = {12613, 12869, 13125, 12612, 12611, 12687, 13123, 13122};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public Theatre(@Nullable Player player, TheatreArea theatreArea) {
        this.player = player;
        this.theatreArea = theatreArea;
    }

    public void startRaid() {
        boss.add(new Maiden());
        boss.add(new Xarpus());
        boss.add(new Bloat());
        boss.add(new Vasilias());
        boss.add(new Sotetseg());
        theatreController.build(this.player, this, this.theatreArea);
        for (var p : player.getTheatreParty().getParty()) {
            if (p != null) {
                p.setTheatre(this);
                p.setInstance(theatreArea);
                p.teleport(entrance.transform(0, 0, theatreArea.getzLevel()));
                p.setTheatreState(TheatreState.ACTIVE);
                p.setRaidDeathState(RaidDeathState.ALIVE);
                p.setRoomState(RoomState.INCOMPLETE);
            }
        }
    }

    public void dispose() {
        for (var p : player.getTheatreParty().getParty()) {
            if (p != null) {
                p.setTheatre(null);
                p.setTheatreState(null);
                p.setRaidDeathState(null);
                p.setRoomState(null);
                p.teleport(3670, 3218, 0);
            }
        }
        this.boss.clear();
        this.theatreArea.dispose();
    }

}
