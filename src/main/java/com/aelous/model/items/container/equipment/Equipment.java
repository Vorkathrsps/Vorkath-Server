package com.aelous.model.items.container.equipment;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.areas.edgevile.Mac;
import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.content.items.equipment.max_cape.MaxCape;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.items.Item;
import com.aelous.model.items.ItemWeight;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.ItemContainerAdapter;
import com.aelous.model.items.container.inventory.Inventory;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.timers.TimerKey;
import com.google.common.collect.ImmutableSet;

import java.util.*;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * The container that manages the equipment for a player.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Equipment extends ItemContainer {

    /**
     * The size of all equipment instances.
     */
    public static final int SIZE = 14;

    /**
     * The error message printed when certain functions from the superclass are
     * utilized.
     */
    private static final String EXCEPTION_MESSAGE = "Please use { equipment.set(index, Item) } instead";

    /**
     * An {@link ImmutableSet} containing equipment indexes that don't require
     * appearance updates.
     */
    private static final ImmutableSet<Integer> NO_APPEARANCE = ImmutableSet.of(EquipSlot.RING, EquipSlot.AMMO);

    /**
     * The player who's equipment is being managed.
     */
    private final Player player;

    /**
     * Creates a new {@link Equipment}.
     */
    public Equipment(Player player) {
        super(SIZE, StackPolicy.STANDARD);
        this.player = player;
        addListener(new EquipmentListener());
    }

    public static boolean hasAmmyOfDamned(Player player) {
        return player.getEquipment().hasAt(EquipSlot.AMULET, 12853) || player.getEquipment().hasAt(EquipSlot.AMULET, 12851);
    }

    public static boolean hasBloodFury(Player player) {
        return player.getEquipment().hasAt(EquipSlot.AMULET, AMULET_OF_BLOOD_FURY);
    }

    public static boolean fullTorag(Player player) {
        return player.getEquipment().containsAll(4745, 4747, 4749, 4751);
    }

    public static boolean fullAhrim(Player player) {
        return player.getEquipment().containsAll(4708, 4710, 4712, 4714);
    }

    public static boolean fullKaril(Player player) {
        return player.getEquipment().containsAll(4732, 4734, 4736, 4738);
    }

    public static boolean hasVerac(Player player) {
        Item helm = player.getEquipment().get(EquipSlot.HEAD);
        Item body = player.getEquipment().get(EquipSlot.BODY);
        Item legs = player.getEquipment().get(EquipSlot.LEGS);
        Item wep = player.getEquipment().get(EquipSlot.WEAPON);

        // Now check for matching ids
        if (helm != null && (helm.getId() != 4753 && helm.getId() != 4976 && helm.getId() != 4977 && helm.getId() != 4978 && helm.getId() != 4979)) {
            return false;
        }
        if (body != null && (body.getId() != 4757 && body.getId() != 4988 && body.getId() != 4989 && body.getId() != 4990 && body.getId() != 4991)) {
            return false;
        }
        if (legs != null && legs.getId() != 4759 && (legs.getId() != 4994 && legs.getId() != 4995 && legs.getId() != 4996 && legs.getId() != 4997)) {
            return false;
        }
        if (wep != null && wep.getId() != 4755 && (wep.getId() != 4982 && wep.getId() != 4983 && wep.getId() != 4984 && wep.getId() != 4985)) {
            return false;
        }

        return true;
    }

    public static boolean hasDragonProtectionGear(Player player) {
        return player.getEquipment().hasShield() && (isWearingDFS(player) || player.getEquipment().getShield().getId() == ItemIdentifiers.ANTIDRAGON_SHIELD);
    }

    public static boolean isWearingDFS(Player player) {
        Item shield = player.getEquipment().get(EquipSlot.SHIELD);
        if (shield != null) {
            if (shield.getId() == DRAGONFIRE_SHIELD || shield.getId() == ANCIENT_WYVERN_SHIELD || shield.getId() == DRAGONFIRE_WARD) {
                return true;
            }
        }
        return false;
    }

    public static boolean darkbow(int itemId) {
        return itemId == DARK_BOW || (itemId >= 12765 && itemId <= 12768);
    }

    public static boolean notAvas(Player player) {
        Item cape = player.getEquipment().get(EquipSlot.CAPE);
        return Math.random() <= 0.2 || cape == null || !wearingAvasEffect(player);
    }

    public static boolean wearingAvasEffect(Player player) {
        Item cape = player.getEquipment().get(EquipSlot.CAPE);
        return cape != null && player.getEquipment().containsAny(10498, 10499, 13337, 9756, 9757, RANGING_CAPE, RANGING_CAPET, 22109, 21898, MASORI_ASSEMBLER, MASORI_ASSEMBLER_L, MASORI_ASSEMBLER_MAX_CAPE);
    }

    public static boolean fullFremennik(Player player) {
        return player.getEquipment().contains(3748) || player.getEquipment().contains(3757) || player.getEquipment().contains(3758);
    }

    public static boolean justiciarSet(Player player) {
        Item helm = player.getEquipment().get(EquipSlot.HEAD);
        Item chest = player.getEquipment().get(EquipSlot.BODY);
        Item legs = player.getEquipment().get(EquipSlot.LEGS);
        return (helm == null || helm.getId() == 22326) && (chest == null || chest.getId() == 22327) && (legs == null || legs.getId() == 22328);
    }

    public static boolean corruptedCrystalSet(Player player) {
        Item helm = player.getEquipment().get(EquipSlot.HEAD);
        Item chest = player.getEquipment().get(EquipSlot.BODY);
        Item legs = player.getEquipment().get(EquipSlot.LEGS);
        return (helm == null || helm.getId() == 30032) && (chest == null || chest.getId() == 30030) && (legs == null || legs.getId() == 30031);
    }

    public static boolean targetIsSlayerTask(Player player, Entity target) {
        if (target.isNpc()) {
            var type = (target.getAsNpc()).id();
            if (type == NpcIdentifiers.COMBAT_DUMMY || Slayer.creatureMatches(player, type)) { // 2668 is combat dummy, always does max hit.
                return true;
            }
        }
        return false;
    }

    public int hpIncrease() {
        int hpIncrease = 0;

        for (int index = 0; index < getItems().length; index++) {
            Item item = get(index);
            if (item == null)
                continue;
            int id = item.getId();
            if (index == EquipSlot.HEAD) {
                if (id == 12029 // torva
                    || id == 12026 // pernix
                    || id == 12023 // virtus
                )
                    hpIncrease += 6;

            } else if (index == EquipSlot.BODY) {
                if (id == 12028 // torva
                    || id == 12025 // pernix
                    || id == 12022 // virtus
                )
                    hpIncrease += 20;
            } else if (index == EquipSlot.LEGS) {
                if (id == 12027 // torva
                    || id == 12024 // pernix
                    || id == 12021 // virtus
                )
                    hpIncrease += 13;
            }
        }

        return hpIncrease;
    }

    private final int[] GRACEFUL_CAPES = new int[]{11852, 13581, 13593, 13605, 13617, 13629, 13669};

    private final int[] GRACEFUL_ITEMS = new int[]{11850, 11852, 11854, 11856, 11858, 11860};

    public boolean wearsFullGraceful() {
        return player.getEquipment().hasAllArr(GRACEFUL_ITEMS);
    }

    private final List<Integer> MAX_CAPES = Arrays.asList(
        MAX_CAPE, FIRE_MAX_CAPE, SARADOMIN_MAX_CAPE, ZAMORAK_MAX_CAPE, GUTHIX_MAX_CAPE, ACCUMULATOR_MAX_CAPE, MAX_CAPE_13342, ARDOUGNE_MAX_CAPE,
        INFERNAL_MAX_CAPE_21285, IMBUED_GUTHIX_MAX_CAPE, IMBUED_SARADOMIN_MAX_CAPE, IMBUED_ZAMORAK_MAX_CAPE, ASSEMBLER_MAX_CAPE, MYTHICAL_MAX_CAPE);

    private final List<Integer> MAX_HOODES = Arrays.asList(MAX_HOOD, FIRE_MAX_HOOD, SARADOMIN_MAX_HOOD, ZAMORAK_MAX_HOOD, GUTHIX_MAX_HOOD, ACCUMULATOR_MAX_HOOD, ARDOUGNE_MAX_HOOD, INFERNAL_MAX_HOOD, IMBUED_SARADOMIN_MAX_HOOD, IMBUED_ZAMORAK_MAX_HOOD, IMBUED_GUTHIX_MAX_HOOD, ASSEMBLER_MAX_HOOD, MYTHICAL_MAX_HOOD);

    public boolean wearingMaxCape() {
        for (int index : MAX_CAPES) {
            if (player.getEquipment().hasAt(EquipSlot.CAPE, index)) {
                return true;
            }
        }
        return false;
    }

    private final int[] SLAYER_HELMETS = new int[]{SLAYER_HELMET, SLAYER_HELMET_I, RED_SLAYER_HELMET, GREEN_SLAYER_HELMET, BLACK_SLAYER_HELMET, PURPLE_SLAYER_HELMET, PURPLE_SLAYER_HELMET_I, TURQUOISE_SLAYER_HELMET};

    public boolean wearingSlayerHelm() {
        for (int slayerHelm : SLAYER_HELMETS) {
            if (player.getEquipment().hasAt(EquipSlot.HEAD, slayerHelm)) {
                return true;
            }
        }
        return false;
    }

    public boolean wearingBeginnerWeapon() {
        List<Integer> beginner_weapons = Arrays.asList();
        for (int weapon : beginner_weapons) {
            if (player.getEquipment().hasAt(EquipSlot.WEAPON, weapon)) {
                return true;
            }
        }
        return false;
    }

    public boolean corpbeastArmour(Item weapon) {
        return (weapon != null && (World.getWorld().equipmentInfo().weaponType(weapon.getId()) != WeaponType.SPEAR)) || player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.ZAMORAKIAN_HASTA);
    }

    /**
     * Handles refreshing all the equipment items.
     */
    public void login() {
        for (int index = 0; index < getItems().length; index++) {
            set(index, get(index), true);
        }
        ItemWeight.calculateWeight(player);
        WeaponInterfaces.updateWeaponInterface(player);
        refresh();
    }

    public void replaceEquipment(int removed, int replaced, int slot, boolean refresh) {
        remove(new Item(removed), slot, true);

        manualWear(new Item(replaced, 1), true);
        if (refresh)
            refresh();
    }

    /**
     * Removes an item from the equipment container.
     *
     * @param item           The {@link Item} to withdraw.
     * @param preferredIndex The preferable index to withdraw {@code item} from.
     * @param refresh        The condition if we will be refreshing our container.
     */
    @Override
    public boolean remove(Item item, int preferredIndex, boolean refresh) {
        boolean removed = super.remove(item, preferredIndex, refresh);
        if (removed && !contains(item)) {
            this.appearanceForIndex(World.getWorld().equipmentInfo().slotFor(item.getId()));
        }
        return removed;
    }

    /**
     * Manually wears multiple items (does not have any restrictions).
     *
     * @param items The items to wear.
     */
    public void manualWearAll(Item[] items) {
        for (Item item : items) {
            manualWear(item, false);
        }
    }

    /**
     * Manually wears an item (does not have any restrictions).
     * Refreshes by default.
     *
     * @param item The item to wear.
     */
    public void manualWear(Item item, boolean notRequiredInInventory) {
        manualWear(item, notRequiredInInventory, true);
    }

    /**
     * Manually wears an item (does not have any restrictions).
     *
     * @param toWear The item to wear.
     * @param toWear do we want to refresh.
     */
    public void manualWear(Item toWear, boolean notRequiredInInventory, boolean refresh) {
        if (toWear == null)
            return;
        if (!notRequiredInInventory) {
            if (!player.inventory().contains(toWear))
                return;
        }
        EquipmentInfo info = World.getWorld().equipmentInfo();
        int targetSlot = info.slotFor(toWear.getId());

        if (targetSlot == -1)
            return;
        set(targetSlot, toWear, false);
        player.inventory().remove(toWear);
        appearanceForIndex(targetSlot);
        WeaponInterfaces.updateWeaponInterface(player);
        if (refresh)
            player.getEquipment().refresh();
    }

    public boolean equip(Item item) {
        int index = player.inventory().getSlot(item.getId());
        return equip(index);
    }

    public boolean equip(int inventoryIndex) {
        if (inventoryIndex == -1)
            return false;

        Inventory inventory = player.inventory();
        Item equip = inventory.get(inventoryIndex);
        if (!Item.valid(equip)) {
            return false;
        }

        if (!player.getInterfaceManager().isClear() && !player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.EQUIPMENT_SCREEN_INTERFACE_ID)) {
            player.getInterfaceManager().close(false);
        }

        //Check if the item has a proper equipment slot..
        EquipmentInfo info = World.getWorld().equipmentInfo();
        int equipmentSlot = info.slotFor(equip.getId());

        if (equipmentSlot == -1) {
            return false;
        }

        int id = equip.getId();

        if (player.stunned()) {
            player.message("You're currently stunned and cannot equip any armoury.");
            return false;
        }


        //Handle duel arena settings..
        if (player.getDueling().inDuel()) {
            for (int i = 11; i < player.getDueling().getRules().length; i++) {
                if (player.getDueling().getRules()[i]) {
                    DuelRule duelRule = DuelRule.forId(i);
                    if (duelRule == null)
                        return false;
                    if (equipmentSlot == duelRule.getEquipmentSlot() || duelRule == DuelRule.NO_SHIELD && equip.isTwoHanded()) {
                        DialogueManager.sendStatement(player, "The rules that were set do not allow this item to be equipped.");
                        return false;
                    }
                }
            }
            if (equipmentSlot == EquipSlot.WEAPON || equip.isTwoHanded()) {
                boolean isDDSOrWhip = equip.name().toLowerCase().contains("dragon dagger") || equip.name().toLowerCase().contains("abyssal whip");
                boolean isWhip = equip.name().toLowerCase().contains("abyssal whip");
                var whipAndDDS = player.<Boolean>getAttribOr(AttributeKey.WHIP_AND_DDS, false);
                var whipOnly = player.<Boolean>getAttribOr(AttributeKey.WHIP_ONLY, false);
                if (player.getDueling().getRules()[DuelRule.LOCK_WEAPON.ordinal()] && !(whipAndDDS && isDDSOrWhip) && !(whipOnly && isWhip)) {
                    DialogueManager.sendStatement(player, "Weapons have been locked in this duel!");
                    return false;
                }
            }
        }

        // Check if we're fit enough to equip this item

        boolean[] needsreq = new boolean[1];
        Map<Integer, Integer> reqs = World.getWorld().equipmentInfo().requirementsFor(equip.getId());
        if (reqs != null && reqs.size() > 0) {
            reqs.forEach((key, value) -> {
                if (!needsreq[0] && player.getSkills().xpLevel(key) < value) {
                    player.message("You need %s %s level of %d to equip this.", Skills.SKILL_INDEFINITES[key], Skills.SKILL_NAMES[key], value);
                    needsreq[0] = true;
                }
            });
        }

        // We don't meet a requirement.
        if (needsreq[0]) {
            player.message("<col=FF0000>You don't have the level requirements to wear: %s.", World.getWorld().definitions().get(ItemDefinition.class, equip.getId()).name);
            return false;
        }

        //For dark lord accounts check if we unlocked this item
        if (player.getGameMode().isDarklord()) {
            if (player.getCollectionLog().unlocked(equip.getId()) == 1) {
                player.message(Color.RED.wrap("You have not unlocked this item yet."));
                return false;
            }
        }

        if (equip.getId() == HARDCORE_IRONMAN_HELM || equip.getId() == HARDCORE_IRONMAN_PLATEBODY || equip.getId() == HARDCORE_IRONMAN_PLATELEGS) {
            if (player.getIronManStatus() != IronMode.HARDCORE) {
                player.message("<col=FF0000>You cannot wear this equipment as you are no longer a hardcore ironman.");
                return false;
            }
        }

        if (equip.getId() == IRONMAN_HELM || equip.getId() == IRONMAN_PLATEBODY || equip.getId() == IRONMAN_PLATELEGS) {
            if (player.getIronManStatus() != IronMode.REGULAR) {
                player.message("<col=FF0000>You cannot wear this equipment as you are no longer a ironman.");
                return false;
            }
        }

        if (equip.getId() == ACHIEVEMENT_DIARY_CAPE_T || equip.getId() == ACHIEVEMENT_DIARY_CAPE || equip.getId() == ACHIEVEMENT_DIARY_HOOD) {
            boolean completedAllAchievements = player.completedAllAchievements();
            if (!completedAllAchievements) {
                player.message("<col=FF0000>You have not completed all the achievements yet.");
                return false;
            }
        }

        if (MAX_CAPES.contains(equip.getId()) || MAX_HOODES.contains(equip.getId())) {
            if (!MaxCape.hasTotalLevel(player)) {
                player.message("You need a Total Level of " + Mac.TOTAL_LEVEL_FOR_MAXED + " to wear this cape or hood.");
                return false;
            }
        }

        // Check if we are already wearing an identical item
        var currentItem = player.getEquipment().get(World.getWorld().equipmentInfo().slotFor(equip.getId()));
        if (currentItem != null && currentItem.getId() == equip.getId() && currentItem.getAmount() == equip.getAmount() && !equip.definition(World.getWorld()).stackable())//stackable items stack bolts ammo darts etc
            return false;

        if (equip.getId() == ANCIENT_WYVERN_SHIELD) {
            player.animate(new Animation(3996));
            player.performGraphic(new Graphic(1395, GraphicHeight.HIGH));
        }

        if (equip.getId() == ItemIdentifiers.AMULET_OF_AVARICE) {
            Skulling.assignSkullState(player, SkullType.WHITE_SKULL);
        }

        if (equipmentSlot == EquipSlot.RING && id == ItemIdentifiers.RING_OF_RECOIL) {
            int charges = player.getAttribOr(AttributeKey.RING_OF_RECOIL_CHARGES, 40);
            if (charges <= 0) {
                player.putAttrib(AttributeKey.RING_OF_RECOIL_CHARGES, 40);
            }
        }

        if (player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS_FULL)) {
            player.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SEAS.getSpell());
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP)) {
            player.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell());
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
            player.getCombat().setPoweredStaffSpell(CombatSpells.SANGUINESTI_STAFF.getSpell());
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, TUMEKENS_SHADOW)) {
            player.getCombat().setPoweredStaffSpell(CombatSpells.TUMEKENS_SHADOW.getSpell());
        } else if (player.getEquipment().hasAt(EquipSlot.WEAPON, ACCURSED_SCEPTRE_A)) {
            player.getCombat().setPoweredStaffSpell(CombatSpells.ACCURSED_SCEPTRE.getSpell());
        } else {
            player.getCombat().setPoweredStaffSpell(null);
        }

        Item current;

        current = get(equipmentSlot);

        Item secondaryItemToUnequip = null;

        if (current != null && equip.stackable() && isItem(equipmentSlot, equip.getId())) {
            int amount = equip.getAmount();
            if (Integer.MAX_VALUE - current.getAmount() < amount) {
                amount = Integer.MAX_VALUE - current.getAmount();
            }
            set(equipmentSlot, current.createAndIncrement(amount), true);
            get(equipmentSlot);
            inventory.remove(new Item(equip.getId(), amount), inventoryIndex, true);
            return true;
        }

        if (hasWeapon() && equipmentSlot == EquipSlot.SHIELD) {
            if (equip.isTwoHanded() || getWeapon().isTwoHanded()) {
                secondaryItemToUnequip = getWeapon();
            }
        }

        if (hasShield() && equipmentSlot == EquipSlot.WEAPON) {
            if (equip.isTwoHanded() || getShield().isTwoHanded()) {
                secondaryItemToUnequip = getShield();
            }
        }

        boolean oneForOneSwap =
            (equipmentSlot == EquipSlot.SHIELD && (!hasWeapon() || !hasShield() || getWeapon().isTwoHanded()))
                || (equipmentSlot == EquipSlot.WEAPON && (!hasShield() || !hasWeapon() || getShield().isTwoHanded()));

        if (secondaryItemToUnequip != null && !inventory.hasCapacity(secondaryItemToUnequip) && !oneForOneSwap) {
            player.message("You do not have enough space in your inventory.");
            return false;
        }

        if (current != null) { // TODO move maxcape code to here
            player.getEquipment().remove(current, equipmentSlot, true); // delete it
        }
        player.inventory().remove(equip, inventoryIndex, true);
        if (current != null)
            player.inventory().add(current, inventoryIndex, true); // add to inv
        player.getEquipment().set(equipmentSlot, equip, true); // add new to equip. use SET instead of ADD to use special equip index.
        appearanceForIndex(equipmentSlot);
        player.setSpecialActivated(false);
        CombatSpecial.updateBar(player);


        //Update weapon interface
        WeaponInterfaces.updateWeaponInterface(player);

        //OSRS resets target and interaction
        player.getCombat().setTarget(null);
        player.setEntityInteraction(null);

        if (secondaryItemToUnequip != null) {
            // On 07 there are two max capes, one has right-click equipment options and the other does not!
            int new_id = secondaryItemToUnequip.getId() == 13342 ? 13280 : secondaryItemToUnequip.getId();
            Item newItem = new Item(new_id, secondaryItemToUnequip.getAmount());
            int slot = info.slotFor(newItem.getId());
            set(slot, null, true);
            appearanceForIndex(slot);
            inventory.add(newItem, inventoryIndex, true);
        }

        return true;
    }

    /**
     * Unequips an {@link Item} from the underlying player's {@code Equipment}.
     *
     * @param equipmentIndex The {@code Equipment} index to unequip the {@code
     *                       Item} from.
     * @return {@code true} if the item was unequipped, {@code false} otherwise.
     */
    public boolean unequip(int equipmentIndex) {
        return unequip(equipmentIndex, -1, player.inventory());
    }

    /**
     * Unequips an {@link Item} from the underlying player's {@code Equipment}.
     *
     * @param equipmentIndex The {@code Equipment} index to unequip the {@code
     *                       Item} from.
     * @param preferredIndex The preferred inventory slot.
     * @param container      The container to which we are putting the items on.
     * @return {@code true} if the item was unequipped, {@code false} otherwise.
     */
    private boolean unequip(int equipmentIndex, int preferredIndex, ItemContainer container) {
        if (player.locked())
            return false;

        if (equipmentIndex == -1)
            return false;
        Item unequip = get(equipmentIndex);
        if (unequip == null)
            return false;

        if (equipmentIndex == EquipSlot.WEAPON || unequip.isTwoHanded()) {
            if (player.getDueling().getRules()[DuelRule.LOCK_WEAPON.ordinal()]) {
                DialogueManager.sendStatement(player, "Weapons have been locked in this duel!");
                return false;
            }
        }

        // This newid can be expanded in the future.
        // Currently converts the maxcape with r-click opts to the corrent inventory one with r-click opts.

        int newid = unequip.getId() == 13342 ? 13280 : unequip.getId();
        Item toInv = new Item(newid, unequip.getAmount());
        if (!container.add(toInv, preferredIndex, true)) {
            return false;
        }

        player.getEquipment().remove(new Item(unequip.getId(), unequip.getAmount()), true);

        appearanceForIndex(equipmentIndex);

        if (unequip.getId() == ItemIdentifiers.AMULET_OF_AVARICE) {
            // Skull..
            Skulling.assignSkullState(player, SkullType.WHITE_SKULL);
        }

        if (!player.getInterfaceManager().isClear() && !player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.EQUIPMENT_SCREEN_INTERFACE_ID)) {
            player.getInterfaceManager().close(false);
        }

        player.getCombat().setCastSpell(null);

        if (player.getCombat().getPoweredStaffSpell() != null) {
            player.getCombat().setPoweredStaffSpell(null);
        }

        //Always reset ranged weapon when unequipping weapon
        if (player.getCombat().getRangedWeapon() != null) {
            player.getCombat().setRangedWeapon(null);
        }

        WeaponInterfaces.updateWeaponInterface(player);

        CombatSpecial.updateBar(player);
        player.setSpecialActivated(false);

        return true;
    }

    /**
     * Flags the {@code APPEARANCE} update block, only if the equipment piece on
     * {@code equipmentIndex} requires an appearance update.
     */
    private void appearanceForIndex(int equipmentIndex) {
        if (!NO_APPEARANCE.contains(equipmentIndex)) {
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (dirty) {
            player.setQueuedAppearanceUpdate(true);
        }
    }

    public boolean hasHead() {
        return get(EquipSlot.HEAD) != null;
    }

    public boolean hasAmulet() {
        return get(EquipSlot.AMULET) != null;
    }

    public boolean hasAmmo() {
        return get(EquipSlot.AMMO) != null;
    }

    public boolean hasChest() {
        return get(EquipSlot.BODY) != null;
    }

    public boolean hasLegs() {
        return get(EquipSlot.LEGS) != null;
    }

    public boolean hasHands() {
        return get(EquipSlot.HANDS) != null;
    }

    public boolean hasFeet() {
        return get(EquipSlot.FEET) != null;
    }

    public boolean hasRing() {
        return get(EquipSlot.RING) != null;
    }

    public boolean hasWeapon() {
        return get(EquipSlot.WEAPON) != null;
    }

    public boolean hasCape() {
        return get(EquipSlot.CAPE) != null;
    }

    public Item getWeapon() {
        return get(EquipSlot.WEAPON);
    }

    public Item getAmmo() {
        return get(EquipSlot.AMMO);
    }

    public Item getCape() {
        return get(EquipSlot.CAPE);
    }

    public Item getAmuletSlot() {
        return get(EquipSlot.AMULET);
    }

    public boolean hasShield() {
        return get(EquipSlot.SHIELD) != null;
    }

    public Item getShield() {
        return get(EquipSlot.SHIELD);
    }

    public Item[] getEquipment() {
        Item[] equipment = new Item[15];
        equipment[1] = player.getEquipment().get(EquipSlot.HEAD);
        equipment[3] = player.getEquipment().get(EquipSlot.CAPE);
        equipment[4] = player.getEquipment().get(EquipSlot.AMULET);
        equipment[5] = player.getEquipment().get(EquipSlot.AMMO);
        equipment[6] = player.getEquipment().get(EquipSlot.WEAPON);
        equipment[7] = player.getEquipment().get(EquipSlot.BODY);
        equipment[8] = player.getEquipment().get(EquipSlot.SHIELD);
        equipment[10] = player.getEquipment().get(EquipSlot.LEGS);
        equipment[12] = player.getEquipment().get(EquipSlot.HANDS);
        equipment[13] = player.getEquipment().get(EquipSlot.FEET);
        equipment[14] = player.getEquipment().get(EquipSlot.RING);
        return equipment;
    }

    public boolean hasNoEquipment() {
        for (Item i : getEquipment()) {
            if (i != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Forces a refresh of {@code Equipment} items to the {@code
     * EQUIPMENT_DISPLAY_ID} widget.
     */
    public void sync() {
        //Also refresh rendering
        player.looks().resetRender();
        refresh(player, InterfaceConstants.EQUIPMENT_DISPLAY_ID);
        player.getCombat().setRangedWeapon(null);
    }

    /**
     * Forces a refresh of {@code Equipment} items to the {@code
     * EQUIPMENT_DISPLAY_ID} widget.
     */
    @Override
    public void refresh(Player player, int widget) {
        player.getPacketSender().sendItemOnInterface(widget, toArray());
    }

    @Override
    public void clear() {
        super.clear();
    }

    private boolean isItem(int slot, int itemId) {
        Item item = get(slot);
        return item != null && item.getId() == itemId;
    }

    public static boolean venomHelm(Entity entity) {
        Player player = (Player) entity;
        Item helm = player.getEquipment().get(EquipSlot.HEAD);
        if (helm == null) return false;
        return player.getEquipment().containsAny(SERPENTINE_HELM, MAGMA_HELM, TANZANITE_HELM);
    }

    @Override
    public String toString() {
        return "{Equipment}=" + Arrays.toString(this.toNonNullArray());
    }

    /**
     * An {@link ItemContainerAdapter} implementation that listens for changes to
     * equipment.
     */
    private final class EquipmentListener extends ItemContainerAdapter {

        /**
         * Creates a new {@link EquipmentListener}.
         */
        EquipmentListener() {
            super(player);
        }

        @Override
        public int getWidgetId() {
            return InterfaceConstants.EQUIPMENT_DISPLAY_ID;
        }

        @Override
        public String getCapacityExceededMsg() {
            throw new IllegalStateException(EXCEPTION_MESSAGE);
        }

        @Override
        public void itemUpdated(ItemContainer container, Optional<Item> oldItem, Optional<Item> newItem, int index, boolean refresh) {
            if (oldItem.equals(newItem))
                return;

            if (refresh) {
                sendItemsToWidget(container);
            }
        }

        @Override
        public void bulkItemsUpdated(ItemContainer container) {
            sendItemsToWidget(container);
        }
    }

}
