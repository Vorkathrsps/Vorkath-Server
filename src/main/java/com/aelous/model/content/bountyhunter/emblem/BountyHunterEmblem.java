package com.aelous.model.content.bountyhunter.emblem;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Patrick van Elderen | December, 07, 2020, 10:14
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum BountyHunterEmblem {

    ANTIQUE_EMBLEM_TIER_1(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_1,250,1,0),
    ANTIQUE_EMBLEM_TIER_2(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_2,500,2,1),
    ANTIQUE_EMBLEM_TIER_3(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_3,1000,4,2),
    ANTIQUE_EMBLEM_TIER_4(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_4,2500,8,3),
    ANTIQUE_EMBLEM_TIER_5(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_5,6000,12,4),
    ANTIQUE_EMBLEM_TIER_6(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_6,10000,15,5),
    ANTIQUE_EMBLEM_TIER_7(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_7,15000,20,6),
    ANTIQUE_EMBLEM_TIER_8(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_8,20000,25,7),
    ANTIQUE_EMBLEM_TIER_9(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_9,25000,32,8),
    ANTIQUE_EMBLEM_TIER_10(ItemIdentifiers.ANTIQUE_EMBLEM_TIER_10,30000,40,9);

    private final int itemId;
    private final int bm;
    private final int targetPoints;
    private final int index;

    BountyHunterEmblem(int itemId, int bm, int targetPoints, int index) {
        this.itemId = itemId;
        this.bm = bm;
        this.targetPoints = targetPoints;
        this.index = index;
    }

    public int getItemId() {
        return itemId;
    }

    public int getBm() {
        return bm;
    }

    public int getTargetPoints() {
        return targetPoints;
    }

    public int getIndex() {
        return index;
    }

    public static Optional<BountyHunterEmblem> forId(int id) {
        return Arrays.stream(values()).filter(a -> a.itemId == id).findAny();
    }

    public BountyHunterEmblem getNextOrLast() {
        int increaseBy = 1;
        return valueOf(index + increaseBy).orElse(ANTIQUE_EMBLEM_TIER_10);
    }

    public BountyHunterEmblem getPreviousOrFirst() {
        return valueOf(index - 1).orElse(ANTIQUE_EMBLEM_TIER_1);
    }

    public static final Set<BountyHunterEmblem> EMBLEMS = Collections.unmodifiableSet(EnumSet.allOf(BountyHunterEmblem.class));

    public static Optional<BountyHunterEmblem> valueOf(int index) {
        return EMBLEMS.stream().filter(emblem -> emblem.index == index).findFirst();
    }

    static final Comparator<BountyHunterEmblem> BEST_EMBLEM_COMPARATOR = Comparator.comparingInt(bountyHunterEmblem -> bountyHunterEmblem.itemId);

    public static Optional<BountyHunterEmblem> getBest(Player player, boolean exclude) {
        List<BountyHunterEmblem> emblems = EMBLEMS.stream().filter(exclude(player, exclude)).collect(Collectors.toList());

        if (emblems.isEmpty()) {
            return Optional.empty();
        }

        return emblems.stream().max(BEST_EMBLEM_COMPARATOR);
    }

    private static Predicate<BountyHunterEmblem> exclude(Player player, boolean exclude) {
        return emblem -> player.inventory().contains(new Item(emblem.getItemId())) && (!exclude || exclude && !emblem.equals(ANTIQUE_EMBLEM_TIER_10));
    }
}
