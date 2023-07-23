package com.aelous.model.content.mechanics;

import com.aelous.GameServer;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.duel.Dueling;
import com.aelous.model.content.mechanics.break_items.BreakItemsOnDeath;
import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.controller.TheatreDeath;
import com.aelous.model.content.raids.theatre.stage.RaidDeathState;
import com.aelous.model.content.raids.theatre.stage.RoomState;
import com.aelous.model.content.raids.theatre.stage.TheatreState;
import com.aelous.model.content.tournaments.TournamentManager;
import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.inter.lootkeys.LootKey;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.RETRIBUTION;

/**
 * Created by Bart on 8/15/2015.
 * Retribution by Jak 12/16/2015
 */
public class Death implements TheatreDeath {

    private static final Logger logger = LogManager.getLogger(Death.class);

    private static final String[] KILL_MESSAGES = {
        "%s will probably tell you he wanted a free teleport after that performance.",
        "Such a shame that %s can't play this game.",
        "%s was made to sit down.",
        "You have defeated %s.",
        "A humiliating defeat for %s.",
        "How not to do it right: Written by %s.",
        "The struggle for %s is real.",
        "%s falls before your might.",
        "Can anyone defeat you? Certainly not %s.",
        "%s didn't stand a chance against you.",
        "What was %s thinking challenging you...",
        "%s should take lessons from you. You're clearly too good for him."
    };

    Player player;

    public Death(Player player) {
        this.player = player;
    }

    public static String randomKillMessage() {
        return KILL_MESSAGES[Utils.random(KILL_MESSAGES.length - 1)];
    }

