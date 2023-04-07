package com.aelous.utility.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    private static HashMap<Integer, String> npcs = new HashMap<>();
    private static HashMap<String, Integer> items = new HashMap<>();
    private static List<String> checkedNpcs = new ArrayList<>();

    private static HashMap<Integer, List<NpcDrop>> always = new HashMap<>();
    private static HashMap<Integer, List<NpcDrop>> common = new HashMap<>();
    private static HashMap<Integer, List<NpcDrop>> uncommon = new HashMap<>();
    private static HashMap<Integer, List<NpcDrop>> rare = new HashMap<>();
    private static HashMap<Integer, List<NpcDrop>> veryRare = new HashMap<>();

    private static HashMap<Integer, List<NpcDrop>> unknownDrops = new HashMap<>();

    private static HashMap<String, HashMap<Integer, List<NpcDrop>>> dropTypes = new HashMap<>();

    public static void main(String[] mainArgs) {
        dropTypes.put("always", always);
        dropTypes.put("common", common);
        dropTypes.put("uncommon", uncommon);
        dropTypes.put("rare", rare);
        dropTypes.put("very-rare", veryRare);
        grabNpcs();
        grabItems();
        System.out.println("Grabbed " + npcs.size() + " npcs.");
        System.out.println("Grabbed " + items.size() + " items.");
        for (int i = 1; i < 10000; i++) {
            String name = npcs.get(i);
            if (name == null) {
                continue;
            }
            if (checkedNpcs.contains(name)) {
                continue;
            }
            List<String> webpage = dumpPage(name);
            dumpDrops(webpage, i);
            writeDrops(i);
        }
    }

    private static void writeDrops(int npc) {
        System.out.println("Writing for " + npcs.get(npc));
        try (BufferedWriter out = new BufferedWriter(new FileWriter(new File("drops.cfg"), true))) {
            String[] types = { "always", "common", "uncommon", "rare", "very-rare" };
            HashMap<String, String> finalizing = new HashMap<>();
            for (String s : types) {
                HashMap<Integer, List<NpcDrop>> drops = dropTypes.get(s);
                List<NpcDrop> dropList = drops.get(npc);
                if (dropList == null) {
                    dropList = new ArrayList<>();
                }
                String itemLine = buildLine(dropList);
                if (itemLine.length() < 1) {
                    itemLine = "nothing";
                }
                finalizing.put(s, itemLine);
            }
            List<Integer> npcList = getIdsForNpc(npcs.get(npc));
            checkedNpcs.add(npcs.get(npc));
            String npcIdList = buildLine(npcList);

            boolean located = false;
            for (String s : types) {
                if (!finalizing.get(s).equals("nothing")) {
                    located = true;
                }
            }
            if (!located) {
                out.close();
                return;
            }
            out.write("# " + npcs.get(npc));
            out.newLine();
            out.write(npcIdList);
            out.newLine();
            for (String s : types) {
                out.write(finalizing.get(s));
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildLine(List<?> ids) {
        String ret = "";
        for (int i = 0; i < ids.size(); i++) {
            ret += ids.get(i).toString() + "\t";
        }
        if (ret.length() > 0) {
            return ret.substring(0, ret.length() - 1);
        } else {
            return ret;
        }
    }

    private static List<Integer> getIdsForNpc(String name) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            if (npcs.get(i) == null) {
                continue;
            }
            if (npcs.get(i).equalsIgnoreCase(name)) {
                ret.add(i);
            }
        }
        return ret;
    }

    private static void dumpDrops(List<String> webpage, int npc) {
        System.out.println("Dumping Drops for " + npcs.get(npc));
        for (String line : webpage) {
            HashMap<Integer, List<NpcDrop>> dropType = null;
            if (line.contains("rarity-")) {
                String gettingRarity = line.substring(line.indexOf("rarity-") + "rarity-".length());
                gettingRarity = gettingRarity.substring(0, gettingRarity.indexOf("\""));
                if (gettingRarity.equalsIgnoreCase("unknown")
                    || gettingRarity.equalsIgnoreCase("random")) {
                    dropType = unknownDrops;
                } else {
                    dropType = dropTypes.get(gettingRarity);
                }
                List<NpcDrop> itemList = dropType.get(npc);
                if (itemList == null) {
                    itemList = new ArrayList<NpcDrop>();
                    dropType.put(npc, itemList);
                }
                String item = getItem(line);
                if (items.get(item) == null) {
                    continue;
                }
                int itemId = items.get(item);
                String quantity = getQuantity(line);
                quantity = quantity.replaceAll("â€“", "-");
                quantity = quantity.replaceAll(",", "");
                if (quantity.equalsIgnoreCase("Unknown")) {
                    quantity = "1";
                }
                NpcDrop drop = createDrop(itemId, quantity);
                itemList.add(drop);
            }
        }
    }

    private static NpcDrop createDrop(int itemId, String quantity) {
        NpcDrop drop = null;
        if (quantity.contains("-")) {
            String[] qs = quantity.split("-");
            if (qs[0].contains(";")) {
                String min = qs[0].split(";")[0];
                String max = qs[0].split(";")[1];
                drop = new NpcDrop(itemId, Integer.parseInt(min));
                drop.setMax(Integer.parseInt(max.trim()));
                return drop;
            } else {
                drop = new NpcDrop(itemId, Integer.parseInt(qs[0]));
            }
            if (qs[1].contains("; ")) {
                String[] args = qs[1].split(";");
                String workingWith = args[args.length - 1].trim();
                if (workingWith.contains("&#")) {
                    workingWith = workingWith.substring(0, workingWith.indexOf("&#"));
                }
                drop.setMax(Integer.parseInt(workingWith));
            } else {
                String[] args = qs[1].split(";");
                String workingWith = args[0].trim();
                if (workingWith.contains("&#")) {
                    workingWith = workingWith.substring(0, workingWith.indexOf("&#"));
                }
                drop.setMax(Integer.parseInt(workingWith));
            }
        } else if (quantity.contains(";")) {
            String[] args = quantity.split(";");
            String gettingMin = args[0].trim();
            if (gettingMin.contains("&#")) {
                gettingMin = gettingMin.substring(0, gettingMin.indexOf("&#"));
            }
            int min = Integer.parseInt(gettingMin);
            String gettingMax = args[args.length - 1].trim();
            if (gettingMax.contains("(noted")) {
                gettingMax = gettingMin;
            }
            int max = Integer.parseInt(gettingMax);
            drop = new NpcDrop(itemId, min);
            drop.setMax(max);
        } else {
            drop = new NpcDrop(itemId, Integer.parseInt(quantity));
        }
        return drop;
    }

    private static String getQuantity(String line) {
        line = line.substring(line.lastIndexOf("<td>") + 4);
        line = line.substring(0, line.indexOf("</td>"));
        return line;
    }

    private static String getItem(String line) {
        line = line.substring(line.indexOf("title") + 7);
        line = line.substring(0, line.indexOf("\""));
        return line;
    }

    private static List<String> dumpPage(String name) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
            new URL("http://runescape.wikia.com/wiki/" + name).openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to locate drop table for: " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void grabItems() {
        try (BufferedReader in = new BufferedReader(new FileReader(new File("item.cfg")))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("//")) {
                    continue;
                }
                if (line.trim().length() < 1) {
                    continue;
                }
                while (line.contains("\t\t")) {
                    line = line.replaceAll("\t\t", "\t");
                }
                while (line.contains("  ")) {
                    line = line.replaceAll("  ", " ");
                }
                String[] args = line.split("\t");
                if (args[0].startsWith("[")) {
                    continue;
                }
                int id = Integer.parseInt(args[0].substring(6).trim());
                String name = args[1].trim();
                if (!items.containsKey(name)) {
                    items.put(name, id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        items.put("Coins", 995);
    }

    private static void grabNpcs() {
        try (BufferedReader in = new BufferedReader(new FileReader("npc.cfg"))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("//")) {
                    continue;
                }
                if (line.trim().length() < 1) {
                    continue;
                }
                while (line.contains("\t\t")) {
                    line = line.replaceAll("\t\t", "\t");
                }
                while (line.contains("  ")) {
                    line = line.replaceAll("  ", " ");
                }
                String[] args = line.split("\t");
                if (args[0].startsWith("[")) {
                    continue;
                }
                int id = Integer.parseInt(args[0].substring(6).trim());
                String name = args[1].trim();
                int health = Integer.parseInt(args[3].trim());
                if (health > 0) {
                    npcs.put(id, name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class NpcDrop {
        int item;
        int quantity;
        int max = -1;

        public NpcDrop(int item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public String toString() {
            if (max == -1) {
                return item + " " + quantity;
            } else {
                return item + " " + quantity + " " + max;
            }
        }

    }

}
