package com.cryptic.model.inter.dialogue.records;

import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.model.items.Item;

public record DialogueDestroyItemRecord(Item item, String title, String note){}
