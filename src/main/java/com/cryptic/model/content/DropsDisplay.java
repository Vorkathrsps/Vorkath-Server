package com.cryptic.model.content;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.*;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Origin
 * mei 26, 2020
 */
public class DropsDisplay {

    private static final Logger logger = LogManager.getLogger(DropsDisplay.class);

    /*
     * These are the NPCs which do not have NPC drops for the purpose of this interface.
     */
    private static final int[] NPCS_DROPS_EXCLUDED = {
        //NpcIdentifiers.SKOTIZO, NpcIdentifiers.TEKTON_7542, NpcIdentifiers.ZOMBIES_CHAMPION,
        NpcIdentifiers.ANIMATED_BRONZE_ARMOUR, NpcIdentifiers.ANIMATED_IRON_ARMOUR,
        NpcIdentifiers.ANIMATED_STEEL_ARMOUR, NpcIdentifiers.ANIMATED_BLACK_ARMOUR,
        NpcIdentifiers.ANIMATED_MITHRIL_ARMOUR, NpcIdentifiers.ANIMATED_ADAMANT_ARMOUR,
        NpcIdentifiers.ANIMATED_RUNE_ARMOUR, NpcIdentifiers.ALBINO_BAT,
        NpcIdentifiers.ANIMATED_SPADE, NpcIdentifiers.FIYR_SHADE,
        NpcIdentifiers.FIYR_SHADOW, NpcIdentifiers.PHRIN_SHADE,
        NpcIdentifiers.PHRIN_SHADOW, NpcIdentifiers.ANIMATED_STEEL_ARMOUR_6438,
        NpcIdentifiers.ASYN_SHADE, NpcIdentifiers.ASYN_SHADOW,
        NpcIdentifiers.RIYL_SHADE, NpcIdentifiers.RIYL_SHADOW,
        NpcIdentifiers.ENT, NpcIdentifiers.JEFF,
        NpcIdentifiers.WILSON, NpcIdentifiers.JAKE,
        NpcIdentifiers.FAREED, NpcIdentifiers.DESSOUS,
        NpcIdentifiers.DAMIS, NpcIdentifiers.KAMIL,
        NpcIdentifiers.RAM, NpcIdentifiers.MOURNER,
        NpcIdentifiers.JAILER, NpcIdentifiers.JACKAL,
        NpcIdentifiers.THROWER_TROLL, NpcIdentifiers.PALMER,
        NpcIdentifiers.STAG, NpcIdentifiers.ULFRIC,
        NpcIdentifiers.UNDEAD_CHICKEN, NpcIdentifiers.UNDEAD_ONE_5343
    };

    public enum Type {
        ITEM,
        NPC
    }

    private static final int DEFAULT_NPC = NpcIdentifiers.ABERRANT_SPECTRE;
    private static final String DEFAULT = "Aberrant spectre";

    public static void start(Player player) {
        search(player, " ", Type.NPC);
        if (display(player, DEFAULT_NPC)) {
            open(player, DEFAULT_NPC);
        }
    }

    public static void start(Player player, int npc) {
        search(player, DEFAULT, Type.NPC);
        if (display(player, npc)) {
            open(player, npc);
        }
    }

    public static void start(Player player, String npcText, int npc) {
        search(player, npcText, Type.NPC);
        if (display(player, npc)) {
            open(player, npc);
        }
    }

    public static void open(Player player, int npc) {
        Tile empty = new Tile(-1, -1, -1);
        NPC defaultNpc = new NPC(npc, empty);
        if (Arrays.stream(NPCS_DROPS_EXCLUDED).anyMatch(n -> n == npc) || defaultNpc.getCombatInfo() != null && defaultNpc.getCombatInfo().unattackable) {
            return;
        }
        player.getInterfaceManager().open(55140);
    }

