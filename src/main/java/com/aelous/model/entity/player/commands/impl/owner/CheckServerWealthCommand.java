package com.aelous.model.entity.player.commands.impl.owner;

/**
 * @author Patrick van Elderen | June, 03, 2021, 13:17
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */

import com.aelous.GameEngine;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.items.tradingpost.TradingPostListing;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.aelous.model.entity.attributes.AttributeKey.MAC_ADDRESS;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Jak |shadowrs tardisfan121@gmail.com heavily modified from original
 */
public class CheckServerWealthCommand implements Command {

    private static final Logger logger = LogManager.getLogger(CheckServerWealthCommand.class);
    int itemsCount;
    long bloodMoneyItemWealth;
    long bloodMoney;
    long votePoints;
    long untradeableBloodMoneyWealth;
    HashSet<String> checkedPlayers = new HashSet<>();
    Map<String, Long> playersValues = new HashMap<>();
    int topPlayers = 0;
    String topWealthString = "";

    @Override
    public void execute(Player dev, String command, String[] parts) {
        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        //Make sure to clear the values because we have sync tasks.
        itemsCount = 0;
        bloodMoneyItemWealth = 0;
        bloodMoney = 0;
        votePoints = 0;
        untradeableBloodMoneyWealth = 0;
        topPlayers = 0;
        topWealthString = "";
        playersValues.clear();
        checkedPlayers.clear();
        boolean checkOffline = false;
        if (parts.length > 1) {
            if (parts[1].equalsIgnoreCase("true") || parts[1].equalsIgnoreCase("yes")) {
                checkOffline = true;
            }
        }

        // on the game thread: do a quick username scan to filter on/offline
        ArrayList<Player> onlineToScan = new ArrayList<>();
        for (Player opp : World.getWorld().getPlayers()) {
            if (opp == null) continue;
            /*if (opp.getUsername().equalsIgnoreCase("box test")) continue;
            if(opp.getPlayerRights().isAdminOrGreater(opp)) continue;
            if(opp.getPlayerRights().isDeveloperOrGreater(opp)) continue;*/
            checkedPlayers.add(opp.getUsername());
            onlineToScan.add(opp);
        }
        //Since we aren't mutating any state of the game, we can just execute everything on a different thread.
        GameEngine.getInstance().submitLowPriority(() -> {
            for (Player opp : onlineToScan) { // this is the online players section
                long playerBloodMoneyWealth = 0;
                if (opp == null) continue;
                //if (opp.getUsername().toLowerCase().equals("box test")) continue;
                //if(opp.getPlayerRights().isAdminOrGreater(opp)) continue;
                //if(opp.getPlayerRights().isDeveloperOrGreater(opp)) continue;
                checkedPlayers.add(opp.getUsername());

                votePoints += opp.<Integer>getAttribOr(AttributeKey.VOTE_POINS, 0);

                ArrayList<Item> allItems = new ArrayList<>();
                allItems.addAll(Arrays.asList(opp.getEquipment().toNonNullArray()));
                allItems.addAll(Arrays.asList(opp.inventory().toNonNullArray()));
                allItems.addAll(Arrays.asList(opp.getBank().toNonNullArray()));
                allItems.addAll(Arrays.asList(opp.getLootingBag().toNonNullArray()));
                allItems.addAll(Arrays.asList(opp.getRunePouch().toNonNullArray()));
                allItems.addAll(opp.<ArrayList<Item>>getAttribOr(AttributeKey.NIFFLER_ITEMS_STORED, new ArrayList<Item>()));

                final var listings = TradingPost.getListings(opp.getUsername().toLowerCase());
                List<TradingPostListing> list = listings.getListedItems();
                for(TradingPostListing tradingPostListing : list) {
                    allItems.add(tradingPostListing.getSaleItem());
                }

                for (Item item : allItems) {
                    if (item == null)
                        continue;
                    itemsCount++;
                    if (item.getId() == BLOOD_MONEY) {
                        bloodMoneyItemWealth += (long) item.getAmount();
                        playerBloodMoneyWealth += (long) item.getAmount();
                    } else {
                        bloodMoneyItemWealth += (long) item.getBloodMoneyPrice().value() * (long) item.getAmount();
                        playerBloodMoneyWealth += (long) item.getBloodMoneyPrice().value() * (long) item.getAmount();
                    }
                }
                playersValues.put(opp.getUsername() + " " + opp.getAttribOr(MAC_ADDRESS, "invalid") + " ", playerBloodMoneyWealth);
            }

            // online scan complete. feedback.
            //This used to be nested sync tasks.
            GameEngine.getInstance().addSyncTask(() -> {
                Map<String, Long> sortedValues = Utils.sortByComparator(playersValues, false);
                sortedValues.forEach((key, value) -> {
                    if (topPlayers++ < 10) {
                        topWealthString += key + "= " + Utils.formatNumber(value) + " " + "bm" + "; ";
                    }
                });
                dev.message("The players with the most wealth are: " + topWealthString);
                dev.message("There are " + World.getWorld().getPlayers().size() + " online players and " + (checkedPlayers.size() - World.getWorld().getPlayers().size()) + " offline players.");
                dev.message("The total bm wealth for all " + checkedPlayers.size() + " players is: " + Utils.formatNumber(bloodMoneyItemWealth));
                dev.message("The total item count for all " + checkedPlayers.size() + " players is: " + Utils.formatNumber(itemsCount));
            });
        });
        // kick off offline scanning
        if (checkOffline) {
            submitOfflineScan(dev, new HashSet<String>(checkedPlayers));
        }
    }

