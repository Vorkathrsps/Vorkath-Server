package com.aelous.model.content.skill.impl.fletching;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.content.skill.impl.fletching.impl.*;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 17, 2020
 */
public class Fletching extends PacketInteraction {

    private static final Logger logger = LoggerFactory.getLogger(Fletching.class);

    private final static HashMap<Integer, Fletchable> FLETCHABLES = new HashMap<>();

    public static void load() {
        Arrow.load();
        Bolt.load();
        Carvable.load();
        Crossbow.load();
        Featherable.load();
        Stringable.load();
        Javelin.load();
    }

    public static void addFletchable(Fletchable fletchable) {
        FLETCHABLES.put(fletchable.getWith().getId(), fletchable);
    }

    private static Fletchable getFletchable(int use, int with) {
        return FLETCHABLES.getOrDefault(use, FLETCHABLES.get(with));
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        Fletchable fletchable = getFletchable(use.getId(), usedWith.getId());

        if (fletchable == null || use.getId() == 590 || usedWith.getId() == 590) {
            return false;
        }

        if (!fletchable.getUse().equalIds(use) && !fletchable.getUse().equalIds(usedWith)) {
            player.message("You need to use this with " + Utils.getAOrAn(fletchable.getUse().name()) + " " + fletchable.getUse().name().toLowerCase() + " to fletch this item.");
            return true;
        }

        String prefix = fletchable.getWith().name().split(" ")[0];

        switch (fletchable.getFletchableItems().length) {

            case 1:
                player.putAttrib(AttributeKey.FLETCHABLE_KEY, fletchable);
                player.getPacketSender().sendString(2799, "<br> <br> <br> <br>" + fletchable.getFletchableItems()[0].getProduct().name());
                player.getPacketSender().sendInterfaceModel(1746, 170, fletchable.getFletchableItems()[0].getProduct().getId());
                player.getPacketSender().sendChatboxInterface(4429);
                return true;
            case 2:
                player.putAttrib(AttributeKey.FLETCHABLE_KEY, fletchable);
                player.getPacketSender().sendInterfaceModel(8869, 170, fletchable.getFletchableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8870, 170, fletchable.getFletchableItems()[1].getProduct().getId());
                player.getPacketSender().sendString(8874, "<br> <br> <br> <br>".concat(prefix + " Short Bow"));
                player.getPacketSender().sendString(8878, "<br> <br> <br> <br>".concat(prefix + " Long Bow"));
                player.getPacketSender().sendChatboxInterface(8866);
                return true;
            case 3:
                player.putAttrib(AttributeKey.FLETCHABLE_KEY, fletchable);
                player.getPacketSender().sendInterfaceModel(8883, 170, fletchable.getFletchableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8884, 170, fletchable.getFletchableItems()[1].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8885, 170, fletchable.getFletchableItems()[2].getProduct().getId());
                player.getPacketSender().sendString(8889, "<br> <br> <br> <br>".concat(prefix + " Short Bow"));
                player.getPacketSender().sendString(8893, "<br> <br> <br> <br>".concat(prefix + " Long Bow"));
                player.getPacketSender().sendString(8897, "<br> <br> <br> <br>".concat("Crossbow Stock"));
                player.getPacketSender().sendChatboxInterface(8880);
                return true;
            case 4:
                player.putAttrib(AttributeKey.FLETCHABLE_KEY, fletchable);
                player.getPacketSender().sendInterfaceModel(8902, 170, fletchable.getFletchableItems()[0].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8903, 170, fletchable.getFletchableItems()[1].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8904, 170, fletchable.getFletchableItems()[2].getProduct().getId());
                player.getPacketSender().sendInterfaceModel(8905, 170, fletchable.getFletchableItems()[3].getProduct().getId());
                player.getPacketSender().sendString(8909, "<br> <br> <br> <br>".concat(fletchable.getFletchableItems()[0].getProduct().getAmount()+" Arrow Shafts"));
                player.getPacketSender().sendString(8913, "<br> <br> <br> <br>".concat("Short Bow"));
                player.getPacketSender().sendString(8917, "<br> <br> <br> <br>".concat("Long Bow"));
                player.getPacketSender().sendString(8921, "<br> <br> <br> <br>".concat("Crossbow Stock"));
                player.getPacketSender().sendChatboxInterface(8899);
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (!player.hasAttrib(AttributeKey.FLETCHABLE_KEY)) {
            return false;
        }

        Fletchable fletchable = player.getAttribOr(AttributeKey.FLETCHABLE_KEY, Fletchable.class);

        switch (button) {

            /* Option 1 - Make all */
            case 1747:
                start(player, fletchable, 0, player.inventory().count(fletchable.getWith().getId()));
                return true;

            /* Option 1 - Make 1 */
            case 2799:
            case 8909:
            case 8874:
            case 8889:
                start(player, fletchable, 0, 1);
                return true;

            /* Option 1 - Make 5 */
            case 2798:
            case 8908:
            case 8873:
            case 8888:
                start(player, fletchable, 0, 5);
                return true;

            /* Option 1 - Make 10 */
            case 1748:
            case 8907:
            case 8872:
            case 8887:
                start(player, fletchable, 0, 10);
                return true;

            /* Option 1 - Make X */
            case 8906:
            case 8871:
            case 8886:
            case 6212:
                try {
                    player.setAmountScript("How many would you like to make?", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            start(player, fletchable, 0, (Integer) value);
                            return true;
                        }
                    });
                } catch (Exception ex) {
                    logger.error(String.format("player=%s error fletching option1: make-x", player.getUsername()), ex);
                }
                return true;

            /* Option 2 - Make 1 */
            case 8913:
            case 8878:
            case 8893:
                start(player, fletchable, 1, 1);
                return true;

            /* Option 2 - Make 5 */
            case 8912:
            case 8877:
            case 8892:
                start(player, fletchable, 1, 5);
                return true;

            /* Option 2 - Make 10 */
            case 8911:
            case 8876:
            case 8891:
                start(player, fletchable, 1, 10);
                return true;

            /* Option 2 - Make X */
            case 8910:
            case 8875:
            case 8890:
                try {
                    player.setAmountScript("How many would you like to make?", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            start(player, fletchable, 1, (Integer) value);
                            return true;
                        }
                    });
                } catch (Exception ex) {
                    logger.error(String.format("player=%s error fletching option1: make-x", player.getUsername()), ex);
                }
                return true;

            /* Option 3 - Make 1 */
            case 8917:
            case 8897:
                start(player, fletchable, 2, 1);
                return true;

            /* Option 3 - Make 5 */
            case 8916:
            case 8896:
                start(player, fletchable, 2, 5);
                return true;

            /* Option 3 - Make 10 */
            case 8915:
            case 8895:
                start(player, fletchable, 2, 10);
                return true;

            /* Option 3 - Make X */
            case 8914:
            case 8894:
                try {

                    player.setAmountScript("How many would you like to make?", new InputScript() {

                            @Override
                            public boolean handle(Object value) {
                                start(player, fletchable, 2, (Integer) value);
                                return true;
                            }
                        });
                } catch (Exception ex) {
                    logger.error(String.format("player=%s error fletching option2: make-x", player.getUsername()), ex);
                }
                return true;

            /* Option 4 - Make 1 */
            case 8921:
                start(player, fletchable, 3, 1);
                return true;

            /* Option 4 - Make 5 */
            case 8920:
                start(player, fletchable, 3, 5);
                return true;

            /* Option 4 - Make 10 */
            case 8919:
                start(player, fletchable, 3, 10);
                return true;

            /* Option 4 - Make X */
            case 8918:
                try {

                    player.setAmountScript("How many would you like to make?", new InputScript() {

                            @Override
                            public boolean handle(Object value) {
                                start(player, fletchable, 3, (Integer) value);
                                return true;
                            }
                        });
                } catch (Exception ex) {
                    logger.error(String.format("player=%s error fletching option3: make-x", player.getUsername()), ex);
                }
                return true;

            default:
                return false;
        }
    }

    private void start(Player player, Fletchable fletchable, int index, int amount) {
        if (fletchable == null) {
            return;
        }

        player.clearAttrib(AttributeKey.FLETCHABLE_KEY);
        FletchableItem item = fletchable.getFletchableItems()[index];
        player.getInterfaceManager().close();

        if (player.getSkills().level(Skills.FLETCHING) < item.getLevel()) {
            DialogueManager.sendStatement(player,"<col=369>You need a Fletching level of " + item.getLevel() + " to do that.");
            return;
        }

        if (!(player.inventory().containsAll(fletchable.getIngediants()))) {
            String firstName = fletchable.getUse().name().toLowerCase();
            String secondName = fletchable.getWith().name().toLowerCase();

            if (fletchable.getUse().getAmount() > 1 && !firstName.endsWith("s")) {
                firstName = firstName.concat("s");
            }

            if (fletchable.getWith().getAmount() > 1 && !secondName.endsWith("s")) {
                secondName = secondName.concat("s");
            }

            if (fletchable.getUse().getAmount() == 1 && firstName.endsWith("s")) {
                firstName = firstName.substring(0, firstName.length() - 1);
            }

            if (fletchable.getWith().getAmount() == 1 && secondName.endsWith("s")) {
                secondName = secondName.substring(0, secondName.length() - 1);
            }

            final String firstAmount;

            if (fletchable.getUse().getAmount() == 1) {
                firstAmount = Utils.getAOrAn(fletchable.getUse().name());
            } else {
                firstAmount = String.valueOf(fletchable.getUse().getAmount());
            }

            final String secondAmount;

            if (fletchable.getWith().getAmount() == 1) {
                secondAmount = Utils.getAOrAn(fletchable.getWith().name());
            } else {
                secondAmount = String.valueOf(fletchable.getWith().getAmount());
            }

            String firstRequirement = firstAmount + " " + firstName;
            String secondRequirement = secondAmount + " " + secondName;
            player.message("You need " + firstRequirement + " and " + secondRequirement + " to do that.");
            return;
        }
        player.action.execute(fletch(player, fletchable, item, amount), true);
    }

    private Action<Player> fletch(Player player, Fletchable fletchable, FletchableItem item, int amount) {
        return new Action<>(player, 2, true) {
            int iterations = 0;

            @Override
            public void execute() {
                ++iterations;

                player.animate(fletchable.getAnimation());
                player.getSkills().addXp(Skills.FLETCHING, item.getExperience() * item.getProduct().getAmount());
                player.inventory().removeAll(fletchable.getIngediants());
                player.inventory().add(item.getProduct());

                if (fletchable.getProductionMessage() != null) {
                    player.message(fletchable.getProductionMessage());
                }

                if(fletchable.getName().equalsIgnoreCase("Stringable")) {
                    if(item.getProduct().name().equalsIgnoreCase("Magic shortbow")) {
                        player.getTaskMasterManager().increase(Tasks.MAGIC_SHORTBOW);
                    }
                }

                if (iterations == amount) {
                    stop();
                    return;
                } else if (iterations > 28) {
                    stop();
                    return;
                }

                if (!(player.inventory().containsAll(fletchable.getIngediants()))) {
                    stop();
                    DialogueManager.sendStatement(player, "<col=369>You have run out of materials.");
                }
            }

            @Override
            public String getName() {
                return "Fletching";
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
