package com.aelous.model.entity.npc;

import com.aelous.GameServer;
import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.areas.burthope.warriors_guild.MagicalAnimator;
import com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.skill.impl.prayer.Bone;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.content.skill.impl.slayer.slayer_partner.SlayerPartner;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.content.treasure.TreasureRewardCaskets;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.kalphite.KalphiteQueenFirstForm;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.kalphite.KalphiteQueenSecondForm;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion.VetionMinion;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah.Zulrah;
import com.aelous.model.entity.combat.method.impl.npcs.fightcaves.TzTokJad;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.aelous.model.entity.combat.method.impl.npcs.hydra.AlchemicalHydra;
import com.aelous.model.entity.combat.method.impl.npcs.karuulm.Drake;
import com.aelous.model.entity.combat.method.impl.npcs.karuulm.Wyrm;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.Gargoyle;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.Nechryael;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch.NechryarchDeathSpawn;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;

import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.*;
import com.aelous.utility.chainedwork.Chain;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.aelous.model.content.collection_logs.CollectionLog.RAIDS_KEY;
import static com.aelous.model.content.collection_logs.LogType.BOSSES;
import static com.aelous.model.content.collection_logs.LogType.OTHER;
import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.CustomNpcIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * Represents an npc's death task, which handles everything
 * an npc does before and after their death animation (including it),
 * such as dropping their drop table items.
 *
 * @author relex lawl
 * @author Created by Bart on 10/6/2015.
 */
public class NPCDeath {

    private static final Logger logger = LogManager.getLogger(NPCDeath.class);
    private static final Logger npcDropLogs = LogManager.getLogger("NpcDropLogs");
    private static final Level NPC_DROPS;

    static {
        NPC_DROPS = Level.getLevel("NPC_DROPS");
    }

    private static final List<Integer> customDrops = Arrays.asList(WHIRLPOOL_496, KRAKEN, CAVE_KRAKEN, WHIRLPOOL, ZULRAH, ZULRAH_2043, ZULRAH_2044);

    public static void execute(NPC npc) {
        // Path reset instantly when hitsplat appears killing the npc.
        var respawnTimer = Utils.secondsToTicks(45);// default 45 seconds
        NpcDefinition def = World.getWorld().definitions().get(NpcDefinition.class, npc.id());
        if(def != null) {
            if(def.combatlevel >= 1 && def.combatlevel <= 50) {
                respawnTimer = Utils.secondsToTicks(30);//30 seconds
            } else if(def.combatlevel >= 51 && def.combatlevel <= 150) {
                respawnTimer = Utils.secondsToTicks(25);//25 seconds
            } else {
                respawnTimer = Utils.secondsToTicks(20);// 20 seconds
            }
        }

        npc.getMovementQueue().clear();
        npc.lockNoDamage();

        // Reset interacting entity..
        npc.setEntityInteraction(null);

        Optional<Player> killer_id = npc.getCombat().getKiller();

        // Player that did the most damage.
        Player killer = killer_id.orElse(null);

        if (killer != null) {
            var biggest_and_baddest_perk = killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.BIGGEST_AND_BADDEST) && Slayer.creatureMatches(killer, npc.id());
            var ancientRevSpawnRoll = 25;
            var superiorSpawnRoll = biggest_and_baddest_perk ? 4 : 6;

            var legendaryInsideCave = killer.tile().memberCave() && killer.getMemberRights().isLegendaryMemberOrGreater(killer);
            var VIPInsideCave = killer.tile().memberCave() && killer.getMemberRights().isLegendaryMemberOrGreater(killer);
            var SponsorInsideCave = killer.tile().memberCave() && killer.getMemberRights().isLegendaryMemberOrGreater(killer);
            if(legendaryInsideCave)
                respawnTimer = 34;
            if(VIPInsideCave)
                respawnTimer = 30;
            if(SponsorInsideCave)
                respawnTimer = 25;

            killer.getCombat().reset();

            // Increment kill.
            killer.getSlayerKillLog().addKill(npc);
            if (!npc.isWorldBoss() || npc.id() != THE_NIGHTMARE_9430 || npc.id() != KALPHITE_QUEEN_6500) {
                killer.getBossKillLog().addKill(npc);
            }

            if (npc.def().name.equalsIgnoreCase("Yak")) {
                AchievementsManager.activate(killer, Achievements.YAK_HUNTER, 1);
            }

            if (npc.def().name.equalsIgnoreCase("Rock Crab")) {
                AchievementsManager.activate(killer, Achievements.ROCK_CRAB_HUNTER, 1);
            }

            if (npc.def().name.equalsIgnoreCase("Sand Crab")) {
                AchievementsManager.activate(killer, Achievements.SAND_CRAB_HUNTER, 1);
            }

            if (npc.def().name.equalsIgnoreCase("Experiment")) {
                AchievementsManager.activate(killer, Achievements.EXPERIMENTS_HUNTER, 1);
            }

            if (npc.def().name.equalsIgnoreCase("Adamant dragon")) {
                var kc = killer.<Integer>getAttribOr(ADAMANT_DRAGONS_KILLED, 0) + 1;
                killer.putAttrib(ADAMANT_DRAGONS_KILLED, kc);
            }

            if (npc.def().name.equalsIgnoreCase("Rune dragon")) {
                var kc = killer.<Integer>getAttribOr(RUNE_DRAGONS_KILLED, 0) + 1;
                killer.putAttrib(RUNE_DRAGONS_KILLED, kc);
            }

            if (npc.def().name.equalsIgnoreCase("Lava dragon")) {
                var kc = killer.<Integer>getAttribOr(LAVA_DRAGONS_KILLED, 0) + 1;
                killer.putAttrib(LAVA_DRAGONS_KILLED, kc);
            }

            if (npc.def().name.contains("dragon") || npc.def().name.contains("Dragon")) {
                AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_I, 1);
                killer.getTaskMasterManager().increase(Tasks.DRAGONS);
            }

