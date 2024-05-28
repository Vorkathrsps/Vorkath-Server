package com.cryptic.model.inter.dialogue.records;

import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.model.inter.dialogue.records.args.*;
import com.cryptic.model.items.Item;
import lombok.Getter;

@Getter
public class DialogueRecord<T> {

    private final T type;

    private DialogueRecord(T type) {
        this.type = type;
    }

    public static DialogueRecord<ProduceItemArgs> buildProduceItem(String title, int total, int lastAmount, int[] items) {
        return new DialogueRecord<>(new ProduceItemArgs(title, total, lastAmount, items));
    }

    public static DialogueRecord<OptionArgs> buildOptions(String title, String[] options) {
        return new DialogueRecord<>(new OptionArgs(title, options));
    }

    public static DialogueRecord<DestroyItemArgs> buildDestroyItem(Item item, String title, String note) {
        return new DialogueRecord<>(new DestroyItemArgs(item, title, note));
    }

    public static DialogueRecord<PlayerArgs> buildPlayer(String title, String[] chats, Expression expression, boolean continueButtons) {
        return new DialogueRecord<>(new PlayerArgs(title, chats, expression, continueButtons));
    }

    public static DialogueRecord<NpcArgs> buildNpc(int npcId, String title, String[] chats, Expression expression, boolean continueButtons) {
        return new DialogueRecord<>(new NpcArgs(npcId, title, chats, expression, continueButtons));
    }

    public static DialogueRecord<SingleItemArgs> buildSingleItem(Item item, String[] messages, boolean continueButton) {
        return new DialogueRecord<>(new SingleItemArgs(item, messages, continueButton));
    }

    public static DialogueRecord<DoubleItemArgs> buildDoubleItem(Item firstItem, Item secondItem, String[] messages, boolean continueButtons) {
        return new DialogueRecord<>(new DoubleItemArgs(firstItem, secondItem, messages, continueButtons));
    }

    public static DialogueRecord<StatementArgs> buildStatement(String[] messages, boolean continueButtons) {
        return new DialogueRecord<>(new StatementArgs(messages, continueButtons));
    }
}

