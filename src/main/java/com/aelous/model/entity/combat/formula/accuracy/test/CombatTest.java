package com.aelous.model.entity.combat.formula.accuracy.test;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.player.Player;
import com.aelous.network.Session;
import org.junit.Test;
import static org.junit.Assert.*;

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
