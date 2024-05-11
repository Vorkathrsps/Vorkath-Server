package com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke.instance;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class TheDukeInstance extends InstancedArea {

    final Player owner;
    final NPC[] fumes = new NPC[]
        {
            new NPC(12198, new Tile(3035, 6449, this.getzLevel())),
            new NPC(12198, new Tile(3038, 6449, this.getzLevel())),
            new NPC(12198, new Tile(3041, 6449, this.getzLevel()))
        };
    final Tile[] tiles =
        {
            new Tile(3036, 6450),
            new Tile(3039, 6450),
            new Tile(3042, 6450)
        };
    final Tile entrance = new Tile(3039, 6435, this.getzLevel());
    final NPC boss = new NPC(12166, new Tile(3036, 6452, this.getzLevel()));
    final int[] slamGraphics = new int[]{2440, 2441, 2442, 2443};
    final int[] gasGraphics = new int[]{2431, 2432, 2433};
    int currentTileIndex = 0;
    int attackCount = 2;
    int gasCount = 0;
    boolean isTwoSent = false;
    boolean isIteratingForward = true;

    public static Area[] rooms() {
        int[] regions = {12132};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public TheDukeInstance(Player owner) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, rooms());
        this.owner = owner;
    }

    public TheDukeInstance build() {
        this.owner.setDukeInstance(this);
        this.owner.setInstancedArea(this);
        this.owner.teleport(entrance);
        this.boss.setDukeInstance(this);
        this.boss.setInstancedArea(this);
        this.boss.spawn(false);
        for (final NPC npc : this.fumes) {
            npc.setDukeInstance(this);
            npc.setInstancedArea(this);
            npc.spawn(false);
        }
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.boss.clearAttrib(AttributeKey.BARON_ENRAGED);
        this.boss.remove();
        for (NPC npc : this.fumes) {
            if (npc == null) continue;
            npc.remove();
        }
    }

}
