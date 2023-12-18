package com.cryptic.model.content.raids.theatre;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatre.boss.bloat.handler.BloatHandler;
import com.cryptic.model.content.raids.theatre.boss.maiden.handler.MaidenHandler;
import com.cryptic.model.content.raids.theatre.boss.nylocas.handler.VasiliasHandler;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.handler.SotetsegHandler;
import com.cryptic.model.content.raids.theatre.boss.verzik.handler.VerzikHandler;
import com.cryptic.model.content.raids.theatre.boss.xarpus.handler.XarpusHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreController;
import com.cryptic.model.content.raids.theatre.loot.ChestType;
import com.cryptic.model.content.raids.theatre.stage.*;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.model.content.mechanics.DeathProcess.*;
import static com.cryptic.model.content.mechanics.DeathProcess.SOTETSEG_AREA;
import static com.cryptic.model.entity.attributes.AttributeKey.RARE_TOB_REWARD;

/**
 * @Author: Origin
 * @Date: 10/5/2023
 */
public class TheatreInstance extends InstancedArea {
    @Getter Player owner;
    @Getter List<Player> players, occupiedCageSpawnPointsList;
    @Getter List<NPC> verzikPillarNpcs, verzikNylocasList, tornadoList, nylocas, pillarList;
    @Getter List<GameObject> verzikPillarObjects, pillarObject;
    @Getter AtomicInteger wave = new AtomicInteger();
    @Getter List<TheatreHandler> bosses;
    @Getter TheatreController theatreController;
    @Getter TheatrePhase theatrePhase;

    //new Tile(3206, 4446); normal start room
    Tile entrance = new Tile(3168, 4316);
    @Getter Tile[] verzikPillarTiles = new Tile[]
        {
            new Tile(3161, 4318, 0),
            new Tile(3161, 4312, 0),
            new Tile(3161, 4306, 0),
            new Tile(3173, 4318, 0),
            new Tile(3173, 4312, 0),
            new Tile(3173, 4306, 0)
        };
    @Getter @Setter boolean hasInitiatedNylocasVasilias = false;

    public static Area[] rooms() {
        int[] regions = {12613, 12869, 13125, 12612, 12611, 12687, 13123, 13122, 12867};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public TheatreInstance(Player owner, List<Player> players) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, rooms());
        this.owner = owner;
        this.players = players;
        this.bosses = new ArrayList<>();
        this.theatreController = new TheatreController(bosses);
        this.theatrePhase = new TheatrePhase(TheatreStage.ONE);
        this.verzikNylocasList = new ArrayList<>();
        this.verzikPillarNpcs = new ArrayList<>();
        this.verzikPillarObjects = new ArrayList<>();
        this.pillarObject = new ArrayList<>();
        this.pillarList = new ArrayList<>();
        this.nylocas = new ArrayList<>();
        this.occupiedCageSpawnPointsList = new ArrayList<>();
        this.tornadoList = new ArrayList<>();
    }

    public TheatreInstance buildParty() {
        owner.setInstancedArea(this);
        owner.setTheatreInstance(this);
        owner.teleport(entrance.transform(0, 0, this.getzLevel()));
        owner.setTheatreState(TheatreState.ACTIVE);
        owner.setRaidDeathState(RaidDeathState.ALIVE);
        owner.setRoomState(RoomState.INCOMPLETE);
        for (var p : players) {
            if (p != owner) {
                if (p != null) {
                    p.setInstancedArea(owner.getTheatreInstance());
                    p.setTheatreInstance(owner.getTheatreInstance());
                    p.teleport(entrance.transform(0, 0, owner.getTheatreInstance().getzLevel()));
                    p.setTheatreState(TheatreState.ACTIVE);
                    p.setRaidDeathState(RaidDeathState.ALIVE);
                    p.setRoomState(RoomState.INCOMPLETE);
                }
            }
        }
        return this;
    }

    public void startRaid() {
        bosses.add(new MaidenHandler());
        bosses.add(new XarpusHandler());
        bosses.add(new BloatHandler());
        bosses.add(new VasiliasHandler());
        bosses.add(new SotetsegHandler());
        bosses.add(new VerzikHandler());
        theatreController.build(this.owner, this);
    }

    public void onRoomStateChanged(RoomState roomState) {
        if (roomState == RoomState.COMPLETE) {
            players.forEach(p -> {
                if (p.tile().inArea(VERZIK_AREA)) {
                    p.teleport(3168, 4315, this.getzLevel());
                } else if (p.tile().inArea(BLOAT_AREA)) {
                    p.teleport(3282, 4447, this.getzLevel());
                } else if (p.tile().inArea(MAIDEN_AREA)) {
                    p.teleport(3180, 4447, this.getzLevel());
                } else if (p.tile().inArea(XARPUS_AREA)) {
                    p.teleport(3170, 4389, this.getzLevel() + 1);
                } else if (p.tile().inArea(VASILIAS_AREA)) {
                    p.teleport(3295, 4248, this.getzLevel());
                } else if (p.tile().inArea(SOTETSEG_AREA)) {
                    p.teleport(3279, 4316, this.getzLevel());
                }
            });
        }
    }

    public void clear() {
        for (var member : players) {
            if (member.getEquipment().contains(ItemIdentifiers.DAWNBRINGER)) {
                member.getEquipment().remove(member.getEquipment().getWeapon());
            } else if (member.getInventory().contains(ItemIdentifiers.DAWNBRINGER)) {
                member.getInventory().remove(ItemIdentifiers.DAWNBRINGER);
            }
            Arrays.stream(rooms())
                .findFirst()
                .filter(p -> p.contains(member.tile()))
                .ifPresent(p -> member.teleport(new Tile(3670, 3219, 0)));
            member.setTheatreParty(null);
        }
        for (var n : verzikNylocasList) {
            if (n != null) {
                n.remove();
            }
        }
        this.getVerzikNylocasList().clear();
        this.getPillarObject().clear();
        this.getPillarList().clear();
        this.getBosses().clear();
        this.getPlayers().clear();
        this.setHasInitiatedNylocasVasilias(false);
    }

    public void spawnTreasure() {
        Tile[] treasure_spawns = new Tile[]
            {
                new Tile(3226, 4323),
                new Tile(3226, 4327),
                new Tile(3233, 4330),
                new Tile(3241, 4323),
                new Tile(3241, 4327)
            };
        int treasureId = 33086;
        for (int index = 0; index < this.getPlayers().size(); index++) {
            var player = this.getPlayers().get(index);
            int rotation = 2;
            switch (index) {
                case 0,1 -> rotation = 3;
                case 2 -> rotation = 4;
                case 3,4 -> rotation = 1;
            }
            Tile t = treasure_spawns[index];
            Tile finalTile = t.transform(0, 0, this.getzLevel());
            GameObject treasure = new GameObject(treasureId + index, finalTile, 10, rotation);
            treasure.spawn();
            for (Player chestOwner : this.getPlayers()) {
                ChestType type = ChestType.DEFAULT;
                if (player == chestOwner) {
                    type = player.hasAttrib(RARE_TOB_REWARD) ? ChestType.RARE_REWARD_ARROW : ChestType.DEFAULT_ARROW;
                } else if (player.hasAttrib(RARE_TOB_REWARD)) {
                    type = ChestType.RARE_REWARD;
                }
                chestOwner.varps().varbit(6450 + index, type.ordinal());
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        clear();
    }

}