    public static void search(Player player, String context, Type type) {
        try {
            context = context.trim().toLowerCase();
            List<String> npc = new ArrayList<>();
            List<Integer> id = new ArrayList<>();
            String finalContext = context;
            //System.out.printf("%s drops%n", ScalarLootTable.registered.size());
            NpcDropRepository.tables.forEach((k, v) -> {
                NpcDefinition npcDefinition = World.getWorld().definitions().get(NpcDefinition.class, k);
                if (v != null && npcDefinition != null) {
                    if (type == Type.ITEM) {
                        ArrayList<Integer> ids = new ArrayList<>();
                        deepAdd(v, ids);
                        ids.forEach(i -> {
                            ItemDefinition idef = World.getWorld().definitions().get(ItemDefinition.class, i);
                            if (idef.name.toLowerCase().contains(finalContext)) {
                                if (!npc.contains(npcDefinition.name)) {
                                    npc.add(npcDefinition.name);
                                    id.add(k);
                                    //System.out.printf("%s vs %s%n", npcDefinition.name, finalContext);
                                }
                            }
                        });
                    }
                    if (type == Type.NPC) {
                        String name = npcDefinition.name;
                        if (name != null && name.toLowerCase().contains(finalContext)) {
                            if (!npc.contains(name)) {
                                if (Arrays.stream(NPCS_DROPS_EXCLUDED).noneMatch(n -> n == k)) {
                                    npc.add(name);
                                    id.add(k);
                                    //System.out.printf("%s vs %d %s %n", npcDefinition.name, k, finalContext);
                                }
                            }
                        }
                    }
                }
            });
            //Clear any previous entries.
            for (int index = 0; index < 430; index++) {
                player.getPacketSender().sendString(55510 + index, "");
            }
            Collections.sort(npc);
            id.sort(Comparator.comparing(a -> World.getWorld().definitions().get(NpcDefinition.class, a).name));
            for (int index = 0; index < npc.size(); index++) {
                player.getPacketSender().sendString(55510 + index, npc.get(index));
                if (index >= 55940)//Max 430 npcs
                    break;
            }
            player.debugMessage("There are " + npc.size() + " npcs with drops");
            if (id.isEmpty()) {
                DialogueManager.sendStatement(player, "No result was found for your search entry!");
                return;
            }
            player.putAttrib(AttributeKey.DROP_DISPLAY_KEY, id);
            display(player, id.get(0));
        } catch (Exception e) {
            logger.error("sadge", e);
        }
    }

    private static void deepAdd(NpcDropTable v, ArrayList<Integer> ids) {
        if (v.getDrops() != null) {
            ids.addAll(v.getDrops().stream().filter(Objects::nonNull).map(it -> ItemRepository.getItemId(it.getItem())).toList());
        }
        if (v.getAlwaysDrops() != null) {
            ids.addAll(v.getAlwaysDrops().stream().filter(Objects::nonNull).map(it -> ItemRepository.getItemId(it.getItem())).toList());
        }
    }

