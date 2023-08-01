package com.cryptic.model.entity.combat.formula.accuracy.test;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.Session;
import org.junit.Test;

public class CombatTest {

    @Test
    public void testSuccessful() {
        Entity attacker = new Player(new Session(null));
        Entity defender = new Player(new Session(null));
        CombatType style = CombatType.MELEE;

        //boolean success = MeleeAccuracy.successful(attacker, defender, style);

       // assertNotNull(success);
        // Add more relevant assertions here based on your use case
    }
}