    public static class AtomicStorage {
        public AtomicInteger topPlayers = new AtomicInteger(0);
        public AtomicInteger itemsCount = new AtomicInteger(0);
        public AtomicLong sumBloodMoneyItemWealth = new AtomicLong(0L);
        public AtomicLong sumBloodMoneyWealth = new AtomicLong(0L);
        public AtomicLong sumRefersByName = new AtomicLong(0L);
        public AtomicLong sumVotePoints = new AtomicLong(0L);
        public AtomicLong sumEly = new AtomicLong(0L);
        public AtomicLong sumAgs = new AtomicLong(0L);
        public AtomicLong sumAgsOR = new AtomicLong(0L);
        public AtomicLong sumAncientVLS = new AtomicLong(0L);
        public AtomicLong sumAncientSWH = new AtomicLong(0L);
        public AtomicLong sumFacegaurd = new AtomicLong(0L);
        public AtomicLong sumAncientFacegaurd = new AtomicLong(0L);
        public AtomicLong sumDHL = new AtomicLong(0L);
        public AtomicLong sum5Bond = new AtomicLong(0L);
        public AtomicLong sum10Bond = new AtomicLong(0L);
        public AtomicLong sum20Bond = new AtomicLong(0L);
        public AtomicLong sum40Bond = new AtomicLong(0L);
        public AtomicLong sum50Bond = new AtomicLong(0L);
        public AtomicLong sum100Bond = new AtomicLong(0L);
        public AtomicLong sumWyvernShield = new AtomicLong(0L);
        public AtomicLong sumWardShield = new AtomicLong(0L);
        public AtomicLong sumSangScythe = new AtomicLong(0L);
        public AtomicLong sumHolyScythe = new AtomicLong(0L);
        public AtomicLong sumScythe = new AtomicLong(0L);
        public AtomicLong sumTwistedbow = new AtomicLong(0L);
        public AtomicLong sumTwistedBowI = new AtomicLong(0L);
        public AtomicLong sumSangTwistedBow = new AtomicLong(0L);
        public AtomicLong sumElderMaul = new AtomicLong(0L);
        public AtomicLong sumInfernalCape = new AtomicLong(0L);
        public AtomicLong sumSerp = new AtomicLong(0L);
        public AtomicLong sumTSOTD = new AtomicLong(0L);
        public AtomicLong sumArcane = new AtomicLong(0L);
        public AtomicLong sumCrawBow = new AtomicLong(0L);
        public AtomicLong sumVig = new AtomicLong(0L);
        public AtomicLong sumCrawBowC = new AtomicLong(0L);
        public AtomicLong sumVigC = new AtomicLong(0L);
        public AtomicLong sumFero = new AtomicLong(0L);
        public AtomicLong sumAvernic = new AtomicLong(0L);
        public AtomicLong sumPrim = new AtomicLong(0L);
        public AtomicLong sumPeg = new AtomicLong(0L);
        public AtomicLong sumEternal = new AtomicLong(0L);
        public AtomicLong sumPrimOR = new AtomicLong(0L);
        public AtomicLong sumPegOR = new AtomicLong(0L);
        public AtomicLong sumEternalOR = new AtomicLong(0L);
        public AtomicLong sumCorruptedBoots = new AtomicLong(0L);
        public AtomicLong sumLuxCoins = new AtomicLong(0L);
        public AtomicLong sumAncestralHat = new AtomicLong(0L);
        public AtomicLong sumAncestralTop = new AtomicLong(0L);
        public AtomicLong sumAncestralBottom = new AtomicLong(0L);
        public AtomicLong sumAncestralHatT = new AtomicLong(0L);
        public AtomicLong sumAncestralTopT = new AtomicLong(0L);
        public AtomicLong sumAncestralBottomT = new AtomicLong(0L);
        public AtomicLong sumClaws = new AtomicLong(0L);
        public AtomicLong sumClawsOr = new AtomicLong(0L);
        public AtomicLong sumDarkElder = new AtomicLong(0L);
        public AtomicLong sumJawaPet = new AtomicLong(0L);
        public AtomicLong sumZriawkPet = new AtomicLong(0L);
        public AtomicLong sumFawkesPet = new AtomicLong(0L);
        public AtomicLong sumRecoloredFawkesPet = new AtomicLong(0L);
        public AtomicLong sumNifflerPet = new AtomicLong(0L);
        public AtomicLong sumWampaPet = new AtomicLong(0L);
        public AtomicLong sumBabyAragogPet = new AtomicLong(0L);
        public AtomicLong sumMiniNecromancerPet = new AtomicLong(0L);
        public AtomicLong sumCorruptedNechryarchPet = new AtomicLong(0L);
        public AtomicLong sumGrimReaperPet = new AtomicLong(0L);
        public AtomicLong sumKerberosPet = new AtomicLong(0L);
        public AtomicLong sumSkorpiosPet = new AtomicLong(0L);
        public AtomicLong sumArachnePet = new AtomicLong(0L);
        public AtomicLong sumArtioPet = new AtomicLong(0L);
        public AtomicLong sumLittleNightmarePet = new AtomicLong(0L);
        public AtomicLong sumDementorPet = new AtomicLong(0L);
        public AtomicLong sumFenrirGreybackJrPet = new AtomicLong(0L);
        public AtomicLong sumFluffyJrPet = new AtomicLong(0L);
        public AtomicLong sumAncientKBDPet = new AtomicLong(0L);
        public AtomicLong sumAncientChaosElePet = new AtomicLong(0L);
        public AtomicLong sumAncientBarrelchestPet = new AtomicLong(0L);
        public AtomicLong sumFounderImpPet = new AtomicLong(0L);
        public AtomicLong sumBabyLavaDragonPet = new AtomicLong(0L);
        public AtomicLong sumJaltokJadPet = new AtomicLong(0L);
        public AtomicLong sumTzrekZukPet = new AtomicLong(0L);
        public AtomicLong sumRingOfElysianPet = new AtomicLong(0L);
        public AtomicLong sumBloodMoneyPet = new AtomicLong(0L);
        public AtomicLong sumGeniePet = new AtomicLong(0L);
        public AtomicLong sumDharokPet = new AtomicLong(0L);
        public AtomicLong sumZombiesChampionPet = new AtomicLong(0L);
        public AtomicLong sumAbyssalDemonPet = new AtomicLong(0L);
        public AtomicLong sumDarkBeastPet = new AtomicLong(0L);
        public AtomicLong sumBabySquirtPet = new AtomicLong(0L);
        public AtomicLong sumJalnibRekPet = new AtomicLong(0L);
        public AtomicLong sumCentaurMalePet = new AtomicLong(0L);
        public AtomicLong sumCentaurFemalePet = new AtomicLong(0L);
        public AtomicLong sumCrystalHelm = new AtomicLong(0L);
        public AtomicLong sumCrystalBody = new AtomicLong(0L);
        public AtomicLong sumCrystalLegs = new AtomicLong(0L);
        public AtomicLong sumDarkArmadylHelm = new AtomicLong(0L);
        public AtomicLong sumDarkArmadylChestplate = new AtomicLong(0L);
        public AtomicLong sumDarkArmadylChainskirt = new AtomicLong(0L);
        public AtomicLong sumBowOfFaerdhinen = new AtomicLong(0L);
        public ConcurrentHashMap<String, Long> playersValues = new ConcurrentHashMap<>();
        public AtomicInteger toScanAmt = new AtomicInteger(0);
        public AtomicInteger scannedCount = new AtomicInteger(0);
        public ConcurrentLinkedQueue<Player> loaded = new ConcurrentLinkedQueue<>();
        public ConcurrentHashMap<Player, Long> playerBMTotal = new ConcurrentHashMap<>();
        public ConcurrentHashMap<Player, ArrayList<Item>> playerBmItems = new ConcurrentHashMap<>();

