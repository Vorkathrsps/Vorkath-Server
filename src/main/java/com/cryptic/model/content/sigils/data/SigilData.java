package com.cryptic.model.content.sigils.data;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;

public enum SigilData {
    FERAL_FIGHTER(ItemIdentifiers.SIGIL_OF_THE_FERAL_FIGHTER_26075, ItemIdentifiers.SIGIL_OF_THE_FERAL_FIGHTER, AttributeKey.FERAL_FIGHTER),
    MENACING_MAGE(ItemIdentifiers.SIGIL_OF_THE_MENACING_MAGE_26078, ItemIdentifiers.SIGIL_OF_THE_MENACING_MAGE, AttributeKey.MENACING_MAGE),
    RUTHLESS_RANGER(ItemIdentifiers.SIGIL_OF_THE_RUTHLESS_RANGER_26072, ItemIdentifiers.SIGIL_OF_THE_RUTHLESS_RANGER, AttributeKey.RUTHLESS_RANGER),
    DEFT_STRIKES(ItemIdentifiers.SIGIL_OF_DEFT_STRIKES_26012, ItemIdentifiers.SIGIL_OF_DEFT_STRIKES, AttributeKey.DEFT_STRIKES),
    METICULOUS_MAGE(ItemIdentifiers.SIGIL_OF_THE_METICULOUS_MAGE_26003, ItemIdentifiers.SIGIL_OF_THE_METICULOUS_MAGE, AttributeKey.METICULOUS_MAGE),
    CONSISTENCY(ItemIdentifiers.SIGIL_OF_CONSISTENCY_25994, ItemIdentifiers.SIGIL_OF_CONSISTENCY, AttributeKey.CONSISTENCY),
    FORMIDABLE_FIGHTER(ItemIdentifiers.SIGIL_OF_THE_FORMIDABLE_FIGHTER_25997, ItemIdentifiers.SIGIL_OF_THE_FORMIDABLE_FIGHTER, AttributeKey.FORMIDABLE_FIGHTER),
    RESISTANCE(ItemIdentifiers.SIGIL_OF_RESISTANCE_28490, ItemIdentifiers.SIGIL_OF_RESISTANCE, AttributeKey.RESISTANCE),
    PRECISION(ItemIdentifiers.SIGIL_OF_PRECISION_28514, ItemIdentifiers.SIGIL_OF_PRECISION, AttributeKey.PRECISION),
    FORTIFICATION(ItemIdentifiers.SIGIL_OF_FORTIFICATION_26006, ItemIdentifiers.SIGIL_OF_FORTIFICATION, AttributeKey.SIGIL_OF_FORTIFICATION);
    public final int unattuned;
    public final int attuned;
    public final AttributeKey attributeKey;

    SigilData(int unattuned, int attuned, AttributeKey attributeKey) {
        this.unattuned = unattuned;
        this.attuned = attuned;
        this.attributeKey = attributeKey;
    }
}
