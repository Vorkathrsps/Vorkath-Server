package com.cryptic.model.entity.npc.droptables.util;

import com.cryptic.GameServer;
import com.cryptic.cache.DataStore;
import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.entity.npc.droptables.ItemDrop;
import com.cryptic.model.entity.npc.droptables.ItemRepository;
import com.cryptic.model.entity.npc.droptables.NpcDropTable;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Monster;
import com.cryptic.utility.MonsterLoader;
import com.cryptic.utility.loaders.loader.impl.BloodMoneyPriceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DropsConverter {
    public static Int2ObjectMap<NpcDropTable> tables = new Int2ObjectArrayMap<>();

    public static List<Integer> array = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ItemRepository.load();
        MonsterLoader.load();
        GameServer.fileStore = new DataStore(GameServer.properties().fileStore);
        GameServer.definitions = new DefinitionRepository();
        new BloodMoneyPriceLoader().load();
        MonsterLoader.monsters.int2ObjectEntrySet().fastForEach(monster -> {
            var id = monster.getIntKey();
            var m = monster.getValue();
            List<ItemDrop> always = new ArrayList<>();
            List<ItemDrop> rareDrops = new ArrayList<>();
            AtomicInteger rolls = new AtomicInteger(1);
            var dropTable = m.getDrops();
            dropTable.forEach(drop -> {
                var quantity = drop.getQuantity();
                if (quantity == null) return;
                addDrop(always, rareDrops, drop, quantity);
                rolls.set(drop.getRolls());
            });
            NpcDropTable table = new NpcDropTable(new int[]{id}, null, 2000, always, rareDrops, rolls.get());
            if (!table.getDrops().isEmpty()) {
                tables.put(id, table);
            }
        });
        var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
        tables.forEach((npc, table) -> {
            if (npc == null || table == null) return;
            String name = NpcDefinition.cached.get(npc).name;
            Path file = Path.of("data/combat/droptest/" + name + ".yaml");
            if (npcsWritten.contains(name)) return;
            try {
                mapper.writeValue(file.toFile(), table);
                npcsWritten.add(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static int nulledIndices = 0;

    private static void addDrop(List<ItemDrop> always, List<ItemDrop> rareDrops, Monster.Drop drop, String quantity) {
        if (quantity.contains(",")) {
            var split = quantity.split(",");
            for (String s : split) {
                addDrop(always, rareDrops, drop, s);
            }
            return;
        }
        int dropId = drop.getId();
        var alwaysDrop = drop.getRarity() == 1;
        var min = 1;
        var max = 1;
        if (quantity.contains("-")) {
            var split = quantity.split("-");
            min = Integer.parseInt(split[0]);
            max = Integer.parseInt(split[1]);
        } else min = max = Integer.parseInt(quantity);
        var weight = 1 / drop.getRarity();
        var rare = Item.getValue(dropId) > 5_000_000;
        if (drop.getId() != 23490 && drop.getId() != 23083) {
            String itemName = ItemRepository.getItemName(drop.getId());
            if (alwaysDrop) {
                ItemDrop itemDrop = new ItemDrop(itemName, min, max, 1);
                always.add(itemDrop);
            } else {
                if (weight >= 20_000) weight = (int) (weight / 8D);
                else if (weight >= 6) weight = (int) (weight / 3D);
                else if (weight >= 2) weight = (int) (weight / 2D);
                if (drop.isNoted()) itemName = "NOTED_" + itemName;
                ItemDrop itemDrop = new ItemDrop(itemName, min, max, (int) weight, rare);
                rareDrops.add(itemDrop);
            }

            if (itemName == null) nulledIndices++;
        }
    }

    private static List<String> npcsWritten = new ArrayList<>();
}
