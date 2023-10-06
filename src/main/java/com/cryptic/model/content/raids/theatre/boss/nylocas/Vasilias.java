package com.cryptic.model.content.raids.theatre.boss.nylocas;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.boss.nylocas.pillars.PillarNpc;
import com.cryptic.model.content.raids.theatre.boss.nylocas.pillars.PillarObject;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class Vasilias implements TheatreHandler {

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
    public void build(Player player, TheatreInstance theatreInstance) {
        PillarNpc pillarNpc1 = (PillarNpc) new PillarNpc(8358, new Tile(3290, 4252, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3289, 4253, theatreInstance.getzLevel()), 10, 1, this), this).spawn(false);
        PillarNpc pillarNpc2 = (PillarNpc) new PillarNpc(8358, new Tile(3299, 4252, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3300, 4253, theatreInstance.getzLevel()), 10, 2, this), this).spawn(false);
        PillarNpc pillarNpc3 = (PillarNpc) new PillarNpc(8358, new Tile(3299, 4243, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3300, 4242, theatreInstance.getzLevel()), 10, 3, this), this).spawn(false);
        PillarNpc pillarNpc4 = (PillarNpc) new PillarNpc(8358, new Tile(3290, 4243, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3289, 4242, theatreInstance.getzLevel()), 10, 0, this), this).spawn(false);
        pillarNpc1.spawnPillarObject();
        pillarNpc2.spawnPillarObject();
        pillarNpc3.spawnPillarObject();
        pillarNpc4.spawnPillarObject();
        pillarNpc1.setInstance(theatreInstance);
        pillarNpc2.setInstance(theatreInstance);
        pillarNpc3.setInstance(theatreInstance);
        pillarNpc4.setInstance(theatreInstance);
    }

    @Override
    public int scale(NPC npc, Player player) {
        int scaledHitpoints;

        if (player.getTheatreInstance().getPlayers().size() <= 3) {
            scaledHitpoints = (int) (npc.hp() * 0.75);
        } else if (player.getTheatreInstance().getPlayers().size() == 4) {
            scaledHitpoints = (int) (npc.hp() * 0.875);
        } else {
            scaledHitpoints = npc.hp();
        }
        return scaledHitpoints;
    }

}
