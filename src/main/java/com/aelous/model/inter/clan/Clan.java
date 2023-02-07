package com.aelous.model.inter.clan;

import com.aelous.model.entity.player.rights.PlayerRights;
import com.google.gson.annotations.Expose;
import com.aelous.model.content.instance.InstancedArea;
import com.aelous.model.entity.npc.NPC;

import java.util.*;

/**
 * @author PVE
 * @Since juli 07, 2020
 */
public class Clan {

    @Expose()
    private String name;
    @Expose()
    private final String owner;
    @Expose()
    private String slogan = "None";

    @Expose()
    private ClanRank joinable = ClanRank.ANYONE;
    @Expose()
    private ClanRank talkable = ClanRank.ANYONE;
    @Expose()
    private ClanRank kickable = ClanRank.LEADER;
    @Expose()
    private ClanRank managable = ClanRank.LEADER;

    @Expose()
    private boolean lootshare = false;
    @Expose()
    private boolean lock = false;
    @Expose(serialize = false, deserialize = false) // disables GSON read/write
    public InstancedArea meetingRoom;
    @Expose(serialize = false, deserialize = false)
    public List<NPC> dummys;

    @Expose()
    private int memberLimit = 100;

    @Expose()
    private final Map<String, ClanRank> ranked = new HashMap<>();

    @Expose(serialize = false, deserialize = false)
    private transient Queue<ClanMember> members;

    public Clan(String name) {
        this.name = name;
        this.owner = name;
    }

    public void init() {
        members = new LinkedList<>();
    }

    public boolean add(ClanMember member) {
        if (members.size() >= getMemberLimit()) {
            return false;
        }

        if (member.getRank() != ClanRank.ANYONE) {
            ranked.put(member.getName(), member.getRank());
        }

        remove(member);

        members.remove(member);

        return members.add(member);
    }

    public ClanMember get(String name) {
        for (ClanMember next : members) {
            if (next.getName().equalsIgnoreCase(name)) {
                return next;
            }
        }

        return null;
    }

    public void clearRank(ClanMember member) {
        ranked.remove(member.getName(), member.getRank());
        ClanRepository.save();
    }

    public void setRank(ClanMember member) {
        ClanRank rank = ranked.get(member.getName());

        if (PlayerRights.is(member.getPlayer(), PlayerRights.ADMINISTRATOR)) {
            member.setRank(ClanRank.ADMIN);
            return;
        }

        if (rank == null) {
            member.setRank(ClanRank.ANYONE);
            return;
        }

        member.setRank(rank);
    }

    public void remove(ClanMember member) {
        members.removeIf(other -> other != null && other.getPlayer() != null && other.getPlayer().isRegistered() && other.getName().equalsIgnoreCase(member.getName()));
    }

    public boolean clanContains(ClanMember member) {
        return members.stream().anyMatch(toCheck -> toCheck != null && toCheck.getPlayer() != null && toCheck.getPlayer().isRegistered() && toCheck.getName().equalsIgnoreCase(member.getName()));
    }

    public boolean canJoin(ClanMember member) {
        return !member.getRank().lessThan(joinable);
    }

    public boolean canTalk(ClanMember member) {
        return !member.getRank().lessThan(talkable);
    }

    public boolean canKick(ClanMember member) {
        return !member.getRank().lessThan(kickable);
    }

    public boolean canManage(ClanMember member) {
        return !member.getRank().lessThan(managable);
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getSlogan() {
        return slogan;
    }

    public boolean isLootshare() {
        return lootshare;
    }

    public boolean isLocked() {
        return lock;
    }

    public int getMemberLimit() {
        return memberLimit;
    }

    public Queue<ClanMember> members() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public void setJoinable(ClanRank joinable) {
        this.joinable = joinable;
        ClanRepository.save();
    }

    public void setTalkable(ClanRank talkable) {
        this.talkable = talkable;
        ClanRepository.save();
    }

    public void setKickable(ClanRank kickable) {
        this.kickable = kickable;
    }

    public void setManagable(ClanRank managable) {
        this.managable = managable;
        ClanRepository.save();
    }

    public void setLootshare(boolean lootshare) {
        this.lootshare = lootshare;
        ClanRepository.save();
    }

    public void setLocked(boolean lock) {
        this.lock = lock;
        ClanRepository.save();
    }

    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    @Override
    public String toString() {
        return String.format("CLAN[name=%s, owner=%s, ranked=%s, members=%s]", name, owner, ranked, members);
    }

}
