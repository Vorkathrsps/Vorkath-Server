package com.aelous.model.map.position.areas.impl;

import com.aelous.model.content.duel.Dueling;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.content.bountyhunter.BountyHunter;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.inter.InterfaceID;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.Controller;
import com.aelous.utility.Varbit;
import com.aelous.utility.timers.TimerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;

import static com.aelous.model.entity.player.QuestTab.InfoTab.PLAYERS_PKING;

public class WildernessArea extends Controller {

    private static final Logger log = LoggerFactory.getLogger(WildernessArea.class);
    public static final Area getFeroxCenter = new Area(3118, 3623, 3153, 3634, 0); //block this when tb'd

    public static final Area getFeroxUpperNorth =  new Area(3120, 3636, 3157, 3640, 0); //block this when tb'd

    public static final Area getFeroxNorthEntrance =  new Area(3122, 3636, 3157, 3643, 0); //block this when tb'd

    public static final Area getFeroxNorthEdges =  new Area(3136, 3642, 3156, 3646, 0); //block this when tb'd

    public static final Area getFeroxEastEdges =  new Area(3154, 3627, 3159, 3633, 0); //block this when tb'd

    public static final Area getFeroxLowerSouth =  new Area(3120, 3621, 3151, 3622, 0); //block this when tb'd

    public static final Area getFeroxLowerSouthEdges =  new Area(3124, 3616, 3144, 3620, 0); //block this when tb'd

    public static final Area getFeroxSouthEntrance =  new Area(3129, 3610, 3140, 3615, 0); //block this when tb'd

    public static final Area getFeroxRandomLine =  new Area(3120, 3635, 3154, 3635, 0); //block this when tb'd

    public static boolean inWilderness(Tile tile) {
        return wildernessLevel(tile) > 0;
    }

    public static boolean inWild(Player player) {
        return inWilderness(player.tile());
    }

    public static int wildernessLevel(Tile tile) {
        int region = tile.region();
        int x = tile.getX();
        int y = tile.getY();
        int z = (tile.y > 6400) ? tile.y - 6400 : tile.y;

        if (region == 12700 || region == 12187) {
            return 0;
        }

        CustomWildernessRegions customWildernessRegions = CustomWildernessRegions.byRegion(tile.region());
        if (customWildernessRegions != null && customWildernessRegions.region == tile.region()) {
            return customWildernessRegions.level;
        }

        if (!(tile.x > 2941 && tile.x < 3392 && tile.y > 3524 && tile.y < 3968) && !inUndergroundWilderness(tile))
            return 0;

        // North of black knights fortress and more - people lure here.
        if (tile.inArea(2998, 3525, 3026, 3536) || tile.inArea(3005, 3537, 3023, 3545)
            || tile.equals(2997, 3525) || tile.inArea(3024, 3537, 3026, 3542)
            || tile.inArea(3027, 3525, 3032, 3530) || tile.inArea(3003, 3537, 3004, 3538)
            // And level 20, west side of wildy, trollhiem shortcut. More people lure here :)
            || tile.inArea(2941, 3676, 2947, 3681)) {
            return 0;
        }

        if (region == 13623) {
            return 0;
        }

        if (x >= 2944 && x <= 3391 && y >= 3520 && y <= 4351) {
            return ((y - 3520) >> 3) + 1;
        } else if (x >= 3008 && x <= 3071 && y >= 10112 && y <= 10175) {
            return ((y - 9920) >> 3) - 1;
        } else if (x >= 2944 && x <= 3391 && y >= 9920 && y <= 10879) {
            return ((y - 9920) >> 3) + 1;
        }
        return 0;
    }

    public static boolean inUndergroundWilderness(Tile tile) {
        int region = tile.region();
        // Revenant caves:
        if (region == 12701 || region == 12702 || region == 12703 || region == 12957 || region == 12958 || region == 12959)
            return true;

        return region == 12192 || region == 12193 || region == 12961 || region == 11937 || region == 12443 || region == 12190;
    }

    // A small custom area between 1-4 wilderness were range is disabled in an attempt to stop raggers. Note: may not be in use.
    public static boolean inRestrictedRangeZone(Tile tile) {
        return tile.inArea(3041, 3548, 3069, 3561);
    }//HAHAHA

    /**
     * Any area, such as Wilderness, dangerous Instances, FFA clan war arena which a Player can attack another Player
     */
    public static boolean inAttackableArea(Entity player) {
        return WildernessArea.inWild((Player) player) || Dueling.in_duel(player);
    }

    public static boolean inside_pirates_hideout(Tile tile) {
        Area original = new Area(3038, 3949, 3044, 3959);
        return tile.inArea(original);
    }

    public static boolean inside_axehut(Tile tile) {
        return tile.inArea(3187, 3958, 3194, 3962);
    }

    public static boolean inside_rouges_castle(Tile tile) {
        return tile.inArea(3275, 3922, 3297, 3946);
    }

    public static boolean inside_extended_pj_timer_zone(Tile tile) {
        return tile.inArea(3047, 3524, 3096, 3539);
    }

