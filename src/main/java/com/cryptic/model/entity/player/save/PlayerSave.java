package com.cryptic.model.entity.player.save;

import com.cryptic.GameServer;
import com.cryptic.PlainTile;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.bank_pin.BankPinModification;
import com.cryptic.model.content.collection_logs.Collection;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.presets.Presetable;
import com.cryptic.model.content.sigils.data.SigilData;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.content.teleport.world_teleport_manager.TeleportData;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.weapon.FightType;
import com.cryptic.model.entity.player.GameMode;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.rights.MemberRights;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.model.inter.lootkeys.LootKey;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.presets.PresetData;
import com.cryptic.model.map.position.Tile;
import com.cryptic.services.database.transactions.UpdatePasswordDatabaseTransaction;
import com.cryptic.utility.Varp;
import com.cryptic.utility.timers.TimerKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.ConstructorConstructor;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;
import static com.cryptic.model.inter.lootkeys.LootKey.LOOT_KEY_CONTAINER_SIZE;

/**
 * Handles saving a player's container and details into a json file.
 * <br><br>
 * Type safety enforced when using OSS's {@link AttributeKey} by Shadowrs/Jak on 06/06/2020
 *
 * @author Origin | 28 feb. 2019 : 12:16:21
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server profile</a>
 */
public class PlayerSave {

    /**
     * SUPER IMPORTANT INFO: Player class needs to have default values set for any objects (or variables) that could be null that it tries to access on login to prevent NPEs thrown when loading a Player from PlayerSave.
     * In other words, when adding any new variables to PlayerSave that might be accessed upon login, make sure to set default values in Player class (for existing players that don't have the new features yet).
     * ALSO: Make super sure to be careful that when adding any new save objects (or variables) here, when loading the details, setting them here may mean they are null so they will set the Player variables to null which will cause NPEs.
     * In other words, make sure to properly null check in the Player class and in other places throughout the server code.
     */

    private static final Logger logger = LogManager.getLogger(PlayerSave.class);

    static final Map<Type, InstanceCreator<?>> instanceCreators = Collections.emptyMap();

    public static final Gson SERIALIZE = new GsonBuilder()
        .setDateFormat("MMM d, yyyy, HH:mm:ss a")
        .setPrettyPrinting()
        .registerTypeAdapterFactory(
            new MapTypeAdapterFactoryNulls(new ConstructorConstructor(instanceCreators, false, new ArrayList<>()), false))
        .disableHtmlEscaping()
        .create();

    /**
     * Loads all the details of the {@code player}.
     *
     * @param player The player to load details for
     */
    public static boolean load(Player player) throws Exception {
        try {
            player.getFarming().load();
        } catch (Exception e) {
            logger.error("Error while loading farming {}", player.getUsername(), e);
            e.printStackTrace();
        }
        return SaveDetails.loadDetails(player);
    }

    public static boolean loadOffline(Player player, String enteredPassword) throws Exception {
        if (!SaveDetails.loadDetails(player)) {
            return false;
        }
        player.setPassword(enteredPassword);
        return true;
    }

    public static boolean loadOfflineWithoutPassword(Player player) throws Exception {
        return SaveDetails.loadDetails(player);
    }

    /**
     * Saves all the details of the {@code player}.
     *
     * @param player The player to save details for
     */
    public static boolean save(Player player) {
        try {
            new SaveDetails(player).parseDetails();
            player.getFarming().save();
            return true;
        } catch (final Exception e) {
            logger.error("save", e);
        }
        return false;
    }

    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Handles saving and loading player's details.
     */
    public static final class SaveDetails {

        public static boolean loadDetails(final Player player) throws Exception {
            final Path path = SAVE_DIR.resolve(player.getUsername() + ".json");
            if (!Files.exists(path)) {
                return false;
            }

            final SaveDetails details;
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                details = PlayerSave.SERIALIZE.fromJson(reader, SaveDetails.class);
            }

            applyDetails(player, details);
            return true;
        }

