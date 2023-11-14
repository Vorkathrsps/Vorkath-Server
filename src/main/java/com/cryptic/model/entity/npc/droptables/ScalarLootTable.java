package com.cryptic.model.entity.npc.droptables;

import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cryptic.cache.DataStore;
import lombok.Getter;
import org.apache.commons.lang3.math.Fraction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by Bart on 6/8/2015.
 */
public class ScalarLootTable {

    public static class TableItem {
        public int id;
        public int weight;
        public int from;
        public int amount = 1;
        public int min, max;
        public boolean rare = false;
        public String name;
        public Fraction computedFraction;

        public Item convert() {
            return new Item(id, amount);
        }

        public Item toItem() {
            return new Item(id, min == max ? min : Utils.random(min, max));
        }

        @Override
        public String toString() {
            return "TableItem{" +
                "id=" + id +
                ", min=" + min +
                ", max=" + max +
                ", weight=" + weight +  // Corrected field name to "weight"
                ", rare=" + rare +
                ", name='" + name + '\'' +
                ", computedFraction=" + computedFraction +
                '}';
        }
    }

    public static Map<Integer, ScalarLootTable> registered = new HashMap<>();
    public TableItem[] items;
    public TableItem[] guaranteed;
    public ScalarLootTable[] tables;
    public int[] npcs;
    public int rndcap;
    private boolean debug = false;
    public int tableWeight = 1;
    public String name;
    @Getter public int totalWeight;
    private transient ScalarLootTable root;
    public int petRarity;
    public int petItem;
    public int odds = 0;

    public static void loadAll(File dir) {
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) {
                loadAll(f);
            } else {
                try {
                    if (f.getName().endsWith(".json")) {
                        ScalarLootTable t = load(f);

                        if (t != null) {
                            t.process();

                            for (int n : t.npcs) {
                                registered.put(n, t);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading drop file " + f);
                    e.printStackTrace();
                }
            }
        }
    }

    private void process() {
        ArrayList<TableItem> temp = new ArrayList<>(getGuaranteedDrops());
        if (temp.contains(null)) {
            temp.removeIf(Objects::isNull);
            guaranteed = temp.toArray(TableItem[]::new);
        }
        if (items != null) {
            temp = new ArrayList<>(List.of(items));
            if (temp.contains(null)) {
                temp.removeIf(Objects::isNull);
                items = temp.toArray(TableItem[]::new);
            }
        }
    }

    private void setRoot(ScalarLootTable root) {
        this.root = root;
        if (tables != null) {
            for (ScalarLootTable table : tables) {
                table.setRoot(root);
            }
        }
    }

    public Item rollItem() {
        List<Item> items = rollItems();
        return items == null ? null : items.get(0);
    }

    public List<Item> rollItems() {
        List<Item> items = new ArrayList<>();
        if (tables != null) {
            System.out.println("total weight: " + totalWeight);
            var tableRandom = Utils.THREAD_LOCAL_RANDOM.get();
            for (ScalarLootTable table : tables) {
                var tableRoll = tableRandom.nextDouble();
                tableRoll /= totalWeight;
                if (tableRoll <= (double) table.tableWeight / totalWeight) {
                    if (table.items != null) {
                        var itemRandom = Utils.THREAD_LOCAL_RANDOM.get();
                        for (TableItem item : table.items) {
                            var itemRoll = itemRandom.nextDouble() / item.weight;
                            if (itemRoll <= (double) item.weight / totalWeight) {
                                items.add(item.toItem());
                                break;
                            }
                        }
                    }
                }
            }
        }
        return items.isEmpty() ? null : items;
    }

    public void calculateWeight() {
        if (tables != null) {
            for (ScalarLootTable table : tables) {
                table.totalWeight = 1;
                table.totalWeight += table.tableWeight;
                if (table.items != null) {
                    for (TableItem item : table.items) {
                        table.totalWeight += item.weight - 1;
                        this.totalWeight += table.totalWeight;
                        item.computedFraction = Fraction.getFraction(item.weight, table.totalWeight);
                    }
                }
            }
        }
    }

    public static ScalarLootTable forNPC(int npc) {
        return registered.get(npc);
    }

    public List<TableItem> getGuaranteedDrops() {
        return guaranteed == null ? Collections.emptyList() : Arrays.asList(guaranteed);
    }

    public List<Item> simulate(Random r, int samples) {
        List<Item> list = new LinkedList<>();
        Map<Integer, Integer> state = new HashMap<>();

        for (int i = 0; i < samples; i++) {
            Item random = rollItem();
            if (random != null)
                state.compute(random.getId(), (key, value) -> value == null ? random.getAmount() : (value + random.getAmount()));
        }

        state.forEach((k, v) -> list.add(new Item(k, v)));
        return list;
    }

    public static ScalarLootTable load(File file) {
        try {
            return load(new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ScalarLootTable load(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setLenient();
        Gson gson = builder.create();
        ScalarLootTable table = gson.fromJson(json, ScalarLootTable.class);
        table.calculateWeight();
        table.process();
        table.setRoot(table);
        return table;
    }

    public void rollForLarransKey(NPC npc, Player player) {
        var inWilderness = WildernessArea.isInWilderness(player);
        if (inWilderness) {
            var larransLuck = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.LARRANS_LUCK);
            var combatLvl = npc.def().combatlevel;
            var roll = combatLvl < 50 ? larransLuck ? 875 : 1000 : larransLuck ? 350 : 400;
            if (World.getWorld().rollDie(roll, 1)) {
                GroundItemHandler.createGroundItem(new GroundItem(new Item(ItemIdentifiers.LARRANS_KEY), player.tile(), player));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner;
        SecureRandom rand = new SecureRandom();
        DefinitionRepository repo = new DefinitionRepository(new DataStore("data/cache", true), true);

        //while (true) {
        for (int i = 0; i < 10; i++) System.out.println();
        while (System.in.available() > 0) System.in.read();
        System.out.print("Drop file: ./data/combat/");
        scanner = new Scanner(System.in);
        String file = scanner.nextLine().trim();
        System.out.print("Number of kills: ");
        int kills = scanner.nextInt();

        ScalarLootTable root = load(new File("./data/combat/" + file));

        List<Item> simulate = root.simulate(rand, kills);
        simulate.sort((o1, o2) -> {
            int oo1 = kills / Math.max(1, o1.getAmount());
            int oo2 = kills / Math.max(1, o2.getAmount());
            return Integer.compare(oo1, oo2);
        });

        for (Item item : simulate) {
            int indiv = kills / Math.max(1, item.getAmount());
            System.out.println(item.getAmount() + " x " + repo.get(ItemDefinition.class, new Item(item.getId()).unnote(repo).getId()).name + " (1/" + indiv + ")");
        }

        System.out.println();
        System.out.println();
    }

    @Override
    public String toString() {
        return "ScalarLootTable{" +
            "items=" + Arrays.toString(items) +
            ", guaranteed=" + Arrays.toString(guaranteed) +
            ", tables=" + Arrays.toString(tables) +
            ", npcs=" + Arrays.toString(npcs) +
            ", rndcap=" + rndcap +
            ", debug=" + debug +
            ", tablweight=" + tableWeight +
            ", name='" + name + '\'' +
            ", root=" + root.name +
            ", petRarity=" + petRarity +
            ", petItem=" + petItem +
            ", odds=" + odds +
            '}';
    }
}
