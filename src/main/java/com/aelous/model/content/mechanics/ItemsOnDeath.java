package com.aelous.model.content.mechanics;

import com.aelous.GameServer;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.areas.wilderness.content.revenant_caves.AncientArtifacts;
import com.aelous.model.content.items_kept_on_death.ItemsKeptOnDeath;
import com.aelous.model.content.mechanics.break_items.BrokenItem;
import com.aelous.model.content.minigames.MinigameManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.content.bountyhunter.emblem.BountyHunterEmblem;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.inter.lootkeys.LootKey;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.test.unit.IKODTest;
import com.aelous.utility.test.unit.PlayerDeathConvertResult;
import com.aelous.utility.test.unit.PlayerDeathDropResult;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | June, 27, 2021, 12:56
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class ItemsOnDeath {

    private static final Logger playerDeathLogs = LogManager.getLogger("PlayerDeathsLogs");
    private static final Level PLAYER_DEATHS;

    static {
        PLAYER_DEATHS = Level.getLevel("PLAYER_DEATHS");
    }

    /**
     * The items the Player lost.
     */
    private static final List<Item> lostItems = new ArrayList<>();

    /**
     * If our account has the ability for the custom PetDefinitions Shout mechanic - where when you kill someone
     * your pet will shout something.
     */
    public static boolean hasShoutAbility(Player player) {
        // Are we a user with the mechanic enabled
        return player.getAttribOr(AttributeKey.PET_SHOUT_ABILITY, false);
    }

    /**
     * Calculates and drops all of the items from {@code player} for {@code killer}.
     *
     * @return
     */
    public static PlayerDeathDropResult droplootToKiller(Player player, Entity killer) {
        var donator_zone = player.tile().region() == 13462;
        var vorkath_area = player.tile().region() == 9023;
        var hydra_area = player.tile().region() == 5536;
        var zulrah_area = player.tile().region() == 9007 || player.tile().region() == 9008;
        var safe_accounts = player.getUsername().equalsIgnoreCase("Box test");
        var duel_arena = player.getDueling().inDuel() || player.getDueling().endingDuel();
        var pest_control = player.tile().region() == 10536;
        var raids_area = player.getRaids() != null && player.getRaids().raiding(player);
        var minigame_safe_death = player.getMinigame() != null && player.getMinigame().getType().equals(MinigameManager.ItemType.SAFE);
        var hunleff_area = player.tile().region() == 6810;

        // If we're in FFA clan wars, don't drop our items.
        // Have these safe area checks before we do some expensive code ... looking for who killed us.
        if (donator_zone || vorkath_area || zulrah_area || hydra_area || safe_accounts || duel_arena || pest_control || raids_area || minigame_safe_death || hunleff_area) {
            playerDeathLogs.log(PLAYER_DEATHS, "Player: " + player.getUsername() + " died in a safe area " + (killer != null && killer.isPlayer() ? " to " + killer.toString() : ""));
            Utils.sendDiscordInfoLog("Player: " + player.getUsername() + " died in a safe area " + (killer != null && killer.isPlayer() ? " to " + killer.toString() : ""), "playerdeaths");
            Utils.sendDiscordInfoLog("Safe deaths activated for: " + player.getUsername() + "" + (killer != null && killer.isPlayer() ? " to " + killer.toString() : "" + " donator_zone: " + donator_zone + " vorkath_area: " + vorkath_area + " hydra_area: " + hydra_area + " zulrah_area: " + zulrah_area + " in safe_accounts: " + safe_accounts + " duel_arena: " + duel_arena + " pest_control: " + pest_control + " raids_area: " + raids_area + " minigame_safe_death: " + minigame_safe_death + " hunleff_area: " + hunleff_area), "playerdeaths");
            return null;
        }

        // If it's not a safe death, turn a Hardcore Ironman into a regular.
        if (player.getIronManStatus() == IronMode.HARDCORE) {
            stripHardcoreRank(player);
        }

        // Past this point.. we're in a dangerous zone! Drop our items....

        Player theKiller = killer == null || killer.isNpc() ? player : killer.getAsPlayer();

        final Tile tile = player.tile();

        // Game Lists
        LinkedList<Item> toDrop = new LinkedList<>();
        List<Item> toDropPre = new LinkedList<>();

        // Unit Testing Lists
        List<Item> outputDrop = new ArrayList<>(toDrop.size());
        List<Item> outputKept = new ArrayList<>(1);
        List<Item> outputDeleted = new ArrayList<>(0);
        List<PlayerDeathConvertResult> outputConverted = new ArrayList<>(0);

        player.getEquipment().forEach(toDropPre::add);
        player.inventory().forEach(item -> {
            if (!item.matchesId(LOOTING_BAG) && !item.matchesId(LOOTING_BAG_22586) && !item.matchesId(RUNE_POUCH)) { // always lost
                toDropPre.add(item);
            } else {
                outputDeleted.add(item); // looting bag goes into deleted
            }
        });
        player.getEquipment().clear(); // everything gets cleared no matter what
        player.inventory().clear();

        toDrop.addAll(toDropPre);


        //System.out.println("Dropping: " + Arrays.toString(toDrop.toArray()));

        // remove always kept before calculating kept-3 by value
        List<Item> alwaysKept = toDrop.stream().filter(ItemsKeptOnDeath::alwaysKept).toList();
        IKODTest.debug("death alwaysKept list : " + Arrays.toString(alwaysKept.stream().map(Item::toShortValueString).toArray()));
        List<Item> keep = new LinkedList<>(alwaysKept);
        toDrop.removeIf(ItemsKeptOnDeath::alwaysKept);

        // custom always lost
        final List<Item> alwaysLostSpecial = toDrop.stream().filter(i -> i.getId() == RUNE_POUCH || i.getId() == LOOTING_BAG || i.getId() == LOOTING_BAG_22586).toList();
        for (Item item : alwaysLostSpecial) {
            toDrop.remove(item); // not included in kept-3 if unskulled
            Item currency;
            if (GameServer.properties().pvpMode) {
                currency = new Item(BLOOD_MONEY, item.getId() == LOOTING_BAG || item.getId() == LOOTING_BAG_22586 ? 1250 : 2500);
            } else {
                currency = new Item(COINS_995, item.getId() == LOOTING_BAG || item.getId() == LOOTING_BAG_22586 ? 1_250_000 : 2_500_000);
            }

            outputDrop.add(currency); // this list isn't whats dropped its for logging
            GroundItemHandler.createGroundItem(new GroundItem(currency, player.tile(), theKiller)); // manually drop it here
        }

        // Sort remaining lost items by value.
        toDrop.sort((o1, o2) -> {
            o1 = o1.unnote();
            o2 = o2.unnote();

            ItemDefinition def = o1.definition(World.getWorld());
            ItemDefinition def2 = o2.definition(World.getWorld());

            int v1 = 0;
            int v2 = 0;

            if (def != null) {
                v1 = o1.getValue();
                if (v1 <= 0 && !GameServer.properties().pvpMode) {
                    v1 = o1.getBloodMoneyPrice().value();
                }
            }
            if (def2 != null) {
                v2 = o2.getValue();
                if (v2 <= 0 && !GameServer.properties().pvpMode) {
                    v2 = o2.getBloodMoneyPrice().value();
                }
            }

            return Integer.compare(v2, v1);
        });
        int keptItems = (Skulling.skulled(player) ? 0 : 3);

        // On Ultimate Iron Man, you drop everything!
        if (player.getIronManStatus() == IronMode.ULTIMATE) {
            keptItems = 0;
        }

        boolean protection_prayer = Prayers.usingPrayer(player, Prayers.PROTECT_ITEM);
        if (protection_prayer) {
            keptItems++;
        }

        //#Update as of 16/02/2021 when smited you're actually smited the pet effect will not work!

        if (player.getSkullType().equals(SkullType.RED_SKULL)) {
            keptItems = 0;
        }
        IKODTest.debug("keeping " + keptItems + " items");

        while (keptItems-- > 0 && !toDrop.isEmpty()) {
            Item head = toDrop.peek();
            if (head == null) {
                keptItems++;
                toDrop.poll();
                continue;
            }
            keep.add(new Item(head.getId(), 1));


            if (head.getAmount() == 1) { // Amount 1? Remove the item entirely.
                Item delete = toDrop.poll();
                IKODTest.debug("kept " + delete.toShortString());
            } else { // Amount more? Subtract one amount.
                int index = toDrop.indexOf(head);
                toDrop.set(index, new Item(head, head.getAmount() - 1));
                IKODTest.debug("kept " + toDrop.get(index).toShortString());
            }
        }
        for (Item item : keep) {
            if (GameServer.properties().pvpMode) {//Only in PvP worlds
                // Handle item breaking..
                BrokenItem brokenItem = BrokenItem.get(item.getId());
                if (brokenItem != null) {
                    player.getPacketSender().sendMessage("Your " + item.unnote().name() + " has been broken. You can fix it by talking to").sendMessage("Perdu who is located in Edgeville at the furnace.");
                    item.setId(brokenItem.brokenItem);

                    //Drop bm for the killer
                    GroundItem groundItem = new GroundItem(new Item(BLOOD_MONEY, (int) brokenItem.bmDrop), player.tile(), theKiller);
                    GroundItemHandler.createGroundItem(groundItem);
                }
            }
            player.inventory().add(item, true);
        }

        // Looting bag items are NOT in top-3 kept from prot item/unskulled. Always lost.
        if (outputDeleted.stream().anyMatch(i -> i.getId() == LOOTING_BAG || i.getId() == LOOTING_BAG_22586)) {
            Item[] lootingBag = player.getLootingBag().toNonNullArray(); // bypass check if carrying bag since inv is cleared above
            toDrop.addAll(Arrays.asList(lootingBag));
            playerDeathLogs.log(PLAYER_DEATHS, player.getUsername() + " (Skulled: " + Skulling.skulled(player) + ") looting bag lost items: " + Arrays.toString(Arrays.asList(lootingBag).toArray()) + (killer != null && killer.isPlayer() ? " to " + killer.getMobName() : ""));
            Utils.sendDiscordInfoLog(player.getUsername() + " (Skulled: " + Skulling.skulled(player) + ") looting bag lost items: " + Arrays.toString(Arrays.asList(lootingBag).toArray()) + (killer != null && killer.isPlayer() ? " to " + killer.getMobName() : ""), "playerdeaths");

            player.getLootingBag().clear();
            IKODTest.debug("looting bag had now: " + Arrays.toString(Arrays.asList(lootingBag).toArray()));
        }

        // Rune pouch items are NOT in top-3 kept from prot item/unskulled. Always lost.
        Item[] runePouch = player.getRunePouch().toNonNullArray(); // bypass check if carrying pouch since inv is cleared above
        toDrop.addAll(Arrays.asList(runePouch));
        player.getRunePouch().clear();
        IKODTest.debug("rune pouch had now: " + Arrays.toString(Arrays.asList(runePouch).toArray()));

        lostItems.clear();
        IKODTest.debug("Dropping now: " + Arrays.toString(toDrop.stream().map(Item::toShortString).toArray()));

        outputKept.addAll(keep);
        IKODTest.debug("Kept-3: " + Arrays.toString(keep.stream().map(Item::toShortString).toArray()));

        Entity lastAttacker = player.getAttribOr(AttributeKey.LAST_DAMAGER, null);
        final boolean npcFlag = lastAttacker != null && lastAttacker.isNpc() && lastAttacker.getAsNpc().getBotHandler() != null;

        LinkedList<Item> toDropConverted = new LinkedList<>();

        toDrop.forEach(item -> {

            if (item.getId() == AncientArtifacts.ANCIENT_EFFIGY.getItemId()
                || item.getId() == AncientArtifacts.ANCIENT_EMBLEM.getItemId()
                || item.getId() == AncientArtifacts.ANCIENT_MEDALLION.getItemId()
                || item.getId() == AncientArtifacts.ANCIENT_RELIC.getItemId()
                || item.getId() == AncientArtifacts.ANCIENT_STATUETTE.getItemId()
                || item.getId() == AncientArtifacts.ANCIENT_TOTEM.getItemId()) {
                GroundItemHandler.createGroundItem(new GroundItem(new Item(item.getId()), player.tile(), theKiller));
                outputDrop.add(new Item(item.getId()));
                // dont add to toDropConverted, we're manually dropping it
                return;
            }

            //Drop emblems but downgrade them a tier.
            if (item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_1.getItemId()
                || item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_2.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_3.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_4.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_5.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_6.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_7.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_8.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_9.getItemId() ||
                item.getId() == BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_10.getItemId()) {

                //Tier 1 shouldnt be dropped cause it cant be downgraded
                if (item.matchesId(BountyHunterEmblem.ANTIQUE_EMBLEM_TIER_1.getItemId())) {
                    return;
                }

                final int lowerEmblem = item.getId() - 2;

                ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, lowerEmblem);
                GroundItemHandler.createGroundItem(new GroundItem(new Item(lowerEmblem), player.tile(), theKiller));
                theKiller.message("<col=ca0d0d>" + player.getUsername() + " dropped a " + def.name + "!");
                outputDrop.add(new Item(lowerEmblem));
                // dont add to toDropConverted, we're manually dropping it
                return;
            }

            // IKODTest.debug("dc2: "+item.toShortString());

            // if we've got to here, add the original or changed SINGLE item to the newer list
            toDropConverted.add(item);
        });
        toDrop = toDropConverted;

        toDrop.forEach(item -> {

            if (ItemsKeptOnDeath.alwaysKept(item)) {
                player.inventory().add(item);
                outputKept.add(item);
                return;
            }

            lostItems.add(item);

            boolean diedToSelf = theKiller == player;

            //This IKOD is such dogshit lol, probs need to redo loot keys part
            boolean lootKeysEnabled = LootKey.lootKeysEnabled(theKiller);
            boolean killerIsDead = theKiller.deadRecently();
            boolean discardLootBecauseItsInKey = lootKeysEnabled && !killerIsDead;
            if (discardLootBecauseItsInKey) {
                return;
            }

            GroundItem g = new GroundItem(item, player.tile(), theKiller);
            GroundItemHandler.createGroundItem(g);
            g.pkedFrom(player.getUsername()); // Mark item as from PvP to avoid ironmen picking it up.

            outputDrop.add(item);
        });
        System.out.println("output : "+toDrop);
        player.putAttrib(AttributeKey.LOST_ITEMS_ON_DEATH, toDrop);
        var list = player.<LinkedList<Item>>getAttribOr(AttributeKey.LOST_ITEMS_ON_DEATH,null);
        System.out.println("attrib : "+Arrays.toString(list.toArray()));
        GroundItemHandler.createGroundItem(new GroundItem(new Item(BONES), player.tile(), theKiller));
        outputDrop.add(new Item(BONES));
        playerDeathLogs.log(PLAYER_DEATHS, player.getUsername() + " (Skulled: " + Skulling.skulled(player) + ") lost items: " + Arrays.toString(lostItems.stream().map(Item::toShortString).toArray()) + (killer != null && killer.isPlayer() ? " to " + killer.getMobName() : ""));
        Utils.sendDiscordInfoLog(player.getUsername() + " (Skulled: " + Skulling.skulled(player) + ") lost items: " + Arrays.toString(lostItems.stream().map(Item::toShortString).toArray()) + (killer != null && killer.isPlayer() ? " to " + killer.getMobName() : ""), "playerdeaths");
        //Reset last attacked by, since we already handled it above, and the player is already dead.
        player.clearAttrib(AttributeKey.LAST_DAMAGER);
        return new PlayerDeathDropResult(theKiller, outputDrop, outputKept, outputDeleted, outputConverted);
    }

    private static void stripHardcoreRank(Player player) {

        World.getWorld().sendWorldMessage("<img=504>" + Color.RED.wrap("[Hardcore fallen]:") + " " + Color.BLUE.wrap(player.getUsername()) + " has fallen as a Hardcore Iron Man!");
        player.message("You have fallen as a Hardcore Iron Man', your Hardcore status has been revoked.");
    }

    private static void stripDarkLordRank(Player player) {
        var lives = player.<Integer>getAttribOr(AttributeKey.DARK_LORD_LIVES, 3) - 1;
        player.putAttrib(AttributeKey.DARK_LORD_LIVES, lives);
        if (lives == 0) {

            player.getGameMode(GameMode.TRAINED_ACCOUNT);
            player.message("You have fallen as a Dark Lord', your status has been revoked.");
            World.getWorld().sendWorldMessage("<img=2013>" + Color.PURPLE.wrap(player.getUsername()) + Color.RED.wrap("has fallen as a Dark Lord!"));
        }
    }
}
