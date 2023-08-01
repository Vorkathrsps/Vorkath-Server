package com.cryptic.model.content.raids.theatre.controller;

import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public interface TheatreRaid {
    void buildRaid(Player player, Theatre theatre, TheatreArea theatreArea);
    int scale(NPC npc, Player player);

}
