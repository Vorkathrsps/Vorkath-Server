package com.aelous.model.content.skill.impl.fishing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 12/1/2015.
 */
public enum FishSpot {

    NET_BAIT(1518, new ArrayList<>(List.of(FishSpotType.NET, FishSpotType.BAIT))),
    CAGE_HARPOON(1519, new ArrayList<>(List.of(FishSpotType.CAGE, FishSpotType.HARPOON))),
    NET_HARPOON(1520, new ArrayList<>(List.of(FishSpotType.BIG_NET, FishSpotType.HARPOON_SHARK))),
    FLY_FISHING(1527, new ArrayList<>(List.of(FishSpotType.FLY, FishSpotType.BAIT))),
    DARK_CRAB(1536, new ArrayList<>(List.of(FishSpotType.DARK_CRAB))),
    MONKFISH(4316, new ArrayList<>(List.of(FishSpotType.MONKFISH, FishSpotType.HARPOON_SHARK))),
    ANGLERFISH(6825, new ArrayList<>(List.of(FishSpotType.ANGLERGISH))),
    INFERNAL_EEL(7676, new ArrayList<>(List.of(FishSpotType.INFERNAL_EEL, FishSpotType.INFERNAL_EEL)));

    public int id;
    public ArrayList<FishSpotType> types;

    FishSpot(int id, ArrayList<FishSpotType> types) {
        this.id = id;
        this.types = types;
    }
}
