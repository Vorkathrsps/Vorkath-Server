package com.cryptic.model.map.position.areas;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Controller {

    private final List<Area> areas;

    public Controller(List<Area> areas) {
        this.areas = areas;
    }

    public abstract void enter(Player player);

    public abstract void leave(Player player);

    public abstract void process(Player player);

    public abstract void onMovement(Player player);

    public abstract boolean canTeleport(Player player);

    public abstract boolean canAttack(Player attacker, Entity target);

    public abstract void defeated(Player player, Entity entity);

    public abstract boolean canTrade(Player player, Player target);

    public abstract boolean isMulti(Entity entity);

    public abstract boolean canEat(Player player, int itemId);

    public abstract boolean canDrink(Player player, int itemId);

    public abstract void onPlayerRightClick(Player player, Player other, int option);

    public abstract boolean handleObjectClick(Player player, GameObject object, int option);

    public abstract boolean handleNpcOption(Player player, NPC npc, int option);

    public abstract boolean useInsideCheck();

    public abstract boolean inside(Entity entity);
}
