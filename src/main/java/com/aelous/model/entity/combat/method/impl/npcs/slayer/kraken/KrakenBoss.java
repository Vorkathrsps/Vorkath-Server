package com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCDeath;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KrakenBoss {

    // NPC ids
    public static final int KRAKEN_WHIRLPOOL = 496;
    public static final int TENTACLE_WHIRLPOOL = 5534;

    public static final int KRAKEN_NPCID = 494;
    public static final int TENTACLE_NPCID = 5535;

    // Object id
    private static final int CREVICE = 537;

    // Relevent tiles
    private static final Tile ENTER_TILE = new Tile(2280, 10022);
    private static final Tile BOSS_TILE = new Tile(2278, 10034);

    // Going from bottom left, top left, top right, bottom right
    public static final List<Tile> TENT_TILES = Arrays.asList(new Tile(2276, 10033), new Tile(2276, 10037), new Tile(2283, 10037), new Tile(2283, 10033));
    private static final Area REAL_ROOM_AREA = new Area(new Tile(2269, 10023), new Tile(2302, 10046));

    // Obj id
    private static final int EXIT_CREVICE = 538;

    //Leave cove obj
    private static final int CAVE_EXIT = 30178;

    // Tile to teleport to when you leave
    private static final Tile ROOM_EXIT = new Tile(2280, 10016);

    // Corner of the region we are going to create an Instance of
    private static final Tile CORNER = new Tile(2240, 9984);

    public static boolean onObject(Player player, GameObject obj, int opt) {
        // Enter crevice
        if (obj.getId() == CREVICE) {
            switch (opt) {
                case 1 -> {// Enter
                    if (CombatFactory.inCombat(player)) {
                        DialogueManager.sendStatement(player, "You can't go in here when under attack.");
                        player.message("You can't go in here when under attack.");
                    } else {
                        player.teleport(ENTER_TILE);
                    }
                }
                case 2 -> {// Private, instanced
                    player.getDialogueManager().start(new KrakenInstanceD());
                }

                case 3 -> {// Look inside
                    int count = 0;
                    for (Player p : World.getWorld().getPlayers()) {
                        if (p != null && p.tile().inArea(REAL_ROOM_AREA))
                            count++;
                        String strEnd = count == 1 ? "" : "s";
                        String isAre = count == 1 ? "is" : "are";
                        DialogueManager.sendStatement(player, "There " + isAre + " currently " + count + " player" + strEnd + " in the cave.");
                    }
                }
            }

            return true;
        }

        //Leaving cove.
        if (obj.getId() == CAVE_EXIT) {
            player.teleport(3088, 3505);
            return true;
        }

        // Leaving
        if (obj.getId() == EXIT_CREVICE) {
            //Check if instance is active
            if (player.getInstancedArea() != null) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.OPTION, "Leave the instance? You cannot return.", "Yes, I want to leave.", "No, I'm staying for now.");
                    }

                    @Override
                    protected void select(int option) {
                        if (option == 1) {
                            player.teleport(ROOM_EXIT);
                        } else if (option == 2) {
                            stop();
                        }
                    }
                });
            } else {
                player.teleport(ROOM_EXIT);
            }
        }
        return false;
    }

    // Spawn hook
    public static void onNpcSpawn(NPC kraken) {
        if (kraken.id() == KRAKEN_WHIRLPOOL) {

            kraken.getCombatInfo().respawntime = 9;

            // Is it spawned in an Instance?
            //TODO?

            // Must be spawned into normal world
            for (Tile tile : TENT_TILES) {
                NPC tentacle = new NPC(TENTACLE_WHIRLPOOL, tile).spawnDirection(6);
                World.getWorld().registerNpc(tentacle);
                tentacle.putAttrib(AttributeKey.BOSS_OWNER, kraken);

                var list = kraken.<ArrayList<NPC>>getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
                list.add(tentacle);
                kraken.putAttrib(AttributeKey.MINION_LIST, list);
            }
        }
    }

    public static void onHit(Player player, NPC npc) {
        var minions = npc.<ArrayList<NPC>>getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
        // Have all minions been attacked first?
        var awake = 0;
        for (NPC minion : minions) {
            if (minion.transmog() == KrakenBoss.TENTACLE_NPCID) {
                awake++;
            } else {
                //System.out.println("minion not awake: "+minion.id()+" "+minion.transmog());
            }
        }

        //System.out.println("awake: "+awake);

        if(awake != 4) {
            var amt = "";

            switch (awake) {
                case 1 -> amt = "other three tentacles";
                case 2 -> amt = "other two tentacles";
                case 3 -> amt = "last tentacle";
                default -> amt = "four tentacles";
            }
            player.message("The "+amt+" need to be disturbed before the Kraken emerges.");
        } else {
            // Do transform and retaliate
            npc.getTimers().addOrSet(TimerKey.COMBAT_ATTACK, 1);
            npc.getCombat().attack(player);

            npc.transmog(KRAKEN_NPCID);
            npc.def(World.getWorld().definitions().get(NpcDefinition.class, KRAKEN_NPCID));
            npc.getCombatInfo(World.getWorld().combatInfo(KRAKEN_NPCID)); // Quickly replace scripts for retaliation before Java finishes processing.
            npc.setCombatMethod(World.getWorld().combatInfo(KRAKEN_NPCID).scripts.newCombatInstance());
            npc.animate(7135);
        }
    }

    public static void onDeath(NPC npc) {
        // Then do the death anim on all tentacles
        List<NPC> minions = npc.<ArrayList<NPC>>getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
        for (NPC minion : minions) {
            if (!minion.hidden()) { // Already dead maybe from recoil/venom
                minion.stopActions(false);
                minion.getCombat().reset();
                NPCDeath.deathReset(minion);
                minion.animate(minion.getCombatInfo().animations.death);
            }
        }

        // Wait 2 for tents to die
        Chain.bound(null).runFn(2, () -> {
            // Hide them
            for (NPC minion : minions) {
                minion.hidden(true);
                minion.transmog(TENTACLE_WHIRLPOOL); // Set it back to the whirlpool
                minion.getCombatInfo(World.getWorld().combatInfo(TENTACLE_WHIRLPOOL));
                minion.hp(minion.maxHp(), 0);
            }
        }).then(11, () -> {
            // Wait some seconds - during this time the default NPCDeath script will respawn the Kraken boss

            // Respawn the minions
            for (NPC m : minions) {
                NPCDeath.respawn(m);
            }
        });
    }

}
