package com.aelous.model.map.position.areas;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;

import java.util.List;

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

    public List<Area> getAreas() {
        return areas;
    }
    //If we want to use the AbstractArea inside method to check instead of the AreaManager inside method
    //to check, we should set this to true in the AbstractArea that overrides this.
    public abstract boolean useInsideCheck();

    public abstract boolean inside(Entity entity);
}
