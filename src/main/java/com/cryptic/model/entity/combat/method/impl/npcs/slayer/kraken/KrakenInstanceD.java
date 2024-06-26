package com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken;

import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstancedAreaManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.BLOOD_MONEY;

/**
 * @author Origin
 * april 26, 2020
 */
public class KrakenInstanceD extends Dialogue {

    String currency = "BM";
    int currencyReq = 1000;

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.ITEM_STATEMENT, new Item(BLOOD_MONEY), "", "Would you like to pay the fee to enter an instanced area?", "<col=FF0000>Warning: any items dropped on death are permanently lost.", "When you leave, you'll have to pay again to enter.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            send(DialogueType.OPTION,DEFAULT_OPTION_TITLE, "Pay " + currencyReq + " " + currency + " to enter.", "Never mind.");
            setPhase(1);
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                boolean canEnter = false;
                int bmInInventory = player.inventory().count(BLOOD_MONEY);
                if (bmInInventory > 0) {
                    if(bmInInventory >= currencyReq) {
                        canEnter = true;
                        player.inventory().remove(BLOOD_MONEY, currencyReq);
                    }
                }

                if(!canEnter) {
                    player.message("You do not have enough BM to do this.");
                    stop();
                    return;
                }

                player.message("You pay " + currencyReq + " " + currency + " to enter an instance room.");
                var instance = InstancedAreaManager.getSingleton().createInstancedArea(new Area(2269, 10022, 2302, 10046));
                player.setInstancedArea(instance);
                player.teleport(new Tile(2280, 10022, instance.getzLevel()));
                NPC kraken = new NPC(KrakenBoss.KRAKEN_WHIRLPOOL, new Tile(2278, 10034, instance.getzLevel()));
                instance.addNpc(kraken);
                for (Tile tile : KrakenBoss.TENT_TILES) {
                    NPC tentacle = new NPC(KrakenBoss.TENTACLE_WHIRLPOOL, new Tile(tile.getX(), tile.getY(), instance.getzLevel()));
                    // tent Should respawn, if killed before boss is dead.
                    instance.addNpc(tentacle);
                    tentacle.spawn(false);
                    tentacle.putAttrib(AttributeKey.BOSS_OWNER, kraken);

                    List<NPC> list = kraken.getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
                    list.add(tentacle);
                    kraken.putAttrib(AttributeKey.MINION_LIST, list);
                }
                stop();
            } else if(option == 2) {
                stop();
            }
        }
    }
}
