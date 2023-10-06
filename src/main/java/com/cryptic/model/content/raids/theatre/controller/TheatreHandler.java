package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public interface TheatreHandler {
    void build(Player player, TheatreInstance theatreInstance);
    int scale(NPC npc, Player player);

}
