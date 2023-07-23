package com.aelous.model.content.raids.theatre.boss.nylocas;

import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.controller.TheatreRaid;
import com.aelous.model.content.raids.theatre.boss.nylocas.pillars.PillarNpc;
import com.aelous.model.content.raids.theatre.boss.nylocas.pillars.PillarObject;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class Vasilias implements TheatreRaid {

    public List<NPC> pillarNpc = new ArrayList<>();
    public List<NPC> vasiliasNpc = new ArrayList<>();
    public List<GameObject> pillarObject = new ArrayList<>();

    public void clearListener() {
        for (var v : vasiliasNpc) {
            v.die();
        }
        vasiliasNpc.clear();
        for (var n : pillarNpc) {
            n.remove();
        }
        pillarNpc.clear();
        for (var o : pillarObject) {
            o.setId(32862);
        }
       // wave.getAndSet(0);
        pillarObject.clear();
    }

    @Override
    public void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea) {
        PillarNpc pillarNpc1 = (PillarNpc) new PillarNpc(8358, new Tile(3290, 4252, theatreArea.getzLevel()), new PillarObject(32862, new Tile(3289, 4253, theatreArea.getzLevel()), 10, 1, this), this).spawn(false);
        PillarNpc pillarNpc2 = (PillarNpc) new PillarNpc(8358, new Tile(3299, 4252, theatreArea.getzLevel()), new PillarObject(32862, new Tile(3300, 4253, theatreArea.getzLevel()), 10, 2, this), this).spawn(false);
        PillarNpc pillarNpc3 = (PillarNpc) new PillarNpc(8358, new Tile(3299, 4243, theatreArea.getzLevel()), new PillarObject(32862, new Tile(3300, 4242, theatreArea.getzLevel()), 10, 3, this), this).spawn(false);
        PillarNpc pillarNpc4 = (PillarNpc) new PillarNpc(8358, new Tile(3290, 4243, theatreArea.getzLevel()), new PillarObject(32862, new Tile(3289, 4242, theatreArea.getzLevel()), 10, 0, this), this).spawn(false);
        pillarNpc1.spawnPillarObject();
        pillarNpc2.spawnPillarObject();
        pillarNpc3.spawnPillarObject();
        pillarNpc4.spawnPillarObject();
        pillarNpc1.setInstance(theatreArea);
        pillarNpc2.setInstance(theatreArea);
        pillarNpc3.setInstance(theatreArea);
        pillarNpc4.setInstance(theatreArea);
    }

    @Override
    public int scale(NPC npc, Theatre theatre) {
        int scaledHitpoints;

        if (theatre.getParty().size() <= 3) {
            scaledHitpoints = (int) (npc.hp() * 0.75);
        } else if (theatre.getParty().size() == 4) {
            scaledHitpoints = (int) (npc.hp() * 0.875);
        } else {
            scaledHitpoints = npc.hp();
        }
        return scaledHitpoints;
    }

}
