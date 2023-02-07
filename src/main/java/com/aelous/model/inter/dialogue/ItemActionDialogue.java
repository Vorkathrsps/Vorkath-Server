package com.aelous.model.inter.dialogue;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.skill.impl.runecrafting.RuneConversion;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * @author Patrick van Elderen | December, 16, 2020, 18:28
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ItemActionDialogue {

    public static void sendInterface(Player player, int... item) {
        switch (item.length) {
            case 1 -> {
                ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, item[0]);
                player.getPacketSender().sendString(2799, "" + def.name);
                player.getPacketSender().sendInterfaceModel(1746, 170, item[0]);
                player.getPacketSender().sendChatboxInterface(4429);
            }

            case 2 -> {
                ItemDefinition firstItem = World.getWorld().definitions().get(ItemDefinition.class, item[0]);
                ItemDefinition secondItem = World.getWorld().definitions().get(ItemDefinition.class, item[1]);
                player.getPacketSender().sendInterfaceModel(8869, 170, item[0]);
                player.getPacketSender().sendInterfaceModel(8870, 170, item[1]);
                player.getPacketSender().sendString(8874, firstItem.name);
                player.getPacketSender().sendString(8878, secondItem.name);
                player.getPacketSender().sendChatboxInterface(8866);
            }

            case 3 -> {
                ItemDefinition firstItem = World.getWorld().definitions().get(ItemDefinition.class, item[0]);
                ItemDefinition secondItem = World.getWorld().definitions().get(ItemDefinition.class, item[1]);
                ItemDefinition thirdItem = World.getWorld().definitions().get(ItemDefinition.class, item[2]);
                player.getPacketSender().sendInterfaceModel(8883, 170, item[0]);
                player.getPacketSender().sendInterfaceModel(8884, 170, item[1]);
                player.getPacketSender().sendInterfaceModel(8885, 170, item[2]);
                player.getPacketSender().sendString(8889, firstItem.name);
                player.getPacketSender().sendString(8893, secondItem.name);
                player.getPacketSender().sendString(8897, thirdItem.name);
                player.getPacketSender().sendChatboxInterface(8880);
            }

            case 4 -> {
                ItemDefinition firstItem = World.getWorld().definitions().get(ItemDefinition.class, item[0]);
                ItemDefinition secondItem = World.getWorld().definitions().get(ItemDefinition.class, item[1]);
                ItemDefinition thirdItem = World.getWorld().definitions().get(ItemDefinition.class, item[2]);
                ItemDefinition fourthItem = World.getWorld().definitions().get(ItemDefinition.class, item[3]);
                player.getPacketSender().sendInterfaceModel(8902, 170, item[0]);
                player.getPacketSender().sendInterfaceModel(8903, 170, item[1]);
                player.getPacketSender().sendInterfaceModel(8904, 170, item[2]);
                player.getPacketSender().sendInterfaceModel(8905, 170, item[3]);
                player.getPacketSender().sendString(8909, firstItem.name);
                player.getPacketSender().sendString(8913, secondItem.name);
                player.getPacketSender().sendString(8917, thirdItem.name);
                player.getPacketSender().sendString(8921, fourthItem.name);
                player.getPacketSender().sendChatboxInterface(8899);
            }
        }
    }

    public static boolean clickButton(Player player, int button) {
        switch (button) {

            case 8909 -> {
                if (player.<Boolean>getAttribOr(RUNECRAFTING,false)) {
                    //First click dialogues
                    switch (player.<Integer>getAttribOr(RC_DIALOGUE, 1)) {
                        //Air rune
                        case 1 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.AIR,1), true);
                        //Fire rune
                        case 2 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.FIRE,1), true);
                        //Nature rune
                        case 3 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.NATURE,1), true);
                        default -> throw new IllegalStateException("Unexpected value: " + player.getAttribOr(RC_DIALOGUE, 1));
                    }
                }
                return true;
            }
            case 8913 -> {
                if (player.<Boolean>getAttribOr(RUNECRAFTING,false)) {
                    switch (player.<Integer>getAttribOr(RC_DIALOGUE, 1)) {
                        //Mind rune
                        case 1 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.MIND, 1), true);
                        //Body rune
                        case 2 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.BODY, 1), true);
                        //Law rune
                        case 3 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.LAW, 1), true);
                        default -> throw new IllegalStateException("Unexpected value: " + player.getAttribOr(RC_DIALOGUE, 1));
                    }
                }
                return true;
            }
            case 8917 -> {
                if (player.<Boolean>getAttribOr(RUNECRAFTING,false)) {
                    switch (player.<Integer>getAttribOr(RC_DIALOGUE, 1)) {
                        //Water rune
                        case 1 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.WATER, 1), true);
                        //Cosmic rune
                        case 2 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.COSMIC, 1), true);
                        //Death rune
                        case 3 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.DEATH, 1), true);
                        default -> throw new IllegalStateException("Unexpected value: " + player.getAttribOr(RC_DIALOGUE, 1));
                    }
                }
                return true;
            }
            case 8921 -> {
                if (player.<Boolean>getAttribOr(RUNECRAFTING,false)) {
                    switch (player.<Integer>getAttribOr(RC_DIALOGUE, 1)) {
                        //Earth rune
                        case 1 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.EARTH, 1), true);
                        //Chaos rune
                        case 2 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.CHAOS, 1), true);
                        //Blood rune
                        case 3 -> player.action.execute(RuneConversion.action(player, RuneConversion.Altar.BLOOD, 1), true);
                        default -> throw new IllegalStateException("Unexpected value: " + player.getAttribOr(RC_DIALOGUE, 1));
                    }
                }
                return true;
            }
        }
        return false;
    }

}
