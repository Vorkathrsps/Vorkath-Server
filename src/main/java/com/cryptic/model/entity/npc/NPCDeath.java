package com.cryptic.model.entity.npc;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.areas.burthope.warriors_guild.MagicalAnimator;
import com.cryptic.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.cryptic.model.content.areas.wilderness.wildernesskeys.WildernessKeys;
import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.content.skill.impl.slayer.superior_slayer.SuperiorSlayer;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.kalphite.KalphiteQueenFirstFormCombat;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.kalphite.KalphiteQueenSecondFormCombat;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.zulrah.Zulrah;
import com.cryptic.model.entity.combat.method.impl.npcs.fightcaves.TzTokJad;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.cryptic.model.entity.combat.method.impl.npcs.hydra.AlchemicalHydra;
import com.cryptic.model.entity.combat.method.impl.npcs.karuulm.Drake;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.Gargoyle;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.Nechryael;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch.NechryarchDeathSpawn;
import com.cryptic.model.entity.npc.droptables.ItemDrops;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.*;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.CAVE_KRAKEN;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.NECHRYAEL;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.RUNE_DRAGON;
import static com.cryptic.model.content.achievements.AchievementsManager.*;
import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.utility.CustomNpcIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

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
        SlayerTask slayerTask = World.getWorld().getSlayerTasks();
        var respawnTimer = Utils.secondsToTicks(45);// default 45 seconds
        NpcDefinition def = World.getWorld().definitions().get(NpcDefinition.class, npc.id());
        if (def != null) {
            if (def.combatLevel >= 1 && def.combatLevel <= 50) {
                respawnTimer = Utils.secondsToTicks(30);//30 seconds
            } else if (def.combatLevel >= 51 && def.combatLevel <= 150) {
                respawnTimer = Utils.secondsToTicks(25);//25 seconds
            } else {
                respawnTimer = Utils.secondsToTicks(20);// 20 seconds
            }
        }

        NpcDefinition definition = NpcDefinition.get(npc.id());

        npc.getMovementQueue().clear();
        npc.getCombat().reset();
        npc.lockNoDamage();

        npc.ignoreOccupiedTiles = false;

        npc.setEntityInteraction(null);

        Optional<Player> killer_id = npc.getCombat().getKiller();

        Player killer = killer_id.orElse(null);

        if (killer != null) {
            /*var assignment = slayerTask.getCurrentAssignment(killer);
            var biggest_and_baddest_perk = killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.BIGGER_AND_BADDER) && assignment != null && ArrayUtils.contains(assignment.getNpcs(), npc.id());
            var ancientRevSpawnRoll = 25;
            var superiorSpawnRoll = biggest_and_baddest_perk ? 4 : 6;*/

            killer.getCombat().reset();

            if (!npc.isWorldBoss() || npc.id() != THE_NIGHTMARE_9430 || npc.id() != KALPHITE_QUEEN_6500) {
                killer.getBossKillLog().addKill(npc);
            }

            if (definition != null && definition.name != null) {
                if (definition.name.equalsIgnoreCase("Adamant dragon")) {
                    var kc = killer.<Integer>getAttribOr(ADAMANT_DRAGONS_KILLED, 0) + 1;
                    killer.putAttrib(ADAMANT_DRAGONS_KILLED, kc);
                }

                if (definition.name.equalsIgnoreCase("Rune dragon")) {
                    var kc = killer.<Integer>getAttribOr(RUNE_DRAGONS_KILLED, 0) + 1;
                    killer.putAttrib(RUNE_DRAGONS_KILLED, kc);
                }

                if (definition.name.equalsIgnoreCase("Lava dragon")) {
                    var kc = killer.<Integer>getAttribOr(LAVA_DRAGONS_KILLED, 0) + 1;
                    killer.putAttrib(LAVA_DRAGONS_KILLED, kc);
                }
            }

            if (killer.getMinigame() != null) {
                killer.getMinigame().killed(killer, npc);
            }

            //Make sure spawns are killed on boss death
            if (npc.id() == SCORPIA) {
                killer.getTaskMasterManager().increase(Tasks.SCORPIA);
                npc.clearAttrib(AttributeKey.SCORPIA_GUARDIANS_SPAWNED);
                World.getWorld().getNpcs().forEachInArea(new Area(3219, 3248, 10329, 10353), n -> {
                    if (n.id() == SCORPIAS_GUARDIAN) {
                        World.getWorld().unregisterNpc(n);
                    }
                });
            }

            if (!killer.getControllers().isEmpty()) {
                for (Controller controller : killer.getControllers()) {
                    controller.defeated(killer, npc);
                }
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
        switch (npc) {
            case AlchemicalHydra alchemicalHydra -> {
                alchemicalHydra.animate(8257);
                Chain.bound(null).runFn(2, () -> {
                    alchemicalHydra.transmog(8622, false);
                    alchemicalHydra.animate(8258);
                });
            }
            case Drake drake -> {
                drake.animate(8277);
                Chain.bound(null).runFn(1, () -> {
                    drake.transmog(8613, false);
                    drake.animate(8278);
                });
            }
            case TzTokJad tzTokJad -> tzTokJad.graphic(453);
            default -> npc.animate(npc.getCombatInfo() != null && npc.getCombatInfo().animations != null ? npc.getCombatInfo().animations.death : -1);
        }

        if (npc.id() == 6612) {
            npc.animate(9980);
        }

        int finalRespawnTimer = respawnTimer;

        Chain.bound(null).runFn(npc.getCombatInfo() != null ? npc.getCombatInfo().deathlen : 5, () -> {
            // Npc died to npc.
            if (killer == null && npc.getCombatMethod() instanceof CommonCombatMethod commonCombatMethod) {
                commonCombatMethod.onDeath(null, npc); // make sure this is always called
            }

            if (killer != null) {

                SuperiorSlayer.trySpawn(killer, slayerTask, npc);
                slayerTask.handleSlayerDeath(killer, npc);

                if (WildernessArea.isInWilderness(killer)) {
                    WildernessKeys.rollWildernessKey(killer, npc);
                }

                if (npc.getCombatMethod() instanceof CommonCombatMethod commonCombatMethod) {
                    commonCombatMethod.set(npc, killer);
                    commonCombatMethod.onDeath(killer, npc);
                }

                if (definition != null) {
                    AchievementsManager.handle(definition.name, killer);
                }

                //Rock crabs
                if (npc.id() == 101 || npc.id() == 103) {
                    switch (npc.id()) {
                        case 101 -> npc.transmog(101, false);
                        case 103 -> npc.transmog(103, false);
                    }
                    npc.walkRadius(0);
                }

                // so in java .. we dont have functions so we need to hardcode the id check
                if (WildernessBossEvent.getINSTANCE().getActiveNpc().isPresent() &&
                    npc == WildernessBossEvent.getINSTANCE().getActiveNpc().get()) {
                    WildernessBossEvent.getINSTANCE().bossDeath(npc);
                }

                killer.getBossTimers().submit(NpcDefinition.cached.get(npc.id()).name, (int) killer.getCombat().getFightTimer().elapsed(TimeUnit.SECONDS), killer);

                //Drop loot, but the first form of KQ, Runite golem and world bosses do not drop anything.
                if ((npc.id() != KALPHITE_QUEEN_6500 && npc.id() != RUNITE_GOLEM && !npc.isWorldBoss() && npc.id() != THE_NIGHTMARE_9430)) {

                    if (WildernessArea.inWilderness(killer.tile())) {
                        killer.getWildernessSlayerCasket().rollForCasket(killer, npc);
                        killer.getWildernessSlayerCasket().rollForSupplys(killer, npc);
                    }

                    ItemDrops drop = new ItemDrops();
                    drop.rollTheDropTable(killer, npc);
                }

                if (npc.getCombatInfo() != null && npc.getCombatInfo().scripts != null && npc.getCombatInfo().scripts.droptable_ != null) {
                    npc.getCombatInfo().scripts.droptable_.reward(npc, killer);
                }
            }

            if (npc.id() == KALPHITE_QUEEN_6500) {
                KalphiteQueenFirstFormCombat.death(npc);
                return;
            } else if (npc.id() == KALPHITE_QUEEN_6501) {
                KalphiteQueenSecondFormCombat.death(npc);
            }

            if (npc.id() == 6716 || npc.id() == 6723 || npc.id() == 7649) {
                NechryarchDeathSpawn.death(npc); //Do death spawn death
            }

            if (npc.id() == NECHRYAEL || npc.id() == NECHRYAEL_11) {
                new Nechryael().onDeath(npc);
            }

            Zulrah.death(killer, npc);

            if (npc.id() == CORPOREAL_BEAST) {
                NPC corp = npc.getAttribOr(AttributeKey.BOSS_OWNER, null);
                if (corp != null) {
                    List<NPC> minList = corp.getAttribOr(AttributeKey.MINION_LIST, null);
                    if (minList != null) {
                        minList.remove(npc);
                    }
                }
            }

            if (killer != null) {
                if (npc.respawns() && !npc.isBot())
                    killer.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(finalRespawnTimer), EffectTimer.MONSTER_RESPAWN);
            }

            deathReset(npc);
            if (npc.respawns()) {
                npc.teleport(npc.spawnTile());
                npc.hidden(true);
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
        return npcId != KALPHITE_QUEEN_963 && npcId != GREAT_OLM_LEFT_CLAW_7555 && npcId != GREAT_OLM_RIGHT_CLAW_7553;
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

        if (npc.getCombatMethod() instanceof CommonCombatMethod commonCombatMethod) {
            commonCombatMethod.onRespawn(npc);
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
                npc.transmog(DRAKE_8612, false);
            }
        }
    }
}
