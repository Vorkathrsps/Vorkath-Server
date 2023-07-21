package com.aelous.model.content.raids.theatre;

import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.bloat.Bloat;
import com.aelous.model.content.raids.theatre.boss.maiden.Maiden;
import com.aelous.model.content.raids.theatre.boss.nylocas.VasiliasHandler;
import com.aelous.model.content.raids.theatre.boss.xarpus.Xarpus;
import com.aelous.model.content.raids.theatre.controller.Raid;
import com.aelous.model.content.raids.theatre.controller.TheatreController;
import com.aelous.model.content.raids.theatre.party.TheatreParty;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class Theatre extends TheatreParty { //TODO clear the raid upon completion or leave
    List<Raid> boss = new ArrayList<>();
    TheatreController theatreController = new TheatreController(boss);
    Tile entrance = new Tile(3219, 4454);
    public TheatreArea theatreArea;
    public static final Area[] rooms() {
        int[] regions = new int[] {12613, 12869, 13125, 12612, 12611, 12687, 13123, 13122};
        var areas = new ArrayList<Area>();
        for (int region : regions) {
            areas.add(new Area(Tile.regionToTile(region).getX(),
                Tile.regionToTile(region).getY(),
                Tile.regionToTile(region).getX() + 63,
                Tile.regionToTile(region).getY() + 63));
        }
        return areas.toArray(Area[]::new);
    }

    public Theatre(@Nullable Player leader, @Nullable Player member, TheatreArea theatreArea) {
        super(leader, member);
        this.theatreArea = theatreArea;
    }

    public void startRaid() {
        this.construct();
        this.leader.setInstance(theatreArea);
        this.leader.teleport(entrance.transform(0, 0, theatreArea.getzLevel()));
    }

    protected void construct() {
        boss.add(new Maiden());
        boss.add(new Xarpus());
        boss.add(new Bloat());
        boss.add(new VasiliasHandler());
        theatreController.build(this.leader,this, this.theatreArea);
    }

    public void clearRaid() {
        boss.clear();
    }

}