        private static void applyDetails(final Player player, final SaveDetails details) {
            player.setUsername(details.username);
            player.setPassword(details.password);
            player.setNewPassword("");
            if (details.tile != null)
                player.setTile(details.tile.tile());
            if (details.playerRights != null)
                player.setPlayerRights(PlayerRights.valueOf(details.playerRights));
            if (details.memberRights != null)
                player.setMemberRights(MemberRights.valueOf(details.memberRights));
            if (details.gameMode != null)
                player.setGameMode(details.gameMode);
            if (details.ironMode == null) {
                player.setIronmanStatus(IronMode.NONE);
            } else {
                player.setIronmanStatus(details.ironMode);
            }
            if (details.lastIP != null) {
                player.setHostAddress(details.lastIP);
            }
            player.getHostAddressMap().put(player.getHostAddress(), 1);
            if (details.creationDate != null)
                player.setCreationDate(details.creationDate);
            if (details.creationIp != null)
                player.setCreationIp(details.creationIp);
            if (details.lastLogin != null)
                player.setLastLogin(details.lastLogin);

            if (details.topPkerReward != null)
                player.putAttrib(AttributeKey.TOP_PKER_REWARD, details.topPkerReward);
            player.looks().female(details.female);
            if (details.looks != null)
                player.looks().looks(details.looks);
            if (details.colors != null)
                player.looks().colors(details.colors);
            if (details.spellBook != null)
                player.setSpellbook(MagicSpellbook.valueOf(details.spellBook));
            if (details.fightType != null)
                player.getCombat().setFightType(FightType.valueOf(details.fightType));
            player.getCombat().getFightType().setParentId(details.fightTypeVarp);
            player.getCombat().getFightType().setChildId(details.fightTypeVarpState);
            player.getCombat().setAutoRetaliate(details.autoRetaliate);
            if (details.previousSpellbook != null) {
                player.setPreviousSpellbook(details.previousSpellbook);
            }
            if (details.lootKeys != null) {
                for (int i = 0; i < LootKey.infoForPlayer(player).keys.length; i++) {
                    for (Map.Entry<Integer, Item[]> integerEntry : details.lootKeys.entrySet()) {
                        ItemContainer ic = new ItemContainer(LOOT_KEY_CONTAINER_SIZE, ItemContainer.StackPolicy.ALWAYS);
                        ic.addAll(integerEntry.getValue());
                        LootKey.infoForPlayer(player).keys[integerEntry.getKey()] = new LootKey(ic, ic.containerValue());
                    }
                }
            }
            if (details.sigils != null) {
                for (var s : details.sigils) {
                    player.putAttrib(s, true);
                }
            }
            player.putAttrib(AttributeKey.LOOT_KEYS_CARRIED, details.lootKeysCarried);
            player.putAttrib(AttributeKey.TOTAL_SIGILS_ACTIVATED, details.totalSigilsActivated);
            player.putAttrib(AttributeKey.LOOT_KEYS_LOOTED, details.lootKeysLooted);
            player.putAttrib(AttributeKey.TOTAL_LOOT_KEYS_VALUE, details.totalLootKeysValue);
            player.putAttrib(AttributeKey.LOOT_KEYS_UNLOCKED, details.lootKeysUnlocked);
            player.putAttrib(AttributeKey.LOOT_KEYS_ACTIVE, details.lootKeysActive);
            player.putAttrib(AttributeKey.LOOT_KEYS_DROP_CONSUMABLES, details.lootKeysDropConsumables);
            player.putAttrib(AttributeKey.SEND_VALUABLES_TO_LOOT_KEYS, details.sendValuablesToLootKey);
            player.putAttrib(AttributeKey.LOOT_KEYS_VALUABLE_ITEM_THRESHOLD, details.lootKeysValuableItemThreshold);
            player.setSpecialAttackPercentage(details.specPercentage);
            player.getTargetSearchTimer().start(details.targetSearchTimer);
            player.getSpecialAttackRestore().start(details.specialAttackRestoreTimer);
            player.setSkullType(details.skullType);
            if (details.quickPrayers != null)
                player.getQuickPrayers().setPrayers(details.quickPrayers);
            if (details.presets != null) {
                // put into individual slots, dont replace an array[20] with a game save array[10]
                for (int i = 0; i < details.presets.length; i++) {
                    player.getPresets()[i] = details.presets[i];
                }
            }
            if (details.lastPreset != null) {
                player.setLastPreset(details.lastPreset);
            }
            if (details.presetsv2 != null) {
                player.setPresetData(details.presetsv2);
            }
            player.getTimers().register(TimerKey.SPECIAL_TELEBLOCK, details.specialTeleblockTimer);
            player.putAttrib(AttributeKey.MEMBER_UNLOCKED, details.memberUnlocked);
            player.putAttrib(AttributeKey.SUPER_MEMBER_UNLOCKED, details.superMemberUnlocked);
            player.putAttrib(AttributeKey.ELITE_MEMBER_UNLOCKED, details.eliteMemberUnlocked);
            player.putAttrib(AttributeKey.EXTREME_MEMBER_UNLOCKED, details.extremeMemberUnlocked);
            player.putAttrib(AttributeKey.LEGENDARY_MEMBER_UNLOCKED, details.legendaryMemberUnlocked);
            player.putAttrib(AttributeKey.VIP_UNLOCKED, details.vipUnlocked);
            player.putAttrib(AttributeKey.SPONSOR_UNLOCKED, details.sponsorMemberUnlocked);
            if (details.saved_tornament_levels != null && details.saved_tornament_xp != null)
                player.skills().restoreLevels(details.saved_tornament_xp, details.saved_tornament_levels);
            player.getSkills().setAllLevels(details.dynamicLevels);
            player.getSkills().setAllXps(details.skillXP);
            if (details.unlockedPets != null) {
                player.setUnlockedPets(details.unlockedPets);
            }
            if (details.insuredPets != null) {
                player.setInsuredPets(details.insuredPets);
            }

            if (details.blockedSlayerTasks != null) {
                player.getSlayerRewards().setBlockedSlayerTask(details.blockedSlayerTasks);
            }

            if (details.slayerUnlocks != null) {
                player.getSlayerRewards().setUnlocks(details.slayerUnlocks);
            }

            if (details.slayerExtensionsList != null) {
                player.getSlayerRewards().setExtendable(details.slayerExtensionsList);
            }

            player.putAttrib(AttributeKey.SLAYER_REWARD_POINTS, details.slayerPoints);


            if (details.inventory != null) {
                for (int i = 0; i < details.inventory.length; i++) {
                    player.inventory().set(i, details.inventory[i], false);
                }
            }
            if (details.equipment != null) {
                for (int i = 0; i < details.equipment.length; i++) {
                    player.getEquipment().set(i, details.equipment[i], false);
                }
            }
            if (details.bank != null) {
                for (int i = 0; i < details.bank.length; i++) {
                    player.getBank().set(i, details.bank[i], false);
                }
            }
            if (details.tabAmounts != null) {
                if (details.tabAmounts.length >= 0)
                    System.arraycopy(details.tabAmounts, 0, player.getBank().tabAmounts, 0, details.tabAmounts.length);
            }
            player.getBank().placeHolder = details.placeholdersActive;
            player.getBank().placeHolderAmount = details.placeHolderAmount;
            player.getBankPin().setHashedPin(details.hashedBankPin);
            player.getBankPin().setPinLength(details.bankPinLength);
            player.getBankPin().setRecoveryDays(details.recoveryDelay);
            player.getBankPin().setPendingMod(details.pendingBankPinMod);
            if (details.lootingBag != null) {
                for (int index = 0; index < details.lootingBag.length; index++) {
                    player.getLootingBag().set(index, details.lootingBag[index], false);
                }
            }
            player.getLootingBag().setAskHowManyToStore(details.askHowManyToStore);
            player.getLootingBag().setStoreAsMany(details.storeAsMany);
            if (details.runePouch != null) {
                for (int index = 0; index < details.runePouch.length; index++) {
                    player.getRunePouch().set(index, details.runePouch[index], false);
                }
            }
            if (details.cartItems != null) {
                player.putAttrib(AttributeKey.CART_ITEMS, details.cartItems);
            }
            if (details.nifflerItems != null) {
                player.putAttrib(AttributeKey.NIFFLER_ITEMS_STORED, details.nifflerItems);
            }
            if (details.sackOfPresentItems != null) {
                player.putAttrib(AttributeKey.SACK_OF_PRESENTS_LIST, details.sackOfPresentItems);
            }
            if (details.newFriends == null)
                details.newFriends = new ArrayList<>(200);
            for (String friend : details.newFriends) {
                player.getRelations().getFriendList().add(friend);
            }
            if (details.newIgnores == null)
                details.newIgnores = new ArrayList<>(100);
            for (String ignore : details.newIgnores) {
                player.getRelations().getIgnoreList().add(ignore);
            }
            if (details.clan != null)
                player.setClanChat(details.clan);

            player.getPresetManager().setSaveLevels(details.savePresetLevels);
            player.getPresetManager().setOpenOnDeath(details.openPresetsOnDeath);
            if (details.savedDuelConfig != null) {
                player.setSavedDuelConfig(details.savedDuelConfig);
            }

            if (details.recentKills != null) {
                for (String kills : details.recentKills) {
                    player.getRecentKills().add(kills);
                }
            }

            player.putAttrib(AttributeKey.LAVA_BEASTS_KILLED, details.lavaBeastsKilled);
            player.putAttrib(AttributeKey.EL_FUEGO_KILLED, details.elFuegoKilled);
            player.putAttrib(AttributeKey.DERANGED_ARCHAEOLOGIST_KILLED, details.derangedArchaeologistKilled);

            if (details.bossTimers != null) {
                player.getBossTimers().setTimes(details.bossTimers);
            }
            if (details.recentTeleports != null) {
                player.setRecentTeleports(details.recentTeleports);
            }
            if (details.favoriteTeleports != null) {
                player.setFavorites(details.favoriteTeleports);
            }
            if (details.collectionLog != null) {
                player.getCollectionLog().collectionLog = details.collectionLog;
            }
            if (details.achievements != null) {
                player.achievements().putAll(details.achievements);
            }

            if (details.task != null) {
                player.putAttrib(AttributeKey.TASK, details.task);
            }

            player.putAttrib(AttributeKey.LOC_BEFORE_JAIL, details.locBeforeJail.tile());

            player.putAttrib(AttributeKey.ALCHEMICAL_HYDRA_LOG_CLAIMED, details.alchemicalHydraLogClaimed);
            player.putAttrib(AttributeKey.ANCIENT_BARRELCHEST_LOG_CLAIMED, details.ancientBarrelchestLogClaimed);
            player.putAttrib(AttributeKey.ANCIENT_CHAOS_ELEMENTAL_LOG_CLAIMED, details.ancientChaosElementalLogClaimed);
            player.putAttrib(AttributeKey.ANCIENT_KING_BLACK_DRAGON_LOG_CLAIMED, details.ancientKingBlackDragonLogClaimed);
            player.putAttrib(AttributeKey.ARACHNE_LOG_CLAIMED, details.arachneLogClaimed);
            player.putAttrib(AttributeKey.ARTIO_LOG_CLAIMED, details.artioLogClaimed);
            player.putAttrib(AttributeKey.SEREN_LOG_CLAIMED, details.serenLogClaimed);
            player.putAttrib(AttributeKey.BARRELCHEST_LOG_CLAIMED, details.barrelchestLogClaimed);
            player.putAttrib(AttributeKey.BRUTAL_LAVA_DRAGON_LOG_CLAIMED, details.brutalLavaDragonLogClaimed);
            player.putAttrib(AttributeKey.CALLISTO_LOG_CLAIMED, details.callistoLogClaimed);
            player.putAttrib(AttributeKey.CERBERUS_LOG_CLAIMED, details.cerberusLogClaimed);
            player.putAttrib(AttributeKey.CHAOS_ELEMENTAL_LOG_CLAIMED, details.chaosElementalLogClaimed);
            player.putAttrib(AttributeKey.CHAOS_FANATIC_LOG_CLAIMED, details.chaosFanaticLogClaimed);
            player.putAttrib(AttributeKey.CORPOREAL_BEAST_LOG_CLAIMED, details.corporealBeastLogClaimed);
            player.putAttrib(AttributeKey.CORRUPTED_NECHRYARCH_LOG_CLAIMED, details.corruptedNechryarchLogClaimed);
            player.putAttrib(AttributeKey.CRAZY_ARCHAEOLOGIST_LOG_CLAIMED, details.crazyArchaeologistLogClaimed);
            player.putAttrib(AttributeKey.DEMONIC_GORILLA_LOG_CLAIMED, details.demonicGorillaLogClaimed);
            player.putAttrib(AttributeKey.GIANT_MOLE_LOG_CLAIMED, details.giantMoleLogClaimed);
            player.putAttrib(AttributeKey.KERBEROS_LOG_CLAIMED, details.kerberosLogClaimed);
            player.putAttrib(AttributeKey.KING_BLACK_DRAGON_LOG_CLAIMED, details.kingBlackDragonLogClaimed);
            player.putAttrib(AttributeKey.KRAKEN_LOG_CLAIMED, details.krakenLogClaimed);
            player.putAttrib(AttributeKey.LAVA_DRAGON_LOG_CLAIMED, details.lavaDragonLogClaimed);
            player.putAttrib(AttributeKey.LIZARDMAN_SHAMAN_LOG_CLAIMED, details.lizardmanShamanLogClaimed);
            player.putAttrib(AttributeKey.SCORPIA_LOG_CLAIMED, details.scorpiaLogClaimed);
            player.putAttrib(AttributeKey.SKORPIOS_LOG_CLAIMED, details.skorpiosLogClaimed);
            player.putAttrib(AttributeKey.SKOTIZO_LOG_CLAIMED, details.skotizoLogClaimed);
            player.putAttrib(AttributeKey.TEKTON_LOG_CLAIMED, details.tektonLogClaimed);
            player.putAttrib(AttributeKey.THERMONUCLEAR_SMOKE_DEVIL_LOG_CLAIMED, details.thermonuclearSmokeDevilLogClaimed);
            player.putAttrib(AttributeKey.THE_NIGTHMARE_LOG_CLAIMED, details.theNightmareLogClaimed);
            player.putAttrib(AttributeKey.CORRUPTED_HUNLEFF_LOG_CLAIMED, details.corruptedHunleffLogClaimed);
            player.putAttrib(AttributeKey.MEN_IN_BLACK_LOG_CLAIMED, details.menInBlackLogClaimed);
            player.putAttrib(AttributeKey.TZTOK_JAD_LOG_CLAIMED, details.tztokJadLogClaimed);
            player.putAttrib(AttributeKey.VENENATIS_LOG_CLAIMED, details.venenatisLogClaimed);
            player.putAttrib(AttributeKey.VETION_LOG_CLAIMED, details.vetionLogClaimed);
            player.putAttrib(AttributeKey.VORKATH_LOG_CLAIMED, details.vorkathLogClaimed);
            player.putAttrib(AttributeKey.ZOMBIES_CHAMPION_LOG_CLAIMED, details.zombiesChampionLogClaimed);
            player.putAttrib(AttributeKey.ZULRAH_LOG_CLAIMED, details.zulrahLogClaimed);
            player.putAttrib(AttributeKey.ARMOUR_MYSTERY_BOX_LOG_CLAIMED, details.armourMysteryBoxLogClaimed);
            player.putAttrib(AttributeKey.DONATOR_MYSTERY_BOX_LOG_CLAIMED, details.donatorMysteryBoxLogClaimed);
            player.putAttrib(AttributeKey.EPIC_PET_MYSTERY_BOX_LOG_CLAIMED, details.epicPetMysteryBoxLogClaimed);
            player.putAttrib(AttributeKey.MYSTERY_CHEST_LOG_CLAIMED, details.mysteryChestLogClaimed);
            player.putAttrib(AttributeKey.RAIDS_MYSTERY_BOX_LOG_CLAIMED, details.raidsMysteryBoxLogClaimed);
            player.putAttrib(AttributeKey.WEAPON_MYSTERY_BOX_LOG_CLAIMED, details.weaponMysteryBoxLogClaimed);
            player.putAttrib(AttributeKey.LEGENDARY_MYSTERY_BOX_LOG_CLAIMED, details.legendaryMysteryBoxLogClaimed);
            player.putAttrib(AttributeKey.ZENYTE_MYSTERY_BOX_LOG_CLAIMED, details.zenyteLogClaimed);
            player.putAttrib(AttributeKey.CRYSTAL_KEY_LOG_CLAIMED, details.crystalKeyLogClaimed);
            player.putAttrib(AttributeKey.MOLTEN_KEY_LOG_CLAIMED, details.moltenKeyLogClaimed);
            player.putAttrib(AttributeKey.ENCHANTED_KEY_R_LOG_CLAIMED, details.enchantedKeyRLogClaimed);
            player.putAttrib(AttributeKey.ENCHANTED_KEY_P_LOG_CLAIMED, details.enchantedKeyPLogClaimed);
            player.putAttrib(AttributeKey.LARRANS_KEY_TIER_I_LOG_CLAIMED, details.larransKeyTierILogClaimed);
            player.putAttrib(AttributeKey.LARRANS_KEY_TIER_II_LOG_CLAIMED, details.larransKeyTierIILogClaimed);
            player.putAttrib(AttributeKey.LARRANS_KEY_TIER_III_LOG_CLAIMED, details.larransKeyTierIIILogClaimed);
            player.putAttrib(AttributeKey.SLAYER_KEY_LOG_CLAIMED, details.slayerKeyLogClaimed);
            player.putAttrib(AttributeKey.WILDERNESS_KEY_LOG_CLAIMED, details.wildernessKeyLogClaimed);
            player.putAttrib(AttributeKey.ANCIENT_REVENANTS_LOG_CLAIMED, details.ancientRevenantsLogClaimed);
            player.putAttrib(AttributeKey.CHAMBER_OF_SECRETS_LOG_CLAIMED, details.chamberOfSecretsLogClaimed);
            player.putAttrib(AttributeKey.REVENANTS_LOG_CLAIMED, details.revenantsLogClaimed);
            player.putAttrib(AttributeKey.SLAYER_LOG_CLAIMED, details.slayerLogClaimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_1_CLAIMED, details.eventReward1Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_2_CLAIMED, details.eventReward2Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_3_CLAIMED, details.eventReward3Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_4_CLAIMED, details.eventReward4Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_5_CLAIMED, details.eventReward5Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_6_CLAIMED, details.eventReward6Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_7_CLAIMED, details.eventReward7Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_8_CLAIMED, details.eventReward8Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_9_CLAIMED, details.eventReward9Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_10_CLAIMED, details.eventReward10Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_11_CLAIMED, details.eventReward11Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_12_CLAIMED, details.eventReward12Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_13_CLAIMED, details.eventReward13Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_14_CLAIMED, details.eventReward14Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_15_CLAIMED, details.eventReward15Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_16_CLAIMED, details.eventReward16Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_17_CLAIMED, details.eventReward17Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_18_CLAIMED, details.eventReward18Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_19_CLAIMED, details.eventReward19Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_20_CLAIMED, details.eventReward20Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_21_CLAIMED, details.eventReward21Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_22_CLAIMED, details.eventReward22Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_23_CLAIMED, details.eventReward23Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_24_CLAIMED, details.eventReward24Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_25_CLAIMED, details.eventReward25Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_26_CLAIMED, details.eventReward26Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_27_CLAIMED, details.eventReward27Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_28_CLAIMED, details.eventReward28Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_29_CLAIMED, details.eventReward29Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_30_CLAIMED, details.eventReward30Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_31_CLAIMED, details.eventReward31Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_32_CLAIMED, details.eventReward32Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_33_CLAIMED, details.eventReward33Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_34_CLAIMED, details.eventReward34Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_35_CLAIMED, details.eventReward35Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_36_CLAIMED, details.eventReward36Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_37_CLAIMED, details.eventReward37Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_38_CLAIMED, details.eventReward38Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_39_CLAIMED, details.eventReward39Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_40_CLAIMED, details.eventReward40Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_41_CLAIMED, details.eventReward41Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_42_CLAIMED, details.eventReward42Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_43_CLAIMED, details.eventReward43Claimed);
            player.putAttrib(AttributeKey.EVENT_REWARD_44_CLAIMED, details.eventReward44Claimed);
            player.setInvulnerable(details.infhp);
            if (details.varps != null) {
                int[] varps = new int[4000];
                details.varps.forEach((k, v) -> {
                    varps[k] = v;
                });
                player.setSessionVarps(varps);
            }
            player.putAttrib(AttributeKey.DAILY_TASKS_LIST, details.dailyTasksList == null ? new ArrayList<DailyTasks>() : details.dailyTasksList);
            player.putAttrib(AttributeKey.DAILY_TASKS_EXTENSION_LIST, details.dailyTasksExtensions == null ? new HashMap<DailyTasks, Integer>() : details.dailyTasksExtensions);


            ARGS_DESERIALIZER.accept(player, details.allAttribs);

            player.putAttrib(AttributeKey.STARTER_BOW_CHARGES, details.starterBowCharges);
            player.putAttrib(AttributeKey.STARTER_STAFF_CHARGES, details.starterStaffCharges);
            player.putAttrib(AttributeKey.STARTER_SWORD_CHARGES, details.starterSwordCharges);
            if (details.lastRecallSave != null) player.setLastSavedTile(details.lastRecallSave.tile());
            player.putAttrib(AttributeKey.VOID_ISLAND_POINTS, details.voidIslandPoints);
            player.putAttrib(PLAYER_UID, details.playerUID);
        }

