package com.cryptic.model.content.raids.theatre.boss.nylocas.handler;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.boss.nylocas.pillars.PillarNpc;
import com.cryptic.model.content.raids.theatre.boss.nylocas.pillars.PillarObject;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasHandler implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        PillarNpc pillarNpc1 = new PillarNpc(8358, new Tile(3290, 4252, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3289, 4253, theatreInstance.getzLevel()), 10, 1, theatreInstance), theatreInstance);
        PillarNpc pillarNpc2 = new PillarNpc(8358, new Tile(3299, 4252, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3300, 4253, theatreInstance.getzLevel()), 10, 2, theatreInstance), theatreInstance);
        PillarNpc pillarNpc3 = new PillarNpc(8358, new Tile(3299, 4243, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3300, 4242, theatreInstance.getzLevel()), 10, 3, theatreInstance), theatreInstance);
        PillarNpc pillarNpc4 = new PillarNpc(8358, new Tile(3290, 4243, theatreInstance.getzLevel()), new PillarObject(32862, new Tile(3289, 4242, theatreInstance.getzLevel()), 10, 0, theatreInstance), theatreInstance);
        pillarNpc1.setInstance(theatreInstance);
        pillarNpc2.setInstance(theatreInstance);
        pillarNpc3.setInstance(theatreInstance);
        pillarNpc4.setInstance(theatreInstance);
        pillarNpc1.spawnPillarObject();
        pillarNpc2.spawnPillarObject();
        pillarNpc3.spawnPillarObject();
        pillarNpc4.spawnPillarObject(); //i just discovered fire fax
        pillarNpc1.spawn(false);
        pillarNpc2.spawn(false);
        pillarNpc3.spawn(false);//how the fuck i do that i hold ALT and mouse drag
        pillarNpc4.spawn(false);
    }

    @Override
    public int scale(NPC npc, Player player, boolean hardMode) {
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
