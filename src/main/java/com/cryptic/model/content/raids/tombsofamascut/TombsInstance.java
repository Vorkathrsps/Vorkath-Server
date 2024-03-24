package com.cryptic.model.content.raids.tombsofamascut;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatre.controller.RaidBuilder;
import com.cryptic.model.content.raids.theatre.controller.RaidController;
import com.cryptic.model.content.raids.theatre.party.RaidParty;
import com.cryptic.model.content.raids.tombsofamascut.warden.builder.KephriPhantomBuilder;
import com.cryptic.model.content.raids.tombsofamascut.warden.builder.WardenBuilder;
import com.cryptic.model.content.raids.tombsofamascut.warden.builder.ZebakPhantomBuilder;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;

import java.util.*;

public class TombsInstance extends InstancedArea {
    RaidParty party;
    @Getter
    List<RaidBuilder> bosses;
    @Getter
    RaidController tombsController;
    @Getter
    public NPC hiddenZebak;
    public boolean initiatedPhaseOne = false, initiatedPhaseTwo = false, isWardenDead = false;
    static final Tile entrance = new Tile(3936, 5165);
    public final GameObject teleporter = new GameObject(45138, 10, 0, new Tile(3936, 5154, this.getzLevel() + 1));
    public static Area[] rooms() {
        int[] regions = {15696};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public TombsInstance(RaidParty party) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, rooms());
        this.party = party;
        this.bosses = new ArrayList<>();
        this.tombsController = new RaidController(bosses);
    }

    public TombsInstance buildParty() {
        Player owner = this.party.getOwner();
        owner.setInstancedArea(this);
        owner.setTombsInstance(this);
        Tile tile = entrance.transform(0, 0, this.getzLevel() + 1);
        owner.teleport(tile);
        for (Player player : this.party.getPlayers()) {
            if (player == null) continue;
            if (player == owner) continue;
            player.setInstancedArea(this);
            player.setTombsInstance(this);
            player.teleport(tile);
        }
        return this;
    }

    public void start() {
        this.bosses.add(new WardenBuilder());
        this.bosses.add(new ZebakPhantomBuilder());
        this.bosses.add(new KephriPhantomBuilder());
        this.hiddenZebak = new NPC(11744, new Tile(3938, 5156, this.getzLevel() + 1));
        this.hiddenZebak.getMovementQueue().setBlockMovement(true);
        this.hiddenZebak.getCombat().setAutoRetaliate(false);
        this.hiddenZebak.setInstancedArea(this);
        this.hiddenZebak.spawn(false);
        this.tombsController.build(this.party.getOwner(), this);
    }

    @Override
    public void dispose() {
        super.dispose();
        teleporter.remove();
    }
}
