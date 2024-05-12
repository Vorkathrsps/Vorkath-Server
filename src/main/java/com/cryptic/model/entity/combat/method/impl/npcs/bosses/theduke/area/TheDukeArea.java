package com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke.area;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;

import java.util.Collections;
import java.util.List;

public class TheDukeArea extends Controller {
    public static Area ROOM = new Area(Tile.regionToTile(12132).getX(), Tile.regionToTile(12132).getY(), Tile.regionToTile(12132).getX() + 63, Tile.regionToTile(12132).getY() + 63);
    public TheDukeArea() {
        super(Collections.singletonList(new Area(Tile.regionToTile(12132).getX(), Tile.regionToTile(12132).getY(), Tile.regionToTile(12132).getX() + 63, Tile.regionToTile(12132).getY() + 63)));
    }

    @Override
    public void enter(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.id() == 12166 || npc.id() == 12191) {
                    if (npc.getZ() != player.getZ()) continue;
                    if (!npc.dead()) {
                        HealthHud.open(player, HealthHud.Type.REGULAR, "Duke Sucellus", npc.hp());
                        if (npc.hp() != npc.maxHp()) HealthHud.update(player, npc.hp(), npc.maxHp());
                    }
                }
            }
        }
    }

    @Override
    public void leave(Player player) {
        HealthHud.close(player);
    }

    @Override
    public void process(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.getZ() != player.getZ()) {
                    continue;
                }
                if (npc.id() == 12166 || npc.id() == 12191) {
                    if (npc.dead()) {
                        HealthHud.close(player);
                    } else {
                        if (npc.hp() != npc.maxHp()) HealthHud.update(player, npc.hp(), npc.maxHp());
                        else if (!HealthHud.updated && HealthHud.needsUpdate) {
                            HealthHud.open(player, HealthHud.Type.REGULAR, "Duke Sucellus", npc.hp());
                        }
                    }
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
        HealthHud.close(player);
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return false;
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
        return true;
    }

    @Override
    public boolean inside(Entity entity) {
        return entity.getAsPlayer().getDukeInstance() != null && ROOM.transformArea(0, 0, 0, 0, entity.getAsPlayer().getDukeInstance().getzLevel()).contains(entity.tile());
    }
}
