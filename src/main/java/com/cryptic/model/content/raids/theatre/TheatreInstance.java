package com.cryptic.model.content.raids.theatre;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.bloat.handler.BloatHandler;
import com.cryptic.model.content.raids.theatre.boss.maiden.handler.MaidenHandler;
import com.cryptic.model.content.raids.theatre.boss.nylocas.handler.VasiliasHandler;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.handler.SotetsegHandler;
import com.cryptic.model.content.raids.theatre.boss.verzik.handler.VerzikHandler;
import com.cryptic.model.content.raids.theatre.boss.verzik.nylocas.PurpleNylocas;
import com.cryptic.model.content.raids.theatre.boss.xarpus.handler.XarpusHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreController;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.cryptic.model.content.mechanics.DeathProcess.*;
import static com.cryptic.model.content.mechanics.DeathProcess.SOTETSEG_AREA;

/**
 * @Author: Origin
 * @Date: 10/5/2023
 */
public class TheatreInstance extends TheatreArea { //TODO make sure we're cleaning up all garbage collections & verzik

    @Getter public Player owner;
    @Getter public List<Player> players;
    public AtomicInteger wave = new AtomicInteger();
    @Getter public List<Player> occupiedCageSpawnPointsList = new ArrayList<>();
    @Getter List<TheatreHandler> bosses = new ArrayList<>();
    @Getter public List<NPC> pillarList = new ArrayList<>();
    @Getter public List<NPC> nylocas = new ArrayList<>();
    @Getter ArrayList<PurpleNylocas> verzikNylocasList = new ArrayList<>();
    @Getter public List<NPC> verzikPillarNpcs = new ArrayList<>();
    @Getter public List<GameObject> pillarObject = new ArrayList<>();
    @Getter public List<GameObject> verzikPillarObjects = new ArrayList<>();
    @Getter public TheatreController theatreController = new TheatreController(bosses);
    @Getter public TheatrePhase theatrePhase = new TheatrePhase(TheatreStage.ONE);
    @Getter public Tile entrance = new Tile(3168, 4304);
    @Getter public final List<Tile> verzikPillarTiles = List.of(new Tile(3161, 4318, 0),
        new Tile(3161, 4312, 0),
        new Tile(3161, 4306, 0),
        new Tile(3173, 4318, 0),
        new Tile(3173, 4312, 0),
        new Tile(3173, 4306, 0));
    @Getter @Setter public boolean hasInitiatedNylocasVasilias = false;
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
    }

    public TheatreInstance buildParty() {
        owner.setInstance(this);
        owner.teleport(entrance.transform(0, 0, this.getzLevel()));
        owner.setTheatreState(TheatreState.ACTIVE);
        owner.setRaidDeathState(RaidDeathState.ALIVE);
        owner.setRoomState(RoomState.INCOMPLETE);
        for (var p : players) {
            if (p != owner) {
                if (p != null) {
                    p.setInstance(owner.getTheatreInstance());
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
                    p.teleport(3168, 4315, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(BLOAT_AREA)) {
                    p.teleport(3282, 4447, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(MAIDEN_AREA)) {
                    p.teleport(3180, 4447, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(XARPUS_AREA)) {
                    p.teleport(3170, 4389, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(VASILIAS_AREA)) {
                    p.teleport(3295, 4248, p.getTheatreInstance().getzLevel());
                } else if (p.tile().inArea(SOTETSEG_AREA)) {
                    p.teleport(3279, 4316, p.getTheatreInstance().getzLevel());
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
    @Override
    public void dispose() {
        super.dispose();
        clear();
    }

}
