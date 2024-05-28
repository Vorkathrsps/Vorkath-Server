package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.inter.dialogue.Expression;

public record NpcArgs(int npcId, String title, String[] chats, Expression expression, boolean continueButtons) {

}