    private static void retrib(Player player) {
        //Retribution. example: https://www.youtube.com/watch?v=7c6idspnxak
        try {
            if (Prayers.usingPrayer(player, RETRIBUTION)) {
                var pker = player.getCombat().getKiller(); // Person who killed the dead player. Might be a 73 AGS spec pj.
                player.graphic(437);
                var damage = (int) (player.getSkills().level(Skills.PRAYER) * 0.25);
                if (player.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1) {
                    var list = new LinkedList<Player>();
                    for (Player p : player.closePlayers(1)) {
                        if (!WildernessArea.inAttackableArea(p) || p.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 0) {
                            //not in the multi area and we were, don't carry over.
                            continue;
                        }
                        if (player.tile().inSqRadius(p.tile(), 1)) {
                            list.add(p);
                        }
                    }

                    var damagePerPlayer = (int) Math.max(1.0, (double) damage / Math.max(1, list.size()));
                    list.forEach(p -> {
                        p.hit(player, damagePerPlayer);
                    });
                } else if (player.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 0 && pker.isPresent()) {
                    if (player.tile().inSqRadius(pker.get().tile(), 1)) {
                        pker.get().hit(player, damage);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Death error!", e);
        }
    }

    public void death(Player player) {
        if (this.inRaid(player)) {
            this.handleRaidDeath(player);
            return;
        }

        player.lock(); //Lock the player

        player.putAttrib(DEATH_TICK, World.getWorld().cycleCount());
        player.putAttrib(DEATH_TILE, player.tile());

        Chain.bound(null).name("check_double_death_task").runFn(3, () -> {// Finish the proper delay after death (2 ticks)
            try {
                Dueling.check_double_death(player); // must be checked after damage shows (because of PID you can't do it on the same cycle!)
            } catch (Exception e) {
                logger.error("Double death check error!", e);
            }
        });

        player.stopActions(true);
        player.action.reset();

        Death.retrib(player);

        var mostdmg = player.getCombat().getKiller();
        var killer = mostdmg.orElse(null);
        var in_tournament = player.inActiveTournament() || player.isInTournamentLobby();

        player.animate(836); //Animate the player

        Chain.noCtx().delay(4, () -> {
            player.stopActions(true);

            Entity lastAttacker = player.getAttrib(AttributeKey.LAST_DAMAGER);

            //Handle player dying to a bot.
            if (lastAttacker != null && lastAttacker.isNpc() && lastAttacker.getAsNpc().getBotHandler() != null) {
                NPC bot = (NPC) lastAttacker;
                bot.stopActions(true);
                int botDeaths = player.getAttribOr(AttributeKey.BOT_DEATHS, 0);
                botDeaths++;
                player.putAttrib(AttributeKey.BOT_DEATHS, botDeaths);
                player.message("You now have " + botDeaths + " bot " + Utils.pluralOrNot("death", botDeaths) + ".");
                DailyTaskManager.increase(DailyTasks.BOTS, player);
            }

            NPC barrowsBro = player.getAttribOr(barrowsBroSpawned, null);
            if (barrowsBro != null) {
                World.getWorld().unregisterNpc(barrowsBro);
            }

            //BH death logic
            // if (killer != null && killer.isPlayer()) {
            //    BountyHunter.onDeath(killer, player);
            // }

            if (killer != null && player.getController() != null) {
                player.getController().defeated(killer, player);
            }

            player.clearAttrib(AttributeKey.LASTDEATH_VALUE);
            try {
                ItemsOnDeath.droplootToKiller(player, killer);

                mostdmg.ifPresent(value -> LootKey.handleDeath(player, value));
            } catch (Exception e) {
                logger.error("Error dropping items and loot!", e);
            }

            player.clearAttrib(AttributeKey.TARGET); // Clear last attacked or interacted.

            // Close open interface. do this BEFORE MINIGAME HANDLING -> such as arena deaths.
            player.stopActions(true);

            var duel_arena = player.getDueling().inDuel();
            /**
             * Custom death handling.
             */
            if (player.getMinigame() != null) {
                if (killer != null && killer.getMinigame() != null) {
                    killer.getMinigame().killed(killer, player);
                }
                player.getMinigame().end(player);
            } else if (duel_arena) {
                player.getDueling().onDeath();
            } else if (player.<Integer>getAttribOr(AttributeKey.JAILED, 0) == 1) {
                player.message("You've died, but you cannot run from your jail sentence!");
                player.teleport(player.tile());
            } else if (in_tournament) {
                TournamentManager.handleDeath(player);
            } else if (player.getRaids() != null) {
                player.getRaids().death(player);
            } else {
                player.teleport(GameServer.properties().defaultTile.tile()); //Teleport the player to Varrock square
            }
            player.message("Oh dear, you are dead!"); //Send the death message
            deathReset(player);
        });
    }

    public static void deathReset(Player player) {

        /**
         * All actual death handling is done here.
         */
        if (player.<Boolean>getAttribOr(HP_EVENT_ACTIVE, false)) {
            player.clearAttrib(HP_EVENT_ACTIVE);
            World.getWorld().clearBroadcast();
        }

        player.putAttrib(AttributeKey.DEATH_TELEPORT_TIMER, String.valueOf(System.currentTimeMillis()));

        //Remove auto-select
        Autocasting.setAutocast(player, null); // Set auto-cast to default; 0
        player.getCombat().setPoweredStaffSpell(null);
        WeaponInterfaces.updateWeaponInterface(player); //Update the weapon interface
        player.getCombat().setRangedWeapon(null);

        //Reset some values
        player.getSkills().resetStats(); //Reset all players stats
        Poison.cure(player); //Cure the player from any poisons
        player.getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
        player.getTimers().cancel(TimerKey.STUNNED); //Remove stunned timer key
        player.getTimers().cancel(TimerKey.TELEBLOCK); //Remove teleblock timer key
        player.getTimers().cancel(TimerKey.TELEBLOCK_IMMUNITY); //Remove the teleblock immunity timer key
        player.getTimers().cancel(TimerKey.FREEZE_IMMUNITY);
        player.clearAttrib(VETION_ENTRY_FEE);
        if (!(WildernessArea.wildernessLevel(player.tile()) <= 7) && !player.getTimers().has(TimerKey.RECHARGE_SPECIAL_ATTACK)) {
            player.restoreSpecialAttack(100); //Set energy to 100%
            player.getTimers().register(TimerKey.RECHARGE_SPECIAL_ATTACK, 150); //Set the value of the timer. Currently 1:30m
        }
        player.setSpecialActivated(false); //Disable special attack
        player.restoreSpecialAttack(100); //Restore spec
        player.getTimers().cancel(TimerKey.COMBAT_LOGOUT); //Remove combat logout timer key

        //Remove timers
        player.getPacketSender().sendEffectTimer(0, EffectTimer.FREEZE);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.TELEBLOCK);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.VENGEANCE);

        player.getPacketSender().sendEffectTimer(0, EffectTimer.ANTIFIRE);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.VENOM);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.STAMINA);

        // Fact: forfeit and death in the duel arena doesn't reset skull related stuff.
        if (!player.getDueling().inDuel()) {
            Skulling.unskull(player);
        }