        private final long playerUID;
        //Account
        private final String username;
        private final String password;
        private final PlainTile tile;
        private final String playerRights;
        private final String memberRights;
        private final GameMode gameMode;
        private final IronMode ironMode;
        private final String lastIP;
        private final Timestamp creationDate;
        private final String creationIp;
        private final Timestamp lastLogin;
        private final Item topPkerReward;
        private final boolean female;
        private final int[] looks;
        private final int[] colors;

        //Combat attribs
        private final String spellBook;
        private final String fightType;
        private final int fightTypeVarp;
        private final int fightTypeVarpState;
        private final boolean autoRetaliate;
        private final MagicSpellbook previousSpellbook;

        @Expose
        private final HashMap<Integer, Item[]> lootKeys;
        private int lootKeysCarried;
        @Expose
        private final List<AttributeKey> sigils;
        private int totalSigilsActivated;
        private int lootKeysLooted;
        private long totalLootKeysValue;
        private boolean lootKeysUnlocked;
        private boolean lootKeysActive;
        private boolean lootKeysDropConsumables;
        private boolean sendValuablesToLootKey;
        private int lootKeysValuableItemThreshold;
        private final int specPercentage;
        private final int targetSearchTimer;
        private final int specialAttackRestoreTimer;
        private final SkullType skullType;
        private final DefaultPrayerData[] quickPrayers;
        private final Presetable[] presets;
        private final PresetData[] presetsv2;
        private final Object[] lastPreset;
        private final int specialTeleblockTimer;