    public static boolean display(Player player, int npc) {
        player.getInterfaceManager().open(55140);
        NpcDefinition def = World.getWorld().definitions().get(NpcDefinition.class, npc);
        int[] colorIDS = {1063, 1062, 1065, 1061, 1066, 1064};
        player.getPacketSender().clearItemOnInterface(56015);
        for (int i = 0; i < 100; i++) {
            player.getPacketSender().sendInterfaceSpriteChange(56400 + i, colorIDS[0]);
            player.getPacketSender().sendString(56700 + i, "");
            player.getPacketSender().sendString(56850 + i, "");
            player.getPacketSender().sendString(57000 + i, "");
            player.getPacketSender().sendString(57150 + i, "");
        }
        NpcDropTable dropTable = NpcDropRepository.forNPC(npc);
        if (dropTable == null) {
            player.message(Color.DARK_GREEN.tag() + "Bestiary: " + Color.OLIVE.tag() + def.name + " has no drops.");
            return true;
        }
        List<Integer[]> drops = new ArrayList<>();
        for (ItemDrop item : dropTable.getAlwaysDrops()) {
            Integer[] drop = new Integer[4];
            drop[0] = ItemRepository.getItemId(item.getItem());
            drop[1] = item.getMinimumAmount();
            drop[2] = item.getMaximumAmount();
            drop[3] = 1;
            drops.add(drop);
        }

        for (ItemDrop item : dropTable.getDrops()) {
            Integer[] drop = new Integer[4];
            drop[0] = ItemRepository.getItemId(item.getItem());
            drop[1] = item.getMinimumAmount();
            drop[2] = item.getMaximumAmount();
            drop[3] = item.getChance();
            drops.add(drop);
        }

        for(int index = 0; index < drops.size(); index++) {
            Integer[] drop = drops.get(index);

            int itemId = drop[0];
            int minAmount = drop[1];
            int maxAmount = drop[2];
            int average = drop[3];

            int colorIndex;

            if (average == 1) {
                colorIndex = 0;
            } else if (average >= 1 && average <= 40) {
                colorIndex = 1;
            } else if (average >= 41 && average <= 89) {
                colorIndex = 4;
            } else if (average >= 90 && average <= 155) {
                colorIndex = 5;
            } else {
                colorIndex = 5;
            }

            player.getPacketSender().sendScrollbarHeight(55153, drops.size() * 39 + 2);
            player.getPacketSender().sendInterfaceSpriteChange(56400 + index, colorIDS[colorIndex]);
            player.getPacketSender().sendItemOnInterfaceSlot(56015, new Item(itemId, Math.min(minAmount, maxAmount)), index);

            Item item = new Item(drop[0]);
            String name = item.unnote().name().length() > 17 ? item.unnote().name().substring(0, 16) + "<br>" + item.unnote().name().substring(16) : item.unnote().name();
            player.getPacketSender().sendString(56700 + index, name);
            String amount = maxAmount == minAmount ? ""+Utils.formatRunescapeStyle(maxAmount) : ""+ Utils.formatRunescapeStyle(minAmount) + "/" + Utils.formatRunescapeStyle(maxAmount);
            player.getPacketSender().sendString(56850 + index, "<col=ffb83f>" + amount);
            var val = item.stackable() && item.getAmount() > 0 ? maxAmount * item.getValue() : item.getValue();
            player.getPacketSender().sendString(57000 + index, "<col=ffb83f>" + Utils.formatRunescapeStyle(val));
            player.getPacketSender().sendString(57150 + index, "<col=ffb83f>" + (average == 1 ? "Always" : ("~ 1 / " + average)));
        }
        return true;
    }

    public static boolean clickActions(Player player, int button) {
        if (button >= 55510 && button <= 55939) {
            int base_button = 55510;
            int modified_button = (base_button - button);
            int index = Math.abs(modified_button);
            List<Integer> key = player.getAttribOr(AttributeKey.DROP_DISPLAY_KEY, new ArrayList<Integer>());
            if (key == null || key.isEmpty())
                return false;
            if (index >= key.size())
                return false;
            DropsDisplay.display(player, key.get(index));
            player.debugMessage("Displaying drops for NPC ID " + key.get(index));
            return true;
        }

        if (button == 55152) {
            close(player);
            return true;
        }

        if (button == 55143) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Search by npc.", "Search by item.", "Nevermind.");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            stop();
                            String input = "";
                            DropsDisplay.search(player, input, DropsDisplay.Type.NPC);
                            for (int i = 0; i < World.getWorld().definitions().total(NpcDefinition.class); i++) {
                                NpcDefinition npcDefinition = World.getWorld().definitions().get(NpcDefinition.class, i);
                                if (npcDefinition.name != null && npcDefinition.name.equalsIgnoreCase(input)) {
                                    if (DropsDisplay.display(player, i)) {
                                        DropsDisplay.open(player, i);
                                        return;
                                    }
                                    break;
                                }
                            }
                            player.getPacketSender().sendMessage("DISABLED - Ynneh");
                        } else if (option == 2) {
                            stop();
                            player.setNameScript("Which item would you like to find?", value -> {
                                DropsDisplay.search(player, (String) value, Type.ITEM);
                                return false;
                            });
                        } else if (option == 3) {
                            stop();
                        }
                    }
                }
            });
        }
        return false;
    }

    public static void close(Player player) {
        player.clearAttrib(AttributeKey.DROP_DISPLAY_KEY);
        player.getInterfaceManager().close();
    }
}
