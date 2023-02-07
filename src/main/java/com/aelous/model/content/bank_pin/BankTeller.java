package com.aelous.model.content.bank_pin;

import com.aelous.model.content.bank_pin.dialogue.BankTellerDialogue;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;

import java.util.function.Predicate;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class BankTeller {

    public static Runnable bankerDialogue(Player player, NPC npc) {
        if (npc.def().name != null && npc.def().name.equals("Banker") && isNearBank(npc)) {
            return () -> player.getDialogueManager().start(new BankTellerDialogue(), npc);
        }
        return null;
    }

    private static boolean isNearBank(NPC npc) {

        Predicate<GameObject> isBankBooth = obj -> "Bank booth".equals(obj.definition().name);
        Tile north = npc.tile().transform(0, 1);
        if (MapObjects.get(isBankBooth, north).isPresent()) {
            return true;
        }
        Tile west = npc.tile().transform(-1, 0);
        if (MapObjects.get(isBankBooth, west).isPresent()) {
            return true;
        }
        Tile east = npc.tile().transform(1, 0);
        if (MapObjects.get(isBankBooth, east).isPresent()) {
            return true;
        }
        Tile south = npc.tile().transform(0, -1);
        if (MapObjects.get(isBankBooth, south).isPresent()) {
            return true;
        }
        return false;
    }
}
