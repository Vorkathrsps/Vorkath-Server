package com.aelous.model.content.mechanics.promo;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.CustomItemIdentifiers;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * This class represents the server wide promo's for reaching X amount of donations.
 *
 * @author Patrick van Elderen | January, 19, 2021, 18:36
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class PaymentPromo {

    private static final int WIDGET = 27400;

    private enum PromoItems {
        FIRST_PROMO_UNLOCK(27415, 27411, new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX), 333, 25),
        SECOND_PROMO_UNLOCK(27416, 27412, new Item(CustomItemIdentifiers.PET_MYSTERY_BOX), 334, 100),
        THIRD_PROMO_UNLOCK(27417, 27413, new Item(CustomItemIdentifiers.LEGENDARY_MYSTERY_BOX), 335, 250),
        FIFTH_PROMO_UNLOCK(27415, 27411, new Item(CustomItemIdentifiers.GRAND_MYSTERY_BOX), 333, 1000),
        SIXTH_PROMO_UNLOCK(27416, 27412, new Item(ItemIdentifiers.CHRISTMAS_CRACKER), 334, 1500),
        EIGHTH_PROMO_UNLOCK(27418, 27414, new Item(CustomItemIdentifiers.RAIDS_MYSTERY_BOX), 336, 2500),
        NINTH_PROMO_UNLOCK(27415, 27411, new Item(CustomItemIdentifiers.MYSTERY_CHEST), 333, 3500),
        ELEVENTH_PROMO_UNLOCK(27417, 27413, new Item(CustomItemIdentifiers.MYSTERY_CHEST,2), 335, 7000),
        TWELFTH_PROMO_UNLOCK(27418, 27414, new Item(CustomItemIdentifiers.MYSTERY_CHEST,3), 336, 10000);

        private final int itemSlot;
        private final int costStringId;
        private final Item item;
        private final int config;
        private final int amountRequiredToUnlock;

        PromoItems(int itemSlot, int costStringId, Item item, int config, int amountRequiredToUnlock) {
            this.itemSlot = itemSlot;
            this.costStringId = costStringId;
            this.item = item;
            this.config = config;
            this.amountRequiredToUnlock = amountRequiredToUnlock;
        }
    }

    /**
     * The player instance of this class
     */
    private final Player player;

    /**
     * The constructor of this class
     *
     * @param player The player
     */
    public PaymentPromo(Player player) {
        this.player = player;
    }

    public void open() {
        player.getInterfaceManager().open(WIDGET);

        //Send current promos
        player.getPacketSender().sendString(27407, "The following promo is always available:<br>Buy two of one item you will get a third free.<br>This promo will never expire.");
        player.getPacketSender().sendString(27408, "");

        //Send total amount frame
        player.getPacketSender().sendString(27410, "$" + player.<Double>getAttribOr(PROMO_PAYMENT_AMOUNT, 0D) + "0");

        int sentCount = 0;
        for (int i = player.getAttribOr(PROMO_ITEMS_UNLOCKED,0); i < PromoItems.values().length; i++) {
            final PromoItems promoItems = PromoItems.values()[i];
            if (sentCount++ > 3)
                break;
            player.getPacketSender().sendString(promoItems.costStringId, promoItems.amountRequiredToUnlock + "$");
            player.getPacketSender().sendConfig(promoItems.config, player.<Double>getAttribOr(PROMO_PAYMENT_AMOUNT, 0D) >= promoItems.amountRequiredToUnlock ? 1 : 0);
            player.getPacketSender().sendItemOnInterfaceSlot(promoItems.itemSlot, promoItems.item, 0);
        }
    }

    public void checkForPromoReward(double increaseForPromo) {
        final double before = player.<Double>getAttribOr(PROMO_PAYMENT_AMOUNT, 0D);
        player.putAttrib(PROMO_PAYMENT_AMOUNT, before + increaseForPromo);
        final double donated = player.<Double>getAttribOr(PROMO_PAYMENT_AMOUNT, 0D);

        int latestUnlockIndex = 0;
        for (int i = 0; i < PromoItems.values().length; i++) {
            if (before >= PromoItems.values()[i].amountRequiredToUnlock)
                continue; // ignore these, they've been previous unlocked by this method
            if (donated >= PromoItems.values()[i].amountRequiredToUnlock) {
                final PromoItems promoItems = PromoItems.values()[i];
                // give item
                player.inventory().addOrBank(promoItems.item);
                latestUnlockIndex = i;
            } else {
                break; // finished with relevant entries
            }
        }
        player.putAttrib(PROMO_ITEMS_UNLOCKED, latestUnlockIndex);

        int section = (latestUnlockIndex+1) / 4; // 4 shown on interface, split the enum up into 4 chunks
        // 3+1/4 = 1, section 1 is indices 4-7

        for (int i = section * 4; i < (section*4) + 4; i++) {
            if (i >= PromoItems.values().length) break;
            final PromoItems i2 = PromoItems.values()[i];
            player.getPacketSender().sendString(i2.costStringId, i2.amountRequiredToUnlock + "$");
            player.getPacketSender().sendConfig(i2.config, player.<Double>getAttribOr(PROMO_PAYMENT_AMOUNT, 0D) >= i2.amountRequiredToUnlock ? 1 : 0);
            player.getPacketSender().sendItemOnInterfaceSlot(i2.itemSlot, i2.item, 0);
        }
    }

}
