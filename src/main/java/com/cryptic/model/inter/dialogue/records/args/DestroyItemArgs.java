package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.items.Item;

public record DestroyItemArgs(Item item, String title, String note){}