        //Member attribs
        private final boolean memberUnlocked;
        private final boolean superMemberUnlocked;
        private final boolean eliteMemberUnlocked;
        private final boolean extremeMemberUnlocked;
        private final boolean legendaryMemberUnlocked;
        private final boolean vipUnlocked;
        private final boolean sponsorMemberUnlocked;

        public boolean infhp;
        private final HashMap<Integer, Integer> varps;

        //Skills
        private final double[] saved_tornament_xp;
        private final int[] saved_tornament_levels;
        private final int[] dynamicLevels;
        private final double[] skillXP;
        private final ArrayList<Integer> unlockedPets;
        private final ArrayList<Integer> insuredPets;

        private final List<Integer> blockedSlayerTasks;
        private final HashMap<Integer, String> slayerUnlocks;
        private final HashMap<Integer, String> slayerExtensionsList;
        private final int slayerPoints;

        //Containers
        private final Item[] inventory;
        private final Item[] equipment;
        private final Item[] bank;
        private final int[] tabAmounts;
        private final boolean placeholdersActive;
        private final int placeHolderAmount;
        private final String hashedBankPin;
        private final int bankPinLength;
        private final int recoveryDelay;
        private final BankPinModification pendingBankPinMod;
        private final Item[] lootingBag;
        private final boolean askHowManyToStore;
        private final boolean storeAsMany;
        private final Item[] runePouch;
        private final ArrayList<Item> nifflerItems;
        private final ArrayList<Item> sackOfPresentItems;
        private final ArrayList<Item> cartItems;

