package com.aelous.model.content.skill.impl.fishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Bart on 12/1/2015.
 */
public enum FishSpotType {

    NET(621, 303, "You need a net to catch these fish.", -1, new ArrayList<>(List.of(Fish.SHRIMP, Fish.ANCHOVIES)), "You cast out your net..."),
    BAIT(623, 307, "You need a Fishing Rod to Bait these fish.", 313, new ArrayList<>(List.of(Fish.SARDINE, Fish.HERRING)), "You cast out your line...", "You don't have any Fishing Bait left."),
    BIG_NET(620, 305, "You need a Big Net to catch these fish.", -1, new ArrayList<>(List.of(Fish.BASS, Fish.MACKEREL, Fish.COD)), "You cast out your net..."),
    CAGE(619, 301, "You need a Lobster Pot to catch Lobsters.", -1, new ArrayList<>(List.of(Fish.LOBSTER)), "You attempt to catch a Lobster."),
    MONKFISH(621, 303, "You need a net to catch these fish.", -1, new ArrayList<>(List.of(Fish.MONKFISH)), "You cast out your net..."),
    HARPOON(618, 311, "You need a Harpoon to catch these fish.", -1, new ArrayList<>(List.of(Fish.TUNA, Fish.SWORDFISH)), "You start to lure the fish."),
    HARPOON_SHARK(618, 311, "You need a Harpoon to catch these fish.", -1, new ArrayList<>(List.of(Fish.SHARK)), "You start to lure the fish."),
    FLY(623, 309, "You need a Fly Fishing Rod to catch these fish.", 314, new ArrayList<>(List.of(Fish.TROUT, Fish.SALMON, Fish.PIKE)), "You cast out your line...", "You don't have any feathers left."),
    DARK_CRAB(619, 301, "You need a Lobster Pot and Dark Bait to catch these Dark Crabs.", 11940, new ArrayList<>(List.of(Fish.DARK_CRAB)), "You attempt to catch a Dark Crab.", "You don't have any Dark Fishing Bait."),
    ANGLERGISH(623, 307, "You need a Fishing Rod to bait these fish.", 13431, new ArrayList<>(List.of(Fish.ANGLERFISH, Fish.RAINBOW)), "You cast our your line...", "You don't have any Fishing Bait left."),
    INFERNAL_EEL(623, 1585, "You need an Oily Fishing Rod to bait these fish.", 313, new ArrayList<>(List.of(Fish.INFERNAL_EEL)), "You cast out your line...", "You don't have any Fishing Bait left."),
    ;

    public int anim;
    public int staticRequiredItem;
    public String missingText;
    public int baitItem;
    public ArrayList<Fish> catchables;
    public String start;
    public String baitMissing;

    FishSpotType(int anim, int staticRequiredItem, String missingText, int baitItem, ArrayList<Fish> catchables, String start, String baitMissing) {
        this.anim = anim;
        this.staticRequiredItem = staticRequiredItem;
        this.missingText = missingText;
        this.baitItem = baitItem;
        this.catchables = catchables;
        this.start = start;
        this.baitMissing = baitMissing;
    }

    FishSpotType(int anim, int staticRequiredItem, String missingText, int baitItem, ArrayList<Fish> catchables, String start) {
        this.anim = anim;
        this.staticRequiredItem = staticRequiredItem;
        this.missingText = missingText;
        this.baitItem = baitItem;
        this.catchables = catchables;
        this.start = start;
    }

    public Fish randomFish(int lvl) {
        if (catchables.size() == 1)
            return catchables.get(0);
        List<Fish> list = catchables.stream().filter(fish -> fish.lvl <= lvl).collect(Collectors.toList());
        Collections.shuffle(list);
        return list.get(0);
    }

    public Fish bestFish(int lvl) {
        if (catchables.size() == 1)
            return catchables.get(0);
        var list = catchables.stream().filter(e -> e.lvl < lvl).sorted(Comparator.comparingInt(o -> o.lvl)).collect(Collectors.toList());
        return list.get(list.size() - 1);
    }

    public int levelReq() {
        Fish fish = catchables.stream().sorted(Comparator.comparingInt(o -> o.lvl)).min(Comparator.naturalOrder()).orElse(null);
        return fish == null ? 1 : fish.lvl;
    }
}
