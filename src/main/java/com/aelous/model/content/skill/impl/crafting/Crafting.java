package com.aelous.model.content.skill.impl.crafting;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.skill.impl.crafting.impl.*;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;

import java.util.HashMap;

import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.ZENYTE_SHARD;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.POTTERY_OVEN_11601;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 16, 2020
 */
public class Crafting extends PacketInteraction {

    /**  The craftable map. */
    private final static HashMap<Integer, Craftable> CRAFTABLES = new HashMap<>();

    /** The leather armour data. */
    private static final Object[][] LEATHER_ARMOR_IDS = {
        {8635, 1, Leather.LEATHER_BODY}, {8634, 5, Leather.LEATHER_BODY}, {8633, 10, Leather.LEATHER_BODY},
        {8638, 1, Leather.LEATHER_GLOVES}, {8637, 5, Leather.LEATHER_GLOVES}, {8636, 10, Leather.LEATHER_GLOVES},
        {8641, 1, Leather.LEATHER_BOOTS}, {8640, 5, Leather.LEATHER_BOOTS}, {8639, 10, Leather.LEATHER_BOOTS},
        {8644, 1, Leather.LEATHER_VANBRACES}, {8643, 5, Leather.LEATHER_VANBRACES}, {8642, 10, Leather.LEATHER_VANBRACES},
        {8647, 1, Leather.LEATHER_CHAPS}, {8646, 5, Leather.LEATHER_CHAPS}, {8645, 10, Leather.LEATHER_CHAPS},
        {8650, 1, Leather.LEATHER_COIF}, {8649, 5, Leather.LEATHER_COIF}, {8648, 10, Leather.LEATHER_COIF},
        {8653, 1, Leather.LEATHER_COWL}, {8652, 5, Leather.LEATHER_COWL}, {8651, 10, Leather.LEATHER_COWL},
    };

    public static void load() {
        for (Gem gem : Gem.values()) {
            Crafting.addCraftable(gem);
        }
        for (Hide hide : Hide.values()) {
            Crafting.addCraftable(hide);
        }
    }

    public static void addCraftable(Craftable craftable) {
        if (CRAFTABLES.put(craftable.getWith().getId(), craftable) != null) {
            System.out.println("[Crafting] Conflicting item values: " + craftable.getWith().getId() + " Type: " + craftable.getName());
        }
    }

