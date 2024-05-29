package com.cryptic.model.inter.lootkeys;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.utility.Utils;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.cryptic.model.entity.attributes.AttributeKey.LOOT_KEYS_LOOTED;
import static com.cryptic.model.entity.attributes.AttributeKey.TOTAL_LOOT_KEYS_VALUE;

public class SkullyValueD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        int keysLooted = player.<Integer>getAttribOr(LOOT_KEYS_LOOTED, 0);
        if(keysLooted >= 1) {
            long totalLootKeysValue = player.<Long>getAttribOr(TOTAL_LOOT_KEYS_VALUE, 0D);
            sendNpcChat(SKULLY, Expression.NODDING_ONE, "You've claimed "+keysLooted+" keys, containing loot worth about "+ Utils.formatRunescapeStyle(totalLootKeysValue)+"gp.");
            setPhase(0);
        } else {
            sendNpcChat(SKULLY, Expression.NODDING_ONE, "Well, seeing as you haven't claimed a key yet, I reckon", "you've claimed a total of 0gp worth of loot.");
            setPhase(0);
        }
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            stop();
        }
    }
}