    public static boolean at_west_dragons(Tile tile) {
        return tile.inArea(2964, 3585, 2999, 3622);
    }

    public WildernessArea() {
        super(Collections.emptyList());
    }

    public void refreshInterface(Player player, boolean kd) {
        //53731
        //53732
        //53733
        if (kd) {
            player.getPacketSender().sendString(53731, "Kills: " + player.getAttribOr(AttributeKey.PLAYER_KILLS, 0));
            player.getPacketSender().sendString(53732, "Deaths: " + player.getAttribOr(AttributeKey.PLAYER_DEATHS, 0));
            player.getPacketSender().sendString(53733, "K/D Ratio: " + player.getKillDeathRatio());
        }
        //53723 - target name
        //53724 - loc/combat level
        //53725 - risk/ v.high ect
        //53726 - risk sprite
        //53727 - target emblem sprite
        //53728 - target emblem #
        //53729 - skip target button

    }

    @Override
    public void enter(Player player) {
        player.getPacketSender().sendInteractionOption("Attack", 2, true);
        player.getInterfaceManager().sendOverlay(InterfaceID.BOUNTY_HUNTER);
        refreshWildernessLevel(player);
        player.putAttrib(AttributeKey.INWILD, World.getWorld().cycleCount());
        if (!BountyHunter.PLAYERS_IN_WILD.contains(player)) {
            BountyHunter.PLAYERS_IN_WILD.add(player);
            player.getPacketSender().sendString(PLAYERS_PKING.childId, QuestTab.InfoTab.INFO_TAB.get(PLAYERS_PKING.childId).fetchLineData(player));
        }
        player.getCombat().getDamageMap().clear();
        player.getRisk().update();
        refreshInterface(player, true);
        player.varps().varbit(Varbit.IN_WILDERNESS, 1);
        System.out.println(player.varps().varbit(Varbit.IN_WILDERNESS));
    }

    @Override
    public void leave(Player player) {

        if (!Skulling.skulled(player)) {
            player.clearAttrib(AttributeKey.SKULL_ENTRIES_TRACKER);
        }
        player.getInterfaceManager().removeOverlay();
        player.getPacketSender().sendInteractionOption("null", 2, true);
        BountyHunter.PLAYERS_IN_WILD.remove(player);
        player.clearAttrib(AttributeKey.SPECIAL_ATTACK_USED);
        player.clearAttrib(AttributeKey.INWILD);
        player.clearAttrib(AttributeKey.PVP_WILDY_AGGRESSION_TRACKER);
        player.clearAttrib(AttributeKey.PLAYER_KILLS_WITHOUT_LEAVING_WILD);
        player.varps().varbit(Varbit.IN_WILDERNESS, 0);
        if (player.frozen()) {
            player.getTimers().cancel(TimerKey.FROZEN);
        }
        player.getPacketSender().sendString(PLAYERS_PKING.childId, QuestTab.InfoTab.INFO_TAB.get(PLAYERS_PKING.childId).fetchLineData(player));
    }

    private void refreshWildernessLevel(Player player) {
        int wildLevel = wildernessLevel(player.tile());

        if (wildLevel > 0) {
            player.getPacketSender().sendString(53730, "Level: " + wildLevel);
        }
    }

    @Override
    public void process(Player player) {

        if (!inWilderness(player.tile())) {
            leave(player);
            return;
        }
    }

    @Override
    public void onMovement(Player player) {
        refreshWildernessLevel(player);
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Player attacker, Entity target) {
        // Level checks only apply to PvP
        if (attacker.isPlayer() && target.isPlayer()) {

            // Is the player deep enough in the wilderness?

            var oppWithinLvl = attacker.getSkills().combatLevel() >= CombatFactory.getLowestLevel(target, attacker) && attacker.getSkills().combatLevel() <= CombatFactory.getHighestLevel(target, attacker);

            if (!oppWithinLvl) {
                attacker.message("Your level difference is too great! You need to move deeper into the Wilderness.");
                attacker.getMovementQueue().clear();
                return false;
            } else {
                var withinLvl = (target.getSkills().combatLevel() >= CombatFactory.getLowestLevel(attacker, target) && target.getSkills().combatLevel() <= CombatFactory.getHighestLevel(attacker, target));
                if (!withinLvl) {
                    attacker.message("Your level difference is too great! You need to move deeper into the Wilderness.");
                    attacker.getMovementQueue().clear();
                    return false;
                }
            }

            if (!inWilderness(target.tile())) {
                attacker.message("That player cannot be attacked, because they are not in the Wilderness.");
                attacker.getMovementQueue().clear();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return entity.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }

    @Override
    public void defeated(Player killer, Entity entity) {
        if (killer.isPlayer()) {
            killer.getRisk().update(); // Make sure wealth attribs are up to date!
        }
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int type) {
        return false;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int type) {
        return false;
    }

    @Override
    public boolean inside(Entity entity) {
        if (entity.getAsPlayer().insideFeroxEnclaveSafe()) {
            return false;
        }
        return wildernessLevel(entity.tile()) > 0;
    }

    @Override
    public boolean useInsideCheck() {
        return true; //We want to check using the inside method of this class.
    }
}
