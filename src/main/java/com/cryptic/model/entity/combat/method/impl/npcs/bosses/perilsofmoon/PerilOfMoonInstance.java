package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon;

import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.state.BlueMoonState;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class PerilOfMoonInstance extends InstancedArea {
    @Getter
    final Tile[][] tilesLeft = new Tile[][]
        {
            {new Tile(1433, 9689, this.getzLevel()), new Tile(1433, 9670, this.getzLevel())},
            {new Tile(1432, 9688, this.getzLevel()), new Tile(1432, 9672, this.getzLevel())},
            {new Tile(1431, 9688, this.getzLevel()), new Tile(1431, 9672, this.getzLevel())},
            {new Tile(1430, 9687, this.getzLevel()), new Tile(1430, 9673, this.getzLevel())},
            {new Tile(1429, 9686, this.getzLevel()), new Tile(1429, 9674, this.getzLevel())},
            {new Tile(1428, 9685, this.getzLevel()), new Tile(1428, 9675, this.getzLevel())},
            {new Tile(1427, 9684, this.getzLevel()), new Tile(1427, 9676, this.getzLevel())}
        };
    @Getter
    final Tile[][] tilesRight = new Tile[][]
        {
            {new Tile(1447, 9691, this.getzLevel()), new Tile(1447, 9670, this.getzLevel())},
            {new Tile(1448, 9688, this.getzLevel()), new Tile(1448, 9672, this.getzLevel())},
            {new Tile(1449, 9688, this.getzLevel()), new Tile(1449, 9672, this.getzLevel())},
            {new Tile(1450, 9687, this.getzLevel()), new Tile(1450, 9674, this.getzLevel())},
            {new Tile(1451, 9686, this.getzLevel()), new Tile(1451, 9675, this.getzLevel())},
            {new Tile(1452, 9685, this.getzLevel()), new Tile(1452, 9676, this.getzLevel())},
            {new Tile(1453, 9684, this.getzLevel()), new Tile(1453, 9677, this.getzLevel())}
        };
    @Getter NPC[] circleNpcs = new NPC[]
        {
            new NPC(13015, new Tile(1438, 9676, this.getzLevel())),
            new NPC(13015, new Tile(1436, 9678, this.getzLevel())),
            new NPC(13015, new Tile(1436, 9681, this.getzLevel())),
            new NPC(13015, new Tile(1438, 9683, this.getzLevel())),
            new NPC(13015, new Tile(1441, 9683, this.getzLevel())),
            new NPC(13015, new Tile(1443, 9681, this.getzLevel())),
            new NPC(13015, new Tile(1443, 9678, this.getzLevel()))
        };

    @Getter
    Player owner;
    @Getter
    List<Player> players;
    @Getter
    List<GameObject> moonfire;
    @Getter
    List<GameObject> braziers;
    @Getter
    List<NPC> tornadoList;
    @Getter
    List<GameObject> objects;
    @Getter
    List<NPC> circles;
    @Getter
    @Setter
    BlueMoonState state = BlueMoonState.DOCILE;
    Tile entrance = new Tile(1440, 9656);

    public static Area[] rooms() {
        int[] regions = {5783, 5782};
        return Arrays.stream(regions).mapToObj(region -> new Area(
            Tile.regionToTile(region).getX(),
            Tile.regionToTile(region).getY(),
            Tile.regionToTile(region).getX() + 63,
            Tile.regionToTile(region).getY() + 63)).toArray(Area[]::new);
    }

    public PerilOfMoonInstance(Player owner, List<Player> players) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, rooms());
        this.owner = owner;
        this.players = players;
        this.moonfire = new ArrayList<>();
        this.braziers = new ArrayList<>();
        this.tornadoList = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.circles = new ArrayList<>();
    }

    public final void buildParty() {
        owner.setInstancedArea(this);
        owner.setPerilInstance(this);
        owner.teleport(entrance.transform(0, 0, this.getzLevel()));
        for (var p : players) {
            if (owner.equals(p)) continue;
            p.setInstancedArea(this);
            p.setPerilInstance(owner.getPerilInstance());
            p.teleport(entrance.transform(0, 0, owner.getPerilInstance().getzLevel()));
        }
        buildBraziers();
        buildMoonfire();
    }

    final void buildBraziers() {
        GameObject brazier_one = new GameObject(52993, new Tile(1453, 9679, this.getzLevel()));
        GameObject brazier_two = new GameObject(52992, new Tile(1425, 9679, this.getzLevel()));
        brazier_one.setRotation(1);
        brazier_two.setRotation(3);
        brazier_one.putAttrib(AttributeKey.UNLIT_BRAZIER_VARBIT, 1);
        brazier_two.putAttrib(AttributeKey.UNLIT_BRAZIER_VARBIT, 1);
        this.getBraziers().add(brazier_one);
        this.getBraziers().add(brazier_two);
        for (var object : this.getBraziers()) object.spawn();
        setVarps();
    }

    final void setVarps() {
        this.owner.varps().varbit(9855, 1);
        this.owner.varps().varbit(9856, 1);
        for (var entity : this.getPlayers()) {
            if (entity instanceof Player player) {
                player.varps().varbit(9855, 1);
                player.varps().varbit(9856, 1);
            }
        }
    }

    final void buildMoonfire() {
        GameObject object = new GameObject(51053, new Tile(1433, 9672, this.getzLevel()));
        this.getMoonfire().add(object);
        for (int index = 0; index < 15; index++) {
            object = new GameObject(51053, new Tile(1433 + index, 9672, this.getzLevel()));
            this.getMoonfire().add(object);
            object = new GameObject(51053, new Tile(1433 + index, 9688, this.getzLevel()));
            this.getMoonfire().add(object);
        }
        for (var o : this.getMoonfire()) {
            o.setRotation(1);
            o.spawn();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (var o : Lists.newArrayList(objects.iterator())) {
            if (o == null) continue;
            o.remove();
        }
        for (var o : Lists.newArrayList(braziers.iterator())) {
            if (o == null) continue;
            o.remove();
        }
        for (var o : Lists.newArrayList(moonfire.iterator())) {
            if (o == null) continue;
            o.remove();
        }
        for (var n : Lists.newArrayList(tornadoList.iterator())) {
            if (n == null) continue;
            n.remove();
        }
        objects.clear();
        braziers.clear();
        moonfire.clear();
        tornadoList.clear();
    }
}
