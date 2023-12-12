package com.cryptic.model.content.raids.theatre.boss.verzik.handler;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.verzik.Verzik;
import com.cryptic.model.content.raids.theatre.boss.verzik.pillars.Pillar;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;

public class VerzikHandler implements TheatreHandler {
    @Override
    public void build(Player player, TheatreInstance theatreInstance) {
        Verzik verzik = new Verzik(NpcIdentifiers.VERZIK_VITUR_8369, new Tile(3166, 4323, theatreInstance.getzLevel()), theatreInstance);
        verzik.setHitpoints(this.scale(verzik, player, false));
        verzik.setInstancedArea(theatreInstance);
        verzik.spawn(false);
        GroundItem groundItem = new GroundItem(new Item(ItemIdentifiers.DAWNBRINGER), theatreInstance.getEntrance().transform(0,0,theatreInstance.getzLevel()), theatreInstance.getOwner()).setState(GroundItem.State.SEEN_BY_EVERYONE);
        groundItem.setInstance(theatreInstance);
        groundItem.spawn();
        for (Tile pillarTile : theatreInstance.getVerzikPillarTiles()) {
            GameObject object = new GameObject(32687, pillarTile.withHeight(theatreInstance.getzLevel()), 10, 0).spawn();
            Pillar npc = new Pillar(8379, pillarTile.withHeight(theatreInstance.getzLevel()), theatreInstance);
            npc.setInstancedArea(theatreInstance);
            npc.spawn(false);
            theatreInstance.getVerzikPillarNpcs().add(npc);
            theatreInstance.getVerzikPillarObjects().add(object);
        }
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
