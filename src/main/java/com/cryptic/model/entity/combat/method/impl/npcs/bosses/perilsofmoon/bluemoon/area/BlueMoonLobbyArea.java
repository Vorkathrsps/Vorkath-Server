package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.area;

import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.PerilOfMoonInstance;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.BlueMoonNPC;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.TornadoNPC;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class BlueMoonLobbyArea extends Controller {
    public static final Area ROOM = new Area(1433, 9650, 1450, 9663);

    public BlueMoonLobbyArea() {
        super(Collections.emptyList());
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player) {

    }

    @Override
    public void process(Player player) {

    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Player attacker, Entity target) {
        return true;
    }

    @Override
    public void defeated(Player player, Entity entity) {

    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player other, int option) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int option) {
        var instance = player.getPerilInstance();
        if (instance == null) return false;
        if (object.getId() == 51373) {
            if (option == 1) {
                buildTornados(instance);
                player.getPacketSender().sendScreenFade("", 1, 2);
                Chain.noCtx().runFn(4, () -> {
                    spawnMoon(instance);
                    spawnCircles(instance);
                    player.teleport(new Tile(1440, 9675, instance.getzLevel()));
                });
            }
        }
        return true;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int option) {
        return true;
    }

    @Override
    public boolean useInsideCheck() {
        return true;
    }

    @Override
    public boolean inside(Entity entity) {
        Player player = (Player) entity;
        InstancedArea instancedArea = player.getPerilInstance();
        return instancedArea != null && ROOM.transformArea(0, 0, 0, 0, player.getPerilInstance().getzLevel()).contains(player.tile());
    }

    final void spawnCircles(PerilOfMoonInstance instance) {
        if (instance == null) return;
        for (var npc : instance.getCircleNpcs()) {
            npc.setInstancedArea(instance);
            npc.spawn(false);
            npc.hidden(true);
        }
    }

    final void spawnMoon(PerilOfMoonInstance instance) {
        if (instance == null) return;
        BlueMoonNPC moon = new BlueMoonNPC(13017, new Tile(1438, 9678, instance.getzLevel()), instance);
        moon.setInstancedArea(instance);
        moon.spawn(false);
        moon.lock();
    }

    final void buildTornados(PerilOfMoonInstance instance) {
        if (instance == null) return;
        Random random = World.getWorld().random();
        BooleanSupplier cancel = () -> instance.getBraziers().isEmpty() || instance.isDisposed();
        Chain.noCtxRepeat().cancelWhen(cancel).repeatingTask(4, _ -> {
            Map<TornadoNPC, Tile> temp = new HashMap<>();
            for (int index = 0; index < 2; index++) {
                var randomIndex1 = random.nextInt(instance.getTilesRight().length);
                var randomIndex2 = random.nextInt(instance.getTilesRight().length);
                TornadoNPC tornado_one = new TornadoNPC(13027, instance.getTilesRight()[randomIndex1][0], instance);
                TornadoNPC tornado_two = new TornadoNPC(13027, instance.getTilesRight()[randomIndex2][1], instance);
                temp.put(tornado_one, instance.getTilesRight()[randomIndex1][0]);
                temp.put(tornado_two, instance.getTilesRight()[randomIndex2][1]);
                instance.getTornadoList().add(tornado_one);
                instance.getTornadoList().add(tornado_two);
            }
            for (var entry : temp.entrySet()) {
                TornadoNPC tornado = entry.getKey();
                Tile currentTile = entry.getValue();
                int row = -1;
                for (int index = 0; index < instance.getTilesRight().length; index++) {
                    if (instance.getTilesRight()[index][0].equals(currentTile) || instance.getTilesRight()[index][1].equals(currentTile)) {
                        row = index;
                        break;
                    }
                }
                if (row != -1) {
                    Tile targetTile;
                    if (currentTile.equals(instance.getTilesRight()[row][1])) {
                        targetTile = instance.getTilesRight()[row][0];
                    } else {
                        targetTile = instance.getTilesRight()[row][1];
                    }
                    tornado.setInstancedArea(instance);
                    tornado.spawn(false);
                    tornado.animate(10970);
                    tornado.stepAbs(targetTile, MovementQueue.StepType.FORCED_WALK);
                    BooleanSupplier reached = () -> tornado.tile().equals(targetTile);
                    tornado.waitUntil(reached, () -> {
                        tornado.animate(10972);
                        Chain.noCtx().runFn(2, () -> {
                            for (var npc : Lists.newArrayList(instance.getTornadoList().iterator())) {
                                if (npc == null || npc != tornado) continue;
                                tornado.remove();
                                instance.getTornadoList().remove(tornado);
                            }
                        });
                    });
                }
            }
        });
        Chain.noCtxRepeat().cancelWhen(cancel).repeatingTask(4, _ -> {
            Map<TornadoNPC, Tile> temp = new HashMap<>();
            for (int index = 0; index < 2; index++) {
                var randomIndex1 = random.nextInt(instance.getTilesLeft().length);
                var randomIndex2 = random.nextInt(instance.getTilesLeft().length);
                TornadoNPC tornado_one = new TornadoNPC(13027, instance.getTilesLeft()[randomIndex1][0], instance);
                TornadoNPC tornado_two = new TornadoNPC(13027, instance.getTilesLeft()[randomIndex2][1], instance);
                temp.put(tornado_one, instance.getTilesLeft()[randomIndex1][0]);
                temp.put(tornado_two, instance.getTilesLeft()[randomIndex2][1]);
                instance.getTornadoList().add(tornado_one);
                instance.getTornadoList().add(tornado_two);
            }
            for (var entry : temp.entrySet()) {
                NPC tornado = entry.getKey();
                Tile currentTile = entry.getValue();
                int row = -1;
                for (int index = 0; index < instance.getTilesLeft().length; index++) {
                    if (instance.getTilesLeft()[index][0].equals(currentTile) || instance.getTilesLeft()[index][1].equals(currentTile)) {
                        row = index;
                        break;
                    }
                }
                if (row != -1) {
                    Tile targetTile;
                    if (currentTile.equals(instance.getTilesLeft()[row][1])) {
                        targetTile = instance.getTilesLeft()[row][0];
                    } else {
                        targetTile = instance.getTilesLeft()[row][1];
                    }
                    tornado.setInstancedArea(instance);
                    tornado.spawn(false);
                    tornado.animate(10970);
                    tornado.stepAbs(targetTile, MovementQueue.StepType.FORCED_WALK);
                    BooleanSupplier reached = () -> tornado.tile().equals(targetTile);
                    tornado.waitUntil(reached, () -> {
                        tornado.animate(10972);
                        Chain.noCtx().runFn(2, () -> {
                            for (var npc : Lists.newArrayList(instance.getTornadoList().iterator())) {
                                if (npc == null || npc != tornado) continue;
                                tornado.remove();
                                instance.getTornadoList().remove(tornado);
                            }
                        });
                    });
                }
            }
        });
    }
}
