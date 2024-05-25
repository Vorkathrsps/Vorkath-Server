package com.cryptic.model.inter.dialogue.records;

import com.cryptic.model.items.Item;

public record DialogueSingleItemRecord(Item item,String[] messages, boolean continueButton){}
