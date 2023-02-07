package com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.specials;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.OlmAnimations;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.Attacks;
import com.aelous.model.content.raids.party.Party;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

/**
 * @author Patrick van Elderen | May, 16, 2021, 18:58
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class FireWall {

    public static void performAttack(Party party, int height) {
        //System.out.println("FireWall");
        party.getGreatOlmNpc().performGreatOlmAttack(party);
        party.setOlmAttackTimer(6);

        TaskManager.submit(new Task("FireWall:performAttackTask1",1, party, true) {
            int tick = 0;

            @Override
            public void execute() {
                if (party.getGreatOlmNpc().dead() || party.isSwitchingPhases()) {
                    stop();
                }

                Player player = party.randomPartyPlayer();

                if(player.dead() || !player.isRegistered()) {
                    stop();
                    return;
                }

                if (tick == 1) {
                    party.setFireWallPlayer(player);
                    party.getFireWallPlayer().getMovementQueue().reset();
                    party.getFireWallPlayer().getMovementQueue().setBlockMovement(true);
                }

                if (tick >= 2 && (party.getFireWallPlayer().tile().getY() < 5732
                    || party.getFireWallPlayer().tile().getY() > 5747)) {
                    OlmAnimations.resetAnimation(party);
                    stop();
                }

                if (tick == 2) {
                    OlmAnimations.resetAnimation(party);
                }

                if (tick == 2) {
                    if (party.getFireWallPlayer().getRaids() != null && party.getFireWallPlayer().getRaids().raiding(party.getFireWallPlayer()) && GreatOlm.insideChamber(party.getFireWallPlayer())) {
                        Tile pos = party.getFireWallPlayer().tile();
                        party.setFireWallSpawn(NPC.of(7558, new Tile(3228, pos.getY() + 1, height)));
                        party.setFireWallSpawn1(NPC.of(7558, new Tile(3228, pos.getY() - 1, height)));

                        NPC decoy = NPC.of(7556, new Tile(3228, pos.getY() + 1, height));
                        NPC decoy1 = NPC.of(7556, new Tile(3228, pos.getY() - 1, height));
                        TaskManager.submit(new Task("FireWall:performAttackTask2",1, party, true) {
                            @Override
                            public void execute() {
                                new Projectile(party.getGreatOlmNpc(), decoy, Attacks.FIRE_BLAST, 60, party.getGreatOlmNpc().projectileSpeed(decoy), 70, 31, 0).sendProjectile();
                                new Projectile(party.getGreatOlmNpc(), decoy1, Attacks.FIRE_BLAST, 60, party.getGreatOlmNpc().projectileSpeed(decoy1), 70, 31, 0).sendProjectile();
                                World.getWorld().registerNpc(party.getFireWallSpawn());
                                World.getWorld().registerNpc(party.getFireWallSpawn1());
                                stop();
                            }
                        });
                    }
                }

                if (tick == 5) {
                    party.getFireWallPlayer().getMovementQueue().reset();
                    party.getFireWallPlayer().getMovementQueue().setBlockMovement(false);

                    Tile pos = party.getFireWallPlayer().tile();
                    if (party.getFireWallPlayer().getRaids() != null && player.getRaids().raiding(party.getFireWallPlayer()) && GreatOlm.insideChamber(party.getFireWallPlayer())) {

                        int x = 3229;
                        for (int i = 0; i < 9; i++) {
                            Tile position = new Tile(x++, pos.getY() - 1, height);
                            party.getFireWallNpcs().add(NPC.of(7558, position));
                            NPC decoy = NPC.of(7558, position);
                            World.getWorld().registerNpc(decoy);
                            new Projectile(party.getFireWallSpawn1(), decoy, Attacks.SMALL_FIRE_BLAST, 60, party.getGreatOlmNpc().projectileSpeed(decoy), 30, 31, 0).sendProjectile();
                            TaskManager.submit(new Task("FireWall:performAttackTask3",2, party, true) {
                                @Override
                                public void execute() {
                                    World.getWorld().unregisterNpc((decoy));
                                    stop();
                                }
                            });
                        }
                        x = 3229;
                        for (int i = 9; i < 18; i++) {
                            Tile position = new Tile(x++, pos.getY() + 1, height);
                            party.getFireWallNpcs().add(NPC.of(7558, position));
                            NPC decoy = NPC.of(7558, position);
                            World.getWorld().registerNpc(decoy);
                            new Projectile(party.getFireWallSpawn1(), decoy, Attacks.SMALL_FIRE_BLAST, 60, party.getGreatOlmNpc().projectileSpeed(decoy), 30, 31, 0).sendProjectile();
                            TaskManager.submit(new Task("FireWall:performAttackTask4",2, party, true) {
                                @Override
                                public void execute() {
                                    World.getWorld().unregisterNpc(decoy);
                                    stop();
                                }
                            });
                        }

                        TaskManager.submit(new Task("FireWall:performAttackTask5",1, party, true) {
                            @Override
                            public void execute() {
                                party.teamMessage("" + party.getFireWallNpcs().size());
                                for (int i = 0; i < 9; i++) {
                                    World.getWorld().registerNpc(party.getFireWallNpcs().get(i));
                                }
                                for (int i = 9; i < 18; i++) {
                                    World.getWorld().registerNpc(party.getFireWallNpcs().get(i));
                                }
                                stop();
                            }
                        });
                    }
                }
                if (tick == 7) {
                    Tile pos = party.getFireWallPlayer().tile();
                    if (party.getFireWallPlayer().getRaids() != null && player.getRaids().raiding(party.getFireWallPlayer()) && GreatOlm.insideChamber(party.getFireWallPlayer())) {

                        int x = 3229;
                        for (int i = 0; i < 9; i++) {
                            Tile position = new Tile(x++, pos.getY() - 1, height);
                            party.getFireWallNpcs().add(NPC.of(7558, position));
                            World.getWorld().registerNpc(party.getFireWallNpcs().get(i));
                        }
                        x = 3229;
                        for (int i = 9; i < 18; i++) {
                            Tile position = new Tile(x++, pos.getY() + 1, height);
                            party.getFireWallNpcs().add(NPC.of(7558, position));
                            World.getWorld().registerNpc(party.getFireWallNpcs().get(i));
                        }
                    }
                }

                if (tick == 15) {
                    for (Player member : party.getMembers()) {
                        if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(party.getFireWallPlayer())) {
                            if (member.tile().getY() == party.getFireWallPlayer().tile().getY()) {
                                member.hit(party.getGreatOlmNpc(), World.getWorld().random(40, 60), party.getGreatOlmNpc().getProjectileHitDelay(member), CombatType.MAGIC).checkAccuracy().submit();
                            }
                        }
                    }
                }

                if (tick == 20) {
                    for (NPC fireWallNpc : party.getFireWallNpcs()) {
                        World.getWorld().unregisterNpc(fireWallNpc);
                    }
                    World.getWorld().unregisterNpc(party.getFireWallSpawn());
                    World.getWorld().unregisterNpc(party.getFireWallSpawn1());
                    party.getFireWallNpcs().clear();
                    stop();
                }
                tick++;
            }
        });
    }
}
