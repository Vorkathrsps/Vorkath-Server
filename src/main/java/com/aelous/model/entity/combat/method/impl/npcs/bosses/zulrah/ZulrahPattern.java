package com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah;

import java.util.Arrays;
import java.util.List;

import static com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah.ZulrahConfig.*;
import static com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah.ZulrahForm.*;
import static com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah.ZulrahPosition.*;

/**
 * Created by Bart on 3/6/2016.
 */
public class ZulrahPattern {
    //TODO: add other Zulrah rotations/patterns.
    public static final ZulrahPattern PATTERN_1 = new ZulrahPattern(
        Arrays.asList(
            new ZulrahPhase(RANGE, CENTER, Arrays.asList(FULL_TOXIC_FUMES, NO_ATTACK)),
            new ZulrahPhase(MELEE, CENTER),
            new ZulrahPhase(MAGIC, CENTER),
            new ZulrahPhase(RANGE, SOUTH, Arrays.asList(SNAKELINGS_CLOUDS_SNAKELINGS)),
            new ZulrahPhase(MELEE, CENTER),
            new ZulrahPhase(MAGIC, WEST),
            new ZulrahPhase(RANGE, SOUTH, Arrays.asList(EAST_SNAKELINGS_REST_FUMES, NO_ATTACK)),
            new ZulrahPhase(MAGIC, SOUTH, Arrays.asList(SNAKELING_FUME_MIX)),
            new ZulrahPhase(JAD_RM, WEST, Arrays.asList(FULL_TOXIC_FUMES)),
            new ZulrahPhase(MELEE, CENTER)
            )
    );

    private final List<ZulrahPhase> phases;

    public ZulrahPattern(List<ZulrahPhase> phases) {
        this.phases = phases;
    }

    public List<ZulrahPhase> getPhases() {
        return phases;
    }
}
