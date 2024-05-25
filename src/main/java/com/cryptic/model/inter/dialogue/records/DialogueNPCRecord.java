package com.cryptic.model.inter.dialogue.records;

import com.cryptic.model.inter.dialogue.Expression;

public record DialogueNPCRecord(int npcId, String title, String[] chats, Expression expression, boolean continueButtons) {

}
