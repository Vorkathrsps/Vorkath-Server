package com.cryptic.clientscripts.impl.dialogue.information;

import com.cryptic.clientscripts.cs2.impl.dialogue.information.types.impl.*;
import com.cryptic.clientscripts.impl.dialogue.information.types.impl.*;
import com.cryptic.model.cs2.impl.dialogue.information.types.impl.*;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.model.items.Item;
import lombok.Getter;

@Getter
public class DialogueInformation<T> {

    private final T type;

    private DialogueInformation(T type) {
        this.type = type;
    }

    public static DialogueInformation<ProduceItemType> buildProduceItem(String title, int total, int lastAmount, int[] items) {
        return new DialogueInformation<>(new ProduceItemType(title, total, lastAmount, items));
    }

    public static DialogueInformation<OptionType> buildOptions(String title, String[] options) {
        return new DialogueInformation<>(new OptionType(title, options));
    }

    public static DialogueInformation<DestroyItemType> buildDestroyItem(Item item, String title, String note) {
        return new DialogueInformation<>(new DestroyItemType(item, title, note));
    }

    public static DialogueInformation<PlayerType> buildPlayer(String title, String[] chats, Expression expression, boolean continueButtons) {
        return new DialogueInformation<>(new PlayerType(title, chats, expression, continueButtons));
    }

    public static DialogueInformation<NpcType> buildNpc(int npcId, String title, String[] chats, Expression expression, boolean continueButtons) {
        return new DialogueInformation<>(new NpcType(npcId, title, chats, expression, continueButtons));
    }

    public static DialogueInformation<SingleItemType> buildSingleItem(Item item, String[] messages, boolean continueButton) {
        return new DialogueInformation<>(new SingleItemType(item, messages, continueButton));
    }

    public static DialogueInformation<DoubleItemType> buildDoubleItem(Item firstItem, Item secondItem, String[] messages, boolean continueButtons) {
        return new DialogueInformation<>(new DoubleItemType(firstItem, secondItem, messages, continueButtons));
    }

    public static DialogueInformation<StatementType> buildStatement(String[] messages, boolean continueButtons) {
        return new DialogueInformation<>(new StatementType(messages, continueButtons));
    }
}