        //Friends
        private List<String> newFriends;

        //Ignores
        private List<String> newIgnores;

        //Clan
        private final String clan;

        //Settings

        private final boolean savePresetLevels;
        private final boolean openPresetsOnDeath;
        private final boolean[] savedDuelConfig;

        private final List<String> recentKills;

        private final int lavaBeastsKilled;
        private final int elFuegoKilled;
        private final int derangedArchaeologistKilled;


        //Content
        private final Map<String, Integer> bossTimers;
        private final List<TeleportData> recentTeleports;
        private final List<TeleportData> favoriteTeleports;
        private final HashMap<Collection, ArrayList<Item>> collectionLog;
        private final HashMap<Achievements, Integer> achievements;

        private final Tasks task;

        private final PlainTile locBeforeJail;

        private final int starterBowCharges;
        private final int starterStaffCharges;
        private final int starterSwordCharges;
        private final PlainTile lastRecallSave;
        private final boolean alchemicalHydraLogClaimed;
        private final boolean ancientBarrelchestLogClaimed;
        private final boolean ancientChaosElementalLogClaimed;
        private final boolean ancientKingBlackDragonLogClaimed;
        private final boolean arachneLogClaimed;
        private final boolean artioLogClaimed;
        private final boolean serenLogClaimed;
        private final boolean barrelchestLogClaimed;
        private final boolean brutalLavaDragonLogClaimed;
        private final boolean callistoLogClaimed;
        private final boolean cerberusLogClaimed;
        private final boolean chaosElementalLogClaimed;
        private final boolean chaosFanaticLogClaimed;
        private final boolean corporealBeastLogClaimed;
        private final boolean corruptedNechryarchLogClaimed;
        private final boolean crazyArchaeologistLogClaimed;
        private final boolean demonicGorillaLogClaimed;
        private final boolean giantMoleLogClaimed;
        private final boolean kerberosLogClaimed;
        private final boolean kingBlackDragonLogClaimed;
        private final boolean krakenLogClaimed;
        private final boolean lavaDragonLogClaimed;
        private final boolean lizardmanShamanLogClaimed;
        private final boolean scorpiaLogClaimed;
        private final boolean skorpiosLogClaimed;
        private final boolean skotizoLogClaimed;
        private final boolean tektonLogClaimed;
        private final boolean thermonuclearSmokeDevilLogClaimed;
        private final boolean theNightmareLogClaimed;
        private final boolean corruptedHunleffLogClaimed;
        private final boolean menInBlackLogClaimed;
        private final boolean tztokJadLogClaimed;
        private final boolean venenatisLogClaimed;
        private final boolean vetionLogClaimed;
        private final boolean vorkathLogClaimed;
        private final boolean zombiesChampionLogClaimed;
        private final boolean zulrahLogClaimed;
        private final boolean armourMysteryBoxLogClaimed;
        private final boolean donatorMysteryBoxLogClaimed;
        private final boolean epicPetMysteryBoxLogClaimed;
        private final boolean mysteryChestLogClaimed;
        private final boolean raidsMysteryBoxLogClaimed;
        private final boolean weaponMysteryBoxLogClaimed;
        private final boolean legendaryMysteryBoxLogClaimed;
        private final boolean zenyteLogClaimed;
        private final boolean crystalKeyLogClaimed;
        private final boolean moltenKeyLogClaimed;
        private final boolean enchantedKeyRLogClaimed;
        private final boolean enchantedKeyPLogClaimed;
        private final boolean larransKeyTierILogClaimed;
        private final boolean larransKeyTierIILogClaimed;
        private final boolean larransKeyTierIIILogClaimed;
        private final boolean slayerKeyLogClaimed;
        private final boolean wildernessKeyLogClaimed;
        private final boolean ancientRevenantsLogClaimed;
        private final boolean chamberOfSecretsLogClaimed;
        private final boolean revenantsLogClaimed;
        private final boolean slayerLogClaimed;
        private final boolean eventReward1Claimed;
        private final boolean eventReward2Claimed;
        private final boolean eventReward3Claimed;
        private final boolean eventReward4Claimed;
        private final boolean eventReward5Claimed;
        private final boolean eventReward6Claimed;
        private final boolean eventReward7Claimed;
        private final boolean eventReward8Claimed;
        private final boolean eventReward9Claimed;
        private final boolean eventReward10Claimed;
        private final boolean eventReward11Claimed;
        private final boolean eventReward12Claimed;
        private final int voidIslandPoints;
        private final boolean eventReward13Claimed;
        private final boolean eventReward14Claimed;
        private final boolean eventReward15Claimed;
        private final boolean eventReward16Claimed;
        private final boolean eventReward17Claimed;
        private final boolean eventReward18Claimed;
        private final boolean eventReward19Claimed;
        private final boolean eventReward20Claimed;
        private final boolean eventReward21Claimed;
        private final boolean eventReward22Claimed;
        private final boolean eventReward23Claimed;
        private final boolean eventReward24Claimed;
        private final boolean eventReward25Claimed;
        private final boolean eventReward26Claimed;
        private final boolean eventReward27Claimed;
        private final boolean eventReward28Claimed;
        private final boolean eventReward29Claimed;
        private final boolean eventReward30Claimed;
        private final boolean eventReward31Claimed;
        private final boolean eventReward32Claimed;
        private final boolean eventReward33Claimed;
        private final boolean eventReward34Claimed;
        private final boolean eventReward35Claimed;
        private final boolean eventReward36Claimed;
        private final boolean eventReward37Claimed;
        private final boolean eventReward38Claimed;
        private final boolean eventReward39Claimed;
        private final boolean eventReward40Claimed;
        private final boolean eventReward41Claimed;
        private final boolean eventReward42Claimed;
        private final boolean eventReward43Claimed;
        private final boolean eventReward44Claimed;

