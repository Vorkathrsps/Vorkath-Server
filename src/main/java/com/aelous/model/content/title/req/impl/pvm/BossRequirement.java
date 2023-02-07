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
public class BossRequirement extends TitleRequirement {

    private final int kills;

    public BossRequirement(int kills) {
        super("Obtain " + kills + " Boss Kills");
        this.kills = kills;
    }

    @Override
    public boolean satisfies(Player player) {
        List<AttributeKey> bosses_killed = new ArrayList<>(List.of(KING_BLACK_DRAGONS_KILLED, VENENATIS_KILLED, VETIONS_KILLED, CRAZY_ARCHAEOLOGISTS_KILLED, CHAOS_ELEMENTALS_KILLED, DEMONIC_GORILLAS_KILLED, BARRELCHESTS_KILLED, LIZARDMAN_SHAMANS_KILLED,
            ZULRAHS_KILLED, ALCHY_KILLED, KRAKENS_KILLED, CORPOREAL_BEASTS_KILLED, CERBERUS_KILLED, VORKATHS_KILLED, SCORPIAS_KILLED, CALLISTOS_KILLED, VENENATIS_KILLED, THERMONUCLEAR_SMOKE_DEVILS_KILLED, GENERAL_GRAARDOR_KILLED, KREE_ARRA_KILLED, COMMANDER_ZILYANA_KILLED,
            KRIL_TSUTSAROTHS_KILLED, SKOTIZOS_KILLED, ZOMBIES_CHAMPIONS_KILLED, TEKTONS_KILLED, CHAOS_FANATICS_KILLED, BRUTAL_LAVA_DRAGONS_KILLED));
        int totalKills = bosses_killed.stream().mapToInt(k -> player.getAttribOr(k, 0)).sum();
        return totalKills >= kills;
    }

}
