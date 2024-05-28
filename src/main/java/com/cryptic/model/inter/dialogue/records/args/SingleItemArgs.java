package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.items.Item;

public record SingleItemArgs(Item item, String[] messages, boolean continueButton) {

}
