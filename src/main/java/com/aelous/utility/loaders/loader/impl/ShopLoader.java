package com.aelous.utility.loaders.loader.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.utility.loaders.loader.DefinitionLoader;
import com.aelous.model.items.container.shop.SellType;
import com.aelous.model.items.container.shop.StoreItem;
import com.aelous.model.items.container.shop.currency.CurrencyType;
import com.aelous.model.items.container.shop.impl.DefaultShop;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.util.*;

public class ShopLoader extends DefinitionLoader {

    private static final Logger logger = LogManager.getLogger(ShopLoader.class);

    @Override
    public void load() throws Exception {
        try (FileReader in = new FileReader(file())) {
            JsonArray array = (JsonArray) JsonParser.parseReader(in);
            Gson builder = new GsonBuilder().create();
            for (int index = 0; index < array.size(); index++) {
                JsonObject reader = (JsonObject) array.get(index);
                final int shopId = Objects.requireNonNull(reader.get("shopId")).getAsInt();
                final String name = Objects.requireNonNull(reader.get("name").getAsString());
                final boolean noiron = reader.get("noiron").getAsBoolean();
                final CurrencyType currency = builder.fromJson(reader.get("currency"), CurrencyType.class);
                final boolean restock = reader.get("restock").getAsBoolean();
                final int scroll = reader.get("scroll").getAsInt();
                final String sellType = reader.get("sellType").getAsString().toUpperCase();
                final LoadedItem[] loadedItems = builder.fromJson(reader.get("items"), LoadedItem[].class);

                final List<StoreItem> storeItems = new ArrayList<>(loadedItems.length);

                for (LoadedItem loadedItem : loadedItems) {
                    OptionalInt value = loadedItem.value == 0 ? OptionalInt.empty() : OptionalInt.of(loadedItem.value);
                    storeItems.add(new StoreItem(loadedItem.id, loadedItem.amount, value, Optional.ofNullable(loadedItem.type)));
                }

                StoreItem[] items = storeItems.toArray(new StoreItem[0]);
                World.getWorld().shops.put(shopId, new DefaultShop(items, shopId, name, noiron, SellType.valueOf(sellType), scroll, restock, currency));
            }
        }
    }
    
    @Override
    public String file() {
        return GameServer.properties().definitionsDirectory + "shops.json";
    }
    
    private static final class LoadedItem {

        private final int id;

        private final int amount;

        private final int value;

        private final CurrencyType type;

        public LoadedItem(int id, int amount, int value, CurrencyType type) {
            this.id = id;
            this.amount = amount;
            this.value = value;
            this.type = type;
        }
    }

}
