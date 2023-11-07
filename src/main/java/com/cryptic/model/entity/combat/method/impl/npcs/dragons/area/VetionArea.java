package com.cryptic.model.entity.combat.method.impl.npcs.dragons.area;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.utility.chainedwork.Chain;

import java.util.List;

public class VetionArea extends Controller {
    public VetionArea() {
        super(List.of(new Area(3248, 10192, 3306, 10212)));
    }
    @Override
    public void enter(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc.id() == 6611) {
                    if (npc.hp() == npc.maxHp()) {
                        Chain.noCtx().runFn(1, () -> {
                            npc.npc().canAttack(false);
                            npc.lockNoDamage();
                            npc.animate(9977);
                        }).then(2, () -> {
                            npc.unlock();
                            npc.npc().canAttack(true);
                            npc.getCombat().setTarget(player);
                        });
                    }
                    if (!npc.dead()) {
                        HealthHud.open(player, HealthHud.Type.REGULAR, "Vet'ion", npc.hp());
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
                if (npc.id() == 6611) {
                    if (npc.dead()) {
                        player.getPacketSender().darkenScreen(0);
                        HealthHud.close(player);
                    } else {
                        if (npc.hp() != npc.maxHp()) HealthHud.update(player, npc.hp(), npc.maxHp());
                        else if (!HealthHud.updated && HealthHud.needsUpdate) {
                            HealthHud.open(player, HealthHud.Type.REGULAR, "Vet'ion", npc.hp());
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
        return false;
    }
}
