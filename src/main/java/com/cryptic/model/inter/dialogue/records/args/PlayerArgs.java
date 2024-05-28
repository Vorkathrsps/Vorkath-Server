package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.inter.dialogue.Expression;

public record PlayerArgs(String title, String[] chats, Expression expression, boolean continueButtons) {
}
