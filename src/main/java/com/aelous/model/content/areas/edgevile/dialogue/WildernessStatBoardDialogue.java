package com.aelous.model.content.areas.edgevile.dialogue;

import com.aelous.GameServer;
import com.aelous.services.database.transactions.*;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;

/**
 * @author Patrick van Elderen | December, 09, 2020, 14:14
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WildernessStatBoardDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Toggle Kills/Deaths Overlay", "Reset K/D", "Nothing");
        setPhase(0);
    }

    @Override
    protected void select(int option) {
        if(isPhase(0)) {
            if(option == 1) {
                player.message("We do not yet have this feature.");
                stop();
            } else if(option == 2) {
                send(DialogueType.OPTION,"Are you sure you want to do this?", "Yes, reset my K/D and Kill streak for 1,000 BM.", "No.");
                setPhase(1);
            } else if(option == 3) {
                stop();
            }
        } else if(isPhase(1)) {
            if(option == 1) {
                boolean canReset = false;
                int bmInInventory = player.inventory().count(BLOOD_MONEY);
                if (bmInInventory > 0) {
                    if(bmInInventory >= 1000) {
                        canReset = true;
                        player.inventory().remove(BLOOD_MONEY, 1000);
                    }
                }

                if(!canReset) {
                    player.message("You do not have enough BM to do this.");
                    stop();
                    return;
                }

                // Store into all time record
                player.putAttrib(AttributeKey.ALLTIME_KILLS,player.<Integer>getAttribOr(AttributeKey.ALLTIME_KILLS,0) + player.<Integer>getAttribOr(AttributeKey.PLAYER_KILLS,0));
                player.putAttrib(AttributeKey.ALLTIME_DEATHS,player.<Integer>getAttribOr(AttributeKey.ALLTIME_DEATHS,0) + player.<Integer>getAttribOr(AttributeKey.PLAYER_DEATHS,0));

                // Reset current
                player.putAttrib(AttributeKey.PLAYER_KILLS,0);
                player.putAttrib(AttributeKey.PLAYER_DEATHS,0);
                player.putAttrib(AttributeKey.KILLSTREAK,0);
                if (GameServer.properties().enableSql) {
                    GameServer.getDatabaseService().submit(new UpdateKillsDatabaseTransaction(player.getAttribOr(AttributeKey.PLAYER_KILLS, 0), player.getUsername()));
                    GameServer.getDatabaseService().submit(new UpdateDeathsDatabaseTransaction(player.getAttribOr(AttributeKey.PLAYER_DEATHS, 0), player.getUsername()));
                    GameServer.getDatabaseService().submit(new UpdateKdrDatabaseTransaction(Double.parseDouble(player.getKillDeathRatio()), player.getUsername()));
                }
                player.message("You have reset your K/D and Kill streak for 1000 BM.");
                stop();
            } else if(option == 2) {
                stop();
            }
        }
    }
}
