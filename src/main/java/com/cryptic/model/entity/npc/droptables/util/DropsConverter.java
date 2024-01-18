package com.cryptic.model.entity.npc.droptables.util;

import com.cryptic.GameServer;
import com.cryptic.cache.DataStore;
import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.droptables.ItemDrop;
import com.cryptic.model.entity.npc.droptables.ItemRepository;
import com.cryptic.model.entity.npc.droptables.NpcDropTable;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.items.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DropsConverter {
    public static Int2ObjectMap<NpcDropTable> tables = new Int2ObjectArrayMap<>();

    public static List<Integer> array = new ArrayList<>();
    public static void main(String[] args) {
        ItemRepository.load();
        GameServer.fileStore = new DataStore(GameServer.properties().fileStore);
        GameServer.definitions = new DefinitionRepository();
        ScalarLootTable.loadAll(new File("data/combat/drops/"));
        System.out.println(ScalarLootTable.registered.size() + " loaded drops");
        ScalarLootTable.registered.forEach((npcId, lootTable) -> {
            int petItem;
            int petRarity;
            petRarity = lootTable.petRarity;
            petItem = lootTable.petItem;
            List<ItemDrop> always = new ArrayList<>();
            List<ItemDrop> rareDrops = new ArrayList<>();
            if(lootTable.guaranteed != null) {
                for (ScalarLootTable.TableItem lootItem : lootTable.getGuaranteedDrops()) {
                    String itemName = ItemRepository.getItemName(lootItem.id);
                    ItemDrop itemDrop = new ItemDrop(itemName, lootItem.min, lootItem.max, lootItem.weight);
                    always.add(itemDrop);
                }
            }
            if (lootTable.tables != null) {
                ScalarLootTable dropTable = ScalarLootTable.forNPC(npcId);
                List<Integer[]> drops = new ArrayList<>();
                double totalTablesWeight = dropTable.ptsTotal();
                int petId, petAverage;
                petId = dropTable.petItem == 0 ? -1 : dropTable.petItem;
                petAverage = dropTable.petRarity;
                if(petId != -1) drops.add(0, new Integer[]{petId, 1, 1,  petAverage}); //"pet" specifically identified by minAmount == -1
                if (dropTable.tables != null) {
                    for (ScalarLootTable table : dropTable.tables) {
                        if (table != null) {
                            double tableChance = table.tableWeight / totalTablesWeight;
                            if (table.items.length == 0) {
                            } else {
                                for (ScalarLootTable.TableItem item : table.items) {
                                    Integer[] drop = new Integer[5];
                                    drop[0] = item.id;
                                    drop[1] = item.min == 0 ? item.amount : item.min;
                                    drop[2] = item.max == 0 ? item.amount : item.max;
                                    if (item.weight == 0)
                                        drop[3] = (int) (1D / tableChance);
                                    else
                                        drop[3] = (int) (1D / (item.computedFraction.doubleValue()));
                                    drop[4] = item.amount;
                                    drops.add(drop);
                                }
                            }
                        }
                    }
                }

                for (Integer[] drop : drops) {
                    int itemId = drop[0];
                    int minAmount = drop[1] == 0 ? 1 : drop[1];
                    int maxAmount = drop[2] == 0 ? 1 : drop[2];
                    int average = drop[3];
                    var def = ItemDefinition.cached.get(itemId);
                    String name = ItemRepository.getItemName(itemId);
                    ItemDrop itemDrop = new ItemDrop(name, minAmount, maxAmount, average, false, false);
                    rareDrops.add(itemDrop);
                }
            }
            NpcDropTable table = new NpcDropTable(lootTable.npcs, ItemRepository.getItemName(petItem), petRarity, always, rareDrops);
            for (var id : lootTable.npcs) {
                tables.put(id, table);
            }
        });
        ScalarLootTable.registered.clear();
        var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
        tables.forEach((npc, table) -> {
            if (npc == null || table == null) return;
            String name = World.getWorld().definitions().get(NpcDefinition.class, npc).name;
            Path file = Path.of("data/combat/npcdrops/" + name + ".yaml");
            if(npcsWritten.contains(name)) return;
            try {
                mapper.writeValue(file.toFile(), table);
                System.out.println("Written: " + file.getFileName());
                npcsWritten.add(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private static List<String> npcsWritten = new ArrayList<>();
}
