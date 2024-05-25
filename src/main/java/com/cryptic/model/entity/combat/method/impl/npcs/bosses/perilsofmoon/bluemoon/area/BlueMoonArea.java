package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.area;

import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.areas.Controller;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class BlueMoonArea extends Controller {
    public static final Area ROOM = new Area(1421, 9665, 1457, 9698);

    public BlueMoonArea() {
        super(Collections.emptyList());
    }

    @Override
    public void enter(Player player) {
        for (var regions : player.getRegions()) {
            for (var npc : regions.getNpcs()) {
                if (npc == null || npc.dead() || npc.tile().getLevel() != player.tile().getLevel()) continue;
                if (npc.id() != 13017) continue;
                HealthHud.open(player, HealthHud.Type.CYAN_SHIELD, "Blue Moon", npc.hp());
                if (npc.hp() != npc.maxHp()) HealthHud.update(player, npc.hp(), npc.maxHp());
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
                if (npc == null || npc.tile().getLevel() != player.tile().getLevel()) continue;
                if (npc.id() != 13017) continue;
                if (npc.dead()) {
                    HealthHud.close(player);
                } else {
                    if (npc.hp() != npc.maxHp())
                        HealthHud.update(player, HealthHud.Type.CYAN_SHIELD, npc.hp(), npc.maxHp());
                    else if (!HealthHud.updated && HealthHud.needsUpdate) {
                        System.out.println("ye we need update doe");
                        HealthHud.open(player, HealthHud.Type.CYAN_SHIELD, "Blue Moon", npc.hp());
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
        var instance = player.getPerilInstance();
        if (instance == null) return false;
        if (object.getId() == 52993) {
            if (option == 1) {
                player.animate(811);
                instance.getOwner().varps().setVarbit(9856, 0);
                for (var p : instance.getPlayers()) p.varps().setVarbit(9856, 0);
                for (var o : Lists.newArrayList(instance.getBraziers().iterator())) {
                    if (o != object) continue;
                    o.attribs().clear();
                    instance.getBraziers().remove(o);
                }
            }
        } else if (object.getId() == 52992) {
            if (option == 1) {
                player.animate(811);
                instance.getOwner().varps().setVarbit(9855, 0);
                for (var p : instance.getPlayers()) p.varps().setVarbit(9855, 0);
                for (var o : Lists.newArrayList(instance.getBraziers().iterator())) {
                    if (o != object) continue;
                    o.attribs().clear();
                    instance.getBraziers().remove(o);
                }
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
}
