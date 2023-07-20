package com.aelous.model.content.raids.theatre;

import com.aelous.model.content.instance.InstanceConfiguration;
import com.aelous.model.content.minigames.impl.tempoross.area.TemporossArea;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.aelous.model.content.raids.theatre.party.TheatreParty;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class Theatre extends TheatreParty {
    Tile entrance = new Tile(3219, 4454);
    public static final Area[] rooms = new Area[]
         {
             new Area(3152, 4415, 3231, 4464),//maiden
             new Area(3295, 4290, 3260, 4335),//sotetseg
             new Area(3326, 4423, 3263, 4467),//bloat
             new Area(3275, 4285, 3314, 4231),//nylo
             new Area(3152, 4403, 3188, 4369, 1),//xarpus , transform z+1
             new Area(3186, 4294, 3150, 4331) //verzik
         };
    public TheatreArea theatreArea;
    public Theatre(@Nullable Player leader, @Nullable Player member, TheatreArea theatreArea) {
        super(leader, member);
        this.theatreArea = theatreArea;
    }
    public void startRaid() {
        create();
        this.leader.setInstance(theatreArea);
        this.leader.teleport(entrance.transform(0, 0, theatreArea.getzLevel()));
    }

    public void buildBossRooms() {
        MaidenProcess maidenProcess = new MaidenProcess(8360, new Tile(3162, 4444, 0), this.leader);
        maidenProcess.spawn(false);
    }
}
