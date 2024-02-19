package com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class KrakenInstance extends KrakenArea {
    @Getter
    Player owner;
    @Getter
    private final Tile entrance = new Tile(2280, 10022);
    @Getter
    List<NPC> nonAwakenedTentacles = new ArrayList<>();
    @Getter
    List<NPC> awakenedTentacles = new ArrayList<>();
    @Getter
    @Setter
    KrakenState krakenState;

    public static Area room() {
        return new Area(Tile.regionToTile(9116).getX(), Tile.regionToTile(9116).getY(), Tile.regionToTile(9116).getX() + 63, Tile.regionToTile(9116).getY() + 63);
    }

    public KrakenInstance(Player owner, KrakenState krakenState) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, room());
        this.owner = owner;
        this.krakenState = krakenState;
    }

    public void build() {
        NPC[] npcs = new NPC[]{
            new NPC(496, new Tile(2278, 10034, this.getzLevel())),
            new NPC(5534, new Tile(2275, 10034, this.getzLevel())),
            new NPC(5534, new Tile(2284, 10034, this.getzLevel())),
            new NPC(5534, new Tile(2284, 10038, this.getzLevel())),
            new NPC(5534, new Tile(2275, 10038, this.getzLevel()))
        };
        for (var n : npcs) {
            if (n.id() == 5534) {
                nonAwakenedTentacles.add(n);
                n.setCombatMethod(new TentacleCombat());
                n.setInstancedArea(this);
                n.spawn(false);
                n.noRetaliation(true);
            } else {
                n.setCombatMethod(new KrakenBossCombat());
                n.setInstancedArea(this);
                n.spawn(true);
                n.noRetaliation(true);
            }
        }
        this.create();
    }

    public void create() {
        owner.setInstancedArea(this);
        owner.teleport(entrance.transform(0, 0, this.getzLevel()));
    }

    public void clear() {
        for (var n : this.getAwakenedTentacles()) {
            if (n == null) continue;
            n.remove();
        }
        for (var n : this.getNonAwakenedTentacles()) {
            if (n == null) continue;
            n.remove();
        }
        this.getNonAwakenedTentacles().clear();
        this.getAwakenedTentacles().clear();
    }

    @Override
    public void dispose() {
        super.dispose();
        clear();
    }

}
