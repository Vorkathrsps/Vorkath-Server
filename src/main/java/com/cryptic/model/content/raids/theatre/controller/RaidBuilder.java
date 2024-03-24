package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public interface RaidBuilder {
    void build(Player player, InstancedArea theatreInstance);
    int scale(NPC npc, Player player, boolean hardMode);

}
