package com.aelous.model.content.seasonal_events.rewards;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.items.Item;
import com.aelous.utility.CustomItemIdentifiers;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 14, 2021
 */
public enum EventRewards {

    EVENT_REWARD_1(new Item(ItemIdentifiers.CRYSTAL_KEY), 210, EVENT_REWARD_1_CLAIMED),
    EVENT_REWARD_2(new Item(CustomItemIdentifiers.SLAYER_KEY), 205, EVENT_REWARD_2_CLAIMED),
    EVENT_REWARD_4(new Item(CustomItemIdentifiers.WEAPON_MYSTERY_BOX), 195, EVENT_REWARD_4_CLAIMED),
    EVENT_REWARD_5(new Item(CustomItemIdentifiers.ARMOUR_MYSTERY_BOX), 190, EVENT_REWARD_5_CLAIMED),
    EVENT_REWARD_6(new Item(ItemIdentifiers.CHRISTMAS_CRACKER), 185, EVENT_REWARD_6_CLAIMED),
    EVENT_REWARD_7(new Item(ItemIdentifiers.SANTA_MASK), 180, EVENT_REWARD_7_CLAIMED),
    EVENT_REWARD_8(new Item(ItemIdentifiers.SANTA_JACKET), 175, EVENT_REWARD_8_CLAIMED),
    EVENT_REWARD_9(new Item(ItemIdentifiers.SANTA_PANTALOONS), 170, EVENT_REWARD_9_CLAIMED),
    EVENT_REWARD_10(new Item(ItemIdentifiers.SANTA_GLOVES), 165, EVENT_REWARD_10_CLAIMED),
    EVENT_REWARD_11(new Item(ItemIdentifiers.SANTA_BOOTS), 160, EVENT_REWARD_11_CLAIMED),
    EVENT_REWARD_12(new Item(ItemIdentifiers.PRESENT_13346), 155, EVENT_REWARD_12_CLAIMED),
    EVENT_REWARD_13(new Item(ItemIdentifiers.BERSERKER_RING_I), 150, EVENT_REWARD_13_CLAIMED),
    EVENT_REWARD_14(new Item(ItemIdentifiers.ARCHERS_RING_I), 145, EVENT_REWARD_14_CLAIMED),
    EVENT_REWARD_15(new Item(ItemIdentifiers.SEERS_RING_I), 140, EVENT_REWARD_15_CLAIMED),
    EVENT_REWARD_16(new Item(ItemIdentifiers.WARRIOR_RING_I), 135, EVENT_REWARD_16_CLAIMED),
    EVENT_REWARD_17(new Item(ItemIdentifiers.CHRISTMAS_CRACKER), 130, EVENT_REWARD_17_CLAIMED),
    EVENT_REWARD_18(new Item(ItemIdentifiers.FREMENNIK_KILT), 125, EVENT_REWARD_18_CLAIMED),
    EVENT_REWARD_19(new Item(ItemIdentifiers.SARADOMIN_GODSWORD), 120, EVENT_REWARD_19_CLAIMED),
    EVENT_REWARD_20(new Item(ItemIdentifiers.ZAMORAK_GODSWORD), 115, EVENT_REWARD_20_CLAIMED),
    EVENT_REWARD_21(new Item(ItemIdentifiers.BANDOS_GODSWORD), 110, EVENT_REWARD_21_CLAIMED),
    EVENT_REWARD_22(new Item(ItemIdentifiers.ARMADYL_GODSWORD), 105, EVENT_REWARD_22_CLAIMED),
    EVENT_REWARD_23(new Item(ItemIdentifiers.BANDOS_CHESTPLATE), 100, EVENT_REWARD_23_CLAIMED),
    EVENT_REWARD_24(new Item(ItemIdentifiers.BANDOS_TASSETS), 95, EVENT_REWARD_24_CLAIMED),
    EVENT_REWARD_25(new Item(CustomItemIdentifiers.PET_MYSTERY_BOX), 90, EVENT_REWARD_25_CLAIMED),
    //EVENT_REWARD_26(new Item(CustomItemIdentifiers.BLOOD_MONEY_CASKET), 85, EVENT_REWARD_26_CLAIMED),
    //EVENT_REWARD_27(new Item(CustomItemIdentifiers.BLOOD_MONEY_CASKET), 80, EVENT_REWARD_27_CLAIMED),
    EVENT_REWARD_28(new Item(ItemIdentifiers.CHRISTMAS_CRACKER), 75, EVENT_REWARD_28_CLAIMED),
    EVENT_REWARD_29(new Item(ItemIdentifiers.LARRANS_KEY), 70, EVENT_REWARD_29_CLAIMED),
    EVENT_REWARD_30(new Item(ItemIdentifiers.LARRANS_KEY), 65, EVENT_REWARD_30_CLAIMED),
    EVENT_REWARD_31(new Item(ItemIdentifiers.LARRANS_KEY), 60, EVENT_REWARD_31_CLAIMED),
    EVENT_REWARD_34(new Item(CustomItemIdentifiers.WEAPON_MYSTERY_BOX), 45, EVENT_REWARD_34_CLAIMED),
    EVENT_REWARD_35(new Item(CustomItemIdentifiers.ARMOUR_MYSTERY_BOX), 40, EVENT_REWARD_35_CLAIMED),
    EVENT_REWARD_36(new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX), 35, EVENT_REWARD_36_CLAIMED),
    EVENT_REWARD_37(new Item(CustomItemIdentifiers.LEGENDARY_MYSTERY_BOX), 30, EVENT_REWARD_37_CLAIMED),
    EVENT_REWARD_38(new Item(CustomItemIdentifiers.MYSTERY_TICKET), 25, EVENT_REWARD_38_CLAIMED),
    EVENT_REWARD_39(new Item(ItemIdentifiers.CHRISTMAS_CRACKER), 20, EVENT_REWARD_39_CLAIMED),
    EVENT_REWARD_40(new Item(ItemIdentifiers.SANTA_HAT), 15, EVENT_REWARD_40_CLAIMED),
    EVENT_REWARD_42(new Item(CustomItemIdentifiers.LEGENDARY_MYSTERY_BOX), 5, EVENT_REWARD_42_CLAIMED),
    EVENT_REWARD_43(new Item(ItemIdentifiers.PRESENT_13346), 2, EVENT_REWARD_43_CLAIMED)
    ;

    public final Item reward;
    public final int chance;
    public final AttributeKey key;

    EventRewards(Item reward, int chance, AttributeKey key) {
        this.reward = reward;
        this.chance = chance;
        this.key = key;
    }
}