        player.getCombat().clearDamagers(); //Clear damagers
        player.setEntityInteraction(null); // Reset entity facing
        Prayers.closeAllPrayers(player); //Disable all prayers
        player.getPacketSender().sendInteractionOption("null", 2, false); //Remove the player attack option
        player.setRunningEnergy(100.0, true); //Set the players run energy to 100
        player.graphic(-1); //Set player graphics to -1
        player.hp(100, 0); //Set hitpoints to 100%
        player.animate(-1);  //Set player animation to -1
        player.getTimers().cancel(TimerKey.CHARGE_SPELL); //Removes the spell charge timer from the player
        player.putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, false); //Let our players use melee again! : )
        player.clearAttrib(AttributeKey.VENOM_TICKS);
        player.clearAttrib(VENOMED_BY);
        player.looks().hide(false);

        player.getUpdateFlag().flag(Flag.APPEARANCE); //Update the players looks
        player.unlock(); //Unlock the player
        player.getMovementQueue().setBlockMovement(false); //Incase the player movement was locked elsewhere unlock it on death.
        //Open presets when dieing if enabled

        //Auto repair broken items if enabled
        var autoRepairOnDeath = player.<Boolean>getAttribOr(AttributeKey.REPAIR_BROKEN_ITEMS_ON_DEATH, false);
        if (autoRepairOnDeath) {
            BreakItemsOnDeath.repair(player);
        }

    }

    public static final Area VERZIK_AREA = new Area(Tile.regionToTile(12611).getX(), Tile.regionToTile(12611).getY(), Tile.regionToTile(12611).getX() + 63, Tile.regionToTile(12611).getY() + 63);
    public static final Area BLOAT_AREA = new Area(Tile.regionToTile(13125).getX(), Tile.regionToTile(13125).getY(), Tile.regionToTile(13125).getX() + 63, Tile.regionToTile(13125).getY() + 63);
    public static final Area MAIDEN_AREA = new Area(Tile.regionToTile(12613).getX(), Tile.regionToTile(12613).getY(), Tile.regionToTile(12613).getX() + 63, Tile.regionToTile(12613).getY() + 63);
    public static final Area XARPUS_AREA = new Area(Tile.regionToTile(12612).getX(), Tile.regionToTile(12612).getY(), Tile.regionToTile(12612).getX() + 63, Tile.regionToTile(12612).getY() + 63);
    public static final Area SOTETSEG_AREA = new Area(Tile.regionToTile(13123).getX(), Tile.regionToTile(13123).getY(), Tile.regionToTile(13123).getX() + 63, Tile.regionToTile(13123).getY() + 63);
    public static final Area VASILIAS_AREA = new Area(Tile.regionToTile(13122).getX(), Tile.regionToTile(13122).getY(), Tile.regionToTile(13122).getX() + 63, Tile.regionToTile(13122).getY() + 63);
    public final Tile[] VERZIK_DEATH_TILES = new Tile[]
        {
            new Tile(3161, 4325),
            new Tile(3159, 4325),
            new Tile(3175, 4325),
            new Tile(3177, 4325),
            new Tile(3157, 4325)

        };
    public final Tile[] VASILIAS_DEATH_TILES = new Tile[]
        {
            new Tile(3290, 4257),
            new Tile(3287, 4254),
            new Tile(3287, 4243),
            new Tile(3304, 4243),
            new Tile(3304, 4254)

        };
    public static List<Player> occupiedList = new ArrayList<>();
    BooleanSupplier wiped = () -> occupiedList.size() == player.getTheatreParty().getParty().size();
    BooleanSupplier completed = () -> player.getRoomState().equals(RoomState.COMPLETE);
    public void handleOnDeath(Player player) {
        Autocasting.setAutocast(player, null); // Set auto-cast to default; 0
        player.getCombat().setPoweredStaffSpell(null);
        WeaponInterfaces.updateWeaponInterface(player); //Update the weapon interface
        player.getCombat().setRangedWeapon(null);
        player.getSkills().resetStats(); //Reset all players stats
        Poison.cure(player); //Cure the player from any poisons
        player.getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
        player.getTimers().cancel(TimerKey.STUNNED); //Remove stunned timer key
        player.getTimers().cancel(TimerKey.TELEBLOCK); //Remove teleblock timer key
        player.getTimers().cancel(TimerKey.TELEBLOCK_IMMUNITY); //Remove the teleblock immunity timer key
        player.getTimers().cancel(TimerKey.FREEZE_IMMUNITY);
        player.setSpecialActivated(false); //Disable special attack
        player.restoreSpecialAttack(100); //Restore spec
        player.getTimers().cancel(TimerKey.COMBAT_LOGOUT); //Remove combat logout timer key
        player.getPacketSender().sendEffectTimer(0, EffectTimer.FREEZE);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.TELEBLOCK);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.VENGEANCE);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.ANTIFIRE);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.VENOM);
        player.getPacketSender().sendEffectTimer(0, EffectTimer.STAMINA);
        player.getCombat().clearDamagers(); //Clear damagers
        player.setEntityInteraction(null); // Reset entity facing
        Prayers.closeAllPrayers(player); //Disable all prayers
        player.getPacketSender().sendInteractionOption("null", 2, false); //Remove the player attack option
        player.setRunningEnergy(100.0, true); //Set the players run energy to 100
        player.graphic(-1); //Set player graphics to -1
        player.hp(100, 0); //Set hitpoints to 100%
        player.animate(-1);  //Set player animation to -1
        player.getTimers().cancel(TimerKey.CHARGE_SPELL); //Removes the spell charge timer from the player
        player.putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, false); //Let our players use melee again! : )
        player.clearAttrib(AttributeKey.VENOM_TICKS);
        player.clearAttrib(VENOMED_BY);
        player.looks().hide(false);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
        handleWipe.run();
        handleCompleted.run();
    }

    Runnable handleWipe = () -> Chain.noCtx().cancelWhen(completed).waitUntil(1, wiped, () -> {
        occupiedList.clear();
        player.setRaidDeathState(RaidDeathState.WIPE);
        player.unlock();
        player.getTheatre().dispose();
        if (player.getIronManStatus().isHardcoreIronman()) {
            player.setIronmanStatus(IronMode.REGULAR);
            player.getPacketSender().sendRights();
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            World.getWorld().sendBroadcast("<img=504>" + player.getDisplayName() + " has lost their hardcore ironman status! Total Level: " + player.getSkills().totalLevel());
        }
        player.message("Oh dear you are dead!");
    });

    Runnable handleCompleted = () -> player.waitUntil(completed, () -> {
        occupiedList.clear();
        player.unlock();
        player.setRaidDeathState(RaidDeathState.ALIVE);
        if (player.tile().inArea(VERZIK_AREA)) {
            player.teleport(3168, 4315, player.getTheatre().theatreArea.getzLevel());
        } else if (player.tile().inArea(BLOAT_AREA)) {
            player.teleport(3282, 4447, player.getTheatre().theatreArea.getzLevel());
        } else if (player.tile().inArea(MAIDEN_AREA)) {
            player.teleport(3180, 4447, player.getTheatre().theatreArea.getzLevel());
        } else if (player.tile().inArea(XARPUS_AREA)) {
            player.teleport(3170, 4389, player.getTheatre().theatreArea.getzLevel());
        } else if (player.tile().inArea(VASILIAS_AREA)) {
            player.teleport(3295, 4248, player.getTheatre().theatreArea.getzLevel());
        } else if (player.tile().inArea(SOTETSEG_AREA)) {
            player.teleport(3279, 4316, player.getTheatre().theatreArea.getzLevel());
        }
    }).cancelWhen(wiped);

    @Override
    public void handleRaidDeath(Player player) { //TODO reset on server restart or crash
        player.setRaidDeathState(RaidDeathState.DEAD);

        player.lock();

        player.stopActions(true);

        player.action.reset();

        if (player.tile().inArea(VERZIK_AREA)) {
            occupiedList.add(player);

            Tile targetTile = null;
            for (var t : VERZIK_DEATH_TILES) {
                if (!player.tile().equals(t)) {
                    targetTile = t.transform(0,0,player.theatre.theatreArea.getzLevel());
                    break;
                }
            }

            for (var p : occupiedList) {
                if (!p.tile().equals(targetTile)) {
                    p.teleport(targetTile);
                }
            }
            handleOnDeath(player);
        } else if (player.tile().inArea(BLOAT_AREA)) {
            occupiedList.add(player);
            player.teleport(3295, 4459, player.theatre.theatreArea.getzLevel());
            handleOnDeath(player);
        } else if (player.tile().inArea(MAIDEN_AREA)) {
            occupiedList.add(player);
            player.teleport(3166, 4433, player.theatre.theatreArea.getzLevel());
            handleOnDeath(player);
        } else if (player.tile().inArea(XARPUS_AREA)) {
            occupiedList.add(player);
            player.teleport(3157, 4387, player.theatre.theatreArea.getzLevel());
            handleOnDeath(player);
        } else if (player.tile().inArea(VASILIAS_AREA)) {
            occupiedList.add(player);

            Tile targetTile = null;
            for (var t : VASILIAS_DEATH_TILES) {
                if (!player.tile().equals(t)) {
                    targetTile = t;
                    break;
                }
            }

            for (var p : occupiedList) {
                if (!p.tile().equals(targetTile)) {
                    p.teleport(targetTile);
                }
            }
            handleOnDeath(player);
        } else if (player.tile().inArea(SOTETSEG_AREA)) {
            occupiedList.add(player);
            player.teleport(3270, 4313, player.theatre.theatreArea.getzLevel());
            handleOnDeath(player);
        }
    }

    @Override
    public boolean inRaid(Player player) {
        return player.getTheatreState().equals(TheatreState.ACTIVE);
    }
}
