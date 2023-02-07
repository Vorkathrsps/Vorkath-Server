package com.aelous.model.inter.clan;

/**
 * The rankings of the clan chat.
 * @author PVE
 * @Since juli 07, 2020
 */
public enum ClanRank {
    ANYONE("Member", -1),
    FRIEND("Friend", 197),
    RECRUIT("Recruit", 198),
    CORPORAL("Corporal", 199),
    SERGEANT("Sergeant", 200),
    LIEUTENANT("Lieutenant", 201),
    CAPTAIN("Captain", 202),
    GENERAL("General", 203),
    LEADER("Leader", 204),
    ADMIN("Admin", 168);

    private final String name;

    private final int rankIndex;

    ClanRank(String name, int rank) {
        this.name = name;
        this.rankIndex = rank;
    }

    public String getName() {
        return name;
    }

    public int getRankIndex() {
        return rankIndex;
    }

    public static ClanRank forId(int id) {
        for (ClanRank rank : ClanRank.values()) {
            if (rank.ordinal() == id) {
                return rank;
            }
        }
        return null;
    }

    public boolean lessThan(ClanRank other) {
        return rankIndex < other.rankIndex;
    }

    public boolean owner() {
        return rankIndex == 204 || rankIndex == 168;
    }
}
