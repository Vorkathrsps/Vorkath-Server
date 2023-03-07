package com.aelous.model.entity.npc.droptables;

import com.aelous.cache.definitions.DefinitionRepository;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.aelous.cache.DataStore;
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
        public int points = 1;
        public int from;
        public int amount = 1;
        public int min, max;
        public boolean rare = false;
        public String name;
        public Fraction computedFraction;

        public Item convert() {
            return new Item(id, amount);
        }

        @Override
        public String toString() {
            return "TableItem{" +
                "id=" + id +
                ", points=" + points +
                ", from=" + from +
                ", amount=" + amount +
                ", min=" + min +
                ", max=" + max +
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
    private int tblrndcap;
    private boolean debug = false;
    private int rowmod = 1;
    public int points = 1;
    private int from;
    private boolean rareaffected;
    public String name;
    private int tblpts;
    private transient ScalarLootTable root;
    public int petRarity;
    public int petItem;
    public int odds = 0;

    public static void loadAll(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                loadAll(f);
            } else {
                try {
                    if (f.getName().endsWith(".json")) {
                        ScalarLootTable t = load(f);

                        t.process();

                        for (int n : t.npcs) {
                            registered.put(n, t);
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

        // Upscale it
        int current = 0;
        if (items != null) {
            for (TableItem item : items) {
                if (item.points == 0) // Filter out guaranteed drops
                    continue;

                item.from = current;
                current += item.points;

                if (item.rare)
                    rareaffected = true;
            }

            rndcap = current;
        }

        // Normalize the nested tables
        if (tables != null) {
            for (ScalarLootTable table : tables) {
                table.from = current;
                current += table.points;

                table.process();
            }

            rndcap = current;
        }

        calcRare();
    }

    private void recursiveCalcChances(int num, int denom) {
        int computed = ptsTotal();
        // Items..
        if (items != null) {
            for (TableItem item : items) {
                double chance = (double) (item.points * num) / (double) (denom * computed);
                item.computedFraction = Fraction.getFraction(chance);
            }
        }

        // Tables
        if (tables != null) {
            for (ScalarLootTable table : tables) {
                table.recursiveCalcChances(num * table.points, denom * computed);
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

    public int ptsTotal() {
        int a = 0;
        if (items != null) {
            for (TableItem item : items) {
                a += item.points;
            }
        }
        if (tables != null) {
            for (ScalarLootTable table : tables) {
                a += table.points;
            }
        }
        return a;
    }

    private void calcRare() {
        // Ingest rare knowledge
        if (tables != null) {
            for (ScalarLootTable table : tables) {
                if (table.rareaffected) {
                    rareaffected = true;
                    break;
                }
            }
        }
    }

    public static ScalarLootTable forNPC(int npc) {
        return registered.get(npc);
    }

    public ScalarLootTable randomTable(Random random, boolean ringOfWealth) {
        int drop = random.nextInt(tblrndcap - (ringOfWealth ? (rowmod == 0 ? 1 : rowmod) : 0));
        for (ScalarLootTable table : tables) {
            if (drop >= table.from && drop < table.from + table.points) {
                return table;
            }
        }

        return null;
    }

    public Item randomItem(Random random) {
        random = new SecureRandom();

        if (rndcap < 1) {
            return null;
        }

        int drop = random.nextInt(rndcap);
        
        if (items != null) {
            for (TableItem item : items) {
                if (drop >= item.from && drop < item.from + item.points) {
                    if (item.min == 0) {
                        return new Item(item.id, item.amount);
                    } else {
                        return new Item(item.id, random(random, item.min, item.max));
                    }
                }
            }
        }

        // Try a table now
        if (tables != null && tables.length > 0) {
            for (ScalarLootTable table : tables) {
                if (drop >= table.from && drop < table.from + table.points) {
                    Item item = table.randomItem(random);
                    return item;
                }
            }
        }

        return null;
    }

    static int random(Random random, int min, int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n + 1));
    }

    public List<TableItem> getGuaranteedDrops() {
        return guaranteed == null ? Collections.emptyList() : Arrays.asList(guaranteed);
    }

    public List<Item> simulate(Random r, int samples) {
        List<Item> list = new LinkedList<>();
        Map<Integer, Integer> state = new HashMap<>();

        for (int i = 0; i < samples; i++) {
            Item random = randomItem(r);
            if (random != null)
                state.compute(random.getId(), (key, value) -> value == null ? random.getAmount() : (value + random.getAmount()));
        }

        state.forEach((k, v) -> list.add(new Item(k, v)));
        return list;
    }

    public static ScalarLootTable load(File file) {
        try {
            ScalarLootTable table = load(new String(Files.readAllBytes(file.toPath())));
            table.process();
            table.calcRare();
            table.tblpts = table.ptsTotal();
            table.setRoot(table);
            table.recursiveCalcChances(1, 1);
            return table;
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
        table.process();
        table.tblpts = table.ptsTotal();
        table.setRoot(table);
        table.recursiveCalcChances(1, 1);
        return table;
    }

    public void rollForLarransKey(NPC npc, Player player) {
        var inWilderness = WildernessArea.inWild(player);
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
        //}
    }

    @Override
    public String toString() {
        return "ScalarLootTable{" +
            "items=" + Arrays.toString(items) +
            ", guaranteed=" + Arrays.toString(guaranteed) +
            ", tables=" + Arrays.toString(tables) +
            ", npcs=" + Arrays.toString(npcs) +
            ", rndcap=" + rndcap +
            ", tblrndcap=" + tblrndcap +
            ", debug=" + debug +
            ", rowmod=" + rowmod +
            ", points=" + points +
            ", from=" + from +
            ", rareaffected=" + rareaffected +
            ", name='" + name + '\'' +
            ", tblpts=" + tblpts +
            ", root=" + root.name +
            ", petRarity=" + petRarity +
            ", petItem=" + petItem +
            ", odds=" + odds +
            '}';
    }
}