        public long vp(Player opp) {
            return 1L * opp.<Integer>getAttribOr(AttributeKey.VOTE_POINS, 0L);
        }

        public long refc(Player opp) {
            return 1L * opp.<Integer>getAttribOr(AttributeKey.REFERRALS_COUNT, 0L);
        }

        public long BMtotal(Player opp) {
            return 1L * playerBMTotal.getOrDefault(opp, 0L);
        }

    }

    private void submitOfflineScan(Player dev, final HashSet<String> checkedPlayers) {
        AtomicStorage storage = new AtomicStorage();
        GameEngine.getInstance().submitLowPriority(() -> {
            try (Stream<Path> walk = Files.walk(Paths.get("data", "saves", "characters"))) {
                List<String> result = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());

                // TODO possible to sort by modified date? recent first. just for niceity when reading dump file. 2k files scan surprisingly fast. <2s
                logger.trace("scanning offline profiles: {} : {}", result.size(), Arrays.toString(result.toArray()));

                ArrayList<Player> offlineToScan = new ArrayList<>(result.size());
                for (String p2name : result) {
                    String rsn = p2name.substring(p2name.lastIndexOf("\\") + 1, p2name.lastIndexOf("."));
                    p2name = Utils.formatText(rsn);
                    //If we checked the online player, skip them.
                    if (checkedPlayers.contains(p2name)) {
                        continue;
                    }
                    //We probably don't need to add them to checked players, but lets do it anyway.
                    checkedPlayers.add(p2name);
                    Player opp = new Player();
                    opp.setUsername(Utils.formatText(p2name));
                    /*if (opp.getUsername().toLowerCase().equals("box test")) continue;
                    if(opp.getPlayerRights().isAdminOrGreater(opp)) continue;
                    if(opp.getPlayerRights().isDeveloperOrGreater(opp)) continue;*/
                    offlineToScan.add(opp);
                }
                storage.toScanAmt.set(offlineToScan.size());

                logger.trace("scanning offline players: {} : {}", offlineToScan.size(), Arrays.toString(offlineToScan
                    .stream().map(p -> p.getUsername()).toArray()));

                for (Player opp : offlineToScan) {
                    GameEngine.getInstance().submitLowPriority(() -> {
                        try {
                            if (PlayerSave.loadOfflineWithoutPassword(opp)) {
                                storage.loaded.add(opp);
                                //We don't really need to check the container's on the game thread since we aren't modifying anything.
                                //GameEngine.getInstance().addSyncTask(() -> {
                                long playerBloodMoneyWealth = 0;

                                storage.sumVotePoints.addAndGet(1L * opp.<Integer>getAttribOr(AttributeKey.VOTE_POINS, 0));
                                storage.sumRefersByName.addAndGet(1L * opp.<Integer>getAttribOr(AttributeKey.REFERRALS_COUNT, 0));

                                ArrayList<Item> allItems = new ArrayList<>();
                                allItems.addAll(Arrays.asList(opp.getEquipment().toNonNullArray()));
                                allItems.addAll(Arrays.asList(opp.inventory().toNonNullArray()));
                                allItems.addAll(Arrays.asList(opp.getBank().toNonNullArray()));
                                allItems.addAll(Arrays.asList(opp.getLootingBag().toNonNullArray()));
                                allItems.addAll(Arrays.asList(opp.getRunePouch().toNonNullArray()));
                                allItems.addAll(opp.<ArrayList<Item>>getAttribOr(AttributeKey.NIFFLER_ITEMS_STORED, new ArrayList<Item>()));

                                final var listings = TradingPost.getListings(opp.getUsername());
                                if(listings != null) {
                                    List<TradingPostListing> list = listings.getListedItems();
                                    for (TradingPostListing tradingPostListing : list) {
                                        allItems.add(tradingPostListing.getSaleItem());
                                    }
                                }

                                for (Item item : allItems) {
                                    if(item.getBloodMoneyPrice() == null) continue;
                                    storage.itemsCount.addAndGet(1);

                                    if(item.getId() == ELYSIAN_SPIRIT_SHIELD) {
                                        storage.sumEly.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ARCANE_SPIRIT_SHIELD) {
                                        storage.sumArcane.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ARMADYL_GODSWORD) {
                                        storage.sumAgs.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ARMADYL_GODSWORD_OR) {
                                        storage.sumAgsOR.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == NEITIZNOT_FACEGUARD) {
                                        storage.sumFacegaurd.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == DRAGON_HUNTER_LANCE) {
                                        storage.sumDHL.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ANCIENT_WYVERN_SHIELD) {
                                        storage.sumWyvernShield.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == DRAGONFIRE_WARD) {
                                        storage.sumWardShield.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == SCYTHE_OF_VITUR) {
                                        storage.sumScythe.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == TWISTED_BOW) {
                                        storage.sumTwistedbow.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ELDER_MAUL) {
                                        storage.sumElderMaul.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == INFERNAL_CAPE) {
                                        storage.sumInfernalCape.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == SERPENTINE_HELM) {
                                        storage.sumSerp.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == TOXIC_STAFF_OF_THE_DEAD) {
                                        storage.sumTSOTD.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == CRAWS_BOW) {
                                        storage.sumCrawBow.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == VIGGORAS_CHAINMACE) {
                                        storage.sumVig.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == FEROCIOUS_GLOVES) {
                                        storage.sumFero.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == AVERNIC_DEFENDER) {
                                        storage.sumAvernic.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == PRIMORDIAL_BOOTS) {
                                        storage.sumPrim.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == PEGASIAN_BOOTS) {
                                        storage.sumPeg.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ETERNAL_BOOTS) {
                                        storage.sumEternal.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ANCESTRAL_HAT) {
                                        storage.sumAncestralHat.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ANCESTRAL_ROBE_TOP) {
                                        storage.sumAncestralTop.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == ANCESTRAL_ROBE_BOTTOM) {
                                        storage.sumAncestralBottom.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == TWISTED_ANCESTRAL_HAT) {
                                        storage.sumAncestralHatT.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == TWISTED_ANCESTRAL_ROBE_TOP) {
                                        storage.sumAncestralTopT.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == TWISTED_ANCESTRAL_ROBE_BOTTOM) {
                                        storage.sumAncestralBottomT.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == DRAGON_CLAWS) {
                                        storage.sumClaws.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == LITTLE_NIGHTMARE) {
                                        storage.sumLittleNightmarePet.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == TZREKZUK) {
                                        storage.sumTzrekZukPet.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == JALNIBREK) {
                                        storage.sumJalnibRekPet.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == CRYSTAL_HELM) {
                                        storage.sumCrystalHelm.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == CRYSTAL_BODY) {
                                        storage.sumCrystalBody.addAndGet(1L * item.getAmount());
                                    }
                                    if(item.getId() == CRYSTAL_LEGS) {
                                        storage.sumCrystalLegs.addAndGet(1L * item.getAmount());
                                    }
                                    if (item.getBloodMoneyPrice().value() > 0)
                                        storage.playerBmItems.compute(opp, (k, v) -> {
                                            if (v == null)
                                                v = new ArrayList<>();
                                            v.add(item);
                                            return v;
                                        });
                                    if (item.getId() == BLOOD_MONEY) {
                                        storage.sumBloodMoneyItemWealth.addAndGet(1L * item.getAmount());
                                        storage.sumBloodMoneyWealth.addAndGet(1L * item.getAmount());
                                        playerBloodMoneyWealth += 1L * item.getAmount();
                                    } else if (item.getId() == BLOODY_TOKEN) {
                                        storage.sumBloodMoneyItemWealth.addAndGet(1L * item.getAmount() * 1000);
                                        storage.sumBloodMoneyWealth.addAndGet(1L * item.getAmount() * 1000);
                                        playerBloodMoneyWealth += 1L * item.getAmount() * 1000;
                                    } else {
                                        storage.sumBloodMoneyItemWealth.addAndGet(1L * item.getBloodMoneyPrice().value() * 1L * item.getAmount());
                                        playerBloodMoneyWealth += 1L * item.getBloodMoneyPrice().value() * 1L * item.getAmount();
                                    }
                                }

                                storage.playerBMTotal.put(opp, playerBloodMoneyWealth);

                                if (storage.playerBmItems.containsKey(opp))
                                    storage.playerBmItems.get(opp).sort(new Comparator<Item>() {
                                        @Override
                                        public int compare(Item o2, Item o1) {
                                            return Long.compare(1L * o1.getBloodMoneyPrice().value() * o1.getAmount(), 1L * o2.getBloodMoneyPrice().value() * o2.getAmount());
                                        }
                                    });

                                // store a message to the wealth amount used for printing to file
                                String mac = opp.getAttribOr(MAC_ADDRESS, "invalid");
                                if (mac == null || mac.length() < 2)
                                    mac = "invalid";
                                storage.playersValues.put(opp.getUsername() + " " + mac + " ", playerBloodMoneyWealth);

                                //player.message("The total gold wealth for player " + plr2.getUsername() + " is: " + Misc.formatNumber(goldWealth));
                                //player.message("The total blood money wealth for player " + plr2.getUsername() + " is: " + Misc.formatNumber(bloodMoneyWealth));
                                //player.message("The total item count for player " + plr2.getUsername() + " is: " + Misc.formatNumber(itemsCount));

                                // println every 20 files scanned for progress updates
                                int current = storage.scannedCount.addAndGet(1);
                                int goal = storage.toScanAmt.get();
                                if (current % 20 == 0) {
                                    // every 20
                                    logger.info("offline profile scanning {}/{}  ({}%) complete. {} remaining...", current, goal, (1d * current) / goal * 100, goal - current);
                                }
                                if (current == goal) {
                                    logger.info("scanning complete! saving info...");
                                }
                            } else {
                                dev.message("Something wrong went checking server wealth for offline Player " + opp.getUsername());
                                logger.error("Something wrong went checking server wealth for offline Player " + opp.getUsername());
                            }
                        } catch (Exception e) {
                            dev.message("Something wrong went checking server wealth for offline Player " + opp.getUsername());
                            logger.error("Something wrong went checking server wealth for offline Player " + opp.getUsername());
                            logger.catching(e);
                        }
                    });
                }
            } catch (Exception e) {
                logger.catching(e);
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isOwner(player);
    }
}
