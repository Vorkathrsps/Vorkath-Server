package com.cryptic.model.content.sigils.data;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.content.sigils.combat.*;
import com.cryptic.model.content.sigils.misc.Stamina;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;

public enum SigilData {
    FERAL_FIGHTER(ItemIdentifiers.SIGIL_OF_THE_FERAL_FIGHTER_26075, ItemIdentifiers.SIGIL_OF_THE_FERAL_FIGHTER, AttributeKey.FERAL_FIGHTER, 1970, FeralFighter.class),
    MENACING_MAGE(ItemIdentifiers.SIGIL_OF_THE_MENACING_MAGE_26078, ItemIdentifiers.SIGIL_OF_THE_MENACING_MAGE, AttributeKey.MENACING_MAGE, 1970, MenacingMage.class),
    RUTHLESS_RANGER(ItemIdentifiers.SIGIL_OF_THE_RUTHLESS_RANGER_26072, ItemIdentifiers.SIGIL_OF_THE_RUTHLESS_RANGER, AttributeKey.RUTHLESS_RANGER, 1970, RuthlessRanger.class),
    DEFT_STRIKES(ItemIdentifiers.SIGIL_OF_DEFT_STRIKES_26012, ItemIdentifiers.SIGIL_OF_DEFT_STRIKES, AttributeKey.DEFT_STRIKES, 1970, DeftStrikes.class),
    METICULOUS_MAGE(ItemIdentifiers.SIGIL_OF_THE_METICULOUS_MAGE_26003, ItemIdentifiers.SIGIL_OF_THE_METICULOUS_MAGE, AttributeKey.METICULOUS_MAGE, 1970, MeticulousMage.class),
    CONSISTENCY(ItemIdentifiers.SIGIL_OF_CONSISTENCY_25994, ItemIdentifiers.SIGIL_OF_CONSISTENCY, AttributeKey.CONSISTENCY, 1970, Consistency.class),
    FORMIDABLE_FIGHTER(ItemIdentifiers.SIGIL_OF_THE_FORMIDABLE_FIGHTER_25997, ItemIdentifiers.SIGIL_OF_THE_FORMIDABLE_FIGHTER, AttributeKey.FORMIDABLE_FIGHTER, 1970, FormidableFighter.class),
    RESISTANCE(ItemIdentifiers.SIGIL_OF_RESISTANCE_28490, ItemIdentifiers.SIGIL_OF_RESISTANCE, AttributeKey.RESISTANCE, 1970, Resistance.class),
    PRECISION(ItemIdentifiers.SIGIL_OF_PRECISION_28514, ItemIdentifiers.SIGIL_OF_PRECISION, AttributeKey.PRECISION, 1970, Precision.class),
    FORTIFICATION(ItemIdentifiers.SIGIL_OF_FORTIFICATION_26006, ItemIdentifiers.SIGIL_OF_FORTIFICATION, AttributeKey.SIGIL_OF_FORTIFICATION, 1970, Fortification.class),
    STAMINA(ItemIdentifiers.SIGIL_OF_STAMINA_26042, ItemIdentifiers.SIGIL_OF_STAMINA, AttributeKey.SIGIL_OF_STAMINA, 1971, Stamina.class);
    public final int unattuned;
    public final int attuned;
    public final int graphic;
    public final Class<? extends AbstractSigil> handler;

    public final AttributeKey attributeKey;

    SigilData(int unattuned, int attuned, AttributeKey attributeKey, int graphic, Class<? extends AbstractSigil> handler) {
        this.unattuned = unattuned;
        this.attuned = attuned;
        this.attributeKey = attributeKey;
        this.graphic = graphic;
        this.handler = handler;
    }
}
