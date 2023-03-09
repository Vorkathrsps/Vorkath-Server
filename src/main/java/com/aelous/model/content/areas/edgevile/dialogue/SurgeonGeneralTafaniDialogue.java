package com.aelous.model.content.areas.edgevile.dialogue;

import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Color;
import com.aelous.utility.timers.TimerKey;

public class SurgeonGeneralTafaniDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        setPhase(0);
        send(DialogueType.NPC_STATEMENT, player.getInteractingNpcId(), Expression.DEFAULT, "Would you like me to heal you?");

    }

    @Override
    public void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No thanks.");
            setPhase(1);
        }
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            setPhase(2);
            if (option == 1) {
                player.performGraphic(new Graphic(683));
                player.message("<col="+ Color.BLUE.getColorValue()+">You have restored your hitpoints, run energy and prayer.");
                player.message("<col="+ Color.HOTPINK.getColorValue()+">You've also been cured of poison and venom.");
                player.hp(Math.max(player.getSkills().level(Skills.HITPOINTS), player.getSkills().xpLevel(Skills.HITPOINTS)), 20); //Set hitpoints to 100%
                player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5)); //Set the players prayer level to full
                player.getSkills().replenishStatsToNorm();
                player.setRunningEnergy(100.0, true);
                Poison.cure(player);
                Venom.cure(2, player, false);

                if(player.getMemberRights().isEliteMemberOrGreater(player)) {
                    if (player.getTimers().has(TimerKey.RECHARGE_SPECIAL_ATTACK)) {
                        player.message("Special attack energy can only be restored every couple of minutes.");
                    } else {
                        player.setSpecialAttackPercentage(100);
                        player.setSpecialActivated(false);
                        CombatSpecial.updateBar(player);
                        player.getTimers().register(TimerKey.RECHARGE_SPECIAL_ATTACK,150); //Set the value of the timer.
                        player.message("<col="+ Color.HOTPINK.getColorValue()+">You have restored your special attack.");
                    }
                }
            }
            stop();
        }
    }

}
