package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.items.Item;

public record DoubleItemArgs(Item firstItem, Item secondItem, String[] messages, boolean continueButtons){}