            if (npc.def().name.contains("Black dragon") || npc.def().name.contains("black dragon")) {
                AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_II, 1);
            }

            if (npc.def().name.equalsIgnoreCase("K'ril Tsutsaroth") || npc.def().name.equalsIgnoreCase("General Graardor") || npc.def().name.equalsIgnoreCase("Commander Zilyana") || npc.def().name.equalsIgnoreCase("Kree'arra")) {
                AchievementsManager.activate(killer, Achievements.GODWAR, 1);
            }

            if (npc.def().name.contains("Revenant") || npc.def().name.contains("revenant")) {
                AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_I, 1);
                AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_II, 1);
                AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_III, 1);
                AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_IV, 1);
                killer.getTaskMasterManager().increase(Tasks.REVENANTS);
                DailyTaskManager.increase(DailyTasks.REVENANTS, killer);
            }

            if (npc.def().name.equalsIgnoreCase("Alchemical Hydra")) {
                killer.getTaskMasterManager().increase(Tasks.ALCHEMICAL_HYDRA);
            }

            if (npc.def().name.equalsIgnoreCase("Chaos Fanatic")) {
                killer.getTaskMasterManager().increase(Tasks.CHAOS_FANATIC);
            }

            if (npc.def().name.equalsIgnoreCase("Corporeal Beast")) {
                AchievementsManager.activate(killer, Achievements.CORPOREAL_CRITTER, 1);
                DailyTaskManager.increase(DailyTasks.CORPOREAL_BEAST, killer);
                killer.getTaskMasterManager().increase(Tasks.CORPOREAL_BEAST);
            }

            if (npc.def().name.equalsIgnoreCase("Crazy archaeologist")) {
                killer.getTaskMasterManager().increase(Tasks.CRAZY_ARCHAEOLOGIST);
            }

            if (npc.def().name.equalsIgnoreCase("Demonic gorilla")) {
                killer.getTaskMasterManager().increase(Tasks.DEMONIC_GORILLA);
            }

            if (npc.def().name.equalsIgnoreCase("King Black Dragon")) {
                AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_II, 1);
                AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_III, 1);
                killer.getTaskMasterManager().increase(Tasks.KING_BLACK_DRAGON);

                if (World.getWorld().rollDie(10, 1)) {
                    npc.respawns(false);//King black dragon can no longer spawn his ancient version spawns.
                    var ancientKingBlackDragon = new NPC(ANCIENT_KING_BLACK_DRAGON, npc.spawnTile()).respawns(false);
                    World.getWorld().getNpcs().add(ancientKingBlackDragon);
                }
            }

            if (npc.id() == ANCIENT_KING_BLACK_DRAGON) {
                AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_II, 1);
                AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_III, 1);
                killer.getTaskMasterManager().increase(Tasks.KING_BLACK_DRAGON);
                DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);
                if(!npc.ancientSpawn()) {
                    Chain.bound(null).runFn(30, () -> {
                        var kingBlackDragon = new NPC(KING_BLACK_DRAGON, npc.spawnTile());
                        World.getWorld().getNpcs().add(kingBlackDragon);
                    });
                }
            }

            if (npc.def().name.equalsIgnoreCase("Lizardman shaman")) {
                killer.getTaskMasterManager().increase(Tasks.LIZARDMAN_SHAMAN);
            }

            if (npc.def().name.equalsIgnoreCase("Thermonuclear smoke devil")) {
                killer.getTaskMasterManager().increase(Tasks.THERMONUCLEAR_SMOKE_DEVIL);
            }

            if (npc.def().name.equalsIgnoreCase("Vet'ion")) {
                killer.getTaskMasterManager().increase(Tasks.VETION);
            }

            if (npc.def().name.equalsIgnoreCase("Chaos Elemental")) {
                killer.getTaskMasterManager().increase(Tasks.CHAOS_ELEMENTAL);
                AchievementsManager.activate(killer, Achievements.ULTIMATE_CHAOS_I, 1);
                AchievementsManager.activate(killer, Achievements.ULTIMATE_CHAOS_II, 1);
                AchievementsManager.activate(killer, Achievements.ULTIMATE_CHAOS_III, 1);
                DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);

                if (World.getWorld().rollDie(10, 1)) {
                    npc.respawns(false);//Chaos elemental can no longer spawn his ancient version spawns.
                    var ancientChaosEle = new NPC(ANCIENT_CHAOS_ELEMENTAL, npc.spawnTile()).respawns(false);
                    World.getWorld().getNpcs().add(ancientChaosEle);
                }
            }

            if (npc.def().name.contains("Zulrah")) {
                killer.getTaskMasterManager().increase(Tasks.ZULRAH);
                DailyTaskManager.increase(DailyTasks.ZULRAH, killer);
            }

            if (npc.def().name.equalsIgnoreCase("Vorkath")) {
                killer.getTaskMasterManager().increase(Tasks.VORKATH);
                DailyTaskManager.increase(DailyTasks.VORKATH, killer);
            }

            if (npc.def().name.equalsIgnoreCase("Brutal lava dragon") || npc.def().name.equalsIgnoreCase("Skotizo") || npc.def().name.equalsIgnoreCase("Corrupted nechryarch")) {
                killer.getTaskMasterManager().increase(Tasks.WORLD_BOSS);
            }

            if (npc.def().name.equalsIgnoreCase("Kalphite Queen")) {
                killer.getTaskMasterManager().increase(Tasks.KALPHITE_QUEEN);
            }

            if (npc.def().name.equalsIgnoreCase("Dagannoth Supreme") || npc.def().name.equalsIgnoreCase("Dagannoth Prime") || npc.def().name.equalsIgnoreCase("Dagannoth Rex")) {
                AchievementsManager.activate(killer, Achievements.LORD_OF_THE_RINGS_I, 1);
                AchievementsManager.activate(killer, Achievements.LORD_OF_THE_RINGS_II, 1);
                killer.getTaskMasterManager().increase(Tasks.DAGANNOTH_KINGS);
            }

            if (npc.def().name.equalsIgnoreCase("Giant Mole")) {
                AchievementsManager.activate(killer, Achievements.HOLEY_MOLEY_I, 1);
                AchievementsManager.activate(killer, Achievements.HOLEY_MOLEY_II, 1);
                AchievementsManager.activate(killer, Achievements.HOLEY_MOLEY_III, 1);
                killer.getTaskMasterManager().increase(Tasks.GIANT_MOLE);
            }

            if (npc.def().name.equalsIgnoreCase("Barrelchest")) {
                DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);

                if (World.getWorld().rollDie(10, 1)) {
                    npc.respawns(false);//Barrelchest can no longer spawn his ancient version spawns.
                    var ancientBarrelchest = new NPC(ANCIENT_BARRELCHEST, npc.spawnTile()).respawns(false);
                    World.getWorld().getNpcs().add(ancientBarrelchest);
                }
            }

            Slayer.reward(killer, npc);
            SlayerPartner.reward(killer, npc);

            if (killer.getMinigame() != null) {
                killer.getMinigame().killed(killer, npc);
            }

            // Check if the dead npc is a barrows brother. Award killcount.
            var isBarrowsBro = false;

            switch (npc.id()) {

                case DHAROK_THE_WRETCHED -> {
                    isBarrowsBro = true;
                    killer.putAttrib(DHAROK, 1);
                }
                case AHRIM_THE_BLIGHTED -> {
                    isBarrowsBro = true;
                    killer.putAttrib(AHRIM, 1);
                }
                case VERAC_THE_DEFILED -> {
                    isBarrowsBro = true;
                    killer.putAttrib(VERAC, 1);
                }
                case TORAG_THE_CORRUPTED -> {
                    isBarrowsBro = true;
                    killer.putAttrib(TORAG, 1);
                }
                case KARIL_THE_TAINTED -> {
                    isBarrowsBro = true;
                    killer.putAttrib(KARIL, 1);
                }
                case GUTHAN_THE_INFESTED -> {
                    isBarrowsBro = true;
                    killer.putAttrib(GUTHAN, 1);
                }

                case KrakenBoss.KRAKEN_NPCID -> {// Kraken boss transmogged KC
                    AchievementsManager.activate(killer, Achievements.SQUIDWARD_I, 1);
                    AchievementsManager.activate(killer, Achievements.SQUIDWARD_II, 1);
                    AchievementsManager.activate(killer, Achievements.SQUIDWARD_III, 1);
                    killer.getTaskMasterManager().increase(Tasks.KRAKEN);
                }

                case CORRUPTED_NECHRYARCH -> {
                    DailyTaskManager.increase(DailyTasks.CORRUPTED_NECHRYARCHS, killer);
                }

                case ADAMANT_DRAGON, ADAMANT_DRAGON_8090, RUNE_DRAGON, RUNE_DRAGON_8031, RUNE_DRAGON_8091 -> AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_IV, 1);

                case CERBERUS, CERBERUS_5863, CERBERUS_5866 -> {
                    killer.getTaskMasterManager().increase(Tasks.CERBERUS);
                    AchievementsManager.activate(killer, Achievements.FLUFFY_I, 1);
                    AchievementsManager.activate(killer, Achievements.FLUFFY_II, 1);

                    if (World.getWorld().rollDie(superiorSpawnRoll, 1)) {
                        npc.respawns(false);//Cerberus can no longer spawn his superior spawns in 1 minute.
                        var kerberos = new NPC(KERBEROS, npc.spawnTile()).respawns(false);
                        World.getWorld().getNpcs().add(kerberos);
                    }
                }

                case KERBEROS -> Chain.bound(null).runFn(30, () -> {
                    var cerberus = new NPC(CERBERUS, npc.spawnTile());
                    World.getWorld().getNpcs().add(cerberus);
                });

                case KALPHITE_QUEEN_6501 -> {
                    AchievementsManager.activate(killer, Achievements.BUG_EXTERMINATOR_I, 1);
                    AchievementsManager.activate(killer, Achievements.BUG_EXTERMINATOR_II, 1);
                }

                case LIZARDMAN_SHAMAN_6767 -> {
                    AchievementsManager.activate(killer, Achievements.DR_CURT_CONNORS_I, 1);
                    AchievementsManager.activate(killer, Achievements.DR_CURT_CONNORS_II, 1);
                    AchievementsManager.activate(killer, Achievements.DR_CURT_CONNORS_III, 1);
                }

                case THERMONUCLEAR_SMOKE_DEVIL -> {
                    AchievementsManager.activate(killer, Achievements.TSJERNOBYL_I, 1);
                    AchievementsManager.activate(killer, Achievements.TSJERNOBYL_II, 1);
                    AchievementsManager.activate(killer, Achievements.TSJERNOBYL_III, 1);
                }

                case VETION, VETION_REBORN -> {
                    AchievementsManager.activate(killer, Achievements.VETION_I, 1);
                    AchievementsManager.activate(killer, Achievements.VETION_II, 1);
                    AchievementsManager.activate(killer, Achievements.VETION_III, 1);
                    DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);
                }

                case VENENATIS_6610 -> {
                    killer.getTaskMasterManager().increase(Tasks.VENENATIS);
                    AchievementsManager.activate(killer, Achievements.BABY_ARAGOG_I, 1);
                    AchievementsManager.activate(killer, Achievements.BABY_ARAGOG_II, 1);
                    AchievementsManager.activate(killer, Achievements.BABY_ARAGOG_III, 1);
                    DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);

                    if (World.getWorld().rollDie(superiorSpawnRoll, 1)) {
                        npc.respawns(false);//Venenatis can no longer spawn his superior spawns in 1 minute.
                        var arachne = new NPC(CustomNpcIdentifiers.ARACHNE, npc.spawnTile()).respawns(false);
                        World.getWorld().getNpcs().add(arachne);
                    }
                }

                case CALLISTO_6609 -> {
                    killer.getTaskMasterManager().increase(Tasks.CALLISTO);
                    AchievementsManager.activate(killer, Achievements.BEAR_GRYLLS_I, 1);
                    AchievementsManager.activate(killer, Achievements.BEAR_GRYLLS_II, 1);
                    AchievementsManager.activate(killer, Achievements.BEAR_GRYLLS_III, 1);
                    DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);
                }

                case ZULRAH, ZULRAH_2043, ZULRAH_2044 -> {
                    AchievementsManager.activate(killer, Achievements.SNAKE_CHARMER_I, 1);
                    AchievementsManager.activate(killer, Achievements.SNAKE_CHARMER_II, 1);
                    AchievementsManager.activate(killer, Achievements.SNAKE_CHARMER_III, 1);
                }

                case VORKATH_8061 -> {
                    AchievementsManager.activate(killer, Achievements.VORKY_I, 1);
                    AchievementsManager.activate(killer, Achievements.VORKY_II, 1);
                }

                case BATTLE_MAGE, BATTLE_MAGE_1611, BATTLE_MAGE_1612 -> {
                    AchievementsManager.activate(killer, Achievements.MAGE_ARENA_I, 1);
                    AchievementsManager.activate(killer, Achievements.MAGE_ARENA_II, 1);
                    AchievementsManager.activate(killer, Achievements.MAGE_ARENA_III, 1);
                    AchievementsManager.activate(killer, Achievements.MAGE_ARENA_IV, 1);
                    DailyTaskManager.increase(DailyTasks.BATTLE_MAGE, killer);
                }

            }

            if (isBarrowsBro) {
                killer.clearAttrib(barrowsBroSpawned);
                killer.putAttrib(BARROWS_MONSTER_KC, 1 + (int) killer.getAttribOr(BARROWS_MONSTER_KC, 0));
                var newkc = killer.getAttribOr(BARROWS_MONSTER_KC, 0);
                killer.getPacketSender().sendString(4536, "Kill Count: " + newkc);
                killer.getPacketSender().sendEntityHintRemoval(false);
            }

            //Make sure spawns are killed on boss death
            if (npc.id() == SCORPIA) {
                killer.getTaskMasterManager().increase(Tasks.SCORPIA);
                npc.clearAttrib(AttributeKey.SCORPIA_GUARDIANS_SPAWNED);
                AchievementsManager.activate(killer, Achievements.BARK_SCORPION_I, 1);
                AchievementsManager.activate(killer, Achievements.BARK_SCORPION_II, 1);
                AchievementsManager.activate(killer, Achievements.BARK_SCORPION_III, 1);
                DailyTaskManager.increase(DailyTasks.WILDERNESS_BOSS, killer);
                World.getWorld().getNpcs().forEachInArea(new Area(3219, 3248, 10329, 10353), n -> {
                    if (n.id() == SCORPIAS_GUARDIAN) {
                        World.getWorld().unregisterNpc(n);
                    }
                });

                if (World.getWorld().rollDie(superiorSpawnRoll, 1)) {
                    npc.respawns(false);//Cerberus can no longer spawn his superior spawns in 1 minute.
                    var skorpios = new NPC(SKORPIOS, npc.spawnTile()).respawns(false);
                    World.getWorld().getNpcs().add(skorpios);
                }
            }

            if (npc.id() == SKORPIOS) {
                World.getWorld().getNpcs().forEachInArea(new Area(3219, 3248, 10329, 10353), n -> {
                    if (n.id() == SCORPIAS_GUARDIAN) {
                        World.getWorld().unregisterNpc(n);
                    }
                });

                Chain.bound(null).runFn(30, () -> {
                    var scorpia = new NPC(SCORPIA, npc.spawnTile());
                    World.getWorld().getNpcs().add(scorpia);
                });
            }

            //Do custom area deaths
            if (killer.getController() != null) {
                killer.getController().defeated(killer, npc);
            }

            //Do bots death
            if (npc.getBotHandler() != null) {
                npc.getBotHandler().onDeath(killer);
            }

            var killerOpp = killer.<Entity>getAttribOr(AttributeKey.LAST_DAMAGER, null);
            if (killer.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 0 && killerOpp != null && killerOpp == npc) { // Last fighting with this dead npc.
                killer.clearAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME); // Allow instant aggro from other npcs/players.
            }

            var done = false;
            for (MagicalAnimator.ArmourSets set : MagicalAnimator.ArmourSets.values()) {
                if (!done && set.npc == npc.id()) {
                    done = true;
                    killer.getPacketSender().sendEntityHintRemoval(true);// remove hint arrow
                }
            }
        }

        //Do death animation
        if (npc instanceof AlchemicalHydra) {
            npc.animate(8257);
            Chain.bound(null).runFn(2, () -> {
                npc.transmog(8622);
                npc.animate(8258);
            });
        } else if (npc instanceof Drake) {
            npc.animate(8277);
            Chain.bound(null).runFn(1, () -> {
                npc.transmog(8613);
                npc.animate(8278);
            });
        } else if (npc instanceof TzTokJad) {
            npc.graphic(453);
        } else {
            npc.animate(npc.getCombatInfo() != null ? npc.getCombatInfo().animations.death : -1);
        }

        int finalRespawnTimer = respawnTimer;
        Chain.bound(null).runFn(npc.getCombatInfo() != null ? npc.getCombatInfo().deathlen : 5, () -> {
            if (killer != null) {
                //Do inferno minigame death here and fight caves

                //Do death scripts
                if (npc.id() == KRAKEN) {
                    KrakenBoss.onDeath(npc); //Kraken uses its own death script
                }

                if (npc.getCombatMethod() instanceof CommonCombatMethod) {
                    CommonCombatMethod commonCombatMethod = (CommonCombatMethod) npc.getCombatMethod();
                    commonCombatMethod.set(npc, killer);
                    commonCombatMethod.onDeath(killer, npc);
                }

                //Rock crabs
                if (npc.id() == 101 || npc.id() == 103) {
                    switch (npc.id()) {
                        case 101 -> npc.transmog(101);
                        case 103 -> npc.transmog(103);
                    }
                    npc.walkRadius(0);
                }

                // so in java .. we dont have functions so we need to hardcode the id check
                if (WildernessBossEvent.getINSTANCE().getActiveNpc().isPresent() &&
                    npc == WildernessBossEvent.getINSTANCE().getActiveNpc().get()) {
                    WildernessBossEvent.getINSTANCE().bossDeath(npc);
                }

                killer.getBossTimers().submit(npc.def().name, (int) killer.getCombat().getFightTimer().elapsed(TimeUnit.SECONDS), killer);

                ScalarLootTable table = null;// ScalarLootTable.forNPC(npc.id());
                //Drop loot, but the first form of KQ, Runite golem and world bosses do not drop anything.
                if (table != null && (npc.id() != KALPHITE_QUEEN_6500 && npc.id() != RUNITE_GOLEM && !npc.isWorldBoss() && npc.id() != THE_NIGHTMARE_9430)) {
                    boolean dropUnderPlayer = npc.id() == NpcIdentifiers.KRAKEN || npc.id() == NpcIdentifiers.CAVE_KRAKEN || npc.id() >= NpcIdentifiers.ZULRAH && npc.id() <= NpcIdentifiers.ZULRAH_2044 || npc.id() >= NpcIdentifiers.VORKATH_8059 && npc.id() <= NpcIdentifiers.VORKATH_8061 || npc.id() >= LAVA_BEAST;
                    boolean jad = npc.id() == TZTOKJAD;
                    boolean doubleDropsLampActive = (Integer) killer.getAttribOr(DOUBLE_DROP_LAMP_TICKS, 0) > 0;
                    boolean rolledDoubleDrop = World.getWorld().rollDie(10, 1);

                    Tile tile = jad ? new Tile(2438, 5169, 0) : dropUnderPlayer ? killer.tile() : npc.tile();

                    table.rollForLarransKey(npc, killer);

                    if (!customDrops.contains(npc.id())) {
                        table.getGuaranteedDrops().forEach(tableItem -> {
                            if (killer.inventory().contains(13116)) {
                                int[] BONES = new int[]{526, 528, 530, 2859, 532, 10976, 10977, 3125, 534, 536, 4812,
                                    4834, 6812, 6729, 11943};
                                for (int bone : BONES) {
                                    if (tableItem.convert().getId() == bone) {
                                        Bone bones = Bone.get(tableItem.convert().getId());
                                        if (bones != null)
                                            killer.getSkills().addXp(Skills.PRAYER, bones.xp);
                                    }
                                }
                            } else {
                                if (tableItem.min > 0) {
                                    // not fixed-amount drop, amount has a min/max amount randomly given
                                    Item dropped = new Item(tableItem.id, Utils.random(tableItem.min, tableItem.max));

                                    if ((dropped.getId() == ItemIdentifiers.DRAGON_BONES || dropped.getId() == ItemIdentifiers.LAVA_DRAGON_BONES && killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.NOTED_DRAGON_BONES)) && WildernessArea.inWilderness(killer.tile())) {
                                        dropped = dropped.note();
                                    }

                                    GroundItemHandler.createGroundItem(new GroundItem(dropped, tile, killer));

                                } else {
                                    // fixed amount items
                                    if ((tableItem.convert().getId() == ItemIdentifiers.DRAGON_BONES || tableItem.convert().getId() == ItemIdentifiers.LAVA_DRAGON_BONES && killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.NOTED_DRAGON_BONES)) && WildernessArea.inWilderness(killer.tile())) {
                                        tableItem.convert().setId(tableItem.convert().note().getId());
                                    }
                                    GroundItemHandler.createGroundItem(new GroundItem(tableItem.convert(), tile, killer));
                                }
                            }
                        });
                    }

                    int dropRolls = npc.getCombatInfo().droprolls;

                    if (killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.DOUBLE_DROP_CHANCE) && World.getWorld().rollDie(100, 1)) {
                        dropRolls += 1;
                        killer.message("The Double drops perk grants you a second drop!");
                    }

                    for (int i = 0; i < dropRolls; i++) {
                        Item reward = table.randomItem(World.getWorld().random());
                        if (reward != null) {
                            if (doubleDropsLampActive) {
                                if (rolledDoubleDrop) {
                                    //Drop the item to the ground instead of editing the item instance
                                    GroundItem doubleDrop = new GroundItem(reward, tile, killer);

                                    GroundItemHandler.createGroundItem(doubleDrop);

                                    killer.message("The double drop effect doubled your drop.");
                                }
                            }

                            // bosses, find npc ID, find item ID
                            BOSSES.log(killer, npc.id(), reward);
                            BOSSES.log(killer, RAIDS_KEY, reward);
                            OTHER.log(killer, npc.id(), reward);

                            if ((reward.getId() == ItemIdentifiers.DRAGON_BONES || reward.getId() == ItemIdentifiers.LAVA_DRAGON_BONES && killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.NOTED_DRAGON_BONES)) && WildernessArea.inWilderness(killer.tile())) {
                                reward = reward.note();
                            }

                            GroundItemHandler.createGroundItem(new GroundItem(reward, tile, killer));

                            if (WildernessArea.inWilderness(killer.tile())) {
                                killer.getWildernessSlayerCasket().rollForCasket(npc);
                                killer.getWildernessSlayerCasket().rollForSupplys(npc);
                            }

                            npcDropLogs.log(NPC_DROPS, "Player " + killer.getUsername() + " got drop item " + reward.unnote().name());
                            Utils.sendDiscordInfoLog("Player " + killer.getUsername() + " got drop item " + reward.unnote().name(), "npcdrops");

                            // Corp beast drops are displayed to surrounding players.
                            if (npc.id() == 319) {
                                Item finalReward = reward;
                                World.getWorld().getPlayers().forEachInArea(new Area(2944, 4352, 3007, 4415), p -> {
                                    String amtString = finalReward.unnote().getAmount() == 1 ? finalReward.unnote().name() : "" + finalReward.getAmount() + " x " + finalReward.unnote().getAmount() + ".";
                                    p.message("<col=0B610B>" + killer.getUsername() + " received a drop: " + amtString);
                                });
                            }
                        }
                    }

                    //Only give BM when the npc is flagged as boss and we have the perk unlocked
                    if (npc.getCombatInfo().boss && killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.BLOOD_MONEY_FROM_KILLING_BOSSES)) {
                        int combat = def.combatlevel;

                        var amount = 0;
                        if (combat > 200) {
                            amount = Utils.random(350, 750);
                        } else {
                            amount = Utils.random(125, 350);
                        }

                        Item BM = new Item(BLOOD_MONEY, amount);

                        GroundItemHandler.createGroundItem(new GroundItem(BM, tile, killer));

                    }

                    treasure(killer, npc, tile);
                }

                // Custom drop tables
                if (npc.getCombatInfo() != null && npc.getCombatInfo().scripts != null && npc.getCombatInfo().scripts.droptable_ != null) {
                    npc.getCombatInfo().scripts.droptable_.reward(npc, killer);
                }

            }

            // Post-death scripts

            if (npc.id() == KALPHITE_QUEEN_6500) {
                KalphiteQueenFirstForm.death(npc);
                return;
            } else if (npc.id() == KALPHITE_QUEEN_6501) {
                KalphiteQueenSecondForm.death(npc);
            }

            if (npc.id() == VETION_REBORN) {
                npc.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, false);
                npc.clearAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED);
                npc.transmog(VETION);
            }

            if (npc.id() == 6613) {
                VetionMinion.death(npc); //Do Vetíon minion death
            }

            if (npc.id() == 6716 || npc.id() == 6723 || npc.id() == 7649) {
                NechryarchDeathSpawn.death(npc); //Do death spawn death
            }

            if (npc.id() == NECHRYAEL || npc.id() == NECHRYAEL_11) {
                new Nechryael().onDeath(npc);
            }

            Zulrah.death(killer, npc);

            if (npc.id() == CORPOREAL_BEAST) { // Corp beast
                // Reset damage counter

                NPC corp = npc.getAttribOr(AttributeKey.BOSS_OWNER, null);
                if (corp != null) {
                    //Check for any minions.
                    List<NPC> minList = corp.getAttribOr(AttributeKey.MINION_LIST, null);
                    if (minList != null) {
                        minList.remove(npc);
                    }
                }
            }

            //Forgot to say its ALL npcs, happens to bots, kraken any npc
            if (killer != null) {
                if (npc.respawns() && !npc.isBot())
                    killer.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(finalRespawnTimer), EffectTimer.MONSTER_RESPAWN);
            }

            deathReset(npc);
            if (npc.respawns()) {
                npc.teleport(npc.spawnTile());
                npc.hidden(true);
                System.out.println("respawn in "+finalRespawnTimer);

                Chain.bound(null).runFn(finalRespawnTimer, () -> {
                    GwdLogic.onRespawn(npc);
                    respawn(npc);
                });
            } else if (unregisterOnDeath(npc.id())) {
                npc.hidden(true);
                World.getWorld().unregisterNpc(npc);
            }
        });
    }

    private static boolean unregisterOnDeath(int npcId) {
        if (npcId == KALPHITE_QUEEN_963 || npcId == GREAT_OLM_LEFT_CLAW_7555 || npcId == GREAT_OLM_RIGHT_CLAW_7553) {
            return false;
        }
        return true;
    }

    private static void treasure(Player killer, NPC npc, Tile tile) {
        if (!killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.TREASURE_HUNT)) {
            return;
        }

        if(!Slayer.creatureMatches(killer, npc.id())) {
            return;
        }

        int treasureCasketChance;
        if (killer.getMemberRights().isSponsorOrGreater(killer))
            treasureCasketChance = 95;
        else if (killer.getMemberRights().isVIPOrGreater(killer))
            treasureCasketChance = 100;
        else if (killer.getMemberRights().isLegendaryMemberOrGreater(killer))
            treasureCasketChance = 105;
        else if (killer.getMemberRights().isExtremeMemberOrGreater(killer))
            treasureCasketChance = 110;
        else if (killer.getMemberRights().isEliteMemberOrGreater(killer))
            treasureCasketChance = 115;
        else if (killer.getMemberRights().isSuperMemberOrGreater(killer))
            treasureCasketChance = 120;
        else if (killer.getMemberRights().isRegularMemberOrGreater(killer))
            treasureCasketChance = 125;
        else
            treasureCasketChance = 128;

        var reduction = treasureCasketChance * killer.masterCasketMemberBonus() / 100;
        treasureCasketChance -= reduction;

        if (World.getWorld().rollDie(killer.getPlayerRights().isDeveloper(killer) && !GameServer.properties().production ? 1 : treasureCasketChance, 1)) {
            Item clueItem = new Item(TreasureRewardCaskets.MASTER_CASKET);
            GroundItem groundItem = new GroundItem(clueItem, tile, killer);
            GroundItemHandler.createGroundItem(groundItem);
            notification(killer, clueItem);
            killer.message("<col=0B610B>You have received a treasure casket drop!");
        }

        boolean inWilderness = WildernessArea.inWilderness(killer.tile());
        Item smallCasket = new Item(ItemIdentifiers.CASKET_7956);
        int combat = killer.getSkills().combatLevel();
        int mul;

        if ((killer.getGameMode() == GameMode.TRAINED_ACCOUNT))
            mul = 2;
        else mul = 1;

        int chance;

        if (combat <= 10)
            chance = 1;
        else if (combat <= 20)
            chance = 2;
        else if (combat <= 80)
            chance = 3;
        else if (combat <= 120)
            chance = 4;
        else
            chance = 5;

        int regularOdds = 100;

        chance *= mul;

    }

    /**
     * If you're resetting an NPC as if it were by death but not, for example maybe kraken tentacles which go back down to
     * the depths when the boss is killed.
     */
    public static void deathReset(NPC npc) {
        if (npc.id() != KALPHITE_QUEEN_6500) { // KQ first stage keeps damage onto stage 2!
            npc.getCombat().clearDamagers(); //Clear damagers
        }

        npc.clearAttrib(AttributeKey.TARGET);
        npc.clearAttrib(AttributeKey.LAST_ATTACKED_MAP);
        npc.putAttrib(AttributeKey.VENOM_TICKS, 0);
        npc.putAttrib(AttributeKey.POISON_TICKS, 0);
        npc.clearAttrib(VENOMED_BY);
    }

    public static void respawn(NPC npc) {

        if (npc.id() == KrakenBoss.KRAKEN_NPCID) {
            npc.transmog(KrakenBoss.KRAKEN_WHIRLPOOL);
            // Transmog kraken info after the drop table is done otherwise it'll look for the wrong table
            npc.getCombatInfo(World.getWorld().combatInfo(KrakenBoss.KRAKEN_WHIRLPOOL));
        }

        if (npc.id() == KrakenBoss.TENTACLE_WHIRLPOOL || npc.id() == NpcIdentifiers.ENORMOUS_TENTACLE) {
            NPC boss = npc.getAttrib(AttributeKey.BOSS_OWNER);
            if (boss != null && npc.dead()) {
                // only respawn minions if our boss is alive
                return;
            }
        }

        if (npc.id() == NpcIdentifiers.GARGOYLE) {
            Gargoyle.onDeath(npc);
        }

        if (npc.id() == NpcIdentifiers.VETION) {//Just do it again for extra safety
            npc.clearAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED);
            npc.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, false);
        }

        if (npc.hidden()) { // not respawned yet. we do this check incase it was force-respawned by .. group spawning (gwd)
            deathReset(npc);
            npc.hidden(false);
            if (npc.getCombatInfo() != null) {
                if (npc.getCombatInfo().stats != null || npc.getCombatInfo().originalStats != null)
                    npc.getCombatInfo().stats = npc.getCombatInfo().originalStats.clone(); // Replenish all stats on this NPC.
                if (npc.getCombatInfo().bonuses != null || npc.getCombatInfo().originalBonuses != null)
                    npc.getCombatInfo().bonuses = npc.getCombatInfo().originalBonuses.clone(); // Replenish all stats on this NPC.
            }

            npc.hp(npc.maxHp(), 0); // Heal up to full hp
            npc.animate(-1); // Reset death animation
            npc.unlock();
            if (npc instanceof Drake) {
                npc.transmog(DRAKE_8612);
            }

            if (npc instanceof Wyrm) {
                npc.transmog(Wyrm.IDLE);
            }
        }
    }

    public static void notification(Player killer, Item drop) {
        Item loot = drop.unnote();
        //TODO: implement these
        // Enabled? Untradable buttons are only enabled if the threshold is enabled. Can't have one without the other.
        boolean notifications_enabled = killer.getAttribOr(AttributeKey.ENABLE_LOOT_NOTIFICATIONS_BUTTONS, false);
        boolean untrade_notifications = killer.getAttribOr(AttributeKey.UNTRADABLE_LOOT_NOTIFICATIONS, false);
        int lootDropThresholdValue = killer.getAttribOr(AttributeKey.LOOT_DROP_THRESHOLD_VALUE, 0);
        if (notifications_enabled) {
            if (!loot.rawtradable()) {
                if (untrade_notifications) {
                    killer.message("Untradable drop: " + loot.getAmount() + " x <col=cc0000>" + loot.name() + "</col>.");
                }
            } else if (loot.getValue() >= lootDropThresholdValue) {
                killer.message("Valuable drop: " + loot.getAmount() + " x <col=cc0000>" + loot.name() + "</col> (" + loot.getValue() * loot.getAmount() + "coins).");
            }
        }
    }

    private static void serenDrops(Entity entity) {
        entity.getCombat().getDamageMap().forEach((key, hits) -> {
            Player player = (Player) key;
            player.message(Color.RED.wrap("You've dealt " + hits.getDamage() + " damage to The Seren!"));
            // Only people nearby are rewarded. This is to avoid people 'poking' the boss to do some damage
            // without really risking being there.
            if (entity.tile().isWithinDistance(player.tile(),10) && hits.getDamage() >= 200) {
                if(entity instanceof NPC) {
                    player.message("You received a drop roll from the table for dealing at least 500 damage!");
                    NPC npc = entity.getAsNpc();

                    //Always log kill timers
                    player.getBossTimers().submit(npc.def().name, (int) player.getCombat().getFightTimer().elapsed(TimeUnit.SECONDS), player);

                    //Always increase kill counts
                    player.getBossKillLog().addKill(npc);

                    //Always drop random BM
                    GroundItemHandler.createGroundItem(new GroundItem(new Item(BLOOD_MONEY, World.getWorld().random(500, 5_500)), npc.tile(), player));

                    //Random drop from the table
                    ScalarLootTable table = ScalarLootTable.forNPC(npc.id());
                    if (table != null) {
                        Item reward = table.randomItem(World.getWorld().random());
                        if (reward != null) {

                            // bosses, find npc ID, find item ID
                            BOSSES.log(player, npc.id(), reward);

                            //Niffler doesn't loot world The Nightmare loot
                            GroundItemHandler.createGroundItem(new GroundItem(reward, npc.tile(), player));

                         }
                    }
                }
            }
        });
    }

    private static void nightmareDrops(Entity entity) {
        entity.getCombat().getDamageMap().forEach((key, hits) -> {
            Player player = (Player) key;
            player.message(Color.RED.wrap("You've dealt " + hits.getDamage() + " damage to The Nightmare!"));
            // Only people nearby are rewarded. This is to avoid people 'poking' the boss to do some damage
            // without really risking being there.
            if (entity.tile().isWithinDistance(player.tile(),10) && hits.getDamage() >= 500) {
                if(entity instanceof NPC) {
                    player.message("You received a drop roll from the table for dealing at least 500 damage!");
                    NPC npc = entity.getAsNpc();

                    //Always log kill timers
                    player.getBossTimers().submit(npc.def().name, (int) player.getCombat().getFightTimer().elapsed(TimeUnit.SECONDS), player);

                    //Always increase kill counts
                    player.getBossKillLog().addKill(npc);

                    //Always drop random BM
                    GroundItemHandler.createGroundItem(new GroundItem(new Item(BLOOD_MONEY, World.getWorld().random(500, 5_500)), npc.tile(), player));

                    //Random drop from the table
                    ScalarLootTable table = ScalarLootTable.forNPC(npc.id());
                    if (table != null) {
                        Item reward = table.randomItem(World.getWorld().random());
                        if (reward != null) {

                            // bosses, find npc ID, find item ID
                            BOSSES.log(player, npc.id(), reward);

                            GroundItemHandler.createGroundItem(new GroundItem(reward, npc.tile(), player));

                        }
                    }
                }
            }
        });
    }
}
