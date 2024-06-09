package com.cryptic.model.map.position.areas.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;

import java.util.Collections;
import java.util.List;

public class CallistoArea extends Controller {
    public static final Area ROOM = new Area(1746, 11525, 1771, 11556);

    public CallistoArea() {
        super(Collections.singletonList(new Area(Tile.regionToTile(7092).getX(), Tile.regionToTile(7092).getY(), Tile.regionToTile(7092).getX() + 63, Tile.regionToTile(7092).getY() + 63)));
    }

    @Override
    public void enter(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.id() == 6609) {
                    if (!npc.dead()) {
                        npc.getHealthHud().set(player);
                    }
                }
            }
        }
    }

    @Override
    public void leave(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.id() == 6609) {
                    npc.getHealthHud().clear(player);
                }
            }
        }
    }

    @Override
    public void process(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.id() == 6609) {
                    npc.getHealthHud().sync(player);
                }
            }
        }
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
        return true;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int option) {
        return true;
    }

    @Override
    public boolean useInsideCheck() {
        return false;
    }

    @Override
    public boolean inside(Entity entity) {
        return ROOM.contains(entity.tile());
    }
}
