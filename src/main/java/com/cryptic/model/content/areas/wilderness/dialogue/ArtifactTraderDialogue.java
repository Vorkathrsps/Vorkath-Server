package com.cryptic.model.content.areas.wilderness.dialogue;

import com.cryptic.model.content.areas.wilderness.content.revenant_caves.AncientArtifacts;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.content.bountyhunter.BountyHunter;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.items.Item;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.utility.Utils;

import static com.cryptic.model.entity.attributes.AttributeKey.EMBLEM_WEALTH;

/**
 * @author Zerikoth
 * @Since oktober 09, 2020
 */
public class ArtifactTraderDialogue extends Dialogue {

    /**
     * The id of the npc
     */
    public static final int NPC_ID = NpcIdentifiers.EMBLEM_TRADER;

    @Override
    protected void start(Object... parameters) {
        var startPhase = player.<Integer>getAttribOr(AttributeKey.DIALOGUE_PHASE, 0);

        if(startPhase == 0) {
            sendNpcChat(NPC_ID, Expression.CALM_TALK, "Hello there " + player.getUsername() + ", how can i help you?");
            setPhase(0);
        } else if(startPhase == 12) {
            sendOption("<col=800000>A PK skull means you drop ALL your items on death.", "Give me a PK skull.", "cancel.");
            setPhase(13);
        }
    }

    @Override
    protected void next() {
        //System.out.println("current phase: "+getPhase());
        if (isPhase(0)) {
            sendOption(DEFAULT_OPTION_TITLE, "I would like to trade my Mysterious Emblems.", "I would like to trade in my ancient artifacts.", "Nevermind.");
            setPhase(1);
        } else if (isPhase(1)) {
            sendNpcChat(NPC_ID, Expression.CALM_TALK, "Certainly, let me calculate ", "your total points from your Mysterious Emblems.");
            setPhase(2);
        } else if (isPhase(2)) {
            sendStatement("Calculating total value...");
            setPhase(3);
        } else if (isPhase(3)) {
            int totalBM = BountyHunter.exchange(player, false);
            if (totalBM > 0) {
                sendNpcChat(NPC_ID, Expression.CALM_TALK, "You will get a total of "+player.<String>getAttribOr(AttributeKey.EMBLEM_WEALTH,"")+"", "for your Mysterious Emblems. Do you wish to exchange", "them?");
                setPhase(4);
            } else {
                sendNpcChat(NPC_ID, Expression.CALM_TALK, "You do not have any Mysterious Emblems.");
                setPhase(6);
            }
        } else if (isPhase(4)) {
            sendOption(DEFAULT_OPTION_TITLE, "Yes", "No");
            setPhase(5);
        } else if (isPhase(6)) {
            stop();
        } else if (isPhase(7)) {
            sendNpcChat(NPC_ID, Expression.CALM_TALK, "Certainly, let me calculate ", "your total BM from your wilderness artifacts.");
            setPhase(8);
        } else if (isPhase(8)) {
            sendStatement("Calculating total BM...");
            setPhase(9);
        } else if (isPhase(10)) {
            sendOption(DEFAULT_OPTION_TITLE, "Yes", "No");
            setPhase(11);
        } else if (isPhase(14)) {
            sendNpcChat(NPC_ID, Expression.CALM_TALK, "Certainly, let me calculate ", "your total points from your ancient artifacts.");
            setPhase(15);
        } else if (isPhase(15)) {
            sendStatement("Calculating total coins...");
            setPhase(16);
        } else if (isPhase(16)) {
            int totalBM = AncientArtifacts.exchange(player, false);
            if (totalBM > 0) {
                sendNpcChat(NPC_ID, Expression.CALM_TALK, "You will get a total of " + Utils.formatNumber(totalBM) + " coins for your", "ancient artifacts. Do you wish to exchange them?");
                setPhase(17);
            } else {
                sendNpcChat(NPC_ID, Expression.CALM_TALK, "You do not have any ancient artifacts.");
                setPhase(6);
            }
        } else if (isPhase(17)) {
            sendOption(DEFAULT_OPTION_TITLE, "Yes", "No");
            setPhase(18);
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(1)) {
            if (option == 1) {
                //Target emblems
                sendPlayerChat(Expression.CALM_TALK, "I would like to exchange my Mysterious Emblems.");
                setPhase(1);
            } else if (option == 2) {
                //Ancient artifcats
                sendPlayerChat(Expression.CALM_TALK, "I would like to exchange my ancient artifacts.");
                setPhase(14);
            } else if (option == 3) {
                stop();
            }
        } else if (isPhase(5)) {
            if (option == 1) {
                sendNpcChat(NPC_ID, Expression.HAPPY, "I've traded your Mysterious Emblems for " + Utils.format(BountyHunter.exchange(player, true)) + " coins.");
                setPhase(6);
            } else if (option == 2) {
                player.clearAttrib(EMBLEM_WEALTH);
                stop();
            }
        } else if (isPhase(13)) {
            if (option == 1) {
                Skulling.assignSkullState(player, SkullType.WHITE_SKULL);
                sendItemStatement(new Item(553), "", "Your are now skulled.");
                setPhase(6);
            } else if (option == 2) {
                stop();
            }
        } else if (isPhase(18)) {
            if (option == 1) {
                sendNpcChat(NPC_ID, Expression.HAPPY, "I've traded your ancient artifacts for " + Utils.format(AncientArtifacts.exchange(player, true)) + " coins.");
                setPhase(6);
            } else if (option == 2) {
                stop();
            }
        }
    }

}
