package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.*;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.left_hand.AutoHeal;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.left_hand.CrystalBurst;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.left_hand.Lightning;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.left_hand.Swap;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.specials.*;
import com.aelous.model.content.raids.party.Party;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;

/**
 * @author Patrick van Elderen | May, 16, 2021, 13:04
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class GreatOlmCombat {

    public static void sequence(Party party, int height) {

        TaskManager.submit(new Task("GreatOlmCombatTask",1, party, false) {
            int tick = 0;

            @Override
            public void execute() {
                if (party.getGreatOlmNpc().dead()) {
                    stop();
                }
                if (party.getPlayersInRaidsLocation(party) == 0) {
                    stop();
                }

                party.setCanAttack(false);
                party.setCanAttackHand(false);

                if (tick >= 2) {
                    if (tick % party.getLeftHandAttackTimer() == 0) {
                        party.setCanAttackHand(true);
                    }
                    if (tick % party.getOlmAttackTimer() == 0 && !party.isSwitchingPhases()) {
                        party.setCanAttack(true);
                    }
                }

                FallingCrystalsTransition.performAttack(party, tick);

                if (party.getPlayersInRaidsLocation(party) >= 1 && !party.getGreatOlmNpc().dead()
                    && party.getGreatOlmNpc().hp() > 0) {
                    if (!party.isTransitionPhase()) {
                        if (party.isCanAttackHand() && !party.isLeftHandDead() && !party.isLeftHandProtected()) {
                            if (party.getCurrentLeftHandCycle() == 0) {
                                party.setCurrentLeftHandCycle(party.getCurrentLeftHandCycle() + 1);
                                Swap.performAttack(party, height);
                            } else if (party.getCurrentLeftHandCycle() == 1) {
                                party.setCurrentLeftHandCycle(party.getCurrentLeftHandCycle() + 1);
                                Lightning.performAttack(party, height);
                            } else if (party.getCurrentLeftHandCycle() == 2) {
                                if (party.getCurrentPhase() == 3)
                                    party.setCurrentLeftHandCycle(party.getCurrentLeftHandCycle() + 1);
                                else
                                    party.setCurrentLeftHandCycle(0);
                                CrystalBurst.performAttack(party);
                            } else if (party.getCurrentLeftHandCycle() == 3) {
                                party.setCurrentLeftHandCycle(0);
                                AutoHeal.performAttack(party);
                            }
                        }

                        if (!party.getOlmTurning() && party.getCanAttack() && (party
                            .getGreatOlmNpc().lastDirection() == party.getGreatOlmNpc().spawnDirection())) {
                            party.setOlmAttacking(true);
                            if (party.getAttackCount() >= (2 + World.getWorld().random(5))) {

                                party.setAttackCount(0);

                                int random = World.getWorld().random(2);

                                if (party.getPhaseAttack().contains("@gre@acid")) {
                                    AcidDrip.performAttack(party);
                                }
                                if (party.getPhaseAttack().contains("@mag@crystal")) {
                                    if (random == 1)
                                        FallingCrystals.performAttack(party, height);
                                    else
                                        CrystalBombs.performAttack(party, height);
                                }
                                if (party.getPhaseAttack().contains("@red@flame")) {
                                    DeepBurn.performAttack(party);
                                }
                            } else {
                                party.setAttackCount(party.getAttackCount() + 1);
                                int random = World.getWorld().random(9);
                                if (random <= 3)
                                    MagicAttack.performAttack(party);
                                else if (random <= 7)
                                    RangeAttack.performAttack(party);
                                else if (random == 8)
                                    OrbAttack.performAttack(party);
                            }
                        }
                    }
                }
                tick++;
            }
        });
    }
}
