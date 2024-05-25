package com.cryptic.model.map.position.areas.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.areas.Controller;

import java.util.Collections;

public class KrakenArea extends Controller {
    public KrakenArea() {
        super(Collections.emptyList());
    }

    @Override
    public void enter(Player player) {

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
                if (npc.id() == 494) {
                    if (npc.dead()) {
                        HealthHud.close(player);
                    } else {
                        if (npc.hp() != npc.maxHp()) HealthHud.update(player, npc.hp(), npc.maxHp());
                        else if (!HealthHud.updated && HealthHud.needsUpdate) {
                            HealthHud.open(player, HealthHud.Type.REGULAR, "Kraken", npc.hp());
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
        return true;
    }

    @Override
    public boolean inside(Entity entity) {
        return entity.tile().region() == 9116;
    }
}
