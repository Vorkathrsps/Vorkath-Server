package com.cryptic.model.content.items.mysterybox.impl;

import com.cryptic.model.content.items.mysterybox.MboxItem;
import com.cryptic.model.content.items.mysterybox.MysteryBox;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cryptic.utility.ItemIdentifiers.*;

public class DonatorMysteryBox extends MysteryBox {
    @Override
    protected String name() {
        return "Mystery box";
    }

    @Override
    public int mysteryBoxId() {
        return MYSTERY_BOX;
    }

    private static final int EXTREME_ROLL = 100;
    private static final int RARE_ROLL = 45;
    private static final int UNCOMMON_ROLL = 25;

    private static final MboxItem[] EXTREMELY_RARE = new MboxItem[]{
        new MboxItem(INFERNAL_CAPE).broadcastWorldMessage(true),
        new MboxItem(ELYSIAN_SPIRIT_SHIELD).broadcastWorldMessage(true),
        new MboxItem(ARCANE_SPIRIT_SHIELD).broadcastWorldMessage(true),
        new MboxItem(SPECTRAL_SPIRIT_SHIELD).broadcastWorldMessage(true),
        new MboxItem(BLUE_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(YELLOW_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(PURPLE_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(RED_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(GREEN_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(BLACK_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(RAINBOW_PARTYHAT).broadcastWorldMessage(true),
        new MboxItem(SANTA_HAT).broadcastWorldMessage(true),
        new MboxItem(BLACK_HWEEN_MASK).broadcastWorldMessage(true),
        new MboxItem(GREEN_HALLOWEEN_MASK).broadcastWorldMessage(true),
        new MboxItem(RED_HALLOWEEN_MASK).broadcastWorldMessage(true),
        new MboxItem(BLUE_HALLOWEEN_MASK).broadcastWorldMessage(true),
        new MboxItem(CHRISTMAS_CRACKER).broadcastWorldMessage(true)
    };

    private static final MboxItem[] RARE = new MboxItem[]{
        new MboxItem(SERPENTINE_HELM).broadcastWorldMessage(true),
        new MboxItem(BANDOS_CHESTPLATE).broadcastWorldMessage(true),
        new MboxItem(BANDOS_TASSETS).broadcastWorldMessage(true),
        new MboxItem(ARMADYL_CHESTPLATE).broadcastWorldMessage(true),
        new MboxItem(ARMADYL_CHAINSKIRT).broadcastWorldMessage(true),
        new MboxItem(ARMADYL_GODSWORD).broadcastWorldMessage(true),
        new MboxItem(BANDOS_GODSWORD).broadcastWorldMessage(true),
        new MboxItem(ZAMORAK_GODSWORD).broadcastWorldMessage(true),
        new MboxItem(SARADOMIN_GODSWORD).broadcastWorldMessage(true),
        new MboxItem(ARMADYL_CROSSBOW).broadcastWorldMessage(true),
        new MboxItem(TOXIC_BLOWPIPE).broadcastWorldMessage(true),
        new MboxItem(TANZANITE_MUTAGEN).broadcastWorldMessage(true),
        new MboxItem(MAGMA_MUTAGEN).broadcastWorldMessage(true),
        new MboxItem(DRAGONFIRE_WARD).broadcastWorldMessage(true),
        new MboxItem(DRAGON_WARHAMMER).broadcastWorldMessage(true),
        new MboxItem(PEGASIAN_BOOTS).broadcastWorldMessage(true),
        new MboxItem(PRIMORDIAL_BOOTS).broadcastWorldMessage(true),
        new MboxItem(ETERNAL_BOOTS).broadcastWorldMessage(true),
        new MboxItem(TOXIC_STAFF_OF_THE_DEAD).broadcastWorldMessage(true)
     };

    private static final MboxItem[] UNCOMMON = new MboxItem[]{
        new MboxItem(OCCULT_NECKLACE),
        new MboxItem(STAFF_OF_LIGHT),
        new MboxItem(STAFF_OF_THE_DEAD),
        new MboxItem(BRIMSTONE_RING),
        new MboxItem(INFINITY_BOOTS),
        new MboxItem(FIGHTER_TORSO),
        new MboxItem(FIGHTER_HAT),
        new MboxItem(RUNE_POUCH),
        new MboxItem(FIRE_CAPE),
        new MboxItem(DRAGON_DEFENDER),
        new MboxItem(BERSERKER_RING_I),
        new MboxItem(ARCHERS_RING_I),
        new MboxItem(SEERS_RING_I)
    };

    private static final MboxItem[] COMMON = new MboxItem[]{
        new MboxItem(AHRIMS_ARMOUR_SET),
        new MboxItem(KARILS_ARMOUR_SET),
        new MboxItem(DHAROKS_ARMOUR_SET),
        new MboxItem(GUTHANS_ARMOUR_SET),
        new MboxItem(VERACS_ARMOUR_SET),
        new MboxItem(DRAGON_BOOTS),
        new MboxItem(BERSERKER_RING),
        new MboxItem(SEERS_RING),
        new MboxItem(ARCHERS_RING),
        new MboxItem(DARK_BOW),
        new MboxItem(ABYSSAL_WHIP),
        new MboxItem(AMULET_OF_FURY),
        new MboxItem(DRAGON_BONES + 1, 100),
        new MboxItem(LAVA_DRAGON_BONES + 1, 50)
    };

    private MboxItem[] allRewardsCached;

    public MboxItem[] allPossibleRewards() {
        if (allRewardsCached == null) {
            ArrayList<MboxItem> mboxItems = new ArrayList<>();
            mboxItems.addAll(Arrays.asList(EXTREMELY_RARE));
            mboxItems.addAll(Arrays.asList(RARE));
            mboxItems.addAll(Arrays.asList(UNCOMMON));
            mboxItems.addAll(Arrays.asList(COMMON));
            allRewardsCached = mboxItems.toArray(new MboxItem[0]);
        }
        return allRewardsCached;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.REGULAR_MYSTERY_BOXES_OPENED;
    }

    @Override
    public MboxItem rollReward() {
        if (Utils.rollPercent(5)) {
            return Utils.randomElement(EXTREMELY_RARE);
        } else if (Utils.rollPercent(10)) {
            return Utils.randomElement(RARE);
        } else if (Utils.rollPercent(35)) {
            return Utils.randomElement(UNCOMMON);
        } else {
            return Utils.randomElement(COMMON);
        }
    }
}
