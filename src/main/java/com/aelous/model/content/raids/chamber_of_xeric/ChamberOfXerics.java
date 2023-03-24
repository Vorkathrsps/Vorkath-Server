package com.aelous.model.content.raids.chamber_of_xeric;

import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.instance.InstanceConfigurationBuilder;
import com.aelous.model.content.instance.InstancedArea;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.content.raids.Raids;
import com.aelous.model.content.raids.RaidsNpc;
import com.aelous.model.content.raids.RaidsObjects;
import com.aelous.model.content.raids.chamber_of_xeric.reward.ChamberOfXericReward;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;
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
        var instance = new InstancedArea(new InstanceConfigurationBuilder().setCloseOnPlayersEmpty(true).createInstanceConfiguration(), new Area(3202, 5123, 3390, 5759));
        final int height = instance.getzLevel();
        party.setHeight(height);

        for (Player member : party.getMembers()) {
            member.setRaids(this);
            member.teleport(new Tile(3232, 5721, height)); // straight to olm
            member.setInstance(instance);
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
        player.getSkills().resetStats();
        int increase = player.getEquipment().hpIncrease();
        player.hp(Math.max(increase > 0 ? player.getSkills().level(Skills.HITPOINTS) + increase : player.getSkills().level(Skills.HITPOINTS), player.getSkills().xpLevel(Skills.HITPOINTS)), 39); //Set hitpoints to 100%
        player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5)); //Set the players prayer level to full
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

        olm(party);
        for (NPC monster : party.monsters) {
            monster.setInstance(party.getLeader().instancedArea);
            monster.putAttrib(AttributeKey.RAID_PARTY, party);
        }
    }

    private void olm(Party party) {
        party.greatOlmCrystal = GameObject.spawn(CRYSTAL_30018, 3232, 5749, party.getHeight(), 10, 0);

        party.greatOlmRewardCrystal = GameObject.spawn(CRYSTAL_30027, 3233, 5751, party.getHeight(), 10, 0);

        GameObject.spawn(CRYSTAL_30027, 3233, 5751, party.getHeight(), 10, 0).remove(); // remove the default cache one so this tile is walkable
        // until DynamicMap support is added, or custom Z clipping is supported, you cant have unique clipping at z>3 which is all instances

        GameObject o2 = GameObject.spawn(LARGE_HOLE, 3238, 5738, party.getHeight(), 10, 1);

        GameObject o3 = GameObject.spawn(LARGE_ROCK_29883, 3238, 5733, party.getHeight(), 10, 1);

        GameObject o1 = GameObject.spawn(CRYSTALLINE_STRUCTURE, 3238, 5743, party.getHeight(), 10, 1);

        party.objects.addAll(Lists.newArrayList(o1, o2, o3));
        Lists.newArrayList(o1, o2, o3).forEach(o -> party.getLeader().getInstancedArea().addGameObj(o));

        var o4 = new GameObject(29888, new Tile(3238, 5743, party.getHeight()), 10, 1);
        var o5 = new GameObject(29882, new Tile(3238, 5738, party.getHeight()), 10, 1);
        var o6 = new GameObject(29885, new Tile(3238, 5733, party.getHeight()), 10, 1);

        // left side
        var o7 = new GameObject(29888, new Tile(3220, 5733, party.getHeight()), 10, 3);
        var o8 = new GameObject(29882, new Tile(3220, 5738, party.getHeight()), 10, 3);
        var o9 = new GameObject(29885, new Tile(3220, 5743, party.getHeight()), 10, 3);

        var cacheLandscapeObjects = Lists.newArrayList(o4,o5,o6,o7,o8,o9);
        party.objects.addAll(cacheLandscapeObjects);
        cacheLandscapeObjects.forEach(o -> party.getLeader().getInstancedArea().addGameObj(o));
        World.getWorld().getSpawnedObjs().addAll(cacheLandscapeObjects);
        LogManager.getLogger("cox").info("adding {}", cacheLandscapeObjects);

        NPC spawn = new RaidsNpc(GREAT_OLM_7554, new Tile(3238, 5738, party.getHeight()), Direction.WEST, party.getSize(), true);

        NPC spawn1 = new RaidsNpc(GREAT_OLM_LEFT_CLAW_7555, new Tile(3238, 5733, party.getHeight()), Direction.WEST, party.getSize(), true);

        NPC spawn2 = new RaidsNpc(GREAT_OLM_RIGHT_CLAW_7553, new Tile(3238, 5743, party.getHeight()), Direction.WEST, party.getSize(), true);

        spawn.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
        spawn1.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
        spawn2.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);

        var mobs = Lists.newArrayList(spawn, spawn1, spawn2);
        party.monsters.addAll(mobs);
        for (NPC mob : mobs) {
            mob.spawn(false);
        }

    }
}
