package com.aelous.model.inter.lootkeys;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.utility.Utils;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.aelous.model.entity.attributes.AttributeKey.LOOT_KEYS_LOOTED;
import static com.aelous.model.entity.attributes.AttributeKey.TOTAL_LOOT_KEYS_VALUE;

public class SkullyValueD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        int keysLooted = player.<Integer>getAttribOr(LOOT_KEYS_LOOTED, 0);
        if(keysLooted >= 1) {
            long totalLootKeysValue = player.<Long>getAttribOr(TOTAL_LOOT_KEYS_VALUE, 0D);
            send(DialogueType.NPC_STATEMENT, SKULLY, 554, "You've claimed "+keysLooted+" keys, containing loot worth about "+ Utils.formatRunescapeStyle(totalLootKeysValue)+"gp.");
            setPhase(0);
        } else {
            send(DialogueType.NPC_STATEMENT, SKULLY, 554, "Well, seeing as you haven't claimed a key yet, I reckon", "you've claimed a total of 0gp worth of loot.");
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
