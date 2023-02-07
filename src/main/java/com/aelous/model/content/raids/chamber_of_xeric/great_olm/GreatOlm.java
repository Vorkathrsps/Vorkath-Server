package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.model.content.raids.party.Party;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.COXArea;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | May, 16, 2021, 12:36
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class GreatOlm extends NPC {

    public GreatOlm(int id, Tile position) {
        super(id, position);
        getMovementQueue().reset();
        getMovementQueue().setBlockMovement(true);
        respawns(false);
    }

    public static boolean insideChamber(Player player) {
        return player.getController() != null && player.getController() instanceof COXArea && player.tile().y >= 5730;
    }

    @Override
    public CommonCombatMethod getCombatMethod() {
        return new GreatOlmCombatMethod();
    }

    public static void start(Party party) {
        party.bossFightStarted(true);
        party.setCanAttackLeftHand(false);
        party.setCanAttack(false);
        party.setOlmAttackTimer(6);
        party.setLeftHandAttackTimer(6);
        party.setCurrentPhase(0);
        party.setClenchedHand(false);
        party.setLeftHandProtected(false);
        party.setClenchedHandFirst(false);
        party.setClenchedHandSecond(false);
        party.setUnClenchedHandFirst(false);
        party.setUnClenchedHandSecond(false);
        party.setLastPhaseStarted(false);
        party.setSwitchingPhases(false);

        TaskManager.submit(new Task("clearAttributesTask",1) {
            int tick = 0;

            @Override
            public void execute() {
                if (tick == 10)
                    startTask(party, party.getHeight());
                if (tick == 20) {
                    GreatOlmCombat.sequence(party, party.getHeight());
                    stop();
                }
                tick++;
            }
        });
    }

    private static void startTask(Party party, int height) {
        TaskManager.submit(new Task("GreatOlmstartTask",1, party, false) {
            int tick = 0;

            @Override
            public void execute() {
                if (tick == 0) {
                    Phases.startPhase1(party, height);
                    party.setSwitchingPhases(false);
                }

                if (party.getPlayersInRaidsLocation(party) == 0) {
                    stop();
                }

                //phaseChange(party, height);
                if (tick >= 11) {
                    if (!party.isSwitchingPhases()) {
                        directionChange(party, tick);
                    }
                }

                if (party.getCurrentPhase() < 3 && party.getLeftHandNpc().hp() <= 590
                    && !party.isRightHandDead() && !party.isClenchedHand() && !party.isClenchedHandFirst()
                    && party.getLeftHandNpc().hp() > 0) {
                    clenchHand(party);
                    party.setClenchedHandFirst(true);
                }

                if (party.getCurrentPhase() < 3
                    && party.getLeftHandNpc().hp() <= 400
                    && !party.isRightHandDead() && !party.isClenchedHand() && !party.isClenchedHandSecond()
                    && party.getLeftHandNpc().hp() > 0) {
                    clenchHand(party);
                    party.setClenchedHandSecond(true);
                }

                if (party.getCurrentPhase() < 3 && party.getRightHandNpc().hp() <= 480
                    && party.isRightHandDead() && !party.isLeftHandDead() && party.isClenchedHand()
                    && !party.isUnClenchedHandFirst()) {
                    party.setUnClenchedHandFirst(true);
                    unClenchHand(party);
                }

                if (party.getCurrentPhase() < 3 && party.getRightHandNpc().hp() <= 250
                    && party.isRightHandDead() && !party.isLeftHandDead() && party.isClenchedHand()
                    && !party.isUnClenchedHandSecond()) {
                    party.setUnClenchedHandSecond(true);
                    unClenchHand(party);
                }

                if (party.getCurrentPhase() < 3 && party.getRightHandNpc().hp() <= 0
                    && party.isRightHandDead() && !party.isLeftHandDead() && party.isClenchedHand()) {
                    unClenchHand(party);
                }

                if (!party.isLastPhaseStarted() && (party.getCurrentPhase() == 3 && party.isLeftHandDead() && party.isRightHandDead())) {
                    party.setLastPhaseStarted(true);
                    party.teamMessage("The Basilisk is giving its all. This is its final stand.");
                }

                if (party.getRightHandNpc().dead() || party.getRightHandNpc().hp() <= 0) {
                    party.setCanAttackLeftHand(true);
                }

                if (party.getGreatOlmNpc().dead()) {
                    party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7348));
                    finishRaid(party, height);
                    stop();
                }
                tick++;
            }
        });
    }

    private static void directionChange(Party party, int tick) {

        int middlePositions = 0;
        int southPositions = 0;
        int northPositions = 0;
        for (Player member : party.getMembers()) {
            if (member != null) {
                if (member.tile().getY() >= 5743) {
                    northPositions++;
                } else if (member.tile().getY() <= 5737) {
                    southPositions++;
                } else {
                    middlePositions++;
                }
            }
        }
        boolean switchDirections = tick % party.getOlmAttackTimer() == 0;

        if ((switchDirections && !party.isTransitionPhase()) || party.isSwitchAfterAttack()) {
            if (party.isOlmAttacking()) {
                party.setSwitchAfterAttack(true);
            } else {
                party.setOlmTurning(true);
                party.setSwitchAfterAttack(false);
                if ((party.getGreatOlmNpc().lastDirection() == party.getGreatOlmNpc().spawnDirection())) {
                    party.setOlmTurning(false);
                } else {
                    Chain.bound(null).runFn(2, () -> party.setOlmTurning(false));
                }
                party.getGreatOlmNpc().lastDirection(party.getGreatOlmNpc().spawnDirection());

                if (northPositions > southPositions && northPositions > middlePositions) {
                    party.getGreatOlmNpc().spawnDirection(Direction.NORTH.toInteger());
                } else if (southPositions > northPositions && southPositions > middlePositions) {
                    party.getGreatOlmNpc().spawnDirection(Direction.SOUTH.toInteger());
                } else {
                    party.getGreatOlmNpc().spawnDirection(Direction.NONE.toInteger());
                }

                if (party.getGreatOlmNpc().spawnDirection() != party.getGreatOlmNpc().lastDirection()) {
                    if (party.getGreatOlmNpc().tile().getX() >= 3238)
                        DirectionSwitching.switchDirectionsEast(party);
                    else
                        DirectionSwitching.switchDirectionsWest(party);
                }
            }
        }
    }

    private static void clenchHand(Party party) {
        party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7360));
        party.setClenchedHand(true);
        party.setLeftHandProtected(true);
        Chain.bound(null).runFn(2, () -> {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7361));
            party.teamMessage("The Basilisk's left claw clenches to protect itself temporarily.");
        });
    }

    private static void unClenchHand(Party party) {
        party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7362));
        party.setClenchedHand(false);
        party.setLeftHandProtected(false);
        Chain.bound(null).runFn(2, () -> party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7355)));
    }

    private static void finishRaid(Party party, int height) {
        party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7348));
        ObjectManager.addObj(new GameObject(29885, new Tile(3238, 5733, height), 10, 1));
        ObjectManager.addObj(new GameObject(29882, new Tile(3238, 5738, height), 10, 1));
        ObjectManager.addObj(new GameObject(29888, new Tile(3238, 5743, height), 10, 1));
        ObjectManager.addObj(new GameObject(29885, new Tile(3220, 5743, height), 10, 3));
        ObjectManager.addObj(new GameObject(29882, new Tile(3220, 5738, height), 10, 3));
        ObjectManager.addObj(new GameObject(29888, new Tile(3220, 5733, height), 10, 3));

        GameObject crystal = new GameObject(30018, new Tile(3232, 5749, height));
        Chain.bound(null).runFn(3, () -> {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(crystal, 7506));
            GameObject chest = new GameObject(30028, new Tile(3233, 5751, height), 10, 0);
            ObjectManager.addObj(chest);
            World.getWorld().unregisterNpc(party.getGreatOlmNpc());
            World.getWorld().unregisterNpc(party.getLeftHandNpc());
            World.getWorld().unregisterNpc(party.getRightHandNpc());
        }).then(2, () -> {
            ObjectManager.removeObj(new GameObject(30018, new Tile(3232, 5750, height)));
            ObjectManager.removeObj(new GameObject(30018, new Tile(3232, 5749, height)));
            ObjectManager.removeObj(new GameObject(30018, new Tile(3233, 5750, height)));
            ObjectManager.removeObj(new GameObject(30018, new Tile(3233, 5749, height)));
        });
        if (party.getLeader().getRaids() != null)
            party.getLeader().getRaids().complete(party);
    }

}
