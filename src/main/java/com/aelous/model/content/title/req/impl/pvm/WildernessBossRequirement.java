package com.aelous.model.content.title.req.impl.pvm;

import com.aelous.model.content.title.req.TitleRequirement;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class WildernessBossRequirement extends TitleRequirement {
    private final int kills;

    public WildernessBossRequirement(int kills) {
        super("Kill " + kills + " Wilderness <br>bosses");
        this.kills = kills;
    }

    @Override
    public boolean satisfies(Player player) {
        List<AttributeKey> wildy_bosses_killed = new ArrayList<>(List.of(KING_BLACK_DRAGONS_KILLED, VENENATIS_KILLED, VETIONS_KILLED, CRAZY_ARCHAEOLOGISTS_KILLED, CHAOS_ELEMENTALS_KILLED, DEMONIC_GORILLAS_KILLED, BARRELCHESTS_KILLED, LIZARDMAN_SHAMANS_KILLED));
        int totalKills = wildy_bosses_killed.stream().mapToInt(k -> player.getAttribOr(k, 0)).sum();
        return totalKills >= kills;
    }
}
