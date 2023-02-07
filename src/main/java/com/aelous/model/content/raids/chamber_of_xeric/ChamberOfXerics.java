package com.aelous.model.content.raids.chamber_of_xeric;

import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.content.raids.Raids;
import com.aelous.model.content.raids.RaidsNpc;
import com.aelous.model.content.raids.RaidsObjects;
import com.aelous.model.content.raids.chamber_of_xeric.reward.ChamberOfXericReward;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;

import static com.aelous.model.entity.attributes.AttributeKey.PERSONAL_POINTS;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 29, 2021
 */
public class ChamberOfXerics extends Raids {

    @Override
    public void startup(Player player) {
        Party party = player.raidsParty;
        if (party == null) return;
        party.setRaidStage(6);
        final int height = party.getLeader().getIndex() * 4;

        for (Player member : party.getMembers()) {
            member.setRaids(this);
            member.teleport(new Tile(3299, 5189, height));
            party.setHeight(height);
        }

        //Clear kills
        party.setKills(0);

        //Clear npcs that somehow survived first
        clearParty(player);

        //Spawn all monsters
        spawnMonsters(player);
    }

    @Override
    public void exit(Player player) {
        player.setRaids(null);

        Party party = player.raidsParty;

        //Remove players from the party if they are not the leader
        if(party != null) {
            party.removeMember(player);
            //Last player in the party leaves clear the whole thing
            if(party.getMembers().size() == 0) {
                //Clear all party members that are left
                party.getMembers().clear();
                clearParty(player);
            }
            player.raidsParty = null;
        }

        //Reset points
        player.putAttrib(PERSONAL_POINTS,0);
        player.message("<col=" + Color.BLUE.getColorValue() + ">You have restored your hitpoints, run energy and prayer.");
        player.message("<col=" + Color.HOTPINK.getColorValue() + ">You've also been cured of poison and venom.");
        player.skills().resetStats();
        int increase = player.getEquipment().hpIncrease();
        player.hp(Math.max(increase > 0 ? player.skills().level(Skills.HITPOINTS) + increase : player.skills().level(Skills.HITPOINTS), player.skills().xpLevel(Skills.HITPOINTS)), 39); //Set hitpoints to 100%
        player.skills().replenishSkill(5, player.skills().xpLevel(5)); //Set the players prayer level to full
        player.setRunningEnergy(100.0, true);
        Poison.cure(player);
        Venom.cure(2, player);

        //Move outside of raids
        player.teleport(1245, 3561, 0);
        player.getInterfaceManager().close(true);
    }

    @Override
    public void complete(Party party) {
        party.forPlayers(p -> {
            p.message(Color.RAID_PURPLE.wrap("Congratulations - your raid is complete!"));
            var completed = p.<Integer>getAttribOr(AttributeKey.CHAMBER_OF_SECRET_RUNS_COMPLETED, 0) + 1;
            p.putAttrib(AttributeKey.CHAMBER_OF_SECRET_RUNS_COMPLETED, completed);
            p.message(String.format("Total points: " + Color.RAID_PURPLE.wrap("%,d") + ", Personal points: " + Color.RAID_PURPLE.wrap("%,d") + " (" + Color.RAID_PURPLE.wrap("%.2f") + "%%)",
                party.totalPoints(), p.<Integer>getAttribOr(PERSONAL_POINTS, 0), (double) (p.<Integer>getAttribOr(PERSONAL_POINTS, 0) / party.totalPoints()) * 100));

            //Daily raids task
            DailyTaskManager.increase(DailyTasks.DAILY_RAIDS, p);

            //Roll a reward for each individual player
            ChamberOfXericReward.giveRewards(p);
        });
    }

    @Override
    public void clearParty(Player player) {
        Party party = player.raidsParty;
        if(party == null) return;
        if(party.monsters == null) {
            return;
        }
        for(NPC npc : party.monsters) {
            if(npc.isRegistered() || !npc.dead()) {
                World.getWorld().unregisterNpc(npc);
            }
        }
        party.monsters.clear();
    }

    @Override
    public boolean death(Player player) {
        Party party = player.raidsParty;
        if (party == null) return false;
        player.teleport(respawnTile(party, player.tile().level));
        int pointsLost = (int) (player.<Integer>getAttribOr(PERSONAL_POINTS, 0) * 0.4);
        if (pointsLost > 0)
            addPoints(player, -pointsLost);

        //Make sure to heal
        player.healPlayer();
        return true;
    }

    @Override
    public Tile respawnTile(Party party, int level) {
        return switch (party.getRaidStage()) {
            case 1 -> new Tile(3310, 5277, level);
            case 2 -> new Tile(3311, 5279, level);
            case 3 -> new Tile(3311, 5311, level);
            case 4 -> new Tile(3311, 5309, level);
            case 5 -> new Tile(3311, 5277, level);
            case 6 -> new Tile(3232, 5721, level);
            default -> throw new IllegalStateException("Unexpected value: " + party.getRaidStage());
        };
    }

    @Override
    public void addPoints(Player player, int points) {
        if (!raiding(player))
            return;
        player.raidsParty.addPersonalPoints(player, points);
    }

    @Override
    public void addDamagePoints(Player player, NPC target, int points) {
        if (!raiding(player))
            return;
        if (target.getAttribOr(AttributeKey.RAIDS_NO_POINTS, false))
            return;
        points *= 10;
        addPoints(player, points);
    }

    private void spawnMonsters(Player player) {
        Party party = player.raidsParty;

        //Create
        NPC vasa = new RaidsNpc(7565, new Tile(3308, 5293, party.getHeight()), party.getSize());
        NPC vanguard1 = new RaidsNpc(VANGUARD_7527, new Tile(3316,5326, party.getHeight()), party.getSize());
        NPC vanguard2 = new RaidsNpc(VANGUARD_7528, new Tile(3313,5332, party.getHeight()), party.getSize());
        NPC vanguard3 = new RaidsNpc(VANGUARD_7529, new Tile(3308,5329, party.getHeight()), party.getSize());
        NPC tekton = new RaidsNpc(TEKTON_ENRAGED_7544, new Tile(3313, 5295, party.getHeight()+1), party.getSize());
        NPC babyMuttadile = new RaidsNpc(MUTTADILE_7562, new Tile(3308,5326,party.getHeight()+1), party.getSize());
        NPC mommaMuttadile = new RaidsNpc(MUTTADILE, new Tile(3312,5330, party.getHeight()+1), party.getSize());
        party.setMommaMuttadile(mommaMuttadile);
        GameObject meatTree = new RaidsObjects(30013, new Tile(3301,5320,party.getHeight()+1));
        party.setMeatTree(meatTree);
        ObjectManager.addObj(meatTree);
        NPC vespula = new RaidsNpc(VESPULA, new Tile(3308,5295, party.getHeight()+2), party.getSize());

        //Spawn
        World.getWorld().registerNpc(vasa);
        World.getWorld().registerNpc(vanguard1);
        World.getWorld().registerNpc(vanguard2);
        World.getWorld().registerNpc(vanguard3);
        World.getWorld().registerNpc(tekton);
        World.getWorld().registerNpc(babyMuttadile);
        World.getWorld().registerNpc(mommaMuttadile);
        World.getWorld().registerNpc(vespula);

        //Add to list
        party.monsters.add(vasa);
        party.monsters.add(vanguard1);
        party.monsters.add(vanguard2);
        party.monsters.add(vanguard3);
        party.monsters.add(tekton);
        party.monsters.add(babyMuttadile);
        party.monsters.add(mommaMuttadile);
        party.monsters.add(vespula);
    }
}
