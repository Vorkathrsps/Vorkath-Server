package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.player.Player;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class RaidController {

    private final List<RaidBuilder> npcs;

    public RaidController(List<RaidBuilder> npcs) {
        super();
        this.npcs = npcs;
    }

    public void build(Player player, InstancedArea instancedArea) {
        for (RaidBuilder r : npcs) {
            r.build(player, instancedArea);
        }
    }

}

