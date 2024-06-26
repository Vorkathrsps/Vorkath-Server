package com.cryptic.model.content.skill.impl;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class SkillReset extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == NpcIdentifiers.MERCENARY_8213) {
            if (option == 1) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.MERCENARY_8213, Expression.DEFAULT, "Hello " + player.getUsername(), "Would you like to reset a combat skill of your choice?");
                        setPhase(0);
                    }

                    @Override
                    protected void next() {
                        if (isPhase(0)) {
                            send(DialogueType.OPTION, "Would you like to reset a combat skill?", "Yes", "No");
                            setPhase(1);
                            return;
                        }
                        if (isPhase(4)) {
                            stop();
                        }
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(1)) {
                            if (option == 1) {
                                send(DialogueType.OPTION, "Choose Skill", "Attack", "Strength", "Defence", "Ranged", "Next Page");
                                setPhase(2);
                            } else {
                                stop();
                            }
                        } else if (isPhase(2)) {
                            if (option == 1) {
                                resetSkill(Skills.ATTACK);
                            } else if (option == 2) {
                                resetSkill(Skills.STRENGTH);
                            } else if (option == 3) {
                                resetSkill(Skills.DEFENCE);
                            } else if (option == 4) {
                                resetSkill(Skills.RANGED);
                            } else if (option == 5) {
                                send(DialogueType.OPTION, "Choose Skill", "Prayer", "Magic", "Hitpoints", "Nevermind");
                                setPhase(3);
                            }
                        } else if (isPhase(3)) {
                            if (option == 1) {
                                resetSkill(Skills.PRAYER);
                            } else if (option == 2) {
                                resetSkill(Skills.MAGIC);
                            } else if (option == 3) {
                                resetSkill(Skills.HITPOINTS);
                            } else if (option == 4) {
                                stop();
                            }
                        } else if (isPhase(4)) {
                            if (option == 1) {
                                stop();
                            }
                        }
                    }

                    private void resetSkill(int skill) {
                        int amount = 0;
                        if (skill == Skills.HITPOINTS) {
                            amount = 1154;
                        }
                        player.getSkills().setXp(skill, amount);
                        player.getSkills().update();
                        player.getSkills().recalculateCombat();
                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.MERCENARY_8213, Expression.DEFAULT, "Alright,", "I've reset your " + Skill.values()[skill].getName() + " Skill!");
                        setPhase(4);
                    }
                });
                return true;
            }
        }
        return false;
    }

}
