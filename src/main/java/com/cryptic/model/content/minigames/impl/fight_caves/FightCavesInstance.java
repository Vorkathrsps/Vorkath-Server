package com.cryptic.model.content.minigames.impl.fight_caves;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;

public class FightCavesInstance extends InstancedArea {
    @Getter Player owner;
    Tile entrance = new Tile(2412, 5111);
    public static Area room() {
        return new Area(Tile.regionToTile(9551).getX(), Tile.regionToTile(9551).getY(), Tile.regionToTile(9551).getX() + 63, Tile.regionToTile(9551).getY() + 63);
    }
    public FightCavesInstance(Player owner) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, room());
        this.owner = owner;
    }

    public void build() {
        NPC npc = new NPC(3127, new Tile(2403, 5090, this.getzLevel()));
        npc.ignoreOccupiedTiles = true;
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
        npc.getCombatInfo().aggressive = true;
        npc.setInstancedArea(this);
        npc.respawns(false).spawn(false);
        owner.setInstancedArea(this);
        owner.teleport(entrance.transform(0,0,this.getzLevel()));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
