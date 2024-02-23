package com.cryptic.model.content.chests;

import com.cryptic.model.items.Item;
import lombok.Data;
import java.util.List;
@Data
public class CustomLootTable {
    private final InnerTable innerTable;
    private final int chance;

    public CustomLootTable(InnerTable innerTable, int chance) {
        this.innerTable = innerTable;
        this.chance = chance;
    }

    static class InnerTable {
        String tableName;
        List<Item> loot;

        public InnerTable(String tableName, List<Item> loot) {
            this.tableName = tableName;
            this.loot = loot;
        }
    }

}
