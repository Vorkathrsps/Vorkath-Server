package com.aelous.model.content.skill.impl.farming;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Varbit;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

/**
 * Created by Situations on 2016-06-15.
 */
public class CompostBin extends PacketInteraction {

    private static final int FALADOR_BIN = 7836;
    private static final int WEEDS = 6055;
    private static final int BUCKET = 1925;

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
       if(object.getId() == FALADOR_BIN) {
           var amt = player.inventory().count(WEEDS);

           if (item.getId() == WEEDS) {
               if (player.getTimers().has(TimerKey.COMPOST_BIN)) {
                   return true;
               }

               if (compostBinFull(player)) {
                   player.messageBox("The compost bin is too full to put anything else in it.");
                   return true;
               }

               if (player.inventory().count(WEEDS) - player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS) > 15) {
                   amt = 15 - player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS);
               }

               player.lock();
               var removed = player.inventory().remove(new Item(WEEDS, amt),true);
               if (removed) {
                   final var weedsRemoved = amt;
                   Chain.bound(null).runFn(1, () -> {
                       player.animate(832);
                       player.message("You fill the compost bin with weeds.");
                       player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS,player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS) + weedsRemoved);
                   });
               }
               player.unlock();
           } else if (item.getId() == BUCKET) {
               if (!readyToCollect(player)) {
                   player.message("Nothing interesting happens.");
                   return true;
               }
           } else {
               player.message("Nothing interesting happens.");
           }
           return true;
       }

        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(object.getId() == FALADOR_BIN) {
            var can_collect = player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS);
            var compost_to_collect = player.<Integer>getAttribOr(AttributeKey.COMPOST_BIN, 0);

            if (option == 1) {
                if (compostBinFull(player)) {
                    if (compost_to_collect == 0) {
                        if (!player.getTimers().has(TimerKey.COMPOST_BIN) && can_collect == 0) {
                            player.lock();
                            player.animate(810);
                            Farming.setTimer(player, TimerKey.COMPOST_BIN, 20);
                            player.putAttrib(AttributeKey.COMPOST_BIN, 15);
                            player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_TYPE, 17);
                            player.unlock();
                        } else if (compost_to_collect == 15) {
                            player.message("gundrilla#1");
                        }
                    } else {
                        player.messageBox("The vegetation hasn't finished rotting yet.");
                    }
                }
            }
            if (option == 4) {
                player.optionsTitled("Dump the entire contents of the bin?", "Yes, throw it all away.", "No, keep it.", () -> {
                    player.lock();
                    player.animate(832);
                    Chain.bound(null).runFn(1, () -> {
                        player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS, 0);
                        player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_TYPE, 0);
                        player.putAttrib(AttributeKey.COMPOST_BIN, 0);
                        player.unlock();
                    });
                });
            }
            return true;
        }

        //TODO Ask Jak how to port this
        /*r.onTimer(TimerKey.COMPOST_BIN) @Suspendable {
            it.player().varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS, 14)
            it.message("The compost has finished.")
        }*/
        return false;
    }

    private boolean compostBinFull(Player player) {
        if (player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS) == 15)
            return true; else return false;
    }

    private boolean readyToCollect(Player player) {
        if (player.varps().varbit(Varbit.FALADOR_COMPOST_BIN_FULLNESS) == 21)
            return true; else return false;
    }
}
