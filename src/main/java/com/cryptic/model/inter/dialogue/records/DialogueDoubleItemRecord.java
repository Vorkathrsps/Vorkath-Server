package com.cryptic.model.inter.dialogue.records;

import com.cryptic.model.items.Item;

public record DialogueDoubleItemRecord(Item firstItem, Item secondItem, String[] messages, boolean continueButtons){}
