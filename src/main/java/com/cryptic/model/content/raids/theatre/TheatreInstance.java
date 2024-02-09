package com.cryptic.model.content.raids.theatre;

import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatre.boss.bloat.handler.BloatHandler;
import com.cryptic.model.content.raids.theatre.boss.maiden.blood.BloodSpawn;
import com.cryptic.model.content.raids.theatre.boss.maiden.handler.MaidenHandler;
import com.cryptic.model.content.raids.theatre.boss.nylocas.handler.VasiliasHandler;
import com.cryptic.model.content.raids.theatre.boss.sotetseg.handler.SotetsegHandler;
import com.cryptic.model.content.raids.theatre.boss.verzik.handler.VerzikHandler;
import com.cryptic.model.content.raids.theatre.boss.xarpus.handler.XarpusHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreHandler;
import com.cryptic.model.content.raids.theatre.controller.TheatreController;
import com.cryptic.model.content.raids.theatre.loot.ChestType;
import com.cryptic.model.content.raids.theatre.loot.TheatreLoot;
import com.cryptic.model.content.raids.theatre.stage.*;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.model.content.mechanics.DeathProcess.*;
import static com.cryptic.model.content.mechanics.DeathProcess.SOTETSEG_AREA;
import static com.cryptic.model.entity.attributes.AttributeKey.RARE_TOB_REWARD;
import static com.cryptic.model.entity.attributes.AttributeKey.TOB_LOOT_CHEST;

/**
 * @Author: Origin
 * @Date: 10/5/2023
 */
public class TheatreInstance extends InstancedArea {
    @Getter
    Player owner;
    @Getter
    List<Player> players, occupiedCageSpawnPointsList;
    @Getter
    List<NPC> verzikPillarNpcs, verzikNylocasList, tornadoList, nylocas, pillarList;
    @Getter
    List<GameObject> verzikPillarObjects, pillarObject;
    @Getter
    AtomicInteger wave = new AtomicInteger();
    public List<GameObject> bloodObjectList;
    public List<BloodSpawn> orbList;
    @Getter
    List<TheatreHandler> bosses;
    @Getter
    TheatreController theatreController;
    @Getter
    TheatrePhase theatrePhase;
    @Getter
    List<GameObject> treasureSpawns = new ArrayList<>();
    Tile entrance = new Tile(3206, 4446);
    @Getter TheatreLoot theatreLoot = new TheatreLoot();
    @Getter
    Tile[] verzikPillarTiles = new Tile[]{new Tile(3161, 4318, 0), new Tile(3161, 4312, 0), new Tile(3161, 4306, 0), new Tile(3173, 4318, 0), new Tile(3173, 4312, 0), new Tile(3173, 4306, 0)};
    Tile[] treasure_spawns = new Tile[]{new Tile(3226, 4323), new Tile(3226, 4327), new Tile(3233, 4330), new Tile(3241, 4323), new Tile(3241, 4327)};
    @Getter @Setter boolean hasInitiatedNylocasVasilias = false;
    @Getter Map<Player, Item[]> lootMap = new HashMap<>();
    public MutableObject<GameObject> throne;

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
        this.bloodObjectList = new ArrayList<>();
        this.orbList = new ArrayList<>();
        this.throne = new MutableObject<>();
    }

    public TheatreInstance buildParty() {
        owner.setInstancedArea(this);
        owner.setTheatreInstance(this);
        owner.teleport(entrance.transform(0, 0, this.getzLevel()));
        owner.setRoomState(RoomState.INCOMPLETE);
        for (var p : players) {
            if (p != owner) {
                if (p != null) {
                    p.setInstancedArea(owner.getTheatreInstance());
                    p.setTheatreInstance(owner.getTheatreInstance());
                    p.teleport(entrance.transform(0, 0, owner.getTheatreInstance().getzLevel()));
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
            member.teleport(new Tile(3670, 3219, 0));
            member.clearAttrib(TOB_LOOT_CHEST);
            member.clearAttrib(RARE_TOB_REWARD);
            member.setTheatreParty(null);
            member.setTheatreInstance(null);
            member.unlock();
        }
        for (var o : treasureSpawns) {
            if (o != null) {
                o.remove();
            }
        }
        for (var n : verzikNylocasList) {
            if (n != null) {
                n.remove();
            }
        }
        for (var o : bloodObjectList) {
            if (o == null) continue;
            o.remove();
        }
        for (var n : orbList) {
            if (n == null) continue;
            n.die();
        }
        this.bloodObjectList.clear();
        this.orbList.clear();
        this.lootMap.clear();
        this.verzikNylocasList.clear();
        this.pillarObject.clear();
        this.pillarList.clear();
        this.bosses.clear();
        this.players.clear();
        this.setHasInitiatedNylocasVasilias(false);
        if (this.throne.getValue() != null) {
            ObjectManager.removeObj(this.throne.getValue());
        }
    }

    public void spawnTreasure() {
        for (int index = 0; index < this.players.size(); index++) {
            Player player = this.players.get(index);
            if (player == null) continue;
            int rotation = 2;
            rotation = findRotation(index, rotation);
            Tile finalTile = determineTile(treasure_spawns[index]);
            GameObject treasure = new GameObject(33086 + index, finalTile, 10, rotation).spawn();
            this.treasureSpawns.add(treasure);
            generateReward(player);
            monumentalChest(index, player);
            player.putAttrib(TOB_LOOT_CHEST, treasure.getId());
        }
    }
    private void generateReward(Player player) {
        ItemContainer container = new ItemContainer(3, ItemContainer.StackPolicy.ALWAYS);
        for (int index = 0; index < 3; index++) {
            Item reward = theatreLoot.reward(player);
            container.add(reward);
            lootMap.put(player, container.getItems());
            player.getPacketSender().sendItemOnInterfaceSlot(12222 + index, reward, 0);
        }
    }

    private Tile determineTile(Tile treasure_spawns) {
        return treasure_spawns.transform(0, 0, this.getzLevel());
    }

    private int findRotation(int index, int rotation) {
        switch (index) {
            case 0, 1 -> rotation = 3;
            case 2 -> rotation = 4;
            case 3, 4 -> rotation = 1;
        }
        return rotation;
    }

    private void monumentalChest(int index, Player player) {
        for (Player owner : this.players) {
            ChestType type = ChestType.DEFAULT;
            if (owner == player) {
                type = player.hasAttrib(RARE_TOB_REWARD) ? ChestType.RARE_REWARD_ARROW : ChestType.DEFAULT_ARROW;
            } else if (player.hasAttrib(RARE_TOB_REWARD)) {
                type = ChestType.RARE_REWARD;
            }
            owner.varps().varbit(6450 + index, type.ordinal());
        }
    }

    @Nonnull
    public Player getRandomTarget() {
        Collections.shuffle(this.players);
        return Objects.requireNonNull(Utils.randomElement(this.players));
    }

    @Override
    public void dispose() {
        super.dispose();
        clear();
    }

}
