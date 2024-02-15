package com.cryptic.model.map.position.areas.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.areas.Controller;

import java.util.Collections;

public class CorporealArea extends Controller {

    public static final Area ROOM = new Area(2973, 4369, 3003, 4399);
    public CorporealArea() {
        super(Collections.singletonList(new Area(2973, 4369, 3003, 4399)));
    }

    @Override
    public void enter(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.id() == 319) {
                    if (!npc.dead()) {
                        HealthHud.open(player, HealthHud.Type.REGULAR, "Corporeal Beast", npc.hp());
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
                if (npc.id() == 319) {
                    if (npc.dead()) {
                        HealthHud.close(player);
                    } else {
                        if (npc.hp() != npc.maxHp()) HealthHud.update(player, HealthHud.Type.REGULAR, npc.hp(), npc.maxHp());
                        else if (!HealthHud.updated && HealthHud.needsUpdate) {
                            HealthHud.open(player, HealthHud.Type.REGULAR, "Corporeal Beast", npc.hp());
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
