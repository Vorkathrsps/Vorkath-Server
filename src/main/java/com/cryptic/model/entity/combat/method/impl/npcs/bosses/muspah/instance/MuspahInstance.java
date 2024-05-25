package com.cryptic.model.entity.combat.method.impl.npcs.bosses.muspah.instance;

import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class MuspahInstance extends InstancedArea {

    public final Player owner;
    public Set<GameObject> spikes;
    public Tile entrance;
    public int[] transforms = new int[]{12077, 12078};
    public NPC boss;
    public Area room = new Area(2834, 4247, 2858, 4270, this.getzLevel());
    public int spikeProgressionCount = 4;

    public static Area[] rooms() {
        int[] regions = {11330};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public MuspahInstance(Player owner) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, rooms());
        this.owner = owner;
        this.spikes = new HashSet<>();
        this.entrance = new Tile(2858, 4258, this.getzLevel());
        this.boss = new NPC(Utils.randomElement(transforms), new Tile(2844, 4256, this.getzLevel()));
        this.boss.spawnDirection(Direction.EAST.toInteger());
    }

    public MuspahInstance build() {
        this.addPlayer(this.owner);
        this.addNpc(this.boss);
        this.owner.teleport(entrance);
        this.boss.spawn(false);
        this.boss.animate(9938);
        return this;
    }

    void sendObject(Tile tile, GameObject object) {
        World.getWorld().sendUnclippedTileGraphic(2335, tile, 0, 0);
        World.getWorld().sendUnclippedTileGraphic(2325, tile, 0, 30);
        this.spikes.add(object);
        Chain.noCtx().delay(1, object::spawn).then(1, () -> {
            for (var o : Lists.newArrayList(this.spikes.iterator())) {
                if (o == null) continue;
                if (o == object) o.setId(46695);
            }
        });
    }

    public void setSpikes(Tile tile) {
        this.sendObject(tile, getObject(tile));
    }

    @NotNull
    private static GameObject getObject(Tile tile) {
        final int randomRotation = Utils.random(2, 3);
        return new GameObject(46693, tile, 10, randomRotation);
    }

    @Override
    public void dispose() {
        for (var o : Lists.newArrayList(spikes.iterator())) {
            o.remove();
        }
        this.spikes.clear();
        super.dispose();
    }

}
