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
    List<Raid> raids = new ArrayList<>();
    TheatreController theatreController = new TheatreController(raids);
    public TheatreArea theatreArea;
    Tile entrance = new Tile(3219, 4454);
    public static final Area[] rooms = new Area[]
         {
             new Area(3152, 4415, 3231, 4464),
             new Area(3295, 4290, 3260, 4335),
             new Area(3326, 4423, 3263, 4467),
             new Area(3275, 4285, 3314, 4231),
             new Area(3136, 4352, 3136 + 63, 4352 + 63),
             new Area(3186, 4294, 3150, 4331)
         };

    public Theatre(@Nullable Player leader, @Nullable Player member, TheatreArea theatreArea) {
        super(leader, member);
        this.theatreArea = theatreArea;
    }

    public void startRaid() {
        this.construct();
        this.leader.setInstance(theatreArea);
        this.leader.teleport(entrance.transform(0, 0, theatreArea.getzLevel()));
    }

    public void construct() {
        raids.add(new Maiden());
        raids.add(new Xarpus());
        raids.add(new Bloat());
        raids.add(new VasiliasHandler());
        theatreController.build(this.leader, this.theatreArea);
    }

    public void clearRaid() {
        raids.clear();
    }
}
