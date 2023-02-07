package com.aelous.utility.test.generic;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.save.PlayerSave;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Shadowrs/Jak
 * @version 6/6/2020
 */
public class PlayerProfileVerf {
    private static final Logger logger = LogManager.getLogger(PlayerProfileVerf.class);
    /**
     * Purposefully kills the server if exceptions in player serialization. Fix it before you can run the server.
     */
    public static void verifyIntegrity() {
        Player player = new Player();
        player.setUsername("saving_test_app_startup");
        player.setNewPassword("");
        try {
            new PlayerSave.SaveDetails(player);
            PlayerSave.load(player);
        } catch (Exception e) {
            logger.error("Player serialization -- integrity fail!");
            logger.catching(e);
            System.exit(1);
        }
    }
}
