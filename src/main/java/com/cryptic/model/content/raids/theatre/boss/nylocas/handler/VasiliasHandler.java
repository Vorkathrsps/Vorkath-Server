package com.cryptic.model.content.raids.theatre.boss.nylocas.handler;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.boss.nylocas.pillars.PillarNpc;
import com.cryptic.model.content.raids.theatre.boss.nylocas.pillars.PillarObject;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class VasiliasHandler implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        var z = theatreInstance.getzLevel();
        PillarNpc pillarNpc1 = new PillarNpc(8358, new Tile(3290, 4252, z), new GameObject(32862, new Tile(3289, 4253, z), 10, 1), theatreInstance);
        PillarNpc pillarNpc2 = new PillarNpc(8358, new Tile(3299, 4252, z), new GameObject(32862, new Tile(3300, 4253, z), 10, 2), theatreInstance);
        PillarNpc pillarNpc3 = new PillarNpc(8358, new Tile(3299, 4243, z), new GameObject(32862, new Tile(3300, 4242, z), 10, 3), theatreInstance);
        PillarNpc pillarNpc4 = new PillarNpc(8358, new Tile(3290, 4243, z), new GameObject(32862, new Tile(3289, 4242, z), 10, 0), theatreInstance);
        pillarNpc1.setInstancedArea(theatreInstance);
        pillarNpc2.setInstancedArea(theatreInstance);
        pillarNpc3.setInstancedArea(theatreInstance);
        pillarNpc4.setInstancedArea(theatreInstance);
        pillarNpc1.spawnPillarObject();
        pillarNpc2.spawnPillarObject();
        pillarNpc3.spawnPillarObject();
        pillarNpc4.spawnPillarObject();
        pillarNpc1.spawn(false);
        pillarNpc2.spawn(false);
        pillarNpc3.spawn(false);
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
