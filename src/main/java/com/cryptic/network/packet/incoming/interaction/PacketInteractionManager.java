package com.cryptic.network.packet.incoming.interaction;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.map.object.GameObject;
import com.google.common.reflect.ClassPath;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the packet interaction
 *
 * @author 2012
 */
public class PacketInteractionManager {

    private static final Logger log = LoggerFactory.getLogger(PacketInteractionManager.class);

    /**
     * The packet interactions
     */
    private static final List<PacketInteraction> interactions = new ArrayList<>();

    /**
     * Loads all the interaction
     */
    public static void init() {
        try {
            loadRecursive();
        } catch (IOException ex) {
            log.error("Exception whilst loading packet interactions.", ex);
        }
        log.info("Loaded {} packet interactions", interactions.size());
    }

    private static void loadRecursive() throws IOException {
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .enableMethodInfo()
            .acceptPackages("com.cryptic.model")
            .scan()) {
            List<Class<PacketInteraction>> classes = scanResult
                .getSubclasses(PacketInteraction.class)
                .filter(classInfo -> {
                    if (classInfo.isAbstract()) return false;

                    MethodInfoList methodInfos = classInfo.getMethodInfo("<init>");
                    return methodInfos.stream().anyMatch(
                        methodInfo ->
                            methodInfo.isPublic()
                                && !methodInfo.isSynthetic()
                                && methodInfo.getParameterInfo().length == 0);
                })
                .loadClasses(PacketInteraction.class);
            for (Class<PacketInteraction> clazz : classes) {
                PacketInteractionManager.load(clazz);
            }
        }
    }

    public static void onRegionChange(Player player) {
        for (PacketInteraction interaction : interactions) {
            interaction.onRegionChange(player);
        }
    }

    public static void onPlayerProcess(Player player) {
        for (PacketInteraction interaction : interactions) {
            interaction.onPlayerProcess(player);
        }
    }

    public static void onLogin(Player player) {
        for (PacketInteraction interaction : interactions) {
            interaction.onLogin(player);
        }
    }

    public static boolean onEquipItem(Player player, Item item) {
        for(PacketInteraction interaction : interactions) {
            if(interaction.handleEquipment(player, item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean onEquipmentAction(Player player, Item item, int slot) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleEquipmentAction(player, item, slot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks button interaction
     *
     * @param player
     *            the player
     * @param button
     *            the buttons
     * @return interaction
     */
    public static boolean checkButtonInteraction(Player player, int button) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleButtonInteraction(player, button)) {
                //System.out.println("checkButtonInteraction prints "+interaction.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

    /**
     * Checks item interaction
     *
     * @param player
     *            player
     * @param item
     *            the item
     * @param option
     *            the type
     * @return interaction
     */
    public static boolean checkItemInteraction(Player player, Item item, int option) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleItemInteraction(player, item, option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks object interaction
     *
     * @param player
     *            the player
     * @param object
     *            the object
     * @param type
     *            the type
     * @return interaction
     */
    public static boolean checkObjectInteraction(Player player, GameObject object, int type) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleObjectInteraction(player, object, type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks npc interaction
     *
     * @param player
     *            the player
     * @param npc
     *            the npc
     * @param type
     *            the type
     * @return interaction
     */
    public static boolean checkNpcInteraction(Player player, NPC npc, int type) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleNpcInteraction(player, npc, type)) {
                //System.out.println("checkNpcInteraction prints "+interaction.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

    /**
     * Checks item on item interaction
     *
     * @param player
     *            the player
     * @param use
     *            the use
     * @param usedWith
     *            the used with
     * @return interaction
     */
    public static boolean checkItemOnItemInteraction(Player player, Item use, Item usedWith) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleItemOnItemInteraction(player, use, usedWith)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks item on player interaction
     *
     * @param player
     *            the player
     * @param use
     *            the use
     * @param usedWith
     *            the used with
     * @return interaction
     */
    public static boolean checkItemOnPlayerInteraction(Player player, Item use, Player usedWith) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleItemOnPlayer(player, use, usedWith)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks item on object interaction
     *
     * @param player
     *            the player
     * @param item
     *            the item
     * @param gameObject
     *            the game object
     * @return interaction
     */
    public static boolean checkItemOnObjectInteraction(Player player, Item item, GameObject gameObject) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleItemOnObject(player, item, gameObject)) {
                //System.out.println("checkItemOnObjectInteraction prints "+interaction.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

    public static boolean checkItemOnNpcInteraction(Player player, Item item, NPC npc) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleItemOnNpc(player, item, npc)) {
                //System.out.println("checkItemOnNpcInteraction prints "+interaction.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

    /**
     * qoL from w.e this base was to Runite
     * @author Jak Shadowrs tardisfan121@gmail.com
     */
    @FunctionalInterface
    public
    interface GameObjHandler {
        boolean handleObjectInteraction(Player player, GameObject object, int option);
    }

    /**
     * qoL from w.e this base was to Runite
     * @author Jak Shadowrs tardisfan121@gmail.com
     */
    public static void addObjectInteraction(GameObjHandler i) {
        interactions.add(new PacketInteraction() {
            @Override
            public boolean handleObjectInteraction(Player player, GameObject object, int option) {
                return i.handleObjectInteraction(player, object, option);
            }
        });
    }
    /**
     * Checks item container interaction
     *
     * @param player
     *            the player
     * @param item
     *            the item
     * @param slot
     *            the slot
     * @param interfaceId
     *            the interface id
     * @param type
     *            the type
     * @return interaction
     */
    public static boolean checkItemContainerActionInteraction(Player player, Item item, int slot, int interfaceId, int type) {
        for (PacketInteraction interaction : interactions) {
            if (interaction.handleItemContainerActionInteraction(player, item, slot, interfaceId, type)) {
                return true;
            }
        }
        return false;
    }

    private static void load(Class<PacketInteraction> clazz) {
/*        if (Modifier.isAbstract(clazz.getModifiers())
            || clazz.isAnonymousClass()
            || clazz.isEnum()
            || clazz.isInterface()) {
            //System.err.println("Unable to load this class: "+clazz.getName()+" abstract class: "+Modifier.isAbstract(clazz.getModifiers())+" anonymousClass: "+clazz.isAnonymousClass()+" enumClass: "+clazz.isEnum()+" interfaceClass: "+clazz.isInterface());
            return;
        }*/

        ////System.out.println("Found: "+clazz.getName() + "default cons: "+hasDefaultConstructor(clazz)+" superclass: "+isSuperClass(clazz, PacketInteraction.class));

        // A valid interaction must extend PacketInteraction and have a default zero-parameters
        // constructor.
        //if (hasDefaultConstructor(clazz) && isSuperClass(clazz, PacketInteraction.class)) {
        //System.err.println("Enter class: "+clazz.getName());
        try {
            // Try to create an instance of that type
            PacketInteraction interaction = clazz.getDeclaredConstructor().newInstance();

            if (!interactions.contains(interaction)) {
                interactions.add(interaction);
                // System.err.println("Add class: "+clazz.getName());
            }
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ex) {
            //System.err.println("Unable to load class: "+ clazz.getName());
        }
        //}
    }

    private static boolean hasDefaultConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    /** Recursive method to determine if a class has a given super class. */
    private static boolean isSuperClass(Class<?> clazz, Class<?> superClass) {
        if (clazz == null) return false;
        if (clazz.getSuperclass() == superClass) return true;
        return isSuperClass(clazz.getSuperclass(), superClass);
    }

}