        private final ArrayList<DailyTasks> dailyTasksList;

        private final Map<DailyTasks, Integer> dailyTasksExtensions;
        @Expose
        private final Map<String, String> allAttribs;

        public String password() {
            return password;
        }

        public SaveDetails(Player player) {
            username = player.getUsername();
            if (player.getNewPassword() != null && !player.getNewPassword().equals("")) { // new pw has been set
                password = BCrypt.hashpw(player.getNewPassword(), BCrypt.gensalt());
                if (GameServer.properties().enableSql) {
                    GameServer.getDatabaseService().submit(new UpdatePasswordDatabaseTransaction(player, password));
                }
                player.setPassword(password);
            } else {
                password = player.getPassword();
            }
            tile = player.tile().toPlain();
            playerRights = player.getPlayerRights().name();
            memberRights = player.getMemberRights().name();
            gameMode = player.getGameMode();
            ironMode = player.getIronManStatus();
            lastIP = player.getHostAddress();
            creationDate = player.getCreationDate();
            creationIp = player.getCreationIp();
            lastLogin = player.getLastLogin();
            topPkerReward = player.<Item>getAttribOr(AttributeKey.TOP_PKER_REWARD, null);
            female = player.looks().female();
            looks = player.looks().looks();
            colors = player.looks().colors();
            spellBook = player.getSpellbook().name();
            fightType = player.getCombat().getFightType().name();
            fightTypeVarp = player.getCombat().getFightType().getParentId();
            fightTypeVarpState = player.getCombat().getFightType().getChildId();
            autoRetaliate = player.getCombat().hasAutoReliateToggled();
            previousSpellbook = player.getPreviousSpellbook();
            sigils = new ArrayList<>();
            for (var s : SigilData.values()) {
                if (player.hasAttrib(s.attributeKey)) {
                    sigils.add(s.attributeKey);
                }
            }
            lootKeys = new HashMap<>();
            if (LootKey.infoForPlayer(player) != null) {
                for (int i = 0; i < LootKey.infoForPlayer(player).keys.length; i++) {
                    if (LootKey.infoForPlayer(player).keys != null && LootKey.infoForPlayer(player).keys[i] != null) {
                        lootKeys.put(i, LootKey.infoForPlayer(player).keys[i].lootContainer.getItems());
                    }
                }
            }
            specPercentage = player.getSpecialAttackPercentage();
            targetSearchTimer = player.getTargetSearchTimer().secondsRemaining();
            specialAttackRestoreTimer = player.getSpecialAttackRestore().secondsRemaining();
            skullType = player.getSkullType();
            quickPrayers = player.getQuickPrayers().getPrayers();
            presets = player.getPresets();//so rest is just writing? like how do oyu write the data from presetdata in logic, you get what im saying? like how would i send it to the player? just setpresetdata in presethandler?
            // its already on player, player.presetsv2
            presetsv2 = player.getPresetData();
            lastPreset = player.getLastPreset();
            specialTeleblockTimer = player.getTimers().left(TimerKey.SPECIAL_TELEBLOCK);
            memberUnlocked = Player.getAttribBooleanOr(player, AttributeKey.MEMBER_UNLOCKED, false);
            superMemberUnlocked = Player.getAttribBooleanOr(player, AttributeKey.SUPER_MEMBER_UNLOCKED, false);
            eliteMemberUnlocked = Player.getAttribBooleanOr(player, AttributeKey.ELITE_MEMBER_UNLOCKED, false);
            extremeMemberUnlocked = Player.getAttribBooleanOr(player, AttributeKey.EXTREME_MEMBER_UNLOCKED, false);
            legendaryMemberUnlocked = Player.getAttribBooleanOr(player, AttributeKey.LEGENDARY_MEMBER_UNLOCKED, false);
            vipUnlocked = Player.getAttribBooleanOr(player, AttributeKey.VIP_UNLOCKED, false);
            sponsorMemberUnlocked = Player.getAttribBooleanOr(player, AttributeKey.SPONSOR_UNLOCKED, false);
            saved_tornament_xp = player.getSavedTornamentXp();
            saved_tornament_levels = player.getSavedTornamentLevels();
            dynamicLevels = player.getSkills().levels();
            skillXP = player.getSkills().xp();
            unlockedPets = player.getUnlockedPets();
            insuredPets = player.getInsuredPets();
            blockedSlayerTasks = player.getSlayerRewards().getBlockedSlayerTask();
            slayerUnlocks = player.getSlayerRewards().getUnlocks();
            slayerExtensionsList = player.getSlayerRewards().getExtendable();
            slayerPoints = Player.getAttribIntOr(player, AttributeKey.SLAYER_REWARD_POINTS, 0);
            inventory = player.inventory().toArray();
            equipment = player.getEquipment().toArray();
            bank = player.getBank().toNonNullArray();
            tabAmounts = player.getBank().tabAmounts;
            placeholdersActive = player.getBank().placeHolder;
            placeHolderAmount = player.getBank().placeHolderAmount;
            hashedBankPin = player.getBankPin().getHashedPin();
            bankPinLength = player.getBankPin().getPinLength();
            recoveryDelay = player.getBankPin().getRecoveryDays();
            pendingBankPinMod = player.getBankPin().getPendingMod();
            lootingBag = player.getLootingBag().toNonNullArray();
            askHowManyToStore = player.getLootingBag().askHowManyToStore();
            storeAsMany = player.getLootingBag().storeAsMany();
            runePouch = player.getRunePouch().toArray();
            cartItems = player.<ArrayList<Item>>getAttribOr(AttributeKey.CART_ITEMS, new ArrayList<Item>());
            nifflerItems = player.<ArrayList<Item>>getAttribOr(AttributeKey.NIFFLER_ITEMS_STORED, new ArrayList<Item>());
            sackOfPresentItems = player.<ArrayList<Item>>getAttribOr(AttributeKey.SACK_OF_PRESENTS_LIST, new ArrayList<Item>());
            newFriends = player.getRelations().getFriendList();
            newIgnores = player.getRelations().getIgnoreList();
            clan = player.getClanChat();
            savePresetLevels = player.getPresetManager().saveLevels();
            openPresetsOnDeath = player.getPresetManager().openOnDeath();
            savedDuelConfig = player.getSavedDuelConfig();
            recentKills = player.getRecentKills();
            lavaBeastsKilled = Player.getAttribIntOr(player, AttributeKey.LAVA_BEASTS_KILLED, 0);
            derangedArchaeologistKilled = Player.getAttribIntOr(player, AttributeKey.DERANGED_ARCHAEOLOGIST_KILLED, 0);
            elFuegoKilled = Player.getAttribIntOr(player, AttributeKey.EL_FUEGO_KILLED, 0); // TODO

            bossTimers = player.getBossTimers().getTimes();
            recentTeleports = player.getRecentTeleports();
            favoriteTeleports = player.getFavorites();
            collectionLog = player.getCollectionLog().collectionLog;
            achievements = player.achievements();
            task = player.getAttribOr(AttributeKey.TASK, Tasks.NONE);
            locBeforeJail = ((Tile) player.getAttribOr(AttributeKey.LOC_BEFORE_JAIL, new Tile(3092, 3500))).toPlain();

            alchemicalHydraLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ALCHEMICAL_HYDRA_LOG_CLAIMED, false);
            ancientBarrelchestLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ANCIENT_BARRELCHEST_LOG_CLAIMED, false);
            ancientChaosElementalLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ANCIENT_CHAOS_ELEMENTAL_LOG_CLAIMED, false);
            ancientKingBlackDragonLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ANCIENT_KING_BLACK_DRAGON_LOG_CLAIMED, false);
            arachneLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ARACHNE_LOG_CLAIMED, false);
            artioLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ARTIO_LOG_CLAIMED, false);
            serenLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.SEREN_LOG_CLAIMED, false);
            barrelchestLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.BARRELCHEST_LOG_CLAIMED, false);
            brutalLavaDragonLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.BRUTAL_LAVA_DRAGON_LOG_CLAIMED, false);
            callistoLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CALLISTO_LOG_CLAIMED, false);
            cerberusLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CERBERUS_LOG_CLAIMED, false);
            chaosElementalLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CHAOS_ELEMENTAL_LOG_CLAIMED, false);
            chaosFanaticLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CHAOS_FANATIC_LOG_CLAIMED, false);
            corporealBeastLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CORPOREAL_BEAST_LOG_CLAIMED, false);
            corruptedNechryarchLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CORRUPTED_NECHRYARCH_LOG_CLAIMED, false);
            crazyArchaeologistLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CRAZY_ARCHAEOLOGIST_LOG_CLAIMED, false);
            demonicGorillaLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.DEMONIC_GORILLA_LOG_CLAIMED, false);
            giantMoleLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.GIANT_MOLE_LOG_CLAIMED, false);
            kerberosLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.KERBEROS_LOG_CLAIMED, false);
            kingBlackDragonLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.KING_BLACK_DRAGON_LOG_CLAIMED, false);
            krakenLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.KRAKEN_LOG_CLAIMED, false);
            lavaDragonLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.LAVA_DRAGON_LOG_CLAIMED, false);
            lizardmanShamanLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.LIZARDMAN_SHAMAN_LOG_CLAIMED, false);
            scorpiaLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.SCORPIA_LOG_CLAIMED, false);
            skorpiosLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.SKORPIOS_LOG_CLAIMED, false);
            skotizoLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.SKOTIZO_LOG_CLAIMED, false);
            tektonLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.TEKTON_LOG_CLAIMED, false);
            thermonuclearSmokeDevilLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.THERMONUCLEAR_SMOKE_DEVIL_LOG_CLAIMED, false);
            theNightmareLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.THE_NIGTHMARE_LOG_CLAIMED, false);
            corruptedHunleffLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CORRUPTED_HUNLEFF_LOG_CLAIMED, false);
            menInBlackLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.MEN_IN_BLACK_LOG_CLAIMED, false);
            tztokJadLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.TZTOK_JAD_LOG_CLAIMED, false);
            venenatisLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.VENENATIS_LOG_CLAIMED, false);
            vetionLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.VETION_LOG_CLAIMED, false);
            vorkathLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.VORKATH_LOG_CLAIMED, false);
            zombiesChampionLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ZOMBIES_CHAMPION_LOG_CLAIMED, false);
            zulrahLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ZULRAH_LOG_CLAIMED, false);
            armourMysteryBoxLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ARMOUR_MYSTERY_BOX_LOG_CLAIMED, false);
            donatorMysteryBoxLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.DONATOR_MYSTERY_BOX_LOG_CLAIMED, false);
            epicPetMysteryBoxLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.EPIC_PET_MYSTERY_BOX_LOG_CLAIMED, false);
            mysteryChestLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.MYSTERY_CHEST_LOG_CLAIMED, false);
            raidsMysteryBoxLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.RAIDS_MYSTERY_BOX_LOG_CLAIMED, false);
            weaponMysteryBoxLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.WEAPON_MYSTERY_BOX_LOG_CLAIMED, false);
            legendaryMysteryBoxLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.LEGENDARY_MYSTERY_BOX_LOG_CLAIMED, false);
            zenyteLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ZENYTE_MYSTERY_BOX_LOG_CLAIMED, false);
            crystalKeyLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CRYSTAL_KEY_LOG_CLAIMED, false);
            moltenKeyLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.MOLTEN_KEY_LOG_CLAIMED, false);
            enchantedKeyRLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ENCHANTED_KEY_R_LOG_CLAIMED, false);
            enchantedKeyPLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ENCHANTED_KEY_P_LOG_CLAIMED, false);
            larransKeyTierILogClaimed = Player.getAttribBooleanOr(player, AttributeKey.LARRANS_KEY_TIER_I_LOG_CLAIMED, false);
            larransKeyTierIILogClaimed = Player.getAttribBooleanOr(player, AttributeKey.LARRANS_KEY_TIER_II_LOG_CLAIMED, false);
            larransKeyTierIIILogClaimed = Player.getAttribBooleanOr(player, AttributeKey.LARRANS_KEY_TIER_III_LOG_CLAIMED, false);
            slayerKeyLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.SLAYER_KEY_LOG_CLAIMED, false);
            wildernessKeyLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.WILDERNESS_KEY_LOG_CLAIMED, false);
            ancientRevenantsLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.ANCIENT_REVENANTS_LOG_CLAIMED, false);
            chamberOfSecretsLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.CHAMBER_OF_SECRETS_LOG_CLAIMED, false);
            revenantsLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.REVENANTS_LOG_CLAIMED, false);
            slayerLogClaimed = Player.getAttribBooleanOr(player, AttributeKey.SLAYER_LOG_CLAIMED, false);

            eventReward1Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_1_CLAIMED, false);
            eventReward2Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_2_CLAIMED, false);
            eventReward3Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_3_CLAIMED, false);
            eventReward4Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_4_CLAIMED, false);
            eventReward5Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_5_CLAIMED, false);
            eventReward6Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_6_CLAIMED, false);
            eventReward7Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_7_CLAIMED, false);
            eventReward8Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_8_CLAIMED, false);
            eventReward9Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_9_CLAIMED, false);
            eventReward10Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_10_CLAIMED, false);
            eventReward11Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_11_CLAIMED, false);
            eventReward12Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_12_CLAIMED, false);
            eventReward13Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_13_CLAIMED, false);
            eventReward14Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_14_CLAIMED, false);
            eventReward15Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_15_CLAIMED, false);
            eventReward16Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_16_CLAIMED, false);
            eventReward17Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_17_CLAIMED, false);
            eventReward18Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_18_CLAIMED, false);
            eventReward19Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_19_CLAIMED, false);
            eventReward20Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_20_CLAIMED, false);
            eventReward21Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_21_CLAIMED, false);
            eventReward22Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_22_CLAIMED, false);
            eventReward23Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_23_CLAIMED, false);
            eventReward24Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_24_CLAIMED, false);
            eventReward25Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_25_CLAIMED, false);
            eventReward26Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_26_CLAIMED, false);
            eventReward27Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_27_CLAIMED, false);
            eventReward28Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_28_CLAIMED, false);
            eventReward29Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_29_CLAIMED, false);
            eventReward30Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_30_CLAIMED, false);
            eventReward31Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_31_CLAIMED, false);
            eventReward32Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_32_CLAIMED, false);
            eventReward33Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_33_CLAIMED, false);
            eventReward34Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_34_CLAIMED, false);
            eventReward35Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_35_CLAIMED, false);
            eventReward36Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_36_CLAIMED, false);
            eventReward37Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_37_CLAIMED, false);
            eventReward38Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_38_CLAIMED, false);
            eventReward39Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_39_CLAIMED, false);
            eventReward40Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_40_CLAIMED, false);
            eventReward41Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_41_CLAIMED, false);
            eventReward42Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_42_CLAIMED, false);
            eventReward43Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_43_CLAIMED, false);
            eventReward44Claimed = Player.getAttribBooleanOr(player, AttributeKey.EVENT_REWARD_44_CLAIMED, false);
            infhp = player.isInvulnerable();
            varps = new HashMap<>() {
                {
                    for (Varp v : Varp.SYNCED_VARPS) {
                        put(v.id(), player.sessionVarps()[v.id()]);
                    }
                }
            };
            dailyTasksList = player.getOrT(AttributeKey.DAILY_TASKS_LIST, new ArrayList<>());
            dailyTasksExtensions = player.getOrT(AttributeKey.DAILY_TASKS_EXTENSION_LIST, new HashMap<>());
            allAttribs = ARGS_SERIALIZER.apply(player);

            starterBowCharges = Player.getAttribIntOr(player, AttributeKey.STARTER_BOW_CHARGES, 0);
            starterStaffCharges = Player.getAttribIntOr(player, AttributeKey.STARTER_STAFF_CHARGES, 0);
            starterSwordCharges = Player.getAttribIntOr(player, AttributeKey.STARTER_SWORD_CHARGES, 0);
            lastRecallSave = player.getLastSavedTile() != null ? player.getLastSavedTile().toPlain() : null;
            voidIslandPoints = Player.getAttribIntOr(player, AttributeKey.VOID_ISLAND_POINTS, 0);
            playerUID = Player.getAttribLongOr(player, PLAYER_UID, -1L);
        }

        public void parseDetails() {
            try {
                final String json = PlayerSave.SERIALIZE.toJson(this);

                final String fileName = username + ".json";
                final Path path = SAVE_DIR.resolve(fileName);

                Path parent = path.getParent();
                if (parent == null) {
                    throw new UnsupportedOperationException("Path must have a parent: " + path);
                }

                if (!Files.exists(parent)) {
                    parent = Files.createDirectories(parent);
                }

                final Path tempFile = Files.createTempFile(parent, fileName, ".tmp");
                Files.writeString(tempFile, json, StandardCharsets.UTF_8);

                Files.move(tempFile, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Error during file save: " + e.getMessage(), e);
            }
        }
    }

    public static boolean playerExists(String name) {
        return Files.exists(SAVE_DIR.resolve(name + ".json"));
    }

    public static final Path SAVE_DIR = Path.of("data", "saves", "characters");

    public static BiConsumer<Player, Map<String, String>> ARGS_DESERIALIZER = (p, m) -> {

    };
    public static Function<Player, Map<String, String>> ARGS_SERIALIZER = m -> {
        return null;
    };

}
