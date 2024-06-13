package com.cryptic.model.inter.clan;

import com.cryptic.model.entity.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents the member of Clan chat.
 * @author PVE
 * @Since juli 07, 2020
 */
@Getter
public class ClanMember {

    /**
     * The player of the member.
     */
    private final transient Player player;

    /**
     * The rank of the member.
     */
    @Setter
    private ClanRank rank;

    /**
     * Creates the member.
     * @param player
     * @param rank
     */
    public ClanMember(Player player, ClanRank rank) {
        this.player = player;
        this.rank = rank;
    }

    public String getName() {
        return player.getUsername().toLowerCase().trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClanMember member) {
            return member.hashCode() == hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player.getUsername(), rank);
    }

    @Override
    public String toString() {
        return String.format("name=%s, rank=%s", getName(), getRank());
    }

}
