package com.aelous.model.content.items;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;

import static com.aelous.model.entity.attributes.AttributeKey.RIGOUR;
import static com.aelous.utility.ItemIdentifiers.DEXTEROUS_PRAYER_SCROLL;

/**
 * @author Patrick van Elderen | April, 29, 2021, 13:55
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DexterousPrayerScroll extends PacketInteraction {

    private static class DexterousPrayerScrollDialogue extends Dialogue {

        @Override
        protected void start(Object... parameters) {
            send(DialogueType.ITEM_STATEMENT,new Item(DEXTEROUS_PRAYER_SCROLL), "", "You can make out some faded words on the ancient", "parchment. It appears to be an archaic invocation of the", "gods! Would you like to absorb its power?");
            setPhase(0);
        }

        @Override
        protected void next() {
            if(isPhase(0)) {
                send(DialogueType.OPTION, "This will consume the scroll.", "Learn to Rigour", "Cancel");
                setPhase(1);
            }
            if(isPhase(2)) {
                stop();
            }
        }

        @Override
        protected void select(int option) {
            if(isPhase(1)) {
                if(option == 1) {
                    if(!player.inventory().contains(DEXTEROUS_PRAYER_SCROLL)) {
                        stop();
                        return;
                    }
                    player.inventory().remove(new Item(DEXTEROUS_PRAYER_SCROLL),true);
                    player.putAttrib(RIGOUR,true);
                    player.getPacketSender().sendConfig(711, 1);
                    send(DialogueType.ITEM_STATEMENT,new Item(DEXTEROUS_PRAYER_SCROLL), "", "You study the scroll and learn a new prayer: "+ Color.DARK_RED.wrap("Rigour"));
                    setPhase(2);
                }
                if(option == 2) {
                    stop();
                }
            }
        }
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 1) {
            if(item.getId() == DEXTEROUS_PRAYER_SCROLL) {
                if (player.inventory().contains(DEXTEROUS_PRAYER_SCROLL)) {
                    if(player.<Boolean>getAttribOr(RIGOUR,false)) {
                        player.message(Color.RED.wrap("You have already learnt the ways of Rigour."));
                        return true;
                    }
                    player.getDialogueManager().start(new DexterousPrayerScrollDialogue());
                }
                return true;
            }
        }
        return false;
    }
}
