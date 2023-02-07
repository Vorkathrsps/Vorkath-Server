package com.aelous.utility.test.unit;

import com.aelous.GameServer;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.save.PlayerSave;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Shadowrs/Jak
 * @version 6/6/2020
 *
 * Same as {@link com.aelous.utility.test.generic.PlayerProfileVerf} except this is a unit test, build to fail on purpose for proof on concept.
 * <br>Do NOT run on startup.
 */
public class PlayerSerializeVerfFailTest {
    @Test
    public void playerCreateSaveLoadTest() {

        GameServer.properties(); // static {} block init
        // now fuck about

        //Test brand new player.
        Player player = new Player();
        player.setUsername("saving_test_app_startup");
        player.setNewPassword("");
        //This is for testing the incorrect attribute error, leave this commented out.
        //player.putAttrib(AttributeKey.BGS_GFX_GOLD, 1);
        //System.out.println("saving");
        boolean successfulSave = false;
        try {
            new PlayerSave.SaveDetails(player);
            successfulSave = true;
        } catch (Exception e) {
            System.err.println("Player serialization -- integrity fail!");
            e.printStackTrace();
        }
        assertTrue(successfulSave);

        boolean successfulLoad = false;
        //System.out.println("loading");
        try {
            PlayerSave.load(player);
            successfulLoad = true;
            // loading wont have any assoc exceptions in production because it still uses PI' playervariables where all vars are stored in one class
            // with their type, no way to CCE, compiler will enforce types.
        } catch (Exception e) {
            System.err.println("Player serialization -- integrity fail!");
            e.printStackTrace();
        }
        assertTrue(successfulLoad);

        //Test existing player.
        Player player2 = new Player();
        player2.setUsername("zoo");
        successfulLoad = false;
        try {
            PlayerSave.loadOfflineWithoutPassword(player2);
            successfulLoad = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(successfulLoad);

        //This is for testing the incorrect attribute error, leave this commented out.
        //player2.putAttrib(AttributeKey.BGS_GFX_GOLD, 1);
        //This is for testing that the loading of the account worked.
        //System.out.println("Bank items for " + player2.getUsername() + " are " + Arrays.toString(player2.getBank().toNonNullArray()));

        successfulSave = false;
        try {
            new PlayerSave.SaveDetails(player2);
            successfulSave = true;
        } catch (Exception e) {
            System.err.println("Player serialization -- integrity fail!");
            e.printStackTrace();
        }
        assertTrue(successfulSave);

        successfulLoad = false;
        //System.out.println("loading");
        try {
            PlayerSave.load(player2);
            successfulLoad = true;
            // loading wont have any assoc exceptions in production because it still uses PI' playervariables where all vars are stored in one class
            // with their type, no way to CCE, compiler will enforce types.
        } catch (Exception e) {
            System.err.println("Player serialization -- integrity fail!");
            e.printStackTrace();
        }
        assertTrue(successfulLoad);
    }
}