    private static Craftable getCraftable(int use, int with) {
        return CRAFTABLES.get(use) == null ? CRAFTABLES.get(with) : CRAFTABLES.get(use);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == POTTERY_OVEN_11601) {
                Jewellery.open(player);
                return true;
            }
        } else if(option == 2) {
            if (obj != null && obj.definition() != null && obj.definition().name != null && obj.definition().name.toLowerCase().contains("spinning wheel")) {
                Spinning.open(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == NpcIdentifiers.ELLIS) {
            Tanning.open(player);
            return true;
        }

        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject obj) {
        if (obj.getId() == 16469 && (item.getId() == 1783 || item.getId() == 1781)) {
            Glass.craft(player, Glass.GlassData.MOLTEN_GLASS, 28);
            return true;
        }
        if(obj.definition() != null && obj.definition().name != null && obj.definition().name.equalsIgnoreCase("Furnace") && (item.getId() == ZENYTE || item.getId() == ONYX || item.getId() == DRAGONSTONE_6903 || item.getId() == DIAMOND || item.getId() == RUBY || item.getId() == EMERALD || item.getId() == SAPPHIRE)) {
            Jewellery.open(player);
            return true;
        }
        if(obj.definition() != null && obj.definition().name != null && obj.definition().name.equalsIgnoreCase("Furnace") && item.getId() == ZENYTE_SHARD) {
            if (player.inventory().containsAll(ZENYTE_SHARD, ONYX)) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, "Fuse your onyx and zenyte shard?", "Yes - fuse them.", "Don't fuse any of my gems!");
                    setPhase(0);
                }

                @Override
                public void select(int option) {
                    if (getPhase() == 0) {
                        if (option == 1) {
                            if (player.inventory().contains(ZENYTE_SHARD) && player.inventory().contains(ONYX)) {
                                player.inventory().remove(new Item(ZENYTE_SHARD));
                                player.inventory().remove(new Item(ONYX));
                                player.inventory().add(new Item(UNCUT_ZENYTE));
                                player.message("You reach into the extremely hot flames and fuse the zenyte and onyx together, forming an uncut zenyte.");
                            }
                            stop();
                        } else if (option == 2) {
                            stop();
                        }
                    }
                }
            });
        } else {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.ITEM_STATEMENT, new Item(ZENYTE_SHARD), "", "You need a <col=000080>cut onyx<col=000000> to fuse with a <col=000080>zenyte shard<col=000000> to<br><col=000000>create an uncut zenyte.");
                    setPhase(0);
                }

                @Override
                public void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
        }
        return true;
    }
        return false;
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item first, Item second) {

        if (Stringing.useItem(player, first, second)) {
            return true;
        }

        if (first.getId() == 1785 && second.getId() == 1775 || first.getId() == 1775 && second.getId() == 1785) {
            Glass.open(player);
            return true;
        }

        if ((first.getId() == 1733 && second.getId() == 1741) || (first.getId() == 1741 && second.getId() == 1733)) {
            player.putAttrib(AttributeKey.CRAFTABLE_KEY, "HIDE");
            player.getInterfaceManager().open(2311);
            return true;
        }

        Craftable craftable = getCraftable(first.getId(), second.getId());
        if (craftable == null) {
            return false;
        }

        if (!craftable.getUse().equalIds(first) && !craftable.getUse().equalIds(second)) {
            player.message("You need to use this with " + Utils.getAOrAn(craftable.getUse().name()) + " " + craftable.getUse().name().toLowerCase() + " to craft this item.");
            return true;
        }

        switch (craftable.getCraftableItems().length) {

            case 1:
                player.putAttrib(AttributeKey.CRAFTABLE_KEY, craftable);
                player.getPacketSender().sendString(2799, "<br> <br> <br> <br>" + craftable.getCraftableItems()[0].getProduct().name());
                player.getPacketSender().sendInterfaceModel(1746, 170, craftable.getCraftableItems()[0].getProduct().getId());
                player.getPacketSender().sendChatboxInterface(4429);
                return true;
            case 2:
                player.putAttrib(AttributeKey.CRAFTABLE_KEY, craftable);
                player.getPacketSender().sendInterfaceModel(8869, 170, craftable.getCraftableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8870, 170, craftable.getCraftableItems()[1].getProduct().getId());
                player.getPacketSender().sendString(8874, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[0].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendString(8878, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[1].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendChatboxInterface(8866);
                return true;
            case 3:
                player.putAttrib(AttributeKey.CRAFTABLE_KEY, craftable);
                player.getPacketSender().sendInterfaceModel(8883, 170, craftable.getCraftableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8884, 170, craftable.getCraftableItems()[1].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8885, 170, craftable.getCraftableItems()[2].getProduct().getId());
                player.getPacketSender().sendString(8889, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[0].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendString(8893, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[1].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendString(8897, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[2].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendChatboxInterface(8880);
                return true;
            case 4:
                player.putAttrib(AttributeKey.CRAFTABLE_KEY, craftable);
                player.getPacketSender().sendInterfaceModel(8902, 170, craftable.getCraftableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8903, 170, craftable.getCraftableItems()[1].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8904, 170, craftable.getCraftableItems()[2].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8905, 170, craftable.getCraftableItems()[3].getProduct().getId());
                player.getPacketSender().sendString(8909, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[0].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendString(8913, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[1].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendString(8917, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[2].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendString(8921, "<br> <br> <br> <br>".concat(craftable.getCraftableItems()[3].getProduct().name().replace("d'hide ", "")));
                player.getPacketSender().sendChatboxInterface(8899);
                return true;
            case 5:
                player.putAttrib(AttributeKey.CRAFTABLE_KEY, craftable);
                player.getPacketSender().sendInterfaceModel(8941, 170, craftable.getCraftableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8942, 170, craftable.getCraftableItems()[1].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8943, 170, craftable.getCraftableItems()[2].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8944, 170, craftable.getCraftableItems()[3].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8945, 170, craftable.getCraftableItems()[4].getProduct().getId());
                player.getPacketSender().sendString(8949, "<br> <br> <br> <br>".concat("Body"));
                player.getPacketSender().sendString(8953, "<br> <br> <br> <br>".concat("Chaps"));
                player.getPacketSender().sendString(8957, "<br> <br> <br> <br>".concat("Vambraces"));
                player.getPacketSender().sendString(8961, "<br> <br> <br> <br>".concat("Bandana"));
                player.getPacketSender().sendString(8965, "<br> <br> <br> <br>".concat("Boots"));
                player.getPacketSender().sendChatboxInterface(8938);
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (Tanning.click(player, button)) {
            return true;
        }

        if (Glass.click(player, button)) {
            return true;
        }

        if (!player.hasAttrib(AttributeKey.CRAFTABLE_KEY)) {
            return false;
        }

        if (button == 2422) {
            return false;
        }

        if (player.getAttrib(AttributeKey.CRAFTABLE_KEY).equals("HIDE")) {
            for (Object[] i : LEATHER_ARMOR_IDS) {
                if ((int) i[0] == button) {
                    player.getInterfaceManager().close();
                    start(player, (Craftable) i[2], 0, (int) i[1]);
                    return true;
                }
            }
        }

        Craftable craftable = player.getAttribOr(AttributeKey.CRAFTABLE_KEY, Craftable.class);

        switch (button) {

            /* Option 1 - Make all */
            case 1747:
                start(player, craftable, 0, player.inventory().count(craftable.getWith().getId()));
                return true;

            /* Option 1 - Make 1 */
            case 2799:
            case 8909:
            case 8874:
            case 8889:
            case 8949:
                start(player, craftable, 0, 1);
                return true;

            /* Option 1 - Make 5 */
            case 2798:
            case 8908:
            case 8873:
            case 8888:
            case 8948:
                start(player, craftable, 0, 5);
                return true;

            /* Option 1 - Make 10 */
            case 8907:
            case 8872:
            case 8887:
            case 8947:
                start(player, craftable, 0, 10);
                return true;

            /* Option 1 - Make X */
            case 1748:
            case 8906:
            case 8871:
            case 8886:
            case 6212:
            case 8946:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        start(player, craftable, 0, (Integer) value);
                        return true;
                    }
                });
                return true;

            /* Option 2 - Make 1 */
            case 8913:
            case 8878:
            case 8893:
            case 8953:
                start(player, craftable, 1, 1);
                return true;

            /* Option 2 - Make 5 */
            case 8912:
            case 8877:
            case 8892:
            case 8952:
                start(player, craftable, 1, 5);
                return true;

            /* Option 2 - Make 10 */
            case 8911:
            case 8876:
            case 8891:
            case 8951:
                start(player, craftable, 1, 10);
                return true;

            /* Option 2 - Make X */
            case 8910:
            case 8875:
            case 8890:
            case 8950:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        start(player, craftable, 1, (Integer) value);
                        return true;
                    }
                });
                return true;

            /* Option 3 - Make 1 */
            case 8917:
            case 8897:
            case 8957:
                start(player, craftable, 2, 1);
                return true;

            /* Option 3 - Make 5 */
            case 8916:
            case 8896:
            case 8956:
                start(player, craftable, 2, 5);
                return true;

            /* Option 3 - Make 10 */
            case 8915:
            case 8895:
            case 8955:
                start(player, craftable, 2, 10);
                return true;

            /* Option 3 - Make X */
            case 8914:
            case 8894:
            case 8954:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        start(player, craftable, 2, (Integer) value);
                        return true;
                    }
                });
                return true;

            /* Option 4 - Make 1 */
            case 8921:
            case 8961:
                start(player, craftable, 3, 1);
                return true;

            /* Option 4 - Make 5 */
            case 8920:
            case 8960:
                start(player, craftable, 3, 5);
                return true;

            /* Option 4 - Make 10 */
            case 8919:
            case 8959:
                start(player, craftable, 3, 10);
                return true;

            /* Option 4 - Make X */
            case 8918:
            case 8958:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        start(player, craftable, 3, (Integer) value);
                        return true;
                    }
                });
                return true;

            /* Option 5 - Make 1 */
            case 8965:
                start(player, craftable, 4, 1);
                return true;

            /* Option 5 - Make 5 */
            case 8964:
                start(player, craftable, 4, 5);
                return true;

            /* Option 5 - Make 10 */
            case 8963:
                start(player, craftable, 4, 10);
                return true;

            /* Option 5 - Make X */
            case 8962:
                player.setAmountScript("Enter amount:", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        start(player, craftable, 4, (Integer) value);
                        return true;
                    }
                });
                return true;

            default:
                return false;
        }
    }

    public boolean craft(Player player, int index, int amount) {
        Craftable craftable = player.getAttrib(AttributeKey.CRAFTABLE_KEY);
        return start(player, craftable, index, amount);
    }

    public static boolean start(Player player, Craftable craftable, int index, int amount) {
        if (craftable == null) {
            return false;
        }

        player.clearAttrib(AttributeKey.CRAFTABLE_KEY);

        CraftableItem item = craftable.getCraftableItems()[index];

        player.getInterfaceManager().close();

        if (player.getSkills().level(Skills.CRAFTING) < item.getLevel()) {
            DialogueManager.sendStatement(player,"<col=369>You need a Crafting level of " + item.getLevel() + " to do that.");
            return true;
        }

        //System.out.println(Arrays.toString(craftable.getIngredients(index)));
        if (!player.inventory().containsAll(craftable.getIngredients(index))) {
            Item requiredItem = craftable.getCraftableItems()[index].getRequiredItem();
            Item product = craftable.getCraftableItems()[index].getProduct();
            String productAmount = "";

            if (product.name().contains("vamb")) {
                productAmount = " pair of";
            } else if (!product.name().endsWith("s")) {
                productAmount = " " + Utils.getAOrAn(product.name());
            }
            //TODO look into message it prints in incorrect order.

            player.message("You need " + requiredItem.getAmount() + " piece" + (requiredItem.getAmount() > 1 ? "s" : "") + " of " + requiredItem.name().toLowerCase() + " to make" + productAmount + " " + product.name().toLowerCase() + ".");
            return true;
        }

        player.action.execute(craft(player, craftable, item, index, amount), true);
        return true;
    }

    private static Action<Player> craft(Player player, Craftable craftable, CraftableItem item, int index, int amount) {
        return new Action<>(player, 2, true) {
            int iterations = 0;

            @Override
            public void execute() {
                player.animate(craftable.getAnimation());
                player.getSkills().addXp(Skills.CRAFTING, item.getExperience());
                player.inventory().removeAll(craftable.getIngredients(index));
                player.inventory().add(item.getProduct());

                if (craftable.getProductionMessage() != null) {
                    player.message(craftable.getProductionMessage());
                }

                if (craftable.getName().equalsIgnoreCase("Gem")) {
                    if (item.getProduct().getId() == 1615) {
                        player.getTaskMasterManager().increase(Tasks.CRAFT_DRAGONSTONES);
                    }
                }

                if (craftable == Gem.SAPPHIRE) {
                    AchievementsManager.activate(player, Achievements.CRAFTING_I, 1);
                }
                if (craftable == Gem.EMERALD) {
                    AchievementsManager.activate(player, Achievements.CRAFTING_II, 1);
                }
                if (craftable == Gem.RUBY) {
                    AchievementsManager.activate(player, Achievements.CRAFTING_III, 1);
                }
                if (craftable == Gem.DIAMOND) {
                    AchievementsManager.activate(player, Achievements.CRAFTING_IV, 1);
                }


                if (++iterations == amount) {
                    stop();
                    return;
                }

                if (!player.inventory().containsAll(craftable.getIngredients(index))) {
                    stop();
                    DialogueManager.sendStatement(player, "<col=369>You have run out of materials.");
                }
            }

            @Override
            public String getName() {
                return "Crafting";
            }

            @Override
            public boolean prioritized() {
                return false;
            }

            @Override
            public WalkablePolicy getWalkablePolicy() {
                return WalkablePolicy.NON_WALKABLE;
            }
        };
    }

}
