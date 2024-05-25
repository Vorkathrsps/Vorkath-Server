package com.cryptic.model.inter.dialogue.records;

import com.cryptic.model.inter.dialogue.Expression;

public record DialoguePlayerRecord(String title, String[] chats, Expression expression, boolean continueButtons){}
