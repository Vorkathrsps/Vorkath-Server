package com.aelous.model.content.raids.theatre;

import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.bloat.Bloat;
import com.aelous.model.content.raids.theatre.boss.maiden.Maiden;
import com.aelous.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.aelous.model.content.raids.theatre.boss.sotetseg.Sotetseg;
import com.aelous.model.content.raids.theatre.boss.xarpus.Xarpus;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.content.raids.theatre.controller.TheatreController;
import com.aelous.model.content.raids.theatre.party.TheatreParty;
import com.aelous.model.content.raids.theatre.stage.TheatrePhase;
import com.aelous.model.content.raids.theatre.stage.TheatreStage;
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
public class Theatre extends TheatreParty { //TODO clear the raid upon completion or leave
    List<TheatreRaid> boss = new ArrayList<>();
    Tile entrance = new Tile(3219, 4454);
    Tile sotet = new Tile(3279, 4309);
    public TheatreController theatreController = new TheatreController(boss);

    public TheatreArea theatreArea;
    @Getter public static TheatrePhase theatrePhase = new TheatrePhase(TheatreStage.ONE);
    public static final Area[] rooms() {
        int[] regions = {12613, 12869, 13125, 12612, 12611, 12687, 13123, 13122};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public Theatre(@Nullable Player leader, @Nullable Player member, TheatreArea theatreArea) {
        super(leader, member);
        this.theatreArea = theatreArea;
    }

    public void startRaid() {
        boss.add(new Maiden());
        boss.add(new Xarpus());
        boss.add(new Bloat());
        boss.add(new Vasilias());
        boss.add(new Sotetseg());
        theatreController.build(this.leader, this, this.theatreArea);
        this.leader.setInstance(theatreArea);
        this.leader.teleport(sotet.transform(0, 0, theatreArea.getzLevel()));
    }

    public void dispose() {
        boss.clear();
    }

}
