package com.cryptic.model.content.raids.chamber_of_xeric;

import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.instance.InstanceConfigurationBuilder;
import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.content.raids.Raids;
import com.cryptic.model.content.raids.RaidsNpc;
import com.cryptic.model.content.raids.RaidsObjects;
import com.cryptic.model.content.raids.chamber_of_xeric.reward.ChamberOfXericReward;
import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.Venom;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Color;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;
import static com.cryptic.model.entity.attributes.AttributeKey.PERSONAL_POINTS;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @Author Origin
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

        build(party);
    }

    @Override
    public void exit(Player player) {
        player.setRaids(null);

        Party party = player.raidsParty;

        //Remove players from the party if they are not the leader
        if(party != null) {
            party.removeMember(player);
            //Last player in the party leaves clear the whole thing
            if(party.getMembers().isEmpty()) {
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
        for (var o : party.objects) {
            if (o != null) {
                o.remove();
            }
        }
        party.monsters.clear();
        party.objects.clear();
    }

    @Override
    public boolean death(Player player) {
        Party party = player.raidsParty;
        if (party == null) return false;
        player.teleport(respawnTile(party, player.tile().level));
        int pointsLost = (int) (player.<Integer>getAttribOr(PERSONAL_POINTS, 0) * 0.4);
        if (pointsLost > 0) {
            addPoints(player, -pointsLost);
        }
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
            case 7 -> new Tile(3232, 5721, level);
            default -> throw new IllegalStateException("Raid bad tile: " + party.getRaidStage());
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

    /***
     * @Author: Origin
     * @param party
     */
    public void build(Party party) {
        GameObject[] objects = new GameObject[]{GameObject.spawn(CRYSTAL_30018, 3232, 5749, party.getHeight(), 10, 0), GameObject.spawn(CRYSTAL_30027, 3233, 5751, party.getHeight(), 10, 0), GameObject.spawn(LARGE_HOLE, 3238, 5738, party.getHeight(), 10, 1), GameObject.spawn(LARGE_ROCK_29883, 3238, 5733, party.getHeight(), 10, 1), GameObject.spawn(CRYSTALLINE_STRUCTURE, 3238, 5743, party.getHeight(), 10, 1), new GameObject(29888, new Tile(3238, 5743, party.getHeight()), 10, 1), new GameObject(29882, new Tile(3238, 5738, party.getHeight()), 10, 1), new GameObject(29885, new Tile(3238, 5733, party.getHeight()), 10, 1), new GameObject(29888, new Tile(3220, 5733, party.getHeight()), 10, 3), new GameObject(29882, new Tile(3220, 5738, party.getHeight()), 10, 3), new GameObject(29885, new Tile(3220, 5743, party.getHeight()), 10, 3)};
        NPC[] entity = new NPC[]{new RaidsNpc(GREAT_OLM_7554, new Tile(3238, 5738, party.getHeight()), Direction.WEST, party.getSize(), true), new RaidsNpc(GREAT_OLM_LEFT_CLAW_7555, new Tile(3238, 5733, party.getHeight()), Direction.WEST, party.getSize(), true), new RaidsNpc(GREAT_OLM_RIGHT_CLAW_7553, new Tile(3238, 5743, party.getHeight()), Direction.WEST, party.getSize(), true)};
        party.objects.addAll(Arrays.asList(objects));
        party.monsters.addAll(Arrays.asList(entity));
        for (var n : party.monsters) {
            n.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
            n.setInstance(party.getLeader().getInstancedArea());
            n.putAttrib(AttributeKey.RAID_PARTY, party);
            n.spawn(false);
        }
    }
}
